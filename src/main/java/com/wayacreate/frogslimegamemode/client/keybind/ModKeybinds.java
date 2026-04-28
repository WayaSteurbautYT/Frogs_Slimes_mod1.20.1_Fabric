package com.wayacreate.frogslimegamemode.client.keybind;

import com.wayacreate.frogslimegamemode.client.gui.FrogSlimeGamemodeScreen;
import com.wayacreate.frogslimegamemode.client.gui.SkillsScreen;
import com.wayacreate.frogslimegamemode.client.gui.CollectionsScreen;
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
    public static KeyBinding switchAbilityKey;
    public static KeyBinding consumeAbilityKey;
    
    // Manhunt keybinds
    public static KeyBinding hunterTrackKey;
    public static KeyBinding hunterBlockKey;
    public static KeyBinding hunterSlowKey;
    public static KeyBinding speedrunnerEscapeKey;
    public static KeyBinding speedrunnerSpeedKey;
    public static KeyBinding speedrunnerInvisKey;
    
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
        
        switchAbilityKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.frogslimegamemode.switch_ability",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_TAB,
            "category.frogslimegamemode"
        ));
        
        consumeAbilityKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.frogslimegamemode.consume_ability",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_X,
            "category.frogslimegamemode"
        ));
        
        // Manhunt keybinds
        hunterTrackKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.frogslimegamemode.hunter_track",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "category.frogslimegamemode"
        ));
        
        hunterBlockKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.frogslimegamemode.hunter_block",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_F,
            "category.frogslimegamemode"
        ));
        
        hunterSlowKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.frogslimegamemode.hunter_slow",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "category.frogslimegamemode"
        ));
        
        speedrunnerEscapeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.frogslimegamemode.speedrunner_escape",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "category.frogslimegamemode"
        ));
        
        speedrunnerSpeedKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.frogslimegamemode.speedrunner_speed",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "category.frogslimegamemode"
        ));
        
        speedrunnerInvisKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.frogslimegamemode.speedrunner_invis",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
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
            if (switchAbilityKey.wasPressed()) {
                if (client.player != null) {
                    ClientPlayNetworking.send(ModNetworking.SWITCH_ABILITY, 
                        net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create());
                }
            }
            if (consumeAbilityKey.wasPressed()) {
                if (client.player != null) {
                    ClientPlayNetworking.send(ModNetworking.CONSUME_ABILITY_ITEM, 
                        net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create());
                }
            }
            
            // Manhunt keybinds
            if (hunterTrackKey.wasPressed()) {
                if (client.player != null) {
                    ClientPlayNetworking.send(ModNetworking.HUNTER_TRACK, 
                        net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create());
                }
            }
            if (hunterBlockKey.wasPressed()) {
                if (client.player != null) {
                    ClientPlayNetworking.send(ModNetworking.HUNTER_BLOCK, 
                        net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create());
                }
            }
            if (hunterSlowKey.wasPressed()) {
                if (client.player != null) {
                    ClientPlayNetworking.send(ModNetworking.HUNTER_SLOW, 
                        net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create());
                }
            }
            if (speedrunnerEscapeKey.wasPressed()) {
                if (client.player != null) {
                    ClientPlayNetworking.send(ModNetworking.SPEEDRUNNER_ESCAPE, 
                        net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create());
                }
            }
            if (speedrunnerSpeedKey.wasPressed()) {
                if (client.player != null) {
                    ClientPlayNetworking.send(ModNetworking.SPEEDRUNNER_SPEED, 
                        net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create());
                }
            }
            if (speedrunnerInvisKey.wasPressed()) {
                if (client.player != null) {
                    ClientPlayNetworking.send(ModNetworking.SPEEDRUNNER_INVIS, 
                        net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create());
                }
            }
        });
    }
}
