package com.wayacreate.frogslimegamemode.item;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;

public class ModPotions {
    public static final Potion FROG_POWER_POTION = new Potion(
        new MobEffectInstance(MobEffects.JUMP_BOOST, 3600, 2),
        new MobEffectInstance(MobEffects.SPEED, 3600, 1)
    );
    public static final Potion SLIME_RESILIENCE_POTION = new Potion(
        new MobEffectInstance(MobEffects.RESISTANCE, 3600, 1),
        new MobEffectInstance(MobEffects.REGENERATION, 1800, 1)
    );
    public static final Potion WAYACREATE_BLESSING_POTION = new Potion(
        new MobEffectInstance(MobEffects.STRENGTH, 3600, 2),
        new MobEffectInstance(MobEffects.SPEED, 3600, 2),
        new MobEffectInstance(MobEffects.REGENERATION, 1800, 1)
    );
    public static final Potion DERPY_CURSE_POTION = new Potion(
        new MobEffectInstance(MobEffects.SLOWNESS, 1200, 1),
        new MobEffectInstance(MobEffects.WEAKNESS, 1200, 0)
    );
    public static final Potion MANHUNT_TRACKER_POTION = new Potion(
        new MobEffectInstance(MobEffects.NIGHT_VISION, 4800, 0),
        new MobEffectInstance(MobEffects.SPEED, 2400, 1)
    );
    public static final Potion EVOLUTION_ELIXIR = new Potion(
        new MobEffectInstance(MobEffects.REGENERATION, 600, 2),
        new MobEffectInstance(MobEffects.STRENGTH, 1200, 1)
    );
    public static final Potion ENDER_SLIME_POTION = new Potion(
        new MobEffectInstance(MobEffects.INVISIBILITY, 2400, 0),
        new MobEffectInstance(MobEffects.SPEED, 2400, 1)
    );
    public static final Potion POISON_SLIME_POTION = new Potion(
        new MobEffectInstance(MobEffects.POISON, 900, 1),
        new MobEffectInstance(MobEffects.SLOWNESS, 900, 1)
    );
    public static final Potion TONGUE_GRAB_POTION = new Potion(
        new MobEffectInstance(MobEffects.STRENGTH, 2400, 1),
        new MobEffectInstance(MobEffects.JUMP_BOOST, 2400, 1)
    );

    public static void register(IEventBus modBus) {
        modBus.addListener(ModPotions::onRegister);
    }

    private static void onRegister(RegisterEvent event) {
        event.register(Registries.POTION, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "frog_power"), () -> FROG_POWER_POTION);
        event.register(Registries.POTION, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "slime_resilience"), () -> SLIME_RESILIENCE_POTION);
        event.register(Registries.POTION, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "wayacreate_blessing"), () -> WAYACREATE_BLESSING_POTION);
        event.register(Registries.POTION, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "derpy_curse"), () -> DERPY_CURSE_POTION);
        event.register(Registries.POTION, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "manhunt_tracker"), () -> MANHUNT_TRACKER_POTION);
        event.register(Registries.POTION, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "evolution_elixir"), () -> EVOLUTION_ELIXIR);
        event.register(Registries.POTION, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "ender_slime"), () -> ENDER_SLIME_POTION);
        event.register(Registries.POTION, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "poison_slime"), () -> POISON_SLIME_POTION);
        event.register(Registries.POTION, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "tongue_grab"), () -> TONGUE_GRAB_POTION);
    }
    
    public static ItemStack createPotionStack(Potion potion) {
        ItemStack stack = new ItemStack(Items.POTION);
        CompoundTag nbt = stack.getOrCreateNbt();
        nbt.putString("Potion", Registries.POTION.getId(potion).toString());
        return stack;
    }
    
    public static ItemStack createSplashPotionStack(Potion potion) {
        ItemStack stack = new ItemStack(Items.SPLASH_POTION);
        CompoundTag nbt = stack.getOrCreateNbt();
        nbt.putString("Potion", Registries.POTION.getId(potion).toString());
        return stack;
    }
    
    public static ItemStack createLingeringPotionStack(Potion potion) {
        ItemStack stack = new ItemStack(Items.LINGERING_POTION);
        CompoundTag nbt = stack.getOrCreateNbt();
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
