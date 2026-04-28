package com.wayacreate.frogslimegamemode.client.gui;

import com.wayacreate.frogslimegamemode.client.state.ProgressionClientState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class FrogSlimeGamemodeScreen extends Screen {
    public FrogSlimeGamemodeScreen(PlayerEntity player) {
        super(Text.literal("Frog & Slime Route"));
    }

    @Override
    protected void init() {
        int bottomY = this.height - 36;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Tasks"), button -> this.client.setScreen(new TasksScreen()))
            .dimensions(28, bottomY, 80, 20)
            .build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Systems"), button -> this.client.setScreen(new SkillsScreen()))
            .dimensions(116, bottomY, 80, 20)
            .build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Unlocks"), button -> this.client.setScreen(new CollectionsScreen()))
            .dimensions(204, bottomY, 80, 20)
            .build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Close"), button -> this.close())
            .dimensions(this.width - 108, bottomY, 80, 20)
            .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        MenuStyle.drawFrame(context, this.width, this.height);
        MenuStyle.drawHeader(
            context,
            this.textRenderer,
            "Frog & Slime Route",
            ProgressionClientState.isActive() ? "Live progression board" : "Enable the gamemode to start your route",
            this.width
        );

        int leftX = 28;
        int topY = 72;
        int leftWidth = this.width / 2 - 38;
        int rightX = this.width / 2 + 10;
        int rightWidth = this.width - rightX - 28;

        MenuStyle.drawPanel(context, leftX, topY, leftWidth, 118, false);
        MenuStyle.drawPanel(context, rightX, topY, rightWidth, 118, true);
        MenuStyle.drawPanel(context, leftX, topY + 128, leftWidth, 132, true);
        MenuStyle.drawPanel(context, rightX, topY + 128, rightWidth, 132, false);

        renderOverview(context, leftX, topY, leftWidth);
        renderAbilityPanel(context, rightX, topY, rightWidth);
        renderStatsPanel(context, leftX, topY + 128, leftWidth);
        renderUnlockPanel(context, rightX, topY + 128, rightWidth);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderOverview(DrawContext context, int x, int y, int width) {
        context.drawTextWithShadow(this.textRenderer, Text.literal("Route Status"), x + 12, y + 10, MenuStyle.TEXT);

        String status = ProgressionClientState.isActive() ? "ACTIVE" : "INACTIVE";
        int statusColor = ProgressionClientState.isActive() ? MenuStyle.POSITIVE : MenuStyle.DANGER;
        context.drawTextWithShadow(this.textRenderer, Text.literal(status), x + width - 58, y + 10, statusColor);

        context.drawTextWithShadow(
            this.textRenderer,
            Text.literal("Level " + ProgressionClientState.getLevel()),
            x + 12,
            y + 30,
            MenuStyle.ACCENT_GOLD
        );

        float xpProgress = ProgressionClientState.getXpToNext() <= 0.0
            ? 0.0f
            : Math.min(1.0f, (float) (ProgressionClientState.getXp() / ProgressionClientState.getXpToNext()));
        MenuStyle.drawProgressBar(context, x + 12, y + 48, width - 24, 12, xpProgress, MenuStyle.ACCENT);
        context.drawTextWithShadow(
            this.textRenderer,
            Text.literal(formatProgress(ProgressionClientState.getXp()) + " / " + formatProgress(ProgressionClientState.getXpToNext()) + " XP"),
            x + 12,
            y + 66,
            MenuStyle.MUTED
        );

        float routeProgress = ProgressionClientState.getOverallProgress();
        MenuStyle.drawProgressBar(context, x + 12, y + 86, width - 24, 12, routeProgress, MenuStyle.POSITIVE);
        context.drawTextWithShadow(
            this.textRenderer,
            Text.literal("Route completion " + (int) (routeProgress * 100) + "%"),
            x + 12,
            y + 104,
            MenuStyle.TEXT
        );
    }

    private void renderAbilityPanel(DrawContext context, int x, int y, int width) {
        context.drawTextWithShadow(this.textRenderer, Text.literal("Current Ability"), x + 12, y + 10, MenuStyle.TEXT);
        context.drawTextWithShadow(
            this.textRenderer,
            Text.literal(ProgressionClientState.getSelectedAbilityName()),
            x + 12,
            y + 34,
            MenuStyle.ACCENT
        );
        context.drawTextWithShadow(
            this.textRenderer,
            Text.literal(ProgressionClientState.getSelectedAbilityDescription()),
            x + 12,
            y + 52,
            MenuStyle.MUTED
        );
        context.drawTextWithShadow(
            this.textRenderer,
            Text.literal("Abilities unlocked: " + ProgressionClientState.getAbilityCount()),
            x + 12,
            y + 80,
            MenuStyle.TEXT
        );
        context.drawTextWithShadow(
            this.textRenderer,
            Text.literal("TAB cycles, R uses, X consumes held mob ability items."),
            x + 12,
            y + 98,
            MenuStyle.MUTED
        );
    }

    private void renderStatsPanel(DrawContext context, int x, int y, int width) {
        context.drawTextWithShadow(this.textRenderer, Text.literal("Run Stats"), x + 12, y + 10, MenuStyle.TEXT);

        drawMetric(context, x + 12, y + 32, "Helpers", Integer.toString(ProgressionClientState.getHelpersSpawned()));
        drawMetric(context, x + 12, y + 54, "Mobs Eaten", Integer.toString(ProgressionClientState.getMobsEaten()));
        drawMetric(context, x + 12, y + 76, "Items", Integer.toString(ProgressionClientState.getItemsCollected()));
        drawMetric(context, x + 12, y + 98, "Deaths", Integer.toString(ProgressionClientState.getDeaths()));

        int rightColumn = x + width / 2 + 8;
        drawMetric(context, rightColumn, y + 32, "Jumps", Integer.toString(ProgressionClientState.getJumps()));
        drawMetric(context, rightColumn, y + 54, "Evolution", "Stage " + ProgressionClientState.getHighestEvolutionStage());
        drawMetric(context, rightColumn, y + 76, "Tasks", ProgressionClientState.getCompletedTaskCount() + "/" + com.wayacreate.frogslimegamemode.tasks.TaskType.values().length);
        drawMetric(context, rightColumn, y + 98, "Achievements", Integer.toString(ProgressionClientState.getAchievementCount()));
    }

    private void renderUnlockPanel(DrawContext context, int x, int y, int width) {
        context.drawTextWithShadow(this.textRenderer, Text.literal("Next Unlocks"), x + 12, y + 10, MenuStyle.TEXT);

        if (ProgressionClientState.getNextUnlockNames().isEmpty()) {
            context.drawTextWithShadow(
                this.textRenderer,
                Text.literal("No pending unlocks. Push the main route forward."),
                x + 12,
                y + 34,
                MenuStyle.MUTED
            );
            return;
        }

        for (int i = 0; i < ProgressionClientState.getNextUnlockNames().size(); i++) {
            int rowY = y + 30 + i * 30;
            context.drawTextWithShadow(
                this.textRenderer,
                Text.literal(ProgressionClientState.getNextUnlockNames().get(i)),
                x + 12,
                rowY,
                MenuStyle.ACCENT_GOLD
            );
            context.drawTextWithShadow(
                this.textRenderer,
                Text.literal(ProgressionClientState.getNextUnlockDescriptions().get(i)),
                x + 12,
                rowY + 14,
                MenuStyle.MUTED
            );
        }
    }

    private void drawMetric(DrawContext context, int x, int y, String label, String value) {
        context.drawTextWithShadow(this.textRenderer, Text.literal(label), x, y, MenuStyle.MUTED);
        context.drawTextWithShadow(this.textRenderer, Text.literal(value), x + 74, y, MenuStyle.TEXT);
    }

    private String formatProgress(double value) {
        if (value >= 1000.0) {
            return String.format("%.1fk", value / 1000.0);
        }
        return Integer.toString((int) value);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
