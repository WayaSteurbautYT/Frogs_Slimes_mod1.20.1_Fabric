package com.wayacreate.frogslimegamemode.network;

import com.wayacreate.frogslimegamemode.achievements.AchievementToast;
import com.wayacreate.frogslimegamemode.client.gui.TasksScreen;
import com.wayacreate.frogslimegamemode.client.hud.GamemodeHud;
import com.wayacreate.frogslimegamemode.client.state.ManhuntClientState;
import com.wayacreate.frogslimegamemode.client.state.ProgressionClientState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ModNetworkingClient {
    private ModNetworkingClient() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(ModNetworkingClient::registerPayloads);
    }

    public static void requestProgressSnapshot() {
        PacketDistributor.sendToServer(ModNetworking.RequestProgressSnapshotPayload.INSTANCE);
    }

    public static void sendUseAbility() {
        PacketDistributor.sendToServer(ModNetworking.UseAbilityPayload.INSTANCE);
    }

    public static void sendSwitchAbility() {
        PacketDistributor.sendToServer(ModNetworking.SwitchAbilityPayload.INSTANCE);
    }

    public static void sendConsumeAbilityItem() {
        PacketDistributor.sendToServer(ModNetworking.ConsumeAbilityItemPayload.INSTANCE);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");

        registrar.playToClient(ModNetworking.GamemodeStatusPayload.ID, ModNetworking.GamemodeStatusPayload.CODEC, ModNetworkingClient::handleGamemodeStatus);
        registrar.playToClient(ModNetworking.OpenTasksScreenPayload.ID, ModNetworking.OpenTasksScreenPayload.CODEC, ModNetworkingClient::handleOpenTasksScreen);
        registrar.playToClient(ModNetworking.ShowTitlePayload.ID, ModNetworking.ShowTitlePayload.CODEC, ModNetworkingClient::handleShowTitle);
        registrar.playToClient(ModNetworking.AchievementToastPayload.ID, ModNetworking.AchievementToastPayload.CODEC, ModNetworkingClient::handleAchievementToast);
        registrar.playToClient(ModNetworking.TotemAnimationPayload.ID, ModNetworking.TotemAnimationPayload.CODEC, ModNetworkingClient::handleTotemAnimation);
        registrar.playToClient(ModNetworking.PlayerTongueAnimationPayload.ID, ModNetworking.PlayerTongueAnimationPayload.CODEC, ModNetworkingClient::handlePlayerTongueAnimation);
        registrar.playToClient(ModNetworking.ManhuntHudUpdatePayload.ID, ModNetworking.ManhuntHudUpdatePayload.CODEC, ModNetworkingClient::handleManhuntHudUpdate);
        registrar.playToClient(ModNetworking.ProgressionSnapshotPayload.ID, ModNetworking.ProgressionSnapshotPayload.CODEC, ModNetworkingClient::handleProgressionSnapshot);
    }

    private static void handleGamemodeStatus(ModNetworking.GamemodeStatusPayload payload, IPayloadContext context) {
        GamemodeHud.setGamemodeActive(payload.active());
        if (!payload.active()) {
            ProgressionClientState.clear();
        }
    }

    private static void handleOpenTasksScreen(ModNetworking.OpenTasksScreenPayload payload, IPayloadContext context) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            client.setScreen(new TasksScreen());
        }
    }

    private static void handleShowTitle(ModNetworking.ShowTitlePayload payload, IPayloadContext context) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            client.inGameHud.setTitle(Component.literal(payload.title()).formatted(ChatFormatting.byColorIndex(payload.color())));
            client.inGameHud.setSubtitle(Component.literal(payload.subtitle()).formatted(ChatFormatting.byColorIndex(payload.color())));
        }
    }

    private static void handleAchievementToast(ModNetworking.AchievementToastPayload payload, IPayloadContext context) {
        Minecraft client = Minecraft.getInstance();
        if (client.getToastManager() == null) {
            return;
        }

        Component titleText = Component.literal(payload.name()).formatted(ChatFormatting.byColorIndex(payload.color()), ChatFormatting.BOLD);
        Component descriptionText = Component.literal(payload.description());
        client.getToastManager().add(SystemToast.create(client, SystemToast.Type.TUTORIAL_HINT, titleText, descriptionText));
        AchievementToast.show(titleText, descriptionText, payload.iconStack());

        if (client.player != null) {
            client.player.playSound(net.minecraft.sounds.SoundEvents.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
        }
    }

    private static void handleTotemAnimation(ModNetworking.TotemAnimationPayload payload, IPayloadContext context) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) {
            return;
        }

        client.player.playSound(net.minecraft.sounds.SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        client.inGameHud.setTitle(Component.literal(payload.title()).formatted(ChatFormatting.byColorIndex(payload.color()), ChatFormatting.BOLD));
        client.inGameHud.setSubtitle(Component.literal(payload.subtitle()).formatted(ChatFormatting.byColorIndex(payload.color())));

        if (client.world != null) {
            for (int i = 0; i < 30; i++) {
                double offsetX = (client.world.random.nextDouble() - 0.5) * 2;
                double offsetY = client.world.random.nextDouble() * 2;
                double offsetZ = (client.world.random.nextDouble() - 0.5) * 2;
                client.world.addParticle(
                    net.minecraft.core.particles.ParticleTypes.TOTEM_OF_UNDYING,
                    client.player.getX() + offsetX,
                    client.player.getY() + offsetY,
                    client.player.getZ() + offsetZ,
                    0,
                    0,
                    0
                );
            }

            if (!payload.itemStack().isEmpty()) {
                for (int i = 0; i < 10; i++) {
                    double offsetX = client.world.random.nextDouble() - 0.5;
                    double offsetY = client.world.random.nextDouble() * 1.5;
                    double offsetZ = client.world.random.nextDouble() - 0.5;
                    client.world.addParticle(
                        new net.minecraft.core.particles.ItemParticleOption(net.minecraft.core.particles.ParticleTypes.ITEM, payload.itemStack()),
                        client.player.getX() + offsetX,
                        client.player.getY() + 1.5 + offsetY,
                        client.player.getZ() + offsetZ,
                        0,
                        0.1,
                        0
                    );
                }
            }
        }
    }

    private static void handlePlayerTongueAnimation(ModNetworking.PlayerTongueAnimationPayload payload, IPayloadContext context) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null && client.world != null) {
            net.minecraft.world.entity.Entity target = client.world.getEntityById(payload.targetEntityId());
            if (target != null) {
                spawnTongueParticles(client, target);
                client.player.playSound(net.minecraft.sounds.SoundEvents.ENTITY_FROG_EAT, 1.0f, 1.0f);
            }
        }
    }

    private static void handleManhuntHudUpdate(ModNetworking.ManhuntHudUpdatePayload payload, IPayloadContext context) {
        ManhuntClientState.update(
            payload.active(),
            payload.role(),
            payload.elapsedTime(),
            payload.deathCount(),
            payload.targetName(),
            payload.selectedIndex(),
            payload.selectedAbilityName(),
            payload.selectedAbilityDescription(),
            payload.hunterTrackCd(),
            payload.hunterBlockCd(),
            payload.hunterSlowCd(),
            payload.speedrunnerEscapeCd(),
            payload.speedrunnerSpeedCd(),
            payload.speedrunnerInvisCd()
        );
    }

    private static void handleProgressionSnapshot(ModNetworking.ProgressionSnapshotPayload payload, IPayloadContext context) {
        ProgressionClientState.update(
            payload.active(),
            payload.level(),
            payload.xp(),
            payload.xpToNext(),
            payload.helpersSpawned(),
            payload.mobsEaten(),
            payload.itemsCollected(),
            payload.deaths(),
            payload.jumps(),
            payload.abilityCount(),
            payload.highestEvolutionStage(),
            payload.achievementCount(),
            payload.selectedAbilityName(),
            payload.selectedAbilityDescription(),
            payload.taskProgress(),
            payload.unlockNames(),
            payload.unlockDescriptions()
        );
    }

    private static void spawnTongueParticles(Minecraft client, net.minecraft.world.entity.Entity target) {
        if (client.player == null || client.world == null) {
            return;
        }

        Vec3 startPos = client.player.getPos().add(0, 1.0, 0);
        Vec3 endPos = target.getPos().add(0, 0.5, 0);
        Vec3 direction = endPos.subtract(startPos);
        double distance = direction.length();
        int particleCount = (int) (distance * 3);

        for (int i = 0; i < particleCount; i++) {
            double t = i / (double) particleCount;
            Vec3 particlePos = startPos.add(direction.multiply(t));
            double offsetX = (client.world.random.nextDouble() - 0.5) * 0.15;
            double offsetY = (client.world.random.nextDouble() - 0.5) * 0.15;
            double offsetZ = (client.world.random.nextDouble() - 0.5) * 0.15;

            client.world.addParticle(
                net.minecraft.core.particles.ParticleTypes.ITEM_SLIME,
                particlePos.x + offsetX,
                particlePos.y + offsetY,
                particlePos.z + offsetZ,
                0,
                0,
                0
            );
        }
    }
}
