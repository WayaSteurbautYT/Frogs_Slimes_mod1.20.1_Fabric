package com.wayacreate.frogslimegamemode.network;

import com.wayacreate.frogslimegamemode.client.hud.GamemodeHud;
import com.wayacreate.frogslimegamemode.gui.TasksScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class ModNetworkingClient {
    private ModNetworkingClient() {
    }

    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.GAMEMODE_STATUS, (client, handler, buf, responseSender) -> {
            boolean active = buf.readBoolean();
            client.execute(() -> {
                GamemodeHud.setGamemodeActive(active);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.OPEN_TASKS_SCREEN, (client, handler, buf, responseSender) -> {
            client.execute(() -> {
                if (client.player != null) {
                    client.setScreen(new TasksScreen());
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.SHOW_TITLE, (client, handler, buf, responseSender) -> {
            String title = buf.readString();
            String subtitle = buf.readString();
            int color = buf.readInt();

            client.execute(() -> {
                if (client.player != null) {
                    client.inGameHud.setTitle(Text.literal(title).formatted(Formatting.byColorIndex(color)));
                    client.inGameHud.setSubtitle(Text.literal(subtitle).formatted(Formatting.byColorIndex(color)));
                }
            });
        });
    }
}
