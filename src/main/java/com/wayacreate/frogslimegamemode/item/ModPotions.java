package com.wayacreate.frogslimegamemode.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModPotions {
    public static final Potion FROG_POWER_POTION = registerPotion("frog_power", 
        new Potion(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 3600, 2),
                  new StatusEffectInstance(StatusEffects.SPEED, 3600, 1)));
    
    public static final Potion SLIME_RESILIENCE_POTION = registerPotion("slime_resilience",
        new Potion(new StatusEffectInstance(StatusEffects.RESISTANCE, 3600, 1),
                  new StatusEffectInstance(StatusEffects.REGENERATION, 1800, 1)));
    
    public static final Potion WAYACREATE_BLESSING_POTION = registerPotion("wayacreate_blessing",
        new Potion(new StatusEffectInstance(StatusEffects.STRENGTH, 3600, 2),
                  new StatusEffectInstance(StatusEffects.SPEED, 3600, 2),
                  new StatusEffectInstance(StatusEffects.REGENERATION, 1800, 1)));
    
    public static final Potion DERPY_CURSE_POTION = registerPotion("derpy_curse",
        new Potion(new StatusEffectInstance(StatusEffects.SLOWNESS, 1200, 1),
                  new StatusEffectInstance(StatusEffects.WEAKNESS, 1200, 0)));
    
    public static final Potion MANHUNT_TRACKER_POTION = registerPotion("manhunt_tracker",
        new Potion(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 4800, 0),
                  new StatusEffectInstance(StatusEffects.SPEED, 2400, 1)));
    
    private static Potion registerPotion(String name, Potion potion) {
        return Registry.register(Registries.POTION, new Identifier("frogslimegamemode", name), potion);
    }
    
    public static void register() {
        // Potions are registered in static block
    }
    
    public static ItemStack createPotionStack(Potion potion) {
        ItemStack stack = new ItemStack(Items.POTION);
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putString("Potion", Registries.POTION.getId(potion).toString());
        return stack;
    }
    
    public static ItemStack createSplashPotionStack(Potion potion) {
        ItemStack stack = new ItemStack(Items.SPLASH_POTION);
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putString("Potion", Registries.POTION.getId(potion).toString());
        return stack;
    }
    
    public static ItemStack createLingeringPotionStack(Potion potion) {
        ItemStack stack = new ItemStack(Items.LINGERING_POTION);
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putString("Potion", Registries.POTION.getId(potion).toString());
        return stack;
    }
    
    public static Item getPotionItem(Potion potion) {
        return Items.POTION;
    }
    
    public static ItemStack createVanillaPotionStack() {
        return createPotionStack(Potions.HEALING);
    }
}
