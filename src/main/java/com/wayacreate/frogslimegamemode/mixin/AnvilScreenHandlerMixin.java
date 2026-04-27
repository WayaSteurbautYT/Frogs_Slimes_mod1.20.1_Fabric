package com.wayacreate.frogslimegamemode.mixin;

import com.wayacreate.frogslimegamemode.item.AbilityDropItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {
    @Inject(method = "onTakeOutput", at = @At("HEAD"), cancellable = true)
    private void onTakeOutput(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        AnvilScreenHandler handler = (AnvilScreenHandler) (Object) this;
        ItemStack leftSlot = handler.getSlot(0).getStack();
        ItemStack rightSlot = handler.getSlot(1).getStack();
        
        // Check if combining ability drop with mob drop
        if (AbilityDropItem.isAbilityDrop(leftSlot) && !rightSlot.isEmpty()) {
            String abilityId = AbilityDropItem.getAbilityId(leftSlot);
            String mobItemId = net.minecraft.registry.Registries.ITEM.getId(rightSlot.getItem()).getPath();
            
            // Map mob drops to abilities
            String targetAbility = getAbilityFromMobDrop(mobItemId);
            if (targetAbility != null && !targetAbility.equals(abilityId)) {
                // Create new ability drop
                ItemStack newAbilityDrop = AbilityDropItem.createAbilityDrop(targetAbility);
                
                // Set output
                handler.getSlot(2).setStack(newAbilityDrop);
                
                // Consume items
                leftSlot.decrement(1);
                rightSlot.decrement(1);
                
                ci.cancel();
            }
        }
    }
    
    private String getAbilityFromMobDrop(String itemId) {
        return switch (itemId) {
            case "rotten_flesh" -> "zombie";
            case "bone" -> "skeleton";
            case "spider_eye" -> "spider";
            case "gunpowder" -> "creeper";
            case "ender_pearl" -> "enderman";
            case "blaze_rod" -> "blaze";
            case "slime_ball" -> "slime";
            case "string" -> "cave_spider";
            case "ghast_tear" -> "ghast";
            case "phantom_membrane" -> "phantom";
            case "shulker_shell" -> "shulker";
            case "dragon_egg" -> "ender_dragon";
            case "rabbit_foot" -> "rabbit";
            case "turtle_shell" -> "turtle";
            case "trident" -> "drowned";
            case "nautilus_shell" -> "elder_guardian";
            default -> null;
        };
    }
}
