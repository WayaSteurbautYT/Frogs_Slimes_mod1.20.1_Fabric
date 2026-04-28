package com.wayacreate.frogslimegamemode.client.hud;

import com.wayacreate.frogslimegamemode.client.state.ManhuntClientState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

public class ManhuntHud {
    public static void onRender(RenderGuiEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || !ManhuntClientState.isActive()) {
            return;
        }

        Font textRenderer = client.textRenderer;
        int screenWidth = client.getWindow().getScaledWidth();
        var drawContext = event.getGuiGraphics();

        if (ManhuntClientState.isSpeedrunner()) {
            renderSpeedrunnerHud(drawContext, textRenderer, screenWidth);
        } else if (ManhuntClientState.isHunter()) {
            renderHunterHud(drawContext, textRenderer, screenWidth);
        }
    }

    private static void renderSpeedrunnerHud(net.minecraft.client.gui.GuiGraphics context, Font textRenderer, int width) {
        int y = 10;
        int x = width - 190;
        context.fill(x - 8, y - 6, width - 10, y + 82, 0xB0121826);
        context.drawBorder(x - 8, y - 6, 188, 88, 0xFF5D79A6);

        context.drawText(
            textRenderer,
            Component.literal("SPEEDRUNNER").formatted(ChatFormatting.GREEN, ChatFormatting.BOLD).getString(),
            x,
            y,
            0x00FF00,
            false
        );
        y += 15;

        context.drawText(
            textRenderer,
            Component.literal("Time: " + ManhuntClientState.getElapsedTime()).formatted(ChatFormatting.YELLOW).getString(),
            x,
            y,
            0xFFFF00,
            false
        );
        y += 15;

        context.drawText(
            textRenderer,
            Component.literal("Deaths: " + ManhuntClientState.getDeathCount() + "/3").formatted(ChatFormatting.RED).getString(),
            x,
            y,
            0xFF0000,
            false
        );
        y += 15;

        context.drawText(
            textRenderer,
            Component.literal("Selected: " + ManhuntClientState.getSelectedAbilityName()).formatted(ChatFormatting.AQUA).getString(),
            x,
            y,
            0x66FFFF,
            false
        );
        y += 12;

        context.drawText(
            textRenderer,
            Component.literal(ManhuntClientState.getSelectedAbilityDescription()).formatted(ChatFormatting.GRAY).getString(),
            x,
            y,
            0xAAAAAA,
            false
        );

        renderCooldowns(context, textRenderer, x, y + 18, true);
    }

    private static void renderHunterHud(net.minecraft.client.gui.GuiGraphics context, Font textRenderer, int width) {
        int y = 10;
        int x = width - 190;
        context.fill(x - 8, y - 6, width - 10, y + 82, 0xB0121826);
        context.drawBorder(x - 8, y - 6, 188, 88, 0xFF8D5C5C);

        context.drawText(
            textRenderer,
            Component.literal("HUNTER").formatted(ChatFormatting.RED, ChatFormatting.BOLD).getString(),
            x,
            y,
            0xFF0000,
            false
        );
        y += 15;

        context.drawText(
            textRenderer,
            Component.literal("Target: " + ManhuntClientState.getTargetName()).formatted(ChatFormatting.GRAY).getString(),
            x,
            y,
            0xAAAAAA,
            false
        );
        y += 15;

        context.drawText(
            textRenderer,
            Component.literal("Selected: " + ManhuntClientState.getSelectedAbilityName()).formatted(ChatFormatting.AQUA).getString(),
            x,
            y,
            0x66FFFF,
            false
        );
        y += 12;

        context.drawText(
            textRenderer,
            Component.literal(ManhuntClientState.getSelectedAbilityDescription()).formatted(ChatFormatting.GRAY).getString(),
            x,
            y,
            0xAAAAAA,
            false
        );

        renderCooldowns(context, textRenderer, x, y + 18, false);
    }

    private static void renderCooldowns(net.minecraft.client.gui.GuiGraphics context, Font textRenderer, int x, int y, boolean isSpeedrunner) {
        if (isSpeedrunner) {
            context.drawText(textRenderer, abilityLine(0, "Escape", ManhuntClientState.getSpeedrunnerEscapeCooldown()).getString(), x, y, lineColor(0, ManhuntClientState.getSpeedrunnerEscapeCooldown()), false);
            y += 12;
            context.drawText(textRenderer, abilityLine(1, "Burst", ManhuntClientState.getSpeedrunnerSpeedCooldown()).getString(), x, y, lineColor(1, ManhuntClientState.getSpeedrunnerSpeedCooldown()), false);
            y += 12;
            context.drawText(textRenderer, abilityLine(2, "Veil", ManhuntClientState.getSpeedrunnerInvisCooldown()).getString(), x, y, lineColor(2, ManhuntClientState.getSpeedrunnerInvisCooldown()), false);
        } else {
            context.drawText(textRenderer, abilityLine(0, "Track", ManhuntClientState.getHunterTrackCooldown()).getString(), x, y, lineColor(0, ManhuntClientState.getHunterTrackCooldown()), false);
            y += 12;
            context.drawText(textRenderer, abilityLine(1, "Blockade", ManhuntClientState.getHunterBlockCooldown()).getString(), x, y, lineColor(1, ManhuntClientState.getHunterBlockCooldown()), false);
            y += 12;
            context.drawText(textRenderer, abilityLine(2, "Snare", ManhuntClientState.getHunterSlowCooldown()).getString(), x, y, lineColor(2, ManhuntClientState.getHunterSlowCooldown()), false);
        }
    }

    private static Component abilityLine(int index, String name, int cooldown) {
        boolean selected = ManhuntClientState.getSelectedIndex() == index;
        String prefix = selected ? "> " : "  ";
        String cooldownText = cooldown > 0 ? " (" + (cooldown / 20) + "s)" : "";
        return Component.literal(prefix + name + cooldownText)
            .formatted(cooldown > 0 ? ChatFormatting.RED : selected ? ChatFormatting.AQUA : ChatFormatting.GRAY);
    }

    private static int lineColor(int index, int cooldown) {
        if (cooldown > 0) {
            return 0xFF7777;
        }
        return ManhuntClientState.getSelectedIndex() == index ? 0x66FFFF : 0xAAAAAA;
    }
}
