package com.wayacreate.frogslimegamemode.mixin;

import com.wayacreate.frogslimegamemode.util.CreateWorldState;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to add Frog & Slime gamemode option to world creation screen.
 * TODO: Add UI button - currently gamemode must be enabled via /frogslime enable command
 */
@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {

    @Inject(method = "createLevel", at = @At("HEAD"))
    private void onCreateLevel(CallbackInfo ci) {
        // Reset gamemode state after world creation
        CreateWorldState.resetFrogSlimeMode();
    }
}
