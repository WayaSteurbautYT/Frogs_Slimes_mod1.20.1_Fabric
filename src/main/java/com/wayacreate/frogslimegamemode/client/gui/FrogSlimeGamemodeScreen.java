package com.wayacreate.frogslimegamemode.client.gui;

import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import com.wayacreate.frogslimegamemode.gamemode.GamemodeData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class FrogSlimeGamemodeScreen extends Screen {
    private final PlayerEntity player;
    
    public FrogSlimeGamemodeScreen(PlayerEntity player) {
        super(Text.literal("Frog & Slime Gamemode"));
        this.player = player;
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        // Title
        this.addDrawableChild(new ButtonWidget.Builder(
            Text.literal("Close"),
            button -> this.close()
        ).dimensions(centerX - 50, centerY + 100, 100, 20).build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        // Title
        context.drawCenteredTextWithShadow(this.textRenderer, 
            Text.literal("Frog & Slime Gamemode").formatted(Formatting.BOLD, Formatting.GOLD),
            centerX, centerY - 80, 0xFFFFFF);
        
        // Gamemode status
        boolean inGamemode = GamemodeManager.isInGamemode(player);
        String statusText = inGamemode ? "Active" : "Inactive";
        Formatting statusColor = inGamemode ? Formatting.GREEN : Formatting.RED;
        
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("Status: ").append(Text.literal(statusText).formatted(statusColor)),
            centerX, centerY - 50, 0xFFFFFF);
        
        if (inGamemode) {
            GamemodeData data = GamemodeManager.getData(player);
            
            // Stats
            context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Helpers Spawned: " + data.getHelpersSpawned()),
                centerX, centerY - 20, 0xFFFFFF);
            
            context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Mobs Eaten: " + data.getMobsEaten()),
                centerX, centerY, 0xFFFFFF);
            
            context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Items Collected: " + data.getItemsCollected()),
                centerX, centerY + 20, 0xFFFFFF);
            
            context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Deaths: " + data.getDeathCount()),
                centerX, centerY + 40, 0xFFFFFF);
            
            context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Jumps: " + data.getJumpCount()),
                centerX, centerY + 60, 0xFFFFFF);
        } else {
            context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Use /frogslime start to begin!").formatted(Formatting.YELLOW),
                centerX, centerY, 0xFFFFFF);
        }
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
}
