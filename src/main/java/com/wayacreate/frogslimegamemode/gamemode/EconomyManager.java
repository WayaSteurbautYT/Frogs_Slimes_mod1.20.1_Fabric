package com.wayacreate.frogslimegamemode.gamemode;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

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
    
    public static void addCoins(ServerPlayer player, int amount) {
        UUID uuid = player.getUuid();
        int currentBalance = getBalance(uuid);
        int newBalance = currentBalance + amount;
        playerBalances.put(uuid, newBalance);
        
        player.sendMessage(Component.literal("+" + amount + " coins")
            .formatted(ChatFormatting.GOLD), false);
        
        FrogSlimeGamemode.LOGGER.info("Player " + player.getName().getString() + " received " + amount + " coins. New balance: " + newBalance);
    }
    
    public static boolean removeCoins(ServerPlayer player, int amount) {
        UUID uuid = player.getUuid();
        int currentBalance = getBalance(uuid);
        
        if (currentBalance < amount) {
            player.sendMessage(Component.literal("Not enough coins! You have " + currentBalance + " but need " + amount)
                .formatted(ChatFormatting.RED), false);
            return false;
        }
        
        int newBalance = currentBalance - amount;
        playerBalances.put(uuid, newBalance);
        
        player.sendMessage(Component.literal("-" + amount + " coins")
            .formatted(ChatFormatting.RED), false);
        
        FrogSlimeGamemode.LOGGER.info("Player " + player.getName().getString() + " spent " + amount + " coins. New balance: " + newBalance);
        return true;
    }
    
    public static void sendCoins(ServerPlayer sender, ServerPlayer receiver, int amount) {
        if (removeCoins(sender, amount)) {
            addCoins(receiver, amount);
            
            sender.sendMessage(Component.literal("Sent " + amount + " coins to " + receiver.getName().getString())
                .formatted(ChatFormatting.GREEN), false);
            receiver.sendMessage(Component.literal("Received " + amount + " coins from " + sender.getName().getString())
                .formatted(ChatFormatting.GREEN), false);
            
            // Track trades
            incrementTrade(sender.getUuid());
            incrementTrade(receiver.getUuid());
        }
    }
    
    public static void checkBalance(ServerPlayer player) {
        int balance = getBalance(player.getUuid());
        player.sendMessage(Component.literal("Balance: " + balance + " coins")
            .formatted(ChatFormatting.GOLD), false);
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
