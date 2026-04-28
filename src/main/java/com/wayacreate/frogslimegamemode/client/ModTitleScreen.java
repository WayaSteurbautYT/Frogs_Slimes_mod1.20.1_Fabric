package com.wayacreate.frogslimegamemode.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import com.wayacreate.frogslimegamemode.client.gui.GuiCopy;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class ModTitleScreen extends TitleScreen {

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        // Render the vanilla background
        this.renderBackground(context);

        // Draw custom title with Minecraft-style formatting
        Font textRenderer = this.textRenderer;
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Hide the vanilla Minecraft title by rendering over it with background
        // The vanilla title is typically at around (centerX, centerY - 30) with scale 2.0

        // Main title - "FROGS & SLIMES" in gold with 3D shadow effect
        Component titleText = Component.literal("LDLIB2")
            .formatted(ChatFormatting.GOLD, ChatFormatting.BOLD);

        float titleScale = Math.max(1.1f, Math.min(1.65f, this.width / 360.0f));
        int titleY = Math.max(26, this.height / 6);
        int titleWidth = textRenderer.getWidth(titleText);

        context.getMatrices().push();
        context.getMatrices().translate(centerX, titleY, 0);
        context.getMatrices().scale(titleScale, titleScale, 1.0f);

        // Draw 3D shadow layers (darker gold shades)
        int shadowOffset = 2;
        context.drawText(textRenderer, titleText, -titleWidth / 2 + shadowOffset, shadowOffset, 0x8B6914, false); // Dark gold shadow
        context.drawText(textRenderer, titleText, -titleWidth / 2 + shadowOffset * 2, shadowOffset * 2, 0x5C4510, false); // Even darker
        context.drawText(textRenderer, titleText, -titleWidth / 2 + shadowOffset * 3, shadowOffset * 3, 0x3D2A0B, false); // Darkest

        // Main title text
        context.drawText(textRenderer, titleText, -titleWidth / 2, 0, 0xFFAA00, false);
        context.getMatrices().pop();

        // Subtitle - "GAMEMODE" in green with shadow
        Component subtitleText = Component.literal("YOGA LAYOUT UI")
            .formatted(ChatFormatting.GREEN, ChatFormatting.BOLD);

        float subtitleScale = Math.max(0.9f, Math.min(1.2f, this.width / 440.0f));
        int subtitleY = titleY + Math.max(24, (int) (24 * titleScale));
        int subtitleWidth = textRenderer.getWidth(subtitleText);

        context.getMatrices().push();
        context.getMatrices().translate(centerX, subtitleY, 0);
        context.getMatrices().scale(subtitleScale, subtitleScale, 1.0f);

        // Shadow for subtitle
        context.drawText(textRenderer, subtitleText, -subtitleWidth / 2 + 1, 1, 0x2E8B57, false);
        context.drawText(textRenderer, subtitleText, -subtitleWidth / 2 + 2, 2, 0x1A5C3A, false);

        // Main subtitle text
        context.drawText(textRenderer, subtitleText, -subtitleWidth / 2, 0, 0x55FF55, false);
        context.getMatrices().pop();

        // Author text - "by WayaCreate" in gray
        Component authorText = Component.literal("by WayaCreate")
            .formatted(ChatFormatting.GRAY);

        float authorScale = 0.9f;
        int authorY = subtitleY + 20;
        int authorWidth = textRenderer.getWidth(authorText);

        context.getMatrices().push();
        context.getMatrices().translate(centerX, authorY, 0);
        context.getMatrices().scale(authorScale, authorScale, 1.0f);
        context.drawText(textRenderer, authorText, -authorWidth / 2, 0, 0xAAAAAA, true);
        context.getMatrices().pop();

        // Gamemode status indicator
        Component statusText = Component.literal(GuiCopy.PRIMARY)
            .formatted(ChatFormatting.AQUA, ChatFormatting.ITALIC);

        float statusScale = Math.max(0.65f, Math.min(0.85f, this.width / 700.0f));
        int statusY = authorY + 20;
        int statusWidth = textRenderer.getWidth(statusText);

        context.getMatrices().push();
        context.getMatrices().translate(centerX, statusY, 0);
        context.getMatrices().scale(statusScale, statusScale, 1.0f);
        context.drawText(textRenderer, statusText, -statusWidth / 2, 0, 0x55FFFF, true);
        context.getMatrices().pop();

        // Version text at bottom
        Component versionText = Component.literal("v1.0.0 - Fabric 1.20.1")
            .formatted(ChatFormatting.DARK_GRAY);

        context.drawCenteredTextWithShadow(textRenderer, versionText, centerX, this.height - 30, 0x555555);

        // Render the vanilla buttons and other elements
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void init() {
        // Call parent to initialize buttons
        super.init();

        // Move all buttons down and adjust horizontal position if needed
        this.children().forEach(widget -> {
            if (widget instanceof net.minecraft.client.gui.components.AbstractWidget) {
                net.minecraft.client.gui.components.AbstractWidget button = (net.minecraft.client.gui.components.AbstractWidget) widget;
                button.setY(button.getY() + 40);

                // Ensure buttons are within screen bounds horizontally
                if (button.getX() < 10) {
                    button.setX(10);
                }
                if (button.getX() + button.getWidth() > this.width - 10) {
                    button.setX(this.width - 10 - button.getWidth());
                }
            }
        });
    }
}
