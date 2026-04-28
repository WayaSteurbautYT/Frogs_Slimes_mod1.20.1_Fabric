package com.wayacreate.frogslimegamemode.client.state;

public final class ManhuntClientState {
    private static boolean active;
    private static String role = "";
    private static String elapsedTime = "0:00";
    private static int deathCount;
    private static String targetName = "Unknown";
    private static int selectedIndex;
    private static String selectedAbilityName = "Track";
    private static String selectedAbilityDescription = "Reveal your current target.";
    private static int hunterTrackCooldown;
    private static int hunterBlockCooldown;
    private static int hunterSlowCooldown;
    private static int speedrunnerEscapeCooldown;
    private static int speedrunnerSpeedCooldown;
    private static int speedrunnerInvisCooldown;

    private ManhuntClientState() {
    }

    public static void update(
        boolean activeValue,
        String roleValue,
        String elapsedTimeValue,
        int deathCountValue,
        String targetNameValue,
        int selectedIndexValue,
        String selectedAbilityNameValue,
        String selectedAbilityDescriptionValue,
        int hunterTrackCooldownValue,
        int hunterBlockCooldownValue,
        int hunterSlowCooldownValue,
        int speedrunnerEscapeCooldownValue,
        int speedrunnerSpeedCooldownValue,
        int speedrunnerInvisCooldownValue
    ) {
        active = activeValue;
        role = roleValue;
        elapsedTime = elapsedTimeValue;
        deathCount = deathCountValue;
        targetName = targetNameValue;
        selectedIndex = selectedIndexValue;
        selectedAbilityName = selectedAbilityNameValue;
        selectedAbilityDescription = selectedAbilityDescriptionValue;
        hunterTrackCooldown = hunterTrackCooldownValue;
        hunterBlockCooldown = hunterBlockCooldownValue;
        hunterSlowCooldown = hunterSlowCooldownValue;
        speedrunnerEscapeCooldown = speedrunnerEscapeCooldownValue;
        speedrunnerSpeedCooldown = speedrunnerSpeedCooldownValue;
        speedrunnerInvisCooldown = speedrunnerInvisCooldownValue;
    }

    public static void clear() {
        update(false, "", "0:00", 0, "Unknown", 0, "Track", "Reveal your current target.", 0, 0, 0, 0, 0, 0);
    }

    public static boolean isActive() {
        return active;
    }

    public static boolean isHunter() {
        return "hunter".equals(role);
    }

    public static boolean isSpeedrunner() {
        return "speedrunner".equals(role);
    }

    public static String getElapsedTime() {
        return elapsedTime;
    }

    public static int getDeathCount() {
        return deathCount;
    }

    public static String getTargetName() {
        return targetName;
    }

    public static int getSelectedIndex() {
        return selectedIndex;
    }

    public static String getSelectedAbilityName() {
        return selectedAbilityName;
    }

    public static String getSelectedAbilityDescription() {
        return selectedAbilityDescription;
    }

    public static int getHunterTrackCooldown() {
        return hunterTrackCooldown;
    }

    public static int getHunterBlockCooldown() {
        return hunterBlockCooldown;
    }

    public static int getHunterSlowCooldown() {
        return hunterSlowCooldown;
    }

    public static int getSpeedrunnerEscapeCooldown() {
        return speedrunnerEscapeCooldown;
    }

    public static int getSpeedrunnerSpeedCooldown() {
        return speedrunnerSpeedCooldown;
    }

    public static int getSpeedrunnerInvisCooldown() {
        return speedrunnerInvisCooldown;
    }
}
