package com.wayacreate.frogslimegamemode;

import com.wayacreate.frogslimegamemode.block.ModBlocks;
import com.wayacreate.frogslimegamemode.client.hud.GamemodeHud;
import com.wayacreate.frogslimegamemode.client.hud.ManhuntHud;
import com.wayacreate.frogslimegamemode.client.keybind.ModKeybinds;
import com.wayacreate.frogslimegamemode.entity.ModEntities;
import com.wayacreate.frogslimegamemode.entity.client.FrogHelperRenderer;
import com.wayacreate.frogslimegamemode.entity.client.FrogKingRenderer;
import com.wayacreate.frogslimegamemode.entity.client.GiantSlimeBossRenderer;
import com.wayacreate.frogslimegamemode.entity.client.SlimeEndermanRenderer;
import com.wayacreate.frogslimegamemode.entity.client.SlimeHelperRenderer;
import com.wayacreate.frogslimegamemode.network.ModNetworkingClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class FrogSlimeGamemodeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        GamemodeHud.onInitializeClient();
        ManhuntHud.onInitializeClient();
        
        ModNetworkingClient.registerClient();
        ModKeybinds.register();
        
        // Register entity renderers
        EntityRendererRegistry.register(ModEntities.FROG_HELPER, FrogHelperRenderer::new);
        EntityRendererRegistry.register(ModEntities.SLIME_HELPER, SlimeHelperRenderer::new);
        EntityRendererRegistry.register(ModEntities.GIANT_SLIME_BOSS, GiantSlimeBossRenderer::new);
        EntityRendererRegistry.register(ModEntities.FROG_KING, FrogKingRenderer::new);
        EntityRendererRegistry.register(ModEntities.SLIME_ENDERMAN, SlimeEndermanRenderer::new);
        
        // Register block color providers for hue shift
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
            // Shift hue to green (frog theme)
            return 0x4CAF50;
        }, ModBlocks.FROG_CRAFTING_TABLE);
        
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
            // Shift hue to green (frog theme)
            return 0x4CAF50;
        }, ModBlocks.FROG_POTION_STAND);
        
        FrogSlimeGamemode.LOGGER.info("Frog & Slime Gamemode client initialized!");
    }
}
