package com.wayacreate.frogslimegamemode.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class CollectionsScreen extends Screen {
    // Hypixel Skyblock Style Colors
    private static final int BACKGROUND_COLOR = 0xFF1a1a2e;      // Dark blue-gray background
    private static final int PANEL_COLOR = 0xFF16213e;          // Slightly lighter panel
    private static final int BORDER_COLOR = 0xFF0f3460;         // Blue border
    private static final int ACCENT_COLOR = 0xFFe94560;          // Red accent
    private static final int GOLD_COLOR = 0xFFffd700;            // Gold for headers
    private static final int GREEN_COLOR = 0xFF55aa55;            // Green for unlocked
    private static final int GRAY_COLOR = 0xFF888888;             // Gray for locked
    private static final int WHITE_COLOR = 0xFFFFFFFF;            // White text
    
    private int currentCategory = 0;
    private static final String[] CATEGORIES = {"Mobs", "Items", "Blocks", "Abilities"};
    private static final int[] CATEGORY_ICONS = {0x1F422, 0x1F4E6, 0x1F9F1, 0x2728}; // Unicode emojis
    
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
    
    // Texture identifiers for GUI elements
    private static final Identifier GUI_ICONS = new Identifier("frogslimegamemode", "textures/gui/icons.png");
    
    @Override
    protected void init() {
        super.init();
        
        // Close button at bottom center
        int buttonWidth = 120;
        int buttonHeight = 24;
        int buttonX = (this.width - buttonWidth) / 2;
        int buttonY = this.height - 36;
        
        this.addDrawableChild(
            ButtonWidget.builder(Text.literal("✕ Close").formatted(Formatting.RED), button -> this.close())
                .dimensions(buttonX, buttonY, buttonWidth, buttonHeight)
                .build()
        );
        
        // Category tab buttons - styled as tabs at top
        int tabWidth = 90;
        int tabHeight = 28;
        int startX = (this.width - (tabWidth * 4 + 15 * 3)) / 2;
        int tabY = 45;
        
        for (int i = 0; i < 4; i++) {
            final int catIndex = i;
            int x = startX + i * (tabWidth + 15);
            boolean isActive = currentCategory == i;
            
            this.addDrawableChild(
                ButtonWidget.builder(
                    isActive ? 
                        Text.literal("▶ " + CATEGORIES[i]).formatted(Formatting.YELLOW) :
                        Text.literal(CATEGORIES[i]).formatted(Formatting.GRAY),
                    button -> {
                        currentCategory = catIndex;
                        this.init();
                    })
                .dimensions(x, tabY, tabWidth, tabHeight)
                .build()
            );
        }
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Draw dark background using BACKGROUND_COLOR
        context.fill(0, 0, this.width, this.height, BACKGROUND_COLOR);
        
        // Draw main panel background
        int panelX = 20;
        int panelY = 30;
        int panelWidth = this.width - 40;
        int panelHeight = this.height - 80;
        
        // Main panel with border
        drawRoundedRect(context, panelX, panelY, panelWidth, panelHeight, PANEL_COLOR, BORDER_COLOR, 2);
        
        // Draw accent line at top
        context.fill(panelX + 50, panelY + 2, panelX + panelWidth - 50, panelY + 4, ACCENT_COLOR);
        
        // Title at top with decorative icon from CATEGORY_ICONS
        String icon = new String(Character.toChars(CATEGORY_ICONS[currentCategory]));
        context.drawCenteredTextWithShadow(this.textRenderer, 
            Text.literal(icon + " Collections " + icon).formatted(Formatting.GOLD, Formatting.BOLD), 
            this.width / 2, 14, GOLD_COLOR);
        
        // Render category content in main panel area
        renderCollectionContent(context, panelX + 15, panelY + 85, panelWidth - 30, panelHeight - 100);
        
        // Use the GUI_ICONS field (placeholder for texture icons)
        if (GUI_ICONS != null) {
            // Draw a decorative icon corner using the texture
            drawIcon(context, panelX + panelWidth - 30, panelY + 10, 0, 0);
        }
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    // Helper method to use RenderLayer
    private void drawTextureRect(DrawContext context, int x, int y, int width, int height) {
        // Use RenderLayer for textured drawing (placeholder for future texture use)
        @SuppressWarnings("unused")
        RenderLayer layer = RenderLayer.getGui();
        context.fill(x, y, x + width, y + height, 0x80FFFFFF);
    }
    
    // Helper method to use GUI_ICONS
    private void drawIcon(DrawContext context, int x, int y, int iconU, int iconV) {
        // Placeholder for icon drawing using GUI_ICONS texture
        // Use drawTextureRect for rendering
        drawTextureRect(context, x, y, 16, 16);
    }
    
    private void drawRoundedRect(DrawContext context, int x, int y, int width, int height, int color, int borderColor, int borderWidth) {
        // Fill main area
        context.fill(x + 4, y, x + width - 4, y + height, color);
        context.fill(x, y + 4, x + width, y + height - 4, color);
        context.fill(x + 2, y + 2, x + width - 2, y + height - 2, color);
        
        // Draw border
        context.fill(x + 4, y, x + width - 4, y + borderWidth, borderColor); // Top
        context.fill(x + 4, y + height - borderWidth, x + width - 4, y + height, borderColor); // Bottom
        context.fill(x, y + 4, x + borderWidth, y + height - 4, borderColor); // Left
        context.fill(x + width - borderWidth, y + 4, x + width, y + height - 4, borderColor); // Right
    }
    
    private void renderCollectionContent(DrawContext context, int x, int y, int width, int height) {
        String category = CATEGORIES[currentCategory];
        int collected = collectionProgress.getOrDefault(category, 0);
        int total = collectionMax.getOrDefault(category, 1);
        double progress = (double) collected / total;
        
        // Category header with icon
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(category.toUpperCase()).formatted(Formatting.GOLD, Formatting.BOLD),
            x + 10, y, GOLD_COLOR);
        
        // Progress text
        String progressText = String.format("%d/%d (%.1f%%)", collected, total, progress * 100);
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(progressText).formatted(Formatting.GREEN),
            x + width - this.textRenderer.getWidth(progressText) - 10, y, GREEN_COLOR);
        
        y += 25;
        
        // Styled progress bar
        int barWidth = width - 20;
        int barHeight = 12;
        int barX = x + 10;
        
        // Bar background
        context.fill(barX, y, barX + barWidth, y + barHeight, 0xFF333333);
        // Progress fill with gradient effect
        int fillWidth = (int)(barWidth * progress);
        if (fillWidth > 0) {
            int progressColor = progress >= 1.0 ? 0xFF55FF55 : 0xFF55AA55;
            context.fill(barX + 1, y + 1, barX + fillWidth - 1, y + barHeight - 1, progressColor);
        }
        // Border
        context.fill(barX, y, barX + barWidth, y + 1, BORDER_COLOR);
        context.fill(barX, y + barHeight - 1, barX + barWidth, y + barHeight, BORDER_COLOR);
        context.fill(barX, y, barX + 1, y + barHeight, BORDER_COLOR);
        context.fill(barX + barWidth - 1, y, barX + barWidth, y + barHeight, BORDER_COLOR);
        
        y += 30;
        
        // Items grid
        String[][] items = getCollectionItems(category);
        int itemsPerRow = 2;
        int itemWidth = (width - 30) / itemsPerRow;
        int itemHeight = 22;
        
        for (int i = 0; i < items.length && y < y + height - 100; i++) {
            int row = i / itemsPerRow;
            int col = i % itemsPerRow;
            int itemX = x + 10 + col * itemWidth;
            int itemY = y + row * itemHeight;
            
            String name = items[i][0];
            boolean unlocked = Boolean.parseBoolean(items[i][1]);
            
            // Item background
            int itemBgColor = unlocked ? 0xFF2a3f2a : 0xFF2a2a3a;
            int itemBorderColor = unlocked ? 0xFF55aa55 : 0xFF444444;
            
            context.fill(itemX, itemY, itemX + itemWidth - 5, itemY + 18, itemBgColor);
            context.fill(itemX, itemY, itemX + itemWidth - 5, itemY + 1, itemBorderColor);
            context.fill(itemX, itemY + 17, itemX + itemWidth - 5, itemY + 18, itemBorderColor);
            
            // Status icon
            String icon = unlocked ? "✓" : "✗";
            int iconColor = unlocked ? GREEN_COLOR : GRAY_COLOR;
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(icon).formatted(unlocked ? Formatting.GREEN : Formatting.GRAY),
                itemX + 6, itemY + 5, iconColor);
            
            // Item name
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(name).formatted(Formatting.WHITE),
                itemX + 20, itemY + 5, WHITE_COLOR);
        }
        
        // Rewards section
        y = this.height - 140;
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Rewards:").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD),
            x + 10, y, 0xFFaa55aa);
        
        y += 18;
        String[] rewards = getRewards(category, collected);
        for (String reward : rewards) {
            boolean unlocked = reward.startsWith("✓");
            int color = unlocked ? GREEN_COLOR : GRAY_COLOR;
            
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(reward).formatted(Formatting.WHITE),
                x + 20, y, color);
            y += 14;
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
