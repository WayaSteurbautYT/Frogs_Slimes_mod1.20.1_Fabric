package com.wayacreate.frogslimegamemode.achievements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class AchievementToast {
    private static Component currentTitle = null;
    private static Component currentDescription = null;
    private static ItemStack currentIcon = ItemStack.EMPTY;
    private static long displayStartTime = 0;
    private static final int DISPLAY_DURATION = 3000; // 3 seconds
    
    public static void show(Component title, Component description, ItemStack icon) {
        currentTitle = title;
        currentDescription = description;
        currentIcon = icon == null ? ItemStack.EMPTY : icon.copy();
        displayStartTime = System.currentTimeMillis();
    }
    
    public static void render(GuiGraphics context, Minecraft client) {
        if (currentTitle == null || currentDescription == null) return;
        
        long elapsed = System.currentTimeMillis() - displayStartTime;
        if (elapsed > DISPLAY_DURATION) {
            currentTitle = null;
            currentDescription = null;
            currentIcon = ItemStack.EMPTY;
            return;
        }
        
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        int margin = Math.max(8, Math.min(screenWidth, screenHeight) / 30);
        int maxTextWidth = Math.max(120, screenWidth / 4);
        List<FormattedCharSequence> descriptionLines = client.textRenderer.wrapLines(currentDescription, maxTextWidth);
        int lineCount = Math.max(1, Math.min(3, descriptionLines.size()));
        int toastWidth = Math.min(Math.max(200, maxTextWidth + 46), screenWidth - margin * 2);
        int toastHeight = 30 + lineCount * 12;
        int x = screenWidth - toastWidth - margin;
        int y = margin;
        
        // Draw background
        context.fill(x, y, x + toastWidth, y + toastHeight, 0x80000000);
        context.fill(x, y, x + toastWidth, y + 1, 0xFF00AA00); // Green border top
        
        if (!currentIcon.isEmpty()) {
            context.drawItem(currentIcon, x + 8, y + 10);
        }

        // Draw title
        context.drawTextWithShadow(
            client.textRenderer,
            currentTitle,
            x + 34, y + 7,
            0xFFFFFF
        );
        
        // Draw description
        for (int i = 0; i < lineCount; i++) {
            context.drawTextWithShadow(
                client.textRenderer,
                descriptionLines.get(i),
                x + 34,
                y + 19 + i * 12,
                0xAAAAAA
            );
        }
    }
    
    public static boolean isDisplaying() {
        return currentTitle != null && currentDescription != null;
    }
}
