package com.wayacreate.frogslimegamemode.mixin;

import com.wayacreate.frogslimegamemode.util.CreateWorldState;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to handle Frog & Slime gamemode state during world creation.
 * Note: UI button implementation would require complex mixin work due to visibility constraints.
 * Users can enable the gamemode via the /frogslime enable command.
 */
@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {

    @Inject(method = "createLevel", at = @At("HEAD"))
    private void onCreateLevel(CallbackInfo ci) {
        // Reset gamemode state after world creation
        CreateWorldState.resetFrogSlimeMode();
    }
}
