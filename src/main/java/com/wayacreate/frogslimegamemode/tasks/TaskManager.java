package com.wayacreate.frogslimegamemode.tasks;

import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TaskManager {
    public static void init() {
        // Initialize task system
    }
    
    public static void tick(MinecraftServer server) {
        // Check task completion for all players in gamemode
        for (var world : server.getWorlds()) {
            for (var player : world.getPlayers()) {
                if (GamemodeManager.isInGamemode(player)) {
                    checkTaskCompletion(player);
                }
            }
        }
    }
    
    public static void checkTaskCompletion(PlayerEntity player) {
        if (!GamemodeManager.isInGamemode(player)) {
            return;
        }
        
        var data = GamemodeManager.getData(player);
        
        for (TaskType task : TaskType.values()) {
            if (!data.isTaskComplete(task) && data.getTaskProgress(task) >= task.getRequiredAmount()) {
                data.setTaskProgress(task, task.getRequiredAmount());
                player.sendMessage(Text.literal("Task Completed: ")
                    .formatted(Formatting.GREEN, Formatting.BOLD)
                    .append(Text.literal(task.name())
                        .formatted(Formatting.YELLOW)), false);
            }
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
            
            player.sendMessage(Text.literal(task.name() + ": " + progress + "/" + required)
                .formatted(complete ? Formatting.GREEN : Formatting.WHITE), false);
        }
    }
}
