package com.wayacreate.frogslimegamemode.tasks;

import com.wayacreate.frogslimegamemode.achievements.AchievementManager;
import com.wayacreate.frogslimegamemode.entity.FrogHelperEntity;
import com.wayacreate.frogslimegamemode.entity.SlimeHelperEntity;
import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import com.wayacreate.frogslimegamemode.network.ModNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TaskManager {
    public static void init() {
        // Initialize task system
    }
    
    public static void tick(MinecraftServer server) {
        if (server.getTicks() % 20 != 0) {
            return;
        }

        // Check task completion for all players in gamemode
        for (var world : server.getWorlds()) {
            for (var player : world.getPlayers()) {
                if (GamemodeManager.isInGamemode(player)) {
                    syncDerivedTasks(player);
                }
            }
        }
    }
    
    public static void syncDerivedTasks(PlayerEntity player) {
        if (!GamemodeManager.isInGamemode(player)) {
            return;
        }

        setTaskProgress(player, TaskType.ACTIVATE_GAMEMODE, 1);
        setTaskProgress(player, TaskType.TAME_HELPER, GamemodeManager.getData(player).getHelpersSpawned());
        setTaskProgress(player, TaskType.UNLOCK_ABILITIES, GamemodeManager.getData(player).getPlayerAbilities().size());
        setTaskProgress(player, TaskType.EVOLVE_HELPER, getHighestHelperEvolution(player));

        if (player instanceof ServerPlayerEntity serverPlayer) {
            int abilityCount = GamemodeManager.getData(player).getPlayerAbilities().size();
            if (abilityCount >= 1) {
                AchievementManager.unlockAchievement(serverPlayer, "ability_unlock");
            }
            if (abilityCount >= 12) {
                AchievementManager.unlockAchievement(serverPlayer, "ability_master");
            }
        }

        if (player.getWorld().getRegistryKey() == World.NETHER) {
            setTaskProgress(player, TaskType.REACH_NETHER, 1);
            if (player instanceof ServerPlayerEntity serverPlayer) {
                AchievementManager.unlockAchievement(serverPlayer, "reach_nether");
            }
        }

        if (player.getWorld().getRegistryKey() == World.END) {
            setTaskProgress(player, TaskType.REACH_END, 1);
            if (player instanceof ServerPlayerEntity serverPlayer) {
                AchievementManager.unlockAchievement(serverPlayer, "end_reached");
            }
        }
    }

    public static void advanceTask(PlayerEntity player, TaskType task, int amount) {
        if (!GamemodeManager.isInGamemode(player) || amount <= 0) {
            return;
        }

        var data = GamemodeManager.getData(player);
        int nextValue = data.getTaskProgress(task) + amount;
        setTaskProgress(player, task, nextValue);
    }

    public static void completeTask(PlayerEntity player, TaskType task) {
        setTaskProgress(player, task, task.getRequiredAmount());
    }

    public static void setTaskProgress(PlayerEntity player, TaskType task, int progress) {
        if (!GamemodeManager.isInGamemode(player)) {
            return;
        }

        var data = GamemodeManager.getData(player);
        int current = data.getTaskProgress(task);
        int clamped = Math.min(task.getRequiredAmount(), Math.max(0, progress));
        if (current == clamped) {
            return;
        }

        data.setTaskProgress(task, clamped);
        if (current < task.getRequiredAmount() && clamped >= task.getRequiredAmount()) {
            onTaskCompleted(player, task);
        }
    }

    private static void onTaskCompleted(PlayerEntity player, TaskType task) {
        player.sendMessage(Text.literal("Objective Complete: ")
            .formatted(Formatting.GREEN, Formatting.BOLD)
            .append(Text.literal(task.getDisplayName()).formatted(task.getColor(), Formatting.BOLD)), false);

        if (player instanceof ServerPlayerEntity serverPlayer) {
            ModNetworking.sendTotemAnimation(
                serverPlayer,
                "Objective Complete!",
                task.getDisplayName(),
                task.getColor()
            );
        }
    }
    
    public static void getTaskProgress(PlayerEntity player) {
        if (!GamemodeManager.isInGamemode(player)) {
            return;
        }
        
        var data = GamemodeManager.getData(player);
        player.sendMessage(Text.literal("=== Task Progress ===")
            .formatted(Formatting.GOLD, Formatting.BOLD), false);
        
        for (TaskType task : TaskType.values()) {
            int progress = data.getTaskProgress(task);
            int required = task.getRequiredAmount();
            boolean complete = data.isTaskComplete(task);
            
            player.sendMessage(Text.literal(task.getDisplayName() + ": " + progress + "/" + required)
                .formatted(complete ? Formatting.GREEN : Formatting.WHITE), false);
        }
    }

    public static float getOverallProgress(PlayerEntity player) {
        if (!GamemodeManager.isInGamemode(player)) {
            return 0.0f;
        }

        var data = GamemodeManager.getData(player);
        float total = 0.0f;
        for (TaskType task : TaskType.values()) {
            total += Math.min(1.0f, data.getTaskProgress(task) / (float) task.getRequiredAmount());
        }
        return total / TaskType.values().length;
    }

    public static int getCompletedTaskCount(PlayerEntity player) {
        if (!GamemodeManager.isInGamemode(player)) {
            return 0;
        }

        int completed = 0;
        var data = GamemodeManager.getData(player);
        for (TaskType task : TaskType.values()) {
            if (data.isTaskComplete(task)) {
                completed++;
            }
        }
        return completed;
    }

    public static List<TaskType> getActiveObjectives(PlayerEntity player, int limit) {
        List<TaskType> tasks = new ArrayList<>();
        if (!GamemodeManager.isInGamemode(player)) {
            return tasks;
        }

        var data = GamemodeManager.getData(player);
        for (TaskType task : TaskType.values()) {
            if (!data.isTaskComplete(task)) {
                tasks.add(task);
            }
        }

        tasks.sort(Comparator.comparing(TaskType::ordinal));
        return tasks.size() > limit ? tasks.subList(0, limit) : tasks;
    }

    public static int getHighestHelperEvolution(PlayerEntity player) {
        int highest = 0;

        var frogs = player.getWorld().getEntitiesByClass(
            FrogHelperEntity.class,
            player.getBoundingBox().expand(64.0),
            frog -> frog.isOwner(player)
        );
        for (FrogHelperEntity frog : frogs) {
            highest = Math.max(highest, frog.getEvolutionStage());
        }

        var slimes = player.getWorld().getEntitiesByClass(
            SlimeHelperEntity.class,
            player.getBoundingBox().expand(64.0),
            slime -> slime.isOwner(player)
        );
        for (SlimeHelperEntity slime : slimes) {
            highest = Math.max(highest, slime.getEvolutionStage());
        }

        return highest;
    }
}
