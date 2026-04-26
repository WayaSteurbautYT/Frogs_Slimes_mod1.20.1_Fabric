package com.wayacreate.frogslimegamemode.entity.ai;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LumberjackGoal extends Goal {
    private final TameableEntity entity;
    private final World world;
    private int cooldown;
    
    public LumberjackGoal(TameableEntity entity) {
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
        
        // Check for nearby log blocks
        BlockPos entityPos = entity.getBlockPos();
        for (int x = -4; x <= 4; x++) {
            for (int y = -1; y <= 4; y++) {
                for (int z = -4; z <= 4; z++) {
                    BlockPos checkPos = entityPos.add(x, y, z);
                    if (isLog(world.getBlockState(checkPos).getBlock())) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    @Override
    public void start() {
        // Find nearest log and break it
        BlockPos entityPos = entity.getBlockPos();
        BlockPos nearestLog = null;
        double nearestDistance = Double.MAX_VALUE;
        
        for (int x = -4; x <= 4; x++) {
            for (int y = -1; y <= 4; y++) {
                for (int z = -4; z <= 4; z++) {
                    BlockPos checkPos = entityPos.add(x, y, z);
                    if (isLog(world.getBlockState(checkPos).getBlock())) {
                        double distance = entity.squaredDistanceTo(checkPos.getX(), checkPos.getY(), checkPos.getZ());
                        if (distance < nearestDistance) {
                            nearestDistance = distance;
                            nearestLog = checkPos;
                        }
                    }
                }
            }
        }
        
        if (nearestLog != null && !world.isClient) {
            world.breakBlock(nearestLog, true, entity);
            cooldown = 30; // 1.5 second cooldown
        }
    }
    
    private boolean isLog(net.minecraft.block.Block block) {
        return block == Blocks.OAK_LOG ||
               block == Blocks.SPRUCE_LOG ||
               block == Blocks.BIRCH_LOG ||
               block == Blocks.JUNGLE_LOG ||
               block == Blocks.ACACIA_LOG ||
               block == Blocks.DARK_OAK_LOG ||
               block == Blocks.MANGROVE_LOG ||
               block == Blocks.CHERRY_LOG ||
               block == Blocks.CRIMSON_STEM ||
               block == Blocks.WARPED_STEM;
    }
}
