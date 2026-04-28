package com.wayacreate.frogslimegamemode.tasks;

import net.minecraft.util.Formatting;

public enum TaskType {
    ACTIVATE_GAMEMODE(
        "First Steps",
        "Enable the gamemode and claim your starter route.",
        "Starter kit and guide access",
        "Journey",
        1,
        Formatting.GREEN
    ),
    TAME_HELPER(
        "First Helper",
        "Tame a frog or slime helper.",
        "Helper commands and role gameplay",
        "Helpers",
        1,
        Formatting.AQUA
    ),
    ASSIGN_ROLE(
        "Field Orders",
        "Assign any role to one of your helpers.",
        "Dedicated helper jobs",
        "Helpers",
        1,
        Formatting.GOLD
    ),
    UNLOCK_ABILITIES(
        "Ability Apprentice",
        "Unlock 3 player abilities.",
        "Ability cycling and combat depth",
        "Abilities",
        3,
        Formatting.LIGHT_PURPLE
    ),
    CRAFT_ABILITY(
        "Mob Smith",
        "Create your first mob ability item.",
        "Permanent ability crafting route",
        "Abilities",
        1,
        Formatting.RED
    ),
    EVOLVE_HELPER(
        "Growing Stronger",
        "Reach the first evolution on any helper.",
        "Stronger helpers and more unlocks",
        "Helpers",
        1,
        Formatting.YELLOW
    ),
    REACH_NETHER(
        "Nether Bound",
        "Reach the Nether.",
        "Nether contracts and blaze abilities",
        "World",
        1,
        Formatting.DARK_RED
    ),
    COMPLETE_CONTRACT(
        "Signed and Sealed",
        "Complete your first contract.",
        "Coins and contract progression",
        "Economy",
        1,
        Formatting.GOLD
    ),
    REACH_END(
        "End Walker",
        "Reach the End.",
        "Final route unlocked",
        "World",
        1,
        Formatting.DARK_PURPLE
    ),
    DEFEAT_FINAL_BOSS(
        "Beat the Run",
        "Defeat the Ender Dragon or Giant Slime Boss.",
        "Gamemode complete",
        "Finale",
        1,
        Formatting.DARK_RED
    );

    private final String displayName;
    private final String description;
    private final String rewardText;
    private final String category;
    private final int requiredAmount;
    private final Formatting color;

    TaskType(String displayName, String description, String rewardText, String category, int requiredAmount, Formatting color) {
        this.displayName = displayName;
        this.description = description;
        this.rewardText = rewardText;
        this.category = category;
        this.requiredAmount = requiredAmount;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getRewardText() {
        return rewardText;
    }

    public String getCategory() {
        return category;
    }

    public int getRequiredAmount() {
        return requiredAmount;
    }

    public Formatting getColor() {
        return color;
    }
}
