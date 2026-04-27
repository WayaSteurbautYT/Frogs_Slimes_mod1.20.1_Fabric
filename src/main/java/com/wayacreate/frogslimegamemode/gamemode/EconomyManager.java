package com.wayacreate.frogslimegamemode.gamemode;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EconomyManager {
    private static final Map<UUID, Integer> playerBalances = new HashMap<>();
    private static final Map<UUID, Integer> totalTrades = new HashMap<>();
    
    public static int getBalance(UUID uuid) {
        return playerBalances.getOrDefault(uuid, 0);
    }
    
    public static void setBalance(UUID uuid, int amount) {
        playerBalances.put(uuid, amount);
    }
    
    public static void addCoins(ServerPlayerEntity player, int amount) {
        UUID uuid = player.getUuid();
        int currentBalance = getBalance(uuid);
        int newBalance = currentBalance + amount;
        playerBalances.put(uuid, newBalance);
        
        player.sendMessage(Text.literal("+" + amount + " coins")
            .formatted(Formatting.GOLD), false);
        
        FrogSlimeGamemode.LOGGER.info("Player " + player.getName().getString() + " received " + amount + " coins. New balance: " + newBalance);
    }
    
    public static boolean removeCoins(ServerPlayerEntity player, int amount) {
        UUID uuid = player.getUuid();
        int currentBalance = getBalance(uuid);
        
        if (currentBalance < amount) {
            player.sendMessage(Text.literal("Not enough coins! You have " + currentBalance + " but need " + amount)
                .formatted(Formatting.RED), false);
            return false;
        }
        
        int newBalance = currentBalance - amount;
        playerBalances.put(uuid, newBalance);
        
        player.sendMessage(Text.literal("-" + amount + " coins")
            .formatted(Formatting.RED), false);
        
        FrogSlimeGamemode.LOGGER.info("Player " + player.getName().getString() + " spent " + amount + " coins. New balance: " + newBalance);
        return true;
    }
    
    public static void sendCoins(ServerPlayerEntity sender, ServerPlayerEntity receiver, int amount) {
        if (removeCoins(sender, amount)) {
            addCoins(receiver, amount);
            
            sender.sendMessage(Text.literal("Sent " + amount + " coins to " + receiver.getName().getString())
                .formatted(Formatting.GREEN), false);
            receiver.sendMessage(Text.literal("Received " + amount + " coins from " + sender.getName().getString())
                .formatted(Formatting.GREEN), false);
            
            // Track trades
            incrementTrade(sender.getUuid());
            incrementTrade(receiver.getUuid());
        }
    }
    
    public static void checkBalance(ServerPlayerEntity player) {
        int balance = getBalance(player.getUuid());
        player.sendMessage(Text.literal("Balance: " + balance + " coins")
            .formatted(Formatting.GOLD), false);
    }
    
    private static void incrementTrade(UUID uuid) {
        totalTrades.put(uuid, totalTrades.getOrDefault(uuid, 0) + 1);
    }
    
    public static int getTotalTrades(UUID uuid) {
        return totalTrades.getOrDefault(uuid, 0);
    }
    
    public static void clearPlayerData(UUID uuid) {
        playerBalances.remove(uuid);
        totalTrades.remove(uuid);
    }
}
