package com.wayacreate.frogslimegamemode.gamemode;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GamemodeState extends SavedData {
    private static final String KEY = "frogslime_gamemode_data";
    
    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();
    
    public GamemodeState() {
        super();
    }
    
    public static GamemodeState create() {
        return new GamemodeState();
    }
    
    public static GamemodeState get(MinecraftServer server) {
        return server.overworld()
            .getDataStorage()
            .computeIfAbsent(GamemodeState::fromNbt, GamemodeState::new, KEY);
    }
    
    public PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.computeIfAbsent(uuid, PlayerData::new);
    }
    
    public boolean hasPlayerData(UUID uuid) {
        return playerDataMap.containsKey(uuid);
    }

    public boolean hasAnyEnabledPlayers() {
        return playerDataMap.values().stream().anyMatch(PlayerData::isGamemodeEnabled);
    }
    
    public void removePlayerData(UUID uuid) {
        playerDataMap.remove(uuid);
        setDirty();
    }
    
    @Override
    public CompoundTag writeNbt(CompoundTag nbt) {
        CompoundTag playersNbt = new CompoundTag();
        
        for (Map.Entry<UUID, PlayerData> entry : playerDataMap.entrySet()) {
            CompoundTag playerNbt = entry.getValue().toNbt();
            playersNbt.put(entry.getKey().toString(), playerNbt);
        }
        
        nbt.put("players", playersNbt);
        return nbt;
    }
    
    public static GamemodeState fromNbt(CompoundTag nbt) {
        GamemodeState state = new GamemodeState();
        
        if (nbt.contains("players", Tag.COMPOUND_TYPE)) {
            CompoundTag playersNbt = nbt.getCompound("players");
            
            for (String uuidStr : playersNbt.getKeys()) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    CompoundTag playerNbt = playersNbt.getCompound(uuidStr);
                    PlayerData playerData = PlayerData.fromNbt(playerNbt);
                    state.playerDataMap.put(uuid, playerData);
                } catch (Exception e) {
                    // Skip invalid UUIDs
                }
            }
        }
        
        return state;
    }
    
    public static class PlayerData {
        private final UUID uuid;
        private boolean gamemodeEnabled;
        private int helpersSpawned;
        private int mobsEaten;
        private int itemsCollected;
        private boolean triggeredEnding;
        private int ticksActive;
        private final Map<String, Integer> taskProgress;
        private int deathCount;
        private int jumpCount;
        private final List<String> playerAbilities;
        private int currentAbilityIndex;
        private int abilityCooldown;
        
        public PlayerData(UUID uuid) {
            this.uuid = uuid;
            this.gamemodeEnabled = false;
            this.helpersSpawned = 0;
            this.mobsEaten = 0;
            this.itemsCollected = 0;
            this.triggeredEnding = false;
            this.ticksActive = 0;
            this.taskProgress = new HashMap<>();
            this.deathCount = 0;
            this.jumpCount = 0;
            this.playerAbilities = new ArrayList<>();
            this.currentAbilityIndex = 0;
            this.abilityCooldown = 0;
        }
        
        public CompoundTag toNbt() {
            CompoundTag nbt = new CompoundTag();
            nbt.putUuid("uuid", uuid);
            nbt.putBoolean("gamemodeEnabled", gamemodeEnabled);
            nbt.putInt("helpersSpawned", helpersSpawned);
            nbt.putInt("mobsEaten", mobsEaten);
            nbt.putInt("itemsCollected", itemsCollected);
            nbt.putBoolean("triggeredEnding", triggeredEnding);
            nbt.putInt("ticksActive", ticksActive);
            nbt.putInt("deathCount", deathCount);
            nbt.putInt("jumpCount", jumpCount);
            nbt.putInt("currentAbilityIndex", currentAbilityIndex);
            nbt.putInt("abilityCooldown", abilityCooldown);
            
            CompoundTag tasksNbt = new CompoundTag();
            for (Map.Entry<String, Integer> entry : taskProgress.entrySet()) {
                tasksNbt.putInt(entry.getKey(), entry.getValue());
            }
            nbt.put("taskProgress", tasksNbt);
            
            ListTag abilitiesNbt = new ListTag();
            for (String ability : playerAbilities) {
                abilitiesNbt.add(net.minecraft.nbt.StringTag.valueOf(ability));
            }
            nbt.put("playerAbilities", abilitiesNbt);
            
            return nbt;
        }
        
        public static PlayerData fromNbt(CompoundTag nbt) {
            UUID uuid = nbt.getUuid("uuid");
            PlayerData data = new PlayerData(uuid);
            
            data.gamemodeEnabled = nbt.getBoolean("gamemodeEnabled");
            data.helpersSpawned = nbt.getInt("helpersSpawned");
            data.mobsEaten = nbt.getInt("mobsEaten");
            data.itemsCollected = nbt.getInt("itemsCollected");
            data.triggeredEnding = nbt.getBoolean("triggeredEnding");
            data.ticksActive = nbt.getInt("ticksActive");
            data.deathCount = nbt.getInt("deathCount");
            data.jumpCount = nbt.getInt("jumpCount");
            data.currentAbilityIndex = nbt.getInt("currentAbilityIndex");
            data.abilityCooldown = nbt.getInt("abilityCooldown");
            
            if (nbt.contains("taskProgress", Tag.COMPOUND_TYPE)) {
                CompoundTag tasksNbt = nbt.getCompound("taskProgress");
                for (String key : tasksNbt.getKeys()) {
                    data.taskProgress.put(key, tasksNbt.getInt(key));
                }
            }
            
            if (nbt.contains("playerAbilities", Tag.LIST_TYPE)) {
                ListTag abilitiesNbt = nbt.getList("playerAbilities", Tag.STRING_TYPE);
                for (int i = 0; i < abilitiesNbt.size(); i++) {
                    data.playerAbilities.add(abilitiesNbt.getString(i));
                }
            }
            
            return data;
        }
        
        // Getters and setters
        public UUID getUuid() { return uuid; }
        public boolean isGamemodeEnabled() { return gamemodeEnabled; }
        public void setGamemodeEnabled(boolean enabled) { 
            this.gamemodeEnabled = enabled;
        }
        public int getHelpersSpawned() { return helpersSpawned; }
        public void incrementHelpers() { helpersSpawned++; }
        public int getMobsEaten() { return mobsEaten; }
        public void incrementMobsEaten() { mobsEaten++; }
        public int getItemsCollected() { return itemsCollected; }
        public void addItemsCollected(int amount) { itemsCollected += amount; }
        public boolean hasTriggeredEnding() { return triggeredEnding; }
        public void setTriggeredEnding(boolean triggered) { this.triggeredEnding = triggered; }
        public int getTicksActive() { return ticksActive; }
        public void tick() { ticksActive++; }
        public int getTaskProgress(String task) { return taskProgress.getOrDefault(task, 0); }
        public void setTaskProgress(String task, int progress) { taskProgress.put(task, progress); }
        public void incrementTaskProgress(String task) { taskProgress.put(task, taskProgress.getOrDefault(task, 0) + 1); }
        public int getDeathCount() { return deathCount; }
        public void incrementDeathCount() { deathCount++; }
        public int getJumpCount() { return jumpCount; }
        public void incrementJumpCount() { jumpCount++; }
        public List<String> getPlayerAbilities() { return new ArrayList<>(playerAbilities); }
        public void addAbility(String abilityId) { 
            if (!playerAbilities.contains(abilityId)) {
                playerAbilities.add(abilityId);
            }
        }
        public int getCurrentAbilityIndex() { return currentAbilityIndex; }
        public void setCurrentAbilityIndex(int index) { this.currentAbilityIndex = index; }
        public int getAbilityCooldown() { return abilityCooldown; }
        public void setAbilityCooldown(int cooldown) { this.abilityCooldown = cooldown; }
    }
}
