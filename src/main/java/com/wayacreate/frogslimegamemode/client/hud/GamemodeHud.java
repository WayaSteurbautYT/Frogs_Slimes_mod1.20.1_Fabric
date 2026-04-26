package com.wayacreate.frogslimegamemode.client.hud;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

public class GamemodeHud {
    private static boolean gamemodeActive = false;
    private static boolean initialized = false;

    public static void onInitializeClient() {
        if (initialized) return;
        initialized = true;
        
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (gamemodeActive) {
                MinecraftClient client = MinecraftClient.getInstance();
                TextRenderer textRenderer = client.textRenderer;
                
                String text = "Frog & Slime Gamemode ACTIVE";
                int x = client.getWindow().getScaledWidth() / 2 - textRenderer.getWidth(text) / 2;
                int y = 10;
                
                drawContext.drawText(textRenderer, Text.literal(text), x, y, 0x00FF00, true);
            }
        });
        
        FrogSlimeGamemode.LOGGER.info("Gamemode HUD initialized");
    }

    public static void setGamemodeActive(boolean active) {
        gamemodeActive = active;
    }

    public static boolean isGamemodeActive() {
        return gamemodeActive;
    }
}
