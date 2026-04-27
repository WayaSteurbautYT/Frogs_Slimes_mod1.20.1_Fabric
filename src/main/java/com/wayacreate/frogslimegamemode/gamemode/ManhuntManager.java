package com.wayacreate.frogslimegamemode.gamemode;

import com.wayacreate.frogslimegamemode.network.ModNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ManhuntManager {
    private static final Map<UUID, UUID> speedrunners = new HashMap<>(); // Maps hunter UUID to target UUID
    private static final Map<UUID, Boolean> gameActive = new HashMap<>();
    private static final Map<UUID, Integer> hunterCooldowns = new HashMap<>();
    private static final Map<UUID, Integer> speedrunnerCooldowns = new HashMap<>();
    private static final Map<UUID, Integer> hunterTrackCooldowns = new HashMap<>();
    private static final Map<UUID, Integer> hunterBlockCooldowns = new HashMap<>();
    private static final Map<UUID, Integer> hunterSlowCooldowns = new HashMap<>();
    private static final Map<UUID, Integer> speedrunnerEscapeCooldowns = new HashMap<>();
    private static final Map<UUID, Integer> speedrunnerSpeedCooldowns = new HashMap<>();
    private static final Map<UUID, Integer> speedrunnerInvisCooldowns = new HashMap<>();
    private static final Set<UUID> soloSpeedrunners = new HashSet<>();
    private static final Map<UUID, Long> startTime = new HashMap<>();
    private static final Map<UUID, Integer> deaths = new HashMap<>();
    private static final Map<UUID, String> playerRoles = new HashMap<>(); // "hunter" or "speedrunner"
    private static final Set<UUID> hunters = new HashSet<>(); // Track all hunters for grouping
    private static final Set<UUID> activeSpeedrunners = new HashSet<>(); // Track alive speedrunners
    private static final Set<UUID> ghostSpeedrunners = new HashSet<>(); // Track dead speedrunners (ghosts)
    private static boolean autoManhuntMode = false;
    private static int countdownTicks = 0;
    private static final int COUNTDOWN_SECONDS = 30;
    
    // Ability cooldowns (in ticks)
    private static final int HUNTER_TRACK_COOLDOWN = 100;
    private static final int HUNTER_BLOCK_COOLDOWN = 200;
    private static final int HUNTER_SLOW_COOLDOWN = 150;
    private static final int SPEEDRUNNER_ESCAPE_COOLDOWN = 180;
    private static final int SPEEDRUNNER_SPEED_COOLDOWN = 120;
    private static final int SPEEDRUNNER_INVIS_COOLDOWN = 300;
    
    public static void setSpeedrunner(ServerPlayerEntity speedrunner) {
        setSpeedrunner(speedrunner, false);
    }
    
    /**
     * Start auto-assignment manhunt mode with countdown
     * Randomly assigns one speedrunner and the rest as hunters
     */
    public static void startAutoManhunt(ServerPlayerEntity initiator) {
        MinecraftServer server = initiator.getServer();
        if (server == null) return;
        
        java.util.List<ServerPlayerEntity> players = new java.util.ArrayList<>(server.getPlayerManager().getPlayerList());
        if (players.size() < 2) {
            initiator.sendMessage(Text.literal("Need at least 2 players for auto manhunt!")
                .formatted(Formatting.RED), false);
            return;
        }
        
        autoManhuntMode = true;
        countdownTicks = COUNTDOWN_SECONDS * 20; // Convert to ticks
        
        // Clear existing games
        for (ServerPlayerEntity player : players) {
            endGame(player);
            playerRoles.remove(player.getUuid());
            hunters.clear();
        }
        
        // Randomly select one speedrunner
        java.util.Collections.shuffle(players);
        ServerPlayerEntity speedrunner = players.get(0);
        
        // Assign the rest as hunters
        java.util.List<ServerPlayerEntity> hunterList = players.subList(1, players.size());
        
        // Set roles
        playerRoles.put(speedrunner.getUuid(), "speedrunner");
        for (ServerPlayerEntity hunter : hunterList) {
            playerRoles.put(hunter.getUuid(), "hunter");
            hunters.add(hunter.getUuid());
        }
        
        server.getPlayerManager().broadcast(
            Text.literal("[MANHUNT] ")
                .formatted(Formatting.GOLD, Formatting.BOLD)
                .append(Text.literal("Auto Manhunt starting in " + COUNTDOWN_SECONDS + " seconds!")
                    .formatted(Formatting.YELLOW)),
            false
        );
        
        server.getPlayerManager().broadcast(
            Text.literal("[MANHUNT] ")
                .formatted(Formatting.GOLD)
                .append(Text.literal(speedrunner.getName().getString() + " is the SPEEDRUNNER!")
                    .formatted(Formatting.GREEN, Formatting.BOLD)),
            false
        );
        
        for (ServerPlayerEntity hunter : hunterList) {
            server.getPlayerManager().broadcast(
                Text.literal("[MANHUNT] ")
                    .formatted(Formatting.GOLD)
                    .append(Text.literal(hunter.getName().getString() + " is a HUNTER!")
                        .formatted(Formatting.RED, Formatting.BOLD)),
                false
            );
        }
    }
    
    public static void setSoloSpeedrunner(ServerPlayerEntity speedrunner) {
        setSpeedrunner(speedrunner, true);
    }
    
    private static void setSpeedrunner(ServerPlayerEntity speedrunner, boolean solo) {
        speedrunners.put(speedrunner.getUuid(), speedrunner.getUuid());
        gameActive.put(speedrunner.getUuid(), true);
        startTime.put(speedrunner.getUuid(), System.currentTimeMillis());
        deaths.put(speedrunner.getUuid(), 0);
        speedrunnerEscapeCooldowns.put(speedrunner.getUuid(), 0);
        speedrunnerSpeedCooldowns.put(speedrunner.getUuid(), 0);
        speedrunnerInvisCooldowns.put(speedrunner.getUuid(), 0);
        activeSpeedrunners.add(speedrunner.getUuid());
        
        if (solo) {
            soloSpeedrunners.add(speedrunner.getUuid());
        }
        
        speedrunner.getServer().getPlayerManager().broadcast(
            Text.literal("[MANHUNT] ")
                .formatted(Formatting.GOLD, Formatting.BOLD)
                .append(Text.literal(speedrunner.getName().getString() + " is now the speedrunner!")
                    .formatted(Formatting.YELLOW))
                .append(Text.literal(solo ? " (Solo Mode)" : "")
                    .formatted(Formatting.AQUA)),
            false
        );
        
        speedrunner.sendMessage(Text.literal("You are the speedrunner! Use your frog and slime helpers to beat the game.")
            .formatted(Formatting.GREEN, Formatting.BOLD), false);
        
        if (solo) {
            speedrunner.sendMessage(Text.literal("Solo Mode: No hunters, but you have a time limit!")
                .formatted(Formatting.AQUA), false);
        }
        
        // Send initial HUD data
        ModNetworking.sendManhuntHudUpdate(speedrunner, "0:00", 0);
    }
    
    public static void setHunter(ServerPlayerEntity hunter, ServerPlayerEntity target) {
        // Check if target is already a hunter - prevent hunters from hunting each other
        if (hunters.contains(target.getUuid())) {
            hunter.sendMessage(Text.literal("Cannot target another hunter!")
                .formatted(Formatting.RED), false);
            return;
        }
        
        speedrunners.put(hunter.getUuid(), target.getUuid());
        gameActive.put(hunter.getUuid(), true);
        hunterCooldowns.put(hunter.getUuid(), 0);
        hunterTrackCooldowns.put(hunter.getUuid(), 0);
        hunterBlockCooldowns.put(hunter.getUuid(), 0);
        hunterSlowCooldowns.put(hunter.getUuid(), 0);
        deaths.put(hunter.getUuid(), 0);
        
        // Add to hunters set for grouping
        hunters.add(hunter.getUuid());
        
        hunter.sendMessage(Text.literal("You are a hunter! Track down ")
            .formatted(Formatting.RED)
            .append(Text.literal(target.getName().getString())
                .formatted(Formatting.YELLOW))
            .append(Text.literal(" and stop them from beating the game!")
                .formatted(Formatting.RED)), false);
        
        hunter.sendMessage(Text.literal("Abilities: ")
            .formatted(Formatting.GRAY)
            .append(Text.literal("[R] Track [F] Block [G] Slow")
                .formatted(Formatting.LIGHT_PURPLE)), false);
        
        // Send initial HUD data with target name
        String targetName = target.getName().getString();
        ModNetworking.sendManhuntHudUpdate(hunter, "N/A", 0, targetName, 0, 0, 0, 0, 0, 0);
    }
    
    public static void endGame(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        speedrunners.remove(uuid);
        gameActive.remove(uuid);
        hunterCooldowns.remove(uuid);
        speedrunnerCooldowns.remove(uuid);
        hunterTrackCooldowns.remove(uuid);
        hunterBlockCooldowns.remove(uuid);
        hunterSlowCooldowns.remove(uuid);
        speedrunnerEscapeCooldowns.remove(uuid);
        speedrunnerSpeedCooldowns.remove(uuid);
        speedrunnerInvisCooldowns.remove(uuid);
        soloSpeedrunners.remove(uuid);
        startTime.remove(uuid);
        deaths.remove(uuid);
        activeSpeedrunners.remove(uuid);
        ghostSpeedrunners.remove(uuid);
        
        player.sendMessage(Text.literal("Manhunt game ended.")
            .formatted(Formatting.GRAY), false);
    }
    
    public static void onSpeedrunnerWin(ServerPlayerEntity speedrunner) {
        long elapsedTime = System.currentTimeMillis() - startTime.getOrDefault(speedrunner.getUuid(), 0L);
        String timeStr = formatTime(elapsedTime);
        
        speedrunner.getServer().getPlayerManager().broadcast(
            Text.literal("[MANHUNT] ")
                .formatted(Formatting.GOLD, Formatting.BOLD)
                .append(Text.literal("Speedrunners win! " + speedrunner.getName().getString() + " beat the game in " + timeStr + "!")
                    .formatted(Formatting.GREEN, Formatting.BOLD)),
            false
        );
        
        // End game for all participants
        for (ServerPlayerEntity player : speedrunner.getServer().getPlayerManager().getPlayerList()) {
            if (isInGame(player)) {
                endGame(player);
            }
        }
    }
    
    public static void onSpeedrunnerDeath(ServerPlayerEntity speedrunner) {
        UUID uuid = speedrunner.getUuid();
        int deathCount = deaths.getOrDefault(uuid, 0) + 1;
        deaths.put(uuid, deathCount);
        
        // Send HUD update to speedrunner
        String elapsedTime = getElapsedTime(speedrunner);
        ModNetworking.sendManhuntHudUpdate(speedrunner, elapsedTime, deathCount);
        
        if (isSoloSpeedrunner(speedrunner)) {
            speedrunner.getServer().getPlayerManager().broadcast(
                Text.literal("[MANHUNT] ")
                    .formatted(Formatting.GOLD, Formatting.BOLD)
                    .append(Text.literal(speedrunner.getName().getString() + " died! Death " + deathCount)
                        .formatted(Formatting.RED)),
                false
            );
            
            if (deathCount >= 3) {
                speedrunner.getServer().getPlayerManager().broadcast(
                    Text.literal("[MANHUNT] ")
                        .formatted(Formatting.GOLD, Formatting.BOLD)
                        .append(Text.literal(speedrunner.getName().getString() + " failed the solo speedrun!")
                            .formatted(Formatting.DARK_RED, Formatting.BOLD)),
                    false
                );
                endGame(speedrunner);
            } else {
                speedrunner.sendMessage(Text.literal("You have " + (3 - deathCount) + " lives remaining!")
                    .formatted(Formatting.YELLOW), false);
            }
        } else {
            // In multiplayer, speedrunner becomes a ghost
            activeSpeedrunners.remove(uuid);
            ghostSpeedrunners.add(uuid);
            
            // Set to spectator mode
            speedrunner.changeGameMode(net.minecraft.world.GameMode.SPECTATOR);
            
            speedrunner.getServer().getPlayerManager().broadcast(
                Text.literal("[MANHUNT] ")
                    .formatted(Formatting.GOLD, Formatting.BOLD)
                    .append(Text.literal(speedrunner.getName().getString() + " died and is now a ghost!")
                        .formatted(Formatting.GRAY, Formatting.ITALIC)),
                false
            );
            
            speedrunner.sendMessage(Text.literal("You are now a ghost. Watch the remaining speedrunners complete the game!")
                .formatted(Formatting.GRAY), false);
            
            // Check if all speedrunners are dead (hunters win)
            checkHuntersWinCondition(speedrunner);
        }
    }
    
    private static void checkHuntersWinCondition(ServerPlayerEntity deadSpeedrunner) {
        // Count total speedrunners (including ghosts)
        int totalSpeedrunners = 0;
        for (UUID uuid : speedrunners.keySet()) {
            if (speedrunners.get(uuid).equals(uuid)) {
                totalSpeedrunners++;
            }
        }
        
        // If all speedrunners are ghosts, hunters win
        if (activeSpeedrunners.isEmpty() && totalSpeedrunners > 0) {
            deadSpeedrunner.getServer().getPlayerManager().broadcast(
                Text.literal("[MANHUNT] ")
                    .formatted(Formatting.GOLD, Formatting.BOLD)
                    .append(Text.literal("Hunters win! All speedrunners have been eliminated.")
                        .formatted(Formatting.RED, Formatting.BOLD)),
                false
            );
            
            // End game for all participants
            for (ServerPlayerEntity player : deadSpeedrunner.getServer().getPlayerManager().getPlayerList()) {
                if (isInGame(player)) {
                    endGame(player);
                }
            }
        }
    }
    
    private static String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes % 60, seconds % 60);
        } else if (minutes > 0) {
            return String.format("%d:%02d", minutes, seconds % 60);
        } else {
            return String.format("%d seconds", seconds);
        }
    }
    
    public static boolean isSpeedrunner(PlayerEntity player) {
        UUID uuid = player.getUuid();
        return speedrunners.containsKey(uuid) && speedrunners.get(uuid).equals(uuid);
    }
    
    public static boolean isSoloSpeedrunner(PlayerEntity player) {
        return isSpeedrunner(player) && soloSpeedrunners.contains(player.getUuid());
    }
    
    public static boolean isHunter(PlayerEntity player) {
        UUID uuid = player.getUuid();
        return speedrunners.containsKey(uuid) && !speedrunners.get(uuid).equals(uuid);
    }
    
    public static boolean isInGame(PlayerEntity player) {
        return gameActive.containsKey(player.getUuid()) && gameActive.get(player.getUuid());
    }
    
    public static ServerPlayerEntity getTarget(ServerPlayerEntity hunter) {
        UUID targetUuid = speedrunners.get(hunter.getUuid());
        if (targetUuid != null && hunter.getServer() != null) {
            return hunter.getServer().getPlayerManager().getPlayer(targetUuid);
        }
        return null;
    }
    
    public static void updateCompass(ServerPlayerEntity player) {
        if (!isHunter(player)) return;
        
        ServerPlayerEntity target = getTarget(player);
        if (target != null && target.getWorld() == player.getWorld()) {
            // Calculate direction to target
            BlockPos playerPos = player.getBlockPos();
            BlockPos targetPos = target.getBlockPos();
            
            double distance = Math.sqrt(
                Math.pow(targetPos.getX() - playerPos.getX(), 2) +
                Math.pow(targetPos.getZ() - playerPos.getZ(), 2)
            );
            
            if (distance < 100) {
                player.sendMessage(Text.literal("Target nearby! Distance: " + (int)distance + " blocks")
                    .formatted(Formatting.RED), true);
            }
        } else if (target != null) {
            // Target in different dimension
            String dimension = getDimensionName((ServerWorld) target.getWorld());
            player.sendMessage(Text.literal("Target in " + dimension)
                .formatted(Formatting.DARK_PURPLE), true);
        }
    }
    
    private static String getDimensionName(ServerWorld world) {
        if (world.getDimensionKey().equals(DimensionTypes.OVERWORLD)) {
            return "Overworld";
        } else if (world.getDimensionKey().equals(DimensionTypes.THE_NETHER)) {
            return "Nether";
        } else if (world.getDimensionKey().equals(DimensionTypes.THE_END)) {
            return "End";
        }
        return "Unknown Dimension";
    }
    
    public static void tick(net.minecraft.server.MinecraftServer server) {
        // Handle countdown for auto manhunt
        if (autoManhuntMode && countdownTicks > 0) {
            countdownTicks--;
            int secondsLeft = countdownTicks / 20;
            
            if (countdownTicks % 20 == 0 && secondsLeft > 0) {
                server.getPlayerManager().broadcast(
                    Text.literal("[MANHUNT] ")
                        .formatted(Formatting.GOLD, Formatting.BOLD)
                        .append(Text.literal("Starting in " + secondsLeft + " seconds!")
                            .formatted(Formatting.YELLOW)),
                    false
                );
            }
            
            if (countdownTicks == 0) {
                startAutoManhuntGame(server);
            }
        }
        
        // Apply glow effects to players in auto manhunt mode
        if (autoManhuntMode) {
            applyGlowEffects(server);
        }
        
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (!isInGame(player)) continue;
            
            if (isHunter(player)) {
                updateCompass(player);
                decreaseIndividualCooldowns(player, hunterTrackCooldowns);
                decreaseIndividualCooldowns(player, hunterBlockCooldowns);
                decreaseIndividualCooldowns(player, hunterSlowCooldowns);
                
                // Send HUD update every second (20 ticks)
                if (server.getTicks() % 20 == 0) {
                    ServerPlayerEntity target = getTarget(player);
                    String targetName = target != null ? target.getName().getString() : "Unknown";
                    int trackCd = hunterTrackCooldowns.getOrDefault(player.getUuid(), 0);
                    int blockCd = hunterBlockCooldowns.getOrDefault(player.getUuid(), 0);
                    int slowCd = hunterSlowCooldowns.getOrDefault(player.getUuid(), 0);
                    ModNetworking.sendManhuntHudUpdate(player, "N/A", 0, targetName, trackCd, blockCd, slowCd, 0, 0, 0);
                }
            } else if (isSpeedrunner(player)) {
                decreaseIndividualCooldowns(player, speedrunnerEscapeCooldowns);
                decreaseIndividualCooldowns(player, speedrunnerSpeedCooldowns);
                decreaseIndividualCooldowns(player, speedrunnerInvisCooldowns);
                checkSoloTimeLimit(player);
                
                // Send HUD update every second (20 ticks)
                if (server.getTicks() % 20 == 0) {
                    String elapsedTime = getElapsedTime(player);
                    int deathCount = deaths.getOrDefault(player.getUuid(), 0);
                    int escapeCd = speedrunnerEscapeCooldowns.getOrDefault(player.getUuid(), 0);
                    int speedCd = speedrunnerSpeedCooldowns.getOrDefault(player.getUuid(), 0);
                    int invisCd = speedrunnerInvisCooldowns.getOrDefault(player.getUuid(), 0);
                    ModNetworking.sendManhuntHudUpdate(player, elapsedTime, deathCount, "", 0, 0, 0, escapeCd, speedCd, invisCd);
                }
            }
        }
    }
    
    private static void startAutoManhuntGame(MinecraftServer server) {
        autoManhuntMode = false;
        
        // Get the speedrunner from playerRoles
        UUID speedrunnerUuid = null;
        for (Map.Entry<UUID, String> entry : playerRoles.entrySet()) {
            if (entry.getValue().equals("speedrunner")) {
                speedrunnerUuid = entry.getKey();
                break;
            }
        }
        
        if (speedrunnerUuid == null) return;
        
        ServerPlayerEntity speedrunner = server.getPlayerManager().getPlayer(speedrunnerUuid);
        if (speedrunner == null) return;
        
        // Start the speedrunner game
        setSpeedrunner(speedrunner, false);
        
        // Assign all hunters to target the speedrunner
        for (UUID hunterUuid : hunters) {
            ServerPlayerEntity hunter = server.getPlayerManager().getPlayer(hunterUuid);
            if (hunter != null) {
                setHunter(hunter, speedrunner);
                // Give hunter clock
                giveHunterClock(hunter);
            }
        }
        
        // Give speedrunner clock
        giveSpeedrunnerClock(speedrunner);
        
        server.getPlayerManager().broadcast(
            Text.literal("[MANHUNT] ")
                .formatted(Formatting.GOLD, Formatting.BOLD)
                .append(Text.literal("GO! The hunt begins!")
                    .formatted(Formatting.GREEN, Formatting.BOLD)),
            false
        );
    }
    
    private static void applyGlowEffects(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            String role = playerRoles.get(player.getUuid());
            if (role == null) continue;
            
            // Apply glow effect based on role
            if (role.equals("hunter")) {
                player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.GLOWING, 40, 0, false, false));
            } else if (role.equals("speedrunner")) {
                player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.GLOWING, 40, 0, false, false));
            }
        }
    }
    
    private static void giveHunterClock(ServerPlayerEntity hunter) {
        // Give hunter a clock to track time
        if (com.wayacreate.frogslimegamemode.item.ModItems.HUNTER_TRACKER != null) {
            hunter.getInventory().insertStack(new net.minecraft.item.ItemStack(com.wayacreate.frogslimegamemode.item.ModItems.HUNTER_TRACKER));
        }
    }
    
    private static void giveSpeedrunnerClock(ServerPlayerEntity speedrunner) {
        // Give speedrunner a clock to track time
        speedrunner.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.CLOCK));
    }
    
    private static void decreaseIndividualCooldowns(ServerPlayerEntity player, Map<UUID, Integer> cooldowns) {
        UUID uuid = player.getUuid();
        if (cooldowns.containsKey(uuid) && cooldowns.get(uuid) > 0) {
            cooldowns.put(uuid, cooldowns.get(uuid) - 1);
        }
    }
    
    private static void checkSoloTimeLimit(ServerPlayerEntity player) {
        if (!isSoloSpeedrunner(player)) return;
        
        long elapsed = System.currentTimeMillis() - startTime.getOrDefault(player.getUuid(), 0L);
        long timeLimit = 60 * 60 * 1000; // 1 hour time limit
        
        if (elapsed >= timeLimit) {
            player.getServer().getPlayerManager().broadcast(
                Text.literal("[MANHUNT] ")
                    .formatted(Formatting.GOLD, Formatting.BOLD)
                    .append(Text.literal(player.getName().getString() + " ran out of time! Solo speedrun failed.")
                        .formatted(Formatting.DARK_RED, Formatting.BOLD)),
                false
            );
            endGame(player);
        } else if (elapsed >= timeLimit - 5 * 60 * 1000) {
            // Warn at 5 minutes remaining
            long remaining = (timeLimit - elapsed) / 1000 / 60;
            player.sendMessage(Text.literal("Time remaining: " + remaining + " minutes!")
                .formatted(Formatting.RED), true);
        }
    }
    
    // Hunter abilities
    public static void useHunterTrackAbility(ServerPlayerEntity hunter) {
        if (!isHunter(hunter)) return;
        
        int cooldown = hunterTrackCooldowns.getOrDefault(hunter.getUuid(), 0);
        if (cooldown > 0) {
            hunter.sendMessage(Text.literal("Track ability on cooldown: " + (cooldown / 20) + "s")
                .formatted(Formatting.RED), true);
            return;
        }
        
        ServerPlayerEntity target = getTarget(hunter);
        if (target == null) return;
        
        hunterTrackCooldowns.put(hunter.getUuid(), HUNTER_TRACK_COOLDOWN);
        
        String dimension = getDimensionName((ServerWorld) target.getWorld());
        BlockPos targetPos = target.getBlockPos();
        BlockPos hunterPos = hunter.getBlockPos();
        
        double distance = Math.sqrt(
            Math.pow(targetPos.getX() - hunterPos.getX(), 2) +
            Math.pow(targetPos.getZ() - hunterPos.getZ(), 2)
        );
        
        hunter.sendMessage(Text.literal("Target tracked!")
            .formatted(Formatting.GREEN), false);
        hunter.sendMessage(Text.literal("Dimension: " + dimension)
            .formatted(Formatting.AQUA), false);
        hunter.sendMessage(Text.literal("Distance: " + String.format("%.1f", distance) + " blocks")
            .formatted(Formatting.YELLOW), false);
        hunter.sendMessage(Text.literal("Coordinates: " + targetPos.getX() + ", " + targetPos.getY() + ", " + targetPos.getZ())
            .formatted(Formatting.GRAY), false);
    }
    
    public static void useHunterBlockAbility(ServerPlayerEntity hunter) {
        if (!isHunter(hunter)) return;
        
        int cooldown = hunterBlockCooldowns.getOrDefault(hunter.getUuid(), 0);
        if (cooldown > 0) {
            hunter.sendMessage(Text.literal("Block ability on cooldown: " + (cooldown / 20) + "s")
                .formatted(Formatting.RED), true);
            return;
        }
        
        ServerPlayerEntity target = getTarget(hunter);
        if (target == null || target.getWorld() != hunter.getWorld()) {
            hunter.sendMessage(Text.literal("Target not in same dimension!")
                .formatted(Formatting.RED), true);
            return;
        }
        
        double distance = hunter.distanceTo(target);
        if (distance > 30) {
            hunter.sendMessage(Text.literal("Target too far! Must be within 30 blocks.")
                .formatted(Formatting.RED), true);
            return;
        }
        
        hunterBlockCooldowns.put(hunter.getUuid(), HUNTER_BLOCK_COOLDOWN);
        
        // Create a temporary shield wall between hunter and target
        net.minecraft.util.math.Vec3d direction = hunter.getPos().subtract(target.getPos()).normalize();
        BlockPos wallPos = target.getBlockPos().add((int)(direction.x * 3), 0, (int)(direction.z * 3));
        ServerWorld world = (ServerWorld) hunter.getWorld();
        
        for (int x = -2; x <= 2; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos placePos = wallPos.add(x, y, z);
                    if (world.getBlockState(placePos).isAir()) {
                        // Use shield blocks (polished blackstone or similar) instead of obsidian
                        world.setBlockState(placePos, net.minecraft.block.Blocks.POLISHED_BLACKSTONE_BRICKS.getDefaultState());
                        // Schedule block removal after 5 seconds
                        world.scheduleBlockTick(placePos, net.minecraft.block.Blocks.POLISHED_BLACKSTONE_BRICKS, 100);
                    }
                }
            }
        }
        
        hunter.sendMessage(Text.literal("Shield wall created!")
            .formatted(Formatting.GREEN), false);
    }
    
    public static void useHunterSlowAbility(ServerPlayerEntity hunter) {
        if (!isHunter(hunter)) return;
        
        int cooldown = hunterSlowCooldowns.getOrDefault(hunter.getUuid(), 0);
        if (cooldown > 0) {
            hunter.sendMessage(Text.literal("Slow ability on cooldown: " + (cooldown / 20) + "s")
                .formatted(Formatting.RED), true);
            return;
        }
        
        ServerPlayerEntity target = getTarget(hunter);
        if (target == null || target.getWorld() != hunter.getWorld()) {
            hunter.sendMessage(Text.literal("Target not in same dimension!")
                .formatted(Formatting.RED), true);
            return;
        }
        
        double distance = hunter.distanceTo(target);
        if (distance > 20) {
            hunter.sendMessage(Text.literal("Target too far! Must be within 20 blocks.")
                .formatted(Formatting.RED), true);
            return;
        }
        
        hunterSlowCooldowns.put(hunter.getUuid(), HUNTER_SLOW_COOLDOWN);
        
        // Apply slowness to target
        target.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
            net.minecraft.entity.effect.StatusEffects.SLOWNESS, 200, 2));
        target.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
            net.minecraft.entity.effect.StatusEffects.MINING_FATIGUE, 200, 1));
        
        hunter.sendMessage(Text.literal("Target slowed!")
            .formatted(Formatting.GREEN), false);
        target.sendMessage(Text.literal("You have been slowed by a hunter!")
            .formatted(Formatting.RED), false);
    }
    
    // Speedrunner abilities
    public static void useSpeedrunnerEscapeAbility(ServerPlayerEntity speedrunner) {
        if (!isSpeedrunner(speedrunner)) return;
        
        int cooldown = speedrunnerEscapeCooldowns.getOrDefault(speedrunner.getUuid(), 0);
        if (cooldown > 0) {
            speedrunner.sendMessage(Text.literal("Escape ability on cooldown: " + (cooldown / 20) + "s")
                .formatted(Formatting.RED), true);
            return;
        }
        
        speedrunnerEscapeCooldowns.put(speedrunner.getUuid(), SPEEDRUNNER_ESCAPE_COOLDOWN);
        
        // Teleport speedrunner randomly within 50 blocks
        ServerWorld world = (ServerWorld) speedrunner.getWorld();
        BlockPos currentPos = speedrunner.getBlockPos();
        
        int offsetX = world.random.nextInt(100) - 50;
        int offsetZ = world.random.nextInt(100) - 50;
        BlockPos newPos = currentPos.add(offsetX, 0, offsetZ);
        
        // Find safe Y position
        int newY = world.getTopY(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING, newPos.getX(), newPos.getZ());
        newPos = new BlockPos(newPos.getX(), newY, newPos.getZ());
        
        speedrunner.teleport(newPos.getX(), newY, newPos.getZ());
        
        speedrunner.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
            net.minecraft.entity.effect.StatusEffects.SPEED, 200, 1));
        
        speedrunner.sendMessage(Text.literal("Escape activated!")
            .formatted(Formatting.GREEN), false);
    }
    
    public static void useSpeedrunnerSpeedAbility(ServerPlayerEntity speedrunner) {
        if (!isSpeedrunner(speedrunner)) return;
        
        int cooldown = speedrunnerSpeedCooldowns.getOrDefault(speedrunner.getUuid(), 0);
        if (cooldown > 0) {
            speedrunner.sendMessage(Text.literal("Speed ability on cooldown: " + (cooldown / 20) + "s")
                .formatted(Formatting.RED), true);
            return;
        }
        
        speedrunnerSpeedCooldowns.put(speedrunner.getUuid(), SPEEDRUNNER_SPEED_COOLDOWN);
        
        speedrunner.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
            net.minecraft.entity.effect.StatusEffects.SPEED, 300, 3));
        speedrunner.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
            net.minecraft.entity.effect.StatusEffects.JUMP_BOOST, 300, 2));
        
        speedrunner.sendMessage(Text.literal("Speed boost activated!")
            .formatted(Formatting.GREEN), false);
    }
    
    public static void useSpeedrunnerInvisAbility(ServerPlayerEntity speedrunner) {
        if (!isSpeedrunner(speedrunner)) return;
        
        int cooldown = speedrunnerInvisCooldowns.getOrDefault(speedrunner.getUuid(), 0);
        if (cooldown > 0) {
            speedrunner.sendMessage(Text.literal("Invisibility ability on cooldown: " + (cooldown / 20) + "s")
                .formatted(Formatting.RED), true);
            return;
        }
        
        speedrunnerInvisCooldowns.put(speedrunner.getUuid(), SPEEDRUNNER_INVIS_COOLDOWN);
        
        speedrunner.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
            net.minecraft.entity.effect.StatusEffects.INVISIBILITY, 400, 0));
        speedrunner.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
            net.minecraft.entity.effect.StatusEffects.SPEED, 400, 1));
        
        speedrunner.sendMessage(Text.literal("Invisibility activated!")
            .formatted(Formatting.GREEN), false);
    }
    
    public static String getElapsedTime(ServerPlayerEntity player) {
        if (!isSpeedrunner(player)) return "N/A";
        long elapsed = System.currentTimeMillis() - startTime.getOrDefault(player.getUuid(), 0L);
        return formatTime(elapsed);
    }
    
    public static int getDeathCount(ServerPlayerEntity player) {
        return deaths.getOrDefault(player.getUuid(), 0);
    }
}
