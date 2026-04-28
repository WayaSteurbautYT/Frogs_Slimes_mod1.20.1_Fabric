package com.wayacreate.frogslimegamemode.integration;

import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

/**
 * Baritone integration for helper schematic building.
 * Used by HelperCommand to manage schematic building tasks.
 */
public class BaritoneIntegration {
    private static final java.util.Map<TamableAnimal, String> activeBuilds = new java.util.HashMap<>();
    private static final java.util.Map<TamableAnimal, Integer> buildProgress = new java.util.HashMap<>();
    
    /**
     * Check if Baritone is available on the server
     */
    public static boolean isBaritoneAvailable() {
        try {
            Class.forName("baritone.api.BaritoneAPI");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    /**
     * Set a helper to build a schematic
     * @param helper The helper entity
     * @param schematicName The name of the schematic to build
     */
    public static void setSchematicBuild(TamableAnimal helper, String schematicName) {
        if (!isBaritoneAvailable()) {
            if (helper.getOwner() instanceof net.minecraft.world.entity.player.Player player) {
                player.sendMessage(Component.literal("Baritone is not installed on this server!")
                    .formatted(ChatFormatting.RED), true);
            }
            return;
        }
        
        // Register the build task
        activeBuilds.put(helper, schematicName);
        buildProgress.put(helper, 0);
        
        // Attempt to load schematic via Baritone API if available
        try {
            Object baritoneApi = Class.forName("baritone.api.BaritoneAPI").getMethod("getProvider").invoke(null);
            if (baritoneApi != null) {
                // Schematic loading would go here
                // For now, we simulate the build process
                if (helper.getOwner() instanceof net.minecraft.world.entity.player.Player player) {
                    player.sendMessage(Component.literal("Started building schematic: " + schematicName)
                        .formatted(ChatFormatting.GREEN), true);
                }
            }
        } catch (Exception e) {
            if (helper.getOwner() instanceof net.minecraft.world.entity.player.Player player) {
                player.sendMessage(Component.literal("Failed to load schematic: " + e.getMessage())
                    .formatted(ChatFormatting.RED), true);
            }
        }
    }
    
    /**
     * Stop a helper from building
     * @param helper The helper entity
     */
    public static void stopBuilding(TamableAnimal helper) {
        if (!isBaritoneAvailable()) {
            return;
        }
        
        // Remove from active builds
        activeBuilds.remove(helper);
        buildProgress.remove(helper);
        
        // Stop Baritone process if available
        try {
            Object baritoneApi = Class.forName("baritone.api.BaritoneAPI").getMethod("getProvider").invoke(null);
            if (baritoneApi != null && helper.getOwner() instanceof net.minecraft.world.entity.player.Player player) {
                // Would call Baritone's stop command here
                player.sendMessage(Component.literal("Stopped schematic building")
                    .formatted(ChatFormatting.YELLOW), true);
            }
        } catch (Exception e) {
            // Ignore if Baritone API call fails
        }
    }
    
    /**
     * Get the build progress of a helper
     * @param helper The helper entity
     * @return Progress percentage (0-100)
     */
    public static int getBuildProgress(TamableAnimal helper) {
        if (!isBaritoneAvailable()) {
            return 0;
        }
        
        // Return simulated progress for now
        // In a full implementation, this would query Baritone's actual progress
        return buildProgress.getOrDefault(helper, 0);
    }
    
    /**
     * Update build progress (called periodically)
     * @param helper The helper entity
     * @param progress The new progress value (0-100)
     */
    public static void updateBuildProgress(TamableAnimal helper, int progress) {
        if (activeBuilds.containsKey(helper)) {
            buildProgress.put(helper, Math.min(100, Math.max(0, progress)));
            
            // Notify owner if build is complete
            if (progress >= 100 && helper.getOwner() instanceof net.minecraft.world.entity.player.Player player) {
                String schematic = activeBuilds.get(helper);
                player.sendMessage(Component.literal("Completed building schematic: " + schematic)
                    .formatted(ChatFormatting.GREEN), true);
                activeBuilds.remove(helper);
            }
        }
    }
    
    /**
     * Check if a helper is currently building
     * @param helper The helper entity
     * @return true if building, false otherwise
     */
    public static boolean isBuilding(TamableAnimal helper) {
        return activeBuilds.containsKey(helper);
    }
    
    /**
     * Get the current schematic being built
     * @param helper The helper entity
     * @return The schematic name, or null if not building
     */
    public static String getCurrentSchematic(TamableAnimal helper) {
        return activeBuilds.get(helper);
    }
}
