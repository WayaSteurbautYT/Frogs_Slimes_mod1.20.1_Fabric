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

    private ModNetworking() {
    }

    public static void registerServer() {
        FrogSlimeGamemode.LOGGER.info("Registering server networking");
        
        ServerPlayNetworking.registerGlobalReceiver(USE_ABILITY, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                // Handle player ability activation
                // For now, just send a message
                player.sendMessage(net.minecraft.text.Text.literal("Ability key pressed!")
                    .formatted(net.minecraft.util.Formatting.GREEN), true);
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
    
    public static void sendTotemAnimation(ServerPlayerEntity player, String title, String subtitle, Formatting color) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(title);
        buf.writeString(subtitle);
        int colorIndex = color.getColorIndex();
        buf.writeInt(colorIndex < 0 ? 15 : colorIndex);
        ServerPlayNetworking.send(player, TOTEM_ANIMATION, buf);
    }
    
    public static void sendUseAbility(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send(player, USE_ABILITY, buf);
    }
}
