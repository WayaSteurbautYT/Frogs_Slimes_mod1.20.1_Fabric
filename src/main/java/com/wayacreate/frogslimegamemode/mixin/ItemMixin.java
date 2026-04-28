package com.wayacreate.frogslimegamemode.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.world.item.Item.class)
public class ItemMixin {
    
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onUse(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack stack = user.getStackInHand(hand);
        
        // Check if this item is an ability drop via NBT
        if (stack.hasNbt()) {
            CompoundTag nbt = stack.getNbt();
            if (nbt != null && nbt.getBoolean("AbilityDrop")) {
                // Cancel vanilla use behavior for ability drops (prevent throwing snowballs/ender pearls)
                cir.setReturnValue(InteractionResultHolder.pass(stack));
                return;
            }
        }
    }
}
