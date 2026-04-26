package com.wayacreate.frogslimegamemode.gamemode;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;

public class ModGameRules {
    // Game rules API has access issues in 1.20.1
    // Using world data instead for gamemode detection
    
    public static void register() {
        FrogSlimeGamemode.LOGGER.info("Game rules system initialized");
    }
}
