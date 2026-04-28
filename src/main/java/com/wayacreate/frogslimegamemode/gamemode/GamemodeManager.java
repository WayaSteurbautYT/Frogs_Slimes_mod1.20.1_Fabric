package com.wayacreate.frogslimegamemode.gamemode;

import com.wayacreate.frogslimegamemode.achievements.AchievementManager;
import com.wayacreate.frogslimegamemode.network.ModNetworking;
import com.wayacreate.frogslimegamemode.tasks.TaskManager;
import com.wayacreate.frogslimegamemode.tasks.TaskType;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GamemodeManager {
    private static final Map<UUID, GamemodeData> players = new HashMap<>();

    public static void enableGamemode(ServerPlayer player) {
        enableGamemode(player, true);
    }

    public static void enableGamemode(ServerPlayer player, boolean enableAllPlayers) {
        MinecraftServer server = player.getServer();
        if (server == null) return;

        if (enableAllPlayers) {
            for (ServerPlayer onlinePlayer : server.getPlayerManager().getPlayerList()) {
                enableGamemodeForPlayer(onlinePlayer);
            }
        } else {
            enableGamemodeForPlayer(player);
        }
    }

    private static void enableGamemodeForPlayer(ServerPlayer player) {
        UUID uuid = player.getUuid();
        if (players.containsKey(uuid)) {
            player.sendMessage(Component.literal("You're already in Frog & Slime Gamemode!")
                .formatted(ChatFormatting.RED), false);
            return;
        }

        GamemodeState state = GamemodeState.get(player.getServer());
        GamemodeState.PlayerData persistentData = state.getPlayerData(uuid);

        if (persistentData.isGamemodeEnabled()) {
            player.sendMessage(Component.literal("Frog & Slime Gamemode already enabled!")
                .formatted(ChatFormatting.YELLOW), false);
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

        player.sendMessage(Component.literal("Frog & Slime Gamemode ACTIVATED!")
            .formatted(ChatFormatting.GREEN, ChatFormatting.BOLD), false);
        player.sendMessage(Component.literal("Your frog and slime helpers will now beat the game for you!")
            .formatted(ChatFormatting.YELLOW), false);
        player.sendMessage(Component.literal("But beware... something unexpected awaits at the end...")
            .formatted(ChatFormatting.RED, ChatFormatting.ITALIC), false);

        TaskManager.completeTask(player, TaskType.ACTIVATE_GAMEMODE);
        AchievementManager.unlockAchievement(player, "journey_started");

        ModNetworking.sendTotemAnimation(
            player,
            "Starting Ability Unlocked!",
            "Tongue Grab - Quick strikes with your frog tongue",
            ChatFormatting.LIGHT_PURPLE
        );

        ModNetworking.syncGamemodeStatus(player, true);
        ModNetworking.sendProgressSnapshot(player);
        grantAdvancement(player, "frogslimegamemode:root");
        unlockModRecipes(player);
    }

    private static void restorePlayerData(ServerPlayer player, GamemodeState.PlayerData persistentData) {
        GamemodeData data = players.get(player.getUuid());

        for (String abilityId : persistentData.getPlayerAbilities()) {
            data.addAbility(abilityId);
        }

        player.sendMessage(Component.literal("Frog & Slime Gamemode restored!")
            .formatted(ChatFormatting.GREEN, ChatFormatting.BOLD), false);
        player.sendMessage(Component.literal("Your abilities have been preserved.")
            .formatted(ChatFormatting.YELLOW), false);

        ModNetworking.syncGamemodeStatus(player, true);
        ModNetworking.sendProgressSnapshot(player);
        unlockModRecipes(player);
    }

    private static void giveStarterItems(ServerPlayer player) {
        player.getInventory().insertStack(createGuideBook());

        if (com.wayacreate.frogslimegamemode.item.ModItems.TASK_BOOK != null) {
            player.getInventory().insertStack(new net.minecraft.world.item.ItemStack(com.wayacreate.frogslimegamemode.item.ModItems.TASK_BOOK));
        }
        if (com.wayacreate.frogslimegamemode.item.ModItems.ORPHAN_SHIELD != null) {
            player.getInventory().insertStack(new net.minecraft.world.item.ItemStack(com.wayacreate.frogslimegamemode.item.ModItems.ORPHAN_SHIELD));
        }

        player.getInventory().insertStack(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.COOKED_BEEF, 32));
        player.getInventory().insertStack(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.GOLDEN_CARROT, 16));
        player.getInventory().insertStack(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.IRON_SWORD));
        player.getInventory().insertStack(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.IRON_PICKAXE));
        player.getInventory().insertStack(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.IRON_AXE));
        player.getInventory().insertStack(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.IRON_SHOVEL));
        player.getInventory().insertStack(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.IRON_HELMET));
        player.getInventory().insertStack(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.IRON_CHESTPLATE));
        player.getInventory().insertStack(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.IRON_LEGGINGS));
        player.getInventory().insertStack(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.IRON_BOOTS));
        player.getInventory().insertStack(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.TORCH, 64));
        player.getInventory().insertStack(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.COAL, 32));
        player.getInventory().insertStack(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.COBBLESTONE, 64));

        if (com.wayacreate.frogslimegamemode.item.ModItems.FROG_HELPER_SPAWN_EGG != null) {
            player.getInventory().insertStack(new net.minecraft.world.item.ItemStack(com.wayacreate.frogslimegamemode.item.ModItems.FROG_HELPER_SPAWN_EGG));
        }
        if (com.wayacreate.frogslimegamemode.item.ModItems.SLIME_HELPER_SPAWN_EGG != null) {
            player.getInventory().insertStack(new net.minecraft.world.item.ItemStack(com.wayacreate.frogslimegamemode.item.ModItems.SLIME_HELPER_SPAWN_EGG));
        }

        player.getInventory().insertStack(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.WATER_BUCKET));
        player.getInventory().insertStack(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.BOW));
        player.getInventory().insertStack(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.ARROW, 64));

        if (com.wayacreate.frogslimegamemode.item.ModItems.ABILITY_STICK != null) {
            player.getInventory().insertStack(new net.minecraft.world.item.ItemStack(com.wayacreate.frogslimegamemode.item.ModItems.ABILITY_STICK, 8));
        }
        if (com.wayacreate.frogslimegamemode.item.ModItems.MINER_ROLE != null) {
            player.getInventory().insertStack(new net.minecraft.world.item.ItemStack(com.wayacreate.frogslimegamemode.item.ModItems.MINER_ROLE));
        }
        if (com.wayacreate.frogslimegamemode.item.ModItems.COMBAT_ROLE != null) {
            player.getInventory().insertStack(new net.minecraft.world.item.ItemStack(com.wayacreate.frogslimegamemode.item.ModItems.COMBAT_ROLE));
        }
    }

    public static net.minecraft.world.item.ItemStack createGuideBook() {
        net.minecraft.world.item.ItemStack book = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.WRITTEN_BOOK);
        net.minecraft.nbt.CompoundTag nbt = book.getOrCreateNbt();
        net.minecraft.nbt.ListTag pages = new net.minecraft.nbt.ListTag();

        pages.add(net.minecraft.nbt.StringTag.of("{\"text\":\"Frog and Slime Guide\\n\\n1. Use /frogslime tasks or the Task Book to follow the route.\\n2. Tame your frog and slime helpers with an empty hand.\\n3. Use TAB to cycle abilities and R to use the selected one.\"}"));
        pages.add(net.minecraft.nbt.StringTag.of("{\"text\":\"Starter Route\\n\\n- Spawn both helpers.\\n- Give one a role stick.\\n- Let them fight until one evolves.\\n- Unlock 3 abilities by eating mobs or using crafted ability items.\"}"));
        pages.add(net.minecraft.nbt.StringTag.of("{\"text\":\"Ability Crafting\\n\\nMain path: right-click the Ability Crafting Table with a mob drop or attuned drop to forge a mob ability item.\\nLegacy path: Ability Stick in an anvil plus a matching drop also works.\"}"));
        pages.add(net.minecraft.nbt.StringTag.of("{\"text\":\"Recipes To Learn\\n\\nFrog Crafting Table: crafting tables plus slime balls.\\nAbility Crafting Table: crafting table, emeralds, slime balls.\\nAbility Stick: stick, stone, dirt, sand.\"}"));
        pages.add(net.minecraft.nbt.StringTag.of("{\"text\":\"Contracts and SMP\\n\\nUse /frogslime contract list, then /frogslime contract accept <id>.\\nUse /frogslime progress to see what is next.\\nNew SMP players get the starter route automatically once the server is running the gamemode.\"}"));
        pages.add(net.minecraft.nbt.StringTag.of("{\"text\":\"Manhunt Controls\\n\\nHunters: hold your Hunter Tracker or compass, press TAB to cycle Track, Blockade, Snare, then press R.\\nSpeedrunners: hold your clock, TAB cycles Escape, Burst, Veil, then press R.\"}"));
        pages.add(net.minecraft.nbt.StringTag.of("{\"text\":\"Winning The Run\\n\\nReach the Nether, then the End. Defeat the Ender Dragon or the Giant Slime Boss to finish the route. If you are the speedrunner in manhunt, that final kill ends the match.\"}"));

        nbt.put("pages", pages);
        nbt.putString("author", "WayaCreate");
        nbt.putString("title", "Frog & Slime Guide");
        book.setCustomName(Component.literal("Frog & Slime Guide").formatted(ChatFormatting.GOLD, ChatFormatting.BOLD));

        return book;
    }

    public static void grantAdvancement(ServerPlayer player, String advancementId) {
        try {
            ResourceLocation id = ResourceLocation.parse(advancementId);
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

    public static void addAbility(ServerPlayer player, String abilityId) {
        GamemodeData data = getData(player);
        if (data != null) {
            data.addAbility(abilityId);

            GamemodeState state = GamemodeState.get(player.getServer());
            GamemodeState.PlayerData persistentData = state.getPlayerData(player.getUuid());
            persistentData.addAbility(abilityId);
            state.markDirty();
        }
    }

    public static void disableGamemode(ServerPlayer player) {
        UUID uuid = player.getUuid();
        if (players.remove(uuid) != null) {
            GamemodeState state = GamemodeState.get(player.getServer());
            GamemodeState.PlayerData persistentData = state.getPlayerData(uuid);
            persistentData.setGamemodeEnabled(false);
            state.markDirty();

            player.sendMessage(Component.literal("Frog & Slime Gamemode deactivated.")
                .formatted(ChatFormatting.GRAY), false);

            ModNetworking.syncGamemodeStatus(player, false);
            ModNetworking.sendProgressSnapshot(player);
        } else {
            player.sendMessage(Component.literal("You're not in Frog & Slime Gamemode!")
                .formatted(ChatFormatting.RED), false);
        }
    }

    public static void onPlayerLeave(ServerPlayer player) {
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

    public static boolean isInGamemode(Player player) {
        return players.containsKey(player.getUuid());
    }

    public static GamemodeData getData(Player player) {
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

            ServerPlayer player = server.getPlayerManager().getPlayer(uuid);
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

    public static void triggerEnding(ServerPlayer player, boolean dragonKilled) {
        GamemodeData data = getData(player);
        if (data != null && !data.hasTriggeredEnding()) {
            data.setTriggeredEnding(true);

            if (dragonKilled) {
                player.getWorld().getServer().execute(() -> {
                    player.sendMessage(Component.literal("Your slime helper consumed the dragon's power!")
                        .formatted(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD), false);
                    player.sendMessage(Component.literal("Final evolution is beginning...")
                        .formatted(ChatFormatting.YELLOW), false);

                    player.getWorld().getServer().getPlayerManager().broadcast(
                        Component.literal("[UNEXPECTED TWIST] ").formatted(ChatFormatting.DARK_RED, ChatFormatting.BOLD)
                            .append(Component.literal(player.getName().getString() + "'s slime helper has evolved into... something terrible!")
                                .formatted(ChatFormatting.RED)),
                        false
                    );

                    player.sendMessage(Component.literal("TO BE CONTINUED...")
                        .formatted(ChatFormatting.DARK_GRAY, ChatFormatting.BOLD), false);
                    player.sendMessage(Component.literal("(Your slime absorbed too much power... what have you created?)")
                        .formatted(ChatFormatting.GRAY, ChatFormatting.ITALIC), false);
                });
            }
        }
    }

    private static void unlockModRecipes(ServerPlayer player) {
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
