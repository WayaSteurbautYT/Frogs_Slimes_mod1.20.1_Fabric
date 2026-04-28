package com.wayacreate.frogslimegamemode.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

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

    public static void drawFrame(DrawContext context, int width, int height) {
        context.fill(0, 0, width, height, BACKGROUND);
        context.fill(14, 14, width - 14, height - 14, PANEL);
        context.drawBorder(14, 14, width - 28, height - 28, BORDER);
        context.fill(24, 24, width - 24, 28, ACCENT);
    }

    public static void drawPanel(DrawContext context, int x, int y, int width, int height, boolean alternate) {
        context.fill(x, y, x + width, y + height, alternate ? PANEL_ALT : PANEL);
        context.drawBorder(x, y, width, height, BORDER);
    }

    public static void drawHeader(DrawContext context, TextRenderer renderer, String title, String subtitle, int width) {
        context.drawCenteredTextWithShadow(renderer, Text.literal(title), width / 2, 34, TEXT);
        context.drawCenteredTextWithShadow(renderer, Text.literal(subtitle), width / 2, 48, MUTED);
    }

    public static void drawProgressBar(DrawContext context, int x, int y, int width, int height, float progress, int fillColor) {
        context.fill(x, y, x + width, y + height, 0xAA0A101A);
        context.drawBorder(x, y, width, height, BORDER);
        int filled = Math.max(0, Math.min(width, (int) (width * progress)));
        if (filled > 0) {
            context.fill(x + 1, y + 1, x + filled - 1, y + height - 1, fillColor);
        }
    }
}
