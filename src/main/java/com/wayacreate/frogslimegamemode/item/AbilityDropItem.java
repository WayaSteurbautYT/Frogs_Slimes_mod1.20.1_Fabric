package com.wayacreate.frogslimegamemode.item;

import com.wayacreate.frogslimegamemode.eating.MobAbility;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.level.Level;

public class AbilityDropItem extends Item {
    public static final String ABILITY_DROP_NBT = "AbilityDrop";
    public static final String ABILITY_ID_NBT = "AbilityId";
    
    public AbilityDropItem(Properties settings) {
        super(settings);
    }
    
    public static ItemStack createAbilityDrop(String abilityId) {
        MobAbility ability = MobAbility.getAbility(abilityId);
        if (ability == null) {
            return ItemStack.EMPTY;
        }
        
        // Use the specific mob item for this ability
        ItemStack drop = new ItemStack(getDropItemForAbility(abilityId));
        CompoundTag nbt = drop.getOrCreateNbt();
        nbt.putBoolean(ABILITY_DROP_NBT, true);
        nbt.putString(ABILITY_ID_NBT, abilityId);
        
        ChatFormatting color = getAbilityColor(ability);
        drop.setCustomName(Component.literal("Mob Drop: " + ability.getName())
            .formatted(color, ChatFormatting.BOLD));
        
        return drop;
    }
    
