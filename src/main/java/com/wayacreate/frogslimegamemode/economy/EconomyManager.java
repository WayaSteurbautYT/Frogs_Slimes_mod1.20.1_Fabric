package com.wayacreate.frogslimegamemode.economy;

import net.minecraft.server.level.ServerPlayer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EconomyManager {
    private static final Map<UUID, Integer> playerBalances = new HashMap<>();
    private static final Map<UUID, Integer> totalTrades = new HashMap<>();
    private static final int STARTING_BALANCE = 100;
    
    public static int getBalance(ServerPlayer player) {
        return playerBalances.getOrDefault(player.getUuid(), STARTING_BALANCE);
    }
    
    public static void setBalance(ServerPlayer player, int amount) {
        playerBalances.put(player.getUuid(), Math.max(0, amount));
    }
    
    public static void addBalance(ServerPlayer player, int amount) {
        setBalance(player, getBalance(player) + amount);
    }
    
    public static boolean removeBalance(ServerPlayer player, int amount) {
        int current = getBalance(player);
        if (current < amount) {
            return false;
        }
        setBalance(player, current - amount);
        return true;
    }
    
    public static boolean transfer(ServerPlayer from, ServerPlayer to, int amount) {
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

    public static void recordTrade(ServerPlayer first, ServerPlayer second) {
        incrementTrade(first.getUuid());
        incrementTrade(second.getUuid());
    }

    private static void incrementTrade(UUID uuid) {
        totalTrades.put(uuid, totalTrades.getOrDefault(uuid, 0) + 1);
    }
}
