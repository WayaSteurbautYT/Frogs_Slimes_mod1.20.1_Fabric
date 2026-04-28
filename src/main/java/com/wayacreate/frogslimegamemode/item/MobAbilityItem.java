package com.wayacreate.frogslimegamemode.item;

import com.wayacreate.frogslimegamemode.eating.MobAbility;
import com.wayacreate.frogslimegamemode.network.ModNetworking;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.level.Level;

public class MobAbilityItem extends Item {
    public static final String MOB_ABILITY_NBT = "MobAbility";
    public static final String ABILITY_ID_NBT = "AbilityId";
    
    public MobAbilityItem(Properties settings) {
        super(settings);
    }
    
    public static ItemStack createMobAbility(String abilityId) {
        MobAbility ability = MobAbility.getAbility(abilityId);
        if (ability == null) {
            return ItemStack.EMPTY;
        }
        
        // Use the specific mob item for this ability (not a totem)
        net.minecraft.world.item.Item displayItem = AbilityDropItem.getDropItemForAbility(abilityId);
        ItemStack item = new ItemStack(displayItem);
        CompoundTag nbt = item.getOrCreateNbt();
        nbt.putBoolean(MOB_ABILITY_NBT, true);
        nbt.putString(ABILITY_ID_NBT, abilityId);
        
        // Set color based on ability type
        ChatFormatting color = AbilityDropItem.getAbilityColor(ability);
        item.setCustomName(Component.literal(ability.getName() + " Ability")
            .formatted(color, ChatFormatting.BOLD));
        
        return item;
    }
    
    public static boolean isMobAbility(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        CompoundTag nbt = stack.getNbt();
        return nbt != null && nbt.getBoolean(MOB_ABILITY_NBT);
    }
    
