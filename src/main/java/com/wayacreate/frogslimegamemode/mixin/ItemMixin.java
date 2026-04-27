package com.wayacreate.frogslimegamemode.mixin;

import com.wayacreate.frogslimegamemode.eating.MobAbility;
import com.wayacreate.frogslimegamemode.item.AbilityDropItem;
import com.wayacreate.frogslimegamemode.network.ModNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.item.Item.class)
public class ItemMixin {
    
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack stack = user.getStackInHand(hand);
        
        // Check if this item is an ability drop via NBT
        if (stack.hasNbt()) {
            NbtCompound nbt = stack.getNbt();
            if (nbt != null && nbt.getBoolean("AbilityDrop")) {
                String abilityId = nbt.getString("AbilityId");
                
                if (!abilityId.isEmpty() && !world.isClient) {
                    MobAbility ability = MobAbility.getAbility(abilityId);
                    
                    if (ability != null) {
                        // Add ability to player's unlocked abilities
                        if (user instanceof net.minecraft.server.network.ServerPlayerEntity serverPlayer) {
                            com.wayacreate.frogslimegamemode.gamemode.GamemodeManager.getData(serverPlayer).addAbility(abilityId);
                            
                            // Get the item to display in the animation
                            net.minecraft.item.Item displayItem = AbilityDropItem.getDropItemForAbility(abilityId);
                            
                            // Send totem animation packet with item for particles
                            ModNetworking.sendTotemAnimation(serverPlayer, 
                                "Ability Unlocked!", 
                                ability.getName() + " - " + ability.getDescription(), 
                                Formatting.LIGHT_PURPLE,
                                displayItem);
                            
                            // Send title animation
                            ModNetworking.showTitle(serverPlayer, 
                                "Ability Unlocked!", 
                                ability.getName() + " - " + ability.getDescription(), 
                                Formatting.LIGHT_PURPLE);
                            
                            // Play level-up sound directly on server (client will hear it)
                            serverPlayer.playSound(net.minecraft.sound.SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                        }
                        
                        // Apply ability bonuses to the player
                        AbilityDropItem.applyAbilityToPlayerStatic(user, ability);
                        
                        // Consume the item
                        stack.decrement(1);
                        
                        // Send message
                        user.sendMessage(Text.literal("You unlocked the ")
                            .formatted(Formatting.LIGHT_PURPLE)
                            .append(ability.getFormattedName())
                            .append(Text.literal("! Press [TAB] to switch abilities.").formatted(Formatting.YELLOW)), false);
                        
                        cir.setReturnValue(TypedActionResult.success(stack));
                        return;
                    }
                }
            }
        }
    }
}
