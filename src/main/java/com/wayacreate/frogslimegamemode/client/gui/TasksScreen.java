package com.wayacreate.frogslimegamemode.client.gui;

import com.wayacreate.frogslimegamemode.tasks.TaskType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TasksScreen extends Screen {
    private static final int TITLE_COLOR = 0xFFFFFF;
    private static final int TEXT_COLOR = 0xE0E0E0;
    private static final int COMPLETED_COLOR = 0x55FF55;
    
    private int currentTab = 0; // 0 = Tasks, 1 = Recipes, 2 = Achievements
    private static final String[] TAB_NAMES = {"Tasks", "Recipes", "Achievements"};

    public TasksScreen() {
        super(Text.literal("Progression"));
    }

    @Override
    protected void init() {
        int buttonWidth = 120;
        int buttonHeight = 20;
        int x = (this.width - buttonWidth) / 2;
        int y = this.height - 28;

        this.addDrawableChild(
            ButtonWidget.builder(Text.literal("Close"), button -> this.close())
                .dimensions(x, y, buttonWidth, buttonHeight)
                .build()
        );
        
        // Tab buttons
        int tabWidth = 100;
        int tabX = (this.width - (tabWidth * 3)) / 2;
        for (int i = 0; i < 3; i++) {
            final int tabIndex = i;
            this.addDrawableChild(
                ButtonWidget.builder(Text.literal(TAB_NAMES[i]), button -> {
                    currentTab = tabIndex;
                })
                .dimensions(tabX + (i * (tabWidth + 5)), 30, tabWidth, 20)
                .build()
            );
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, TITLE_COLOR);
        
        // Draw current tab content
        switch (currentTab) {
            case 0 -> renderTasksTab(context);
            case 1 -> renderRecipesTab(context);
            case 2 -> renderAchievementsTab(context);
        }

        super.render(context, mouseX, mouseY, delta);
    }
    
    private void renderTasksTab(DrawContext context) {
        int startY = 60;
        int lineHeight = 16;
        int index = 0;
        
        context.drawTextWithShadow(this.textRenderer, Text.literal("=== TASKS ===").formatted(Formatting.GOLD), 20, startY, TITLE_COLOR);
        startY += 25;
        
        for (TaskType task : TaskType.values()) {
            int progress = getTaskProgress(task);
            int required = task.getRequiredAmount();
            boolean complete = progress >= required;
            
            String taskText = task.name().replace("_", " ");
            String progressText = progress + "/" + required;
            
            int color = complete ? COMPLETED_COLOR : TEXT_COLOR;
            String status = complete ? "[✓]" : "[ ]";
            
            context.drawTextWithShadow(this.textRenderer, 
                Text.literal(status + " " + taskText + ": " + progressText), 
                20, startY + (index * lineHeight), color);
            index++;
        }
        
        // Progress bar
        int totalProgress = getTotalTaskProgress();
        int totalRequired = getTotalTaskRequired();
        float percent = totalRequired > 0 ? (float) totalProgress / totalRequired : 0;
        
        int barWidth = 200;
        int barHeight = 10;
        int barX = (this.width - barWidth) / 2;
        int barY = startY + (index * lineHeight) + 20;
        
        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF333333);
        context.fill(barX, barY, barX + (int)(barWidth * percent), barY + barHeight, 0xFF55FF55);
        
        context.drawCenteredTextWithShadow(this.textRenderer, 
            Text.literal("Total Progress: " + (int)(percent * 100) + "%").formatted(Formatting.GOLD),
            this.width / 2, barY + 15, TITLE_COLOR);
    }
    
    private void renderRecipesTab(DrawContext context) {
        int startY = 60;
        int lineHeight = 20;
        int index = 0;
        
        context.drawTextWithShadow(this.textRenderer, Text.literal("=== CRAFTING RECIPES ===").formatted(Formatting.GOLD), 20, startY, TITLE_COLOR);
        startY += 25;
        
        String[][] recipes = {
            {"Manhunt Compass", "Compass + Redstone", "Basic"},
            {"Role Assignment Stick", "Stick + Paper", "Basic"},
            {"Evolution Stone", "Slime Ball + Diamond", "Advanced"},
            {"Slime Food", "Slime Ball + Wheat", "Basic"},
            {"Frog Food", "Slime Ball + Spider Eye", "Basic"},
            {"Ability Essence", "Mob Drop + Nether Star", "Elite"},
            {"Final Evolution Crystal", "Evolution Stone x4 + Dragon Egg", "Master"}
        };
        
        for (String[] recipe : recipes) {
            String name = recipe[0];
            String ingredients = recipe[1];
            String tier = recipe[2];
            
            int tierColor = switch (tier) {
                case "Basic" -> 0xFFFFFF;
                case "Advanced" -> 0x55FFFF;
                case "Elite" -> 0xAA00AA;
                case "Master" -> 0xFFAA00;
                default -> 0xFFFFFF;
            };
            
            context.drawTextWithShadow(this.textRenderer, 
                Text.literal("• " + name).formatted(Formatting.BOLD), 
                20, startY + (index * lineHeight), tierColor);
            context.drawTextWithShadow(this.textRenderer, 
                Text.literal("  " + ingredients).formatted(Formatting.GRAY), 
                20, startY + (index * lineHeight) + 10, TEXT_COLOR);
            index += 2;
        }
    }
    
    private void renderAchievementsTab(DrawContext context) {
        int startY = 60;
        int lineHeight = 18;
        int index = 0;
        
        context.drawTextWithShadow(this.textRenderer, Text.literal("=== ACHIEVEMENTS ===").formatted(Formatting.GOLD), 20, startY, TITLE_COLOR);
        startY += 25;
        
        String[][] achievements = {
            {"First Ally", "Tame your first helper", "Early Game"},
            {"Growing Stronger", "Helper evolves first time", "Early Game"},
            {"Mob Hunter", "Kill 10 mobs with helpers", "Progression"},
            {"Elite Force", "Helper reaches Elite stage", "Progression"},
            {"Master Ally", "Helper reaches Master stage", "Late Game"},
            {"Ultimate Power", "Helper reaches Final Form", "End Game"},
            {"Boss Slayer", "Defeat Giant Slime Boss", "End Game"}
        };
        
        for (String[] achievement : achievements) {
            String name = achievement[0];
            String description = achievement[1];
            String category = achievement[2];
            
            context.drawTextWithShadow(this.textRenderer, 
                Text.literal("★ " + name).formatted(Formatting.YELLOW, Formatting.BOLD), 
                20, startY + (index * lineHeight), 0xFFAA00);
            context.drawTextWithShadow(this.textRenderer, 
                Text.literal("  " + description).formatted(Formatting.GRAY), 
                20, startY + (index * lineHeight) + 10, TEXT_COLOR);
            context.drawTextWithShadow(this.textRenderer, 
                Text.literal("  [" + category + "]").formatted(Formatting.DARK_GRAY), 
                20, startY + (index * lineHeight) + 10 + 10, 0x888888);
            index += 3;
        }
    }
    
    private int getTaskProgress(TaskType task) {
        // This would normally fetch from the server
        // For now, return placeholder values
        return switch (task) {
            case KILL_MOBS -> 25;
            case COLLECT_ITEMS -> 50;
            case REACH_NETHER -> 0;
            case FIND_DIAMONDS -> 5;
            case KILL_BOSS -> 0;
        };
    }
    
    private int getTotalTaskProgress() {
        int total = 0;
        for (TaskType task : TaskType.values()) {
            total += getTaskProgress(task);
        }
        return total;
    }
    
    private int getTotalTaskRequired() {
        int total = 0;
        for (TaskType task : TaskType.values()) {
            total += task.getRequiredAmount();
        }
        return total;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}

