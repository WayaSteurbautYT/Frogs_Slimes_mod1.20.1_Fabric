package com.wayacreate.frogslimegamemode.evolution;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public enum MobTransformation {
    FROG("frog", "Frog", Formatting.GREEN),
    SLIME("slime", "Slime", Formatting.GREEN),
    ENDERMAN("enderman", "Ender Frog", Formatting.DARK_PURPLE),
    BLAZE("blaze", "Blaze Frog", Formatting.GOLD),
    WITCH("witch", "Witch Frog", Formatting.LIGHT_PURPLE),
    IRON_GOLEM("iron_golem", "Iron Frog", Formatting.GRAY),
    SNOW_GOLEM("snow_golem", "Frost Frog", Formatting.AQUA),
    VILLAGER("villager", "Merchant Frog", Formatting.YELLOW),
    PIGLIN("piglin", "Piglin Frog", Formatting.GOLD),
    WARDEN("warden", "Warden Frog", Formatting.DARK_BLUE);
    
    private final String id;
    private final String displayName;
    private final Formatting color;
    
    MobTransformation(String id, String displayName, Formatting color) {
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
    
    public Formatting getColor() {
        return color;
    }
    
    public Text getFormattedName() {
        return Text.literal(displayName).formatted(color, Formatting.BOLD);
    }
    
    public static MobTransformation fromId(String id) {
        for (MobTransformation transformation : values()) {
            if (transformation.id.equals(id)) {
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
