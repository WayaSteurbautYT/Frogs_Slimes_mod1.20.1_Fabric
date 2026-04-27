package com.wayacreate.frogslimegamemode.client.hud;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import com.wayacreate.frogslimegamemode.gamemode.ManhuntManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ManhuntHud {
    private static boolean initialized = false;
    
    // Client-side data storage
    private static final Map<UUID, String> clientElapsedTimes = new HashMap<>();
    private static final Map<UUID, Integer> clientDeathCounts = new HashMap<>();
    private static final Map<UUID, String> clientTargetNames = new HashMap<>();
    private static final Map<UUID, Integer> clientHunterTrackCd = new HashMap<>();
    private static final Map<UUID, Integer> clientHunterBlockCd = new HashMap<>();
    private static final Map<UUID, Integer> clientHunterSlowCd = new HashMap<>();
    private static final Map<UUID, Integer> clientSpeedrunnerEscapeCd = new HashMap<>();
    private static final Map<UUID, Integer> clientSpeedrunnerSpeedCd = new HashMap<>();
    private static final Map<UUID, Integer> clientSpeedrunnerInvisCd = new HashMap<>();

    public static void onInitializeClient() {
        if (initialized) return;
        initialized = true;
        
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;
            
            // Check if player is in a manhunt game
            if (!ManhuntManager.isInGame(client.player)) return;
            
            TextRenderer textRenderer = client.textRenderer;
            int screenWidth = client.getWindow().getScaledWidth();
            int screenHeight = client.getWindow().getScaledHeight();
            
            if (ManhuntManager.isSpeedrunner(client.player)) {
                renderSpeedrunnerHud(drawContext, textRenderer, screenWidth, screenHeight, client);
            } else if (ManhuntManager.isHunter(client.player)) {
                renderHunterHud(drawContext, textRenderer, screenWidth, screenHeight, client);
            }
        });
        
        FrogSlimeGamemode.LOGGER.info("Manhunt HUD initialized");
    }
    
    private static void renderSpeedrunnerHud(net.minecraft.client.gui.DrawContext context, TextRenderer textRenderer, int width, int height, MinecraftClient client) {
        int y = 10;
        int x = width - 150;
        
        // Role indicator
        context.drawText(textRenderer, 
            Text.literal("SPEEDRUNNER").formatted(Formatting.GREEN, Formatting.BOLD).getString(), 
            x, y, 0x00FF00, false);
        y += 15;
        
        // Elapsed time
        String elapsedTime = clientElapsedTimes.getOrDefault(client.player.getUuid(), "0:00");
        context.drawText(textRenderer, 
            Text.literal("Time: " + elapsedTime).formatted(Formatting.YELLOW).getString(), 
            x, y, 0xFFFF00, false);
        y += 15;
        
        // Death count
        int deaths = clientDeathCounts.getOrDefault(client.player.getUuid(), 0);
        context.drawText(textRenderer, 
            Text.literal("Deaths: " + deaths + "/3").formatted(Formatting.RED).getString(), 
            x, y, 0xFF0000, false);
        y += 15;
        
        // Solo mode indicator
        if (ManhuntManager.isSoloSpeedrunner(client.player)) {
            context.drawText(textRenderer, 
                Text.literal("SOLO MODE").formatted(Formatting.AQUA, Formatting.BOLD).getString(), 
                x, y, 0x00FFFF, false);
        }
        
        // Ability cooldowns
        renderCooldowns(context, textRenderer, x, y + 20, client, true);
    }
    
    private static void renderHunterHud(net.minecraft.client.gui.DrawContext context, TextRenderer textRenderer, int width, int height, MinecraftClient client) {
        int y = 10;
        int x = width - 150;
        
        // Role indicator
        context.drawText(textRenderer, 
            Text.literal("HUNTER").formatted(Formatting.RED, Formatting.BOLD).getString(), 
            x, y, 0xFF0000, false);
        y += 15;
        
        // Target info
        String targetName = clientTargetNames.getOrDefault(client.player.getUuid(), "Unknown");
        context.drawText(textRenderer, 
            Text.literal("Target: " + targetName).formatted(Formatting.GRAY).getString(), 
            x, y, 0xAAAAAA, false);
        y += 15;
        
        // Ability cooldowns
        renderCooldowns(context, textRenderer, x, y + 10, client, false);
    }
    
    private static void renderCooldowns(net.minecraft.client.gui.DrawContext context, TextRenderer textRenderer, int x, int y, MinecraftClient client, boolean isSpeedrunner) {
        UUID uuid = client.player.getUuid();
        
        if (isSpeedrunner) {
            // Speedrunner abilities
            int escapeCooldown = clientSpeedrunnerEscapeCd.getOrDefault(uuid, 0);
            int speedCooldown = clientSpeedrunnerSpeedCd.getOrDefault(uuid, 0);
            int invisCooldown = clientSpeedrunnerInvisCd.getOrDefault(uuid, 0);
            
            context.drawText(textRenderer, 
                Text.literal("[V] Escape" + (escapeCooldown > 0 ? " (" + (escapeCooldown / 20) + "s)" : "")).formatted(escapeCooldown > 0 ? Formatting.RED : Formatting.GRAY).getString(), 
                x, y, escapeCooldown > 0 ? 0xFF6666 : 0xAAAAAA, false);
            y += 12;
            context.drawText(textRenderer, 
                Text.literal("[B] Speed" + (speedCooldown > 0 ? " (" + (speedCooldown / 20) + "s)" : "")).formatted(speedCooldown > 0 ? Formatting.RED : Formatting.GRAY).getString(), 
                x, y, speedCooldown > 0 ? 0xFF6666 : 0xAAAAAA, false);
            y += 12;
            context.drawText(textRenderer, 
                Text.literal("[N] Invis" + (invisCooldown > 0 ? " (" + (invisCooldown / 20) + "s)" : "")).formatted(invisCooldown > 0 ? Formatting.RED : Formatting.GRAY).getString(), 
                x, y, invisCooldown > 0 ? 0xFF6666 : 0xAAAAAA, false);
        } else {
            // Hunter abilities
            int trackCooldown = clientHunterTrackCd.getOrDefault(uuid, 0);
            int blockCooldown = clientHunterBlockCd.getOrDefault(uuid, 0);
            int slowCooldown = clientHunterSlowCd.getOrDefault(uuid, 0);
            
            context.drawText(textRenderer, 
                Text.literal("[R] Track" + (trackCooldown > 0 ? " (" + (trackCooldown / 20) + "s)" : "")).formatted(trackCooldown > 0 ? Formatting.RED : Formatting.GRAY).getString(), 
                x, y, trackCooldown > 0 ? 0xFF6666 : 0xAAAAAA, false);
            y += 12;
            context.drawText(textRenderer, 
                Text.literal("[F] Block" + (blockCooldown > 0 ? " (" + (blockCooldown / 20) + "s)" : "")).formatted(blockCooldown > 0 ? Formatting.RED : Formatting.GRAY).getString(), 
                x, y, blockCooldown > 0 ? 0xFF6666 : 0xAAAAAA, false);
            y += 12;
            context.drawText(textRenderer, 
                Text.literal("[G] Slow" + (slowCooldown > 0 ? " (" + (slowCooldown / 20) + "s)" : "")).formatted(slowCooldown > 0 ? Formatting.RED : Formatting.GRAY).getString(), 
                x, y, slowCooldown > 0 ? 0xFF6666 : 0xAAAAAA, false);
        }
    }
    
    // Public methods to update client-side data (called from networking)
    public static void updateClientElapsedTime(UUID uuid, String time) {
        clientElapsedTimes.put(uuid, time);
    }
    
    public static void updateClientDeathCount(UUID uuid, int deaths) {
        clientDeathCounts.put(uuid, deaths);
    }
    
    public static void updateClientTargetName(UUID uuid, String targetName) {
        clientTargetNames.put(uuid, targetName);
    }
    
    public static void updateClientCooldowns(UUID uuid, boolean isHunter, int cooldown) {
        // This method is deprecated - use individual cooldown update methods instead
    }
    
    public static void updateClientHunterCooldowns(UUID uuid, int trackCd, int blockCd, int slowCd) {
        clientHunterTrackCd.put(uuid, trackCd);
        clientHunterBlockCd.put(uuid, blockCd);
        clientHunterSlowCd.put(uuid, slowCd);
    }
    
    public static void updateClientSpeedrunnerCooldowns(UUID uuid, int escapeCd, int speedCd, int invisCd) {
        clientSpeedrunnerEscapeCd.put(uuid, escapeCd);
        clientSpeedrunnerSpeedCd.put(uuid, speedCd);
        clientSpeedrunnerInvisCd.put(uuid, invisCd);
    }
    
    public static void clearClientData(UUID uuid) {
        clientElapsedTimes.remove(uuid);
        clientDeathCounts.remove(uuid);
        clientTargetNames.remove(uuid);
        clientHunterTrackCd.remove(uuid);
        clientHunterBlockCd.remove(uuid);
        clientHunterSlowCd.remove(uuid);
        clientSpeedrunnerEscapeCd.remove(uuid);
        clientSpeedrunnerSpeedCd.remove(uuid);
        clientSpeedrunnerInvisCd.remove(uuid);
    }
}
