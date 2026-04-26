package com.wayacreate.frogslimegamemode.mixin;

import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to detect when a world loads and check for Frog & Slime gamemode.
 * Gamemode is enabled via /frogslime enable command.
 */
@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        // Gamemode detection is handled by GamemodeManager
        // This mixin is kept for future world-based detection
    }
}
