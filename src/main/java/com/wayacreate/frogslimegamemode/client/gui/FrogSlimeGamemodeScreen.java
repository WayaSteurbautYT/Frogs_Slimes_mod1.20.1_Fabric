package com.wayacreate.frogslimegamemode.client.gui;

import com.wayacreate.frogslimegamemode.client.state.ProgressionClientState;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;

public class FrogSlimeGamemodeScreen extends Screen {
    public FrogSlimeGamemodeScreen(Player player) {
        super(Component.literal(GuiCopy.PRIMARY));
    }

    @Override
    protected void init() {
        int buttonWidth = MenuStyle.getButtonWidth(this.width);
        int buttonHeight = MenuStyle.getButtonHeight();
        int bottomY = this.height - MenuStyle.getBottomInset(this.height);
        int left = MenuStyle.getContentMargin(this.width, this.height);
        int gap = MenuStyle.getGutter(this.width);

        this.addDrawableChild(Button.builder(Component.literal("Tasks"), button -> this.client.setScreen(new TasksScreen()))
            .dimensions(left, bottomY, buttonWidth, buttonHeight)
            .build());
        this.addDrawableChild(Button.builder(Component.literal("Systems"), button -> this.client.setScreen(new SkillsScreen()))
            .dimensions(left + buttonWidth + gap, bottomY, buttonWidth, buttonHeight)
            .build());
        this.addDrawableChild(Button.builder(Component.literal("Unlocks"), button -> this.client.setScreen(new CollectionsScreen()))
            .dimensions(left + (buttonWidth + gap) * 2, bottomY, buttonWidth, buttonHeight)
            .build());
        this.addDrawableChild(Button.builder(Component.literal("Close"), button -> this.close())
            .dimensions(this.width - left - buttonWidth, bottomY, buttonWidth, buttonHeight)
            .build());
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        MenuStyle.drawFrame(context, this.width, this.height);
        int contentMargin = MenuStyle.getContentMargin(this.width, this.height);
        int gutter = MenuStyle.getGutter(this.width);
        int contentWidth = this.width - contentMargin * 2;
        MenuStyle.drawHeader(
            context,
            this.textRenderer,
            GuiCopy.PRIMARY,
            GuiCopy.SECONDARY,
            contentMargin,
            contentWidth
        );

        int leftX = contentMargin;
        int topY = MenuStyle.getContentTop();
        int leftWidth = (contentWidth - gutter) / 2;
        int rightX = leftX + leftWidth + gutter;
        int rightWidth = contentWidth - leftWidth - gutter;
        int topPanelHeight = MenuStyle.clamp(this.height / 5, 118, 152);
        int bottomPanelHeight = MenuStyle.clamp(this.height / 4, 132, 176);
        int bottomY = topY + topPanelHeight + gutter;

        MenuStyle.drawPanel(context, leftX, topY, leftWidth, topPanelHeight, false);
        MenuStyle.drawPanel(context, rightX, topY, rightWidth, topPanelHeight, true);
        MenuStyle.drawPanel(context, leftX, bottomY, leftWidth, bottomPanelHeight, true);
        MenuStyle.drawPanel(context, rightX, bottomY, rightWidth, bottomPanelHeight, false);

        renderOverview(context, leftX, topY, leftWidth, topPanelHeight);
        renderAbilityPanel(context, rightX, topY, rightWidth);
        renderStatsPanel(context, leftX, bottomY, leftWidth);
        renderUnlockPanel(context, rightX, bottomY, rightWidth);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderOverview(GuiGraphics context, int x, int y, int width, int height) {
        context.drawTextWithShadow(this.textRenderer, Component.literal("Route Status"), x + 12, y + 10, MenuStyle.TEXT);

        String status = ProgressionClientState.isActive() ? "ACTIVE" : "INACTIVE";
        int statusColor = ProgressionClientState.isActive() ? MenuStyle.POSITIVE : MenuStyle.DANGER;
        int statusWidth = this.textRenderer.getWidth(status);
        context.drawTextWithShadow(this.textRenderer, Component.literal(status), x + width - statusWidth - 12, y + 10, statusColor);

        context.drawTextWithShadow(
            this.textRenderer,
            Component.literal("Level " + ProgressionClientState.getLevel()),
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
            Component.literal(formatProgress(ProgressionClientState.getXp()) + " / " + formatProgress(ProgressionClientState.getXpToNext()) + " XP"),
            x + 12,
            y + 66,
            MenuStyle.MUTED
        );

        float routeProgress = ProgressionClientState.getOverallProgress();
        int progressY = Math.min(y + height - 30, y + 86);
        MenuStyle.drawProgressBar(context, x + 12, progressY, width - 24, 12, routeProgress, MenuStyle.POSITIVE);
        context.drawTextWithShadow(
            this.textRenderer,
            Component.literal("Route completion " + (int) (routeProgress * 100) + "%"),
            x + 12,
            progressY + 18,
            MenuStyle.TEXT
        );
    }

    private void renderAbilityPanel(GuiGraphics context, int x, int y, int width) {
        context.drawTextWithShadow(this.textRenderer, Component.literal("Current Ability"), x + 12, y + 10, MenuStyle.TEXT);
        context.drawTextWithShadow(
            this.textRenderer,
            Component.literal(ProgressionClientState.getSelectedAbilityName()),
            x + 12,
            y + 34,
            MenuStyle.ACCENT
        );
        int wrappedHeight = MenuStyle.drawWrappedText(
            context,
            this.textRenderer,
            ProgressionClientState.getSelectedAbilityDescription(),
            x + 12,
            y + 52,
            width - 24,
            MenuStyle.MUTED,
            3
        );
        context.drawTextWithShadow(
            this.textRenderer,
            Component.literal("Abilities unlocked: " + ProgressionClientState.getAbilityCount()),
            x + 12,
            y + 58 + wrappedHeight,
            MenuStyle.TEXT
        );
        MenuStyle.drawWrappedText(
            context,
            this.textRenderer,
            "TAB cycles, R uses, X consumes held mob ability items. " + GuiCopy.PRIMARY,
            x + 12,
            y + 76 + wrappedHeight,
            width - 24,
            MenuStyle.MUTED,
            3
        );
    }

    private void renderStatsPanel(GuiGraphics context, int x, int y, int width) {
        context.drawTextWithShadow(this.textRenderer, Component.literal("Run Stats"), x + 12, y + 10, MenuStyle.TEXT);

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

    private void renderUnlockPanel(GuiGraphics context, int x, int y, int width) {
        context.drawTextWithShadow(this.textRenderer, Component.literal("Next Unlocks"), x + 12, y + 10, MenuStyle.TEXT);

        if (ProgressionClientState.getNextUnlockNames().isEmpty()) {
            MenuStyle.drawWrappedText(
                context,
                this.textRenderer,
                GuiCopy.FULL,
                x + 12,
                y + 34,
                width - 24,
                MenuStyle.MUTED,
                5
            );
            return;
        }

        for (int i = 0; i < ProgressionClientState.getNextUnlockNames().size(); i++) {
            int rowY = y + 30 + i * 30;
            context.drawTextWithShadow(
                this.textRenderer,
                Component.literal(ProgressionClientState.getNextUnlockNames().get(i)),
                x + 12,
                rowY,
                MenuStyle.ACCENT_GOLD
            );
            context.drawTextWithShadow(
                this.textRenderer,
                Component.literal(ProgressionClientState.getNextUnlockDescriptions().get(i)),
                x + 12,
                rowY + 14,
                MenuStyle.MUTED
            );
        }
    }

    private void drawMetric(GuiGraphics context, int x, int y, String label, String value) {
        context.drawTextWithShadow(this.textRenderer, Component.literal(label), x, y, MenuStyle.MUTED);
        context.drawTextWithShadow(this.textRenderer, Component.literal(value), x + 74, y, MenuStyle.TEXT);
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
