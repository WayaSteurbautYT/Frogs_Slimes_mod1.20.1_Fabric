package com.wayacreate.frogslimegamemode.dimension;

import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

public class TransformedEndTeleporter {
    
    public static void teleportToTransformedEnd(Entity entity) {
        MinecraftServer server = entity.getServer();
        if (server == null) return;
        
        // Try to get the custom dimension
        ResourceKey<Level> transformedEndKey = ResourceKey.of(Registries.WORLD, ModDimensions.TRANSFORMED_END_ID);
        ServerLevel transformedEnd = server.getWorld(transformedEndKey);
        
        if (transformedEnd != null && entity instanceof ServerPlayer player) {
            player.teleport(transformedEnd, 0, 50, 0, player.getYaw(), player.getPitch());
            player.sendMessage(Component.literal("Teleporting to the Transformed End!")
                .formatted(ChatFormatting.GREEN, ChatFormatting.BOLD), true);
        } else {
            // Fallback to regular End if custom dimension not available
            ServerLevel endWorld = server.getWorld(Level.END);
            if (endWorld != null && entity instanceof ServerPlayer player) {
                player.teleport(endWorld, 0, 50, 0, player.getYaw(), player.getPitch());
                player.sendMessage(Component.literal("Teleporting to the End (Custom dimension not loaded)")
                    .formatted(ChatFormatting.YELLOW), true);
            } else if (entity instanceof ServerPlayer player) {
                player.sendMessage(Component.literal("End dimension not found!")
                    .formatted(ChatFormatting.RED), true);
            }
        }
    }
    
    public static void teleportFromTransformedEnd(Entity entity) {
        MinecraftServer server = entity.getServer();
        if (server == null) return;
        
        ServerLevel overworld = server.getWorld(Level.OVERWORLD);
        if (overworld != null && entity instanceof ServerPlayer player) {
            player.teleport(overworld, overworld.getSpawnPos().getX(), overworld.getSpawnPos().getY(), overworld.getSpawnPos().getZ(), player.getYaw(), player.getPitch());
        }
    }
}
