package com.wayacreate.frogslimegamemode.mixin;

import com.wayacreate.frogslimegamemode.client.ModTitleScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to replace the vanilla title screen with our custom one.
 */
@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void onInit(CallbackInfo ci) {
        // Only replace if this is the vanilla TitleScreen, not ModTitleScreen
        if (!this.getClass().getName().equals(ModTitleScreen.class.getName())) {
            MinecraftClient client = MinecraftClient.getInstance();
            client.setScreen(new ModTitleScreen());
            ci.cancel();
        }
    }
}
