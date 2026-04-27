package com.wayacreate.frogslimegamemode.network;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import com.wayacreate.frogslimegamemode.achievements.Achievement;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public final class ModNetworking {
    public static final Identifier GAMEMODE_STATUS = new Identifier(FrogSlimeGamemode.MOD_ID, "gamemode_status");
    public static final Identifier OPEN_TASKS_SCREEN = new Identifier(FrogSlimeGamemode.MOD_ID, "open_tasks_screen");
    public static final Identifier SHOW_TITLE = new Identifier(FrogSlimeGamemode.MOD_ID, "show_title");
    public static final Identifier ACHIEVEMENT_TOAST = new Identifier(FrogSlimeGamemode.MOD_ID, "achievement_toast");
    public static final Identifier TOTEM_ANIMATION = new Identifier(FrogSlimeGamemode.MOD_ID, "totem_animation");
    public static final Identifier USE_ABILITY = new Identifier(FrogSlimeGamemode.MOD_ID, "use_ability");
    public static final Identifier PLAYER_TONGUE_ANIMATION = new Identifier(FrogSlimeGamemode.MOD_ID, "player_tongue_animation");
    public static final Identifier SWITCH_ABILITY = new Identifier(FrogSlimeGamemode.MOD_ID, "switch_ability");
    
    // Manhunt ability packets
    public static final Identifier HUNTER_TRACK = new Identifier(FrogSlimeGamemode.MOD_ID, "hunter_track");
    public static final Identifier HUNTER_BLOCK = new Identifier(FrogSlimeGamemode.MOD_ID, "hunter_block");
    public static final Identifier HUNTER_SLOW = new Identifier(FrogSlimeGamemode.MOD_ID, "hunter_slow");
    public static final Identifier SPEEDRUNNER_ESCAPE = new Identifier(FrogSlimeGamemode.MOD_ID, "speedrunner_escape");
    public static final Identifier SPEEDRUNNER_SPEED = new Identifier(FrogSlimeGamemode.MOD_ID, "speedrunner_speed");
    public static final Identifier SPEEDRUNNER_INVIS = new Identifier(FrogSlimeGamemode.MOD_ID, "speedrunner_invis");
    public static final Identifier MANHUNT_HUD_UPDATE = new Identifier(FrogSlimeGamemode.MOD_ID, "manhunt_hud_update");

    private ModNetworking() {
    }

    public static void registerServer() {
        FrogSlimeGamemode.LOGGER.info("Registering server networking");
        
        ServerPlayNetworking.registerGlobalReceiver(USE_ABILITY, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                // Handle player ability activation
                com.wayacreate.frogslimegamemode.abilities.PlayerAbilityManager.useCurrentAbility(player);
            });
        });
        
        ServerPlayNetworking.registerGlobalReceiver(SWITCH_ABILITY, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                // Handle ability switching
                com.wayacreate.frogslimegamemode.abilities.PlayerAbilityManager.switchToNextAbility(player);
            });
        });
        
        // Manhunt ability packets
        ServerPlayNetworking.registerGlobalReceiver(HUNTER_TRACK, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                com.wayacreate.frogslimegamemode.gamemode.ManhuntManager.useHunterTrackAbility(player);
            });
        });
        
        ServerPlayNetworking.registerGlobalReceiver(HUNTER_BLOCK, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                com.wayacreate.frogslimegamemode.gamemode.ManhuntManager.useHunterBlockAbility(player);
            });
        });
        
        ServerPlayNetworking.registerGlobalReceiver(HUNTER_SLOW, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                com.wayacreate.frogslimegamemode.gamemode.ManhuntManager.useHunterSlowAbility(player);
            });
        });
        
        ServerPlayNetworking.registerGlobalReceiver(SPEEDRUNNER_ESCAPE, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                com.wayacreate.frogslimegamemode.gamemode.ManhuntManager.useSpeedrunnerEscapeAbility(player);
            });
        });
        
        ServerPlayNetworking.registerGlobalReceiver(SPEEDRUNNER_SPEED, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                com.wayacreate.frogslimegamemode.gamemode.ManhuntManager.useSpeedrunnerSpeedAbility(player);
            });
        });
        
        ServerPlayNetworking.registerGlobalReceiver(SPEEDRUNNER_INVIS, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                com.wayacreate.frogslimegamemode.gamemode.ManhuntManager.useSpeedrunnerInvisAbility(player);
            });
        });
    }

    public static void registerClient() {
    }

    public static void syncGamemodeStatus(ServerPlayerEntity player, boolean active) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(active);
        ServerPlayNetworking.send(player, GAMEMODE_STATUS, buf);
    }

    public static void openTasksScreen(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send(player, OPEN_TASKS_SCREEN, buf);
    }

    public static void showTitle(ServerPlayerEntity player, String title, String subtitle, Formatting color) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(title);
        buf.writeString(subtitle);
        int colorIndex = color.getColorIndex();
        buf.writeInt(colorIndex < 0 ? 15 : colorIndex);
        ServerPlayNetworking.send(player, SHOW_TITLE, buf);
    }

    public static void sendAchievementToast(ServerPlayerEntity player, Achievement achievement) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(achievement.getName());
        buf.writeString(achievement.getDescription());
        buf.writeString(achievement.getFormattedName().getString());
        int colorIndex = achievement.getColor().getColorIndex();
        buf.writeInt(colorIndex < 0 ? 15 : colorIndex);
        ServerPlayNetworking.send(player, ACHIEVEMENT_TOAST, buf);
    }
    
    public static void sendTotemAnimation(ServerPlayerEntity player, String title, String subtitle, Formatting color, net.minecraft.item.Item item) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(title);
        buf.writeString(subtitle);
        int colorIndex = color.getColorIndex();
        buf.writeInt(colorIndex < 0 ? 15 : colorIndex);
        buf.writeItemStack(new net.minecraft.item.ItemStack(item));
        ServerPlayNetworking.send(player, TOTEM_ANIMATION, buf);
    }
    
    public static void sendTotemAnimation(ServerPlayerEntity player, String title, String subtitle, Formatting color) {
        sendTotemAnimation(player, title, subtitle, color, net.minecraft.item.Items.TOTEM_OF_UNDYING);
    }
    
    public static void sendUseAbility(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send(player, USE_ABILITY, buf);
    }
    
    public static void sendPlayerTongueAnimation(ServerPlayerEntity player, int targetEntityId) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(targetEntityId);
        ServerPlayNetworking.send(player, PLAYER_TONGUE_ANIMATION, buf);
    }
    
    public static void sendManhuntHudUpdate(ServerPlayerEntity player, String elapsedTime, int deathCount) {
        sendManhuntHudUpdate(player, elapsedTime, deathCount, "", 0, 0, 0, 0, 0, 0);
    }
    
    public static void sendManhuntHudUpdate(ServerPlayerEntity player, String elapsedTime, int deathCount, 
            String targetName, int hunterTrackCd, int hunterBlockCd, int hunterSlowCd, 
            int speedrunnerEscapeCd, int speedrunnerSpeedCd, int speedrunnerInvisCd) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(elapsedTime);
        buf.writeInt(deathCount);
        buf.writeString(targetName);
        buf.writeInt(hunterTrackCd);
        buf.writeInt(hunterBlockCd);
        buf.writeInt(hunterSlowCd);
        buf.writeInt(speedrunnerEscapeCd);
        buf.writeInt(speedrunnerSpeedCd);
        buf.writeInt(speedrunnerInvisCd);
        ServerPlayNetworking.send(player, MANHUNT_HUD_UPDATE, buf);
    }
}
