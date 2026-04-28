package com.wayacreate.frogslimegamemode.gamemode;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import com.wayacreate.frogslimegamemode.progression.ProgressionUnlock;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerLevel {
    private static final Map<ServerPlayer, PlayerLevelData> playerLevels = new HashMap<>();
    
    public static class PlayerLevelData {
        private int level;
        private double xp;
        private double xpToNextLevel;
        
        public PlayerLevelData() {
            this.level = 1;
            this.xp = 0;
            this.xpToNextLevel = 100;
        }
        
        public int getLevel() { return level; }
        public double getXP() { return xp; }
        public double getXPToNextLevel() { return xpToNextLevel; }
        
        public void addXP(double amount) {
            xp += amount;
            while (xp >= xpToNextLevel) {
                xp -= xpToNextLevel;
                level++;
                xpToNextLevel = calculateXPForLevel(level);
            }
        }
        
        private double calculateXPForLevel(int level) {
            return 100 * Math.pow(1.5, level - 1);
        }
    }
    
    public static void addXP(ServerPlayer player, double amount) {
        PlayerLevelData data = playerLevels.computeIfAbsent(player, k -> new PlayerLevelData());
        int oldLevel = data.getLevel();
        data.addXP(amount);
        
        if (data.getLevel() > oldLevel) {
            onLevelUp(player, data.getLevel());
        }
    }
    
    private static void onLevelUp(ServerPlayer player, int newLevel) {
        player.sendMessage(Component.literal("Level Up! You are now level " + newLevel)
            .formatted(ChatFormatting.GOLD, ChatFormatting.BOLD), true);
        
        // Check for new unlocks
        List<ProgressionUnlock.Unlock> newUnlocks = ProgressionUnlock.getUnlocksForLevel(newLevel);
        if (!newUnlocks.isEmpty()) {
            player.sendMessage(Component.literal("New unlocks available!")
                .formatted(ChatFormatting.AQUA, ChatFormatting.BOLD), true);
            
            for (ProgressionUnlock.Unlock unlock : newUnlocks) {
                if (unlock.getRequiredLevel() == newLevel) {
                    player.sendMessage(Component.literal("• ")
                        .append(unlock.getFormattedName())
                        .append(Component.literal(": " + unlock.getDescription())
                            .formatted(ChatFormatting.GRAY)), true);
                }
            }
        }
        
        FrogSlimeGamemode.LOGGER.info("Player " + player.getName().getString() + " reached level " + newLevel);
    }
    
    public static int getLevel(ServerPlayer player) {
        PlayerLevelData data = playerLevels.get(player);
        return data != null ? data.getLevel() : 1;
    }
    
    public static double getXP(ServerPlayer player) {
        PlayerLevelData data = playerLevels.get(player);
        return data != null ? data.getXP() : 0;
    }
    
    public static double getXPToNextLevel(ServerPlayer player) {
        PlayerLevelData data = playerLevels.get(player);
        return data != null ? data.getXPToNextLevel() : 100;
    }
    
    public static List<ProgressionUnlock.Unlock> getAvailableUnlocks(ServerPlayer player, int evolutionStage) {
        int level = getLevel(player);
        return ProgressionUnlock.getUnlocksForLevel(level);
    }
}
