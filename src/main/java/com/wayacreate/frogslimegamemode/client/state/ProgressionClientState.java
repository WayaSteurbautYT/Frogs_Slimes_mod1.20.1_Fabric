package com.wayacreate.frogslimegamemode.client.state;

import com.wayacreate.frogslimegamemode.tasks.TaskType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class ProgressionClientState {
    private static boolean active;
    private static int level = 1;
    private static double xp;
    private static double xpToNext = 100.0;
    private static int helpersSpawned;
    private static int mobsEaten;
    private static int itemsCollected;
    private static int deaths;
    private static int jumps;
    private static int abilityCount;
    private static int highestEvolutionStage;
    private static int achievementCount;
    private static String selectedAbilityName = "Tongue Grab";
    private static String selectedAbilityDescription = "Unlock more abilities by eating mobs.";
    private static final EnumMap<TaskType, Integer> taskProgress = new EnumMap<>(TaskType.class);
    private static List<String> nextUnlockNames = List.of();
    private static List<String> nextUnlockDescriptions = List.of();

    private ProgressionClientState() {
    }

    public static void update(
        boolean activeValue,
        int levelValue,
        double xpValue,
        double xpToNextValue,
        int helpersSpawnedValue,
        int mobsEatenValue,
        int itemsCollectedValue,
        int deathsValue,
        int jumpsValue,
        int abilityCountValue,
        int highestEvolutionStageValue,
        int achievementCountValue,
        String selectedAbilityNameValue,
        String selectedAbilityDescriptionValue,
        Map<TaskType, Integer> taskProgressValue,
        List<String> nextUnlockNamesValue,
        List<String> nextUnlockDescriptionsValue
    ) {
        active = activeValue;
        level = levelValue;
        xp = xpValue;
        xpToNext = xpToNextValue;
        helpersSpawned = helpersSpawnedValue;
        mobsEaten = mobsEatenValue;
        itemsCollected = itemsCollectedValue;
        deaths = deathsValue;
        jumps = jumpsValue;
        abilityCount = abilityCountValue;
        highestEvolutionStage = highestEvolutionStageValue;
        achievementCount = achievementCountValue;
        selectedAbilityName = selectedAbilityNameValue;
        selectedAbilityDescription = selectedAbilityDescriptionValue;
        taskProgress.clear();
        taskProgress.putAll(taskProgressValue);
        nextUnlockNames = List.copyOf(nextUnlockNamesValue);
        nextUnlockDescriptions = List.copyOf(nextUnlockDescriptionsValue);
    }

    public static void clear() {
        update(false, 1, 0.0, 100.0, 0, 0, 0, 0, 0, 0, 0, 0, "No Ability", "Enable the gamemode to begin.", Map.of(), List.of(), List.of());
    }

    public static boolean isActive() {
        return active;
    }

    public static int getLevel() {
        return level;
    }

    public static double getXp() {
        return xp;
    }

    public static double getXpToNext() {
        return xpToNext;
    }

    public static int getHelpersSpawned() {
        return helpersSpawned;
    }

    public static int getMobsEaten() {
        return mobsEaten;
    }

    public static int getItemsCollected() {
        return itemsCollected;
    }

    public static int getDeaths() {
        return deaths;
    }

    public static int getJumps() {
        return jumps;
    }

    public static int getAbilityCount() {
        return abilityCount;
    }

    public static int getHighestEvolutionStage() {
        return highestEvolutionStage;
    }

    public static int getAchievementCount() {
        return achievementCount;
    }

    public static String getSelectedAbilityName() {
        return selectedAbilityName;
    }

    public static String getSelectedAbilityDescription() {
        return selectedAbilityDescription;
    }

    public static int getTaskProgress(TaskType task) {
        return taskProgress.getOrDefault(task, 0);
    }

    public static float getOverallProgress() {
        if (TaskType.values().length == 0) {
            return 0.0f;
        }

        float total = 0.0f;
        for (TaskType task : TaskType.values()) {
            total += Math.min(1.0f, getTaskProgress(task) / (float) task.getRequiredAmount());
        }
        return total / TaskType.values().length;
    }

    public static int getCompletedTaskCount() {
        int completed = 0;
        for (TaskType task : TaskType.values()) {
            if (getTaskProgress(task) >= task.getRequiredAmount()) {
                completed++;
            }
        }
        return completed;
    }

    public static List<String> getNextUnlockNames() {
        return nextUnlockNames;
    }

    public static List<String> getNextUnlockDescriptions() {
        return nextUnlockDescriptions;
    }
}
