package com.wayacreate.frogslimegamemode.gamemode;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GamemodeState extends PersistentState {
    private static final String KEY = "frogslime_gamemode_data";
    
    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();
    
    public GamemodeState() {
        super();
    }
    
    public static GamemodeState create() {
        return new GamemodeState();
    }
    
    public static GamemodeState get(MinecraftServer server) {
        return server.getWorld(World.OVERWORLD)
            .getPersistentStateManager()
            .getOrCreate(GamemodeState::fromNbt, GamemodeState::new, KEY);
    }
    
    public PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.computeIfAbsent(uuid, PlayerData::new);
    }
    
    public boolean hasPlayerData(UUID uuid) {
        return playerDataMap.containsKey(uuid);
    }
    
    public void removePlayerData(UUID uuid) {
        playerDataMap.remove(uuid);
        markDirty();
    }
    
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound playersNbt = new NbtCompound();
        
        for (Map.Entry<UUID, PlayerData> entry : playerDataMap.entrySet()) {
            NbtCompound playerNbt = entry.getValue().toNbt();
            playersNbt.put(entry.getKey().toString(), playerNbt);
        }
        
        nbt.put("players", playersNbt);
        return nbt;
    }
    
    public static GamemodeState fromNbt(NbtCompound nbt) {
        GamemodeState state = new GamemodeState();
        
        if (nbt.contains("players", NbtElement.COMPOUND_TYPE)) {
            NbtCompound playersNbt = nbt.getCompound("players");
            
            for (String uuidStr : playersNbt.getKeys()) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    NbtCompound playerNbt = playersNbt.getCompound(uuidStr);
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
        
        public NbtCompound toNbt() {
            NbtCompound nbt = new NbtCompound();
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
            
            NbtCompound tasksNbt = new NbtCompound();
            for (Map.Entry<String, Integer> entry : taskProgress.entrySet()) {
                tasksNbt.putInt(entry.getKey(), entry.getValue());
            }
            nbt.put("taskProgress", tasksNbt);
            
            NbtList abilitiesNbt = new NbtList();
            for (String ability : playerAbilities) {
                abilitiesNbt.add(net.minecraft.nbt.NbtString.of(ability));
            }
            nbt.put("playerAbilities", abilitiesNbt);
            
            return nbt;
        }
        
        public static PlayerData fromNbt(NbtCompound nbt) {
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
            
            if (nbt.contains("taskProgress", NbtElement.COMPOUND_TYPE)) {
                NbtCompound tasksNbt = nbt.getCompound("taskProgress");
                for (String key : tasksNbt.getKeys()) {
                    data.taskProgress.put(key, tasksNbt.getInt(key));
                }
            }
            
            if (nbt.contains("playerAbilities", NbtElement.LIST_TYPE)) {
                NbtList abilitiesNbt = nbt.getList("playerAbilities", NbtElement.STRING_TYPE);
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
