package com.wayacreate.frogslimegamemode.client.gui;

import com.wayacreate.frogslimegamemode.client.state.ProgressionClientState;
import com.wayacreate.frogslimegamemode.tasks.TaskType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class TasksScreen extends Screen {
    private static final String[] TABS = {"Journey", "Recipes", "Achievements"};
    private static final String[][] RECIPE_ROWS = {
        {"Ability Forge", "Crafting Table + Emeralds + Slime Balls", "Main forge for mob ability items."},
        {"Frog Crafting Table", "Crafting Tables + Slime Balls", "Unlock the custom frog crafting route."},
        {"Ability Stick", "Stick + Stone + Dirt + Sand", "Legacy path for anvil ability crafting."},
        {"Hunter Tracker", "Compass with the hunter kit", "Hold it to cycle Track, Blockade, Snare."},
        {"Speedrunner Clock", "Starter clock for the speedrunner", "Hold it, TAB to cycle, R to cast."},
        {"Contracts", "/frogslime contract list", "Accept a contract to start the economy route."}
    };
    private static final String[][] ACHIEVEMENT_ROWS = {
        {"Journey Started", "Enable the route and receive the starter kit.", "ACTIVATE_GAMEMODE"},
        {"First Ally", "Tame your first helper.", "TAME_HELPER"},
        {"Helper Commander", "Assign a helper role.", "ASSIGN_ROLE"},
        {"Mob Smith", "Forge your first mob ability item.", "CRAFT_ABILITY"},
        {"Ability Apprentice", "Unlock 3 player abilities.", "UNLOCK_ABILITIES"},
        {"Nether Bound", "Reach the Nether.", "REACH_NETHER"},
        {"End Walker", "Reach the End.", "REACH_END"},
        {"Beat the Run", "Defeat the dragon or giant slime boss.", "DEFEAT_FINAL_BOSS"}
    };

    private int currentTab;

    public TasksScreen() {
        super(Component.literal(GuiCopy.PRIMARY));
    }

    @Override
    protected void init() {
        int buttonWidth = MenuStyle.getButtonWidth(this.width) - 10;
        int startX = MenuStyle.getContentMargin(this.width, this.height);
        int gap = MenuStyle.getGutter(this.width);
        for (int i = 0; i < TABS.length; i++) {
            int index = i;
            this.addDrawableChild(Button.builder(Component.literal(TABS[i]), button -> currentTab = index)
                .dimensions(startX + i * (buttonWidth + gap), 36, buttonWidth, 20)
                .build());
        }

        this.addDrawableChild(Button.builder(Component.literal("Close"), button -> this.close())
            .dimensions(this.width - MenuStyle.getContentMargin(this.width, this.height) - MenuStyle.getButtonWidth(this.width), this.height - MenuStyle.getBottomInset(this.height), MenuStyle.getButtonWidth(this.width), 20)
            .build());
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        MenuStyle.drawFrame(context, this.width, this.height);
        MenuStyle.drawHeader(
            context,
            this.textRenderer,
            GuiCopy.PRIMARY,
            GuiCopy.SECONDARY,
            MenuStyle.getContentMargin(this.width, this.height),
            this.width - MenuStyle.getContentMargin(this.width, this.height) * 2
        );

        int panelX = MenuStyle.getContentMargin(this.width, this.height);
        int panelY = MenuStyle.getContentTop();
        int panelWidth = this.width - panelX * 2;
        int panelHeight = this.height - panelY - MenuStyle.getBottomInset(this.height) - 12;
        MenuStyle.drawPanel(context, panelX, panelY, panelWidth, panelHeight, currentTab % 2 == 1);
        switch (currentTab) {
            case 0 -> renderJourney(context, panelX, panelWidth);
            case 1 -> renderRecipes(context, panelX, panelWidth);
            default -> renderAchievements(context, panelX, panelWidth);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderJourney(GuiGraphics context, int panelLeft, int panelWidth) {
        int panelX = panelLeft + 12;
        int y = 86;

        context.drawTextWithShadow(
            this.textRenderer,
            Component.literal("Completed " + ProgressionClientState.getCompletedTaskCount() + " / " + TaskType.values().length),
            panelX,
            y,
            MenuStyle.ACCENT_GOLD
        );
        MenuStyle.drawProgressBar(context, panelX + 132, y + 1, panelWidth - 168, 10, ProgressionClientState.getOverallProgress(), MenuStyle.POSITIVE);

        int rowY = y + 28;
        for (TaskType task : TaskType.values()) {
            int progress = ProgressionClientState.getTaskProgress(task);
            boolean complete = progress >= task.getRequiredAmount();
            int rowHeight = 34;

            MenuStyle.drawPanel(context, panelX, rowY, panelWidth - 24, rowHeight, !complete);
            context.drawTextWithShadow(
                this.textRenderer,
                Component.literal(task.getDisplayName()),
                panelX + 10,
                rowY + 7,
                complete ? MenuStyle.POSITIVE : MenuStyle.TEXT
            );
            context.drawTextWithShadow(
                this.textRenderer,
                Component.literal(task.getCategory()),
                panelX + 10,
                rowY + 19,
                MenuStyle.MUTED
            );

            context.drawTextWithShadow(
                this.textRenderer,
                Component.literal(task.getDescription()),
                panelX + MenuStyle.clamp(panelWidth / 4, 124, 168),
                rowY + 7,
                MenuStyle.MUTED
            );

            float ratio = Math.min(1.0f, progress / (float) task.getRequiredAmount());
            int progressBarX = panelX + panelWidth - 160;
            MenuStyle.drawProgressBar(context, progressBarX, rowY + 8, 96, 10, ratio, complete ? MenuStyle.POSITIVE : MenuStyle.ACCENT);
            context.drawTextWithShadow(
                this.textRenderer,
                Component.literal(progress + "/" + task.getRequiredAmount()),
                progressBarX + 106,
                rowY + 7,
                complete ? MenuStyle.POSITIVE : MenuStyle.TEXT
            );

            context.drawTextWithShadow(
                this.textRenderer,
                Component.literal("Unlock: " + task.getRewardText()),
                panelX + MenuStyle.clamp(panelWidth / 4, 124, 168),
                rowY + 19,
                complete ? MenuStyle.POSITIVE : MenuStyle.TEXT
            );

            rowY += rowHeight + 6;
        }
    }

    private void renderRecipes(GuiGraphics context, int panelLeft, int panelWidth) {
        int x = panelLeft + 12;
        int y = 90;

        context.drawTextWithShadow(this.textRenderer, Component.literal("Core Route Recipes"), x, y, MenuStyle.ACCENT_GOLD);
        y += 22;

        for (int i = 0; i < RECIPE_ROWS.length; i++) {
            int rowY = y + i * 42;
            MenuStyle.drawPanel(context, x, rowY, panelWidth - 24, 36, i % 2 == 1);
            context.drawTextWithShadow(this.textRenderer, Component.literal(RECIPE_ROWS[i][0]), x + 10, rowY + 6, MenuStyle.TEXT);
            context.drawTextWithShadow(this.textRenderer, Component.literal(RECIPE_ROWS[i][1]), x + 10, rowY + 18, MenuStyle.ACCENT);
            MenuStyle.drawWrappedText(context, this.textRenderer, RECIPE_ROWS[i][2], x + MenuStyle.clamp(panelWidth / 2, 220, 300), rowY + 6, panelWidth / 2 - 30, MenuStyle.MUTED, 2);
        }

        MenuStyle.drawWrappedText(
            context,
            this.textRenderer,
            GuiCopy.FULL,
            x,
            this.height - 88,
            panelWidth - 24,
            MenuStyle.MUTED,
            4
        );
    }

    private void renderAchievements(GuiGraphics context, int panelLeft, int panelWidth) {
        int x = panelLeft + 12;
        int y = 90;

        context.drawTextWithShadow(
            this.textRenderer,
            Component.literal("Achievement Popups Unlocked: " + ProgressionClientState.getAchievementCount()),
            x,
            y,
            MenuStyle.ACCENT_GOLD
        );
        y += 24;

        for (int i = 0; i < ACHIEVEMENT_ROWS.length; i++) {
            int rowY = y + i * 34;
            TaskType gate = TaskType.valueOf(ACHIEVEMENT_ROWS[i][2]);
            boolean unlocked = ProgressionClientState.getTaskProgress(gate) >= gate.getRequiredAmount();
            MenuStyle.drawPanel(context, x, rowY, panelWidth - 24, 28, i % 2 == 0);
            context.drawTextWithShadow(
                this.textRenderer,
                Component.literal(ACHIEVEMENT_ROWS[i][0]),
                x + 10,
                rowY + 8,
                unlocked ? MenuStyle.POSITIVE : MenuStyle.TEXT
            );
            context.drawTextWithShadow(
                this.textRenderer,
                Component.literal(ACHIEVEMENT_ROWS[i][1]),
                x + 160,
                rowY + 8,
                MenuStyle.MUTED
            );
            context.drawTextWithShadow(
                this.textRenderer,
                Component.literal(unlocked ? "Unlocked" : "Locked"),
                x + panelWidth - 92,
                rowY + 8,
                unlocked ? MenuStyle.POSITIVE : MenuStyle.DANGER
            );
        }

        int unlockY = this.height - 120;
        context.drawTextWithShadow(this.textRenderer, Component.literal("Up Next"), x, unlockY, MenuStyle.ACCENT_GOLD);
        if (ProgressionClientState.getNextUnlockNames().isEmpty()) {
            context.drawTextWithShadow(this.textRenderer, Component.literal("No queued unlocks."), x, unlockY + 16, MenuStyle.MUTED);
            return;
        }

        for (int i = 0; i < ProgressionClientState.getNextUnlockNames().size(); i++) {
            context.drawTextWithShadow(
                this.textRenderer,
                Component.literal(ProgressionClientState.getNextUnlockNames().get(i) + " - " + ProgressionClientState.getNextUnlockDescriptions().get(i) + " | " + GuiCopy.PRIMARY),
                x,
                unlockY + 16 + i * 14,
                MenuStyle.MUTED
            );
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