    public static String getAbilityId(ItemStack stack) {
        if (!isMobAbility(stack)) return null;
        CompoundTag nbt = stack.getNbt();
        return nbt != null ? nbt.getString(ABILITY_ID_NBT) : null;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getStackInHand(hand);
        
        String abilityId = getAbilityId(stack);
        
        if (!isMobAbility(stack) || abilityId == null) {
            return InteractionResultHolder.pass(stack);
        }
        
        if (!world.isClient) {
            MobAbility ability = MobAbility.getAbility(abilityId);
            
            if (ability != null) {
                // Check if player is sneaking to add to helper, otherwise add to player
                boolean addToHelper = user.isSneaking();
                
                if (user instanceof ServerPlayer serverPlayer) {
                    // Get the item to display in the animation
                    net.minecraft.world.item.Item displayItem = AbilityDropItem.getDropItemForAbility(abilityId);
                    
                    if (addToHelper) {
                        // Add to helper abilities
                        var helpers = user.getWorld().getEntitiesByClass(
                            com.wayacreate.frogslimegamemode.entity.FrogHelperEntity.class,
                            user.getBoundingBox().expand(32),
                            frog -> frog.isOwner(user)
                        );
                        
                        var slimes = user.getWorld().getEntitiesByClass(
                            com.wayacreate.frogslimegamemode.entity.SlimeHelperEntity.class,
                            user.getBoundingBox().expand(32),
                            slime -> slime.isOwner(user)
                        );
                        
                        if (!helpers.isEmpty()) {
                            helpers.get(0).addAbility(ability);
                            ModNetworking.sendTotemAnimation(serverPlayer, 
                                "Helper Ability Added!", 
                                ability.getName() + " - " + ability.getDescription(), 
                                ChatFormatting.GREEN,
                                displayItem);
                            serverPlayer.playSound(net.minecraft.sounds.SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                            user.sendMessage(Component.literal("Your frog gained ")
                                .formatted(ChatFormatting.GREEN)
                                .append(ability.getFormattedName())
                                .append(Component.literal("!").formatted(ChatFormatting.GREEN)), false);
                        } else if (!slimes.isEmpty()) {
                            slimes.get(0).addAbility(ability);
                            ModNetworking.sendTotemAnimation(serverPlayer, 
                                "Helper Ability Added!", 
                                ability.getName() + " - " + ability.getDescription(), 
                                ChatFormatting.GREEN,
                                displayItem);
                            serverPlayer.playSound(net.minecraft.sounds.SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                            user.sendMessage(Component.literal("Your slime gained ")
                                .formatted(ChatFormatting.GREEN)
                                .append(ability.getFormattedName())
                                .append(Component.literal("!").formatted(ChatFormatting.GREEN)), false);
                        } else {
                            user.sendMessage(Component.literal("No helper nearby! Ability added to you instead.")
                                .formatted(ChatFormatting.YELLOW), false);
                            com.wayacreate.frogslimegamemode.gamemode.GamemodeManager.getData(serverPlayer).addAbility(abilityId);
                            ModNetworking.sendTotemAnimation(serverPlayer, 
                                "Ability Unlocked!", 
                                ability.getName() + " - " + ability.getDescription(), 
                                ChatFormatting.LIGHT_PURPLE,
                                displayItem);
                            serverPlayer.playSound(net.minecraft.sounds.SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                            user.sendMessage(Component.literal("You unlocked the ")
                                .formatted(ChatFormatting.LIGHT_PURPLE)
                                .append(ability.getFormattedName())
                                .append(Component.literal("! Press [TAB] to switch abilities.").formatted(ChatFormatting.YELLOW)), false);
                        }
                    } else {
                        // Add to player's unlocked abilities
                        com.wayacreate.frogslimegamemode.gamemode.GamemodeManager.getData(serverPlayer).addAbility(abilityId);
                        
                        // Send totem animation packet with item for particles
                        ModNetworking.sendTotemAnimation(serverPlayer, 
                            "Ability Unlocked!", 
                            ability.getName() + " - " + ability.getDescription(), 
                            ChatFormatting.LIGHT_PURPLE,
                            displayItem);
                        
                        // Send title animation
                        ModNetworking.showTitle(serverPlayer, 
                            "Ability Unlocked!", 
                            ability.getName() + " - " + ability.getDescription(), 
                            ChatFormatting.LIGHT_PURPLE);
                        
                        // Play level-up sound directly on server
                        serverPlayer.playSound(net.minecraft.sounds.SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                        
                        // Apply ability bonuses to the player
                        AbilityDropItem.applyAbilityToPlayerStatic(user, ability);
                        
                        // Send message
                        user.sendMessage(Component.literal("You unlocked the ")
                            .formatted(ChatFormatting.LIGHT_PURPLE)
                            .append(ability.getFormattedName())
                            .append(Component.literal("! Press [TAB] to switch abilities.").formatted(ChatFormatting.YELLOW)), false);
                    }
                }
                
                // Consume the item
                stack.decrement(1);
                
                return InteractionResultHolder.success(stack);
            }
        }
        
        return InteractionResultHolder.pass(stack);
    }
    
    /**
     * Consume a mob ability item from the player's hand when hotkey is pressed.
     * This is called from the server when the consume ability keybinding is pressed.
     */
    public static void consumeHeldAbilityItem(net.minecraft.server.level.ServerPlayer player) {
        // Check main hand first, then offhand
        ItemStack mainHand = player.getMainHandStack();
        ItemStack offHand = player.getOffHandStack();
        ItemStack abilityStack = null;
        boolean isMainHand = false;
        
        if (isMobAbility(mainHand)) {
            abilityStack = mainHand;
            isMainHand = true;
        } else if (isMobAbility(offHand)) {
            abilityStack = offHand;
            isMainHand = false;
        }
        
        if (abilityStack == null || abilityStack.isEmpty()) {
            player.sendMessage(Component.literal("Hold a Mob Ability item to consume it!")
                .formatted(ChatFormatting.RED), false);
            return;
        }
        
        String abilityId = getAbilityId(abilityStack);
        if (abilityId == null) {
            player.sendMessage(Component.literal("Invalid ability item!").formatted(ChatFormatting.RED), false);
            return;
        }
        
        MobAbility ability = MobAbility.getAbility(abilityId);
        if (ability == null) {
            player.sendMessage(Component.literal("Unknown ability!").formatted(ChatFormatting.RED), false);
            return;
        }
        
        // Check if already unlocked
        var playerData = com.wayacreate.frogslimegamemode.gamemode.GamemodeManager.getData(player);
        if (playerData.hasAbility(abilityId)) {
            player.sendMessage(Component.literal("You already have this ability!")
                .formatted(ChatFormatting.YELLOW), false);
            return;
        }
        
        // Add ability to player
        playerData.addAbility(abilityId);
        
        // Apply ability bonuses
        AbilityDropItem.applyAbilityToPlayerStatic(player, ability);
        
        // Get display item for effects
        net.minecraft.world.item.Item displayItem = AbilityDropItem.getDropItemForAbility(abilityId);
        
        // Send totem animation
        com.wayacreate.frogslimegamemode.network.ModNetworking.sendTotemAnimation(player, 
            "Ability Unlocked!", 
            ability.getName() + " - " + ability.getDescription(), 
            ChatFormatting.LIGHT_PURPLE,
            displayItem);
        
        // Play sound
        player.playSound(net.minecraft.sounds.SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        
        // Send message
        player.sendMessage(Component.literal("You consumed the ")
            .formatted(ChatFormatting.LIGHT_PURPLE)
            .append(ability.getFormattedName())
            .append(Component.literal("! Press [TAB] to switch abilities.").formatted(ChatFormatting.YELLOW)), false);
        
        // Consume the item from the correct hand
        if (isMainHand) {
            mainHand.decrement(1);
        } else {
            offHand.decrement(1);
        }
    }
}
