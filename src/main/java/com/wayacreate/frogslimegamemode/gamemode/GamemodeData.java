package com.wayacreate.frogslimegamemode.gamemode;

import com.wayacreate.frogslimegamemode.tasks.TaskType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GamemodeData {
    private final UUID playerUuid;
    private int helpersSpawned;
    private int mobsEaten;
    private int itemsCollected;
    private boolean triggeredEnding;
    private int ticksActive;
    private Map<TaskType, Integer> taskProgress;
    private int deathCount;
    private int jumpCount;
    private final List<String> playerAbilities;
    
    public GamemodeData(UUID playerUuid) {
        this.playerUuid = playerUuid;
        this.helpersSpawned = 0;
        this.mobsEaten = 0;
        this.itemsCollected = 0;
        this.triggeredEnding = false;
        this.ticksActive = 0;
        this.taskProgress = new HashMap<>();
        this.deathCount = 0;
        this.jumpCount = 0;
        this.playerAbilities = new ArrayList<>();
        for (TaskType task : TaskType.values()) {
            taskProgress.put(task, 0);
        }
    }
    
    public void tick() {
        ticksActive++;
    }
    
    public UUID getPlayerUuid() {
        return playerUuid;
    }
    
    public int getHelpersSpawned() {
        return helpersSpawned;
    }
    
    public void incrementHelpers() {
        helpersSpawned++;
    }
    
    public int getMobsEaten() {
        return mobsEaten;
    }
    
    public void incrementMobsEaten() {
        mobsEaten++;
    }
    
    public int getItemsCollected() {
        return itemsCollected;
    }
    
    public void addItemsCollected(int amount) {
        itemsCollected += amount;
    }
    
    public boolean hasTriggeredEnding() {
        return triggeredEnding;
    }
    
    public void setTriggeredEnding(boolean triggered) {
        this.triggeredEnding = triggered;
    }
    
    public int getTicksActive() {
        return ticksActive;
    }
    
    public int getTaskProgress(TaskType task) {
        return taskProgress.getOrDefault(task, 0);
    }
    
    public void incrementTaskProgress(TaskType task) {
        taskProgress.put(task, taskProgress.getOrDefault(task, 0) + 1);
    }
    
    public void setTaskProgress(TaskType task, int progress) {
        taskProgress.put(task, progress);
    }
    
    public boolean isTaskComplete(TaskType task) {
        return getTaskProgress(task) >= task.getRequiredAmount();
    }
    
    public int getDeathCount() {
        return deathCount;
    }
    
    public void incrementDeathCount() {
        deathCount++;
    }
    
    public int getJumpCount() {
        return jumpCount;
    }
    
    public void incrementJumpCount() {
        jumpCount++;
    }
    
    public List<String> getPlayerAbilities() {
        return new ArrayList<>(playerAbilities);
    }
    
    public void addAbility(String abilityId) {
        if (!playerAbilities.contains(abilityId)) {
            playerAbilities.add(abilityId);
        }
    }
}