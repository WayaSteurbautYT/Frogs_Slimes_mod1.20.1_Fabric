package com.wayacreate.frogslimegamemode.network;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import com.wayacreate.frogslimegamemode.achievements.Achievement;
import com.wayacreate.frogslimegamemode.abilities.PlayerAbilityManager;
import com.wayacreate.frogslimegamemode.eating.MobAbility;
import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import com.wayacreate.frogslimegamemode.gamemode.PlayerLevel;
import com.wayacreate.frogslimegamemode.item.ModItems;
import com.wayacreate.frogslimegamemode.progression.ProgressionUnlock;
import com.wayacreate.frogslimegamemode.tasks.TaskManager;
import com.wayacreate.frogslimegamemode.tasks.TaskType;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class ModNetworking {
    public static final Identifier GAMEMODE_STATUS = new Identifier(FrogSlimeGamemode.MOD_ID, "gamemode_status");
    public static final Identifier OPEN_TASKS_SCREEN = new Identifier(FrogSlimeGamemode.MOD_ID, "open_tasks_screen");
    public static final Identifier SHOW_TITLE = new Identifier(FrogSlimeGamemode.MOD_ID, "show_title");
    public static final Identifier ACHIEVEMENT_TOAST = new Identifier(FrogSlimeGamemode.MOD_ID, "achievement_toast");
    public static final Identifier TOTEM_ANIMATION = new Identifier(FrogSlimeGamemode.MOD_ID, "totem_animation");
    public static final Identifier USE_ABILITY = new Identifier(FrogSlimeGamemode.MOD_ID, "use_ability");
    public static final Identifier PLAYER_TONGUE_ANIMATION = new Identifier(FrogSlimeGamemode.MOD_ID, "player_tongue_animation");
    public static final Identifier SWITCH_ABILITY = new Identifier(FrogSlimeGamemode.MOD_ID, "switch_ability");
    public static final Identifier CONSUME_ABILITY_ITEM = new Identifier(FrogSlimeGamemode.MOD_ID, "consume_ability_item");
    
    // Manhunt ability packets
    public static final Identifier HUNTER_TRACK = new Identifier(FrogSlimeGamemode.MOD_ID, "hunter_track");
    public static final Identifier HUNTER_BLOCK = new Identifier(FrogSlimeGamemode.MOD_ID, "hunter_block");
    public static final Identifier HUNTER_SLOW = new Identifier(FrogSlimeGamemode.MOD_ID, "hunter_slow");
    public static final Identifier SPEEDRUNNER_ESCAPE = new Identifier(FrogSlimeGamemode.MOD_ID, "speedrunner_escape");
    public static final Identifier SPEEDRUNNER_SPEED = new Identifier(FrogSlimeGamemode.MOD_ID, "speedrunner_speed");
    public static final Identifier SPEEDRUNNER_INVIS = new Identifier(FrogSlimeGamemode.MOD_ID, "speedrunner_invis");
    public static final Identifier MANHUNT_HUD_UPDATE = new Identifier(FrogSlimeGamemode.MOD_ID, "manhunt_hud_update");
    public static final Identifier REQUEST_PROGRESS_SNAPSHOT = new Identifier(FrogSlimeGamemode.MOD_ID, "request_progress_snapshot");
    public static final Identifier PROGRESSION_SNAPSHOT = new Identifier(FrogSlimeGamemode.MOD_ID, "progression_snapshot");

    private ModNetworking() {
    }

    public static void registerServer() {
        FrogSlimeGamemode.LOGGER.info("Registering server networking");
        
        ServerPlayNetworking.registerGlobalReceiver(USE_ABILITY, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                if (!com.wayacreate.frogslimegamemode.gamemode.ManhuntManager.useContextualAbility(player)) {
                    PlayerAbilityManager.useCurrentAbility(player);
                }
            });
        });
        
        ServerPlayNetworking.registerGlobalReceiver(SWITCH_ABILITY, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                if (!com.wayacreate.frogslimegamemode.gamemode.ManhuntManager.cycleContextualAbility(player)) {
                    PlayerAbilityManager.switchToNextAbility(player);
                }
            });
        });
        
        ServerPlayNetworking.registerGlobalReceiver(CONSUME_ABILITY_ITEM, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                // Handle consuming mob ability item from hand
                com.wayacreate.frogslimegamemode.item.MobAbilityItem.consumeHeldAbilityItem(player);
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

        ServerPlayNetworking.registerGlobalReceiver(REQUEST_PROGRESS_SNAPSHOT, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> sendProgressSnapshot(player));
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
        sendProgressSnapshot(player);
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
        buf.writeItemStack(new ItemStack(getAchievementIcon(achievement.getId())));
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
        sendManhuntHudUpdate(player, false, "", elapsedTime, deathCount, "", 0, "Track", "Reveal your target.", 0, 0, 0, 0, 0, 0);
    }
    
    public static void sendManhuntHudUpdate(
        ServerPlayerEntity player,
        boolean active,
        String role,
        String elapsedTime,
        int deathCount,
        String targetName,
        int selectedIndex,
        String selectedAbilityName,
        String selectedAbilityDescription,
        int hunterTrackCd,
        int hunterBlockCd,
        int hunterSlowCd,
        int speedrunnerEscapeCd,
        int speedrunnerSpeedCd,
        int speedrunnerInvisCd
    ) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(active);
        buf.writeString(role);
        buf.writeString(elapsedTime);
        buf.writeInt(deathCount);
        buf.writeString(targetName);
        buf.writeInt(selectedIndex);
        buf.writeString(selectedAbilityName);
        buf.writeString(selectedAbilityDescription);
        buf.writeInt(hunterTrackCd);
        buf.writeInt(hunterBlockCd);
        buf.writeInt(hunterSlowCd);
        buf.writeInt(speedrunnerEscapeCd);
        buf.writeInt(speedrunnerSpeedCd);
        buf.writeInt(speedrunnerInvisCd);
        ServerPlayNetworking.send(player, MANHUNT_HUD_UPDATE, buf);
    }

    public static void clearManhuntHud(ServerPlayerEntity player) {
        sendManhuntHudUpdate(player, false, "", "0:00", 0, "", 0, "Track", "Reveal your target.", 0, 0, 0, 0, 0, 0);
    }

    public static void sendProgressSnapshot(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        boolean active = GamemodeManager.isInGamemode(player);
        buf.writeBoolean(active);

        if (!active) {
            buf.writeInt(1);
            buf.writeDouble(0.0);
            buf.writeDouble(100.0);
            buf.writeInt(0);
            buf.writeInt(0);
            buf.writeInt(0);
            buf.writeInt(0);
            buf.writeInt(0);
            buf.writeInt(0);
            buf.writeInt(0);
            buf.writeInt(0);
            buf.writeString("No Ability");
            buf.writeString("Enable the gamemode to begin.");
            writeTaskSnapshot(buf, player);
            buf.writeInt(0);
            buf.writeInt(0);
            ServerPlayNetworking.send(player, PROGRESSION_SNAPSHOT, buf);
            return;
        }

        int level = PlayerLevel.getLevel(player);
        double xp = PlayerLevel.getXP(player);
        double xpToNext = PlayerLevel.getXPToNextLevel(player);
        int highestEvolutionStage = TaskManager.getHighestHelperEvolution(player);
        MobAbility currentAbility = PlayerAbilityManager.getCurrentAbility(player);
        var data = GamemodeManager.getData(player);

        buf.writeInt(level);
        buf.writeDouble(xp);
        buf.writeDouble(xpToNext);
        buf.writeInt(data.getHelpersSpawned());
        buf.writeInt(data.getMobsEaten());
        buf.writeInt(data.getItemsCollected());
        buf.writeInt(data.getDeathCount());
        buf.writeInt(data.getJumpCount());
        buf.writeInt(data.getPlayerAbilities().size());
        buf.writeInt(highestEvolutionStage);
        buf.writeInt(com.wayacreate.frogslimegamemode.achievements.AchievementManager.getPlayerAchievements(player.getUuid()).size());
        buf.writeString(currentAbility != null ? currentAbility.getName() : "Tongue Grab");
        buf.writeString(currentAbility != null ? currentAbility.getDescription() : "Quick strikes with your frog tongue.");

        writeTaskSnapshot(buf, player);

        List<ProgressionUnlock.Unlock> unlocks = ProgressionUnlock.getUnlocksForLevel(level);
        List<ProgressionUnlock.Unlock> upcoming = new ArrayList<>();
        for (ProgressionUnlock.Unlock unlock : unlocks) {
            if (!ProgressionUnlock.isUnlocked(unlock.getId(), level - 1, highestEvolutionStage)) {
                upcoming.add(unlock);
            }
        }

        int writeCount = Math.min(3, upcoming.size());
        buf.writeInt(writeCount);
        for (int i = 0; i < writeCount; i++) {
            ProgressionUnlock.Unlock unlock = upcoming.get(i);
            buf.writeString(unlock.getName());
            buf.writeString(unlock.getDescription());
        }

        ServerPlayNetworking.send(player, PROGRESSION_SNAPSHOT, buf);
    }

    private static void writeTaskSnapshot(PacketByteBuf buf, ServerPlayerEntity player) {
        buf.writeInt(TaskType.values().length);
        for (TaskType task : TaskType.values()) {
            int progress = GamemodeManager.isInGamemode(player) ? GamemodeManager.getData(player).getTaskProgress(task) : 0;
            buf.writeString(task.name());
            buf.writeInt(progress);
        }
    }

    private static Item getAchievementIcon(String achievementId) {
        return switch (achievementId) {
            case "journey_started" -> Items.BOOK;
            case "first_helper" -> ModItems.FROG_HELPER_SPAWN_EGG;
            case "helper_commander" -> ModItems.COMBAT_ROLE;
            case "first_evolution", "elite_helper", "master_helper" -> ModItems.EVOLUTION_STONE;
            case "mob_smith" -> ModItems.MOB_ABILITY;
            case "final_form", "boss_killer", "dragon_slayer" -> ModItems.FINAL_EVOLUTION_CRYSTAL;
            case "first_trade", "merchant", "trade_tycoon" -> Items.EMERALD;
            case "first_contract", "contract_master", "contract_legend" -> Items.PAPER;
            case "ability_unlock", "ability_master" -> ModItems.ABILITY_DROP;
            default -> ModItems.TASK_BOOK != null ? ModItems.TASK_BOOK : Items.BOOK;
        };
    }
}
