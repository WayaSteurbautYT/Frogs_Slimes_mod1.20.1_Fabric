package com.wayacreate.frogslimegamemode.eating;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;

public class MobAbility {
    private final String id;
    private final String name;
    private final String description;
    private final double damageBonus;
    private final double speedBonus;
    private final double healthBonus;
    private final double knockbackResistance;
    private final AbilityType activeAbility;
    private final int abilityCooldown;
    
    public enum AbilityType {
        NONE,
        TELEPORT,
        FIREBALL,
        ICE_SUMMON,
        POISON_CLOUD,
        LIGHTNING_STRIKE,
        LEAP_ATTACK,
        SONIC_BOOM,
        WEB_SHOT,
        THORNS,
        PROJECTILE_SHOT,
        EXPLOSION,
        HEALING,
        INVISIBILITY,
        FIRE_RESISTANCE,
        WATER_BREATH,
        NIGHT_VISION,
        SPEED_BOOST,
        KNOCKBACK_WAVE,
        LIFE_STEAL,
        SHIELD_BASH,
        LEVITATION,
        UNDEAD_HEALING,
        REGENERATION,
        STRENGTH_BOOST,
        RESISTANCE_BOOST,
        FORTUNE,
        LOOTING,
        SATURATION,
        HASTE,
        MINING_FATIGUE_CURE,
        WITHER_CURE,
        BLINDNESS_CURE,
        POISON_CURE,
        TONGUE_GRAB
    }
    
    public MobAbility(String id, String name, String description, double damageBonus, double speedBonus, double healthBonus, double knockbackResistance) {
        this(id, name, description, damageBonus, speedBonus, healthBonus, knockbackResistance, AbilityType.NONE, 0);
    }
    
    public MobAbility(String id, String name, String description, double damageBonus, double speedBonus, double healthBonus, double knockbackResistance, AbilityType activeAbility, int abilityCooldown) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.damageBonus = damageBonus;
        this.speedBonus = speedBonus;
        this.healthBonus = healthBonus;
        this.knockbackResistance = knockbackResistance;
        this.activeAbility = activeAbility;
        this.abilityCooldown = abilityCooldown;
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getDamageBonus() { return damageBonus; }
    public double getSpeedBonus() { return speedBonus; }
    public double getHealthBonus() { return healthBonus; }
    public double getKnockbackResistance() { return knockbackResistance; }
    public AbilityType getActiveAbility() { return activeAbility; }
    public int getAbilityCooldown() { return abilityCooldown; }
    
