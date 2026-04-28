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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = FrogSlimeGamemode.MOD_ID, dist = Dist.CLIENT)
public class FrogSlimeGamemodeClient {
    public FrogSlimeGamemodeClient(IEventBus modBus) {
        ModNetworkingClient.register(modBus);
        ModKeybinds.register(modBus);

        modBus.addListener(FrogSlimeGamemodeClient::onRegisterEntityRenderers);
        modBus.addListener(FrogSlimeGamemodeClient::onRegisterBlockColors);

        NeoForge.EVENT_BUS.addListener(GamemodeHud::onRender);
        NeoForge.EVENT_BUS.addListener(ManhuntHud::onRender);

        FrogSlimeGamemode.LOGGER.info("Frog & Slime Gamemode client initialized!");
    }

    private static void onRegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.FROG_HELPER, FrogHelperRenderer::new);
        event.registerEntityRenderer(ModEntities.SLIME_HELPER, SlimeHelperRenderer::new);
        event.registerEntityRenderer(ModEntities.GIANT_SLIME_BOSS, GiantSlimeBossRenderer::new);
        event.registerEntityRenderer(ModEntities.FROG_KING, FrogKingRenderer::new);
        event.registerEntityRenderer(ModEntities.SLIME_ENDERMAN, SlimeEndermanRenderer::new);
    }

    private static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, world, pos, tintIndex) -> 0x4CAF50, ModBlocks.FROG_CRAFTING_TABLE);
        event.register((state, world, pos, tintIndex) -> 0x4CAF50, ModBlocks.FROG_POTION_STAND);
    }
}
