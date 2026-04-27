package com.wayacreate.frogslimegamemode.dimension;

import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class TransformedEndTeleporter {
    
    public static void teleportToTransformedEnd(Entity entity) {
        MinecraftServer server = entity.getServer();
        if (server == null) return;
        
        // Try to get the custom dimension
        RegistryKey<World> transformedEndKey = RegistryKey.of(RegistryKeys.WORLD, ModDimensions.TRANSFORMED_END_ID);
        ServerWorld transformedEnd = server.getWorld(transformedEndKey);
        
        if (transformedEnd != null && entity instanceof ServerPlayerEntity player) {
            player.teleport(transformedEnd, 0, 50, 0, player.getYaw(), player.getPitch());
            player.sendMessage(Text.literal("Teleporting to the Transformed End!")
                .formatted(Formatting.GREEN, Formatting.BOLD), true);
        } else {
            // Fallback to regular End if custom dimension not available
            ServerWorld endWorld = server.getWorld(World.END);
            if (endWorld != null && entity instanceof ServerPlayerEntity player) {
                player.teleport(endWorld, 0, 50, 0, player.getYaw(), player.getPitch());
                player.sendMessage(Text.literal("Teleporting to the End (Custom dimension not loaded)")
                    .formatted(Formatting.YELLOW), true);
            } else if (entity instanceof ServerPlayerEntity player) {
                player.sendMessage(Text.literal("End dimension not found!")
                    .formatted(Formatting.RED), true);
            }
        }
    }
    
    public static void teleportFromTransformedEnd(Entity entity) {
        MinecraftServer server = entity.getServer();
        if (server == null) return;
        
        ServerWorld overworld = server.getWorld(World.OVERWORLD);
        if (overworld != null && entity instanceof ServerPlayerEntity player) {
            player.teleport(overworld, overworld.getSpawnPos().getX(), overworld.getSpawnPos().getY(), overworld.getSpawnPos().getZ(), player.getYaw(), player.getPitch());
        }
    }
}
