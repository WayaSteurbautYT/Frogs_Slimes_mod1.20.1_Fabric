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
    private int scrollOffset = 0;
    private static final int LINE_HEIGHT = 20;
    private static final int MAX_VISIBLE_LINES = 12;
    
    public FrogSlimeGamemodeScreen(PlayerEntity player) {
        super(Text.literal("Frog & Slime Gamemode"));
        this.player = player;
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int buttonY = Math.min(this.height - 40, this.height / 2 + 120);
        
        // Close button
        this.addDrawableChild(new ButtonWidget.Builder(
            Text.literal("Close"),
            button -> this.close()
        ).dimensions(centerX - 50, buttonY, 100, 20).build());
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (amount < 0 && scrollOffset > 0) {
            scrollOffset--;
        } else if (amount > 0) {
            scrollOffset++;
        }
        return true;
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        
        // Draw dark background panel
        context.fill(this.width / 2 - 110, this.height / 2 - 110, this.width / 2 + 110, this.height / 2 + 110, 0xC0101010);
        context.drawBorder(this.width / 2 - 110, this.height / 2 - 110, 220, 220, 0xFF000000);
        
        int centerX = this.width / 2;
        int startY = this.height / 2 - 100;
        int currentLine = 0;
        
        // Title
        context.drawCenteredTextWithShadow(this.textRenderer, 
            Text.literal("Frog & Slime Gamemode").formatted(Formatting.BOLD, Formatting.GOLD),
            centerX, startY + (currentLine++ * LINE_HEIGHT), 0xFFFFFF);
        
        currentLine++; // Spacer
        
        // Gamemode status
        boolean inGamemode = GamemodeManager.isInGamemode(player);
        String statusText = inGamemode ? "Active" : "Inactive";
        Formatting statusColor = inGamemode ? Formatting.GREEN : Formatting.RED;
        
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("Status: ").append(Text.literal(statusText).formatted(statusColor)),
            centerX, startY + (currentLine++ * LINE_HEIGHT), 0xFFFFFF);
        
        currentLine++; // Spacer
        
        if (inGamemode) {
            GamemodeData data = GamemodeManager.getData(player);
            
            // Stats with scroll support
            String[] stats = {
                "Helpers Spawned: " + data.getHelpersSpawned(),
                "Mobs Eaten: " + data.getMobsEaten(),
                "Items Collected: " + data.getItemsCollected(),
                "Deaths: " + data.getDeathCount(),
                "Jumps: " + data.getJumpCount(),
                "Abilities: " + data.getPlayerAbilities().size(),
                "Play Time: " + (data.getTicksActive() / 20) + "s"
            };
            
            for (int i = 0; i < stats.length; i++) {
                int displayIndex = i - scrollOffset;
                if (displayIndex >= 0 && displayIndex < MAX_VISIBLE_LINES) {
                    context.drawCenteredTextWithShadow(this.textRenderer,
                        Text.literal(stats[i]),
                        centerX, startY + ((currentLine + displayIndex) * LINE_HEIGHT), 0xFFFFFF);
                }
            }
            
            // Show scroll indicator if needed
            if (stats.length > MAX_VISIBLE_LINES) {
                context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal("Scroll for more...").formatted(Formatting.GRAY),
                    centerX, startY + ((currentLine + MAX_VISIBLE_LINES) * LINE_HEIGHT), 0xFFFFFF);
            }
        } else {
            context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Use /frogslime start to begin!").formatted(Formatting.YELLOW),
                centerX, startY + (currentLine++ * LINE_HEIGHT), 0xFFFFFF);
        }
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
}
