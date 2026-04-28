package com.wayacreate.frogslimegamemode.client.hud;

import com.wayacreate.frogslimegamemode.client.state.ProgressionClientState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class ProgressBarHud {
    private static final int BAR_WIDTH = 182;
    private static final int BAR_HEIGHT = 5;
    private static final int Y_OFFSET = 40;
    
    public static void render(DrawContext context, MinecraftClient client) {
        if (client.player == null || !ProgressionClientState.isActive()) return;
        
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        
        int x = (screenWidth - BAR_WIDTH) / 2;
        int y = screenHeight - Y_OFFSET;
        
        // Calculate progress (0.0 to 1.0)
        float progress = ProgressionClientState.getOverallProgress();
        
        // Draw background bar
        context.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, 0x80000000);
        
        // Draw progress bar (color based on progress)
        int progressWidth = (int) (BAR_WIDTH * progress);
        int color = getProgressColor(progress);
        context.fill(x, y, x + progressWidth, y + BAR_HEIGHT, color);
        
        // Draw percentage text
        String percentage = String.format("%.0f%%", progress * 100);
        int textX = x + BAR_WIDTH / 2 - client.textRenderer.getWidth(percentage) / 2;
        int textY = y - 12;
        context.drawTextWithShadow(client.textRenderer, Text.literal(percentage), textX, textY, 0xFFFFFF);
        
        // Draw label
        String label = "Journey Progress";
        int labelX = x + BAR_WIDTH / 2 - client.textRenderer.getWidth(label) / 2;
        int labelY = y + BAR_HEIGHT + 5;
        context.drawTextWithShadow(client.textRenderer, Text.literal(label), labelX, labelY, 0xAAAAAA);
    }
    
    private static int getProgressColor(float progress) {
        // Interpolate from red (0xFF0000) to green (0x00FF00)
        int red = (int) (255 * (1 - progress));
        int green = (int) (255 * progress);
        return (red << 16) | (green << 8) | 0x80;
    }
}
