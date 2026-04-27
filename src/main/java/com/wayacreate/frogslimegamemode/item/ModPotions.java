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
    public static Potion FROG_POWER_POTION;
    public static Potion SLIME_RESILIENCE_POTION;
    public static Potion WAYACREATE_BLESSING_POTION;
    public static Potion DERPY_CURSE_POTION;
    public static Potion MANHUNT_TRACKER_POTION;
    public static Potion EVOLUTION_ELIXIR;
    public static Potion ENDER_SLIME_POTION;
    public static Potion POISON_SLIME_POTION;
    public static Potion TONGUE_GRAB_POTION;
    
    private static Potion registerPotion(String name, Potion potion) {
        return Registry.register(Registries.POTION, new Identifier("frogslimegamemode", name), potion);
    }
    
    public static void register() {
        FROG_POWER_POTION = registerPotion("frog_power", 
            new Potion(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 3600, 2),
                      new StatusEffectInstance(StatusEffects.SPEED, 3600, 1)));
        
        SLIME_RESILIENCE_POTION = registerPotion("slime_resilience",
            new Potion(new StatusEffectInstance(StatusEffects.RESISTANCE, 3600, 1),
                      new StatusEffectInstance(StatusEffects.REGENERATION, 1800, 1)));
        
        WAYACREATE_BLESSING_POTION = registerPotion("wayacreate_blessing",
            new Potion(new StatusEffectInstance(StatusEffects.STRENGTH, 3600, 2),
                      new StatusEffectInstance(StatusEffects.SPEED, 3600, 2),
                      new StatusEffectInstance(StatusEffects.REGENERATION, 1800, 1)));
        
        DERPY_CURSE_POTION = registerPotion("derpy_curse",
            new Potion(new StatusEffectInstance(StatusEffects.SLOWNESS, 1200, 1),
                      new StatusEffectInstance(StatusEffects.WEAKNESS, 1200, 0)));
        
        MANHUNT_TRACKER_POTION = registerPotion("manhunt_tracker",
            new Potion(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 4800, 0),
                      new StatusEffectInstance(StatusEffects.SPEED, 2400, 1)));
        
        EVOLUTION_ELIXIR = registerPotion("evolution_elixir",
            new Potion(new StatusEffectInstance(StatusEffects.REGENERATION, 600, 2),
                      new StatusEffectInstance(StatusEffects.STRENGTH, 1200, 1)));
        
        ENDER_SLIME_POTION = registerPotion("ender_slime",
            new Potion(new StatusEffectInstance(StatusEffects.INVISIBILITY, 2400, 0),
                      new StatusEffectInstance(StatusEffects.SPEED, 2400, 1)));
        
        POISON_SLIME_POTION = registerPotion("poison_slime",
            new Potion(new StatusEffectInstance(StatusEffects.POISON, 900, 1),
                      new StatusEffectInstance(StatusEffects.SLOWNESS, 900, 1)));
        
        TONGUE_GRAB_POTION = registerPotion("tongue_grab",
            new Potion(new StatusEffectInstance(StatusEffects.STRENGTH, 2400, 1),
                      new StatusEffectInstance(StatusEffects.JUMP_BOOST, 2400, 1)));
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
