package com.wayacreate.frogslimegamemode.client.hud;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

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
                
                // Show gamemode status
                String text = "Frog & Slime Gamemode ACTIVE";
                int x = client.getWindow().getScaledWidth() / 2 - textRenderer.getWidth(text) / 2;
                int y = 10;
                
                drawContext.drawText(textRenderer, Text.literal(text), x, y, 0x00FF00, true);
                
                // Show mob level when looking at a mob
                if (client.player != null && client.world != null) {
                    Entity target = getEntityPlayerIsLookingAt(client);
                    if (target instanceof MobEntity mob) {
                        int mobLevel = calculateMobLevel(mob);
                        Text levelText = Text.literal("Level: " + mobLevel)
                            .formatted(Formatting.GREEN, Formatting.BOLD);
                        
                        int levelX = client.getWindow().getScaledWidth() / 2 - textRenderer.getWidth(levelText) / 2;
                        int levelY = 25;
                        
                        drawContext.drawText(textRenderer, levelText, levelX, levelY, 0xFFFFFF, true);
                    }
                }
                
                // Render progress bar
                ProgressBarHud.render(drawContext, client);
                
                // Render achievement toast
                com.wayacreate.frogslimegamemode.achievements.AchievementToast.render(drawContext, client);
            }
        });
        
        FrogSlimeGamemode.LOGGER.info("Gamemode HUD initialized");
    }

    private static Entity getEntityPlayerIsLookingAt(MinecraftClient client) {
        if (client.player == null || client.world == null) return null;
        
        HitResult hit = client.player.raycast(10.0, 0.0f, false);
        if (hit.getType() == HitResult.Type.ENTITY) {
            return ((EntityHitResult) hit).getEntity();
        }
        return null;
    }
    
    private static int calculateMobLevel(MobEntity mob) {
        // Calculate level based on max health
        double maxHealth = mob.getMaxHealth();
        
        // Base level calculation: 1 health = 1 level, scaled
        int level = (int) Math.max(1, Math.floor(maxHealth / 2.0));
        
        // Adjust for special mobs
        if (mob.getType().getSpawnGroup().getName().equals("monster")) {
            level += 2; // Hostile mobs are stronger
        }
        
        // Cap level at reasonable range
        return Math.min(level, 50);
    }

    public static void setGamemodeActive(boolean active) {
        gamemodeActive = active;
    }

    public static boolean isGamemodeActive() {
        return gamemodeActive;
    }
}
