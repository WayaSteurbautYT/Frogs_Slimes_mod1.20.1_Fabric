package com.wayacreate.frogslimegamemode.item;

import com.wayacreate.frogslimegamemode.eating.MobAbility;
import com.wayacreate.frogslimegamemode.network.ModNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class AbilityDropItem extends Item {
    public static final String ABILITY_DROP_NBT = "AbilityDrop";
    public static final String ABILITY_ID_NBT = "AbilityId";
    
    public AbilityDropItem(Settings settings) {
        super(settings);
    }
    
    public static ItemStack createAbilityDrop(String abilityId) {
        MobAbility ability = MobAbility.getAbility(abilityId);
        if (ability == null) {
            return ItemStack.EMPTY;
        }
        
        // Use vanilla items based on mob drops
        ItemStack drop = new ItemStack(getDropItemForAbility(abilityId));
        NbtCompound nbt = drop.getOrCreateNbt();
        nbt.putBoolean(ABILITY_DROP_NBT, true);
        nbt.putString(ABILITY_ID_NBT, abilityId);
        
        // Set color based on ability type
        Formatting color = getAbilityColor(ability);
        drop.setCustomName(Text.literal("Ability: " + ability.getName())
            .formatted(color, Formatting.BOLD));
        
        return drop;
    }
    
    private static Item getDropItemForAbility(String abilityId) {
        // Map mob abilities to their vanilla drop items
        switch (abilityId) {
            case "blaze":
                return Items.BLAZE_ROD;
            case "enderman":
                return Items.ENDER_PEARL;
            case "iron_golem":
                return Items.IRON_INGOT;
            case "snow_golem":
                return Items.SNOWBALL;
            case "witch":
                return Items.GLASS_BOTTLE;
            case "ghast":
                return Items.GHAST_TEAR;
            case "piglin":
                return Items.GOLD_INGOT;
            case "warden":
                return Items.SCULK_CATALYST;
            case "creeper":
                return Items.GUNPOWDER;
            case "spider":
                return Items.STRING;
            case "skeleton":
                return Items.BONE;
            case "zombie":
                return Items.ROTTEN_FLESH;
            case "pig":
                return Items.PORKCHOP;
            case "cow":
                return Items.LEATHER;
            case "chicken":
                return Items.FEATHER;
            case "sheep":
                return Items.WHITE_WOOL;
            case "rabbit":
                return Items.RABBIT_HIDE;
            case "villager":
                return Items.EMERALD;
            default:
                return Items.STICK;
        }
    }
    
    private static Formatting getAbilityColor(MobAbility ability) {
        switch (ability.getActiveAbility()) {
            case FIREBALL:
                return Formatting.RED;
            case LEAP_ATTACK:
                return Formatting.GREEN;
            case TELEPORT:
                return Formatting.DARK_PURPLE;
            case POISON_CLOUD:
                return Formatting.DARK_GREEN;
            case ICE_SUMMON:
                return Formatting.AQUA;
            case LIGHTNING_STRIKE:
                return Formatting.YELLOW;
            case SONIC_BOOM:
                return Formatting.BLUE;
            case WEB_SHOT:
                return Formatting.GRAY;
            case THORNS:
                return Formatting.DARK_RED;
            case NONE:
            default:
                return Formatting.LIGHT_PURPLE;
        }
    }
    
    public static boolean isAbilityDrop(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        NbtCompound nbt = stack.getNbt();
        return nbt != null && nbt.getBoolean(ABILITY_DROP_NBT);
    }
    
    public static String getAbilityId(ItemStack stack) {
        if (!isAbilityDrop(stack)) return null;
        NbtCompound nbt = stack.getNbt();
        return nbt != null ? nbt.getString(ABILITY_ID_NBT) : null;
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        
        if (!isAbilityDrop(stack)) {
            return TypedActionResult.pass(stack);
        }
        
        String abilityId = getAbilityId(stack);
        if (abilityId == null) {
            return TypedActionResult.pass(stack);
        }
        
        if (!world.isClient) {
            MobAbility ability = MobAbility.getAbility(abilityId);
            if (ability != null) {
                // Add ability to player's unlocked abilities
                if (user instanceof ServerPlayerEntity serverPlayer) {
                    com.wayacreate.frogslimegamemode.gamemode.GamemodeManager.getData(serverPlayer).addAbility(abilityId);
                    
                    // Send totem animation with title popup and exp sound
                    ModNetworking.sendTotemAnimation(serverPlayer, 
                        "Ability Unlocked!", 
                        ability.getName() + " - " + ability.getDescription(), 
                        Formatting.LIGHT_PURPLE);
                }
                
                // Apply ability bonuses to the player
                applyAbilityToPlayer(user, ability);
                
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
    
    private void applyAbilityToPlayer(PlayerEntity player, MobAbility ability) {
        // Apply temporary or permanent ability effects to player
        // For now, we'll apply temporary effects
        int duration = 600; // 30 seconds
        
        // Speed bonus
        if (ability.getSpeedBonus() > 0) {
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.SPEED,
                duration, (int) Math.min(ability.getSpeedBonus() * 2, 2)));
        }
        
        // Health bonus (absorption)
        if (ability.getHealthBonus() > 0) {
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.ABSORPTION,
                duration, (int) Math.min(ability.getHealthBonus() / 5, 4)));
        }
        
        // Damage bonus (strength)
        if (ability.getDamageBonus() > 0) {
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.STRENGTH,
                duration, (int) Math.min(ability.getDamageBonus() / 3, 2)));
        }
        
        // Fire resistance for fire-based abilities
        if (ability.getActiveAbility() == MobAbility.AbilityType.FIREBALL) {
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.FIRE_RESISTANCE,
                duration, 0));
        }
        
        // Jump boost for leap attack
        if (ability.getActiveAbility() == MobAbility.AbilityType.LEAP_ATTACK) {
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.JUMP_BOOST,
                duration, 2));
        }
        
        // Night vision for enderman ability
        if (ability.getActiveAbility() == MobAbility.AbilityType.TELEPORT) {
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.NIGHT_VISION,
                duration, 0));
        }
    }
}
