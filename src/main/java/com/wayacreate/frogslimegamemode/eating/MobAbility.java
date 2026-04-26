package com.wayacreate.frogslimegamemode.eating;

import net.minecraft.entity.EntityType;
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
    
    public MobAbility(String id, String name, String description, double damageBonus, double speedBonus, double healthBonus, double knockbackResistance) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.damageBonus = damageBonus;
        this.speedBonus = speedBonus;
        this.healthBonus = healthBonus;
        this.knockbackResistance = knockbackResistance;
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getDamageBonus() { return damageBonus; }
    public double getSpeedBonus() { return speedBonus; }
    public double getHealthBonus() { return healthBonus; }
    public double getKnockbackResistance() { return knockbackResistance; }
    
    public Text getFormattedName() {
        return Text.literal(name).formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD);
    }
    
    public Text getFormattedDescription() {
        return Text.literal(description).formatted(Formatting.GRAY);
    }
    
    private static final Map<String, MobAbility> ABILITIES = new HashMap<>();
    
    static {
        // Hostile mobs
        register(new MobAbility("zombie", "Undead Strength", "Strength of the undead", 2.0, 0.0, 5.0, 0.1));
        register(new MobAbility("skeleton", "Sniper Precision", "Precision from beyond", 1.0, 0.05, 0.0, 0.0));
        register(new MobAbility("creeper", "Explosive Power", "Explosive tendencies", 5.0, 0.1, 0.0, 0.0));
        register(new MobAbility("spider", "Wall Climber", "Arachnid agility", 1.5, 0.15, 0.0, 0.0));
        register(new MobAbility("enderman", "Teleportation", "End dimension powers", 3.0, 0.2, 10.0, 0.2));
        register(new MobAbility("witch", "Potion Mastery", "Magical brewing knowledge", 2.0, 0.0, 5.0, 0.1));
        register(new MobAbility("blaze", "Fire Immunity", "Nether fire resistance", 3.0, 0.1, 5.0, 0.1));
        register(new MobAbility("ghast", "Fireball Fury", "Nether explosive power", 4.0, 0.0, 5.0, 0.2));
        register(new MobAbility("piglin", "Gold Greed", "Trading prowess", 1.0, 0.05, 0.0, 0.0));
        register(new MobAbility("warden", "Sonic Power", "Deep dark sensing", 8.0, 0.0, 20.0, 0.5));
        
        // Passive mobs (funny twists)
        register(new MobAbility("pig", "Pork Chop Power", "Delicious strength", 1.0, 0.0, 3.0, 0.0));
        register(new MobAbility("cow", "Mighty Moo", "Dairy durability", 1.0, 0.0, 5.0, 0.1));
        register(new MobAbility("chicken", "Egg-cellent", "Eggstra speed", 0.5, 0.2, 0.0, 0.0));
        register(new MobAbility("sheep", "Wool Shield", "Fluffy protection", 0.5, 0.0, 8.0, 0.3));
        register(new MobAbility("rabbit", "Hop to It", "Bouncy agility", 0.5, 0.25, 0.0, 0.0));
        
        // Special mobs
        register(new MobAbility("iron_golem", "Iron Defense", "Guardian protection", 5.0, 0.0, 30.0, 0.6));
        register(new MobAbility("snow_golem", "Frosty", "Chilling presence", 1.0, 0.0, 3.0, 0.0));
        register(new MobAbility("villager", "Trade Master", "Economic wisdom", 0.5, 0.0, 2.0, 0.0));
        
        // YouTuber references (funny twists)
        register(new MobAbility("dream_speedrunner", "Speed Demon", "Impossible luck", 10.0, 0.5, 10.0, 0.3));
        register(new MobAbility("technoblade_pig", "Piglin King", "Techno never dies", 15.0, 0.3, 50.0, 0.8));
        register(new MobAbility("grian_minecraft", "Prankster", "Building chaos", 3.0, 0.2, 5.0, 0.1));
        register(new MobAbility("mumbo_jumbo", "Redstone Genius", "Engineering prowess", 2.0, 0.1, 8.0, 0.2));
    }
    
    private static void register(MobAbility ability) {
        ABILITIES.put(ability.getId(), ability);
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
