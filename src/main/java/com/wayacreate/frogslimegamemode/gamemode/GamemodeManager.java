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
        // Give Task Book
        if (com.wayacreate.frogslimegamemode.item.ModItems.TASK_BOOK != null) {
            player.getInventory().insertStack(new net.minecraft.item.ItemStack(com.wayacreate.frogslimegamemode.item.ModItems.TASK_BOOK));
        }
        
        // Give Orphan Shield
        if (com.wayacreate.frogslimegamemode.item.ModItems.ORPHAN_SHIELD != null) {
            player.getInventory().insertStack(new net.minecraft.item.ItemStack(com.wayacreate.frogslimegamemode.item.ModItems.ORPHAN_SHIELD));
        }
        
        // Give some basic food
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.COOKED_BEEF, 16));
        
        // Give some basic tools
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.IRON_SWORD));
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.IRON_PICKAXE));
        
        // Give torches
        player.getInventory().insertStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.TORCH, 32));
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
        
        // Periodically save to persistent state
        GamemodeState state = GamemodeState.get(server);
        for (UUID uuid : players.keySet()) {
            GamemodeData data = players.get(uuid);
            GamemodeState.PlayerData persistentData = state.getPlayerData(uuid);
            
            // Sync abilities
            for (String abilityId : data.getPlayerAbilities()) {
                persistentData.addAbility(abilityId);
            }
            
            persistentData.setGamemodeEnabled(true);
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