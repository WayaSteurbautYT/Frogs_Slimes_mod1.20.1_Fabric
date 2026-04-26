package com.wayacreate.frogslimegamemode.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import com.wayacreate.frogslimegamemode.network.ModNetworking;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class FrogSlimeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("frogslime")
            .then(CommandManager.literal("enable")
                .executes(FrogSlimeCommand::startGamemode))
            .then(CommandManager.literal("disable")
                .executes(FrogSlimeCommand::stopGamemode))
            .then(CommandManager.literal("info")
                .executes(FrogSlimeCommand::showInfo))
            .then(CommandManager.literal("tasks")
                .executes(FrogSlimeCommand::openTasks))
            .then(CommandManager.literal("reset")
                .executes(FrogSlimeCommand::resetData))
        );
    }
    
    private static int startGamemode(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            GamemodeManager.enableGamemode(player);
            return 1;
        }
        context.getSource().sendError(Text.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int stopGamemode(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            GamemodeManager.disableGamemode(player);
            return 1;
        }
        context.getSource().sendError(Text.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int showInfo(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            player.sendMessage(Text.literal("=== Frog & Slime Gamemode by WayaCreate ===")
                .formatted(Formatting.GOLD, Formatting.BOLD), false);
            player.sendMessage(Text.literal("Commands:").formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("  /frogslime enable - Begin the gamemode")
                .formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("  /frogslime disable - Stop the gamemode")
                .formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal(""), false);
            player.sendMessage(Text.literal("How to Play:").formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("1. Start the gamemode with /frogslime enable")
                .formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("2. Spawn helpers with spawn eggs")
                .formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("3. Tame them by right-clicking")
                .formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("4. They evolve by killing mobs!")
                .formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("5. Defeat the Ender Dragon for a surprise...")
                .formatted(Formatting.DARK_RED, Formatting.ITALIC), false);
            return 1;
        }
        return 0;
    }
    
    private static int openTasks(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            ModNetworking.openTasksScreen(player);
            return 1;
        }
        return 0;
    }
    
    private static int resetData(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            // Disable gamemode first
            if (GamemodeManager.isInGamemode(player)) {
                GamemodeManager.disableGamemode(player);
            }
            
            // Reset ability manager data for this player
            com.wayacreate.frogslimegamemode.abilities.PlayerAbilityManager.resetPlayer(player.getUuid());
            
            player.sendMessage(Text.literal("All Frog & Slime Gamemode data has been reset!")
                .formatted(Formatting.GREEN, Formatting.BOLD), false);
            player.sendMessage(Text.literal("Use /frogslime enable to start a new game.")
                .formatted(Formatting.YELLOW), false);
            
            return 1;
        }
        context.getSource().sendError(Text.literal("Only players can use this command!"));
        return 0;
    }
}