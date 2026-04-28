package com.wayacreate.frogslimegamemode.client.gui;

import com.wayacreate.frogslimegamemode.client.state.ProgressionClientState;
import com.wayacreate.frogslimegamemode.tasks.TaskType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

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
        super(Text.literal("Progression"));
    }

    @Override
    protected void init() {
        int startX = 28;
        for (int i = 0; i < TABS.length; i++) {
            int index = i;
            this.addDrawableChild(ButtonWidget.builder(Text.literal(TABS[i]), button -> currentTab = index)
                .dimensions(startX + i * 86, 36, 78, 20)
                .build());
        }

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Close"), button -> this.close())
            .dimensions(this.width - 108, this.height - 36, 80, 20)
            .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        MenuStyle.drawFrame(context, this.width, this.height);
        MenuStyle.drawHeader(
            context,
            this.textRenderer,
            "Progression Board",
            ProgressionClientState.isActive() ? "Live route objectives and unlock help" : "Enable the gamemode to populate this board",
            this.width
        );

        MenuStyle.drawPanel(context, 28, 72, this.width - 56, this.height - 118, currentTab % 2 == 1);
        switch (currentTab) {
            case 0 -> renderJourney(context);
            case 1 -> renderRecipes(context);
            default -> renderAchievements(context);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderJourney(DrawContext context) {
        int panelX = 40;
        int y = 86;

        context.drawTextWithShadow(
            this.textRenderer,
            Text.literal("Completed " + ProgressionClientState.getCompletedTaskCount() + " / " + TaskType.values().length),
            panelX,
            y,
            MenuStyle.ACCENT_GOLD
        );
        MenuStyle.drawProgressBar(context, panelX + 132, y + 1, this.width - 220, 10, ProgressionClientState.getOverallProgress(), MenuStyle.POSITIVE);

        int rowY = y + 28;
        for (TaskType task : TaskType.values()) {
            int progress = ProgressionClientState.getTaskProgress(task);
            boolean complete = progress >= task.getRequiredAmount();
            int rowHeight = 34;

            MenuStyle.drawPanel(context, panelX, rowY, this.width - 80, rowHeight, !complete);
            context.drawTextWithShadow(
                this.textRenderer,
                Text.literal(task.getDisplayName()),
                panelX + 10,
                rowY + 7,
                complete ? MenuStyle.POSITIVE : MenuStyle.TEXT
            );
            context.drawTextWithShadow(
                this.textRenderer,
                Text.literal(task.getCategory()),
                panelX + 10,
                rowY + 19,
                MenuStyle.MUTED
            );

            context.drawTextWithShadow(
                this.textRenderer,
                Text.literal(task.getDescription()),
                panelX + 124,
                rowY + 7,
                MenuStyle.MUTED
            );

            float ratio = Math.min(1.0f, progress / (float) task.getRequiredAmount());
            MenuStyle.drawProgressBar(context, this.width - 188, rowY + 8, 96, 10, ratio, complete ? MenuStyle.POSITIVE : MenuStyle.ACCENT);
            context.drawTextWithShadow(
                this.textRenderer,
                Text.literal(progress + "/" + task.getRequiredAmount()),
                this.width - 82,
                rowY + 7,
                complete ? MenuStyle.POSITIVE : MenuStyle.TEXT
            );

            context.drawTextWithShadow(
                this.textRenderer,
                Text.literal("Unlock: " + task.getRewardText()),
                panelX + 124,
                rowY + 19,
                complete ? MenuStyle.POSITIVE : MenuStyle.TEXT
            );

            rowY += rowHeight + 6;
        }
    }

    private void renderRecipes(DrawContext context) {
        int x = 40;
        int y = 90;

        context.drawTextWithShadow(this.textRenderer, Text.literal("Core Route Recipes"), x, y, MenuStyle.ACCENT_GOLD);
        y += 22;

        for (int i = 0; i < RECIPE_ROWS.length; i++) {
            int rowY = y + i * 42;
            MenuStyle.drawPanel(context, x, rowY, this.width - 80, 36, i % 2 == 1);
            context.drawTextWithShadow(this.textRenderer, Text.literal(RECIPE_ROWS[i][0]), x + 10, rowY + 6, MenuStyle.TEXT);
            context.drawTextWithShadow(this.textRenderer, Text.literal(RECIPE_ROWS[i][1]), x + 10, rowY + 18, MenuStyle.ACCENT);
            context.drawTextWithShadow(this.textRenderer, Text.literal(RECIPE_ROWS[i][2]), x + 286, rowY + 12, MenuStyle.MUTED);
        }

        context.drawTextWithShadow(
            this.textRenderer,
            Text.literal("The guide book and /frogslime recipes cover the same route in chat-friendly form."),
            x,
            this.height - 72,
            MenuStyle.MUTED
        );
    }

    private void renderAchievements(DrawContext context) {
        int x = 40;
        int y = 90;

        context.drawTextWithShadow(
            this.textRenderer,
            Text.literal("Achievement Popups Unlocked: " + ProgressionClientState.getAchievementCount()),
            x,
            y,
            MenuStyle.ACCENT_GOLD
        );
        y += 24;

        for (int i = 0; i < ACHIEVEMENT_ROWS.length; i++) {
            int rowY = y + i * 34;
            TaskType gate = TaskType.valueOf(ACHIEVEMENT_ROWS[i][2]);
            boolean unlocked = ProgressionClientState.getTaskProgress(gate) >= gate.getRequiredAmount();
            MenuStyle.drawPanel(context, x, rowY, this.width - 80, 28, i % 2 == 0);
            context.drawTextWithShadow(
                this.textRenderer,
                Text.literal(ACHIEVEMENT_ROWS[i][0]),
                x + 10,
                rowY + 8,
                unlocked ? MenuStyle.POSITIVE : MenuStyle.TEXT
            );
            context.drawTextWithShadow(
                this.textRenderer,
                Text.literal(ACHIEVEMENT_ROWS[i][1]),
                x + 160,
                rowY + 8,
                MenuStyle.MUTED
            );
            context.drawTextWithShadow(
                this.textRenderer,
                Text.literal(unlocked ? "Unlocked" : "Locked"),
                this.width - 98,
                rowY + 8,
                unlocked ? MenuStyle.POSITIVE : MenuStyle.DANGER
            );
        }

        int unlockY = this.height - 120;
        context.drawTextWithShadow(this.textRenderer, Text.literal("Up Next"), x, unlockY, MenuStyle.ACCENT_GOLD);
        if (ProgressionClientState.getNextUnlockNames().isEmpty()) {
            context.drawTextWithShadow(this.textRenderer, Text.literal("No queued unlocks."), x, unlockY + 16, MenuStyle.MUTED);
            return;
        }

        for (int i = 0; i < ProgressionClientState.getNextUnlockNames().size(); i++) {
            context.drawTextWithShadow(
                this.textRenderer,
                Text.literal(ProgressionClientState.getNextUnlockNames().get(i) + " - " + ProgressionClientState.getNextUnlockDescriptions().get(i)),
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
