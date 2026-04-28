package com.wayacreate.frogslimegamemode.achievements;

import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class Achievement {
    private final String id;
    private final String name;
    private final String description;
    private final ChatFormatting color;
    
    public Achievement(String id, String name, String description, ChatFormatting color) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.color = color;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public ChatFormatting getColor() {
        return color;
    }
    
    public Component getFormattedName() {
        return Component.literal(name).formatted(color, ChatFormatting.BOLD);
    }
    
    public Component getFormattedDescription() {
        return Component.literal(description).formatted(ChatFormatting.GRAY);
    }
}
