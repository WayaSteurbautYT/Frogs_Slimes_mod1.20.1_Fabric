package com.wayacreate.frogslimegamemode.gamemode;

import com.wayacreate.frogslimegamemode.achievements.AchievementManager;
import com.wayacreate.frogslimegamemode.guild.GuildManager;
import com.wayacreate.frogslimegamemode.item.HunterTrackerItem;
import com.wayacreate.frogslimegamemode.item.ManhuntCompassItem;
import com.wayacreate.frogslimegamemode.item.ModItems;
import com.wayacreate.frogslimegamemode.network.ModNetworking;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ManhuntManager {
    private static final String ROLE_HUNTER = "hunter";
    private static final String ROLE_SPEEDRUNNER = "speedrunner";
    private static final int ABILITY_COUNT = 3;

    private static final String[] HUNTER_ABILITY_NAMES = {"Track", "Blockade", "Snare"};
    private static final String[] HUNTER_ABILITY_DESCRIPTIONS = {
        "Reveal the current target's location and dimension.",
        "Raise a short-lived wall near the target.",
        "Slow the target if they are close enough."
    };
    private static final String[] SPEEDRUNNER_ABILITY_NAMES = {"Escape", "Burst", "Veil"};
    private static final String[] SPEEDRUNNER_ABILITY_DESCRIPTIONS = {
        "Blink to a safer nearby position.",
        "Gain a strong speed and jump burst.",
        "Turn invisible and create space."
    };

    // Active participant data. Speedrunners map to themselves, hunters map to their target.
    private static final Map<UUID, UUID> participantTargets = new HashMap<>();
    private static final Map<UUID, Boolean> gameActive = new HashMap<>();
    private static final Map<UUID, Long> startTime = new HashMap<>();
    private static final Map<UUID, Integer> deaths = new HashMap<>();
    private static final Map<UUID, String> playerRoles = new HashMap<>();
    private static final Map<UUID, Integer> selectedAbilityIndex = new HashMap<>();
    private static final Map<UUID, Integer> hunterTrackCooldowns = new HashMap<>();
    private static final Map<UUID, Integer> hunterBlockCooldowns = new HashMap<>();
    private static final Map<UUID, Integer> hunterSlowCooldowns = new HashMap<>();
    private static final Map<UUID, Integer> speedrunnerEscapeCooldowns = new HashMap<>();
    private static final Map<UUID, Integer> speedrunnerSpeedCooldowns = new HashMap<>();
    private static final Map<UUID, Integer> speedrunnerInvisCooldowns = new HashMap<>();
    private static final Map<UUID, Integer> hunterKills = new HashMap<>();
    private static final Map<UUID, Integer> soloWarningMinutes = new HashMap<>();

    private static final Set<UUID> hunters = new HashSet<>();
    private static final Set<UUID> soloSpeedrunners = new HashSet<>();
    private static final Set<UUID> activeSpeedrunners = new HashSet<>();
    private static final Set<UUID> ghostSpeedrunners = new HashSet<>();

    private static boolean autoManhuntMode = false;
    private static int countdownTicks = 0;
    private static final int COUNTDOWN_SECONDS = 30;

    // Cooldowns in ticks.
    private static final int HUNTER_TRACK_COOLDOWN = 100;
    private static final int HUNTER_BLOCK_COOLDOWN = 200;
    private static final int HUNTER_SLOW_COOLDOWN = 150;
    private static final int SPEEDRUNNER_ESCAPE_COOLDOWN = 180;
    private static final int SPEEDRUNNER_SPEED_COOLDOWN = 120;
    private static final int SPEEDRUNNER_INVIS_COOLDOWN = 300;

    private ManhuntManager() {
    }

    public static void setSpeedrunner(ServerPlayer speedrunner) {
        startSpeedrunner(speedrunner, false, true);
        giveSpeedrunnerClock(speedrunner);
    }

    public static void setSoloSpeedrunner(ServerPlayer speedrunner) {
        startSpeedrunner(speedrunner, true, true);
        giveSpeedrunnerClock(speedrunner);
    }

    public static void startAutoManhunt(ServerPlayer initiator) {
        MinecraftServer server = initiator.getServer();
        if (server == null) {
            return;
        }

        List<ServerPlayer> players = new ArrayList<>(server.getPlayerList().getPlayerList());
        if (players.size() < 2) {
            initiator.sendSystemMessage(Component.literal("Need at least 2 players for auto manhunt!")
                .withStyle(ChatFormatting.RED), false);
            return;
        }

        resetAllState(server, false);
        autoManhuntMode = true;
        countdownTicks = COUNTDOWN_SECONDS * 20;

        Collections.shuffle(players);
        ServerPlayer chosenSpeedrunner = players.get(0);
        playerRoles.put(chosenSpeedrunner.getUuid(), ROLE_SPEEDRUNNER);
        for (int i = 1; i < players.size(); i++) {
            playerRoles.put(players.get(i).getUuid(), ROLE_HUNTER);
        }

        server.getPlayerList().broadcast(
            Component.literal("[MANHUNT] ")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                .append(Component.literal("Auto Manhunt starts in " + COUNTDOWN_SECONDS + " seconds.")
                    .withStyle(ChatFormatting.YELLOW)),
            false
        );
        server.getPlayerList().broadcast(
            Component.literal("[MANHUNT] ")
                .withStyle(ChatFormatting.GOLD)
                .append(Component.literal(chosenSpeedrunner.getName().getString() + " is the speedrunner.")
                    .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)),
            false
        );
    }

    public static void startTeamManhunt(ServerPlayer initiator, String speedrunnerTeam, String hunterTeam) {
        MinecraftServer server = initiator.getServer();
        if (server == null) {
            return;
        }

        List<ServerPlayer> players = new ArrayList<>(server.getPlayerList().getPlayerList());
        if (players.size() < 2) {
            initiator.sendSystemMessage(Component.literal("Need at least 2 players for team manhunt!")
                .withStyle(ChatFormatting.RED), false);
            return;
        }

        resetAllState(server, false);
        autoManhuntMode = true;
        countdownTicks = COUNTDOWN_SECONDS * 20;

        List<ServerPlayer> speedrunnerPlayers = new ArrayList<>();
        List<ServerPlayer> hunterPlayers = new ArrayList<>();
        for (ServerPlayer player : players) {
            String grouping = getGroupingName(player);
            if (speedrunnerTeam.equalsIgnoreCase(grouping)) {
                speedrunnerPlayers.add(player);
                playerRoles.put(player.getUUID(), ROLE_SPEEDRUNNER);
            } else if (hunterTeam.equalsIgnoreCase(grouping)) {
                hunterPlayers.add(player);
                playerRoles.put(player.getUUID(), ROLE_HUNTER);
            }
        }

        if (speedrunnerPlayers.isEmpty()) {
            initiator.sendSystemMessage(Component.literal("Speedrunner team has no online members!")
                .withStyle(ChatFormatting.RED), false);
            autoManhuntMode = false;
            countdownTicks = 0;
            playerRoles.clear();
            return;
        }

        if (hunterPlayers.isEmpty()) {
            initiator.sendSystemMessage(Component.literal("Hunter team has no online members!")
                .withStyle(ChatFormatting.RED), false);
            autoManhuntMode = false;
            countdownTicks = 0;
            playerRoles.clear();
            return;
        }

        server.getPlayerList().broadcast(
            Component.literal("[MANHUNT] ")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                .append(Component.literal("Team Manhunt starts in " + COUNTDOWN_SECONDS + " seconds.")
                    .withStyle(ChatFormatting.YELLOW)),
            false
        );
        server.getPlayerList().broadcast(
            Component.literal("[MANHUNT] ")
                .withStyle(ChatFormatting.GOLD)
                .append(Component.literal("Speedrunners: " + joinNames(speedrunnerPlayers))
                    .withStyle(ChatFormatting.GREEN)),
            false
        );
        server.getPlayerList().broadcast(
            Component.literal("[MANHUNT] ")
                .withStyle(ChatFormatting.GOLD)
                .append(Component.literal("Hunters: " + joinNames(hunterPlayers))
                    .withStyle(ChatFormatting.RED)),
            false
        );
    }

    public static void setHunter(ServerPlayer hunter, ServerPlayer target) {
        if (hunter == target) {
            hunter.sendSystemMessage(Component.literal("You cannot hunt yourself.")
                .withStyle(ChatFormatting.RED), false);
            return;
        }

        if (isHunter(target)) {
            hunter.sendSystemMessage(Component.literal("Cannot target another hunter.")
                .withStyle(ChatFormatting.RED), false);
            return;
        }

        UUID uuid = hunter.getUUID();
        initializeHunter(hunter, target, true);

        hunter.sendSystemMessage(Component.literal("You are now hunting ")
            .withStyle(ChatFormatting.RED)
            .append(Component.literal(target.getName().getString()).withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD))
            .append(Component.literal(". Hold a tracker or manhunt compass, press TAB to cycle, then R to use.")
                .withStyle(ChatFormatting.RED)), false);

        giveHunterClock(hunter);
        sendHudUpdate(hunter);
    }

    public static void endGame(ServerPlayer player) {
        UUID uuid = player.getUUID();
        boolean wasGhost = ghostSpeedrunners.contains(uuid);
        clearParticipantState(uuid);

        if (wasGhost && player.isSpectator()) {
            player.changeGameMode(GameType.SURVIVAL);
        }

        ModNetworking.clearManhuntHud(player);
        player.sendSystemMessage(Component.literal("Manhunt ended for you.")
            .withStyle(ChatFormatting.GRAY), false);
    }

    public static void endAllGames(MinecraftServer server) {
        if (server == null) {
            return;
        }

        Set<UUID> uuids = getAllParticipantUuids();
        for (UUID uuid : uuids) {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            boolean wasGhost = ghostSpeedrunners.contains(uuid);
            clearParticipantState(uuid);

            if (player != null) {
                if (wasGhost && player.isSpectator()) {
                    player.changeGameMode(GameType.SURVIVAL);
                }
                ModNetworking.clearManhuntHud(player);
                player.sendSystemMessage(Component.literal("Manhunt ended.")
                    .withStyle(ChatFormatting.GRAY), false);
            }
        }

        autoManhuntMode = false;
        countdownTicks = 0;
    }

    public static void onSpeedrunnerWin(ServerPlayer speedrunner) {
        if (!isSpeedrunner(speedrunner)) {
            return;
        }

        long elapsed = System.currentTimeMillis() - startTime.getOrDefault(speedrunner.getUUID(), System.currentTimeMillis());
        String time = formatTime(elapsed);

        speedrunner.getServer().getPlayerList().broadcast(
            Component.literal("[MANHUNT] ")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                .append(Component.literal(speedrunner.getName().getString() + " beat the run in " + time + ".")
                    .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)),
            false
        );

        if (elapsed < 60L * 60L * 1000L) {
            AchievementManager.unlockAchievement(speedrunner, "speedrunner");
        }
        if (elapsed < 30L * 60L * 1000L) {
            AchievementManager.unlockAchievement(speedrunner, "speedrunner_30min");
        }
        if (isSoloSpeedrunner(speedrunner)) {
            AchievementManager.unlockAchievement(speedrunner, "lone_wolf");
        }

        endAllGames(speedrunner.getServer());
    }

    public static void onSpeedrunnerDeath(ServerPlayer speedrunner) {
        if (!isSpeedrunner(speedrunner)) {
            return;
        }

        UUID uuid = speedrunner.getUUID();
        int deathCount = deaths.getOrDefault(uuid, 0) + 1;
        deaths.put(uuid, deathCount);
        sendHudUpdate(speedrunner);

        ServerPlayer nearestHunter = findNearestHunter(speedrunner);
        if (nearestHunter != null) {
            int kills = hunterKills.getOrDefault(nearestHunter.getUUID(), 0) + 1;
            hunterKills.put(nearestHunter.getUUID(), kills);
            if (kills == 1) {
                AchievementManager.unlockAchievement(nearestHunter, "first_hunt");
            }
        }

        if (isSoloSpeedrunner(speedrunner)) {
            speedrunner.getServer().getPlayerList().broadcast(
                Component.literal("[MANHUNT] ")
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                    .append(Component.literal(speedrunner.getName().getString() + " died (" + deathCount + "/3).")
                        .withStyle(ChatFormatting.RED)),
                false
            );

            if (deathCount >= 3) {
                speedrunner.getServer().getPlayerList().broadcast(
                    Component.literal("[MANHUNT] ")
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                        .append(Component.literal(speedrunner.getName().getString() + " failed the solo run.")
                            .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD)),
                    false
                );
                endAllGames(speedrunner.getServer());
            } else {
                speedrunner.sendSystemMessage(Component.literal("Lives remaining: " + (3 - deathCount))
                    .withStyle(ChatFormatting.YELLOW), false);
            }
            return;
        }

        activeSpeedrunners.remove(uuid);
        ghostSpeedrunners.add(uuid);
        speedrunner.changeGameMode(GameType.SPECTATOR);

        speedrunner.getServer().getPlayerList().broadcast(
            Component.literal("[MANHUNT] ")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                .append(Component.literal(speedrunner.getName().getString() + " has been turned into a ghost.")
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)),
            false
        );

        speedrunner.sendSystemMessage(Component.literal("You are out for this life. Spectate the remaining speedrunners.")
            .withStyle(ChatFormatting.GRAY), false);

        checkHuntersWinCondition(speedrunner.getServer());
    }

    public static boolean useContextualAbility(ServerPlayer player) {
        if (!isInGame(player)) {
            return false;
        }

        if (isHunter(player) && hasHunterController(player)) {
            useSelectedHunterAbility(player);
            return true;
        }

        if (isSpeedrunner(player) && hasSpeedrunnerController(player)) {
            useSelectedSpeedrunnerAbility(player);
            return true;
        }

        return false;
    }

    public static boolean cycleContextualAbility(ServerPlayer player) {
        if (!isInGame(player)) {
            return false;
        }

        if (isHunter(player) && hasHunterController(player)) {
            cycleSelectedAbility(player, HUNTER_ABILITY_NAMES, HUNTER_ABILITY_DESCRIPTIONS, ChatFormatting.RED);
            return true;
        }

        if (isSpeedrunner(player) && hasSpeedrunnerController(player)) {
            cycleSelectedAbility(player, SPEEDRUNNER_ABILITY_NAMES, SPEEDRUNNER_ABILITY_DESCRIPTIONS, ChatFormatting.AQUA);
            return true;
        }

        return false;
    }

    public static boolean isSpeedrunner(Player player) {
        UUID uuid = player.getUUID();
        return participantTargets.containsKey(uuid) && participantTargets.get(uuid).equals(uuid);
    }

    public static boolean isSoloSpeedrunner(Player player) {
        return isSpeedrunner(player) && soloSpeedrunners.contains(player.getUUID());
    }

    public static boolean isHunter(Player player) {
        UUID uuid = player.getUUID();
        return participantTargets.containsKey(uuid) && !participantTargets.get(uuid).equals(uuid);
    }

    public static boolean isInGame(Player player) {
        return gameActive.getOrDefault(player.getUUID(), false);
    }

    public static boolean isCountdownActive() {
        return autoManhuntMode && countdownTicks > 0;
    }

    public static int getCountdownSeconds() {
        return Math.max(0, (int) Math.ceil(countdownTicks / 20.0));
    }

    public static int getActiveHunterCount() {
        return hunters.size();
    }

    public static int getActiveSpeedrunnerCount() {
        return activeSpeedrunners.size();
    }

    public static int getGhostSpeedrunnerCount() {
        return ghostSpeedrunners.size();
    }

    public static String getRole(Player player) {
        if (isHunter(player)) {
            return ROLE_HUNTER;
        }
        if (isSpeedrunner(player)) {
            return ROLE_SPEEDRUNNER;
        }
        return "";
    }

    public static ServerPlayer getTarget(ServerPlayer hunter) {
        UUID targetUuid = participantTargets.get(hunter.getUUID());
        if (targetUuid == null || hunter.getServer() == null || targetUuid.equals(hunter.getUUID())) {
            return null;
        }
        return hunter.getServer().getPlayerList().getPlayer(targetUuid);
    }

    public static ServerPlayer getPrimarySpeedrunner(MinecraftServer server) {
        if (server == null) {
            return null;
        }

        for (UUID uuid : activeSpeedrunners) {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player != null) {
                return player;
            }
        }

        for (UUID uuid : participantTargets.keySet()) {
            if (participantTargets.get(uuid).equals(uuid)) {
                ServerPlayer player = server.getPlayerList().getPlayer(uuid);
                if (player != null) {
                    return player;
                }
            }
        }

        return null;
    }

    public static void updateCompass(ServerPlayer hunter) {
        if (!isHunter(hunter)) {
            return;
        }

        ServerPlayer target = getTarget(hunter);
        if (target == null) {
            hunter.sendSystemMessage(Component.literal("No speedrunner target assigned.")
                .withStyle(ChatFormatting.GRAY), true);
            return;
        }

        if (target.level() == hunter.level()) {
            double distance = Math.sqrt(hunter.distanceToSqr(target));
            hunter.sendSystemMessage(Component.literal("Target: " + target.getName().getString() + " | " + (int) distance + " blocks")
                .withStyle(distance < 100 ? ChatFormatting.RED : ChatFormatting.YELLOW), true);
        } else {
            hunter.sendSystemMessage(Component.literal("Target is in " + getDimensionName((ServerLevel) target.level()))
                .withStyle(ChatFormatting.DARK_PURPLE), true);
        }
    }

    public static void tick(MinecraftServer server) {
        if (autoManhuntMode && countdownTicks > 0) {
            countdownTicks--;
            if (countdownTicks % 20 == 0) {
                int secondsLeft = countdownTicks / 20;
                if (secondsLeft > 0 && (secondsLeft <= 5 || secondsLeft % 10 == 0)) {
                    server.getPlayerList().broadcast(
                        Component.literal("[MANHUNT] ")
                            .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                            .append(Component.literal("Starting in " + secondsLeft + " seconds.")
                                .withStyle(ChatFormatting.YELLOW)),
                        false
                    );
                }
            }
            if (countdownTicks == 0) {
                startCountdownGame(server);
            } else {
                applyGlowEffects(server);
            }
        }

        for (ServerPlayer player : server.getPlayerList().getPlayerList()) {
            if (!isInGame(player)) {
                continue;
            }

            if (isHunter(player)) {
                tickCooldowns(player, hunterTrackCooldowns);
                tickCooldowns(player, hunterBlockCooldowns);
                tickCooldowns(player, hunterSlowCooldowns);
                if (server.getTickCount() % 20 == 0) {
                    updateCompass(player);
                    sendHudUpdate(player);
                }
            } else if (isSpeedrunner(player)) {
                tickCooldowns(player, speedrunnerEscapeCooldowns);
                tickCooldowns(player, speedrunnerSpeedCooldowns);
                tickCooldowns(player, speedrunnerInvisCooldowns);
                if (server.getTickCount() % 20 == 0) {
                    checkSoloTimeLimit(player);
                    sendHudUpdate(player);
                }
            }
        }
    }

    public static void useHunterTrackAbility(ServerPlayer hunter) {
        if (!isHunter(hunter)) {
            return;
        }

        int cooldown = hunterTrackCooldowns.getOrDefault(hunter.getUUID(), 0);
        if (cooldown > 0) {
            hunter.sendSystemMessage(Component.literal("Track is on cooldown for " + (cooldown / 20) + "s.")
                .withStyle(ChatFormatting.RED), true);
            return;
        }

        ServerPlayer target = getTarget(hunter);
        if (target == null) {
            hunter.sendSystemMessage(Component.literal("No target assigned.")
                .withStyle(ChatFormatting.RED), true);
            return;
        }

        hunterTrackCooldowns.put(hunter.getUUID(), HUNTER_TRACK_COOLDOWN);
        BlockPos targetPos = target.blockPosition();
        double distance = Math.sqrt(hunter.distanceToSqr(target));

        hunter.sendSystemMessage(Component.literal("Track ready.")
            .withStyle(ChatFormatting.GREEN), true);
        hunter.sendSystemMessage(Component.literal("Dimension: " + getDimensionName((ServerLevel) target.level()))
            .withStyle(ChatFormatting.AQUA), false);
        hunter.sendSystemMessage(Component.literal("Distance: " + String.format("%.1f", distance) + " blocks")
            .withStyle(ChatFormatting.YELLOW), false);
        hunter.sendSystemMessage(Component.literal("Coords: " + targetPos.getX() + ", " + targetPos.getY() + ", " + targetPos.getZ())
            .withStyle(ChatFormatting.GRAY), false);
        sendHudUpdate(hunter);
    }

    public static void useHunterBlockAbility(ServerPlayer hunter) {
        if (!isHunter(hunter)) {
            return;
        }

        int cooldown = hunterBlockCooldowns.getOrDefault(hunter.getUUID(), 0);
        if (cooldown > 0) {
            hunter.sendSystemMessage(Component.literal("Blockade is on cooldown for " + (cooldown / 20) + "s.")
                .withStyle(ChatFormatting.RED), true);
            return;
        }

        ServerPlayer target = getTarget(hunter);
        if (target == null || target.level() != hunter.level()) {
            hunter.sendSystemMessage(Component.literal("The target is not in your dimension.")
                .withStyle(ChatFormatting.RED), true);
            return;
        }

        if (hunter.distanceTo(target) > 30.0f) {
            hunter.sendSystemMessage(Component.literal("Blockade requires the target to be within 30 blocks.")
                .withStyle(ChatFormatting.RED), true);
            return;
        }

        hunterBlockCooldowns.put(hunter.getUUID(), HUNTER_BLOCK_COOLDOWN);

        Vec3 direction = hunter.position().subtract(target.position()).normalize();
        BlockPos wallCenter = target.blockPosition().add((int) (direction.x * 3), 0, (int) (direction.z * 3));
        ServerLevel world = (ServerLevel) hunter.level();

        for (int x = -2; x <= 2; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos placePos = wallCenter.add(x, y, z);
                    if (world.getBlockState(placePos).isAir()) {
                        world.setBlockState(placePos, Blocks.POLISHED_BLACKSTONE_BRICKS.getDefaultState());
                        world.scheduleBlockTick(placePos, Blocks.POLISHED_BLACKSTONE_BRICKS, 100);
                    }
                }
            }
        }

        hunter.sendSystemMessage(Component.literal("Blockade deployed.")
            .withStyle(ChatFormatting.GREEN), true);
        sendHudUpdate(hunter);
    }

    public static void useHunterSlowAbility(ServerPlayer hunter) {
        if (!isHunter(hunter)) {
            return;
        }

        int cooldown = hunterSlowCooldowns.getOrDefault(hunter.getUUID(), 0);
        if (cooldown > 0) {
            hunter.sendSystemMessage(Component.literal("Snare is on cooldown for " + (cooldown / 20) + "s.")
                .withStyle(ChatFormatting.RED), true);
            return;
        }

        ServerPlayer target = getTarget(hunter);
        if (target == null || target.level() != hunter.level()) {
            hunter.sendSystemMessage(Component.literal("The target is not in your dimension.")
                .withStyle(ChatFormatting.RED), true);
            return;
        }

        if (hunter.distanceTo(target) > 20.0f) {
            hunter.sendSystemMessage(Component.literal("Snare requires the target to be within 20 blocks.")
                .withStyle(ChatFormatting.RED), true);
            return;
        }

        hunterSlowCooldowns.put(hunter.getUUID(), HUNTER_SLOW_COOLDOWN);
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 2));
        target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 200, 1));

        hunter.sendSystemMessage(Component.literal("Target snared.")
            .withStyle(ChatFormatting.GREEN), true);
        target.sendSystemMessage(Component.literal("A hunter snared you.")
            .withStyle(ChatFormatting.RED), true);
        sendHudUpdate(hunter);
        sendHudUpdate(target);
    }

    public static void useSpeedrunnerEscapeAbility(ServerPlayer speedrunner) {
        if (!canUseSpeedrunnerAbility(speedrunner, speedrunnerEscapeCooldowns, "Escape")) {
            return;
        }

        speedrunnerEscapeCooldowns.put(speedrunner.getUUID(), SPEEDRUNNER_ESCAPE_COOLDOWN);

        ServerLevel world = (ServerLevel) speedrunner.level();
        BlockPos origin = speedrunner.blockPosition();
        BlockPos destination = origin;

        for (int attempts = 0; attempts < 12; attempts++) {
            int offsetX = world.random.nextInt(101) - 50;
            int offsetZ = world.random.nextInt(101) - 50;
            int topY = world.getTopY(Heightmap.Type.MOTION_BLOCKING, origin.getX() + offsetX, origin.getZ() + offsetZ);
            BlockPos candidate = new BlockPos(origin.getX() + offsetX, topY, origin.getZ() + offsetZ);
            if (world.getBlockState(candidate).isAir() && world.getBlockState(candidate.above()).isAir()) {
                destination = candidate;
                break;
            }
        }

        speedrunner.teleportTo(destination.getX() + 0.5, destination.getY(), destination.getZ() + 0.5);
        speedrunner.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 1));
        speedrunner.sendSystemMessage(Component.literal("Escape activated.")
            .withStyle(ChatFormatting.GREEN), true);
        sendHudUpdate(speedrunner);
    }

    public static void useSpeedrunnerSpeedAbility(ServerPlayer speedrunner) {
        if (!canUseSpeedrunnerAbility(speedrunner, speedrunnerSpeedCooldowns, "Burst")) {
            return;
        }

        speedrunnerSpeedCooldowns.put(speedrunner.getUUID(), SPEEDRUNNER_SPEED_COOLDOWN);
        speedrunner.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 300, 3));
        speedrunner.addEffect(new MobEffectInstance(MobEffects.JUMP, 300, 2));
        speedrunner.sendSystemMessage(Component.literal("Burst activated.")
            .withStyle(ChatFormatting.GREEN), true);
        sendHudUpdate(speedrunner);
    }

    public static void useSpeedrunnerInvisAbility(ServerPlayer speedrunner) {
        if (!canUseSpeedrunnerAbility(speedrunner, speedrunnerInvisCooldowns, "Veil")) {
            return;
        }

        speedrunnerInvisCooldowns.put(speedrunner.getUUID(), SPEEDRUNNER_INVIS_COOLDOWN);
        speedrunner.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 400, 0));
        speedrunner.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 1));
        speedrunner.sendSystemMessage(Component.literal("Veil activated.")
            .withStyle(ChatFormatting.GREEN), true);
        sendHudUpdate(speedrunner);
    }

    public static String getElapsedTime(ServerPlayer player) {
        if (!isSpeedrunner(player)) {
            return "N/A";
        }

        long started = startTime.getOrDefault(player.getUUID(), System.currentTimeMillis());
        return formatTime(System.currentTimeMillis() - started);
    }

    public static int getDeathCount(ServerPlayer player) {
        return deaths.getOrDefault(player.getUUID(), 0);
    }

    private static void startSpeedrunner(ServerPlayer speedrunner, boolean solo, boolean announce) {
        UUID uuid = speedrunner.getUUID();
        clearParticipantState(uuid);

        participantTargets.put(uuid, uuid);
        gameActive.put(uuid, true);
        startTime.put(uuid, System.currentTimeMillis());
        deaths.put(uuid, 0);
        playerRoles.put(uuid, ROLE_SPEEDRUNNER);
        selectedAbilityIndex.put(uuid, 0);
        speedrunnerEscapeCooldowns.put(uuid, 0);
        speedrunnerSpeedCooldowns.put(uuid, 0);
        speedrunnerInvisCooldowns.put(uuid, 0);
        hunters.remove(uuid);
        activeSpeedrunners.add(uuid);
        ghostSpeedrunners.remove(uuid);

        if (solo) {
            soloSpeedrunners.add(uuid);
        } else {
            soloSpeedrunners.remove(uuid);
        }

        if (speedrunner.isSpectator()) {
            speedrunner.changeGameMode(GameType.SURVIVAL);
        }

        if (announce) {
            speedrunner.getServer().getPlayerList().broadcast(
                Component.literal("[MANHUNT] ")
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                    .append(Component.literal(speedrunner.getName().getString() + " is now the speedrunner.")
                        .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD))
                    .append(Component.literal(solo ? " Solo mode is active." : "")
                        .withStyle(ChatFormatting.AQUA)),
                false
            );
        }

        speedrunner.sendSystemMessage(Component.literal("Hold a clock, press TAB to cycle Escape, Burst, Veil, then press R.")
            .withStyle(ChatFormatting.AQUA), false);
        sendHudUpdate(speedrunner);
    }

    private static void initializeHunter(ServerPlayer hunter, ServerPlayer target, boolean resetCooldowns) {
        UUID uuid = hunter.getUUID();
        clearParticipantState(uuid);

        participantTargets.put(uuid, target.getUUID());
        gameActive.put(uuid, true);
        deaths.put(uuid, 0);
        playerRoles.put(uuid, ROLE_HUNTER);
        selectedAbilityIndex.put(uuid, 0);
        hunters.add(uuid);
        soloSpeedrunners.remove(uuid);
        activeSpeedrunners.remove(uuid);
        ghostSpeedrunners.remove(uuid);

        if (resetCooldowns) {
            hunterTrackCooldowns.put(uuid, 0);
            hunterBlockCooldowns.put(uuid, 0);
            hunterSlowCooldowns.put(uuid, 0);
        }
    }

    private static void clearParticipantState(UUID uuid) {
        participantTargets.remove(uuid);
        gameActive.remove(uuid);
        startTime.remove(uuid);
        deaths.remove(uuid);
        playerRoles.remove(uuid);
        selectedAbilityIndex.remove(uuid);
        hunterTrackCooldowns.remove(uuid);
        hunterBlockCooldowns.remove(uuid);
        hunterSlowCooldowns.remove(uuid);
        speedrunnerEscapeCooldowns.remove(uuid);
        speedrunnerSpeedCooldowns.remove(uuid);
        speedrunnerInvisCooldowns.remove(uuid);
        hunterKills.remove(uuid);
        soloWarningMinutes.remove(uuid);

        hunters.remove(uuid);
        soloSpeedrunners.remove(uuid);
        activeSpeedrunners.remove(uuid);
        ghostSpeedrunners.remove(uuid);

        for (Map.Entry<UUID, UUID> entry : new HashMap<>(participantTargets).entrySet()) {
            if (entry.getValue().equals(uuid) && !entry.getKey().equals(uuid)) {
                participantTargets.remove(entry.getKey());
                gameActive.remove(entry.getKey());
                playerRoles.remove(entry.getKey());
                selectedAbilityIndex.remove(entry.getKey());
                hunterTrackCooldowns.remove(entry.getKey());
                hunterBlockCooldowns.remove(entry.getKey());
                hunterSlowCooldowns.remove(entry.getKey());
                hunters.remove(entry.getKey());
            }
        }
    }

    private static void resetAllState(MinecraftServer server, boolean notifyPlayers) {
        Set<UUID> uuids = getAllParticipantUuids();
        for (UUID uuid : uuids) {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            boolean wasGhost = ghostSpeedrunners.contains(uuid);
            clearParticipantState(uuid);

            if (player != null) {
                if (wasGhost && player.isSpectator()) {
                    player.changeGameMode(GameType.SURVIVAL);
                }
                ModNetworking.clearManhuntHud(player);
                if (notifyPlayers) {
                    player.sendSystemMessage(Component.literal("Manhunt ended.")
                        .withStyle(ChatFormatting.GRAY), false);
                }
            }
        }

        autoManhuntMode = false;
        countdownTicks = 0;
    }

    private static Set<UUID> getAllParticipantUuids() {
        Set<UUID> uuids = new HashSet<>();
        uuids.addAll(participantTargets.keySet());
        uuids.addAll(gameActive.keySet());
        uuids.addAll(playerRoles.keySet());
        uuids.addAll(activeSpeedrunners);
        uuids.addAll(ghostSpeedrunners);
        uuids.addAll(hunters);
        return uuids;
    }

    private static void startCountdownGame(MinecraftServer server) {
        autoManhuntMode = false;
        countdownTicks = 0;

        List<ServerPlayer> speedrunnerPlayers = new ArrayList<>();
        List<ServerPlayer> hunterPlayers = new ArrayList<>();
        for (Map.Entry<UUID, String> entry : playerRoles.entrySet()) {
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            if (player == null) {
                continue;
            }
            if (ROLE_SPEEDRUNNER.equals(entry.getValue())) {
                speedrunnerPlayers.add(player);
            } else if (ROLE_HUNTER.equals(entry.getValue())) {
                hunterPlayers.add(player);
            }
        }

        if (speedrunnerPlayers.isEmpty()) {
            return;
        }

        for (ServerPlayer speedrunner : speedrunnerPlayers) {
            startSpeedrunner(speedrunner, false, false);
            giveSpeedrunnerClock(speedrunner);
        }

        ServerPlayer primaryTarget = speedrunnerPlayers.get(0);
        for (ServerPlayer hunter : hunterPlayers) {
            initializeHunter(hunter, primaryTarget, true);
            giveHunterClock(hunter);
            hunter.sendSystemMessage(Component.literal("Hold your tracker or compass, press TAB to cycle Track, Blockade, Snare, then press R.")
                .withStyle(ChatFormatting.RED), false);
            sendHudUpdate(hunter);
        }

        server.getPlayerList().broadcast(
            Component.literal("[MANHUNT] ")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                .append(Component.literal("GO! The hunt begins.")
                    .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)),
            false
        );
    }

    private static void applyGlowEffects(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayerList()) {
            String role = playerRoles.get(player.getUUID());
            if (ROLE_HUNTER.equals(role)) {
                player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0, false, false));
            } else if (ROLE_SPEEDRUNNER.equals(role)) {
                player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0, false, false));
            }
        }
    }

    private static void giveHunterClock(ServerPlayer hunter) {
        if (ModItems.HUNTER_TRACKER != null) {
            hunter.getInventory().add(new ItemStack(ModItems.HUNTER_TRACKER));
        }
        if (ModItems.MANHUNT_COMPASS != null) {
            hunter.getInventory().add(new ItemStack(ModItems.MANHUNT_COMPASS));
        }
    }

    private static void giveSpeedrunnerClock(ServerPlayer speedrunner) {
        speedrunner.getInventory().add(new ItemStack(Items.CLOCK));
    }

    private static void tickCooldowns(ServerPlayer player, Map<UUID, Integer> cooldowns) {
        UUID uuid = player.getUUID();
        int current = cooldowns.getOrDefault(uuid, 0);
        if (current > 0) {
            cooldowns.put(uuid, current - 1);
        }
    }

    private static void checkSoloTimeLimit(ServerPlayer player) {
        if (!isSoloSpeedrunner(player)) {
            return;
        }

        long elapsed = System.currentTimeMillis() - startTime.getOrDefault(player.getUUID(), System.currentTimeMillis());
        long timeLimit = 60L * 60L * 1000L;
        if (elapsed >= timeLimit) {
            player.getServer().getPlayerList().broadcast(
                Component.literal("[MANHUNT] ")
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                    .append(Component.literal(player.getName().getString() + " ran out of time.")
                        .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD)),
                false
            );
            endAllGames(player.getServer());
            return;
        }

        long remainingMinutes = (timeLimit - elapsed) / 1000L / 60L;
        if (remainingMinutes <= 5) {
            int previous = soloWarningMinutes.getOrDefault(player.getUUID(), Integer.MAX_VALUE);
            if (remainingMinutes < previous) {
                soloWarningMinutes.put(player.getUUID(), (int) remainingMinutes);
                player.sendSystemMessage(Component.literal("Solo timer: " + remainingMinutes + " minute(s) left.")
                    .withStyle(ChatFormatting.RED), true);
            }
        }
    }

    private static void useSelectedHunterAbility(ServerPlayer hunter) {
        int index = selectedAbilityIndex.getOrDefault(hunter.getUUID(), 0);
        switch (index) {
            case 0 -> useHunterTrackAbility(hunter);
            case 1 -> useHunterBlockAbility(hunter);
            case 2 -> useHunterSlowAbility(hunter);
            default -> useHunterTrackAbility(hunter);
        }
    }

    private static void useSelectedSpeedrunnerAbility(ServerPlayer speedrunner) {
        int index = selectedAbilityIndex.getOrDefault(speedrunner.getUUID(), 0);
        switch (index) {
            case 0 -> useSpeedrunnerEscapeAbility(speedrunner);
            case 1 -> useSpeedrunnerSpeedAbility(speedrunner);
            case 2 -> useSpeedrunnerInvisAbility(speedrunner);
            default -> useSpeedrunnerEscapeAbility(speedrunner);
        }
    }

    private static void cycleSelectedAbility(ServerPlayer player, String[] names, String[] descriptions, ChatFormatting color) {
        UUID uuid = player.getUUID();
        int next = (selectedAbilityIndex.getOrDefault(uuid, 0) + 1) % ABILITY_COUNT;
        selectedAbilityIndex.put(uuid, next);

        player.sendSystemMessage(Component.literal("Selected " + names[next] + ": " + descriptions[next])
            .withStyle(color), true);
        ModNetworking.showTitle(player, names[next], descriptions[next], color);
        sendHudUpdate(player);
    }

    private static boolean canUseSpeedrunnerAbility(ServerPlayer speedrunner, Map<UUID, Integer> cooldowns, String label) {
        if (!isSpeedrunner(speedrunner)) {
            return false;
        }

        if (!activeSpeedrunners.contains(speedrunner.getUUID())) {
            speedrunner.sendSystemMessage(Component.literal("Ghost speedrunners cannot use abilities.")
                .withStyle(ChatFormatting.RED), true);
            return false;
        }

        int cooldown = cooldowns.getOrDefault(speedrunner.getUUID(), 0);
        if (cooldown > 0) {
            speedrunner.sendSystemMessage(Component.literal(label + " is on cooldown for " + (cooldown / 20) + "s.")
                .withStyle(ChatFormatting.RED), true);
            return false;
        }

        return true;
    }

    private static void sendHudUpdate(ServerPlayer player) {
        if (!isInGame(player)) {
            ModNetworking.clearManhuntHud(player);
            return;
        }

        boolean hunter = isHunter(player);
        int index = selectedAbilityIndex.getOrDefault(player.getUUID(), 0);
        String[] names = hunter ? HUNTER_ABILITY_NAMES : SPEEDRUNNER_ABILITY_NAMES;
        String[] descriptions = hunter ? HUNTER_ABILITY_DESCRIPTIONS : SPEEDRUNNER_ABILITY_DESCRIPTIONS;
        String targetName = "";
        String elapsedTime = "N/A";

        if (hunter) {
            ServerPlayer target = getTarget(player);
            if (target != null) {
                targetName = target.getName().getString();
                elapsedTime = getElapsedOrDefault(target);
            }
        } else {
            elapsedTime = getElapsedOrDefault(player);
        }

        ModNetworking.sendManhuntHudUpdate(
            player,
            true,
            hunter ? ROLE_HUNTER : ROLE_SPEEDRUNNER,
            elapsedTime,
            deaths.getOrDefault(player.getUUID(), 0),
            targetName,
            index,
            names[index],
            descriptions[index],
            hunterTrackCooldowns.getOrDefault(player.getUUID(), 0),
            hunterBlockCooldowns.getOrDefault(player.getUUID(), 0),
            hunterSlowCooldowns.getOrDefault(player.getUUID(), 0),
            speedrunnerEscapeCooldowns.getOrDefault(player.getUUID(), 0),
            speedrunnerSpeedCooldowns.getOrDefault(player.getUUID(), 0),
            speedrunnerInvisCooldowns.getOrDefault(player.getUUID(), 0)
        );
    }

    private static String getElapsedOrDefault(ServerPlayer player) {
        if (!isSpeedrunner(player)) {
            return "N/A";
        }
        return formatTime(System.currentTimeMillis() - startTime.getOrDefault(player.getUUID(), System.currentTimeMillis()));
    }

    private static ServerPlayer findNearestHunter(ServerPlayer speedrunner) {
        ServerPlayer nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (UUID hunterUuid : hunters) {
            ServerPlayer hunter = speedrunner.getServer().getPlayerList().getPlayer(hunterUuid);
            if (hunter == null || hunter.level() != speedrunner.level()) {
                continue;
            }
            double distance = hunter.squaredDistanceTo(speedrunner);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = hunter;
            }
        }
        return nearest;
    }

    private static void checkHuntersWinCondition(MinecraftServer server) {
        boolean hasAnySpeedrunner = false;
        for (UUID uuid : participantTargets.keySet()) {
            if (participantTargets.get(uuid).equals(uuid)) {
                hasAnySpeedrunner = true;
                break;
            }
        }

        if (!hasAnySpeedrunner || !activeSpeedrunners.isEmpty()) {
            return;
        }

        server.getPlayerList().broadcast(
            Component.literal("[MANHUNT] ")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                .append(Component.literal("Hunters win. All speedrunners were eliminated.")
                    .withStyle(ChatFormatting.RED, ChatFormatting.BOLD)),
            false
        );

        for (UUID hunterUuid : hunters) {
            ServerPlayer hunter = server.getPlayerList().getPlayer(hunterUuid);
            if (hunter != null) {
                AchievementManager.unlockAchievement(hunter, "team_hunter");
            }
        }

        endAllGames(server);
    }

    private static boolean hasHunterController(ServerPlayer player) {
        return isHunterController(player.getMainHandStack()) || isHunterController(player.getOffHandStack());
    }

    private static boolean isHunterController(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        if ((ModItems.HUNTER_TRACKER != null && stack.isOf(ModItems.HUNTER_TRACKER))
            || (ModItems.MANHUNT_COMPASS != null && stack.isOf(ModItems.MANHUNT_COMPASS))
            || ManhuntCompassItem.isManhuntCompass(stack)) {
            return true;
        }

        CompoundTag nbt = stack.getNbt();
        return nbt != null && nbt.getBoolean(HunterTrackerItem.HUNTER_TRACKER_NBT);
    }

    private static boolean hasSpeedrunnerController(ServerPlayer player) {
        return isClock(player.getMainHandStack()) || isClock(player.getOffHandStack());
    }

    private static boolean isClock(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.isOf(Items.CLOCK);
    }

    private static String getDimensionName(ServerLevel world) {
        if (world.getDimensionKey().equals(DimensionTypes.OVERWORLD)) {
            return "Overworld";
        }
        if (world.getDimensionKey().equals(DimensionTypes.THE_NETHER)) {
            return "Nether";
        }
        if (world.getDimensionKey().equals(DimensionTypes.THE_END)) {
            return "End";
        }
        return "Unknown";
    }

    private static String formatTime(long millis) {
        long totalSeconds = Math.max(0L, millis / 1000L);
        long hours = totalSeconds / 3600L;
        long minutes = (totalSeconds % 3600L) / 60L;
        long seconds = totalSeconds % 60L;
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("%d:%02d", minutes, seconds);
    }

    private static String getGroupingName(ServerPlayer player) {
        String guildName = GuildManager.getGuildName(player.level());
        if (guildName != null && !guildName.isBlank()) {
            return guildName;
        }

        String teamName = TeamManager.getPlayerTeam(player.level());
        return teamName != null ? teamName : "";
    }

    private static String joinNames(List<ServerPlayer> players) {
        List<String> names = new ArrayList<>();
        for (ServerPlayer player : players) {
            names.add(player.getName().getString());
        }
        return String.join(", ", names);
    }
}
