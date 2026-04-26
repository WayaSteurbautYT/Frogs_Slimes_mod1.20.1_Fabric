package com.wayacreate.frogslimegamemode;

import com.wayacreate.frogslimegamemode.client.hud.GamemodeHud;
import com.wayacreate.frogslimegamemode.client.keybind.ModKeybinds;
import com.wayacreate.frogslimegamemode.network.ModNetworkingClient;
import net.fabricmc.api.ClientModInitializer;

public class FrogSlimeGamemodeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        GamemodeHud.onInitializeClient();
        
        ModNetworkingClient.registerClient();
        ModKeybinds.register();
        
        // Entity renderers will be registered when the renderer classes are implemented
        // For now, we'll use vanilla renderers as fallback
        
        FrogSlimeGamemode.LOGGER.info("Frog & Slime Gamemode client initialized!");
    }
}
