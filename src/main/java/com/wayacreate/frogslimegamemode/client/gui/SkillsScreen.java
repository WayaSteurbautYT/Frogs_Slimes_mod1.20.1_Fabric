package com.wayacreate.frogslimegamemode.client.gui;

import com.wayacreate.frogslimegamemode.client.state.ManhuntClientState;
import com.wayacreate.frogslimegamemode.client.state.ProgressionClientState;
import com.wayacreate.frogslimegamemode.tasks.TaskType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class SkillsScreen extends Screen {
    private static final String[] CATEGORIES = {"Journey", "Helpers", "Abilities", "Manhunt"};

    private int currentCategory;

    public SkillsScreen() {
        super(Text.literal("Route Systems"));
    }

    @Override
    protected void init() {
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Close"), button -> this.close())
            .dimensions(this.width - 108, this.height - 36, 80, 20)
            .build());

        for (int i = 0; i < CATEGORIES.length; i++) {
            int index = i;
            this.addDrawableChild(ButtonWidget.builder(Text.literal(CATEGORIES[i]), button -> currentCategory = index)
                .dimensions(28, 76 + i * 26, 90, 20)
                .build());
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        MenuStyle.drawFrame(context, this.width, this.height);
        MenuStyle.drawHeader(
            context,
            this.textRenderer,
            "Route Systems",
            "Progress, helpers, abilities, and manhunt controls",
            this.width
        );

        MenuStyle.drawPanel(context, 28, 72, 102, this.height - 118, true);
        MenuStyle.drawPanel(context, 140, 72, this.width - 168, this.height - 118, false);

        renderCategoryList(context);
        renderCategory(context);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderCategoryList(DrawContext context) {
        context.drawTextWithShadow(this.textRenderer, Text.literal("Systems"), 40, 84, MenuStyle.ACCENT_GOLD);
        for (int i = 0; i < CATEGORIES.length; i++) {
            int y = 112 + i * 26;
            int color = i == currentCategory ? MenuStyle.ACCENT : MenuStyle.MUTED;
            context.drawTextWithShadow(this.textRenderer, Text.literal(CATEGORIES[i]), 40, y, color);
        }
    }

    private void renderCategory(DrawContext context) {
        switch (currentCategory) {
            case 0 -> renderJourney(context);
            case 1 -> renderHelpers(context);
            case 2 -> renderAbilities(context);
            default -> renderManhunt(context);
        }
    }

    private void renderJourney(DrawContext context) {
        int x = 154;
        int y = 86;
        context.drawTextWithShadow(this.textRenderer, Text.literal("Journey Flow"), x, y, MenuStyle.ACCENT_GOLD);
        y += 24;

        context.drawTextWithShadow(this.textRenderer, Text.literal("Level " + ProgressionClientState.getLevel()), x, y, MenuStyle.TEXT);
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

        context.drawTextWithShadow(this.textRenderer, Text.literal("Completed Tasks"), x, y, MenuStyle.MUTED);
        context.drawTextWithShadow(this.textRenderer, Text.literal(ProgressionClientState.getCompletedTaskCount() + "/" + TaskType.values().length), x + 110, y, MenuStyle.TEXT);
        y += 24;

        context.drawTextWithShadow(this.textRenderer, Text.literal("Recommended route"), x, y, MenuStyle.ACCENT_GOLD);
        y += 18;
        drawLine(context, x, y, taskHint(TaskType.TAME_HELPER)); y += 16;
        drawLine(context, x, y, taskHint(TaskType.ASSIGN_ROLE)); y += 16;
        drawLine(context, x, y, taskHint(TaskType.UNLOCK_ABILITIES)); y += 16;
        drawLine(context, x, y, taskHint(TaskType.REACH_NETHER)); y += 16;
        drawLine(context, x, y, taskHint(TaskType.REACH_END)); y += 16;
        drawLine(context, x, y, taskHint(TaskType.DEFEAT_FINAL_BOSS));
    }

    private void renderHelpers(DrawContext context) {
        int x = 154;
        int y = 86;
        context.drawTextWithShadow(this.textRenderer, Text.literal("Helper Command"), x, y, MenuStyle.ACCENT_GOLD);
        y += 24;

        drawLine(context, x, y, "Helpers spawned: " + ProgressionClientState.getHelpersSpawned()); y += 16;
        drawLine(context, x, y, "Highest evolution stage: " + ProgressionClientState.getHighestEvolutionStage()); y += 16;
        drawLine(context, x, y, progressLine(TaskType.TAME_HELPER)); y += 16;
        drawLine(context, x, y, progressLine(TaskType.ASSIGN_ROLE)); y += 16;
        drawLine(context, x, y, progressLine(TaskType.EVOLVE_HELPER)); y += 24;

        context.drawTextWithShadow(this.textRenderer, Text.literal("What changed"), x, y, MenuStyle.ACCENT_GOLD);
        y += 18;
        drawLine(context, x, y, "Starter kit now includes helper role items."); y += 16;
        drawLine(context, x, y, "Role assignment advances progression and achievements."); y += 16;
        drawLine(context, x, y, "Evolution progress now feeds task and unlock tracking.");
    }

    private void renderAbilities(DrawContext context) {
        int x = 154;
        int y = 86;
        context.drawTextWithShadow(this.textRenderer, Text.literal("Ability Loop"), x, y, MenuStyle.ACCENT_GOLD);
        y += 24;

        drawLine(context, x, y, "Current slot: " + ProgressionClientState.getSelectedAbilityName()); y += 16;
        drawLine(context, x, y, ProgressionClientState.getSelectedAbilityDescription()); y += 16;
        drawLine(context, x, y, "Abilities unlocked: " + ProgressionClientState.getAbilityCount()); y += 16;
        drawLine(context, x, y, progressLine(TaskType.UNLOCK_ABILITIES)); y += 16;
        drawLine(context, x, y, progressLine(TaskType.CRAFT_ABILITY)); y += 24;

        context.drawTextWithShadow(this.textRenderer, Text.literal("Controls"), x, y, MenuStyle.ACCENT_GOLD);
        y += 18;
        drawLine(context, x, y, "TAB cycles your selected player ability."); y += 16;
        drawLine(context, x, y, "R casts the selected active ability."); y += 16;
        drawLine(context, x, y, "X consumes a held mob ability item if you forged one.");
    }

    private void renderManhunt(DrawContext context) {
        int x = 154;
        int y = 86;
        context.drawTextWithShadow(this.textRenderer, Text.literal("Manhunt Controls"), x, y, MenuStyle.ACCENT_GOLD);
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

    private void drawLine(DrawContext context, int x, int y, String line) {
        context.drawTextWithShadow(this.textRenderer, Text.literal(line), x, y, MenuStyle.MUTED);
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
