package com.wayacreate.frogslimegamemode.network;

import com.wayacreate.frogslimegamemode.client.hud.GamemodeHud;
import com.wayacreate.frogslimegamemode.gui.TasksScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.toast.SystemToast;
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

        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.ACHIEVEMENT_TOAST, (client, handler, buf, responseSender) -> {
            String name = buf.readString();
            String description = buf.readString();
            int color = buf.readInt();

            client.execute(() -> {
                if (client.getToastManager() != null) {
                    Text titleText = Text.literal(name).formatted(Formatting.byColorIndex(color), Formatting.BOLD);
                    Text descriptionText = Text.literal(description);
                    client.getToastManager().add(SystemToast.create(client, SystemToast.Type.TUTORIAL_HINT, titleText, descriptionText));
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.TOTEM_ANIMATION, (client, handler, buf, responseSender) -> {
            String title = buf.readString();
            String subtitle = buf.readString();
            int color = buf.readInt();

            client.execute(() -> {
                if (client.player != null) {
                    // Play experience level-up sound
                    client.player.playSound(net.minecraft.sound.SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    
                    // Show title
                    client.inGameHud.setTitle(Text.literal(title).formatted(Formatting.byColorIndex(color), Formatting.BOLD));
                    client.inGameHud.setSubtitle(Text.literal(subtitle).formatted(Formatting.byColorIndex(color)));
                    
                    // Spawn totem particles around player
                    if (client.world != null) {
                        for (int i = 0; i < 30; i++) {
                            double offsetX = (client.world.random.nextDouble() - 0.5) * 2;
                            double offsetY = client.world.random.nextDouble() * 2;
                            double offsetZ = (client.world.random.nextDouble() - 0.5) * 2;
                            client.world.addParticle(net.minecraft.particle.ParticleTypes.TOTEM_OF_UNDYING,
                                client.player.getX() + offsetX,
                                client.player.getY() + offsetY,
                                client.player.getZ() + offsetZ,
                                0, 0, 0);
                        }
                    }
                }
            });
        });
    }
}
