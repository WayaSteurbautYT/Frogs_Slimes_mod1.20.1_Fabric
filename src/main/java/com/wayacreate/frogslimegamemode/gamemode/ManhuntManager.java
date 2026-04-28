package com.wayacreate.frogslimegamemode.gamemode;

import com.wayacreate.frogslimegamemode.achievements.AchievementManager;
import com.wayacreate.frogslimegamemode.guild.GuildManager;
import com.wayacreate.frogslimegamemode.item.HunterTrackerItem;
import com.wayacreate.frogslimegamemode.item.ManhuntCompassItem;
import com.wayacreate.frogslimegamemode.item.ModItems;
import com.wayacreate.frogslimegamemode.network.ModNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.Heightmap;
import net.minecraft.world.dimension.DimensionTypes;

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

    public static void setSpeedrunner(ServerPlayerEntity speedrunner) {
        startSpeedrunner(speedrunner, false, true);
        giveSpeedrunnerClock(speedrunner);
    }

    public static void setSoloSpeedrunner(ServerPlayerEntity speedrunner) {
        startSpeedrunner(speedrunner, true, true);
        giveSpeedrunnerClock(speedrunner);
    }

    public static void startAutoManhunt(ServerPlayerEntity initiator) {
        MinecraftServer server = initiator.getServer();
        if (server == null) {
            return;
        }

        List<ServerPlayerEntity> players = new ArrayList<>(server.getPlayerManager().getPlayerList());
        if (players.size() < 2) {
            initiator.sendMessage(Text.literal("Need at least 2 players for auto manhunt!")
                .formatted(Formatting.RED), false);
            return;
        }

        resetAllState(server, false);
        autoManhuntMode = true;
        countdownTicks = COUNTDOWN_SECONDS * 20;

        Collections.shuffle(players);
        ServerPlayerEntity chosenSpeedrunner = players.get(0);
        playerRoles.put(chosenSpeedrunner.getUuid(), ROLE_SPEEDRUNNER);
        for (int i = 1; i < players.size(); i++) {
            playerRoles.put(players.get(i).getUuid(), ROLE_HUNTER);
        }

        server.getPlayerManager().broadcast(
            Text.literal("[MANHUNT] ")
                .formatted(Formatting.GOLD, Formatting.BOLD)
                .append(Text.literal("Auto Manhunt starts in " + COUNTDOWN_SECONDS + " seconds.")
                    .formatted(Formatting.YELLOW)),
            false
        );
        server.getPlayerManager().broadcast(
            Text.literal("[MANHUNT] ")
                .formatted(Formatting.GOLD)
                .append(Text.literal(chosenSpeedrunner.getName().getString() + " is the speedrunner.")
                    .formatted(Formatting.GREEN, Formatting.BOLD)),
            false
        );
    }

    public static void startTeamManhunt(ServerPlayerEntity initiator, String speedrunnerTeam, String hunterTeam) {
        MinecraftServer server = initiator.getServer();
        if (server == null) {
            return;
        }

        List<ServerPlayerEntity> players = new ArrayList<>(server.getPlayerManager().getPlayerList());
        if (players.size() < 2) {
            initiator.sendMessage(Text.literal("Need at least 2 players for team manhunt!")
                .formatted(Formatting.RED), false);
            return;
        }

        resetAllState(server, false);
        autoManhuntMode = true;
        countdownTicks = COUNTDOWN_SECONDS * 20;

        List<ServerPlayerEntity> speedrunnerPlayers = new ArrayList<>();
        List<ServerPlayerEntity> hunterPlayers = new ArrayList<>();
        for (ServerPlayerEntity player : players) {
            String grouping = getGroupingName(player);
            if (speedrunnerTeam.equalsIgnoreCase(grouping)) {
                speedrunnerPlayers.add(player);
                playerRoles.put(player.getUuid(), ROLE_SPEEDRUNNER);
            } else if (hunterTeam.equalsIgnoreCase(grouping)) {
                hunterPlayers.add(player);
                playerRoles.put(player.getUuid(), ROLE_HUNTER);
            }
        }

        if (speedrunnerPlayers.isEmpty()) {
            initiator.sendMessage(Text.literal("Speedrunner team has no online members!")
                .formatted(Formatting.RED), false);
            autoManhuntMode = false;
            countdownTicks = 0;
            playerRoles.clear();
            return;
        }

        if (hunterPlayers.isEmpty()) {
            initiator.sendMessage(Text.literal("Hunter team has no online members!")
                .formatted(Formatting.RED), false);
            autoManhuntMode = false;
            countdownTicks = 0;
            playerRoles.clear();
            return;
        }

        server.getPlayerManager().broadcast(
            Text.literal("[MANHUNT] ")
                .formatted(Formatting.GOLD, Formatting.BOLD)
                .append(Text.literal("Team Manhunt starts in " + COUNTDOWN_SECONDS + " seconds.")
                    .formatted(Formatting.YELLOW)),
            false
        );
        server.getPlayerManager().broadcast(
            Text.literal("[MANHUNT] ")
                .formatted(Formatting.GOLD)
                .append(Text.literal("Speedrunners: " + joinNames(speedrunnerPlayers))
                    .formatted(Formatting.GREEN)),
            false
        );
        server.getPlayerManager().broadcast(
            Text.literal("[MANHUNT] ")
                .formatted(Formatting.GOLD)
                .append(Text.literal("Hunters: " + joinNames(hunterPlayers))
                    .formatted(Formatting.RED)),
            false
        );
    }

    public static void setHunter(ServerPlayerEntity hunter, ServerPlayerEntity target) {
        if (hunter == target) {
            hunter.sendMessage(Text.literal("You cannot hunt yourself.")
                .formatted(Formatting.RED), false);
            return;
        }

        if (isHunter(target)) {
            hunter.sendMessage(Text.literal("Cannot target another hunter.")
                .formatted(Formatting.RED), false);
            return;
        }

        UUID uuid = hunter.getUuid();
        initializeHunter(hunter, target, true);

        hunter.sendMessage(Text.literal("You are now hunting ")
            .formatted(Formatting.RED)
            .append(Text.literal(target.getName().getString()).formatted(Formatting.YELLOW, Formatting.BOLD))
            .append(Text.literal(". Hold a tracker or manhunt compass, press TAB to cycle, then R to use.")
                .formatted(Formatting.RED)), false);

        giveHunterClock(hunter);
        sendHudUpdate(hunter);
    }

    public static void endGame(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        boolean wasGhost = ghostSpeedrunners.contains(uuid);
        clearParticipantState(uuid);

        if (wasGhost && player.isSpectator()) {
            player.changeGameMode(GameMode.SURVIVAL);
        }

        ModNetworking.clearManhuntHud(player);
        player.sendMessage(Text.literal("Manhunt ended for you.")
            .formatted(Formatting.GRAY), false);
    }

    public static void endAllGames(MinecraftServer server) {
        if (server == null) {
            return;
        }

        Set<UUID> uuids = getAllParticipantUuids();
        for (UUID uuid : uuids) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            boolean wasGhost = ghostSpeedrunners.contains(uuid);
            clearParticipantState(uuid);

            if (player != null) {
                if (wasGhost && player.isSpectator()) {
                    player.changeGameMode(GameMode.SURVIVAL);
                }
                ModNetworking.clearManhuntHud(player);
                player.sendMessage(Text.literal("Manhunt ended.")
                    .formatted(Formatting.GRAY), false);
            }
        }

        autoManhuntMode = false;
        countdownTicks = 0;
    }

    public static void onSpeedrunnerWin(ServerPlayerEntity speedrunner) {
        if (!isSpeedrunner(speedrunner)) {
            return;
        }

        long elapsed = System.currentTimeMillis() - startTime.getOrDefault(speedrunner.getUuid(), System.currentTimeMillis());
        String time = formatTime(elapsed);

        speedrunner.getServer().getPlayerManager().broadcast(
            Text.literal("[MANHUNT] ")
                .formatted(Formatting.GOLD, Formatting.BOLD)
                .append(Text.literal(speedrunner.getName().getString() + " beat the run in " + time + ".")
                    .formatted(Formatting.GREEN, Formatting.BOLD)),
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

    public static void onSpeedrunnerDeath(ServerPlayerEntity speedrunner) {
        if (!isSpeedrunner(speedrunner)) {
            return;
        }

        UUID uuid = speedrunner.getUuid();
        int deathCount = deaths.getOrDefault(uuid, 0) + 1;
        deaths.put(uuid, deathCount);
        sendHudUpdate(speedrunner);

        ServerPlayerEntity nearestHunter = findNearestHunter(speedrunner);
        if (nearestHunter != null) {
            int kills = hunterKills.getOrDefault(nearestHunter.getUuid(), 0) + 1;
            hunterKills.put(nearestHunter.getUuid(), kills);
            if (kills == 1) {
                AchievementManager.unlockAchievement(nearestHunter, "first_hunt");
            }
        }

        if (isSoloSpeedrunner(speedrunner)) {
            speedrunner.getServer().getPlayerManager().broadcast(
                Text.literal("[MANHUNT] ")
                    .formatted(Formatting.GOLD, Formatting.BOLD)
                    .append(Text.literal(speedrunner.getName().getString() + " died (" + deathCount + "/3).")
                        .formatted(Formatting.RED)),
                false
            );

            if (deathCount >= 3) {
                speedrunner.getServer().getPlayerManager().broadcast(
                    Text.literal("[MANHUNT] ")
                        .formatted(Formatting.GOLD, Formatting.BOLD)
                        .append(Text.literal(speedrunner.getName().getString() + " failed the solo run.")
                            .formatted(Formatting.DARK_RED, Formatting.BOLD)),
                    false
                );
                endAllGames(speedrunner.getServer());
            } else {
                speedrunner.sendMessage(Text.literal("Lives remaining: " + (3 - deathCount))
                    .formatted(Formatting.YELLOW), false);
            }
            return;
        }

        activeSpeedrunners.remove(uuid);
        ghostSpeedrunners.add(uuid);
        speedrunner.changeGameMode(GameMode.SPECTATOR);

        speedrunner.getServer().getPlayerManager().broadcast(
            Text.literal("[MANHUNT] ")
                .formatted(Formatting.GOLD, Formatting.BOLD)
                .append(Text.literal(speedrunner.getName().getString() + " has been turned into a ghost.")
                    .formatted(Formatting.GRAY, Formatting.ITALIC)),
            false
        );

        speedrunner.sendMessage(Text.literal("You are out for this life. Spectate the remaining speedrunners.")
            .formatted(Formatting.GRAY), false);

        checkHuntersWinCondition(speedrunner.getServer());
    }

    public static boolean useContextualAbility(ServerPlayerEntity player) {
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

    public static boolean cycleContextualAbility(ServerPlayerEntity player) {
        if (!isInGame(player)) {
            return false;
        }

        if (isHunter(player) && hasHunterController(player)) {
            cycleSelectedAbility(player, HUNTER_ABILITY_NAMES, HUNTER_ABILITY_DESCRIPTIONS, Formatting.RED);
            return true;
        }

        if (isSpeedrunner(player) && hasSpeedrunnerController(player)) {
            cycleSelectedAbility(player, SPEEDRUNNER_ABILITY_NAMES, SPEEDRUNNER_ABILITY_DESCRIPTIONS, Formatting.AQUA);
            return true;
        }

        return false;
    }

    public static boolean isSpeedrunner(PlayerEntity player) {
        UUID uuid = player.getUuid();
        return participantTargets.containsKey(uuid) && participantTargets.get(uuid).equals(uuid);
    }

    public static boolean isSoloSpeedrunner(PlayerEntity player) {
        return isSpeedrunner(player) && soloSpeedrunners.contains(player.getUuid());
    }

    public static boolean isHunter(PlayerEntity player) {
        UUID uuid = player.getUuid();
        return participantTargets.containsKey(uuid) && !participantTargets.get(uuid).equals(uuid);
    }

    public static boolean isInGame(PlayerEntity player) {
        return gameActive.getOrDefault(player.getUuid(), false);
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

    public static String getRole(PlayerEntity player) {
        if (isHunter(player)) {
            return ROLE_HUNTER;
        }
        if (isSpeedrunner(player)) {
            return ROLE_SPEEDRUNNER;
        }
        return "";
    }

    public static ServerPlayerEntity getTarget(ServerPlayerEntity hunter) {
        UUID targetUuid = participantTargets.get(hunter.getUuid());
        if (targetUuid == null || hunter.getServer() == null || targetUuid.equals(hunter.getUuid())) {
            return null;
        }
        return hunter.getServer().getPlayerManager().getPlayer(targetUuid);
    }

    public static ServerPlayerEntity getPrimarySpeedrunner(MinecraftServer server) {
        if (server == null) {
            return null;
        }

        for (UUID uuid : activeSpeedrunners) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            if (player != null) {
                return player;
            }
        }

        for (UUID uuid : participantTargets.keySet()) {
            if (participantTargets.get(uuid).equals(uuid)) {
                ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
                if (player != null) {
                    return player;
                }
            }
        }

        return null;
    }

    public static void updateCompass(ServerPlayerEntity hunter) {
        if (!isHunter(hunter)) {
            return;
        }

        ServerPlayerEntity target = getTarget(hunter);
        if (target == null) {
            hunter.sendMessage(Text.literal("No speedrunner target assigned.")
                .formatted(Formatting.GRAY), true);
            return;
        }

        if (target.getWorld() == hunter.getWorld()) {
            double distance = Math.sqrt(hunter.squaredDistanceTo(target));
            hunter.sendMessage(Text.literal("Target: " + target.getName().getString() + " | " + (int) distance + " blocks")
                .formatted(distance < 100 ? Formatting.RED : Formatting.YELLOW), true);
        } else {
            hunter.sendMessage(Text.literal("Target is in " + getDimensionName((ServerWorld) target.getWorld()))
                .formatted(Formatting.DARK_PURPLE), true);
        }
    }

    public static void tick(MinecraftServer server) {
        if (autoManhuntMode && countdownTicks > 0) {
            countdownTicks--;
            if (countdownTicks % 20 == 0) {
                int secondsLeft = countdownTicks / 20;
                if (secondsLeft > 0 && (secondsLeft <= 5 || secondsLeft % 10 == 0)) {
                    server.getPlayerManager().broadcast(
                        Text.literal("[MANHUNT] ")
                            .formatted(Formatting.GOLD, Formatting.BOLD)
                            .append(Text.literal("Starting in " + secondsLeft + " seconds.")
                                .formatted(Formatting.YELLOW)),
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

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (!isInGame(player)) {
                continue;
            }

            if (isHunter(player)) {
                tickCooldowns(player, hunterTrackCooldowns);
                tickCooldowns(player, hunterBlockCooldowns);
                tickCooldowns(player, hunterSlowCooldowns);
                if (server.getTicks() % 20 == 0) {
                    updateCompass(player);
                    sendHudUpdate(player);
                }
            } else if (isSpeedrunner(player)) {
                tickCooldowns(player, speedrunnerEscapeCooldowns);
                tickCooldowns(player, speedrunnerSpeedCooldowns);
                tickCooldowns(player, speedrunnerInvisCooldowns);
                if (server.getTicks() % 20 == 0) {
                    checkSoloTimeLimit(player);
                    sendHudUpdate(player);
                }
            }
        }
    }

    public static void useHunterTrackAbility(ServerPlayerEntity hunter) {
        if (!isHunter(hunter)) {
            return;
        }

        int cooldown = hunterTrackCooldowns.getOrDefault(hunter.getUuid(), 0);
        if (cooldown > 0) {
            hunter.sendMessage(Text.literal("Track is on cooldown for " + (cooldown / 20) + "s.")
                .formatted(Formatting.RED), true);
            return;
        }

        ServerPlayerEntity target = getTarget(hunter);
        if (target == null) {
            hunter.sendMessage(Text.literal("No target assigned.")
                .formatted(Formatting.RED), true);
            return;
        }

        hunterTrackCooldowns.put(hunter.getUuid(), HUNTER_TRACK_COOLDOWN);
        BlockPos targetPos = target.getBlockPos();
        double distance = Math.sqrt(hunter.squaredDistanceTo(target));

        hunter.sendMessage(Text.literal("Track ready.")
            .formatted(Formatting.GREEN), true);
        hunter.sendMessage(Text.literal("Dimension: " + getDimensionName((ServerWorld) target.getWorld()))
            .formatted(Formatting.AQUA), false);
        hunter.sendMessage(Text.literal("Distance: " + String.format("%.1f", distance) + " blocks")
            .formatted(Formatting.YELLOW), false);
        hunter.sendMessage(Text.literal("Coords: " + targetPos.getX() + ", " + targetPos.getY() + ", " + targetPos.getZ())
            .formatted(Formatting.GRAY), false);
        sendHudUpdate(hunter);
    }

    public static void useHunterBlockAbility(ServerPlayerEntity hunter) {
        if (!isHunter(hunter)) {
            return;
        }

        int cooldown = hunterBlockCooldowns.getOrDefault(hunter.getUuid(), 0);
        if (cooldown > 0) {
            hunter.sendMessage(Text.literal("Blockade is on cooldown for " + (cooldown / 20) + "s.")
                .formatted(Formatting.RED), true);
            return;
        }

        ServerPlayerEntity target = getTarget(hunter);
        if (target == null || target.getWorld() != hunter.getWorld()) {
            hunter.sendMessage(Text.literal("The target is not in your dimension.")
                .formatted(Formatting.RED), true);
            return;
        }

        if (hunter.distanceTo(target) > 30.0f) {
            hunter.sendMessage(Text.literal("Blockade requires the target to be within 30 blocks.")
                .formatted(Formatting.RED), true);
            return;
        }

        hunterBlockCooldowns.put(hunter.getUuid(), HUNTER_BLOCK_COOLDOWN);

        Vec3d direction = hunter.getPos().subtract(target.getPos()).normalize();
        BlockPos wallCenter = target.getBlockPos().add((int) (direction.x * 3), 0, (int) (direction.z * 3));
        ServerWorld world = (ServerWorld) hunter.getWorld();

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

        hunter.sendMessage(Text.literal("Blockade deployed.")
            .formatted(Formatting.GREEN), true);
        sendHudUpdate(hunter);
    }

    public static void useHunterSlowAbility(ServerPlayerEntity hunter) {
        if (!isHunter(hunter)) {
            return;
        }

        int cooldown = hunterSlowCooldowns.getOrDefault(hunter.getUuid(), 0);
        if (cooldown > 0) {
            hunter.sendMessage(Text.literal("Snare is on cooldown for " + (cooldown / 20) + "s.")
                .formatted(Formatting.RED), true);
            return;
        }

        ServerPlayerEntity target = getTarget(hunter);
        if (target == null || target.getWorld() != hunter.getWorld()) {
            hunter.sendMessage(Text.literal("The target is not in your dimension.")
                .formatted(Formatting.RED), true);
            return;
        }

        if (hunter.distanceTo(target) > 20.0f) {
            hunter.sendMessage(Text.literal("Snare requires the target to be within 20 blocks.")
                .formatted(Formatting.RED), true);
            return;
        }

        hunterSlowCooldowns.put(hunter.getUuid(), HUNTER_SLOW_COOLDOWN);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 200, 2));
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 200, 1));

        hunter.sendMessage(Text.literal("Target snared.")
            .formatted(Formatting.GREEN), true);
        target.sendMessage(Text.literal("A hunter snared you.")
            .formatted(Formatting.RED), true);
        sendHudUpdate(hunter);
        sendHudUpdate(target);
    }

    public static void useSpeedrunnerEscapeAbility(ServerPlayerEntity speedrunner) {
        if (!canUseSpeedrunnerAbility(speedrunner, speedrunnerEscapeCooldowns, "Escape")) {
            return;
        }

        speedrunnerEscapeCooldowns.put(speedrunner.getUuid(), SPEEDRUNNER_ESCAPE_COOLDOWN);

        ServerWorld world = (ServerWorld) speedrunner.getWorld();
        BlockPos origin = speedrunner.getBlockPos();
        BlockPos destination = origin;

        for (int attempts = 0; attempts < 12; attempts++) {
            int offsetX = world.random.nextInt(101) - 50;
            int offsetZ = world.random.nextInt(101) - 50;
            int topY = world.getTopY(Heightmap.Type.MOTION_BLOCKING, origin.getX() + offsetX, origin.getZ() + offsetZ);
            BlockPos candidate = new BlockPos(origin.getX() + offsetX, topY, origin.getZ() + offsetZ);
            if (world.getBlockState(candidate).isAir() && world.getBlockState(candidate.up()).isAir()) {
                destination = candidate;
                break;
            }
        }

        speedrunner.teleport(destination.getX() + 0.5, destination.getY(), destination.getZ() + 0.5);
        speedrunner.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 200, 1));
        speedrunner.sendMessage(Text.literal("Escape activated.")
            .formatted(Formatting.GREEN), true);
        sendHudUpdate(speedrunner);
    }

    public static void useSpeedrunnerSpeedAbility(ServerPlayerEntity speedrunner) {
        if (!canUseSpeedrunnerAbility(speedrunner, speedrunnerSpeedCooldowns, "Burst")) {
            return;
        }

        speedrunnerSpeedCooldowns.put(speedrunner.getUuid(), SPEEDRUNNER_SPEED_COOLDOWN);
        speedrunner.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 300, 3));
        speedrunner.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 300, 2));
        speedrunner.sendMessage(Text.literal("Burst activated.")
            .formatted(Formatting.GREEN), true);
        sendHudUpdate(speedrunner);
    }

    public static void useSpeedrunnerInvisAbility(ServerPlayerEntity speedrunner) {
        if (!canUseSpeedrunnerAbility(speedrunner, speedrunnerInvisCooldowns, "Veil")) {
            return;
        }

        speedrunnerInvisCooldowns.put(speedrunner.getUuid(), SPEEDRUNNER_INVIS_COOLDOWN);
        speedrunner.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 400, 0));
        speedrunner.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 400, 1));
        speedrunner.sendMessage(Text.literal("Veil activated.")
            .formatted(Formatting.GREEN), true);
        sendHudUpdate(speedrunner);
    }

    public static String getElapsedTime(ServerPlayerEntity player) {
        if (!isSpeedrunner(player)) {
            return "N/A";
        }

        long started = startTime.getOrDefault(player.getUuid(), System.currentTimeMillis());
        return formatTime(System.currentTimeMillis() - started);
    }

    public static int getDeathCount(ServerPlayerEntity player) {
        return deaths.getOrDefault(player.getUuid(), 0);
    }

    private static void startSpeedrunner(ServerPlayerEntity speedrunner, boolean solo, boolean announce) {
        UUID uuid = speedrunner.getUuid();
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
            speedrunner.changeGameMode(GameMode.SURVIVAL);
        }

        if (announce) {
            speedrunner.getServer().getPlayerManager().broadcast(
                Text.literal("[MANHUNT] ")
                    .formatted(Formatting.GOLD, Formatting.BOLD)
                    .append(Text.literal(speedrunner.getName().getString() + " is now the speedrunner.")
                        .formatted(Formatting.GREEN, Formatting.BOLD))
                    .append(Text.literal(solo ? " Solo mode is active." : "")
                        .formatted(Formatting.AQUA)),
                false
            );
        }

        speedrunner.sendMessage(Text.literal("Hold a clock, press TAB to cycle Escape, Burst, Veil, then press R.")
            .formatted(Formatting.AQUA), false);
        sendHudUpdate(speedrunner);
    }

    private static void initializeHunter(ServerPlayerEntity hunter, ServerPlayerEntity target, boolean resetCooldowns) {
        UUID uuid = hunter.getUuid();
        clearParticipantState(uuid);

        participantTargets.put(uuid, target.getUuid());
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
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            boolean wasGhost = ghostSpeedrunners.contains(uuid);
            clearParticipantState(uuid);

            if (player != null) {
                if (wasGhost && player.isSpectator()) {
                    player.changeGameMode(GameMode.SURVIVAL);
                }
                ModNetworking.clearManhuntHud(player);
                if (notifyPlayers) {
                    player.sendMessage(Text.literal("Manhunt ended.")
                        .formatted(Formatting.GRAY), false);
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

        List<ServerPlayerEntity> speedrunnerPlayers = new ArrayList<>();
        List<ServerPlayerEntity> hunterPlayers = new ArrayList<>();
        for (Map.Entry<UUID, String> entry : playerRoles.entrySet()) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());
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

        for (ServerPlayerEntity speedrunner : speedrunnerPlayers) {
            startSpeedrunner(speedrunner, false, false);
            giveSpeedrunnerClock(speedrunner);
        }

        ServerPlayerEntity primaryTarget = speedrunnerPlayers.get(0);
        for (ServerPlayerEntity hunter : hunterPlayers) {
            initializeHunter(hunter, primaryTarget, true);
            giveHunterClock(hunter);
            hunter.sendMessage(Text.literal("Hold your tracker or compass, press TAB to cycle Track, Blockade, Snare, then press R.")
                .formatted(Formatting.RED), false);
            sendHudUpdate(hunter);
        }

        server.getPlayerManager().broadcast(
            Text.literal("[MANHUNT] ")
                .formatted(Formatting.GOLD, Formatting.BOLD)
                .append(Text.literal("GO! The hunt begins.")
                    .formatted(Formatting.GREEN, Formatting.BOLD)),
            false
        );
    }

    private static void applyGlowEffects(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            String role = playerRoles.get(player.getUuid());
            if (ROLE_HUNTER.equals(role)) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 40, 0, false, false));
            } else if (ROLE_SPEEDRUNNER.equals(role)) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 40, 0, false, false));
            }
        }
    }

    private static void giveHunterClock(ServerPlayerEntity hunter) {
        if (ModItems.HUNTER_TRACKER != null) {
            hunter.getInventory().insertStack(new ItemStack(ModItems.HUNTER_TRACKER));
        }
        if (ModItems.MANHUNT_COMPASS != null) {
            hunter.getInventory().insertStack(new ItemStack(ModItems.MANHUNT_COMPASS));
        }
    }

    private static void giveSpeedrunnerClock(ServerPlayerEntity speedrunner) {
        speedrunner.getInventory().insertStack(new ItemStack(Items.CLOCK));
    }

    private static void tickCooldowns(ServerPlayerEntity player, Map<UUID, Integer> cooldowns) {
        UUID uuid = player.getUuid();
        int current = cooldowns.getOrDefault(uuid, 0);
        if (current > 0) {
            cooldowns.put(uuid, current - 1);
        }
    }

    private static void checkSoloTimeLimit(ServerPlayerEntity player) {
        if (!isSoloSpeedrunner(player)) {
            return;
        }

        long elapsed = System.currentTimeMillis() - startTime.getOrDefault(player.getUuid(), System.currentTimeMillis());
        long timeLimit = 60L * 60L * 1000L;
        if (elapsed >= timeLimit) {
            player.getServer().getPlayerManager().broadcast(
                Text.literal("[MANHUNT] ")
                    .formatted(Formatting.GOLD, Formatting.BOLD)
                    .append(Text.literal(player.getName().getString() + " ran out of time.")
                        .formatted(Formatting.DARK_RED, Formatting.BOLD)),
                false
            );
            endAllGames(player.getServer());
            return;
        }

        long remainingMinutes = (timeLimit - elapsed) / 1000L / 60L;
        if (remainingMinutes <= 5) {
            int previous = soloWarningMinutes.getOrDefault(player.getUuid(), Integer.MAX_VALUE);
            if (remainingMinutes < previous) {
                soloWarningMinutes.put(player.getUuid(), (int) remainingMinutes);
                player.sendMessage(Text.literal("Solo timer: " + remainingMinutes + " minute(s) left.")
                    .formatted(Formatting.RED), true);
            }
        }
    }

    private static void useSelectedHunterAbility(ServerPlayerEntity hunter) {
        int index = selectedAbilityIndex.getOrDefault(hunter.getUuid(), 0);
        switch (index) {
            case 0 -> useHunterTrackAbility(hunter);
            case 1 -> useHunterBlockAbility(hunter);
            case 2 -> useHunterSlowAbility(hunter);
            default -> useHunterTrackAbility(hunter);
        }
    }

    private static void useSelectedSpeedrunnerAbility(ServerPlayerEntity speedrunner) {
        int index = selectedAbilityIndex.getOrDefault(speedrunner.getUuid(), 0);
        switch (index) {
            case 0 -> useSpeedrunnerEscapeAbility(speedrunner);
            case 1 -> useSpeedrunnerSpeedAbility(speedrunner);
            case 2 -> useSpeedrunnerInvisAbility(speedrunner);
            default -> useSpeedrunnerEscapeAbility(speedrunner);
        }
    }

    private static void cycleSelectedAbility(ServerPlayerEntity player, String[] names, String[] descriptions, Formatting color) {
        UUID uuid = player.getUuid();
        int next = (selectedAbilityIndex.getOrDefault(uuid, 0) + 1) % ABILITY_COUNT;
        selectedAbilityIndex.put(uuid, next);

        player.sendMessage(Text.literal("Selected " + names[next] + ": " + descriptions[next])
            .formatted(color), true);
        ModNetworking.showTitle(player, names[next], descriptions[next], color);
        sendHudUpdate(player);
    }

    private static boolean canUseSpeedrunnerAbility(ServerPlayerEntity speedrunner, Map<UUID, Integer> cooldowns, String label) {
        if (!isSpeedrunner(speedrunner)) {
            return false;
        }

        if (!activeSpeedrunners.contains(speedrunner.getUuid())) {
            speedrunner.sendMessage(Text.literal("Ghost speedrunners cannot use abilities.")
                .formatted(Formatting.RED), true);
            return false;
        }

        int cooldown = cooldowns.getOrDefault(speedrunner.getUuid(), 0);
        if (cooldown > 0) {
            speedrunner.sendMessage(Text.literal(label + " is on cooldown for " + (cooldown / 20) + "s.")
                .formatted(Formatting.RED), true);
            return false;
        }

        return true;
    }

    private static void sendHudUpdate(ServerPlayerEntity player) {
        if (!isInGame(player)) {
            ModNetworking.clearManhuntHud(player);
            return;
        }

        boolean hunter = isHunter(player);
        int index = selectedAbilityIndex.getOrDefault(player.getUuid(), 0);
        String[] names = hunter ? HUNTER_ABILITY_NAMES : SPEEDRUNNER_ABILITY_NAMES;
        String[] descriptions = hunter ? HUNTER_ABILITY_DESCRIPTIONS : SPEEDRUNNER_ABILITY_DESCRIPTIONS;
        String targetName = "";
        String elapsedTime = "N/A";

        if (hunter) {
            ServerPlayerEntity target = getTarget(player);
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
            deaths.getOrDefault(player.getUuid(), 0),
            targetName,
            index,
            names[index],
            descriptions[index],
            hunterTrackCooldowns.getOrDefault(player.getUuid(), 0),
            hunterBlockCooldowns.getOrDefault(player.getUuid(), 0),
            hunterSlowCooldowns.getOrDefault(player.getUuid(), 0),
            speedrunnerEscapeCooldowns.getOrDefault(player.getUuid(), 0),
            speedrunnerSpeedCooldowns.getOrDefault(player.getUuid(), 0),
            speedrunnerInvisCooldowns.getOrDefault(player.getUuid(), 0)
        );
    }

    private static String getElapsedOrDefault(ServerPlayerEntity player) {
        if (!isSpeedrunner(player)) {
            return "N/A";
        }
        return formatTime(System.currentTimeMillis() - startTime.getOrDefault(player.getUuid(), System.currentTimeMillis()));
    }

    private static ServerPlayerEntity findNearestHunter(ServerPlayerEntity speedrunner) {
        ServerPlayerEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (UUID hunterUuid : hunters) {
            ServerPlayerEntity hunter = speedrunner.getServer().getPlayerManager().getPlayer(hunterUuid);
            if (hunter == null || hunter.getWorld() != speedrunner.getWorld()) {
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

        server.getPlayerManager().broadcast(
            Text.literal("[MANHUNT] ")
                .formatted(Formatting.GOLD, Formatting.BOLD)
                .append(Text.literal("Hunters win. All speedrunners were eliminated.")
                    .formatted(Formatting.RED, Formatting.BOLD)),
            false
        );

        for (UUID hunterUuid : hunters) {
            ServerPlayerEntity hunter = server.getPlayerManager().getPlayer(hunterUuid);
            if (hunter != null) {
                AchievementManager.unlockAchievement(hunter, "team_hunter");
            }
        }

        endAllGames(server);
    }

    private static boolean hasHunterController(ServerPlayerEntity player) {
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

        NbtCompound nbt = stack.getNbt();
        return nbt != null && nbt.getBoolean(HunterTrackerItem.HUNTER_TRACKER_NBT);
    }

    private static boolean hasSpeedrunnerController(ServerPlayerEntity player) {
        return isClock(player.getMainHandStack()) || isClock(player.getOffHandStack());
    }

    private static boolean isClock(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.isOf(Items.CLOCK);
    }

    private static String getDimensionName(ServerWorld world) {
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

    private static String getGroupingName(ServerPlayerEntity player) {
        String guildName = GuildManager.getGuildName(player.getUuid());
        if (guildName != null && !guildName.isBlank()) {
            return guildName;
        }

        String teamName = TeamManager.getPlayerTeam(player.getUuid());
        return teamName != null ? teamName : "";
    }

    private static String joinNames(List<ServerPlayerEntity> players) {
        List<String> names = new ArrayList<>();
        for (ServerPlayerEntity player : players) {
            names.add(player.getName().getString());
        }
        return String.join(", ", names);
    }
}
