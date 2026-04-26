package com.wayacreate.frogslimegamemode.client;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ModTitleScreen extends TitleScreen {
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Render the vanilla background
        this.renderBackground(context);
        
        // Draw custom title with Minecraft-style formatting
        TextRenderer textRenderer = this.textRenderer;
        int centerX = this.width / 2;
        
        // Main title - "FROGS & SLIMES" in gold with shadow
        Text titleText = Text.literal("FROGS & SLIMES")
            .formatted(Formatting.GOLD, Formatting.BOLD);
        
        context.drawCenteredTextWithShadow(textRenderer, titleText, centerX, 80, 0xFFAA00);
        
        // Subtitle - "GAMEMODE" in green
        Text subtitleText = Text.literal("GAMEMODE")
            .formatted(Formatting.GREEN, Formatting.BOLD);
        
        context.drawCenteredTextWithShadow(textRenderer, subtitleText, centerX, 100, 0x55FF55);
        
        // Author text - "by WayaCreate" in gray
        Text authorText = Text.literal("by WayaCreate")
            .formatted(Formatting.GRAY);
        
        context.drawCenteredTextWithShadow(textRenderer, authorText, centerX, 120, 0xAAAAAA);
        
        // Version text
        Text versionText = Text.literal("v1.0.0 - Fabric 1.20.1")
            .formatted(Formatting.DARK_GRAY);
        
        context.drawCenteredTextWithShadow(textRenderer, versionText, centerX, this.height - 30, 0x555555);
        
        // Render the vanilla buttons and other elements
        super.render(context, mouseX, mouseY, delta);
    }
}
