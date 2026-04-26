package com.wayacreate.frogslimegamemode.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class ManhuntCompassItem extends Item {
    public ManhuntCompassItem(Settings settings) {
        super(settings);
    }
    
    public void inventoryTick(ItemStack stack, World world, PlayerEntity player, int slot, boolean selected) {
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            // Only update every 20 ticks (1 second) to reduce server load
            if (world.getTime() % 20 == 0) {
                // Find nearest player (excluding the holder)
                ServerPlayerEntity nearestPlayer = null;
                double nearestDistance = Double.MAX_VALUE;
                
                for (ServerPlayerEntity otherPlayer : serverWorld.getPlayers()) {
                    if (otherPlayer != player && otherPlayer.isAlive()) {
                        double distance = player.squaredDistanceTo(otherPlayer);
                        if (distance < nearestDistance) {
                            nearestDistance = distance;
                            nearestPlayer = otherPlayer;
                        }
                    }
                }
                
                if (nearestPlayer != null) {
                    // Update tooltip with target info
                    stack.setCustomName(Text.literal("Tracking: " + nearestPlayer.getName().getString())
                        .formatted(Formatting.RED, Formatting.BOLD));
                    
                    // Send direction message only when selected
                    if (selected) {
                        int distanceBlocks = (int) Math.sqrt(nearestDistance);
                        player.sendMessage(Text.literal("Target is " + distanceBlocks + " blocks away")
                            .formatted(Formatting.YELLOW), true);
                    }
                } else {
                    stack.setCustomName(Text.literal("No target found")
                        .formatted(Formatting.GRAY));
                }
            }
        }
        
        super.inventoryTick(stack, world, player, slot, selected);
    }
}
