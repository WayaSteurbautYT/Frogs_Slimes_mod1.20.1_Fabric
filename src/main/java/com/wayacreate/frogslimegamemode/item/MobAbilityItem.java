package com.wayacreate.frogslimegamemode.item;

import com.wayacreate.frogslimegamemode.eating.MobAbility;
import com.wayacreate.frogslimegamemode.network.ModNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class MobAbilityItem extends Item {
    public static final String MOB_ABILITY_NBT = "MobAbility";
    public static final String ABILITY_ID_NBT = "AbilityId";
    
    public MobAbilityItem(Settings settings) {
        super(settings);
    }
    
    public static ItemStack createMobAbility(String abilityId) {
        MobAbility ability = MobAbility.getAbility(abilityId);
        if (ability == null) {
            return ItemStack.EMPTY;
        }
        
        // Create the actual mob ability item
        ItemStack item = new ItemStack(ModItems.MOB_ABILITY);
        NbtCompound nbt = item.getOrCreateNbt();
        nbt.putBoolean(MOB_ABILITY_NBT, true);
        nbt.putString(ABILITY_ID_NBT, abilityId);
        
        // Set color based on ability type
        Formatting color = AbilityDropItem.getAbilityColor(ability);
        item.setCustomName(Text.literal(ability.getName() + " Ability")
            .formatted(color, Formatting.BOLD));
        
        return item;
    }
    
    public static boolean isMobAbility(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        NbtCompound nbt = stack.getNbt();
        return nbt != null && nbt.getBoolean(MOB_ABILITY_NBT);
    }
    
    public static String getAbilityId(ItemStack stack) {
        if (!isMobAbility(stack)) return null;
        NbtCompound nbt = stack.getNbt();
        return nbt != null ? nbt.getString(ABILITY_ID_NBT) : null;
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        
        String abilityId = getAbilityId(stack);
        
        if (!isMobAbility(stack) || abilityId == null) {
            return TypedActionResult.pass(stack);
        }
        
        if (!world.isClient) {
            MobAbility ability = MobAbility.getAbility(abilityId);
            
            if (ability != null) {
                // Add ability to player's unlocked abilities
                if (user instanceof ServerPlayerEntity serverPlayer) {
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
                    
                    // Play level-up sound directly on server
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
                
                return TypedActionResult.success(stack);
            }
        }
        
        return TypedActionResult.pass(stack);
    }
}
