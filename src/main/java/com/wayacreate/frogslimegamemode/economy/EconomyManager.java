package com.wayacreate.frogslimegamemode.economy;

import net.minecraft.server.network.ServerPlayerEntity;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EconomyManager {
    private static final Map<UUID, Integer> playerBalances = new HashMap<>();
    private static final Map<UUID, Integer> totalTrades = new HashMap<>();
    private static final int STARTING_BALANCE = 100;
    
    public static int getBalance(ServerPlayerEntity player) {
        return playerBalances.getOrDefault(player.getUuid(), STARTING_BALANCE);
    }
    
    public static void setBalance(ServerPlayerEntity player, int amount) {
        playerBalances.put(player.getUuid(), Math.max(0, amount));
    }
    
    public static void addBalance(ServerPlayerEntity player, int amount) {
        setBalance(player, getBalance(player) + amount);
    }
    
    public static boolean removeBalance(ServerPlayerEntity player, int amount) {
        int current = getBalance(player);
        if (current < amount) {
            return false;
        }
        setBalance(player, current - amount);
        return true;
    }
    
    public static boolean transfer(ServerPlayerEntity from, ServerPlayerEntity to, int amount) {
        if (!removeBalance(from, amount)) {
            return false;
        }
        addBalance(to, amount);
        incrementTrade(from.getUuid());
        incrementTrade(to.getUuid());
        return true;
    }

    public static int getTotalTrades(UUID uuid) {
        return totalTrades.getOrDefault(uuid, 0);
    }

    public static void recordTrade(ServerPlayerEntity first, ServerPlayerEntity second) {
        incrementTrade(first.getUuid());
        incrementTrade(second.getUuid());
    }

    private static void incrementTrade(UUID uuid) {
        totalTrades.put(uuid, totalTrades.getOrDefault(uuid, 0) + 1);
    }
}
