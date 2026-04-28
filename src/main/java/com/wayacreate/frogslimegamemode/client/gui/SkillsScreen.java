package com.wayacreate.frogslimegamemode.client.gui;

import com.wayacreate.frogslimegamemode.client.state.ManhuntClientState;
import com.wayacreate.frogslimegamemode.client.state.ProgressionClientState;
import com.wayacreate.frogslimegamemode.tasks.TaskType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class SkillsScreen extends Screen {
    private static final String[] CATEGORIES = {"Journey", "Helpers", "Abilities", "Manhunt"};

    private int currentCategory;

    public SkillsScreen() {
        super(Component.literal(GuiCopy.PRIMARY));
    }

    @Override
    protected void init() {
        this.addDrawableChild(Button.builder(Component.literal("Close"), button -> this.close())
            .dimensions(this.width - 108, this.height - 36, 80, 20)
            .build());

        for (int i = 0; i < CATEGORIES.length; i++) {
            int index = i;
            this.addDrawableChild(Button.builder(Component.literal(CATEGORIES[i]), button -> currentCategory = index)
                .dimensions(28, 76 + i * 26, 90, 20)
                .build());
        }
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

        int leftX = MenuStyle.getContentMargin(this.width, this.height);
        int topY = MenuStyle.getContentTop();
        int leftWidth = MenuStyle.clamp(this.width / 7, 102, 144);
        int gutter = MenuStyle.getGutter(this.width);
        int rightX = leftX + leftWidth + gutter;
        int rightWidth = this.width - rightX - leftX;
        int panelHeight = this.height - topY - MenuStyle.getBottomInset(this.height) - 12;
        MenuStyle.drawPanel(context, leftX, topY, leftWidth, panelHeight, true);
        MenuStyle.drawPanel(context, rightX, topY, rightWidth, panelHeight, false);

        renderCategoryList(context, leftX);
        renderCategory(context, rightX);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderCategoryList(GuiGraphics context, int leftX) {
        context.drawTextWithShadow(this.textRenderer, Component.literal("Systems"), leftX + 12, 84, MenuStyle.ACCENT_GOLD);
        for (int i = 0; i < CATEGORIES.length; i++) {
            int y = 112 + i * 26;
            int color = i == currentCategory ? MenuStyle.ACCENT : MenuStyle.MUTED;
            context.drawTextWithShadow(this.textRenderer, Component.literal(CATEGORIES[i]), leftX + 12, y, color);
        }
    }

    private void renderCategory(GuiGraphics context, int contentX) {
        switch (currentCategory) {
            case 0 -> renderJourney(context, contentX);
            case 1 -> renderHelpers(context, contentX);
            case 2 -> renderAbilities(context, contentX);
            default -> renderManhunt(context, contentX);
        }
    }

    private void renderJourney(GuiGraphics context, int x) {
        int y = 86;
        context.drawTextWithShadow(this.textRenderer, Component.literal("Journey Flow"), x, y, MenuStyle.ACCENT_GOLD);
        y += 24;

        context.drawTextWithShadow(this.textRenderer, Component.literal("Level " + ProgressionClientState.getLevel()), x, y, MenuStyle.TEXT);
        MenuStyle.drawProgressBar(
            context,
            x + 82,
            y + 2,
            220,
            10,
            ProgressionClientState.getXpToNext() <= 0.0 ? 0.0f : Math.min(1.0f, (float) (ProgressionClientState.getXp() / ProgressionClientState.getXpToNext())),
            MenuStyle.ACCENT
        );
        y += 24;

        context.drawTextWithShadow(this.textRenderer, Component.literal("Completed Tasks"), x, y, MenuStyle.MUTED);
        context.drawTextWithShadow(this.textRenderer, Component.literal(ProgressionClientState.getCompletedTaskCount() + "/" + TaskType.values().length), x + 110, y, MenuStyle.TEXT);
        y += 24;

        context.drawTextWithShadow(this.textRenderer, Component.literal("Recommended route"), x, y, MenuStyle.ACCENT_GOLD);
        y += 18;
        drawLine(context, x, y, taskHint(TaskType.TAME_HELPER)); y += 16;
        drawLine(context, x, y, taskHint(TaskType.ASSIGN_ROLE)); y += 16;
        drawLine(context, x, y, taskHint(TaskType.UNLOCK_ABILITIES)); y += 16;
        drawLine(context, x, y, taskHint(TaskType.REACH_NETHER)); y += 16;
        drawLine(context, x, y, taskHint(TaskType.REACH_END)); y += 16;
        drawLine(context, x, y, taskHint(TaskType.DEFEAT_FINAL_BOSS));
    }

    private void renderHelpers(GuiGraphics context, int x) {
        int y = 86;
        context.drawTextWithShadow(this.textRenderer, Component.literal("Helper Command"), x, y, MenuStyle.ACCENT_GOLD);
        y += 24;

        drawLine(context, x, y, "Helpers spawned: " + ProgressionClientState.getHelpersSpawned()); y += 16;
        drawLine(context, x, y, "Highest evolution stage: " + ProgressionClientState.getHighestEvolutionStage()); y += 16;
        drawLine(context, x, y, progressLine(TaskType.TAME_HELPER)); y += 16;
        drawLine(context, x, y, progressLine(TaskType.ASSIGN_ROLE)); y += 16;
        drawLine(context, x, y, progressLine(TaskType.EVOLVE_HELPER)); y += 24;

        context.drawTextWithShadow(this.textRenderer, Component.literal("What changed"), x, y, MenuStyle.ACCENT_GOLD);
        y += 18;
        drawLine(context, x, y, "Starter kit now includes helper role items."); y += 16;
        drawLine(context, x, y, "Role assignment advances progression and achievements."); y += 16;
        drawLine(context, x, y, "Evolution progress now feeds task and unlock tracking. " + GuiCopy.PRIMARY);
    }

    private void renderAbilities(GuiGraphics context, int x) {
        int y = 86;
        context.drawTextWithShadow(this.textRenderer, Component.literal("Ability Loop"), x, y, MenuStyle.ACCENT_GOLD);
        y += 24;

        drawLine(context, x, y, "Current slot: " + ProgressionClientState.getSelectedAbilityName()); y += 16;
        drawLine(context, x, y, ProgressionClientState.getSelectedAbilityDescription()); y += 16;
        drawLine(context, x, y, "Abilities unlocked: " + ProgressionClientState.getAbilityCount()); y += 16;
        drawLine(context, x, y, progressLine(TaskType.UNLOCK_ABILITIES)); y += 16;
        drawLine(context, x, y, progressLine(TaskType.CRAFT_ABILITY)); y += 24;

        context.drawTextWithShadow(this.textRenderer, Component.literal("Controls"), x, y, MenuStyle.ACCENT_GOLD);
        y += 18;
        drawLine(context, x, y, "TAB cycles your selected player ability."); y += 16;
        drawLine(context, x, y, "R casts the selected active ability."); y += 16;
        drawLine(context, x, y, "X consumes a held mob ability item if you forged one.");
    }

    private void renderManhunt(GuiGraphics context, int x) {
        int y = 86;
        context.drawTextWithShadow(this.textRenderer, Component.literal("Manhunt Controls"), x, y, MenuStyle.ACCENT_GOLD);
        y += 24;

        if (!ManhuntClientState.isActive()) {
            drawLine(context, x, y, "Use /frogslime manhunt speedrunner or /frogslime manhunt auto."); y += 16;
            drawLine(context, x, y, "Hunters hold a tracker or manhunt compass."); y += 16;
            drawLine(context, x, y, "Speedrunners hold a clock."); y += 16;
            drawLine(context, x, y, "TAB cycles role abilities and R uses the selected one."); y += 24;
            drawLine(context, x, y, "The run now ends when the dragon or giant slime boss dies.");
            return;
        }

        drawLine(context, x, y, "Role: " + (ManhuntClientState.isHunter() ? "Hunter" : "Speedrunner")); y += 16;
        drawLine(context, x, y, "Selected: " + ManhuntClientState.getSelectedAbilityName()); y += 16;
        drawLine(context, x, y, ManhuntClientState.getSelectedAbilityDescription()); y += 16;

        if (ManhuntClientState.isHunter()) {
            drawLine(context, x, y, "Target: " + ManhuntClientState.getTargetName()); y += 16;
            drawLine(context, x, y, "TAB cycles Track, Blockade, Snare."); y += 16;
            drawLine(context, x, y, "R uses the highlighted hunter tool.");
        } else {
            drawLine(context, x, y, "Time: " + ManhuntClientState.getElapsedTime()); y += 16;
            drawLine(context, x, y, "Deaths: " + ManhuntClientState.getDeathCount()); y += 16;
            drawLine(context, x, y, "TAB cycles Escape, Burst, Veil."); y += 16;
            drawLine(context, x, y, "R uses the highlighted escape tool.");
        }
    }

    private void drawLine(GuiGraphics context, int x, int y, String line) {
        context.drawTextWithShadow(this.textRenderer, Component.literal(line), x, y, MenuStyle.MUTED);
    }

    private String progressLine(TaskType task) {
        int progress = ProgressionClientState.getTaskProgress(task);
        boolean complete = progress >= task.getRequiredAmount();
        return task.getDisplayName() + ": " + progress + "/" + task.getRequiredAmount() + (complete ? " ready" : " in progress");
    }

    private String taskHint(TaskType task) {
        int progress = ProgressionClientState.getTaskProgress(task);
        if (progress >= task.getRequiredAmount()) {
            return task.getDisplayName() + " complete";
        }
        return task.getDisplayName() + " - " + task.getDescription();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
