package com.wayacreate.frogslimegamemode.entity.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class MiningGoal extends Goal {
    private final TamableAnimal entity;
    private final Level world;
    private int cooldown;
    
    public MiningGoal(TamableAnimal entity) {
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
        
        // Check for nearby ore blocks
        BlockPos entityPos = entity.getBlockPos();
        for (int x = -3; x <= 3; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos checkPos = entityPos.add(x, y, z);
                    if (isOre(world.getBlockState(checkPos).getBlock())) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    @Override
    public void start() {
        // Find nearest ore and break it
        BlockPos entityPos = entity.getBlockPos();
        BlockPos nearestOre = null;
        double nearestDistance = Double.MAX_VALUE;
        
        for (int x = -3; x <= 3; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos checkPos = entityPos.add(x, y, z);
                    if (isOre(world.getBlockState(checkPos).getBlock())) {
                        double distance = entity.squaredDistanceTo(checkPos.getX(), checkPos.getY(), checkPos.getZ());
                        if (distance < nearestDistance) {
                            nearestDistance = distance;
                            nearestOre = checkPos;
                        }
                    }
                }
            }
        }
        
        if (nearestOre != null && !world.isClient) {
            world.breakBlock(nearestOre, true, entity);
            cooldown = 40; // 2 second cooldown
        }
    }
    
    private boolean isOre(net.minecraft.world.level.block.Block block) {
        return block == Blocks.COAL_ORE || 
               block == Blocks.IRON_ORE || 
               block == Blocks.GOLD_ORE || 
               block == Blocks.DIAMOND_ORE || 
               block == Blocks.REDSTONE_ORE ||
               block == Blocks.LAPIS_ORE ||
               block == Blocks.COPPER_ORE ||
               block == Blocks.DEEPSLATE_COAL_ORE ||
               block == Blocks.DEEPSLATE_IRON_ORE ||
               block == Blocks.DEEPSLATE_GOLD_ORE ||
               block == Blocks.DEEPSLATE_DIAMOND_ORE ||
               block == Blocks.DEEPSLATE_REDSTONE_ORE ||
               block == Blocks.DEEPSLATE_LAPIS_ORE ||
               block == Blocks.DEEPSLATE_COPPER_ORE;
    }
}
