package com.wayacreate.frogslimegamemode.mixin;

import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import com.wayacreate.frogslimegamemode.gamemode.GamemodeState;
import com.wayacreate.frogslimegamemode.util.CreateWorldState;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to automatically enable Frog & Slime gamemode when player joins
 * if the world was created with Frog & Slime mode selected.
 */
@Mixin(net.minecraft.server.players.PlayerList.class)
public class PlayerManagerMixin {

    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    private void onPlayerConnect(ServerPlayer player, CallbackInfo ci) {
        boolean fromWorldPreset = CreateWorldState.isFrogSlimeMode();
        boolean serverAlreadyRunningGamemode = player.getServer() != null
            && GamemodeState.get(player.getServer()).hasAnyEnabledPlayers();

        if (fromWorldPreset || serverAlreadyRunningGamemode) {
            GamemodeManager.enableGamemode(player, false);
            CreateWorldState.resetFrogSlimeMode();
        }
    }
}