    public Text getFormattedName() {
        return Text.literal(name).formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD);
    }
    
    public Text getFormattedDescription() {
        return Text.literal(description).formatted(Formatting.GRAY);
    }
    
    /**
     * Apply passive ability effects to a player
     */
    public void applyToPlayer(PlayerEntity player) {
        // Apply speed bonus
        if (speedBonus > 0) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 100, (int)(speedBonus * 2), false, false));
        }
        
        // Apply health bonus (absorption)
        if (healthBonus > 0) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, (int)(healthBonus / 5), false, false));
        }
        
        // Apply damage bonus (strength)
        if (damageBonus > 0) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 100, (int)(damageBonus / 3), false, false));
        }
        
        // Apply knockback resistance
        if (knockbackResistance > 0) {
            // Note: Knockback resistance is handled via attribute modification in a full implementation
        }
    }
    
    private static final Map<String, MobAbility> ABILITIES = new HashMap<>();
    
    static {
        // Hostile mobs with active abilities
        register(new MobAbility("zombie", "Undead Strength", "Strength of the undead", 2.0, 0.0, 5.0, 0.1, AbilityType.UNDEAD_HEALING, 200));
        register(new MobAbility("skeleton", "Sniper Precision", "Precision from beyond", 1.0, 0.05, 0.0, 0.0, AbilityType.PROJECTILE_SHOT, 150));
        register(new MobAbility("creeper", "Explosive Power", "Explosive tendencies", 5.0, 0.1, 0.0, 0.0, AbilityType.EXPLOSION, 300));
        register(new MobAbility("spider", "Wall Climber", "Arachnid agility", 1.5, 0.15, 0.0, 0.0, AbilityType.WEB_SHOT, 150));
        register(new MobAbility("cave_spider", "Toxic Bite", "Deadly venom", 2.0, 0.2, 0.0, 0.0, AbilityType.POISON_CURE, 180));
        register(new MobAbility("enderman", "Teleportation", "End dimension powers", 3.0, 0.2, 10.0, 0.2, AbilityType.TELEPORT, 100));
        register(new MobAbility("witch", "Potion Mastery", "Magical brewing knowledge", 2.0, 0.0, 5.0, 0.1, AbilityType.POISON_CURE, 180));
        register(new MobAbility("blaze", "Fire Immunity", "Nether fire resistance", 3.0, 0.1, 5.0, 0.1, AbilityType.FIREBALL, 120));
        register(new MobAbility("ghast", "Fireball Fury", "Nether explosive power", 4.0, 0.0, 5.0, 0.2, AbilityType.FIREBALL, 200));
        register(new MobAbility("piglin", "Gold Greed", "Trading prowess", 1.0, 0.05, 0.0, 0.0, AbilityType.FORTUNE, 100));
        register(new MobAbility("warden", "Sonic Power", "Deep dark sensing", 8.0, 0.0, 20.0, 0.5, AbilityType.SONIC_BOOM, 400));
        register(new MobAbility("phantom", "Nightmare Flight", "Swooping terror", 2.0, 0.3, 0.0, 0.0, AbilityType.LEVITATION, 120));
        register(new MobAbility("drowned", "Depth Dweller", "Underwater strength", 2.5, 0.1, 8.0, 0.1, AbilityType.WATER_BREATH, 150));
        register(new MobAbility("husk", "Desert Hunger", "Thirst for life", 2.0, 0.0, 6.0, 0.1, AbilityType.UNDEAD_HEALING, 180));
        register(new MobAbility("stray", "Frozen Arrows", "Icy precision", 1.5, 0.1, 0.0, 0.0, AbilityType.ICE_SUMMON, 160));
        register(new MobAbility("wither_skeleton", "Wither Power", "Decaying strength", 4.0, 0.1, 10.0, 0.2, AbilityType.WITHER_CURE, 250));
        register(new MobAbility("evoker", "Vex Magic", "Summon spectral minions", 3.0, 0.1, 5.0, 0.1, AbilityType.SONIC_BOOM, 200));
        register(new MobAbility("vindicator", "Axe Fury", "Charging berserker", 4.0, 0.15, 8.0, 0.2, AbilityType.SHIELD_BASH, 180));
        register(new MobAbility("pillager", "Crossbow Mastery", "Ranged precision", 2.0, 0.05, 0.0, 0.0, AbilityType.LOOTING, 140));
        register(new MobAbility("ravager", "Beast Charge", "Devastating ram", 6.0, 0.2, 15.0, 0.4, AbilityType.KNOCKBACK_WAVE, 300));
        register(new MobAbility("vex", "Spectral Flight", "Ghostly aggression", 2.0, 0.4, 0.0, 0.0, AbilityType.TELEPORT, 100));
        register(new MobAbility("guardian", "Laser Beam", "Underwater focus", 2.5, 0.0, 8.0, 0.2, AbilityType.PROJECTILE_SHOT, 170));
        register(new MobAbility("elder_guardian", "Elder Power", "Ancient guardian", 5.0, 0.0, 20.0, 0.4, AbilityType.THORNS, 350));
        register(new MobAbility("shulker", "Shell Defense", "Bullet protection", 3.0, 0.0, 15.0, 0.5, AbilityType.LEVITATION, 200));
        register(new MobAbility("endermite", "End Pest", "Dimensional nuisance", 1.0, 0.3, 0.0, 0.0, AbilityType.TELEPORT, 80));
        register(new MobAbility("zombified_piglin", "Golden Fury", "Nether warrior", 4.0, 0.15, 12.0, 0.2, AbilityType.FIRE_RESISTANCE, 220));
        register(new MobAbility("hoglin", "Beast Rampage", "Nether charging", 5.0, 0.2, 15.0, 0.3, AbilityType.LEAP_ATTACK, 250));
        register(new MobAbility("zoglin", "Undead Beast", "Corrupted fury", 4.5, 0.15, 12.0, 0.3, AbilityType.UNDEAD_HEALING, 230));
        register(new MobAbility("piglin_brute", "Brute Strength", "Raw power", 6.0, 0.1, 18.0, 0.4, AbilityType.STRENGTH_BOOST, 280));
        register(new MobAbility("silverfish", "Swarm Tactics", "Infestation", 1.0, 0.25, 0.0, 0.0, AbilityType.WEB_SHOT, 100));
        register(new MobAbility("magma_cube", "Molten Bounce", "Fiery jumps", 3.0, 0.2, 8.0, 0.2, AbilityType.FIREBALL, 150));
        register(new MobAbility("slime", "Bouncy Blob", "Gooey resilience", 2.0, 0.15, 6.0, 0.2, AbilityType.LEAP_ATTACK, 120));
        
        // Passive mobs (funny twists)
        register(new MobAbility("pig", "Pork Chop Power", "Delicious strength", 1.0, 0.0, 3.0, 0.0, AbilityType.SATURATION, 100));
        register(new MobAbility("cow", "Mighty Moo", "Dairy durability", 1.0, 0.0, 5.0, 0.1, AbilityType.REGENERATION, 150));
        register(new MobAbility("chicken", "Egg-cellent", "Eggstra speed", 0.5, 0.2, 0.0, 0.0, AbilityType.LEAP_ATTACK, 80));
        register(new MobAbility("sheep", "Wool Shield", "Fluffy protection", 0.5, 0.0, 8.0, 0.3, AbilityType.THORNS, 250));
        register(new MobAbility("rabbit", "Hop to It", "Bouncy agility", 0.5, 0.25, 0.0, 0.0, AbilityType.LEAP_ATTACK, 60));
        register(new MobAbility("horse", "Equine Speed", "Gallop power", 2.0, 0.3, 5.0, 0.1, AbilityType.SPEED_BOOST, 120));
        register(new MobAbility("donkey", "Steady Carry", "Pack strength", 1.5, 0.1, 8.0, 0.2, AbilityType.RESISTANCE_BOOST, 100));
        register(new MobAbility("mule", "Hybrid Power", "Best of both", 1.8, 0.15, 7.0, 0.2, AbilityType.RESISTANCE_BOOST, 100));
        register(new MobAbility("llama", "Spit Attack", "Projectile defense", 1.0, 0.1, 6.0, 0.2, AbilityType.PROJECTILE_SHOT, 130));
        register(new MobAbility("trader_llama", "Merchant Guard", "Trading protection", 1.5, 0.1, 8.0, 0.2, AbilityType.FORTUNE, 180));
        register(new MobAbility("fox", "Cunning Speed", "Swift predator", 1.5, 0.3, 2.0, 0.0, AbilityType.SPEED_BOOST, 90));
        register(new MobAbility("wolf", "Pack Hunter", "Feral strength", 2.0, 0.2, 4.0, 0.1, AbilityType.LEAP_ATTACK, 110));
        register(new MobAbility("cat", "Feline Agility", "Graceful speed", 1.0, 0.25, 1.0, 0.0, AbilityType.SPEED_BOOST, 70));
        register(new MobAbility("ocelot", "Wild Sprint", "Jungle speed", 1.2, 0.3, 1.5, 0.0, AbilityType.SPEED_BOOST, 80));
        register(new MobAbility("parrot", "Mimic Dance", "Disco vibes", 0.5, 0.2, 0.0, 0.0, AbilityType.INVISIBILITY, 100));
        register(new MobAbility("turtle", "Shell Defense", "Ancient protection", 0.5, 0.0, 10.0, 0.4, AbilityType.THORNS, 200));
        register(new MobAbility("dolphin", "Ocean Grace", "Swimming speed", 1.5, 0.3, 3.0, 0.0, AbilityType.WATER_BREATH, 100));
        register(new MobAbility("squid", "Ink Cloud", "Escape mechanism", 0.5, 0.2, 0.0, 0.0, AbilityType.BLINDNESS_CURE, 90));
        register(new MobAbility("glow_squid", "Luminous Ink", "Glowing escape", 0.8, 0.25, 2.0, 0.0, AbilityType.BLINDNESS_CURE, 100));
        register(new MobAbility("pufferfish", "Toxic Puff", "Poison defense", 0.3, 0.0, 1.0, 0.1, AbilityType.POISON_CURE, 80));
        register(new MobAbility("tropical_fish", "School Speed", "Swift swimmer", 0.5, 0.3, 1.0, 0.0, AbilityType.SPEED_BOOST, 70));
        register(new MobAbility("salmon", "River Leap", "Jumping power", 0.5, 0.2, 1.0, 0.0, AbilityType.LEAP_ATTACK, 60));
        register(new MobAbility("cod", "Deep Swimmer", "Ocean endurance", 0.5, 0.15, 1.5, 0.0, AbilityType.WATER_BREATH, 70));
        register(new MobAbility("bee", "Stinging Fury", "Flying defender", 0.8, 0.3, 0.5, 0.0, AbilityType.POISON_CURE, 60));
        register(new MobAbility("axolotl", "Regeneration", "Healing power", 1.0, 0.15, 3.0, 0.0, AbilityType.REGENERATION, 120));
        register(new MobAbility("goat", "Head Bash", "Ramming power", 2.0, 0.2, 5.0, 0.2, AbilityType.SHIELD_BASH, 140));
        register(new MobAbility("panda", "Bamboo Strength", "Bear power", 2.5, 0.1, 8.0, 0.2, AbilityType.SATURATION, 160));
        register(new MobAbility("polar_bear", "Arctic Fury", "Frozen strength", 3.0, 0.15, 10.0, 0.3, AbilityType.ICE_SUMMON, 180));
        register(new MobAbility("strider", "Lava Walker", "Nether traversal", 1.5, 0.2, 4.0, 0.1, AbilityType.FIRE_RESISTANCE, 100));
        register(new MobAbility("allay", "Collection Joy", "Item gathering", 0.5, 0.4, 1.0, 0.0, AbilityType.FORTUNE, 80));
        register(new MobAbility("frog", "Tongue Grab", "Quick strikes", 1.0, 0.2, 2.0, 0.0, AbilityType.TONGUE_GRAB, 90));
        register(new MobAbility("tadpole", "Growth Potential", "Future power", 0.3, 0.1, 0.5, 0.0, AbilityType.REGENERATION, 50));
        register(new MobAbility("warden", "Sonic Power", "Deep dark sensing", 8.0, 0.0, 20.0, 0.5, AbilityType.SONIC_BOOM, 400));
        
        // Special mobs
        register(new MobAbility("iron_golem", "Iron Defense", "Guardian protection", 5.0, 0.0, 30.0, 0.6, AbilityType.THORNS, 300));
        register(new MobAbility("snow_golem", "Frosty", "Chilling presence", 1.0, 0.0, 3.0, 0.0, AbilityType.ICE_SUMMON, 150));
        register(new MobAbility("villager", "Trade Master", "Economic wisdom", 0.5, 0.0, 2.0, 0.0, AbilityType.NONE, 0));
        register(new MobAbility("wandering_trader", "Traveling Merchant", "Exotic goods", 0.5, 0.1, 2.0, 0.0, AbilityType.NONE, 0));
        
        // YouTuber references (funny twists)
        register(new MobAbility("dream_speedrunner", "Speed Demon", "Impossible luck", 10.0, 0.5, 10.0, 0.3, AbilityType.TELEPORT, 50));
        register(new MobAbility("technoblade_pig", "Piglin King", "Techno never dies", 15.0, 0.3, 50.0, 0.8, AbilityType.LIGHTNING_STRIKE, 500));
        register(new MobAbility("grian_minecraft", "Prankster", "Building chaos", 3.0, 0.2, 5.0, 0.1, AbilityType.LEAP_ATTACK, 100));
        register(new MobAbility("mumbo_jumbo", "Redstone Genius", "Engineering prowess", 2.0, 0.1, 8.0, 0.2, AbilityType.NONE, 0));
        
        // Special player kill rewards
        register(new MobAbility("wayacreate", "WayaCreate Power", "Creator's blessing - All stats boosted", 5.0, 0.3, 15.0, 0.3, AbilityType.REGENERATION, 200));
        register(new MobAbility("derpy_derp", "Derpy Derp", "Hah you suck! - Really you got the worse ability", -2.0, -0.1, -5.0, 0.0, AbilityType.NONE, 0));
    }
    
    private static void register(MobAbility ability) {
        ABILITIES.put(ability.getId(), ability);
    }
    
    public static MobAbility getAbilityById(String id) {
        return ABILITIES.get(id);
    }
    
    public static MobAbility getAbility(String id) {
        return ABILITIES.get(id);
    }
    
    public static MobAbility getAbilityFromEntity(EntityType<?> entityType) {
        String entityId = EntityType.getId(entityType).getPath();
        return ABILITIES.getOrDefault(entityId, null);
    }
    
    public static Map<String, MobAbility> getAllAbilities() {
        return new HashMap<>(ABILITIES);
    }
}
