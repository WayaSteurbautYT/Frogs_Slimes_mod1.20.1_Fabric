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
import net.minecraft.world.item.ItemStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.List;

public class FrogSlimeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("frogslime")
            .then(Commands.literal("enable")
                .executes(FrogSlimeCommand::startGamemode))
            .then(Commands.literal("disable")
                .executes(FrogSlimeCommand::stopGamemode))
            .then(Commands.literal("help")
                .executes(FrogSlimeCommand::showHelp))
            .then(Commands.literal("info")
                .executes(FrogSlimeCommand::showHelp))
            .then(Commands.literal("guide")
                .executes(FrogSlimeCommand::giveGuide))
            .then(Commands.literal("progress")
                .executes(FrogSlimeCommand::showProgress))
            .then(Commands.literal("abilities")
                .executes(FrogSlimeCommand::showAbilities))
            .then(Commands.literal("recipes")
                .executes(FrogSlimeCommand::showRecipes))
            .then(Commands.literal("tasks")
                .executes(FrogSlimeCommand::openTasks))
            .then(Commands.literal("reset")
                .executes(FrogSlimeCommand::resetData))
            .then(Commands.literal("manhunt")
                .then(Commands.literal("auto")
                    .executes(FrogSlimeCommand::startAutoManhunt))
                .then(Commands.literal("team")
                    .then(Commands.argument("speedrunner_team", StringArgumentType.string())
                        .then(Commands.argument("hunter_team", StringArgumentType.string())
                            .executes(FrogSlimeCommand::startTeamManhunt))))
                .then(Commands.literal("speedrunner")
                    .executes(FrogSlimeCommand::setSpeedrunner))
                .then(Commands.literal("solo")
                    .executes(FrogSlimeCommand::setSoloSpeedrunner))
                .then(Commands.literal("hunter")
                    .executes(FrogSlimeCommand::setHunter))
                .then(Commands.literal("status")
                    .executes(FrogSlimeCommand::showManhuntStatus))
                .then(Commands.literal("end")
                    .executes(FrogSlimeCommand::endManhunt)))
            .then(Commands.literal("dimension")
                .then(Commands.literal("transformed_end")
                    .executes(FrogSlimeCommand::teleportToTransformedEnd))
                .then(Commands.literal("return")
                    .executes(FrogSlimeCommand::returnFromDimension)))
            .then(Commands.literal("role")
                .then(Commands.literal("giverole")
                    .executes(FrogSlimeCommand::giveRoleItem)))
            .then(Commands.literal("team")
                .then(Commands.literal("create")
                    .then(Commands.argument("name", StringArgumentType.string())
                        .then(Commands.argument("color", StringArgumentType.string())
                            .executes(FrogSlimeCommand::createTeam))))
                .then(Commands.literal("join")
                    .then(Commands.argument("name", StringArgumentType.string())
                        .executes(FrogSlimeCommand::joinTeam)))
                .then(Commands.literal("leave")
                    .executes(FrogSlimeCommand::leaveTeam))
                .then(Commands.literal("list")
                    .executes(FrogSlimeCommand::listTeams))
                .then(Commands.literal("tp")
                    .then(Commands.argument("player", StringArgumentType.string())
                        .executes(FrogSlimeCommand::teleportToTeamMember))))
            .then(Commands.literal("rank")
                .then(Commands.argument("player", StringArgumentType.string())
                    .then(Commands.argument("rank", StringArgumentType.string())
                        .executes(FrogSlimeCommand::setPlayerRank))))
            .then(Commands.literal("contract")
                .then(Commands.literal("list")
                    .executes(FrogSlimeCommand::listAvailableContracts))
                .then(Commands.literal("accept")
                    .then(Commands.argument("type", StringArgumentType.string())
                        .executes(FrogSlimeCommand::acceptContract)))
                .then(Commands.literal("my")
                    .executes(FrogSlimeCommand::listMyContracts)))
            .then(EconomyCommands.buildShopSubcommand())
            .then(EconomyCommands.buildTradeSubcommand())
            .then(EconomyCommands.buildBalanceSubcommand())
            .then(EconomyCommands.buildPaySubcommand())
            .then(EconomyCommands.buildMessageSubcommand())
            .then(GuildCommand.buildGuildSubcommand("guild"))
            .then(Commands.literal("test")
                .then(Commands.literal("achievement")
                    .then(Commands.argument("id", StringArgumentType.string())
                        .executes(FrogSlimeCommand::testAchievement))))
        );
    }
    
    private static int startGamemode(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            GamemodeManager.enableGamemode(player);
            return 1;
        }
        context.getSource().sendError(Component.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int stopGamemode(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            GamemodeManager.disableGamemode(player);
            return 1;
        }
        context.getSource().sendError(Component.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int showHelp(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            player.sendMessage(Component.literal("=== Frog & Slime Command Board ===")
                .formatted(ChatFormatting.GOLD, ChatFormatting.BOLD), false);
            player.sendMessage(Component.literal("Core route").formatted(ChatFormatting.YELLOW), false);
            player.sendMessage(Component.literal("  /frogslime enable - Start the route for online players")
                .formatted(ChatFormatting.WHITE), false);
            player.sendMessage(Component.literal("  /frogslime progress - Show live tasks and next unlocks")
                .formatted(ChatFormatting.WHITE), false);
            player.sendMessage(Component.literal("  /frogslime abilities - List unlocked abilities and the selected slot")
                .formatted(ChatFormatting.WHITE), false);
            player.sendMessage(Component.literal("  /frogslime recipes - Explain the main crafting route")
                .formatted(ChatFormatting.WHITE), false);
            player.sendMessage(Component.literal("  /frogslime guide - Reclaim the guide book and task book")
                .formatted(ChatFormatting.WHITE), false);
            player.sendMessage(Component.literal("  /frogslime tasks - Open the progression board")
                .formatted(ChatFormatting.WHITE), false);
            player.sendMessage(Component.literal("Contracts and SMP").formatted(ChatFormatting.YELLOW), false);
            player.sendMessage(Component.literal("  /frogslime contract list - View available contracts")
                .formatted(ChatFormatting.WHITE), false);
            player.sendMessage(Component.literal("  /frogslime contract accept <id> - Start a contract")
                .formatted(ChatFormatting.WHITE), false);
            player.sendMessage(Component.literal("  /frogslime contract my - Track accepted contracts")
                .formatted(ChatFormatting.WHITE), false);
            player.sendMessage(Component.literal("Manhunt").formatted(ChatFormatting.YELLOW), false);
            player.sendMessage(Component.literal("  /frogslime manhunt speedrunner | hunter | auto | status | end")
                .formatted(ChatFormatting.WHITE), false);
            player.sendMessage(Component.literal("Hunter controls: hold tracker or compass, TAB cycles, R uses.")
                .formatted(ChatFormatting.RED), false);
            player.sendMessage(Component.literal("Speedrunner controls: hold a clock, TAB cycles, R uses.")
                .formatted(ChatFormatting.AQUA), false);
            return 1;
        }
        return 0;
    }

    private static int showInfo(CommandContext<CommandSourceStack> context) {
        return showHelp(context);
    }
    
    private static int openTasks(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            ModNetworking.openTasksScreen(player);
            return 1;
        }
        return 0;
    }
    
    private static int resetData(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            // Disable gamemode first
            if (GamemodeManager.isInGamemode(player)) {
                GamemodeManager.disableGamemode(player);
            }
            
            // Reset ability manager data for this player
            com.wayacreate.frogslimegamemode.abilities.PlayerAbilityManager.resetPlayer(player.getUuid());
            
            player.sendMessage(Component.literal("All Frog & Slime Gamemode data has been reset!")
                .formatted(ChatFormatting.GREEN, ChatFormatting.BOLD), false);
            player.sendMessage(Component.literal("Use /frogslime enable to start a new game.")
                .formatted(ChatFormatting.YELLOW), false);
            
            return 1;
        }
        context.getSource().sendError(Component.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int startAutoManhunt(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            ManhuntManager.startAutoManhunt(player);
            return 1;
        }
        context.getSource().sendError(Component.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int startTeamManhunt(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        String speedrunnerTeam = StringArgumentType.getString(context, "speedrunner_team");
        String hunterTeam = StringArgumentType.getString(context, "hunter_team");
        
        if (player != null) {
            ManhuntManager.startTeamManhunt(player, speedrunnerTeam, hunterTeam);
            return 1;
        }
        context.getSource().sendError(Component.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int setSpeedrunner(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            ManhuntManager.setSpeedrunner(player);
            return 1;
        }
        context.getSource().sendError(Component.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int setSoloSpeedrunner(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            ManhuntManager.setSoloSpeedrunner(player);
            return 1;
        }
        context.getSource().sendError(Component.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int setHunter(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            ServerPlayer target = ManhuntManager.getPrimarySpeedrunner(player.getServer());
            if (target == player) {
                target = null;
            }
            
            for (ServerPlayer other : player.getServer().getPlayerManager().getPlayerList()) {
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
                player.sendMessage(Component.literal("No valid speedrunner target found.")
                    .formatted(ChatFormatting.RED), false);
            }
            return 1;
        }
        context.getSource().sendError(Component.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int endManhunt(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            ManhuntManager.endAllGames(player.getServer());
            return 1;
        }
        context.getSource().sendError(Component.literal("Only players can use this command!"));
        return 0;
    }

    private static int showManhuntStatus(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }

        player.sendMessage(Component.literal("=== Manhunt Status ===")
            .formatted(ChatFormatting.GOLD, ChatFormatting.BOLD), false);

        if (ManhuntManager.isCountdownActive()) {
            player.sendMessage(Component.literal("Countdown: " + ManhuntManager.getCountdownSeconds() + "s")
                .formatted(ChatFormatting.YELLOW), false);
        }

        player.sendMessage(Component.literal("Hunters: " + ManhuntManager.getActiveHunterCount()
            + " | Active speedrunners: " + ManhuntManager.getActiveSpeedrunnerCount()
            + " | Ghost speedrunners: " + ManhuntManager.getGhostSpeedrunnerCount())
            .formatted(ChatFormatting.WHITE), false);

        if (ManhuntManager.isHunter(player)) {
            ServerPlayer target = ManhuntManager.getTarget(player);
            player.sendMessage(Component.literal("You are a hunter.")
                .formatted(ChatFormatting.RED, ChatFormatting.BOLD), false);
            player.sendMessage(Component.literal("Target: " + (target != null ? target.getName().getString() : "None"))
                .formatted(ChatFormatting.YELLOW), false);
        } else if (ManhuntManager.isSpeedrunner(player)) {
            player.sendMessage(Component.literal("You are the speedrunner.")
                .formatted(ChatFormatting.GREEN, ChatFormatting.BOLD), false);
            player.sendMessage(Component.literal("Elapsed: " + ManhuntManager.getElapsedTime(player)
                + " | Deaths: " + ManhuntManager.getDeathCount(player))
                .formatted(ChatFormatting.WHITE), false);
        } else {
            ServerPlayer activeTarget = ManhuntManager.getPrimarySpeedrunner(player.getServer());
            if (activeTarget != null) {
                player.sendMessage(Component.literal("Current lead speedrunner: " + activeTarget.getName().getString())
                    .formatted(ChatFormatting.AQUA), false);
            }
        }

        return 1;
    }
    
    private static int teleportToTransformedEnd(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            TransformedEndTeleporter.teleportToTransformedEnd(player);
            player.sendMessage(Component.literal("Use /frogslime dimension return to go back!")
                .formatted(ChatFormatting.YELLOW), true);
            return 1;
        }
        context.getSource().sendError(Component.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int returnFromDimension(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            TransformedEndTeleporter.teleportFromTransformedEnd(player);
            return 1;
        }
        context.getSource().sendError(Component.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int giveRoleItem(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            player.getInventory().insertStack(new net.minecraft.world.item.ItemStack(ModItems.MINER_ROLE));
            player.getInventory().insertStack(new net.minecraft.world.item.ItemStack(ModItems.LUMBERJACK_ROLE));
            player.getInventory().insertStack(new net.minecraft.world.item.ItemStack(ModItems.COMBAT_ROLE));
            player.getInventory().insertStack(new net.minecraft.world.item.ItemStack(ModItems.BUILDER_ROLE));
            player.getInventory().insertStack(new net.minecraft.world.item.ItemStack(ModItems.FARMER_ROLE));
            player.sendMessage(Component.literal("Gave you the full helper role kit.")
                .formatted(ChatFormatting.GREEN), false);
            return 1;
        }
        context.getSource().sendError(Component.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int createTeam(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        String name = StringArgumentType.getString(context, "name");
        String color = StringArgumentType.getString(context, "color");
        
        if (player != null) {
            if (TeamManager.createTeam(name, color, player)) {
                return 1;
            } else {
                context.getSource().sendError(Component.literal("Team already exists!"));
                return 0;
            }
        }
        context.getSource().sendError(Component.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int joinTeam(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        String name = StringArgumentType.getString(context, "name");
        
        if (player != null) {
            if (TeamManager.joinTeam(name, player)) {
                return 1;
            }
            return 0;
        }
        context.getSource().sendError(Component.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int leaveTeam(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        
        if (player != null) {
            if (TeamManager.leaveTeam(player)) {
                return 1;
            }
            return 0;
        }
        context.getSource().sendError(Component.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int listTeams(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        
        if (player != null) {
            TeamManager.listTeams(player);
            return 1;
        }
        context.getSource().sendError(Component.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int teleportToTeamMember(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        String targetName = StringArgumentType.getString(context, "player");
        
        if (player != null) {
            TeamManager.teleportToTeamMember(player, targetName);
            return 1;
        }
        context.getSource().sendError(Component.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int setPlayerRank(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        String targetName = StringArgumentType.getString(context, "player");
        String rankName = StringArgumentType.getString(context, "rank");
        
        if (player != null) {
            if (RankManager.setRankByName(targetName, rankName, player)) {
                return 1;
            }
            return 0;
        }
        context.getSource().sendError(Component.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int listAvailableContracts(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            ContractManager.listAvailableContracts(player);
            return 1;
        }
        context.getSource().sendError(Component.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int acceptContract(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        String contractType = StringArgumentType.getString(context, "type");
        
        if (player != null) {
            ContractManager.acceptContract(player, contractType);
            return 1;
        }
        context.getSource().sendError(Component.literal("Only players can use this command!"));
        return 0;
    }
    
    private static int listMyContracts(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            ContractManager.listContracts(player);
            return 1;
        }
        context.getSource().sendError(Component.literal("Only players can use this command!"));
        return 0;
    }

    private static int giveGuide(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }

        player.getInventory().insertStack(GamemodeManager.createGuideBook());
        if (ModItems.TASK_BOOK != null) {
            player.getInventory().insertStack(new ItemStack(ModItems.TASK_BOOK));
        }

        player.sendMessage(Component.literal("Guide book restored. Use /frogslime tasks for the live board.")
            .formatted(ChatFormatting.GREEN), false);
        if (GamemodeManager.isInGamemode(player)) {
            ModNetworking.openTasksScreen(player);
        }
        return 1;
    }

    private static int showProgress(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }

        if (!GamemodeManager.isInGamemode(player)) {
            player.sendMessage(Component.literal("Enable the route first with /frogslime enable.")
                .formatted(ChatFormatting.RED), false);
            return 0;
        }

        TaskManager.syncDerivedTasks(player);
        ModNetworking.sendProgressSnapshot(player);

        player.sendMessage(Component.literal("=== Route Progress ===")
            .formatted(ChatFormatting.GOLD, ChatFormatting.BOLD), false);
        player.sendMessage(Component.literal("Level " + PlayerLevel.getLevel(player)
            + " | Completion " + (int) (TaskManager.getOverallProgress(player) * 100)
            + "% | Tasks " + TaskManager.getCompletedTaskCount(player) + "/" + TaskType.values().length)
            .formatted(ChatFormatting.YELLOW), false);

        for (TaskType task : TaskManager.getActiveObjectives(player, 3)) {
            int progress = GamemodeManager.getData(player).getTaskProgress(task);
            player.sendMessage(Component.literal("- " + task.getDisplayName() + " [" + progress + "/" + task.getRequiredAmount() + "]")
                .formatted(task.getColor()), false);
            player.sendMessage(Component.literal("  " + task.getDescription() + " -> " + task.getRewardText())
                .formatted(ChatFormatting.GRAY), false);
        }

        List<String> unlocks = getUpcomingUnlocks(player, 3);
        if (!unlocks.isEmpty()) {
            player.sendMessage(Component.literal("Next unlocks").formatted(ChatFormatting.AQUA), false);
            for (String unlock : unlocks) {
                player.sendMessage(Component.literal("  " + unlock).formatted(ChatFormatting.WHITE), false);
            }
        }
        return 1;
    }

    private static int showAbilities(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }

        if (!GamemodeManager.isInGamemode(player)) {
            player.sendMessage(Component.literal("Enable the route first with /frogslime enable.")
                .formatted(ChatFormatting.RED), false);
            return 0;
        }

        List<String> abilities = GamemodeManager.getData(player).getPlayerAbilities();
        MobAbility selected = PlayerAbilityManager.getCurrentAbility(player);

        player.sendMessage(Component.literal("=== Player Abilities ===")
            .formatted(ChatFormatting.GOLD, ChatFormatting.BOLD), false);
        player.sendMessage(Component.literal("Unlocked: " + abilities.size())
            .formatted(ChatFormatting.YELLOW), false);
        if (selected != null) {
            player.sendMessage(Component.literal("Selected: " + selected.getName() + " - " + selected.getDescription())
                .formatted(ChatFormatting.AQUA), false);
        }
        player.sendMessage(Component.literal("TAB cycles, R uses the selected active ability.")
            .formatted(ChatFormatting.GRAY), false);

        for (String abilityId : abilities) {
            MobAbility ability = MobAbility.getAbility(abilityId);
            if (ability != null) {
                player.sendMessage(Component.literal("- " + ability.getName() + ": " + ability.getDescription())
                    .formatted(ChatFormatting.WHITE), false);
            } else {
                player.sendMessage(Component.literal("- " + abilityId)
                    .formatted(ChatFormatting.WHITE), false);
            }
        }
        return 1;
    }

    private static int showRecipes(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }

        player.sendMessage(Component.literal("=== Route Recipes ===")
            .formatted(ChatFormatting.GOLD, ChatFormatting.BOLD), false);
        player.sendMessage(Component.literal("Ability Forge: crafting table + emeralds + slime balls.")
            .formatted(ChatFormatting.WHITE), false);
        player.sendMessage(Component.literal("Frog Crafting Table: crafting tables + slime balls.")
            .formatted(ChatFormatting.WHITE), false);
        player.sendMessage(Component.literal("Ability Stick: stick + stone + dirt + sand.")
            .formatted(ChatFormatting.WHITE), false);
        player.sendMessage(Component.literal("Main crafting loop: use mob drops at the Ability Forge for final mob ability items.")
            .formatted(ChatFormatting.AQUA), false);
        player.sendMessage(Component.literal("Legacy loop: combine a matching drop with an Ability Stick in an anvil.")
            .formatted(ChatFormatting.GRAY), false);
        player.sendMessage(Component.literal("Contracts: /frogslime contract list, then /frogslime contract accept <id>.")
            .formatted(ChatFormatting.YELLOW), false);
        return 1;
    }

    private static List<String> getUpcomingUnlocks(ServerPlayer player, int limit) {
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
    
    private static int testAchievement(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        String achievementId = StringArgumentType.getString(context, "id");
        
        if (player != null) {
            AchievementManager.unlockAchievement(player, achievementId);
            player.sendMessage(Component.literal("Testing achievement: " + achievementId)
                .formatted(ChatFormatting.YELLOW), false);
            return 1;
        }
        context.getSource().sendError(Component.literal("Only players can use this command!"));
        return 0;
    }
}
