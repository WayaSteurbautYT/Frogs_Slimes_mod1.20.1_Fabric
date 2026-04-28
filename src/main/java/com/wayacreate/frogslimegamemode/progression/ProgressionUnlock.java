package com.wayacreate.frogslimegamemode.progression;

import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgressionUnlock {
    public enum UnlockType {
        ITEM,
        POTION,
        RECIPE,
        JOB_CLASS,
        ABILITY
    }
    
    public static class Unlock {
        private final String id;
        private final String name;
        private final String description;
        private final UnlockType type;
        private final int requiredLevel;
        private final int requiredEvolutionStage;
        private final ChatFormatting color;
        
        public Unlock(String id, String name, String description, UnlockType type, int requiredLevel, int requiredEvolutionStage, ChatFormatting color) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.type = type;
            this.requiredLevel = requiredLevel;
            this.requiredEvolutionStage = requiredEvolutionStage;
            this.color = color;
        }
        
        public String getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public UnlockType getType() { return type; }
        public int getRequiredLevel() { return requiredLevel; }
        public int getRequiredEvolutionStage() { return requiredEvolutionStage; }
        public ChatFormatting getColor() { return color; }
        
        public Component getFormattedName() {
            return Component.literal(name).formatted(color, ChatFormatting.BOLD);
        }
    }
    
    private static final Map<String, Unlock> UNLOCKS = new HashMap<>();
    private static final Map<Integer, List<String>> UNLOCKS_BY_LEVEL = new HashMap<>();
    private static final Map<Integer, List<String>> UNLOCKS_BY_EVOLUTION = new HashMap<>();
    
    static {
        // Stage 0 Unlocks (Level 1-9)
        register(new Unlock("basic_compass", "Manhunt Compass", "Track other players", UnlockType.ITEM, 1, 0, ChatFormatting.RED));
        register(new Unlock("role_stick", "Role Assignment Stick", "Assign roles to helpers", UnlockType.ITEM, 1, 0, ChatFormatting.GOLD));
        register(new Unlock("slime_food", "Slime Food", "Feed your slime helpers", UnlockType.ITEM, 3, 0, ChatFormatting.GREEN));
        register(new Unlock("frog_food", "Frog Food", "Feed your frog helpers", UnlockType.ITEM, 3, 0, ChatFormatting.GREEN));
        register(new Unlock("miner_class", "Miner Class", "Helper can mine ores", UnlockType.JOB_CLASS, 5, 0, ChatFormatting.GRAY));
        register(new Unlock("lumberjack_class", "Lumberjack Class", "Helper can chop trees", UnlockType.JOB_CLASS, 5, 0, ChatFormatting.DARK_GREEN));
        
        // Stage 1 Unlocks (Level 10-24)
        register(new Unlock("evolution_stone", "Evolution Stone", "Evolve your helpers", UnlockType.ITEM, 10, 1, ChatFormatting.AQUA));
        register(new Unlock("combat_class", "Combat Specialist", "Enhanced combat helper", UnlockType.JOB_CLASS, 10, 1, ChatFormatting.RED));
        register(new Unlock("speed_potion", "Speed Potion", "Temporary speed boost", UnlockType.POTION, 12, 1, ChatFormatting.YELLOW));
        register(new Unlock("strength_potion", "Strength Potion", "Temporary damage boost", UnlockType.POTION, 15, 1, ChatFormatting.RED));
        register(new Unlock("mob_essence_zombie", "Zombie Essence", "Undead strength ability", UnlockType.ABILITY, 15, 1, ChatFormatting.DARK_GRAY));
        register(new Unlock("mob_essence_spider", "Spider Essence", "Wall climbing ability", UnlockType.ABILITY, 18, 1, ChatFormatting.DARK_GRAY));
        
        // Stage 2 Unlocks (Level 25-49)
        register(new Unlock("final_evolution_crystal", "Final Evolution Crystal", "Ultimate helper evolution", UnlockType.ITEM, 25, 2, ChatFormatting.LIGHT_PURPLE));
        register(new Unlock("regeneration_potion", "Regeneration Potion", "Heal over time", UnlockType.POTION, 28, 2, ChatFormatting.LIGHT_PURPLE));
        register(new Unlock("resistance_potion", "Resistance Potion", "Damage reduction", UnlockType.POTION, 30, 2, ChatFormatting.BLUE));
        register(new Unlock("mob_essence_enderman", "Enderman Essence", "Teleportation ability", UnlockType.ABILITY, 30, 2, ChatFormatting.DARK_PURPLE));
        register(new Unlock("mob_essence_blaze", "Blaze Essence", "Fire immunity ability", UnlockType.ABILITY, 35, 2, ChatFormatting.GOLD));
        register(new Unlock("mob_essence_warden", "Warden Essence", "Sonic power ability", UnlockType.ABILITY, 40, 2, ChatFormatting.DARK_BLUE));
        register(new Unlock("elite_class", "Elite Class", "Advanced helper role", UnlockType.JOB_CLASS, 45, 2, ChatFormatting.AQUA));
        
        // Stage 3 Unlocks (Level 50+)
        register(new Unlock("master_class", "Master Class", "Ultimate helper role", UnlockType.JOB_CLASS, 50, 3, ChatFormatting.GOLD));
        register(new Unlock("nether_star_recipe", "Nether Star Crafting", "Craft ability drops", UnlockType.RECIPE, 55, 3, ChatFormatting.YELLOW));
        register(new Unlock("mob_essence_dream", "Dream Essence", "Speed demon ability", UnlockType.ABILITY, 60, 3, ChatFormatting.AQUA));
        register(new Unlock("mob_essence_techno", "Technoblade Essence", "Piglin king ability", UnlockType.ABILITY, 70, 3, ChatFormatting.DARK_RED));
        register(new Unlock("ultimate_potion", "Ultimate Potion", "All effects combined", UnlockType.POTION, 80, 3, ChatFormatting.GOLD));
        register(new Unlock("boss_access", "Boss Access", "Enter the final boss arena", UnlockType.ITEM, 90, 3, ChatFormatting.DARK_RED));
    }
    
    private static void register(Unlock unlock) {
        UNLOCKS.put(unlock.getId(), unlock);
        
        UNLOCKS_BY_LEVEL.computeIfAbsent(unlock.getRequiredLevel(), k -> new ArrayList<>()).add(unlock.getId());
        UNLOCKS_BY_EVOLUTION.computeIfAbsent(unlock.getRequiredEvolutionStage(), k -> new ArrayList<>()).add(unlock.getId());
    }
    
    public static Unlock getUnlock(String id) {
        return UNLOCKS.get(id);
    }
    
    public static List<Unlock> getUnlocksForLevel(int level) {
        List<Unlock> unlocks = new ArrayList<>();
        for (int i = 1; i <= level; i++) {
            List<String> unlockIds = UNLOCKS_BY_LEVEL.get(i);
            if (unlockIds != null) {
                for (String id : unlockIds) {
                    unlocks.add(UNLOCKS.get(id));
                }
            }
        }
        return unlocks;
    }
    
    public static List<Unlock> getUnlocksForEvolutionStage(int stage) {
        List<Unlock> unlocks = new ArrayList<>();
        for (int i = 0; i <= stage; i++) {
            List<String> unlockIds = UNLOCKS_BY_EVOLUTION.get(i);
            if (unlockIds != null) {
                for (String id : unlockIds) {
                    unlocks.add(UNLOCKS.get(id));
                }
            }
        }
        return unlocks;
    }
    
    public static List<Unlock> getUnlocksForType(UnlockType type) {
        List<Unlock> unlocks = new ArrayList<>();
        for (Unlock unlock : UNLOCKS.values()) {
            if (unlock.getType() == type) {
                unlocks.add(unlock);
            }
        }
        return unlocks;
    }
    
    public static boolean isUnlocked(String unlockId, int playerLevel, int evolutionStage) {
        Unlock unlock = UNLOCKS.get(unlockId);
        if (unlock == null) return false;
        return playerLevel >= unlock.getRequiredLevel() && evolutionStage >= unlock.getRequiredEvolutionStage();
    }
    
    public static Map<String, Unlock> getAllUnlocks() {
        return new HashMap<>(UNLOCKS);
    }
}
