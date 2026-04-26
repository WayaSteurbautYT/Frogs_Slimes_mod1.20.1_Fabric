package com.wayacreate.frogslimegamemode.mixin;

import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import com.wayacreate.frogslimegamemode.util.CreateWorldState;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to automatically enable Frog & Slime gamemode when player joins
 * if the world was created with Frog & Slime mode selected.
 *
 * Temporarily disabled due to method signature mapping issues.
 * TODO: Fix once world creation UI is implemented.
 */
// @Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    // @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    private void onPlayerConnect(ServerPlayerEntity player, CallbackInfo ci) {
        // Check if world was created with frogslime mode
        if (CreateWorldState.isFrogSlimeMode()) {
            GamemodeManager.enableGamemode(player);
            CreateWorldState.resetFrogSlimeMode();
        }
    }
}
