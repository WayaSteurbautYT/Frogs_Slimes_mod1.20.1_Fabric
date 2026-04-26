package com.wayacreate.frogslimegamemode.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;

public class CollectionsScreen extends Screen {
    private static final int TITLE_COLOR = 0xFFFFFF;
    private static final int UNLOCKED_COLOR = 0x55FF55;
    private static final int LOCKED_COLOR = 0x555555;
    
    private int currentCategory = 0; // 0 = Mobs, 1 = Items, 2 = Blocks, 3 = Abilities
    private static final String[] CATEGORIES = {"Mobs", "Items", "Blocks", "Abilities"};
    
    // Collection progress (placeholder values)
    private static final Map<String, Integer> collectionProgress = new HashMap<>();
    private static final Map<String, Integer> collectionMax = new HashMap<>();
    
    static {
        collectionProgress.put("Mobs", 15);
        collectionMax.put("Mobs", 20);
        collectionProgress.put("Items", 45);
        collectionMax.put("Items", 60);
        collectionProgress.put("Blocks", 30);
        collectionMax.put("Blocks", 50);
        collectionProgress.put("Abilities", 8);
        collectionMax.put("Abilities", 10);
    }
    
    public CollectionsScreen() {
        super(Text.literal("Collections"));
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
        renderCollectionCategory(context);
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void renderCollectionCategory(DrawContext context) {
        int startX = 120;
        int startY = 50;
        
        String category = CATEGORIES[currentCategory];
        int collected = collectionProgress.getOrDefault(category, 0);
        int total = collectionMax.getOrDefault(category, 1);
        double progress = (double) collected / total;
        
        // Category header
        context.drawTextWithShadow(this.textRenderer, 
            Text.literal("=== " + category + " ===").formatted(Formatting.GOLD, Formatting.BOLD), 
            startX, startY, TITLE_COLOR);
        startY += 30;
        
        // Overall progress
        context.drawTextWithShadow(this.textRenderer, 
            Text.literal("Collected: " + collected + "/" + total).formatted(Formatting.AQUA, Formatting.BOLD), 
            startX, startY, 0x00FFFF);
        startY += 25;
        
        // Progress bar
        int barWidth = 200;
        int barHeight = 15;
        int barX = startX;
        int barY = startY;
        
        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF333333);
        context.fill(barX, barY, barX + (int)(barWidth * progress), barY + barHeight, UNLOCKED_COLOR);
        
        context.drawTextWithShadow(this.textRenderer, 
            Text.literal(String.format("%.1f%%", progress * 100)).formatted(Formatting.WHITE), 
            barX + barWidth / 2 - 15, barY + 2, 0xFFFFFF);
        
        startY += 30;
        
        // Collection items
        context.drawTextWithShadow(this.textRenderer, 
            Text.literal("Collection:").formatted(Formatting.YELLOW, Formatting.BOLD), 
            startX, startY, 0xFFFF00);
        startY += 20;
        
        String[][] items = getCollectionItems(category);
        for (String[] item : items) {
            String name = item[0];
            boolean unlocked = Boolean.parseBoolean(item[1]);
            int color = unlocked ? UNLOCKED_COLOR : LOCKED_COLOR;
            String status = unlocked ? "[✓]" : "[ ]";
            
            context.drawTextWithShadow(this.textRenderer, 
                Text.literal(status + " " + name).formatted(Formatting.WHITE), 
                startX, startY, color);
            startY += 15;
        }
        
        // Rewards section
        startY += 10;
        context.drawTextWithShadow(this.textRenderer, 
            Text.literal("Rewards:").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), 
            startX, startY, 0xAA00AA);
        startY += 20;
        
