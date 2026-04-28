package com.wayacreate.frogslimegamemode.gamemode;

import com.wayacreate.frogslimegamemode.achievements.AchievementManager;
import com.wayacreate.frogslimegamemode.network.ModNetworking;
import com.wayacreate.frogslimegamemode.tasks.TaskManager;
import com.wayacreate.frogslimegamemode.tasks.TaskType;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GamemodeManager {
    private static final Map<UUID, GamemodeData> players = new HashMap<>();

    public static void enableGamemode(ServerPlayerEntity player) {
        enableGamemode(player, true);
    }

    public static void enableGamemode(ServerPlayerEntity player, boolean enableAllPlayers) {
        MinecraftServer server = player.getServer();
        if (server == null) return;

        if (enableAllPlayers) {
            for (ServerPlayerEntity onlinePlayer : server.getPlayerManager().getPlayerList()) {
                enableGamemodeForPlayer(onlinePlayer);
            }
        } else {
            enableGamemodeForPlayer(player);
        }
    }

    private static void enableGamemodeForPlayer(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        if (players.containsKey(uuid)) {
            player.sendMessage(Text.literal("You're already in Frog & Slime Gamemode!")
                .formatted(Formatting.RED), false);
            return;
        }

        GamemodeState state = GamemodeState.get(player.getServer());
        GamemodeState.PlayerData persistentData = state.getPlayerData(uuid);

        if (persistentData.isGamemodeEnabled()) {
            player.sendMessage(Text.literal("Frog & Slime Gamemode already enabled!")
                .formatted(Formatting.YELLOW), false);
            players.put(uuid, new GamemodeData(uuid));
            restorePlayerData(player, persistentData);
            return;
        }

        players.put(uuid, new GamemodeData(uuid));
        giveStarterItems(player);

        GamemodeData data = players.get(uuid);
        data.addAbility("frog");

        persistentData.setGamemodeEnabled(true);
        state.markDirty();

        player.setInvulnerable(false);
        player.getAbilities().invulnerable = false;
        player.sendAbilitiesUpdate();

        player.sendMessage(Text.literal("Frog & Slime Gamemode ACTIVATED!")
            .formatted(Formatting.GREEN, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Your frog and slime helpers will now beat the game for you!")
            .formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("But beware... something unexpected awaits at the end...")
            .formatted(Formatting.RED, Formatting.ITALIC), false);

        TaskManager.completeTask(player, TaskType.ACTIVATE_GAMEMODE);
        AchievementManager.unlockAchievement(player, "journey_started");

        ModNetworking.sendTotemAnimation(
            player,
            "Starting Ability Unlocked!",
            "Tongue Grab - Quick strikes with your frog tongue",
            Formatting.LIGHT_PURPLE
        );

        ModNetworking.syncGamemodeStatus(player, true);
        ModNetworking.sendProgressSnapshot(player);
        grantAdvancement(player, "frogslimegamemode:root");
        unlockModRecipes(player);
    }

    private static void restorePlayerData(ServerPlayerEntity player, GamemodeState.PlayerData persistentData) {
        GamemodeData data = players.get(player.getUuid());

        for (String abilityId : persistentData.getPlayerAbilities()) {
            data.addAbility(abilityId);
        }

        player.sendMessage(Text.literal("Frog & Slime Gamemode restored!")
            .formatted(Formatting.GREEN, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Your abilities have been preserved.")
            .formatted(Formatting.YELLOW), false);

        ModNetworking.syncGamemodeStatus(player, true);
        ModNetworking.sendProgressSnapshot(player);
        unlockModRecipes(player);
    }

    private static void giveStarterItems(ServerPlayerEntity player) {
        player.getInventory().insertStack(createGuideBook());

        if (com.wayacreate.frogslimegamemode.item.ModItems.TASK_BOOK != null) {
            player.getInventory().insertStack(new net.minecraft.item.ItemStack(com.wayacreate.frogslimegamemode.item.ModItems.TASK_BOOK));
        }
        if (com.wayacreate.frogslimegamemode.item.ModItems.ORPHAN_SHIELD != null) {
            player.getInventory().insertStack(new net.minecraft.item.ItemStack(com.wayacreate.frogslimegamemode.item.ModItems.ORPHAN_SHIELD));
        }

        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.COOKED_BEEF, 32));
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.GOLDEN_CARROT, 16));
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.IRON_SWORD));
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.IRON_PICKAXE));
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.IRON_AXE));
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.IRON_SHOVEL));
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.IRON_HELMET));
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.IRON_CHESTPLATE));
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.IRON_LEGGINGS));
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.IRON_BOOTS));
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.TORCH, 64));
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.COAL, 32));
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.COBBLESTONE, 64));

        if (com.wayacreate.frogslimegamemode.item.ModItems.FROG_HELPER_SPAWN_EGG != null) {
            player.getInventory().insertStack(new net.minecraft.item.ItemStack(com.wayacreate.frogslimegamemode.item.ModItems.FROG_HELPER_SPAWN_EGG));
        }
        if (com.wayacreate.frogslimegamemode.item.ModItems.SLIME_HELPER_SPAWN_EGG != null) {
            player.getInventory().insertStack(new net.minecraft.item.ItemStack(com.wayacreate.frogslimegamemode.item.ModItems.SLIME_HELPER_SPAWN_EGG));
        }

        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.WATER_BUCKET));
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.BOW));
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.ARROW, 64));

        if (com.wayacreate.frogslimegamemode.item.ModItems.ABILITY_STICK != null) {
            player.getInventory().insertStack(new net.minecraft.item.ItemStack(com.wayacreate.frogslimegamemode.item.ModItems.ABILITY_STICK, 8));
        }
        if (com.wayacreate.frogslimegamemode.item.ModItems.MINER_ROLE != null) {
            player.getInventory().insertStack(new net.minecraft.item.ItemStack(com.wayacreate.frogslimegamemode.item.ModItems.MINER_ROLE));
        }
        if (com.wayacreate.frogslimegamemode.item.ModItems.COMBAT_ROLE != null) {
            player.getInventory().insertStack(new net.minecraft.item.ItemStack(com.wayacreate.frogslimegamemode.item.ModItems.COMBAT_ROLE));
        }
    }

    public static net.minecraft.item.ItemStack createGuideBook() {
        net.minecraft.item.ItemStack book = new net.minecraft.item.ItemStack(net.minecraft.item.Items.WRITTEN_BOOK);
        net.minecraft.nbt.NbtCompound nbt = book.getOrCreateNbt();
        net.minecraft.nbt.NbtList pages = new net.minecraft.nbt.NbtList();

        pages.add(net.minecraft.nbt.NbtString.of("{\"text\":\"Frog and Slime Guide\\n\\n1. Use /frogslime tasks or the Task Book to follow the route.\\n2. Tame your frog and slime helpers with an empty hand.\\n3. Use TAB to cycle abilities and R to use the selected one.\"}"));
        pages.add(net.minecraft.nbt.NbtString.of("{\"text\":\"Starter Route\\n\\n- Spawn both helpers.\\n- Give one a role stick.\\n- Let them fight until one evolves.\\n- Unlock 3 abilities by eating mobs or using crafted ability items.\"}"));
        pages.add(net.minecraft.nbt.NbtString.of("{\"text\":\"Ability Crafting\\n\\nMain path: right-click the Ability Crafting Table with a mob drop or attuned drop to forge a mob ability item.\\nLegacy path: Ability Stick in an anvil plus a matching drop also works.\"}"));
        pages.add(net.minecraft.nbt.NbtString.of("{\"text\":\"Recipes To Learn\\n\\nFrog Crafting Table: crafting tables plus slime balls.\\nAbility Crafting Table: crafting table, emeralds, slime balls.\\nAbility Stick: stick, stone, dirt, sand.\"}"));
        pages.add(net.minecraft.nbt.NbtString.of("{\"text\":\"Contracts and SMP\\n\\nUse /frogslime contract list, then /frogslime contract accept <id>.\\nUse /frogslime progress to see what is next.\\nNew SMP players get the starter route automatically once the server is running the gamemode.\"}"));
        pages.add(net.minecraft.nbt.NbtString.of("{\"text\":\"Manhunt Controls\\n\\nHunters: hold your Hunter Tracker or compass, press TAB to cycle Track, Blockade, Snare, then press R.\\nSpeedrunners: hold your clock, TAB cycles Escape, Burst, Veil, then press R.\"}"));
        pages.add(net.minecraft.nbt.NbtString.of("{\"text\":\"Winning The Run\\n\\nReach the Nether, then the End. Defeat the Ender Dragon or the Giant Slime Boss to finish the route. If you are the speedrunner in manhunt, that final kill ends the match.\"}"));

        nbt.put("pages", pages);
        nbt.putString("author", "WayaCreate");
        nbt.putString("title", "Frog & Slime Guide");
        book.setCustomName(Text.literal("Frog & Slime Guide").formatted(Formatting.GOLD, Formatting.BOLD));

        return book;
    }

    public static void grantAdvancement(ServerPlayerEntity player, String advancementId) {
        try {
            Identifier id = new Identifier(advancementId);
            Advancement advancement = player.getServer().getAdvancementLoader().get(id);
            if (advancement != null) {
                AdvancementProgress progress = player.getAdvancementTracker().getProgress(advancement);
                if (!progress.isDone()) {
                    for (String criterion : progress.getUnobtainedCriteria()) {
                        player.getAdvancementTracker().grantCriterion(advancement, criterion);
                    }
                }
            }
        } catch (Exception e) {
            // Advancement system might not be available or advancement doesn't exist
        }
    }

    public static void addAbility(ServerPlayerEntity player, String abilityId) {
        GamemodeData data = getData(player);
        if (data != null) {
            data.addAbility(abilityId);

            GamemodeState state = GamemodeState.get(player.getServer());
            GamemodeState.PlayerData persistentData = state.getPlayerData(player.getUuid());
            persistentData.addAbility(abilityId);
            state.markDirty();
        }
    }

    public static void disableGamemode(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        if (players.remove(uuid) != null) {
            GamemodeState state = GamemodeState.get(player.getServer());
            GamemodeState.PlayerData persistentData = state.getPlayerData(uuid);
            persistentData.setGamemodeEnabled(false);
            state.markDirty();

            player.sendMessage(Text.literal("Frog & Slime Gamemode deactivated.")
                .formatted(Formatting.GRAY), false);

            ModNetworking.syncGamemodeStatus(player, false);
            ModNetworking.sendProgressSnapshot(player);
        } else {
            player.sendMessage(Text.literal("You're not in Frog & Slime Gamemode!")
                .formatted(Formatting.RED), false);
        }
    }

    public static void onPlayerLeave(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        if (players.containsKey(uuid)) {
            GamemodeData data = players.get(uuid);
            GamemodeState state = GamemodeState.get(player.getServer());
            GamemodeState.PlayerData persistentData = state.getPlayerData(uuid);

            for (String abilityId : data.getPlayerAbilities()) {
                persistentData.addAbility(abilityId);
            }

            persistentData.setGamemodeEnabled(true);
            state.markDirty();
            players.remove(uuid);
        }
    }

    public static boolean isInGamemode(PlayerEntity player) {
        return players.containsKey(player.getUuid());
    }

    public static GamemodeData getData(PlayerEntity player) {
        return players.get(player.getUuid());
    }

    public static void tick(MinecraftServer server) {
        for (GamemodeData data : players.values()) {
            data.tick();
        }

        GamemodeState state = GamemodeState.get(server);
        for (UUID uuid : players.keySet()) {
            GamemodeData data = players.get(uuid);
            GamemodeState.PlayerData persistentData = state.getPlayerData(uuid);

            for (String abilityId : data.getPlayerAbilities()) {
                persistentData.addAbility(abilityId);
            }

            persistentData.setGamemodeEnabled(true);

            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            if (player != null) {
                if (player.isInvulnerable() || player.getAbilities().invulnerable) {
                    player.setInvulnerable(false);
                    player.getAbilities().invulnerable = false;
                    player.sendAbilitiesUpdate();
                }

                if (server.getTicks() % 20 == 0) {
                    TaskManager.syncDerivedTasks(player);
                    ModNetworking.sendProgressSnapshot(player);
                }
            }
        }
        state.markDirty();
    }

    public static void triggerEnding(ServerPlayerEntity player, boolean dragonKilled) {
        GamemodeData data = getData(player);
        if (data != null && !data.hasTriggeredEnding()) {
            data.setTriggeredEnding(true);

            if (dragonKilled) {
                player.getWorld().getServer().execute(() -> {
                    player.sendMessage(Text.literal("Your slime helper consumed the dragon's power!")
                        .formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), false);
                    player.sendMessage(Text.literal("Final evolution is beginning...")
                        .formatted(Formatting.YELLOW), false);

                    player.getWorld().getServer().getPlayerManager().broadcast(
                        Text.literal("[UNEXPECTED TWIST] ").formatted(Formatting.DARK_RED, Formatting.BOLD)
                            .append(Text.literal(player.getName().getString() + "'s slime helper has evolved into... something terrible!")
                                .formatted(Formatting.RED)),
                        false
                    );

                    player.sendMessage(Text.literal("TO BE CONTINUED...")
                        .formatted(Formatting.DARK_GRAY, Formatting.BOLD), false);
                    player.sendMessage(Text.literal("(Your slime absorbed too much power... what have you created?)")
                        .formatted(Formatting.GRAY, Formatting.ITALIC), false);
                });
            }
        }
    }

    private static void unlockModRecipes(ServerPlayerEntity player) {
        try {
            var modRecipes = player.getServer().getRecipeManager().values().stream()
                .filter(recipe -> recipe.getId().getNamespace().equals(com.wayacreate.frogslimegamemode.FrogSlimeGamemode.MOD_ID))
                .toList();
            player.unlockRecipes(modRecipes);
        } catch (Exception e) {
            com.wayacreate.frogslimegamemode.FrogSlimeGamemode.LOGGER.warn("Failed to unlock recipes for {}", player.getName().getString(), e);
        }
    }
}
