package com.wayacreate.frogslimegamemode.achievements;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Achievement {
    private final String id;
    private final String name;
    private final String description;
    private final Formatting color;
    
    public Achievement(String id, String name, String description, Formatting color) {
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
    
    public Formatting getColor() {
        return color;
    }
    
    public Text getFormattedName() {
        return Text.literal(name).formatted(color, Formatting.BOLD);
    }
    
    public Text getFormattedDescription() {
        return Text.literal(description).formatted(Formatting.GRAY);
    }
}
