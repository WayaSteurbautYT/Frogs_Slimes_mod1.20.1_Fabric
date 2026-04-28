package com.wayacreate.frogslimegamemode.achievements;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import com.wayacreate.frogslimegamemode.network.ModNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AchievementManager {
    private static final Map<String, Achievement> ACHIEVEMENTS = new HashMap<>();
    private static final Map<UUID, Map<String, Boolean>> PLAYER_ACHIEVEMENTS = new HashMap<>();
    
    static {
        register(new Achievement("journey_started", "Journey Started", "Activated the Frog & Slime route", Formatting.GREEN));

        // Early game achievements
        register(new Achievement("first_helper", "First Ally", "Tamed your first frog or slime helper", Formatting.GREEN));
        register(new Achievement("helper_commander", "Helper Commander", "Assigned a role to your first helper", Formatting.GOLD));
        register(new Achievement("first_evolution", "Growing Stronger", "Your helper evolved for the first time", Formatting.AQUA));
        register(new Achievement("mob_eater", "Hungry Helper", "Your helper ate its first mob", Formatting.YELLOW));
        
        // Progression achievements
        register(new Achievement("kill_10_mobs", "Mob Hunter", "Killed 10 mobs with your helpers", Formatting.RED));
        register(new Achievement("kill_50_mobs", "Mob Slayer", "Killed 50 mobs with your helpers", Formatting.DARK_RED));
        register(new Achievement("kill_100_mobs", "Mob Destroyer", "Killed 100 mobs with your helpers", Formatting.DARK_RED));
        register(new Achievement("collect_100_items", "Resource Gatherer", "Collected 100 items", Formatting.GOLD));
        register(new Achievement("collect_500_items", "Hoarding Master", "Collected 500 items", Formatting.GOLD));
        register(new Achievement("reach_nether", "Nether Bound", "Reached the Nether", Formatting.DARK_PURPLE));
        register(new Achievement("find_diamonds", "Diamond Finder", "Found 10 diamonds", Formatting.AQUA));
        register(new Achievement("find_netherite", "Netherite Hunter", "Found netherite", Formatting.DARK_GRAY));
        
        // Evolution achievements
        register(new Achievement("elite_helper", "Elite Force", "Your helper reached Elite stage", Formatting.LIGHT_PURPLE));
        register(new Achievement("master_helper", "Master Ally", "Your helper reached Master stage", Formatting.GOLD));
        register(new Achievement("final_form", "Ultimate Power", "Your helper reached Final Form", Formatting.DARK_RED));
        
        // Combat achievements
        register(new Achievement("teleport_ability", "Ender Powers", "Your helper gained teleportation ability", Formatting.DARK_PURPLE));
        register(new Achievement("fire_ability", "Fire Master", "Your helper gained fire immunity", Formatting.RED));
        register(new Achievement("ice_ability", "Frost Walker", "Your helper gained ice powers", Formatting.AQUA));
        
        // End game achievements
        register(new Achievement("boss_killer", "Boss Slayer", "Defeated the Giant Slime Boss", Formatting.GOLD));
        register(new Achievement("speedrunner", "Speed Demon", "Completed the game in under 1 hour", Formatting.RED));
        register(new Achievement("speedrunner_30min", "Speed God", "Completed the game in under 30 minutes", Formatting.GOLD));
        
        // Manhunt achievements
        register(new Achievement("first_hunt", "First Blood", "Eliminated your first speedrunner as a hunter", Formatting.RED));
        register(new Achievement("survivor", "Survivor", "Survived 5 hunter attacks as a speedrunner", Formatting.GREEN));
        register(new Achievement("team_hunter", "Pack Hunter", "Won a manhunt as part of a hunter team", Formatting.DARK_RED));
        register(new Achievement("lone_wolf", "Lone Wolf", "Won a solo manhunt", Formatting.GOLD));
        
        // Contract achievements
        register(new Achievement("first_contract", "Contract Accepted", "Accepted your first bounty contract", Formatting.YELLOW));
        register(new Achievement("contract_master", "Bounty Hunter", "Completed 10 bounty contracts", Formatting.GOLD));
        register(new Achievement("contract_legend", "Legendary Hunter", "Completed 50 bounty contracts", Formatting.DARK_PURPLE));
        
        // Trading achievements
        register(new Achievement("first_trade", "First Trade", "Completed your first trade", Formatting.GREEN));
        register(new Achievement("merchant", "Merchant", "Completed 25 trades", Formatting.GOLD));
        register(new Achievement("trade_tycoon", "Trade Tycoon", "Completed 100 trades", Formatting.DARK_AQUA));
        
        // Ability achievements
        register(new Achievement("ability_unlock", "Power Unlocked", "Unlocked your first player ability", Formatting.LIGHT_PURPLE));
        register(new Achievement("mob_smith", "Mob Smith", "Forged your first mob ability item", Formatting.RED));
        register(new Achievement("ability_master", "Ability Master", "Unlocked all player abilities", Formatting.GOLD));
        
        // Dimension achievements
        register(new Achievement("end_reached", "End Walker", "Reached the End dimension", Formatting.DARK_PURPLE));
        register(new Achievement("dragon_slayer", "Dragon Slayer", "Defeated the Ender Dragon", Formatting.GOLD));
    }
    
    private static void register(Achievement achievement) {
        ACHIEVEMENTS.put(achievement.getId(), achievement);
    }
    
    public static void init() {
        // Achievements are registered in static block
        FrogSlimeGamemode.LOGGER.info("Achievement system initialized with " + ACHIEVEMENTS.size() + " achievements");
    }
    
    public static Achievement getAchievement(String id) {
        return ACHIEVEMENTS.get(id);
    }
    
    public static boolean hasAchievement(ServerPlayerEntity player, String achievementId) {
        Map<String, Boolean> playerAchievements = PLAYER_ACHIEVEMENTS.get(player.getUuid());
        return playerAchievements != null && playerAchievements.getOrDefault(achievementId, false);
    }
    
    public static void unlockAchievement(ServerPlayerEntity player, String achievementId) {
        if (hasAchievement(player, achievementId)) {
            return;
        }
        
        Achievement achievement = getAchievement(achievementId);
        if (achievement == null) {
            return;
        }
        
        // Mark as unlocked
        PLAYER_ACHIEVEMENTS.computeIfAbsent(player.getUuid(), k -> new HashMap<>()).put(achievementId, true);
        
        // Send toast notification to client via networking
        ModNetworking.sendAchievementToast(player, achievement);
        
        // Send chat message
        player.sendMessage(Text.literal("Achievement Unlocked: ")
            .formatted(Formatting.GOLD, Formatting.BOLD)
            .append(achievement.getFormattedName()), false);
        
        FrogSlimeGamemode.LOGGER.info("Player " + player.getName().getString() + " unlocked achievement: " + achievementId);
    }
    
    public static Map<String, Boolean> getPlayerAchievements(UUID playerUuid) {
        return PLAYER_ACHIEVEMENTS.getOrDefault(playerUuid, new HashMap<>());
    }
    
    public static void clearPlayerData(UUID playerUuid) {
        PLAYER_ACHIEVEMENTS.remove(playerUuid);
    }
}
