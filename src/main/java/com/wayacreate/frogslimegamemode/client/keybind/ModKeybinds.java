package com.wayacreate.frogslimegamemode.client.keybind;

import com.wayacreate.frogslimegamemode.client.gui.CollectionsScreen;
import com.wayacreate.frogslimegamemode.client.gui.FrogSlimeGamemodeScreen;
import com.wayacreate.frogslimegamemode.client.gui.SkillsScreen;
import com.wayacreate.frogslimegamemode.network.ModNetworkingClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;

public class ModKeybinds {
    public static final KeyMapping openGuiKey = new KeyMapping(
        "key.frogslimegamemode.open_gui",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_G,
        "category.frogslimegamemode"
    );
    public static final KeyMapping openSkillsKey = new KeyMapping(
        "key.frogslimegamemode.open_skills",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_K,
        "category.frogslimegamemode"
    );
    public static final KeyMapping openCollectionsKey = new KeyMapping(
        "key.frogslimegamemode.open_collections",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_C,
        "category.frogslimegamemode"
    );
    public static final KeyMapping useAbilityKey = new KeyMapping(
        "key.frogslimegamemode.use_ability",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_R,
        "category.frogslimegamemode"
    );
    public static final KeyMapping switchAbilityKey = new KeyMapping(
        "key.frogslimegamemode.switch_ability",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_TAB,
        "category.frogslimegamemode"
    );
    public static final KeyMapping consumeAbilityKey = new KeyMapping(
        "key.frogslimegamemode.consume_ability",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_X,
        "category.frogslimegamemode"
    );

    private ModKeybinds() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(ModKeybinds::onRegisterKeyMappings);
        NeoForge.EVENT_BUS.addListener(ModKeybinds::onClientTick);
    }

    private static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(openGuiKey);
        event.register(openSkillsKey);
        event.register(openCollectionsKey);
        event.register(useAbilityKey);
        event.register(switchAbilityKey);
        event.register(consumeAbilityKey);
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) {
            return;
        }

        while (openGuiKey.wasPressed()) {
            ModNetworkingClient.requestProgressSnapshot();
            client.setScreen(new FrogSlimeGamemodeScreen(client.player));
        }

        while (openSkillsKey.wasPressed()) {
            ModNetworkingClient.requestProgressSnapshot();
            client.setScreen(new SkillsScreen());
        }

        while (openCollectionsKey.wasPressed()) {
            ModNetworkingClient.requestProgressSnapshot();
            client.setScreen(new CollectionsScreen());
        }

        while (useAbilityKey.wasPressed()) {
            ModNetworkingClient.sendUseAbility();
        }

        while (switchAbilityKey.wasPressed()) {
            ModNetworkingClient.sendSwitchAbility();
        }

        while (consumeAbilityKey.wasPressed()) {
            ModNetworkingClient.sendConsumeAbilityItem();
        }
    }
}
