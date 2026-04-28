package com.wayacreate.frogslimegamemode.achievements;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class AchievementToast {
    private static Text currentTitle = null;
    private static Text currentDescription = null;
    private static ItemStack currentIcon = ItemStack.EMPTY;
    private static long displayStartTime = 0;
    private static final int DISPLAY_DURATION = 3000; // 3 seconds
    
    public static void show(Text title, Text description, ItemStack icon) {
        currentTitle = title;
        currentDescription = description;
        currentIcon = icon == null ? ItemStack.EMPTY : icon.copy();
        displayStartTime = System.currentTimeMillis();
    }
    
    public static void render(DrawContext context, MinecraftClient client) {
        if (currentTitle == null || currentDescription == null) return;
        
        long elapsed = System.currentTimeMillis() - displayStartTime;
        if (elapsed > DISPLAY_DURATION) {
            currentTitle = null;
            currentDescription = null;
            currentIcon = ItemStack.EMPTY;
            return;
        }
        
        int screenWidth = client.getWindow().getScaledWidth();
        
        int toastWidth = 220;
        int toastHeight = 36;
        int x = screenWidth - toastWidth - 10;
        int y = 10;
        
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
        context.drawTextWithShadow(
            client.textRenderer,
            currentDescription,
            x + 34, y + 19,
            0xAAAAAA
        );
    }
    
    public static boolean isDisplaying() {
        return currentTitle != null && currentDescription != null;
    }
}
