package com.wayacreate.frogslimegamemode.client.hud;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import com.wayacreate.frogslimegamemode.client.state.ManhuntClientState;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ManhuntHud {
    private static boolean initialized = false;

    public static void onInitializeClient() {
        if (initialized) return;
        initialized = true;
        
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;
            
            // Check if player is in a manhunt game
            if (!ManhuntClientState.isActive()) return;
            
            TextRenderer textRenderer = client.textRenderer;
            int screenWidth = client.getWindow().getScaledWidth();
            
            if (ManhuntClientState.isSpeedrunner()) {
                renderSpeedrunnerHud(drawContext, textRenderer, screenWidth);
            } else if (ManhuntClientState.isHunter()) {
                renderHunterHud(drawContext, textRenderer, screenWidth);
            }
        });
        
        FrogSlimeGamemode.LOGGER.info("Manhunt HUD initialized");
    }
    
    private static void renderSpeedrunnerHud(net.minecraft.client.gui.DrawContext context, TextRenderer textRenderer, int width) {
        int y = 10;
        int x = width - 190;
        context.fill(x - 8, y - 6, width - 10, y + 82, 0xB0121826);
        context.drawBorder(x - 8, y - 6, 188, 88, 0xFF5D79A6);
        
        context.drawText(textRenderer, 
            Text.literal("SPEEDRUNNER").formatted(Formatting.GREEN, Formatting.BOLD).getString(), 
            x, y, 0x00FF00, false);
        y += 15;
        
        context.drawText(textRenderer, 
            Text.literal("Time: " + ManhuntClientState.getElapsedTime()).formatted(Formatting.YELLOW).getString(), 
            x, y, 0xFFFF00, false);
        y += 15;
        
        context.drawText(textRenderer, 
            Text.literal("Deaths: " + ManhuntClientState.getDeathCount() + "/3").formatted(Formatting.RED).getString(), 
            x, y, 0xFF0000, false);
        y += 15;

        context.drawText(textRenderer,
            Text.literal("Selected: " + ManhuntClientState.getSelectedAbilityName()).formatted(Formatting.AQUA).getString(),
            x, y, 0x66FFFF, false);
        y += 12;

        context.drawText(textRenderer,
            Text.literal(ManhuntClientState.getSelectedAbilityDescription()).formatted(Formatting.GRAY).getString(),
            x, y, 0xAAAAAA, false);

        renderCooldowns(context, textRenderer, x, y + 18, true);
    }
    
    private static void renderHunterHud(net.minecraft.client.gui.DrawContext context, TextRenderer textRenderer, int width) {
        int y = 10;
        int x = width - 190;
        context.fill(x - 8, y - 6, width - 10, y + 82, 0xB0121826);
        context.drawBorder(x - 8, y - 6, 188, 88, 0xFF8D5C5C);
        
        context.drawText(textRenderer, 
            Text.literal("HUNTER").formatted(Formatting.RED, Formatting.BOLD).getString(), 
            x, y, 0xFF0000, false);
        y += 15;
        
        context.drawText(textRenderer, 
            Text.literal("Target: " + ManhuntClientState.getTargetName()).formatted(Formatting.GRAY).getString(), 
            x, y, 0xAAAAAA, false);
        y += 15;

        context.drawText(textRenderer,
            Text.literal("Selected: " + ManhuntClientState.getSelectedAbilityName()).formatted(Formatting.AQUA).getString(),
            x, y, 0x66FFFF, false);
        y += 12;

        context.drawText(textRenderer,
            Text.literal(ManhuntClientState.getSelectedAbilityDescription()).formatted(Formatting.GRAY).getString(),
            x, y, 0xAAAAAA, false);

        renderCooldowns(context, textRenderer, x, y + 18, false);
    }
    
    private static void renderCooldowns(net.minecraft.client.gui.DrawContext context, TextRenderer textRenderer, int x, int y, boolean isSpeedrunner) {
        if (isSpeedrunner) {
            context.drawText(textRenderer, 
                abilityLine(0, "Escape", ManhuntClientState.getSpeedrunnerEscapeCooldown()).getString(),
                x, y, lineColor(0, ManhuntClientState.getSpeedrunnerEscapeCooldown()), false);
            y += 12;
            context.drawText(textRenderer, 
                abilityLine(1, "Burst", ManhuntClientState.getSpeedrunnerSpeedCooldown()).getString(),
                x, y, lineColor(1, ManhuntClientState.getSpeedrunnerSpeedCooldown()), false);
            y += 12;
            context.drawText(textRenderer, 
                abilityLine(2, "Veil", ManhuntClientState.getSpeedrunnerInvisCooldown()).getString(),
                x, y, lineColor(2, ManhuntClientState.getSpeedrunnerInvisCooldown()), false);
        } else {
            context.drawText(textRenderer, 
                abilityLine(0, "Track", ManhuntClientState.getHunterTrackCooldown()).getString(),
                x, y, lineColor(0, ManhuntClientState.getHunterTrackCooldown()), false);
            y += 12;
            context.drawText(textRenderer, 
                abilityLine(1, "Blockade", ManhuntClientState.getHunterBlockCooldown()).getString(),
                x, y, lineColor(1, ManhuntClientState.getHunterBlockCooldown()), false);
            y += 12;
            context.drawText(textRenderer, 
                abilityLine(2, "Snare", ManhuntClientState.getHunterSlowCooldown()).getString(),
                x, y, lineColor(2, ManhuntClientState.getHunterSlowCooldown()), false);
        }
    }
    
    private static Text abilityLine(int index, String name, int cooldown) {
        boolean selected = ManhuntClientState.getSelectedIndex() == index;
        String prefix = selected ? "> " : "  ";
        String cooldownText = cooldown > 0 ? " (" + (cooldown / 20) + "s)" : "";
        return Text.literal(prefix + name + cooldownText)
            .formatted(cooldown > 0 ? Formatting.RED : selected ? Formatting.AQUA : Formatting.GRAY);
    }

    private static int lineColor(int index, int cooldown) {
        if (cooldown > 0) {
            return 0xFF7777;
        }
        return ManhuntClientState.getSelectedIndex() == index ? 0x66FFFF : 0xAAAAAA;
    }
}
