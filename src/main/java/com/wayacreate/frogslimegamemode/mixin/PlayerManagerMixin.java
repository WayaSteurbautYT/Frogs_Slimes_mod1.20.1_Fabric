package com.wayacreate.frogslimegamemode.mixin;

import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import com.wayacreate.frogslimegamemode.util.CreateWorldState;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to automatically enable Frog & Slime gamemode when player joins
 * if the world was created with Frog & Slime mode selected.
 */
@Mixin(net.minecraft.server.PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    private void onPlayerConnect(ServerPlayerEntity player, CallbackInfo ci) {
        // Check if world was created with frogslime mode
        if (CreateWorldState.isFrogSlimeMode()) {
            GamemodeManager.enableGamemode(player);
            CreateWorldState.resetFrogSlimeMode();
        }
    }
}
