package com.wayacreate.frogslimegamemode.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class MenuStyle {
    public static final int BACKGROUND = 0xF0101522;
    public static final int PANEL = 0xEE182133;
    public static final int PANEL_ALT = 0xEE212D44;
    public static final int BORDER = 0xFF4A628B;
    public static final int ACCENT = 0xFF7DB8FF;
    public static final int ACCENT_GOLD = 0xFFE8C26F;
    public static final int POSITIVE = 0xFF68D391;
    public static final int MUTED = 0xFF98A7C0;
    public static final int TEXT = 0xFFF4F7FF;
    public static final int DANGER = 0xFFF87171;

    private MenuStyle() {
    }

    public static void drawFrame(GuiGraphics context, int width, int height) {
        int frameMargin = getFrameMargin(width, height);
        context.fill(0, 0, width, height, BACKGROUND);
        context.fill(frameMargin, frameMargin, width - frameMargin, height - frameMargin, PANEL);
        context.drawBorder(frameMargin, frameMargin, width - (frameMargin * 2), height - (frameMargin * 2), BORDER);
        context.fill(frameMargin + 10, frameMargin + 10, width - frameMargin - 10, frameMargin + 14, ACCENT);
    }

    public static void drawPanel(GuiGraphics context, int x, int y, int width, int height, boolean alternate) {
        context.fill(x, y, x + width, y + height, alternate ? PANEL_ALT : PANEL);
        context.drawBorder(x, y, width, height, BORDER);
    }

    public static void drawHeader(GuiGraphics context, Font renderer, String title, String subtitle, int width) {
        drawHeader(context, renderer, title, subtitle, 0, width);
    }

    public static void drawHeader(GuiGraphics context, Font renderer, String title, String subtitle, int x, int width) {
        int centerX = x + width / 2;
        context.drawCenteredTextWithShadow(renderer, Component.literal(title), centerX, getHeaderTop(), TEXT);
        drawWrappedCenteredText(context, renderer, subtitle, centerX, getHeaderTop() + 16, Math.max(120, width - 40), MUTED, 3);
    }

    public static void drawProgressBar(GuiGraphics context, int x, int y, int width, int height, float progress, int fillColor) {
        context.fill(x, y, x + width, y + height, 0xAA0A101A);
        context.drawBorder(x, y, width, height, BORDER);
        int filled = Math.max(0, Math.min(width, (int) (width * progress)));
        if (filled > 0) {
            context.fill(x + 1, y + 1, x + filled - 1, y + height - 1, fillColor);
        }
    }

    public static int getFrameMargin(int width, int height) {
        return clamp(Math.min(width, height) / 18, 12, 28);
    }

    public static int getContentMargin(int width, int height) {
        return getFrameMargin(width, height) + 14;
    }

    public static int getHeaderTop() {
        return 30;
    }

    public static int getContentTop() {
        return 86;
    }

    public static int getBottomInset(int height) {
        return clamp(height / 22, 28, 40);
    }

    public static int getGutter(int width) {
        return clamp(width / 42, 10, 18);
    }

    public static int getButtonWidth(int width) {
        return clamp(width / 6, 84, 128);
    }

    public static int getButtonHeight() {
        return 20;
    }

    public static int drawWrappedText(GuiGraphics context, Font renderer, String text, int x, int y, int maxWidth, int color, int maxLines) {
        List<FormattedCharSequence> lines = renderer.wrapLines(Component.literal(text), maxWidth);
        int drawn = 0;
        for (FormattedCharSequence line : lines) {
            if (drawn >= maxLines) {
                break;
            }
            context.drawTextWithShadow(renderer, line, x, y + drawn * 12, color);
            drawn++;
        }
        return drawn * 12;
    }

    public static int drawWrappedCenteredText(GuiGraphics context, Font renderer, String text, int centerX, int y, int maxWidth, int color, int maxLines) {
        List<FormattedCharSequence> lines = renderer.wrapLines(Component.literal(text), maxWidth);
        int drawn = 0;
        for (FormattedCharSequence line : lines) {
            if (drawn >= maxLines) {
                break;
            }
            int lineWidth = renderer.getWidth(line);
            context.drawTextWithShadow(renderer, line, centerX - lineWidth / 2, y + drawn * 12, color);
            drawn++;
        }
        return drawn * 12;
    }

    public static int getWrappedHeight(Font renderer, String text, int maxWidth, int maxLines) {
        return Math.min(renderer.wrapLines(Component.literal(text), maxWidth).size(), maxLines) * 12;
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
