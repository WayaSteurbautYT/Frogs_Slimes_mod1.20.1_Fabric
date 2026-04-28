package com.wayacreate.frogslimegamemode.guild;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.*;

public class Guild {
    private UUID id;
    private String name;
    private UUID ownerUuid;
    private final Set<UUID> members = new HashSet<>();
    private final Map<UUID, GuildRank> memberRanks = new HashMap<>();
    private int coins = 0;
    private final List<GuildMission> missions = new ArrayList<>();
    private final List<ItemStack> storage = new ArrayList<>();
    
    public enum GuildRank {
        OWNER(4),
        OFFICER(3),
        VETERAN(2),
        MEMBER(1);
        
        private final int level;
        
        GuildRank(int level) {
            this.level = level;
        }
        
        public int getLevel() {
            return level;
        }
    }
    
    public Guild(String name, UUID ownerUuid) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.ownerUuid = ownerUuid;
        this.members.add(ownerUuid);
        this.memberRanks.put(ownerUuid, GuildRank.OWNER);
    }
    
    public UUID getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public UUID getOwnerUuid() {
        return ownerUuid;
    }
    
    public Set<UUID> getMembers() {
        return new HashSet<>(members);
    }
    
    public boolean addMember(UUID playerUuid) {
        if (members.contains(playerUuid)) {
            return false;
        }
        members.add(playerUuid);
        memberRanks.put(playerUuid, GuildRank.MEMBER);
        return true;
    }
    
    public boolean removeMember(UUID playerUuid) {
        if (playerUuid.equals(ownerUuid)) {
            return false; // Can't remove owner
        }
        members.remove(playerUuid);
        memberRanks.remove(playerUuid);
        return true;
    }
    
    public GuildRank getRank(UUID playerUuid) {
        return memberRanks.getOrDefault(playerUuid, GuildRank.MEMBER);
    }
    
    public void setRank(UUID playerUuid, GuildRank rank) {
        if (members.contains(playerUuid)) {
            memberRanks.put(playerUuid, rank);
        }
    }
    
    public boolean hasPermission(UUID playerUuid, GuildRank requiredRank) {
        GuildRank playerRank = getRank(playerUuid);
        return playerRank.getLevel() >= requiredRank.getLevel();
    }
    
    public int getCoins() {
        return coins;
    }
    
    public void addCoins(int amount) {
        this.coins += amount;
    }
    
    public boolean removeCoins(int amount) {
        if (coins < amount) {
            return false;
        }
        coins -= amount;
        return true;
    }
    
    public List<GuildMission> getMissions() {
        return new ArrayList<>(missions);
    }
    
    public void addMission(GuildMission mission) {
        missions.add(mission);
    }
    
    public boolean removeMission(UUID missionId) {
        return missions.removeIf(m -> m.getId().equals(missionId));
    }
    
    // Storage methods to use the storage field
    public List<ItemStack> getStorage() {
        return new ArrayList<>(storage);
    }
    
    public boolean addToStorage(ItemStack item) {
        if (storage.size() < 54) { // Double chest size
            storage.add(item.copy());
            return true;
        }
        return false;
    }
    
    public boolean removeFromStorage(int index) {
        if (index >= 0 && index < storage.size()) {
            storage.remove(index);
            return true;
        }
        return false;
    }
    
    public ItemStack getFromStorage(int index) {
        if (index >= 0 && index < storage.size()) {
            return storage.get(index).copy();
        }
        return ItemStack.EMPTY;
    }
    
    public int getStorageSize() {
        return storage.size();
    }
    
    public void clearStorage() {
        storage.clear();
    }
    
    public CompoundTag toNbt() {
        CompoundTag nbt = new CompoundTag();
        nbt.putUuid("id", id);
        nbt.putString("name", name);
        nbt.putUuid("owner", ownerUuid);
        
        ListTag membersList = new ListTag();
        for (UUID member : members) {
            CompoundTag memberNbt = new CompoundTag();
            memberNbt.putUuid("uuid", member);
            memberNbt.putString("rank", memberRanks.get(member).name());
            membersList.add(memberNbt);
        }
        nbt.put("members", membersList);
        
        nbt.putInt("coins", coins);
        
        ListTag missionsList = new ListTag();
        for (GuildMission mission : missions) {
            missionsList.add(mission.toNbt());
        }
        nbt.put("missions", missionsList);
        
        return nbt;
    }
    
    public static Guild fromNbt(CompoundTag nbt) {
        UUID guildId = nbt.getUuid("id");
        String name = nbt.getString("name");
        UUID owner = nbt.getUuid("owner");
        
        Guild guild = new Guild(name, owner);
        // Store the loaded ID for reference
        guild.id = guildId;
        
        ListTag membersList = nbt.getList("members", 10);
        guild.members.clear();
        guild.memberRanks.clear();
        
        for (int i = 0; i < membersList.size(); i++) {
            CompoundTag memberNbt = membersList.getCompound(i);
            UUID memberId = memberNbt.getUuid("uuid");
            GuildRank rank = GuildRank.valueOf(memberNbt.getString("rank"));
            guild.members.add(memberId);
            guild.memberRanks.put(memberId, rank);
        }
        
        guild.coins = nbt.getInt("coins");
        
        ListTag missionsList = nbt.getList("missions", 10);
        for (int i = 0; i < missionsList.size(); i++) {
            guild.missions.add(GuildMission.fromNbt(missionsList.getCompound(i)));
        }
        
        return guild;
    }
}
