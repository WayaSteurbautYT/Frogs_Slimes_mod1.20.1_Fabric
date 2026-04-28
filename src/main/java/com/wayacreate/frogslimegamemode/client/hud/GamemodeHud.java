package com.wayacreate.frogslimegamemode.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

public class GamemodeHud {
    private static boolean gamemodeActive = false;

    public static void onRender(RenderGuiEvent.Post event) {
        var drawContext = event.getGuiGraphics();
        Minecraft client = Minecraft.getInstance();

        if (gamemodeActive) {
            Font textRenderer = client.textRenderer;
            String text = "Frog & Slime Gamemode ACTIVE";
            int x = client.getWindow().getScaledWidth() / 2 - textRenderer.getWidth(text) / 2;
            int y = 10;

            drawContext.drawText(textRenderer, Component.literal(text), x, y, 0x00FF00, true);

            if (client.player != null && client.world != null) {
                Entity target = getEntityPlayerIsLookingAt(client);
                if (target instanceof Mob mob) {
                    int mobLevel = calculateMobLevel(mob);
                    Component levelText = Component.literal("Level: " + mobLevel)
                        .formatted(ChatFormatting.GREEN, ChatFormatting.BOLD);

                    int levelX = client.getWindow().getScaledWidth() / 2 - textRenderer.getWidth(levelText) / 2;
                    int levelY = 25;
                    drawContext.drawText(textRenderer, levelText, levelX, levelY, 0xFFFFFF, true);
                }
            }

            ProgressBarHud.render(drawContext, client);
        }

        com.wayacreate.frogslimegamemode.achievements.AchievementToast.render(drawContext, client);
    }

    private static Entity getEntityPlayerIsLookingAt(Minecraft client) {
        if (client.player == null || client.world == null) {
            return null;
        }

        HitResult hit = client.player.raycast(10.0, 0.0f, false);
        if (hit.getType() == HitResult.Type.ENTITY) {
            return ((EntityHitResult) hit).getEntity();
        }
        return null;
    }

    private static int calculateMobLevel(Mob mob) {
        double maxHealth = mob.getMaxHealth();
        int level = (int) Math.max(1, Math.floor(maxHealth / 2.0));

        if (mob.getType().getSpawnGroup().getName().equals("monster")) {
            level += 2;
        }

        return Math.min(level, 50);
    }

    public static void setGamemodeActive(boolean active) {
        gamemodeActive = active;
    }

    public static boolean isGamemodeActive() {
        return gamemodeActive;
    }
}
