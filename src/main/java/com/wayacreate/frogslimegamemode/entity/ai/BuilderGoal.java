package com.wayacreate.frogslimegamemode.entity.ai;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BuilderGoal extends Goal {
    private final TameableEntity entity;
    private final World world;
    private int cooldown;
    
    public BuilderGoal(TameableEntity entity) {
        this.entity = entity;
        this.world = entity.getWorld();
        this.cooldown = 0;
    }
    
    @Override
    public boolean canStart() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        }
        
        if (!entity.isTamed() || entity.getOwner() == null) {
            return false;
        }
        
        // Check for nearby empty spaces or incomplete structures
        BlockPos entityPos = entity.getBlockPos();
        for (int x = -2; x <= 2; x++) {
            for (int y = -1; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos checkPos = entityPos.add(x, y, z);
                    if (shouldPlaceBlock(world.getBlockState(checkPos), checkPos)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    @Override
    public void start() {
        // Find best position to place a block
        BlockPos entityPos = entity.getBlockPos();
        BlockPos bestPos = null;
        
        for (int x = -2; x <= 2; x++) {
            for (int y = -1; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos checkPos = entityPos.add(x, y, z);
                    if (shouldPlaceBlock(world.getBlockState(checkPos), checkPos)) {
                        double distance = entity.squaredDistanceTo(checkPos.getX(), checkPos.getY(), checkPos.getZ());
                        if (bestPos == null || distance < entity.squaredDistanceTo(bestPos.getX(), bestPos.getY(), bestPos.getZ())) {
                            bestPos = checkPos;
                        }
                    }
                }
            }
        }
        
        if (bestPos != null && !world.isClient) {
            // Place cobblestone or dirt as building material
            BlockState blockToPlace = world.getBlockState(bestPos.down()).isSolidBlock(world, bestPos.down()) 
                ? Blocks.COBBLESTONE.getDefaultState() 
                : Blocks.DIRT.getDefaultState();
            
            world.setBlockState(bestPos, blockToPlace);
            cooldown = 60; // 3 second cooldown
        }
    }
    
    private boolean shouldPlaceBlock(BlockState state, BlockPos pos) {
        // Place block if air and below is solid
        return state.isAir() && !world.getBlockState(pos.down()).isAir();
    }
}