    public static net.minecraft.world.item.Item getDropItemForAbility(String abilityId) {
        return switch (abilityId) {
            case "zombie" -> net.minecraft.world.item.Items.ROTTEN_FLESH;
            case "skeleton" -> net.minecraft.world.item.Items.BONE;
            case "spider" -> net.minecraft.world.item.Items.SPIDER_EYE;
            case "creeper" -> net.minecraft.world.item.Items.GUNPOWDER;
            case "enderman" -> net.minecraft.world.item.Items.ENDER_PEARL;
            case "witch" -> net.minecraft.world.item.Items.POTION;
            case "blaze" -> net.minecraft.world.item.Items.BLAZE_ROD;
            case "slime" -> net.minecraft.world.item.Items.SLIME_BALL;
            case "cow", "mooshroom" -> net.minecraft.world.item.Items.LEATHER;
            case "pig" -> net.minecraft.world.item.Items.PORKCHOP;
            case "sheep" -> net.minecraft.world.item.Items.WHITE_WOOL;
            case "chicken" -> net.minecraft.world.item.Items.FEATHER;
            case "rabbit" -> net.minecraft.world.item.Items.RABBIT_FOOT;
            case "guardian" -> net.minecraft.world.item.Items.PRISMARINE_CRYSTALS;
            case "ghast" -> net.minecraft.world.item.Items.GHAST_TEAR;
            case "phantom" -> net.minecraft.world.item.Items.PHANTOM_MEMBRANE;
            case "cave_spider" -> net.minecraft.world.item.Items.STRING;
            case "shulker" -> net.minecraft.world.item.Items.SHULKER_SHELL;
            case "ender_dragon" -> net.minecraft.world.item.Items.DRAGON_EGG;
            case "turtle" -> net.minecraft.world.item.Items.TURTLE_HELMET;
            case "drowned" -> net.minecraft.world.item.Items.TRIDENT;
            case "elder_guardian" -> net.minecraft.world.item.Items.NAUTILUS_SHELL;
            case "horse", "donkey", "mule", "llama", "trader_llama" -> net.minecraft.world.item.Items.LEATHER;
            case "fox", "wolf", "cat", "ocelot" -> net.minecraft.world.item.Items.BONE;
            case "parrot" -> net.minecraft.world.item.Items.MUSIC_DISC_CAT;
            case "dolphin" -> net.minecraft.world.item.Items.COD;
            case "squid", "glow_squid" -> net.minecraft.world.item.Items.INK_SAC;
            case "pufferfish", "tropical_fish", "salmon", "cod" -> net.minecraft.world.item.Items.COD;
            case "bee" -> net.minecraft.world.item.Items.HONEYCOMB;
            case "axolotl" -> net.minecraft.world.item.Items.AXOLOTL_BUCKET;
            case "goat", "panda", "polar_bear" -> net.minecraft.world.item.Items.LEATHER;
            case "frog", "tadpole" -> net.minecraft.world.item.Items.SLIME_BALL;
            case "allay" -> net.minecraft.world.item.Items.AMETHYST_SHARD;
            case "villager", "wandering_trader" -> net.minecraft.world.item.Items.EMERALD;
            case "iron_golem" -> net.minecraft.world.item.Items.IRON_INGOT;
            case "snow_golem" -> net.minecraft.world.item.Items.SNOWBALL;
            case "dream_speedrunner" -> net.minecraft.world.item.Items.ENDER_PEARL;
            case "technoblade_pig" -> net.minecraft.world.item.Items.GOLDEN_APPLE;
            case "grian_minecraft" -> net.minecraft.world.item.Items.FEATHER;
            case "king_kong" -> net.minecraft.world.item.Items.GOLDEN_CARROT;
            case "mumbo_jumbo" -> net.minecraft.world.item.Items.REDSTONE;
            case "derpy" -> net.minecraft.world.item.Items.DIRT;
            case "basalt_giant" -> net.minecraft.world.item.Items.BASALT;
            case "piglin_beast" -> net.minecraft.world.item.Items.GOLD_INGOT;
            case "sculk_crawler" -> net.minecraft.world.item.Items.SCULK;
            case "ancient_guardian" -> net.minecraft.world.item.Items.PRISMARINE_SHARD;
            case "nether_star_born" -> net.minecraft.world.item.Items.NETHER_STAR;
            case "dragon_soul" -> net.minecraft.world.item.Items.DRAGON_EGG;
            case "wither_king" -> net.minecraft.world.item.Items.WITHER_ROSE;
            case "ice_walker" -> net.minecraft.world.item.Items.PACKED_ICE;
            case "jungle_trap" -> net.minecraft.world.item.Items.VINE;
            case "desert_pharaoh" -> net.minecraft.world.item.Items.SAND;
            case "ocean_kraken" -> net.minecraft.world.item.Items.HEART_OF_THE_SEA;
            case "sky_leviathan" -> net.minecraft.world.item.Items.ELYTRA;
            case "mushroom_giant" -> net.minecraft.world.item.Items.RED_MUSHROOM;
            case "hyrule_guardian" -> net.minecraft.world.item.Items.EMERALD;
            case "pokemon_master" -> net.minecraft.world.item.Items.GLOWSTONE;
            case "overwatch_tracer" -> net.minecraft.world.item.Items.CLOCK;
            case "mcu_thor" -> net.minecraft.world.item.Items.LIGHTNING_ROD;
            case "harry_potter" -> net.minecraft.world.item.Items.BLAZE_POWDER;
            case "matrix_ninja" -> net.minecraft.world.item.Items.GREEN_DYE;
            case "terminator" -> net.minecraft.world.item.Items.IRON_BLOCK;
            case "predator" -> net.minecraft.world.item.Items.SPYGLASS;
            case "alien_queen" -> net.minecraft.world.item.Items.FERMENTED_SPIDER_EYE;
            case "godzilla" -> net.minecraft.world.item.Items.TNT;
            case "dovahkiin" -> net.minecraft.world.item.Items.DRAGON_HEAD;
            case "witcher" -> net.minecraft.world.item.Items.NETHERITE_SWORD;
            case "geralt" -> net.minecraft.world.item.Items.DIAMOND_SWORD;
            case "kratos" -> net.minecraft.world.item.Items.NETHERITE_AXE;
            case "master_chief" -> net.minecraft.world.item.Items.SHIELD;
            case "doom_slayer" -> net.minecraft.world.item.Items.CROSSBOW;
            case "link" -> net.minecraft.world.item.Items.TRIDENT;
            case "samus" -> net.minecraft.world.item.Items.FIREWORK_ROCKET;
            case "minecraft_steve" -> net.minecraft.world.item.Items.DIAMOND_PICKAXE;
            case "notch" -> net.minecraft.world.item.Items.APPLE;
            case "herobrine" -> net.minecraft.world.item.Items.COAL;
            case "wither_boss" -> net.minecraft.world.item.Items.WITHER_SKELETON_SKULL;
            default -> net.minecraft.world.item.Items.TOTEM_OF_UNDYING;
        };
    }
    
