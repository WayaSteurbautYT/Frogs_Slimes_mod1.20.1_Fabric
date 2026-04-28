package com.wayacreate.frogslimegamemode.item;

import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

public class FrogFoodItem extends Item {
    public static final FoodProperties FROG_FOOD_COMPONENT = new FoodProperties.Builder()
        .hunger(3)
        .saturationModifier(0.5f)
        .alwaysEdible()
        .build();
    
    public FrogFoodItem(Properties settings) {
        super(settings);
    }
    
    @Override
    public ItemStack finishUsing(ItemStack stack, Level world, LivingEntity user) {
        if (user instanceof Player player) {
            if (!world.isClient && GamemodeManager.isInGamemode(player)) {
                player.addStatusEffect(new MobEffectInstance(MobEffects.SPEED, 600, 1));
                player.addStatusEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 600, 0));
                player.sendMessage(Component.literal("You feel swift like a frog!")
                    .formatted(ChatFormatting.GREEN), true);
            }
        }
        return super.finishUsing(stack, world, user);
    }
}