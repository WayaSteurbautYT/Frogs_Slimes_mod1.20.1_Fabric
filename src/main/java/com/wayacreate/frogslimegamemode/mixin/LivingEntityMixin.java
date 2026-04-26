package com.wayacreate.frogslimegamemode.mixin;

import com.wayacreate.frogslimegamemode.entity.FrogHelperEntity;
import com.wayacreate.frogslimegamemode.entity.SlimeHelperEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(DamageSource damageSource, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        
        if (!entity.getWorld().isClient && damageSource.getAttacker() != null) {
            if (damageSource.getAttacker() instanceof FrogHelperEntity frog) {
                frog.onKilledMob();
            } else if (damageSource.getAttacker() instanceof SlimeHelperEntity slime) {
                slime.onKilledMob();
            }
        }
    }
}
