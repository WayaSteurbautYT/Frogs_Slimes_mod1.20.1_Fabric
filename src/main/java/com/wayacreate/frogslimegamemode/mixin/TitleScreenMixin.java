package com.wayacreate.frogslimegamemode.mixin;

import com.wayacreate.frogslimegamemode.client.ModTitleScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

/**
 * Mixin to replace the vanilla title screen with our custom one.
 */
@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void onInit(CallbackInfo ci) {
        // Only replace if this is the vanilla TitleScreen, not ModTitleScreen
        if (!Objects.equals(this.getClass().getName(), ModTitleScreen.class.getName())) {
            Minecraft client = Minecraft.getInstance();
            client.setScreen(new ModTitleScreen());
            ci.cancel();
        }
    }
}
