package com.wayacreate.frogslimegamemode.gamemode;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import com.wayacreate.frogslimegamemode.progression.ProgressionUnlock;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerLevel {
    private static final Map<ServerPlayerEntity, PlayerLevelData> playerLevels = new HashMap<>();
    
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
    
    public static void addXP(ServerPlayerEntity player, double amount) {
        PlayerLevelData data = playerLevels.computeIfAbsent(player, k -> new PlayerLevelData());
        int oldLevel = data.getLevel();
        data.addXP(amount);
        
        if (data.getLevel() > oldLevel) {
            onLevelUp(player, data.getLevel());
        }
    }
    
    private static void onLevelUp(ServerPlayerEntity player, int newLevel) {
        player.sendMessage(Text.literal("Level Up! You are now level " + newLevel)
            .formatted(Formatting.GOLD, Formatting.BOLD), true);
        
        // Check for new unlocks
        List<ProgressionUnlock.Unlock> newUnlocks = ProgressionUnlock.getUnlocksForLevel(newLevel);
        if (!newUnlocks.isEmpty()) {
            player.sendMessage(Text.literal("New unlocks available!")
                .formatted(Formatting.AQUA, Formatting.BOLD), true);
            
            for (ProgressionUnlock.Unlock unlock : newUnlocks) {
                if (unlock.getRequiredLevel() == newLevel) {
                    player.sendMessage(Text.literal("• ")
                        .append(unlock.getFormattedName())
                        .append(Text.literal(": " + unlock.getDescription())
                            .formatted(Formatting.GRAY)), true);
                }
            }
        }
        
        FrogSlimeGamemode.LOGGER.info("Player " + player.getName().getString() + " reached level " + newLevel);
    }
    
    public static int getLevel(ServerPlayerEntity player) {
        PlayerLevelData data = playerLevels.get(player);
        return data != null ? data.getLevel() : 1;
    }
    
    public static double getXP(ServerPlayerEntity player) {
        PlayerLevelData data = playerLevels.get(player);
        return data != null ? data.getXP() : 0;
    }
    
    public static double getXPToNextLevel(ServerPlayerEntity player) {
        PlayerLevelData data = playerLevels.get(player);
        return data != null ? data.getXPToNextLevel() : 100;
    }
    
    public static List<ProgressionUnlock.Unlock> getAvailableUnlocks(ServerPlayerEntity player, int evolutionStage) {
        int level = getLevel(player);
        return ProgressionUnlock.getUnlocksForLevel(level);
    }
}
