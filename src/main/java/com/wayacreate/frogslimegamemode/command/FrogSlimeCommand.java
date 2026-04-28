package com.wayacreate.frogslimegamemode.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.wayacreate.frogslimegamemode.achievements.AchievementManager;
import com.wayacreate.frogslimegamemode.abilities.PlayerAbilityManager;
import com.wayacreate.frogslimegamemode.dimension.TransformedEndTeleporter;
import com.wayacreate.frogslimegamemode.eating.MobAbility;
import com.wayacreate.frogslimegamemode.gamemode.ContractManager;
import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import com.wayacreate.frogslimegamemode.gamemode.ManhuntManager;
import com.wayacreate.frogslimegamemode.gamemode.PlayerLevel;
import com.wayacreate.frogslimegamemode.gamemode.RankManager;
import com.wayacreate.frogslimegamemode.gamemode.TeamManager;
import com.wayacreate.frogslimegamemode.item.ModItems;
import com.wayacreate.frogslimegamemode.network.ModNetworking;
import com.wayacreate.frogslimegamemode.progression.ProgressionUnlock;
import com.wayacreate.frogslimegamemode.tasks.TaskManager;
import com.wayacreate.frogslimegamemode.tasks.TaskType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class FrogSlimeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("frogslime")
            .then(CommandManager.literal("enable")
                .executes(FrogSlimeCommand::startGamemode))
            .then(CommandManager.literal("disable")
                .executes(FrogSlimeCommand::stopGamemode))
            .then(CommandManager.literal("help")
                .executes(FrogSlimeCommand::showHelp))
            .then(CommandManager.literal("info")
                .executes(FrogSlimeCommand::showHelp))
            .then(CommandManager.literal("guide")
                .executes(FrogSlimeCommand::giveGuide))
            .then(CommandManager.literal("progress")
                .executes(FrogSlimeCommand::showProgress))
            .then(CommandManager.literal("abilities")
                .executes(FrogSlimeCommand::showAbilities))
            .then(CommandManager.literal("recipes")
                .executes(FrogSlimeCommand::showRecipes))
            .then(CommandManager.literal("tasks")
                .executes(FrogSlimeCommand::openTasks))
            .then(CommandManager.literal("reset")
                .executes(FrogSlimeCommand::resetData))
            .then(CommandManager.literal("manhunt")
                .then(CommandManager.literal("auto")
                    .executes(FrogSlimeCommand::startAutoManhunt))
                .then(CommandManager.literal("team")
                    .then(CommandManager.argument("speedrunner_team", StringArgumentType.string())
                        .then(CommandManager.argument("hunter_team", StringArgumentType.string())
                            .executes(FrogSlimeCommand::startTeamManhunt))))
                .then(CommandManager.literal("speedrunner")
                    .executes(FrogSlimeCommand::setSpeedrunner))
                .then(CommandManager.literal("solo")
                    .executes(FrogSlimeCommand::setSoloSpeedrunner))
                .then(CommandManager.literal("hunter")
                    .executes(FrogSlimeCommand::setHunter))
                .then(CommandManager.literal("status")
                    .executes(FrogSlimeCommand::showManhuntStatus))
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
            .then(CommandManager.literal("contract")
                .then(CommandManager.literal("list")
                    .executes(FrogSlimeCommand::listAvailableContracts))
                .then(CommandManager.literal("accept")
                    .then(CommandManager.argument("type", StringArgumentType.string())
                        .executes(FrogSlimeCommand::acceptContract)))
                .then(CommandManager.literal("my")
                    .executes(FrogSlimeCommand::listMyContracts)))
            .then(EconomyCommands.buildShopSubcommand())
            .then(EconomyCommands.buildTradeSubcommand())
            .then(EconomyCommands.buildBalanceSubcommand())
            .then(EconomyCommands.buildPaySubcommand())
            .then(EconomyCommands.buildMessageSubcommand())
            .then(GuildCommand.buildGuildSubcommand("guild"))
            .then(CommandManager.literal("test")
                .then(CommandManager.literal("achievement")
                    .then(CommandManager.argument("id", StringArgumentType.string())
                        .executes(FrogSlimeCommand::testAchievement))))
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
    
    private static int showHelp(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            player.sendMessage(Text.literal("=== Frog & Slime Command Board ===")
                .formatted(Formatting.GOLD, Formatting.BOLD), false);
            player.sendMessage(Text.literal("Core route").formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("  /frogslime enable - Start the route for online players")
                .formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("  /frogslime progress - Show live tasks and next unlocks")
                .formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("  /frogslime abilities - List unlocked abilities and the selected slot")
                .formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("  /frogslime recipes - Explain the main crafting route")
                .formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("  /frogslime guide - Reclaim the guide book and task book")
                .formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("  /frogslime tasks - Open the progression board")
                .formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("Contracts and SMP").formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("  /frogslime contract list - View available contracts")
                .formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("  /frogslime contract accept <id> - Start a contract")
                .formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("  /frogslime contract my - Track accepted contracts")
                .formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("Manhunt").formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("  /frogslime manhunt speedrunner | hunter | auto | status | end")
                .formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("Hunter controls: hold tracker or compass, TAB cycles, R uses.")
                .formatted(Formatting.RED), false);
            player.sendMessage(Text.literal("Speedrunner controls: hold a clock, TAB cycles, R uses.")
                .formatted(Formatting.AQUA), false);
            return 1;
        }
        return 0;
    }

    private static int showInfo(CommandContext<ServerCommandSource> context) {
        return showHelp(context);
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
    
    private static int startTeamManhunt(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        String speedrunnerTeam = StringArgumentType.getString(context, "speedrunner_team");
        String hunterTeam = StringArgumentType.getString(context, "hunter_team");
        
        if (player != null) {
            ManhuntManager.startTeamManhunt(player, speedrunnerTeam, hunterTeam);
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
            ServerPlayerEntity target = ManhuntManager.getPrimarySpeedrunner(player.getServer());
            if (target == player) {
                target = null;
            }
            
            for (ServerPlayerEntity other : player.getServer().getPlayerManager().getPlayerList()) {
                if (target != null) {
                    break;
                }
                if (other != player && !ManhuntManager.isHunter(other)) {
                    target = other;
                }
            }
            
            if (target != null) {
                ManhuntManager.setHunter(player, target);
            } else {
                player.sendMessage(Text.literal("No valid speedrunner target found.")
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
            ManhuntManager.endAllGames(player.getServer());
            return 1;
        }
        context.getSource().sendError(Text.literal("Only players can use this command!"));
        return 0;
    }

    private static int showManhuntStatus(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }

        player.sendMessage(Text.literal("=== Manhunt Status ===")
            .formatted(Formatting.GOLD, Formatting.BOLD), false);

        if (ManhuntManager.isCountdownActive()) {
            player.sendMessage(Text.literal("Countdown: " + ManhuntManager.getCountdownSeconds() + "s")
                .formatted(Formatting.YELLOW), false);
        }

        player.sendMessage(Text.literal("Hunters: " + ManhuntManager.getActiveHunterCount()
            + " | Active speedrunners: " + ManhuntManager.getActiveSpeedrunnerCount()
            + " | Ghost speedrunners: " + ManhuntManager.getGhostSpeedrunnerCount())
            .formatted(Formatting.WHITE), false);

        if (ManhuntManager.isHunter(player)) {
            ServerPlayerEntity target = ManhuntManager.getTarget(player);
            player.sendMessage(Text.literal("You are a hunter.")
                .formatted(Formatting.RED, Formatting.BOLD), false);
            player.sendMessage(Text.literal("Target: " + (target != null ? target.getName().getString() : "None"))
                .formatted(Formatting.YELLOW), false);
        } else if (ManhuntManager.isSpeedrunner(player)) {
            player.sendMessage(Text.literal("You are the speedrunner.")
                .formatted(Formatting.GREEN, Formatting.BOLD), false);
            player.sendMessage(Text.literal("Elapsed: " + ManhuntManager.getElapsedTime(player)
                + " | Deaths: " + ManhuntManager.getDeathCount(player))
                .formatted(Formatting.WHITE), false);
        } else {
            ServerPlayerEntity activeTarget = ManhuntManager.getPrimarySpeedrunner(player.getServer());
            if (activeTarget != null) {
                player.sendMessage(Text.literal("Current lead speedrunner: " + activeTarget.getName().getString())
                    .formatted(Formatting.AQUA), false);
            }
        }

        return 1;
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
            player.getInventory().insertStack(new net.minecraft.item.ItemStack(ModItems.MINER_ROLE));
            player.getInventory().insertStack(new net.minecraft.item.ItemStack(ModItems.LUMBERJACK_ROLE));
            player.getInventory().insertStack(new net.minecraft.item.ItemStack(ModItems.COMBAT_ROLE));
            player.getInventory().insertStack(new net.minecraft.item.ItemStack(ModItems.BUILDER_ROLE));
            player.getInventory().insertStack(new net.minecraft.item.ItemStack(ModItems.FARMER_ROLE));
            player.sendMessage(Text.literal("Gave you the full helper role kit.")
                .formatted(Formatting.GREEN), false);
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
    
    private static int listAvailableContracts(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            ContractManager.listAvailableContracts(player);
            return 1;
        }
        context.getSource().sendError(Text.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int acceptContract(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        String contractType = StringArgumentType.getString(context, "type");
        
        if (player != null) {
            ContractManager.acceptContract(player, contractType);
            return 1;
        }
        context.getSource().sendError(Text.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int listMyContracts(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            ContractManager.listContracts(player);
            return 1;
        }
        context.getSource().sendError(Text.literal("Only players can use this command!"));
        return 0;
    }

    private static int giveGuide(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }

        player.getInventory().insertStack(GamemodeManager.createGuideBook());
        if (ModItems.TASK_BOOK != null) {
            player.getInventory().insertStack(new ItemStack(ModItems.TASK_BOOK));
        }

        player.sendMessage(Text.literal("Guide book restored. Use /frogslime tasks for the live board.")
            .formatted(Formatting.GREEN), false);
        if (GamemodeManager.isInGamemode(player)) {
            ModNetworking.openTasksScreen(player);
        }
        return 1;
    }

    private static int showProgress(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }

        if (!GamemodeManager.isInGamemode(player)) {
            player.sendMessage(Text.literal("Enable the route first with /frogslime enable.")
                .formatted(Formatting.RED), false);
            return 0;
        }

        TaskManager.syncDerivedTasks(player);
        ModNetworking.sendProgressSnapshot(player);

        player.sendMessage(Text.literal("=== Route Progress ===")
            .formatted(Formatting.GOLD, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Level " + PlayerLevel.getLevel(player)
            + " | Completion " + (int) (TaskManager.getOverallProgress(player) * 100)
            + "% | Tasks " + TaskManager.getCompletedTaskCount(player) + "/" + TaskType.values().length)
            .formatted(Formatting.YELLOW), false);

        for (TaskType task : TaskManager.getActiveObjectives(player, 3)) {
            int progress = GamemodeManager.getData(player).getTaskProgress(task);
            player.sendMessage(Text.literal("- " + task.getDisplayName() + " [" + progress + "/" + task.getRequiredAmount() + "]")
                .formatted(task.getColor()), false);
            player.sendMessage(Text.literal("  " + task.getDescription() + " -> " + task.getRewardText())
                .formatted(Formatting.GRAY), false);
        }

        List<String> unlocks = getUpcomingUnlocks(player, 3);
        if (!unlocks.isEmpty()) {
            player.sendMessage(Text.literal("Next unlocks").formatted(Formatting.AQUA), false);
            for (String unlock : unlocks) {
                player.sendMessage(Text.literal("  " + unlock).formatted(Formatting.WHITE), false);
            }
        }
        return 1;
    }

    private static int showAbilities(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }

        if (!GamemodeManager.isInGamemode(player)) {
            player.sendMessage(Text.literal("Enable the route first with /frogslime enable.")
                .formatted(Formatting.RED), false);
            return 0;
        }

        List<String> abilities = GamemodeManager.getData(player).getPlayerAbilities();
        MobAbility selected = PlayerAbilityManager.getCurrentAbility(player);

        player.sendMessage(Text.literal("=== Player Abilities ===")
            .formatted(Formatting.GOLD, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Unlocked: " + abilities.size())
            .formatted(Formatting.YELLOW), false);
        if (selected != null) {
            player.sendMessage(Text.literal("Selected: " + selected.getName() + " - " + selected.getDescription())
                .formatted(Formatting.AQUA), false);
        }
        player.sendMessage(Text.literal("TAB cycles, R uses the selected active ability.")
            .formatted(Formatting.GRAY), false);

        for (String abilityId : abilities) {
            MobAbility ability = MobAbility.getAbility(abilityId);
            if (ability != null) {
                player.sendMessage(Text.literal("- " + ability.getName() + ": " + ability.getDescription())
                    .formatted(Formatting.WHITE), false);
            } else {
                player.sendMessage(Text.literal("- " + abilityId)
                    .formatted(Formatting.WHITE), false);
            }
        }
        return 1;
    }

    private static int showRecipes(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }

        player.sendMessage(Text.literal("=== Route Recipes ===")
            .formatted(Formatting.GOLD, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Ability Forge: crafting table + emeralds + slime balls.")
            .formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("Frog Crafting Table: crafting tables + slime balls.")
            .formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("Ability Stick: stick + stone + dirt + sand.")
            .formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("Main crafting loop: use mob drops at the Ability Forge for final mob ability items.")
            .formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("Legacy loop: combine a matching drop with an Ability Stick in an anvil.")
            .formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("Contracts: /frogslime contract list, then /frogslime contract accept <id>.")
            .formatted(Formatting.YELLOW), false);
        return 1;
    }

    private static List<String> getUpcomingUnlocks(ServerPlayerEntity player, int limit) {
        List<String> upcoming = new ArrayList<>();
        int level = PlayerLevel.getLevel(player);
        int evolutionStage = TaskManager.getHighestHelperEvolution(player);

        for (ProgressionUnlock.Unlock unlock : ProgressionUnlock.getUnlocksForLevel(level + 10)) {
            if (!ProgressionUnlock.isUnlocked(unlock.getId(), level, evolutionStage)) {
                upcoming.add(unlock.getName() + " - " + unlock.getDescription()
                    + " (Lv " + unlock.getRequiredLevel() + ", Evo " + unlock.getRequiredEvolutionStage() + ")");
                if (upcoming.size() >= limit) {
                    break;
                }
            }
        }

        return upcoming;
    }
    
    private static int testAchievement(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        String achievementId = StringArgumentType.getString(context, "id");
        
        if (player != null) {
            AchievementManager.unlockAchievement(player, achievementId);
            player.sendMessage(Text.literal("Testing achievement: " + achievementId)
                .formatted(Formatting.YELLOW), false);
            return 1;
        }
        context.getSource().sendError(Text.literal("Only players can use this command!"));
        return 0;
    }
}
