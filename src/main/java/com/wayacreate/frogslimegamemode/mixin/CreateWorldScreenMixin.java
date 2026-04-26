package com.wayacreate.frogslimegamemode.mixin;

import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {
    
    @Shadow
    private GameMode gameMode;
    
    @Unique
    private int frogSlimeCycleIndex = 0;
    
    @Inject(method = "cycleGameMode", at = @At("HEAD"), cancellable = true)
    private void onCycleGameMode(CallbackInfoReturnable<Boolean> cir) {
        // Override the cycle to include our custom gamemode
        // Cycle: Survival (0), Creative (1), Adventure (2), Spectator (3), Frog & Slime (4)
        GameMode[] modes = {GameMode.SURVIVAL, GameMode.CREATIVE, GameMode.ADVENTURE, GameMode.SPECTATOR, GameMode.SURVIVAL};
        
        // Find current index
        int currentIndex = 0;
        if (this.gameMode == GameMode.CREATIVE) currentIndex = 1;
        else if (this.gameMode == GameMode.ADVENTURE) currentIndex = 2;
        else if (this.gameMode == GameMode.SPECTATOR) currentIndex = 3;
        else if (frogSlimeCycleIndex == 4) currentIndex = 4;
        
        // Cycle to next
        frogSlimeCycleIndex = (currentIndex + 1) % 5;
        this.gameMode = modes[frogSlimeCycleIndex];
        
        cir.setReturnValue(true);
    }
    
    @Inject(method = "getGameModeName", at = @At("HEAD"), cancellable = true)
    private void onGetGameModeName(CallbackInfoReturnable<Text> cir) {
        if (frogSlimeCycleIndex == 4) {
            cir.setReturnValue(Text.translatable("selectWorld.gameMode.frogslime"));
        }
    }
}
