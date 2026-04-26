package com.wayacreate.frogslimegamemode.client.keybind;

import com.wayacreate.frogslimegamemode.client.gui.FrogSlimeGamemodeScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModKeybinds {
    public static KeyBinding openGuiKey;
    
    public static void register() {
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.frogslimegamemode.open_gui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "category.frogslimegamemode"
        ));
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openGuiKey.wasPressed()) {
                if (client.player != null) {
                    client.setScreen(new FrogSlimeGamemodeScreen(client.player));
                }
            }
        });
    }
}
