package com.wayacreate.frogslimegamemode.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SkillsScreen extends Screen {
    private static final int TITLE_COLOR = 0xFFFFFF;
    private static final int TEXT_COLOR = 0xE0E0E0;
    private static final int PROGRESS_COLOR = 0x55FF55;
    private static final int LOCKED_COLOR = 0x555555;
    
    private int currentCategory = 0; // 0 = Combat, 1 = Farming, 2 = Mining, 3 = Foraging
    private static final String[] CATEGORIES = {"Combat", "Farming", "Mining", "Foraging"};
    
    // Skill levels (1-60 like Hypixel)
    private static final int[] skillLevels = {15, 8, 12, 5};
    private static final double[] skillXP = {4500, 1200, 2800, 300};
    
    public SkillsScreen() {
        super(Text.literal("Skills"));
    }
    
    @Override
    protected void init() {
        int buttonWidth = 100;
        int buttonHeight = 20;
        int buttonX = (this.width - buttonWidth) / 2;
        int buttonY = this.height - 28;
        
        this.addDrawableChild(
            ButtonWidget.builder(Text.literal("Close"), button -> this.close())
                .dimensions(buttonX, buttonY, buttonWidth, buttonHeight)
                .build()
        );
        
        // Category buttons
        int catButtonWidth = 80;
        int catButtonX = 20;
        int catButtonY = 50;
        
        for (int i = 0; i < 4; i++) {
            final int catIndex = i;
            this.addDrawableChild(
                ButtonWidget.builder(Text.literal(CATEGORIES[i]), button -> {
                    currentCategory = catIndex;
                })
                .dimensions(catButtonX, catButtonY + (i * 25), catButtonWidth, buttonHeight)
                .build()
            );
        }
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, TITLE_COLOR);
        
        // Draw current category content
        renderSkillCategory(context);
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void renderSkillCategory(DrawContext context) {
        int startX = 120;
        int startY = 50;
        
        String category = CATEGORIES[currentCategory];
        int level = skillLevels[currentCategory];
        double currentXP = skillXP[currentCategory];
        double maxXP = getXPForLevel(level + 1);
        double progress = currentXP / maxXP;
        
        // Category header
        context.drawTextWithShadow(this.textRenderer, 
            Text.literal("=== " + category + " ===").formatted(Formatting.GOLD, Formatting.BOLD), 
            startX, startY, TITLE_COLOR);
        startY += 30;
        
        // Level display
        context.drawTextWithShadow(this.textRenderer, 
            Text.literal("Level: " + level + "/60").formatted(Formatting.AQUA, Formatting.BOLD), 
            startX, startY, 0x00FFFF);
        startY += 25;
        
        // XP bar
        int barWidth = 200;
        int barHeight = 15;
        int barX = startX;
        int barY = startY;
        
        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF333333);
        context.fill(barX, barY, barX + (int)(barWidth * progress), barY + barHeight, PROGRESS_COLOR);
        
        context.drawTextWithShadow(this.textRenderer, 
            Text.literal(String.format("%.1f%%", progress * 100)).formatted(Formatting.WHITE), 
            barX + barWidth / 2 - 15, barY + 2, 0xFFFFFF);
        
        startY += 30;
        
        // XP display
        context.drawTextWithShadow(this.textRenderer, 
            Text.literal("XP: " + formatNumber(currentXP) + " / " + formatNumber(maxXP)).formatted(Formatting.GRAY), 
            startX, startY, TEXT_COLOR);
        startY += 25;
        
        // Skill bonuses
        context.drawTextWithShadow(this.textRenderer, 
            Text.literal("Bonuses:").formatted(Formatting.YELLOW, Formatting.BOLD), 
            startX, startY, 0xFFFF00);
        startY += 20;
        
        String[] bonuses = getSkillBonuses(category, level);
        for (String bonus : bonuses) {
            context.drawTextWithShadow(this.textRenderer, 
                Text.literal("• " + bonus).formatted(Formatting.GRAY), 
                startX, startY, TEXT_COLOR);
            startY += 15;
        }
        
        // Milestones
        startY += 10;
        context.drawTextWithShadow(this.textRenderer, 
            Text.literal("Milestones:").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), 
            startX, startY, 0xAA00AA);
        startY += 20;
        
        int[] milestones = {10, 20, 30, 40, 50, 60};
        for (int milestone : milestones) {
            boolean unlocked = level >= milestone;
            int color = unlocked ? PROGRESS_COLOR : LOCKED_COLOR;
            String status = unlocked ? "[✓]" : "[ ]";
            
            context.drawTextWithShadow(this.textRenderer, 
                Text.literal(status + " Level " + milestone).formatted(Formatting.WHITE), 
                startX, startY, color);
            startY += 15;
        }
    }
    
    private String[] getSkillBonuses(String category, int level) {
        return switch (category) {
            case "Combat" -> new String[] {
                "+" + (level * 0.5) + "% Damage",
                "+" + (level * 0.3) + "% Crit Chance",
                "+" + (level * 0.2) + "% Crit Damage"
            };
            case "Farming" -> new String[] {
                "+" + (level * 0.5) + " Crop Yield",
                "+" + (level * 0.3) + " Farming Fortune",
                "-" + (level * 0.1) + " Growth Time"
            };
            case "Mining" -> new String[] {
                "+" + (level * 0.5) + " Mining Speed",
                "+" + (level * 0.3) + " Mining Fortune",
                "+" + (level * 0.2) + " Ore Drop Rate"
            };
            case "Foraging" -> new String[] {
                "+" + (level * 0.5) + " Wood Drop Rate",
                "+" + (level * 0.3) + " Foraging Fortune",
                "+" + (level * 0.2) + " Log Drop Multiplier"
            };
            default -> new String[] {};
        };
    }
    
    private double getXPForLevel(int level) {
        // Hypixel-style XP curve
        if (level <= 1) return 50;
        if (level <= 10) return level * 100;
        if (level <= 20) return level * 200;
        if (level <= 30) return level * 400;
        if (level <= 40) return level * 800;
        if (level <= 50) return level * 1600;
        return level * 3200;
    }
    
    private String formatNumber(double number) {
        if (number >= 1000000) {
            return String.format("%.1fM", number / 1000000);
        } else if (number >= 1000) {
            return String.format("%.1fK", number / 1000);
        }
        return String.format("%.0f", number);
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
}
