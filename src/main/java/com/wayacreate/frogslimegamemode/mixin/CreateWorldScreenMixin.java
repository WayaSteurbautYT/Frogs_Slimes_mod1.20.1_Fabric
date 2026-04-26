package com.wayacreate.frogslimegamemode.mixin;

import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to add Frog & Slime gamemode option to world creation screen.
 * World name containing "frogslime" will auto-enable the gamemode.
 */
@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {
    
    @Unique
    private static boolean globalFrogSlimeMode = false;
    
    @Inject(method = "createLevel", at = @At("HEAD"))
    private void onCreateLevel(CallbackInfo ci) {
        // For now, gamemode is enabled via /frogslime enable command
        // This mixin is kept for future world creation UI integration
        globalFrogSlimeMode = false;
    }
    
    public static boolean isFrogSlimeMode() {
        return globalFrogSlimeMode;
    }
    
    public static void resetFrogSlimeMode() {
        globalFrogSlimeMode = false;
    }
}
