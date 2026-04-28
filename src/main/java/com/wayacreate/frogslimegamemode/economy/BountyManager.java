package com.wayacreate.frogslimegamemode.economy;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BountyManager {
    private static final Map<UUID, Bounty> bounties = new ConcurrentHashMap<>(); // targetUuid -> bounty
    
    public static class Bounty {
        private final UUID targetUuid;
        private final String targetName;
        private final Map<UUID, Integer> coinContributions = new HashMap<>();
        private final List<ItemStack> itemRewards = new ArrayList<>();
        private final long createdAt;
        
        public Bounty(ServerPlayerEntity target) {
            this.targetUuid = target.getUuid();
            this.targetName = target.getName().getString();
            this.createdAt = System.currentTimeMillis();
        }
        
        public UUID getTargetUuid() {
            return targetUuid;
        }
        
        public String getTargetName() {
            return targetName;
        }
        
        public long getCreatedAt() {
            return createdAt;
        }
        
        public int getTotalReward() {
            return coinContributions.values().stream().mapToInt(Integer::intValue).sum();
        }
        
        public void addCoinReward(ServerPlayerEntity contributor, int amount) {
            coinContributions.merge(contributor.getUuid(), amount, Integer::sum);
        }
        
        public void addItemReward(ItemStack item) {
            itemRewards.add(item.copy());
        }
        
        public List<ItemStack> getItemRewards() {
            List<ItemStack> copy = new ArrayList<>();
            for (ItemStack stack : itemRewards) {
                copy.add(stack.copy());
            }
            return copy;
        }
        
        public boolean claim(ServerPlayerEntity killer) {
            int reward = getTotalReward();
            
            if (reward > 0) {
                EconomyManager.addBalance(killer, reward);
            }
            
            for (ItemStack item : itemRewards) {
                killer.getInventory().offerOrDrop(item);
            }
            
            return reward > 0 || !itemRewards.isEmpty();
        }
    }
    
    public static boolean addBounty(ServerPlayerEntity target, ServerPlayerEntity contributor, 
                                     int coins, List<ItemStack> items) {
        // Verify contributor has coins
        if (coins > 0 && !EconomyManager.removeBalance(contributor, coins)) {
            contributor.sendMessage(Text.literal("You don't have enough coins!").formatted(Formatting.RED), false);
            return false;
        }
        
        // Verify contributor has items
        for (ItemStack item : items) {
            if (!contributor.getInventory().contains(item)) {
                contributor.sendMessage(Text.literal("You don't have the required items!").formatted(Formatting.RED), false);
                // Refund coins if already taken
                if (coins > 0) {
                    EconomyManager.addBalance(contributor, coins);
                }
                return false;
            }
        }
        
        // Remove items
        for (ItemStack item : items) {
            contributor.getInventory().removeOne(item);
        }
        
        // Get or create bounty
        Bounty bounty = bounties.computeIfAbsent(target.getUuid(), k -> new Bounty(target));
        
        if (coins > 0) {
            bounty.addCoinReward(contributor, coins);
        }
        
        for (ItemStack item : items) {
            bounty.addItemReward(item);
        }
        
        contributor.sendMessage(Text.literal("Added bounty on ").formatted(Formatting.GREEN)
            .append(target.getName().getString())
            .append(Text.literal("! Total reward: ").formatted(Formatting.WHITE))
            .append(Text.literal(bounty.getTotalReward() + " coins").formatted(Formatting.GOLD)), false);
        
        // Notify target if online
        target.sendMessage(Text.literal("A bounty has been placed on your head! Reward: ").formatted(Formatting.RED)
            .append(Text.literal(bounty.getTotalReward() + " coins").formatted(Formatting.GOLD)), false);
        
        return true;
    }
    
    public static boolean claimBounty(ServerPlayerEntity killer, ServerPlayerEntity target) {
        Bounty bounty = bounties.get(target.getUuid());
        
        if (bounty == null) {
            return false;
        }
        
        // Can't claim your own bounty
        if (killer.getUuid().equals(target.getUuid())) {
            return false;
        }
        
        // Claim the bounty
        if (bounty.claim(killer)) {
            // Announce
            killer.getServer().getPlayerManager().broadcast(
                Text.literal("").formatted(Formatting.GOLD)
                    .append(killer.getName().getString())
                    .append(Text.literal(" claimed the bounty on "))
                    .append(target.getName().getString())
                    .append(Text.literal(" for "))
                    .append(Text.literal(bounty.getTotalReward() + " coins!").formatted(Formatting.GREEN)),
                false);
            
            // Remove bounty
            bounties.remove(target.getUuid());
            
            return true;
        }
        
        return false;
    }
    
    public static Bounty getBounty(UUID targetUuid) {
        return bounties.get(targetUuid);
    }
    
    public static Collection<Bounty> getAllBounties() {
        return new ArrayList<>(bounties.values());
    }
    
    public static boolean hasBounty(UUID playerUuid) {
        return bounties.containsKey(playerUuid);
    }
    
    public static void onPlayerDeath(ServerPlayerEntity player, ServerPlayerEntity killer) {
        if (killer != null && !killer.getUuid().equals(player.getUuid())) {
            claimBounty(killer, player);
        }
    }
}
