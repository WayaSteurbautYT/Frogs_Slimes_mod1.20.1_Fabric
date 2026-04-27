package com.wayacreate.frogslimegamemode.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.wayacreate.frogslimegamemode.dimension.TransformedEndTeleporter;
import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import com.wayacreate.frogslimegamemode.gamemode.ManhuntManager;
import com.wayacreate.frogslimegamemode.gamemode.RankManager;
import com.wayacreate.frogslimegamemode.gamemode.TeamManager;
import com.wayacreate.frogslimegamemode.network.ModNetworking;
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
            .then(CommandManager.literal("manhunt")
                .then(CommandManager.literal("auto")
                    .executes(FrogSlimeCommand::startAutoManhunt))
                .then(CommandManager.literal("speedrunner")
                    .executes(FrogSlimeCommand::setSpeedrunner))
                .then(CommandManager.literal("solo")
                    .executes(FrogSlimeCommand::setSoloSpeedrunner))
                .then(CommandManager.literal("hunter")
                    .executes(FrogSlimeCommand::setHunter))
                .then(CommandManager.literal("end")
                    .executes(FrogSlimeCommand::endManhunt)))
            .then(CommandManager.literal("dimension")
                .then(CommandManager.literal("transformed_end")
                    .executes(FrogSlimeCommand::teleportToTransformedEnd))
                .then(CommandManager.literal("return")
                    .executes(FrogSlimeCommand::returnFromDimension)))
            .then(CommandManager.literal("role")
                .then(CommandManager.literal("giverole")
                    .executes(FrogSlimeCommand::giveRoleItem)))
            .then(CommandManager.literal("team")
                .then(CommandManager.literal("create")
                    .then(CommandManager.argument("name", StringArgumentType.string())
                        .then(CommandManager.argument("color", StringArgumentType.string())
                            .executes(FrogSlimeCommand::createTeam))))
                .then(CommandManager.literal("join")
                    .then(CommandManager.argument("name", StringArgumentType.string())
                        .executes(FrogSlimeCommand::joinTeam)))
                .then(CommandManager.literal("leave")
                    .executes(FrogSlimeCommand::leaveTeam))
                .then(CommandManager.literal("list")
                    .executes(FrogSlimeCommand::listTeams))
                .then(CommandManager.literal("tp")
                    .then(CommandManager.argument("player", StringArgumentType.string())
                        .executes(FrogSlimeCommand::teleportToTeamMember))))
            .then(CommandManager.literal("rank")
                .then(CommandManager.argument("player", StringArgumentType.string())
                    .then(CommandManager.argument("rank", StringArgumentType.string())
                        .executes(FrogSlimeCommand::setPlayerRank))))
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
    
    private static int startAutoManhunt(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            ManhuntManager.startAutoManhunt(player);
            return 1;
        }
        context.getSource().sendError(Text.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int setSpeedrunner(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            ManhuntManager.setSpeedrunner(player);
            return 1;
        }
        context.getSource().sendError(Text.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int setSoloSpeedrunner(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            ManhuntManager.setSoloSpeedrunner(player);
            return 1;
        }
        context.getSource().sendError(Text.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int setHunter(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            // Default to nearest player as target
            ServerPlayerEntity nearest = null;
            double nearestDist = Double.MAX_VALUE;
            
            for (ServerPlayerEntity other : player.getServer().getPlayerManager().getPlayerList()) {
                if (other != player) {
                    double dist = player.squaredDistanceTo(other);
                    if (dist < nearestDist) {
                        nearestDist = dist;
                        nearest = other;
                    }
                }
            }
            
            if (nearest != null) {
                ManhuntManager.setHunter(player, nearest);
            } else {
                player.sendMessage(Text.literal("No other players found to hunt!")
                    .formatted(Formatting.RED), false);
            }
            return 1;
        }
        context.getSource().sendError(Text.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int endManhunt(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            ManhuntManager.endGame(player);
            return 1;
        }
        context.getSource().sendError(Text.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int teleportToTransformedEnd(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            TransformedEndTeleporter.teleportToTransformedEnd(player);
            player.sendMessage(Text.literal("Use /frogslime dimension return to go back!")
                .formatted(Formatting.YELLOW), true);
            return 1;
        }
        context.getSource().sendError(Text.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int returnFromDimension(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            TransformedEndTeleporter.teleportFromTransformedEnd(player);
            return 1;
        }
        context.getSource().sendError(Text.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int giveRoleItem(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            // Give a role selection item
            com.wayacreate.frogslimegamemode.item.RoleItem roleItem = new com.wayacreate.frogslimegamemode.item.RoleItem(
                new net.fabricmc.fabric.api.item.v1.FabricItemSettings().maxCount(1), "Selection");
            net.minecraft.item.ItemStack stack = new net.minecraft.item.ItemStack(roleItem);
            player.getInventory().insertStack(stack);
            player.sendMessage(Text.literal("Use this item to select your role!").formatted(Formatting.GREEN), false);
            return 1;
        }
        context.getSource().sendError(Text.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int createTeam(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        String name = StringArgumentType.getString(context, "name");
        String color = StringArgumentType.getString(context, "color");
        
        if (player != null) {
            if (TeamManager.createTeam(name, color, player)) {
                return 1;
            } else {
                context.getSource().sendError(Text.literal("Team already exists!"));
                return 0;
            }
        }
        context.getSource().sendError(Text.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int joinTeam(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        String name = StringArgumentType.getString(context, "name");
        
        if (player != null) {
            if (TeamManager.joinTeam(name, player)) {
                return 1;
            }
            return 0;
        }
        context.getSource().sendError(Text.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int leaveTeam(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        
        if (player != null) {
            if (TeamManager.leaveTeam(player)) {
                return 1;
            }
            return 0;
        }
        context.getSource().sendError(Text.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int listTeams(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        
        if (player != null) {
            TeamManager.listTeams(player);
            return 1;
        }
        context.getSource().sendError(Text.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int teleportToTeamMember(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        String targetName = StringArgumentType.getString(context, "player");
        
        if (player != null) {
            TeamManager.teleportToTeamMember(player, targetName);
            return 1;
        }
        context.getSource().sendError(Text.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int setPlayerRank(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        String targetName = StringArgumentType.getString(context, "player");
        String rankName = StringArgumentType.getString(context, "rank");
        
        if (player != null) {
            if (RankManager.setRankByName(targetName, rankName, player)) {
                return 1;
            }
            return 0;
        }
        context.getSource().sendError(Text.literal("Only players can use this command!"));
        return 0;
    }
}