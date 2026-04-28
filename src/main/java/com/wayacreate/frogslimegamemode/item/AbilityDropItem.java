package com.wayacreate.frogslimegamemode.item;

import com.wayacreate.frogslimegamemode.eating.MobAbility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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
        
        // Use the specific mob item for this ability
        ItemStack drop = new ItemStack(getDropItemForAbility(abilityId));
        NbtCompound nbt = drop.getOrCreateNbt();
        nbt.putBoolean(ABILITY_DROP_NBT, true);
        nbt.putString(ABILITY_ID_NBT, abilityId);
        
        Formatting color = getAbilityColor(ability);
        drop.setCustomName(Text.literal("Mob Drop: " + ability.getName())
            .formatted(color, Formatting.BOLD));
        
        return drop;
    }
    
    public static net.minecraft.item.Item getDropItemForAbility(String abilityId) {
        return switch (abilityId) {
            case "zombie" -> net.minecraft.item.Items.ROTTEN_FLESH;
            case "skeleton" -> net.minecraft.item.Items.BONE;
            case "spider" -> net.minecraft.item.Items.SPIDER_EYE;
            case "creeper" -> net.minecraft.item.Items.GUNPOWDER;
            case "enderman" -> net.minecraft.item.Items.ENDER_PEARL;
            case "witch" -> net.minecraft.item.Items.POTION;
            case "blaze" -> net.minecraft.item.Items.BLAZE_ROD;
            case "slime" -> net.minecraft.item.Items.SLIME_BALL;
            case "cow", "mooshroom" -> net.minecraft.item.Items.LEATHER;
            case "pig" -> net.minecraft.item.Items.PORKCHOP;
            case "sheep" -> net.minecraft.item.Items.WHITE_WOOL;
            case "chicken" -> net.minecraft.item.Items.FEATHER;
            case "rabbit" -> net.minecraft.item.Items.RABBIT_FOOT;
            case "guardian" -> net.minecraft.item.Items.PRISMARINE_CRYSTALS;
            case "ghast" -> net.minecraft.item.Items.GHAST_TEAR;
            case "phantom" -> net.minecraft.item.Items.PHANTOM_MEMBRANE;
            case "cave_spider" -> net.minecraft.item.Items.STRING;
            case "shulker" -> net.minecraft.item.Items.SHULKER_SHELL;
            case "ender_dragon" -> net.minecraft.item.Items.DRAGON_EGG;
            case "turtle" -> net.minecraft.item.Items.TURTLE_HELMET;
            case "drowned" -> net.minecraft.item.Items.TRIDENT;
            case "elder_guardian" -> net.minecraft.item.Items.NAUTILUS_SHELL;
            case "horse", "donkey", "mule", "llama", "trader_llama" -> net.minecraft.item.Items.LEATHER;
            case "fox", "wolf", "cat", "ocelot" -> net.minecraft.item.Items.BONE;
            case "parrot" -> net.minecraft.item.Items.FEATHER;
            case "dolphin" -> net.minecraft.item.Items.COD;
            case "squid", "glow_squid" -> net.minecraft.item.Items.INK_SAC;
            case "pufferfish", "tropical_fish", "salmon", "cod" -> net.minecraft.item.Items.COD;
            case "bee" -> net.minecraft.item.Items.HONEYCOMB;
            case "axolotl" -> net.minecraft.item.Items.AXOLOTL_BUCKET;
            case "goat", "panda", "polar_bear" -> net.minecraft.item.Items.LEATHER;
            case "frog", "tadpole" -> net.minecraft.item.Items.SLIME_BALL;
            case "allay" -> net.minecraft.item.Items.AMETHYST_SHARD;
            case "villager", "wandering_trader" -> net.minecraft.item.Items.EMERALD;
            case "iron_golem" -> net.minecraft.item.Items.IRON_INGOT;
            case "snow_golem" -> net.minecraft.item.Items.SNOWBALL;
            case "dream_speedrunner" -> net.minecraft.item.Items.ENDER_PEARL;
            case "technoblade_pig" -> net.minecraft.item.Items.GOLDEN_APPLE;
            case "grian_minecraft" -> net.minecraft.item.Items.FEATHER;
            case "king_kong" -> net.minecraft.item.Items.GOLDEN_CARROT;
            case "mumbo_jumbo" -> net.minecraft.item.Items.REDSTONE;
            case "derpy" -> net.minecraft.item.Items.DIRT;
            case "basalt_giant" -> net.minecraft.item.Items.BASALT;
            case "piglin_beast" -> net.minecraft.item.Items.GOLD_INGOT;
            case "sculk_crawler" -> net.minecraft.item.Items.SCULK;
            case "ancient_guardian" -> net.minecraft.item.Items.PRISMARINE_SHARD;
            case "nether_star_born" -> net.minecraft.item.Items.NETHER_STAR;
            case "dragon_soul" -> net.minecraft.item.Items.DRAGON_EGG;
            case "wither_king" -> net.minecraft.item.Items.WITHER_ROSE;
            case "ice_walker" -> net.minecraft.item.Items.PACKED_ICE;
            case "jungle_trap" -> net.minecraft.item.Items.VINE;
            case "desert_pharaoh" -> net.minecraft.item.Items.SAND;
            case "ocean_kraken" -> net.minecraft.item.Items.HEART_OF_THE_SEA;
            case "sky_leviathan" -> net.minecraft.item.Items.ELYTRA;
            case "mushroom_giant" -> net.minecraft.item.Items.RED_MUSHROOM;
            case "hyrule_guardian" -> net.minecraft.item.Items.EMERALD;
            case "pokemon_master" -> net.minecraft.item.Items.GLOWSTONE;
            case "overwatch_tracer" -> net.minecraft.item.Items.CLOCK;
            case "mcu_thor" -> net.minecraft.item.Items.LIGHTNING_ROD;
            case "harry_potter" -> net.minecraft.item.Items.BLAZE_POWDER;
            case "matrix_ninja" -> net.minecraft.item.Items.GREEN_DYE;
            case "terminator" -> net.minecraft.item.Items.IRON_BLOCK;
            case "predator" -> net.minecraft.item.Items.SPYGLASS;
            case "alien_queen" -> net.minecraft.item.Items.FERMENTED_SPIDER_EYE;
            case "godzilla" -> net.minecraft.item.Items.TNT;
            case "dovahkiin" -> net.minecraft.item.Items.DRAGON_HEAD;
            case "witcher" -> net.minecraft.item.Items.NETHERITE_SWORD;
            case "geralt" -> net.minecraft.item.Items.DIAMOND_SWORD;
            case "kratos" -> net.minecraft.item.Items.NETHERITE_AXE;
            case "master_chief" -> net.minecraft.item.Items.SHIELD;
            case "doom_slayer" -> net.minecraft.item.Items.CROSSBOW;
            case "link" -> net.minecraft.item.Items.TRIDENT;
            case "samus" -> net.minecraft.item.Items.FIREWORK_ROCKET;
            case "minecraft_steve" -> net.minecraft.item.Items.DIAMOND_PICKAXE;
            case "notch" -> net.minecraft.item.Items.APPLE;
            case "herobrine" -> net.minecraft.item.Items.COAL;
            case "wither_boss" -> net.minecraft.item.Items.WITHER_SKELETON_SKULL;
            default -> net.minecraft.item.Items.TOTEM_OF_UNDYING;
        };
    }
    
    public static Formatting getAbilityColor(MobAbility ability) {
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
        
        if (!world.isClient) {
            user.sendMessage(Text.literal("This is an ability drop. Put it in an anvil with the mob's item to create the final ability!")
                .formatted(Formatting.YELLOW), false);
        }
        
        return TypedActionResult.pass(stack);
    }
    
    public static void applyAbilityToPlayerStatic(PlayerEntity player, MobAbility ability) {
        int duration = 600;
        
        if (ability.getSpeedBonus() > 0) {
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.SPEED,
                duration, (int) Math.min(ability.getSpeedBonus() * 2, 2)));
        }
        
        if (ability.getHealthBonus() > 0) {
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.ABSORPTION,
                duration, (int) Math.min(ability.getHealthBonus() / 5, 4)));
        }
        
        if (ability.getDamageBonus() > 0) {
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.STRENGTH,
                duration, (int) Math.min(ability.getDamageBonus() / 3, 2)));
        }
        
        if (ability.getActiveAbility() == MobAbility.AbilityType.FIREBALL) {
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.FIRE_RESISTANCE,
                duration, 0));
        }
        
        if (ability.getActiveAbility() == MobAbility.AbilityType.LEAP_ATTACK) {
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.JUMP_BOOST,
                duration, 2));
        }
        
        if (ability.getActiveAbility() == MobAbility.AbilityType.TELEPORT) {
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.NIGHT_VISION,
                duration, 0));
        }
    }
}
