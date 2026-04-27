package com.wayacreate.frogslimegamemode.gamemode;

import com.wayacreate.frogslimegamemode.network.ModNetworking;
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
            // Enable gamemode for all online players
            for (ServerPlayerEntity onlinePlayer : server.getPlayerManager().getPlayerList()) {
                enableGamemodeForPlayer(onlinePlayer);
            }
        } else {
            // Enable only for the specific player
            enableGamemodeForPlayer(player);
        }
    }
    
    private static void enableGamemodeForPlayer(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        if (!players.containsKey(uuid)) {
            // Load from persistent state if exists
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
            
            // Give starter items only for new games
            giveStarterItems(player);
            
            // Add frog ability as starting ability
            GamemodeData data = players.get(uuid);
            data.addAbility("frog");
            
            // Mark as enabled in persistent state
            persistentData.setGamemodeEnabled(true);
            state.markDirty();
            
            // Ensure player is NOT invulnerable - critical for SMP gameplay
            player.setInvulnerable(false);
            player.getAbilities().invulnerable = false;
            player.sendAbilitiesUpdate();
            
            player.sendMessage(Text.literal("Frog & Slime Gamemode ACTIVATED!")
                .formatted(Formatting.GREEN, Formatting.BOLD), false);
            player.sendMessage(Text.literal("Your frog and slime helpers will now beat the game for you!")
                .formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("But beware... something unexpected awaits at the end...")
                .formatted(Formatting.RED, Formatting.ITALIC), false);
            
            // Show totem animation for starting ability
            ModNetworking.sendTotemAnimation(player, 
                "Starting Ability Unlocked!", 
                "Tongue Grab - Quick strikes with your frog tongue", 
                Formatting.LIGHT_PURPLE);
            
            ModNetworking.syncGamemodeStatus(player, true);
            grantAdvancement(player, "frogslimegamemode:root");
        } else {
            player.sendMessage(Text.literal("You're already in Frog & Slime Gamemode!")
                .formatted(Formatting.RED), false);
        }
    }
    
    private static void restorePlayerData(ServerPlayerEntity player, GamemodeState.PlayerData persistentData) {
        GamemodeData data = players.get(player.getUuid());
        
        // Restore abilities
        for (String abilityId : persistentData.getPlayerAbilities()) {
            data.addAbility(abilityId);
        }
        
        // Restore other stats
        // Note: We don't restore items on rejoin to prevent duplication
        
        player.sendMessage(Text.literal("Frog & Slime Gamemode restored!")
            .formatted(Formatting.GREEN, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Your abilities have been preserved.")
                .formatted(Formatting.YELLOW), false);
        
        ModNetworking.syncGamemodeStatus(player, true);
    }
    
    private static void giveStarterItems(ServerPlayerEntity player) {
        // Give Guide Book
        player.getInventory().insertStack(createGuideBook());
        
        // Give Task Book
        if (com.wayacreate.frogslimegamemode.item.ModItems.TASK_BOOK != null) {
            player.getInventory().insertStack(new net.minecraft.item.ItemStack(com.wayacreate.frogslimegamemode.item.ModItems.TASK_BOOK));
        }
        
        // Give Orphan Shield
        if (com.wayacreate.frogslimegamemode.item.ModItems.ORPHAN_SHIELD != null) {
            player.getInventory().insertStack(new net.minecraft.item.ItemStack(com.wayacreate.frogslimegamemode.item.ModItems.ORPHAN_SHIELD));
        }
        
        // SMP-Ready Starter Kit
        // Food (enough for survival)
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.COOKED_BEEF, 32));
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.GOLDEN_CARROT, 16));
        
        // Tools (iron for early game, not overpowered)
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.IRON_SWORD));
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.IRON_PICKAXE));
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.IRON_AXE));
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.IRON_SHOVEL));
        
        // Armor (iron for protection but not invincible)
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.IRON_HELMET));
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.IRON_CHESTPLATE));
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.IRON_LEGGINGS));
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.IRON_BOOTS));
        
        // Torches and basic supplies
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.TORCH, 64));
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.COAL, 32));
        
        // Building blocks (cobblestone for building)
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.COBBLESTONE, 64));
        
        // Spawn eggs for helpers (one of each)
        if (com.wayacreate.frogslimegamemode.item.ModItems.FROG_HELPER_SPAWN_EGG != null) {
            player.getInventory().insertStack(new net.minecraft.item.ItemStack(com.wayacreate.frogslimegamemode.item.ModItems.FROG_HELPER_SPAWN_EGG));
        }
        if (com.wayacreate.frogslimegamemode.item.ModItems.SLIME_HELPER_SPAWN_EGG != null) {
            player.getInventory().insertStack(new net.minecraft.item.ItemStack(com.wayacreate.frogslimegamemode.item.ModItems.SLIME_HELPER_SPAWN_EGG));
        }
        
        // Water bucket for survival
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.WATER_BUCKET));
        
        // Bow and arrows for early combat
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.BOW));
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.ARROW, 64));
    }
    
    private static net.minecraft.item.ItemStack createGuideBook() {
        net.minecraft.item.ItemStack book = new net.minecraft.item.ItemStack(net.minecraft.item.Items.WRITABLE_BOOK);
        net.minecraft.nbt.NbtCompound nbt = book.getOrCreateNbt();
        net.minecraft.nbt.NbtList pages = new net.minecraft.nbt.NbtList();
        
        pages.add(net.minecraft.nbt.NbtString.of("{\"text\":\"§l§6Frog & Slime Gamemode Guide\\n\\n§rWelcome to the Frog & Slime Gamemode! Your frog and slime helpers will assist you on your journey to beat the game.\"}"));
        pages.add(net.minecraft.nbt.NbtString.of("{\"text\":\"§l§aGetting Started\\n\\n§r• You start with a §bFrog Ability§r (Tongue Grab)\\n• Use §e[TAB]§r to switch between abilities\\n• Eat mobs to unlock new abilities\\n• Press §e[RIGHT CLICK]§r on mobs to eat them\"}"));
        pages.add(net.minecraft.nbt.NbtString.of("{\"text\":\"§l§cMob Abilities\\n\\n§rEach mob gives unique powers:\\n• §cZombie§r - Fireball attack\\n• §eSkeleton§r - Poison cloud\\n• §aSpider§r - Web shot\\n• §4Creeper§r - Explosion resistance\\n• §dEnderman§r - Teleport\"}"));
        pages.add(net.minecraft.nbt.NbtString.of("{\"text\":\"§l§9Crafting Mob Abilities\\n\\n§rTo create a usable ability item:\\n\\n1. Kill mobs to get their drops\\n2. Combine the drop with an §bAbility Drop§r in an §eAnvil\\n3. This creates a §6Mob Ability Item§r you can use\"}"));
        pages.add(net.minecraft.nbt.NbtString.of("{\"text\":\"§l§5Anvil Crafting Examples\\n\\n§r§cRotten Flesh§r + §bAbility Drop§r = §6Zombie Ability\\n§eBone§r + §bAbility Drop§r = §6Skeleton Ability\\n§aSpider Eye§r + §bAbility Drop§r = §6Spider Ability\\n§4Gunpowder§r + §bAbility Drop§r = §6Creeper Ability\"}"));
        pages.add(net.minecraft.nbt.NbtString.of("{\"text\":\"§l§bYour Helpers\\n\\n§rYou have spawn eggs for:\\n• §aFrog Helper§r - Melee combat\\n• §bSlime Helper§r - Ranged attacks\\n\\nThey will fight for you and gather resources!\"}"));
        pages.add(net.minecraft.nbt.NbtString.of("{\"text\":\"§l§eEvolution System\\n\\n§rAs you progress, your helpers can evolve into stronger forms. Kill bosses and complete challenges to unlock evolutions!\"}"));
        pages.add(net.minecraft.nbt.NbtString.of("{\"text\":\"§l§dCommands\\n\\n§r§e/frogslime enable§r - Start gamemode\\n§e/frogslime disable§r - Stop gamemode\\n§e/frogslime dimension§r - Teleport to custom dimension\\n§e/helper§r - Control your helpers\"}"));
        pages.add(net.minecraft.nbt.NbtString.of("{\"text\":\"§l§cTips\\n\\n§r• Keep your helpers alive!\\n• Collect many mob abilities\\n• Use the Task Book for objectives\\n• Work with other players in multiplayer\\n• Beware the unexpected ending...\"}"));
        pages.add(net.minecraft.nbt.NbtString.of("{\"text\":\"§l§6Good Luck!\\n\\n§rMay your frog and slime helpers lead you to victory... or something unexpected.\\n\\n§7- WayaCreate\"}"));
        
        nbt.put("pages", pages);
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
                    player.getAdvancementTracker().grantCriterion(advancement, "impossible");
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
            
            // Also save to persistent state
            GamemodeState state = GamemodeState.get(player.getServer());
            GamemodeState.PlayerData persistentData = state.getPlayerData(player.getUuid());
            persistentData.addAbility(abilityId);
            state.markDirty();
        }
    }
    
    public static void disableGamemode(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        if (players.remove(uuid) != null) {
            // Update persistent state
            GamemodeState state = GamemodeState.get(player.getServer());
            GamemodeState.PlayerData persistentData = state.getPlayerData(uuid);
            persistentData.setGamemodeEnabled(false);
            state.markDirty();
            
            player.sendMessage(Text.literal("Frog & Slime Gamemode deactivated.")
                .formatted(Formatting.GRAY), false);
            
            ModNetworking.syncGamemodeStatus(player, false);
        } else {
            player.sendMessage(Text.literal("You're not in Frog & Slime Gamemode!")
                .formatted(Formatting.RED), false);
        }
    }
    
    public static void onPlayerLeave(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        if (players.containsKey(uuid)) {
            // Save data to persistent state before removing
            GamemodeData data = players.get(uuid);
            GamemodeState state = GamemodeState.get(player.getServer());
            GamemodeState.PlayerData persistentData = state.getPlayerData(uuid);
            
            // Sync abilities
            for (String abilityId : data.getPlayerAbilities()) {
                persistentData.addAbility(abilityId);
            }
            
            persistentData.setGamemodeEnabled(true);
            state.markDirty();
            
            // Remove from memory (data persists in GamemodeState)
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
        
        // Periodically save to persistent state and ensure players are not invulnerable
        GamemodeState state = GamemodeState.get(server);
        for (UUID uuid : players.keySet()) {
            GamemodeData data = players.get(uuid);
            GamemodeState.PlayerData persistentData = state.getPlayerData(uuid);
            
            // Sync abilities
            for (String abilityId : data.getPlayerAbilities()) {
                persistentData.addAbility(abilityId);
            }
            
            persistentData.setGamemodeEnabled(true);
            
            // Ensure player is NOT invulnerable - critical for SMP gameplay
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            if (player != null) {
                if (player.isInvulnerable() || player.getAbilities().invulnerable) {
                    player.setInvulnerable(false);
                    player.getAbilities().invulnerable = false;
                    player.sendAbilitiesUpdate();
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
}