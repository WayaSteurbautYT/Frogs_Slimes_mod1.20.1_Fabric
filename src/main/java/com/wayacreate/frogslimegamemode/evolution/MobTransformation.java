package com.wayacreate.frogslimegamemode.evolution;

import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.Objects;

public enum MobTransformation {
    FROG("frog", "Frog", ChatFormatting.GREEN),
    SLIME("slime", "Slime", ChatFormatting.GREEN),
    ENDERMAN("enderman", "Ender Frog", ChatFormatting.DARK_PURPLE),
    BLAZE("blaze", "Blaze Frog", ChatFormatting.GOLD),
    WITCH("witch", "Witch Frog", ChatFormatting.LIGHT_PURPLE),
    IRON_GOLEM("iron_golem", "Iron Frog", ChatFormatting.GRAY),
    SNOW_GOLEM("snow_golem", "Frost Frog", ChatFormatting.AQUA),
    VILLAGER("villager", "Merchant Frog", ChatFormatting.YELLOW),
    PIGLIN("piglin", "Piglin Frog", ChatFormatting.GOLD),
    WARDEN("warden", "Warden Frog", ChatFormatting.DARK_BLUE);
    
    private final String id;
    private final String displayName;
    private final ChatFormatting color;
    
    MobTransformation(String id, String displayName, ChatFormatting color) {
        this.id = id;
        this.displayName = displayName;
        this.color = color;
    }
    
    public String getId() {
        return id;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public ChatFormatting getColor() {
        return color;
    }
    
    public Component getFormattedName() {
        return Component.literal(displayName).formatted(color, ChatFormatting.BOLD);
    }
    
    public static MobTransformation fromId(String id) {
        for (MobTransformation transformation : values()) {
            if (Objects.equals(transformation.id, id)) {
                return transformation;
            }
        }
        return FROG;
    }
    
    public static MobTransformation getNextTransformation(MobTransformation current, int evolutionStage) {
        return switch (evolutionStage) {
            case 0 -> FROG;
            case 1 -> SLIME;
            case 2 -> ENDERMAN;
            case 3 -> switch (current) {
                case ENDERMAN -> BLAZE;
                case BLAZE -> WITCH;
                case WITCH -> IRON_GOLEM;
                case IRON_GOLEM -> SNOW_GOLEM;
                case SNOW_GOLEM -> VILLAGER;
                case VILLAGER -> PIGLIN;
                case PIGLIN -> WARDEN;
                default -> WARDEN;
            };
            default -> WARDEN;
        };
    }
}