    public static ChatFormatting getAbilityColor(MobAbility ability) {
        return switch (ability.getActiveAbility()) {
            case FIREBALL -> ChatFormatting.RED;
            case LEAP_ATTACK -> ChatFormatting.GREEN;
            case TELEPORT -> ChatFormatting.DARK_PURPLE;
            case POISON_CLOUD -> ChatFormatting.DARK_GREEN;
            case ICE_SUMMON -> ChatFormatting.AQUA;
            case LIGHTNING_STRIKE -> ChatFormatting.YELLOW;
            case SONIC_BOOM -> ChatFormatting.BLUE;
            case WEB_SHOT -> ChatFormatting.GRAY;
            case THORNS -> ChatFormatting.DARK_RED;
            case HEALING -> ChatFormatting.LIGHT_PURPLE;
            case INVISIBILITY -> ChatFormatting.WHITE;
            case FIRE_RESISTANCE -> ChatFormatting.GOLD;
            case WATER_BREATH -> ChatFormatting.DARK_AQUA;
            case NIGHT_VISION -> ChatFormatting.DARK_BLUE;
            case SPEED_BOOST -> ChatFormatting.YELLOW;
            case KNOCKBACK_WAVE -> ChatFormatting.RED;
            case LIFE_STEAL -> ChatFormatting.DARK_RED;
            case SHIELD_BASH -> ChatFormatting.BLUE;
            case LEVITATION -> ChatFormatting.LIGHT_PURPLE;
            case UNDEAD_HEALING -> ChatFormatting.DARK_GRAY;
            case REGENERATION -> ChatFormatting.RED;
            case STRENGTH_BOOST -> ChatFormatting.RED;
            case RESISTANCE_BOOST -> ChatFormatting.BLUE;
            case FORTUNE -> ChatFormatting.GOLD;
            case LOOTING -> ChatFormatting.AQUA;
            case SATURATION -> ChatFormatting.GREEN;
            case HASTE -> ChatFormatting.YELLOW;
            case MINING_FATIGUE_CURE -> ChatFormatting.GREEN;
            case WITHER_CURE -> ChatFormatting.DARK_PURPLE;
            case BLINDNESS_CURE -> ChatFormatting.WHITE;
            case POISON_CURE -> ChatFormatting.GREEN;
            case NONE -> ChatFormatting.LIGHT_PURPLE;
            default -> ChatFormatting.LIGHT_PURPLE;
        };
    }
    
    public static boolean isAbilityDrop(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        CompoundTag nbt = stack.getNbt();
        return nbt != null && nbt.getBoolean(ABILITY_DROP_NBT);
    }
    
    public static String getAbilityId(ItemStack stack) {
        if (!isAbilityDrop(stack)) return null;
        CompoundTag nbt = stack.getNbt();
        return nbt != null ? nbt.getString(ABILITY_ID_NBT) : null;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getStackInHand(hand);
        
        if (!world.isClient) {
            user.sendMessage(Component.literal("Take this to the Ability Crafting Table to forge the final mob ability item. The anvil plus Ability Stick route still works too.")
                .formatted(ChatFormatting.YELLOW), false);
        }
        
        return InteractionResultHolder.pass(stack);
    }
    
    public static void applyAbilityToPlayerStatic(Player player, MobAbility ability) {
        int duration = 600;
        
        if (ability.getSpeedBonus() > 0) {
            player.addStatusEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.SPEED,
                duration, (int) Math.min(ability.getSpeedBonus() * 2, 2)));
        }
        
        if (ability.getHealthBonus() > 0) {
            player.addStatusEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.ABSORPTION,
                duration, (int) Math.min(ability.getHealthBonus() / 5, 4)));
        }
        
        if (ability.getDamageBonus() > 0) {
            player.addStatusEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.STRENGTH,
                duration, (int) Math.min(ability.getDamageBonus() / 3, 2)));
        }
        
        if (ability.getActiveAbility() == MobAbility.AbilityType.FIREBALL) {
            player.addStatusEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE,
                duration, 0));
        }
        
        if (ability.getActiveAbility() == MobAbility.AbilityType.LEAP_ATTACK) {
            player.addStatusEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.JUMP_BOOST,
                duration, 2));
        }
        
        if (ability.getActiveAbility() == MobAbility.AbilityType.TELEPORT) {
            player.addStatusEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.NIGHT_VISION,
                duration, 0));
        }
    }
}
