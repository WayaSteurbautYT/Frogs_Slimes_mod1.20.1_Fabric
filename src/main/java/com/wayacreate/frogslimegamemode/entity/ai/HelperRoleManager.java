package com.wayacreate.frogslimegamemode.entity.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.ChatFormatting;

public final class HelperRoleManager {
    public static final String MINER = "Miner";
    public static final String LUMBERJACK = "Lumberjack";
    public static final String COMBAT_SPECIALIST = "Combat Specialist";
    public static final String BUILDER = "Builder";
    public static final String FARMER = "Farmer";

    private HelperRoleManager() {
    }

    public static String normalizeRole(String role) {
        if (role == null) {
            return "";
        }

        String trimmed = role.trim();
        if (trimmed.isEmpty()) {
            return "";
        }

        return switch (trimmed.toLowerCase()) {
            case "miner" -> MINER;
            case "lumberjack" -> LUMBERJACK;
            case "combat", "combat specialist" -> COMBAT_SPECIALIST;
            case "builder" -> BUILDER;
            case "farmer" -> FARMER;
            default -> trimmed;
        };
    }

    public static ChatFormatting getRoleColor(String role) {
        return switch (normalizeRole(role)) {
            case MINER -> ChatFormatting.DARK_GRAY;
            case LUMBERJACK -> ChatFormatting.DARK_GREEN;
            case COMBAT_SPECIALIST -> ChatFormatting.RED;
            case BUILDER -> ChatFormatting.GOLD;
            case FARMER -> ChatFormatting.GREEN;
            default -> ChatFormatting.WHITE;
        };
    }

    public static Goal createRoleGoal(String role, TamableAnimal entity) {
        return switch (normalizeRole(role)) {
            case MINER -> new MiningGoal(entity);
            case LUMBERJACK -> new LumberjackGoal(entity);
            case BUILDER -> new BuilderGoal(entity);
            case FARMER -> new FarmerGoal(entity);
            default -> null;
        };
    }

    public static boolean isCombatRole(String role) {
        return COMBAT_SPECIALIST.equals(normalizeRole(role));
    }
}
