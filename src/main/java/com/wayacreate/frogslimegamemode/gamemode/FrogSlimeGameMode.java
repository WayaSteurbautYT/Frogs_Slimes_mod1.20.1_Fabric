package com.wayacreate.frogslimegamemode.gamemode;

import net.minecraft.world.level.GameType;

/**
 * Custom gamemode for Frog & Slime Gamemode
 * This extends the concept of game modes to include our custom gamemode
 */
public class FrogSlimeGameMode {
    public static final String ID = "frogslime";
    public static final String NAME = "Frog & Slime";
    
    /**
     * Check if a gamemode string matches our custom gamemode
     */
    public static boolean isFrogSlimeGamemode(String gamemode) {
        return ID.equalsIgnoreCase(gamemode);
    }
    
    /**
     * Get the underlying Minecraft gamemode for our custom gamemode
     * We use SURVIVAL as the base since our gamemode is survival-based
     */
    public static GameType getBaseGameMode() {
        return GameType.SURVIVAL;
    }
}