        String[] rewards = getRewards(category, collected);
        for (String reward : rewards) {
            boolean unlocked = reward.startsWith("✓");
            int color = unlocked ? UNLOCKED_COLOR : LOCKED_COLOR;
            
            context.drawTextWithShadow(this.textRenderer, 
                Text.literal(reward).formatted(Formatting.GRAY), 
                startX, startY, color);
            startY += 15;
        }
    }
    
    private String[][] getCollectionItems(String category) {
        return switch (category) {
            case "Mobs" -> new String[][] {
                {"Zombie", "true"}, {"Skeleton", "true"}, {"Creeper", "true"},
                {"Spider", "true"}, {"Enderman", "true"}, {"Witch", "true"},
                {"Blaze", "true"}, {"Ghast", "true"}, {"Piglin", "true"},
                {"Warden", "true"}, {"Pig", "true"}, {"Cow", "true"},
                {"Chicken", "true"}, {"Sheep", "true"}, {"Rabbit", "true"},
                {"Iron Golem", "false"}, {"Snow Golem", "false"}, {"Villager", "false"},
                {"Dream Essence", "false"}, {"Technoblade Essence", "false"}
            };
            case "Items" -> new String[][] {
                {"Rotten Flesh", "true"}, {"Bone", "true"}, {"Gunpowder", "true"},
                {"String", "true"}, {"Ender Pearl", "true"}, {"Blaze Rod", "true"},
                {"Ghast Tear", "true"}, {"Gold Ingot", "true"}, {"Echo Shard", "true"},
                {"Porkchop", "true"}, {"Beef", "true"}, {"Chicken", "true"},
                {"Wool", "true"}, {"Rabbit Hide", "true"}, {"Iron Ingot", "true"},
                {"Emerald", "true"}, {"Netherite Ingot", "false"}, {"Golden Apple", "false"},
                {"Diamond", "false"}, {"Redstone", "false"}
            };
            case "Blocks" -> new String[][] {
                {"Dirt", "true"}, {"Grass Block", "true"}, {"Stone", "true"},
                {"Cobblestone", "true"}, {"Wood", "true"}, {"Sand", "true"},
                {"Gravel", "true"}, {"Coal Ore", "true"}, {"Iron Ore", "true"},
                {"Gold Ore", "true"}, {"Diamond Ore", "true"}, {"Redstone Ore", "true"},
                {"Lapis Ore", "true"}, {"Obsidian", "true"}, {"Bedrock", "false"},
                {"Netherrack", "false"}, {"End Stone", "false"}, {"Deepslate", "false"},
                {"Ancient Debris", "false"}, {"Sculk", "false"}
            };
            case "Abilities" -> new String[][] {
                {"Undead Strength", "true"}, {"Sniper Precision", "true"},
                {"Explosive Power", "true"}, {"Wall Climber", "true"},
                {"Teleportation", "true"}, {"Potion Mastery", "true"},
                {"Fire Immunity", "true"}, {"Fireball Fury", "true"},
                {"Gold Greed", "false"}, {"Sonic Power", "false"}
            };
            default -> new String[][] {};
        };
    }
    
    private String[] getRewards(String category, int collected) {
        int[] milestones = {5, 10, 15, 20};
        String[] rewards = new String[milestones.length];
        
        for (int i = 0; i < milestones.length; i++) {
            int milestone = milestones[i];
            boolean unlocked = collected >= milestone;
            String prefix = unlocked ? "✓ " : "  ";
            
            rewards[i] = switch (category) {
                case "Mobs" -> prefix + "Milestone " + milestone + ": " + getMobReward(milestone);
                case "Items" -> prefix + "Milestone " + milestone + ": " + getItemReward(milestone);
                case "Blocks" -> prefix + "Milestone " + milestone + ": " + getBlockReward(milestone);
                case "Abilities" -> prefix + "Milestone " + milestone + ": " + getAbilityReward(milestone);
                default -> prefix + "Milestone " + milestone;
            };
        }
        
        return rewards;
    }
    
    private String getMobReward(int milestone) {
        return switch (milestone) {
            case 5 -> "+5% Mob Damage";
            case 10 -> "+10% Mob Damage, +5% XP";
            case 15 -> "+15% Mob Damage, +10% XP";
            case 20 -> "+20% Mob Damage, +15% XP, Special Title";
            default -> "Unknown";
        };
    }
    
    private String getItemReward(int milestone) {
        return switch (milestone) {
            case 5 -> "+5% Item Rarity";
            case 10 -> "+10% Item Rarity, +5% Drop Chance";
            case 15 -> "+15% Item Rarity, +10% Drop Chance";
            case 20 -> "+20% Item Rarity, +15% Drop Chance, Access to Shop";
            default -> "Unknown";
        };
    }
    
    private String getBlockReward(int milestone) {
        return switch (milestone) {
            case 5 -> "+5% Mining Speed";
            case 10 -> "+10% Mining Speed, +5% Fortune";
            case 15 -> "+15% Mining Speed, +10% Fortune";
            case 20 -> "+20% Mining Speed, +15% Fortune, Access to Deep Mining";
            default -> "Unknown";
        };
    }
    
    private String getAbilityReward(int milestone) {
        return switch (milestone) {
            case 5 -> "+5% Ability Power";
            case 10 -> "+10% Ability Power, -5% Cooldown";
            case 15 -> "+15% Ability Power, -10% Cooldown";
            case 20 -> "+20% Ability Power, -15% Cooldown, Ultimate Ability";
            default -> "Unknown";
        };
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
}
