package com.wayacreate.frogslimegamemode.client.gui;

import com.wayacreate.frogslimegamemode.client.state.ProgressionClientState;
import com.wayacreate.frogslimegamemode.progression.ProgressionUnlock;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CollectionsScreen extends Screen {
    private static final String[] CATEGORIES = {"Items", "Abilities", "Recipes", "Roles"};

    private int currentCategory;

    public CollectionsScreen() {
        super(Text.literal("Unlock Library"));
    }

    @Override
    protected void init() {
        for (int i = 0; i < CATEGORIES.length; i++) {
            int index = i;
            this.addDrawableChild(ButtonWidget.builder(Text.literal(CATEGORIES[i]), button -> currentCategory = index)
                .dimensions(28 + i * 92, 36, 84, 20)
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
            "Unlock Library",
            "Level and evolution gates for the full route",
            this.width
        );

        MenuStyle.drawPanel(context, 28, 72, this.width - 56, this.height - 118, currentCategory % 2 == 1);
        renderUnlocks(context);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderUnlocks(DrawContext context) {
        List<ProgressionUnlock.Unlock> unlocks = getUnlocksForCurrentCategory();
        int unlocked = 0;
        for (ProgressionUnlock.Unlock unlock : unlocks) {
            if (isUnlocked(unlock)) {
                unlocked++;
            }
        }

        int x = 40;
        int y = 88;
        context.drawTextWithShadow(
            this.textRenderer,
            Text.literal(CATEGORIES[currentCategory] + " unlocked: " + unlocked + "/" + unlocks.size()),
            x,
            y,
            MenuStyle.ACCENT_GOLD
        );
        float progress = unlocks.isEmpty() ? 0.0f : unlocked / (float) unlocks.size();
        MenuStyle.drawProgressBar(context, x + 156, y + 2, this.width - 244, 10, progress, MenuStyle.POSITIVE);

        int columnWidth = (this.width - 98) / 2;
        int rowY = y + 28;
        for (int i = 0; i < unlocks.size(); i++) {
            int column = i % 2;
            int row = i / 2;
            int boxX = x + column * (columnWidth + 10);
            int boxY = rowY + row * 38;
            ProgressionUnlock.Unlock unlock = unlocks.get(i);
            boolean unlockedEntry = isUnlocked(unlock);

            MenuStyle.drawPanel(context, boxX, boxY, columnWidth, 30, (row + column) % 2 == 1);
            context.drawTextWithShadow(
                this.textRenderer,
                Text.literal(unlock.getName()),
                boxX + 8,
                boxY + 6,
                unlockedEntry ? MenuStyle.POSITIVE : MenuStyle.TEXT
            );
            context.drawTextWithShadow(
                this.textRenderer,
                Text.literal("Lv " + unlock.getRequiredLevel() + " / Evo " + unlock.getRequiredEvolutionStage()),
                boxX + 8,
                boxY + 17,
                MenuStyle.ACCENT
            );
            context.drawTextWithShadow(
                this.textRenderer,
                Text.literal(unlockedEntry ? "Ready" : "Locked"),
                boxX + columnWidth - 36,
                boxY + 11,
                unlockedEntry ? MenuStyle.POSITIVE : MenuStyle.DANGER
            );
        }

        context.drawTextWithShadow(
            this.textRenderer,
            Text.literal("Current level " + ProgressionClientState.getLevel() + " | Evolution stage " + ProgressionClientState.getHighestEvolutionStage()),
            x,
            this.height - 72,
            MenuStyle.MUTED
        );
    }

    private List<ProgressionUnlock.Unlock> getUnlocksForCurrentCategory() {
        List<ProgressionUnlock.Unlock> unlocks = new ArrayList<>();
        for (ProgressionUnlock.Unlock unlock : ProgressionUnlock.getAllUnlocks().values()) {
            if (matchesCategory(unlock.getType())) {
                unlocks.add(unlock);
            }
        }
        unlocks.sort(Comparator
            .comparingInt(ProgressionUnlock.Unlock::getRequiredLevel)
            .thenComparingInt(ProgressionUnlock.Unlock::getRequiredEvolutionStage)
            .thenComparing(ProgressionUnlock.Unlock::getName));
        return unlocks;
    }

    private boolean matchesCategory(ProgressionUnlock.UnlockType type) {
        return switch (currentCategory) {
            case 0 -> type == ProgressionUnlock.UnlockType.ITEM || type == ProgressionUnlock.UnlockType.POTION;
            case 1 -> type == ProgressionUnlock.UnlockType.ABILITY;
            case 2 -> type == ProgressionUnlock.UnlockType.RECIPE;
            default -> type == ProgressionUnlock.UnlockType.JOB_CLASS;
        };
    }

    private boolean isUnlocked(ProgressionUnlock.Unlock unlock) {
        return ProgressionUnlock.isUnlocked(
            unlock.getId(),
            ProgressionClientState.getLevel(),
            ProgressionClientState.getHighestEvolutionStage()
        );
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
