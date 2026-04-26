package com.wayacreate.frogslimegamemode.item;

import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class SlimeFoodItem extends Item {
    public static final FoodComponent SLIME_FOOD_COMPONENT = new FoodComponent.Builder()
        .hunger(4)
        .saturationModifier(0.6f)
        .alwaysEdible()
        .build();
    
    public SlimeFoodItem(Settings settings) {
        super(settings);
    }
    
    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof PlayerEntity player) {
            if (!world.isClient && GamemodeManager.isInGamemode(player)) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 600, 1));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 600, 0));
                player.sendMessage(Text.literal("You feel bouncy like a slime!")
                    .formatted(Formatting.GREEN), true);
            }
        }
        return super.finishUsing(stack, world, user);
    }
}