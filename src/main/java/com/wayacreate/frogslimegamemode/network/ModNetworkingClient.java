package com.wayacreate.frogslimegamemode.network;

import com.wayacreate.frogslimegamemode.client.hud.GamemodeHud;
import com.wayacreate.frogslimegamemode.client.gui.TasksScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

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
            net.minecraft.item.ItemStack itemStack = buf.readItemStack();

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
                        
                        // Spawn item particles showing the ability item
                        if (!itemStack.isEmpty()) {
                            for (int i = 0; i < 10; i++) {
                                double offsetX = (client.world.random.nextDouble() - 0.5) * 1;
                                double offsetY = client.world.random.nextDouble() * 1.5;
                                double offsetZ = (client.world.random.nextDouble() - 0.5) * 1;
                                client.world.addParticle(
                                    new net.minecraft.particle.ItemStackParticleEffect(net.minecraft.particle.ParticleTypes.ITEM, itemStack),
                                    client.player.getX() + offsetX,
                                    client.player.getY() + 1.5 + offsetY,
                                    client.player.getZ() + offsetZ,
                                    0, 0.1, 0);
                            }
                        }
                    }
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.PLAYER_TONGUE_ANIMATION, (client, handler, buf, responseSender) -> {
            int targetEntityId = buf.readInt();

            client.execute(() -> {
                if (client.player != null && client.world != null) {
                    net.minecraft.entity.Entity target = client.world.getEntityById(targetEntityId);
                    if (target != null) {
                        // Spawn tongue particles from player to target
                        spawnTongueParticles(client, target);
                        // Play frog eat sound
                        client.player.playSound(net.minecraft.sound.SoundEvents.ENTITY_FROG_EAT, 1.0f, 1.0f);
                    }
                }
            });
        });
    }
    
    private static void spawnTongueParticles(net.minecraft.client.MinecraftClient client, net.minecraft.entity.Entity target) {
        if (client.player == null || client.world == null) return;
        
        Vec3d startPos = client.player.getPos().add(0, 1.0, 0);
        Vec3d endPos = target.getPos().add(0, 0.5, 0);
        Vec3d direction = endPos.subtract(startPos);
        double distance = direction.length();
        int particleCount = (int) (distance * 3);
        
        for (int i = 0; i < particleCount; i++) {
            double t = i / (double) particleCount;
            Vec3d particlePos = startPos.add(direction.multiply(t));
            
            double offsetX = (client.world.random.nextDouble() - 0.5) * 0.15;
            double offsetY = (client.world.random.nextDouble() - 0.5) * 0.15;
            double offsetZ = (client.world.random.nextDouble() - 0.5) * 0.15;
            
            client.world.addParticle(net.minecraft.particle.ParticleTypes.ITEM_SLIME,
                particlePos.x + offsetX,
                particlePos.y + offsetY,
                particlePos.z + offsetZ,
                0, 0, 0);
        }
    }
}
