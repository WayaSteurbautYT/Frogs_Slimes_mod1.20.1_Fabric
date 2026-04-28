package com.wayacreate.frogslimegamemode.guild;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.*;

public class GuildMission {
    private final UUID id;
    private String name;
    private String description;
    private final List<ItemStack> requiredItems;
    private int coinReward;
    private final List<ItemStack> itemRewards;
    private int experienceReward;
    private final Set<UUID> completedBy;
    private final long createdAt;
    private long expiresAt;
    private boolean active;
    
    public GuildMission(String name, String description, List<ItemStack> requiredItems,
                       int coinReward, List<ItemStack> itemRewards, int experienceReward,
                       long durationHours) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.requiredItems = new ArrayList<>(requiredItems);
        this.coinReward = coinReward;
        this.itemRewards = new ArrayList<>(itemRewards);
        this.experienceReward = experienceReward;
        this.completedBy = new HashSet<>();
        this.createdAt = System.currentTimeMillis();
        this.expiresAt = createdAt + (durationHours * 3600000);
        this.active = true;
    }
    
    public UUID getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public List<ItemStack> getRequiredItems() {
        List<ItemStack> copy = new ArrayList<>();
        for (ItemStack stack : requiredItems) {
            copy.add(stack.copy());
        }
        return copy;
    }
    
    public int getCoinReward() {
        return coinReward;
    }
    
    public List<ItemStack> getItemRewards() {
        List<ItemStack> copy = new ArrayList<>();
        for (ItemStack stack : itemRewards) {
            copy.add(stack.copy());
        }
        return copy;
    }
    
    public int getExperienceReward() {
        return experienceReward;
    }
    
    public boolean isCompletedBy(UUID playerUuid) {
        return completedBy.contains(playerUuid);
    }
    
    public void markCompleted(UUID playerUuid) {
        completedBy.add(playerUuid);
    }
    
    public Set<UUID> getCompletedBy() {
        return new HashSet<>(completedBy);
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
    
    public boolean isActive() {
        return active && !isExpired();
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public long getTimeRemaining() {
        long remaining = expiresAt - System.currentTimeMillis();
        return Math.max(0, remaining);
    }
    
    public boolean hasPlayerContributed(UUID playerUuid, List<ItemStack> contributions) {
        // Check if player has contributed all required items
        for (ItemStack required : requiredItems) {
            int requiredCount = required.getCount();
            int contributedCount = 0;
            
            for (ItemStack contribution : contributions) {
                if (contribution.isOf(required.getItem())) {
                    contributedCount += contribution.getCount();
                }
            }
            
            if (contributedCount < requiredCount) {
                return false;
            }
        }
        return true;
    }
    
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putUuid("id", id);
        nbt.putString("name", name);
        nbt.putString("description", description);
        
        NbtList requiredList = new NbtList();
        for (ItemStack stack : requiredItems) {
            requiredList.add(stack.writeNbt(new NbtCompound()));
        }
        nbt.put("requiredItems", requiredList);
        
        nbt.putInt("coinReward", coinReward);
        
        NbtList rewardList = new NbtList();
        for (ItemStack stack : itemRewards) {
            rewardList.add(stack.writeNbt(new NbtCompound()));
        }
        nbt.put("itemRewards", rewardList);
        
        nbt.putInt("experienceReward", experienceReward);
        
        NbtList completedList = new NbtList();
        for (UUID uuid : completedBy) {
            NbtCompound uuidNbt = new NbtCompound();
            uuidNbt.putUuid("uuid", uuid);
            completedList.add(uuidNbt);
        }
        nbt.put("completedBy", completedList);
        
        nbt.putLong("createdAt", createdAt);
        nbt.putLong("expiresAt", expiresAt);
        nbt.putBoolean("active", active);
        
        return nbt;
    }
    
    public static GuildMission fromNbt(NbtCompound nbt) {
        String name = nbt.getString("name");
        String description = nbt.getString("description");
        
        List<ItemStack> requiredItems = new ArrayList<>();
        NbtList requiredList = nbt.getList("requiredItems", 10);
        for (int i = 0; i < requiredList.size(); i++) {
            requiredItems.add(ItemStack.fromNbt(requiredList.getCompound(i)));
        }
        
        int coinReward = nbt.getInt("coinReward");
        
        List<ItemStack> itemRewards = new ArrayList<>();
        NbtList rewardList = nbt.getList("itemRewards", 10);
        for (int i = 0; i < rewardList.size(); i++) {
            itemRewards.add(ItemStack.fromNbt(rewardList.getCompound(i)));
        }
        
        int experienceReward = nbt.getInt("experienceReward");
        long durationHours = (nbt.getLong("expiresAt") - nbt.getLong("createdAt")) / 3600000;
        
        GuildMission mission = new GuildMission(name, description, requiredItems, 
            coinReward, itemRewards, experienceReward, durationHours);
        
        // Restore completion status
        NbtList completedList = nbt.getList("completedBy", 10);
        for (int i = 0; i < completedList.size(); i++) {
            mission.completedBy.add(completedList.getCompound(i).getUuid("uuid"));
        }
        
        mission.active = nbt.getBoolean("active");
        
        return mission;
    }
}
