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
        // Map mob abilities to their vanilla drop items - comprehensive coverage
        return switch (abilityId) {
            // Nether mobs
            case "blaze", "magma_cube" -> Items.BLAZE_ROD;
            case "enderman", "endermite", "vex" -> Items.ENDER_PEARL;
            case "ghast" -> Items.GHAST_TEAR;
            case "piglin", "zombified_piglin", "piglin_brute", "hoglin", "zoglin" -> Items.GOLD_INGOT;
            case "strider" -> Items.STRING;
            
            // Overworld hostile mobs
            case "zombie", "husk", "drowned" -> Items.ROTTEN_FLESH;
            case "skeleton", "stray", "wither_skeleton" -> Items.BONE;
            case "creeper" -> Items.GUNPOWDER;
            case "spider", "cave_spider" -> Items.STRING;
            case "silverfish" -> Items.SPIDER_EYE;
            
            // Special mobs
            case "warden" -> Items.SCULK_CATALYST;
            case "phantom" -> Items.PHANTOM_MEMBRANE;
            case "witch" -> Items.GLASS_BOTTLE;
            case "evoker", "vindicator", "pillager", "ravager" -> Items.EMERALD;
            case "guardian", "elder_guardian" -> Items.PRISMARINE_SHARD;
            case "shulker" -> Items.SHULKER_SHELL;
            
            // Passive mobs
            case "pig" -> Items.PORKCHOP;
            case "cow", "mooshroom" -> Items.LEATHER;
            case "chicken" -> Items.FEATHER;
            case "sheep" -> Items.WHITE_WOOL;
            case "rabbit" -> Items.RABBIT_HIDE;
            case "horse", "donkey", "mule", "llama", "trader_llama" -> Items.LEATHER;
            case "fox", "wolf", "cat", "ocelot" -> Items.BONE;
            case "parrot" -> Items.FEATHER;
            case "turtle" -> Items.SCUTE;
            case "dolphin" -> Items.COD;
            case "squid", "glow_squid" -> Items.INK_SAC;
            case "pufferfish", "tropical_fish", "salmon", "cod" -> Items.COD;
            case "bee" -> Items.HONEYCOMB;
            case "axolotl" -> Items.AXOLOTL_BUCKET;
            case "goat", "panda", "polar_bear" -> Items.LEATHER;
            case "frog", "tadpole" -> Items.SLIME_BALL;
            case "allay" -> Items.AMETHYST_SHARD;
            case "villager", "wandering_trader" -> Items.EMERALD;
            
            // Special items
            case "iron_golem" -> Items.IRON_INGOT;
            case "snow_golem" -> Items.SNOWBALL;
            
            // YouTuber references
            case "dream_speedrunner" -> Items.ENDER_PEARL;
            case "technoblade_pig" -> Items.GOLDEN_APPLE;
            case "grian_minecraft" -> Items.FEATHER;
            case "mumbo_jumbo" -> Items.REDSTONE;
            
            // Default fallback
            default -> Items.STICK;
        };
    }
    
    private static Formatting getAbilityColor(MobAbility ability) {
        return switch (ability.getActiveAbility()) {
            case FIREBALL -> Formatting.RED;
            case LEAP_ATTACK -> Formatting.GREEN;
            case TELEPORT -> Formatting.DARK_PURPLE;
            case POISON_CLOUD -> Formatting.DARK_GREEN;
            case ICE_SUMMON -> Formatting.AQUA;
            case LIGHTNING_STRIKE -> Formatting.YELLOW;
            case SONIC_BOOM -> Formatting.BLUE;
            case WEB_SHOT -> Formatting.GRAY;
            case THORNS -> Formatting.DARK_RED;
            case HEALING -> Formatting.LIGHT_PURPLE;
            case INVISIBILITY -> Formatting.WHITE;
            case FIRE_RESISTANCE -> Formatting.GOLD;
            case WATER_BREATH -> Formatting.DARK_AQUA;
            case NIGHT_VISION -> Formatting.DARK_BLUE;
            case SPEED_BOOST -> Formatting.YELLOW;
            case KNOCKBACK_WAVE -> Formatting.RED;
            case LIFE_STEAL -> Formatting.DARK_RED;
            case SHIELD_BASH -> Formatting.BLUE;
            case LEVITATION -> Formatting.LIGHT_PURPLE;
            case UNDEAD_HEALING -> Formatting.DARK_GRAY;
            case REGENERATION -> Formatting.RED;
            case STRENGTH_BOOST -> Formatting.RED;
            case RESISTANCE_BOOST -> Formatting.BLUE;
            case FORTUNE -> Formatting.GOLD;
            case LOOTING -> Formatting.AQUA;
            case SATURATION -> Formatting.GREEN;
            case HASTE -> Formatting.YELLOW;
            case MINING_FATIGUE_CURE -> Formatting.GREEN;
            case WITHER_CURE -> Formatting.DARK_PURPLE;
            case BLINDNESS_CURE -> Formatting.WHITE;
            case POISON_CURE -> Formatting.GREEN;
            case NONE -> Formatting.LIGHT_PURPLE;
            default -> Formatting.LIGHT_PURPLE;
        };
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
