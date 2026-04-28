package com.wayacreate.frogslimegamemode.network;

import com.wayacreate.frogslimegamemode.achievements.AchievementToast;
import com.wayacreate.frogslimegamemode.client.hud.GamemodeHud;
import com.wayacreate.frogslimegamemode.client.hud.ManhuntHud;
import com.wayacreate.frogslimegamemode.client.gui.TasksScreen;
import com.wayacreate.frogslimegamemode.client.state.ManhuntClientState;
import com.wayacreate.frogslimegamemode.client.state.ProgressionClientState;
import com.wayacreate.frogslimegamemode.tasks.TaskType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;

public final class ModNetworkingClient {
    private ModNetworkingClient() {
    }

    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.GAMEMODE_STATUS, (client, handler, buf, responseSender) -> {
            boolean active = buf.readBoolean();
            client.execute(() -> {
                GamemodeHud.setGamemodeActive(active);
                if (!active) {
                    ProgressionClientState.clear();
                }
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
            net.minecraft.item.ItemStack iconStack = buf.readItemStack();
            int color = buf.readInt();

            client.execute(() -> {
                if (client.getToastManager() != null) {
                    Text titleText = Text.literal(name).formatted(Formatting.byColorIndex(color), Formatting.BOLD);
                    Text descriptionText = Text.literal(description);
                    client.getToastManager().add(SystemToast.create(client, SystemToast.Type.TUTORIAL_HINT, titleText, descriptionText));
                    
                    AchievementToast.show(titleText, descriptionText, iconStack);
                    
                    if (client.player != null) {
                        client.player.playSound(net.minecraft.sound.SoundEvents.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
                    }
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

        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.MANHUNT_HUD_UPDATE, (client, handler, buf, responseSender) -> {
            boolean active = buf.readBoolean();
            String role = buf.readString();
            String elapsedTime = buf.readString();
            int deathCount = buf.readInt();
            String targetName = buf.readString();
            int selectedIndex = buf.readInt();
            String selectedAbilityName = buf.readString();
            String selectedAbilityDescription = buf.readString();
            int hunterTrackCd = buf.readInt();
            int hunterBlockCd = buf.readInt();
            int hunterSlowCd = buf.readInt();
            int speedrunnerEscapeCd = buf.readInt();
            int speedrunnerSpeedCd = buf.readInt();
            int speedrunnerInvisCd = buf.readInt();

            client.execute(() -> {
                ManhuntClientState.update(
                    active,
                    role,
                    elapsedTime,
                    deathCount,
                    targetName,
                    selectedIndex,
                    selectedAbilityName,
                    selectedAbilityDescription,
                    hunterTrackCd,
                    hunterBlockCd,
                    hunterSlowCd,
                    speedrunnerEscapeCd,
                    speedrunnerSpeedCd,
                    speedrunnerInvisCd
                );
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.PROGRESSION_SNAPSHOT, (client, handler, buf, responseSender) -> {
            boolean active = buf.readBoolean();
            int level = buf.readInt();
            double xp = buf.readDouble();
            double xpToNext = buf.readDouble();
            int helpersSpawned = buf.readInt();
            int mobsEaten = buf.readInt();
            int itemsCollected = buf.readInt();
            int deaths = buf.readInt();
            int jumps = buf.readInt();
            int abilityCount = buf.readInt();
            int highestEvolutionStage = buf.readInt();
            int achievementCount = buf.readInt();
            String selectedAbilityName = buf.readString();
            String selectedAbilityDescription = buf.readString();
            int taskCount = buf.readInt();

            EnumMap<TaskType, Integer> taskProgress = new EnumMap<>(TaskType.class);
            for (int i = 0; i < taskCount; i++) {
                String taskName = buf.readString();
                int progress = buf.readInt();
                taskProgress.put(TaskType.valueOf(taskName), progress);
            }

            int unlockCount = buf.readInt();
            List<String> unlockNames = new ArrayList<>();
            List<String> unlockDescriptions = new ArrayList<>();
            for (int i = 0; i < unlockCount; i++) {
                unlockNames.add(buf.readString());
                unlockDescriptions.add(buf.readString());
            }

            client.execute(() -> ProgressionClientState.update(
                active,
                level,
                xp,
                xpToNext,
                helpersSpawned,
                mobsEaten,
                itemsCollected,
                deaths,
                jumps,
                abilityCount,
                highestEvolutionStage,
                achievementCount,
                selectedAbilityName,
                selectedAbilityDescription,
                taskProgress,
                unlockNames,
                unlockDescriptions
            ));
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
