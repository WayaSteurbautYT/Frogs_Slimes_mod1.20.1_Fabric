package com.wayacreate.frogslimegamemode.evolution;

import net.minecraft.util.Formatting;

public enum EvolutionStage {
    BASIC(0, "Basic", "Just starting out"),
    ADVANCED(1, "Advanced", "Getting stronger"),
    ELITE(2, "Elite", "Powerful ally"),
    MASTER(3, "Master", "Peak performance"),
    FINAL(4, "FINAL FORM", "Unstoppable power");
    
    private final int level;
    private final String name;
    private final String description;
    
    EvolutionStage(int level, String name, String description) {
        this.level = level;
        this.name = name;
        this.description = description;
    }
    
    public int getLevel() {
        return level;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Formatting getColor() {
        return switch (this) {
            case BASIC -> Formatting.WHITE;
            case ADVANCED -> Formatting.GREEN;
            case ELITE -> Formatting.AQUA;
            case MASTER -> Formatting.GOLD;
            case FINAL -> Formatting.DARK_RED;
        };
    }
    
    public static EvolutionStage fromLevel(int level) {
        for (EvolutionStage stage : values()) {
            if (stage.level == level) {
                return stage;
            }
        }
        return BASIC;
    }
}