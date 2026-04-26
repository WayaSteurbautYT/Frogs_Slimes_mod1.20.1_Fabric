package com.wayacreate.frogslimegamemode.client.keybind;

import com.wayacreate.frogslimegamemode.client.gui.FrogSlimeGamemodeScreen;
import com.wayacreate.frogslimegamemode.gui.SkillsScreen;
import com.wayacreate.frogslimegamemode.gui.CollectionsScreen;
import com.wayacreate.frogslimegamemode.network.ModNetworking;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModKeybinds {
    public static KeyBinding openGuiKey;
    public static KeyBinding openSkillsKey;
    public static KeyBinding openCollectionsKey;
    public static KeyBinding useAbilityKey;
    
    public static void register() {
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.frogslimegamemode.open_gui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "category.frogslimegamemode"
        ));
        
        openSkillsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.frogslimegamemode.open_skills",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "category.frogslimegamemode"
        ));
        
        openCollectionsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.frogslimegamemode.open_collections",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            "category.frogslimegamemode"
        ));
        
        useAbilityKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.frogslimegamemode.use_ability",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "category.frogslimegamemode"
        ));
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openGuiKey.wasPressed()) {
                if (client.player != null) {
                    client.setScreen(new FrogSlimeGamemodeScreen(client.player));
                }
            }
            if (openSkillsKey.wasPressed()) {
                client.setScreen(new SkillsScreen());
            }
            if (openCollectionsKey.wasPressed()) {
                client.setScreen(new CollectionsScreen());
            }
            if (useAbilityKey.wasPressed()) {
                if (client.player != null) {
                    ClientPlayNetworking.send(ModNetworking.USE_ABILITY, 
                        net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create());
                }
            }
        });
    }
}
