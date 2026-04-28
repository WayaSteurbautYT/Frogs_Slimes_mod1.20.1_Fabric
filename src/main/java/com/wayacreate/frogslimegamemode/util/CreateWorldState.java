package com.wayacreate.frogslimegamemode.util;

/**
 * Utility class to track Frog & Slime gamemode state during world creation.
 * Level name containing "frogslime" will auto-enable the gamemode.
 */
public class CreateWorldState {
    
    private static boolean globalFrogSlimeMode = false;
    
    public static boolean isFrogSlimeMode() {
        return globalFrogSlimeMode;
    }
    
    public static void resetFrogSlimeMode() {
        globalFrogSlimeMode = false;
    }
    
    public static void setFrogSlimeMode(boolean enabled) {
        globalFrogSlimeMode = enabled;
    }
}
