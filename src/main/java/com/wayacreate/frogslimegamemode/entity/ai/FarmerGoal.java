package com.wayacreate.frogslimegamemode.entity.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class FarmerGoal extends Goal {
    private final TamableAnimal entity;
    private final Level world;
    private int cooldown;
    
    public FarmerGoal(TamableAnimal entity) {
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
        
        // Check for nearby crops
        BlockPos entityPos = entity.getBlockPos();
        for (int x = -3; x <= 3; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos checkPos = entityPos.add(x, y, z);
                    if (isCrop(world.getBlockState(checkPos))) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    @Override
    public void start() {
        // Find nearest crop and harvest/bone meal it
        BlockPos entityPos = entity.getBlockPos();
        BlockPos nearestCrop = null;
        double nearestDistance = Double.MAX_VALUE;
        
        for (int x = -3; x <= 3; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos checkPos = entityPos.add(x, y, z);
                    if (isCrop(world.getBlockState(checkPos))) {
                        double distance = entity.squaredDistanceTo(checkPos.getX(), checkPos.getY(), checkPos.getZ());
                        if (distance < nearestDistance) {
                            nearestDistance = distance;
                            nearestCrop = checkPos;
                        }
                    }
                }
            }
        }
        
        if (nearestCrop != null && !world.isClient) {
            BlockState cropState = world.getBlockState(nearestCrop);
            
            if (cropState.getBlock() instanceof CropBlock cropBlock) {
                if (cropBlock.isMature(cropState)) {
                    // Harvest mature crop
                    world.breakBlock(nearestCrop, true, entity);
                } else {
                    // Apply bone meal to grow crop faster
                    world.setBlockState(nearestCrop, cropBlock.withAge(cropBlock.getAge(cropState) + 1));
                }
            }
            
            cooldown = 40; // 2 second cooldown
        }
    }
    
    private boolean isCrop(BlockState state) {
        return state.getBlock() instanceof CropBlock || 
               state.getBlock() == Blocks.WHEAT ||
               state.getBlock() == Blocks.CARROTS ||
               state.getBlock() == Blocks.POTATOES ||
               state.getBlock() == Blocks.BEETROOTS;
    }
}
