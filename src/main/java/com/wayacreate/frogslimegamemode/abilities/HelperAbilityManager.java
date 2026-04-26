package com.wayacreate.frogslimegamemode.abilities;

import com.wayacreate.frogslimegamemode.eating.MobAbility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class HelperAbilityManager {
    
    public static void executeAbility(Entity helper, MobAbility ability, ServerWorld world) {
        switch (ability.getActiveAbility()) {
            case TELEPORT -> executeTeleport(helper, world);
            case FIREBALL -> executeFireball(helper, world);
            case ICE_SUMMON -> executeIceSummon(helper, world);
            case POISON_CLOUD -> executePoisonCloud(helper, world);
            case LIGHTNING_STRIKE -> executeLightningStrike(helper, world);
            case LEAP_ATTACK -> executeLeapAttack(helper, world);
            case SONIC_BOOM -> executeSonicBoom(helper, world);
            case WEB_SHOT -> executeWebShot(helper, world);
            case THORNS -> executeThorns(helper, world);
            case PROJECTILE_SHOT -> executeProjectileShot(helper, world);
            case EXPLOSION -> executeExplosion(helper, world);
            case HEALING -> executeHealing(helper, world);
            case INVISIBILITY -> executeInvisibility(helper, world);
            case FIRE_RESISTANCE -> executeFireResistance(helper, world);
            case WATER_BREATH -> executeWaterBreath(helper, world);
            case NIGHT_VISION -> executeNightVision(helper, world);
            case SPEED_BOOST -> executeSpeedBoost(helper, world);
            case KNOCKBACK_WAVE -> executeKnockbackWave(helper, world);
            case LIFE_STEAL -> executeLifeSteal(helper, world);
            case SHIELD_BASH -> executeShieldBash(helper, world);
            case LEVITATION -> executeLevitation(helper, world);
            case UNDEAD_HEALING -> executeUndeadHealing(helper, world);
            case REGENERATION -> executeRegeneration(helper, world);
            case STRENGTH_BOOST -> executeStrengthBoost(helper, world);
            case RESISTANCE_BOOST -> executeResistanceBoost(helper, world);
            case FORTUNE -> executeFortune(helper, world);
            case LOOTING -> executeLooting(helper, world);
            case SATURATION -> executeSaturation(helper, world);
            case HASTE -> executeHaste(helper, world);
            case MINING_FATIGUE_CURE -> executeMiningFatigueCure(helper, world);
            case WITHER_CURE -> executeWitherCure(helper, world);
            case BLINDNESS_CURE -> executeBlindnessCure(helper, world);
            case POISON_CURE -> executePoisonCure(helper, world);
            case NONE -> {}
        }
    }
    
    private static void executeTeleport(Entity helper, ServerWorld world) {
        Vec3d lookDir = helper.getRotationVector();
        double teleportDistance = 8.0;
        Vec3d newPos = helper.getPos().add(lookDir.multiply(teleportDistance));
        
        helper.teleport(newPos.x, newPos.y, newPos.z);
        
        for (int i = 0; i < 20; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.PORTAL,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
    }
    
    private static void executeFireball(Entity helper, ServerWorld world) {
        Vec3d lookDir = helper.getRotationVector();
        
        for (int i = 0; i < 5; i++) {
            world.spawnParticles(ParticleTypes.FLAME,
                helper.getX() + lookDir.x * i,
                helper.getY() + 1.5,
                helper.getZ() + lookDir.z * i,
                3, 0.1, 0.1, 0.1, 0.02);
        }
        
        world.getOtherEntities(helper, helper.getBoundingBox().expand(10)).forEach(entity -> {
            if (entity.distanceTo(helper) < 10) {
                entity.damage(world.getDamageSources().magic(), 8.0f);
                entity.setFireTicks(60);
            }
        });
    }
    
    private static void executeIceSummon(Entity helper, ServerWorld world) {
        for (int i = 0; i < 20; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 5;
            double offsetZ = (world.random.nextDouble() - 0.5) * 5;
            world.spawnParticles(ParticleTypes.SNOWFLAKE,
                helper.getX() + offsetX,
                helper.getY() + 1,
                helper.getZ() + offsetZ,
                2, 0.0, 0.1, 0.0, 0.01);
        }
        
        world.getOtherEntities(helper, helper.getBoundingBox().expand(5)).forEach(entity -> {
            if (entity instanceof net.minecraft.entity.LivingEntity living) {
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 2));
            }
        });
    }
    
    private static void executePoisonCloud(Entity helper, ServerWorld world) {
        for (int i = 0; i < 30; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 5;
            double offsetY = world.random.nextDouble() * 2;
            double offsetZ = (world.random.nextDouble() - 0.5) * 5;
            world.spawnParticles(ParticleTypes.EFFECT,
                helper.getX() + offsetX,
                helper.getY() + offsetY,
                helper.getZ() + offsetZ,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        world.getOtherEntities(helper, helper.getBoundingBox().expand(5)).forEach(entity -> {
            if (entity instanceof net.minecraft.entity.LivingEntity living) {
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 150, 1));
            }
        });
    }
    
    private static void executeLightningStrike(Entity helper, ServerWorld world) {
        Vec3d lookDir = helper.getRotationVector();
        Vec3d targetPos = helper.getPos().add(lookDir.multiply(15));
        
        world.spawnParticles(ParticleTypes.ELECTRIC_SPARK,
            targetPos.x, targetPos.y + 2, targetPos.z,
            15, 0.5, 0.5, 0.5, 0.1);
        
        net.minecraft.entity.LightningEntity lightning = net.minecraft.entity.EntityType.LIGHTNING_BOLT.create(world);
        if (lightning != null) {
            lightning.setPosition(targetPos);
            world.spawnEntity(lightning);
        }
        
        world.getOtherEntities(helper, helper.getBoundingBox().expand(15)).forEach(entity -> {
            if (entity.distanceTo(helper) < 15) {
                entity.damage(world.getDamageSources().magic(), 12.0f);
            }
        });
    }
    
    private static void executeLeapAttack(Entity helper, ServerWorld world) {
        Vec3d lookDir = helper.getRotationVector();
        Vec3d velocity = lookDir.multiply(1.5).add(0, 0.8, 0);
        
        helper.addVelocity(velocity);
        helper.velocityModified = true;
        
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.CLOUD,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.entity.LivingEntity living) {
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 200, 3));
        }
    }
    
    private static void executeSonicBoom(Entity helper, ServerWorld world) {
        for (int i = 0; i < 40; i++) {
            double angle = (i / 40.0) * Math.PI * 2;
            double offsetX = Math.cos(angle) * 4;
            double offsetZ = Math.sin(angle) * 4;
            world.spawnParticles(ParticleTypes.SONIC_BOOM,
                helper.getX() + offsetX,
                helper.getY() + 1,
                helper.getZ() + offsetZ,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        world.getOtherEntities(helper, helper.getBoundingBox().expand(6)).forEach(entity -> {
            if (entity.distanceTo(helper) < 6) {
                entity.damage(world.getDamageSources().magic(), 10.0f);
                Vec3d knockbackDir = entity.getPos().subtract(helper.getPos()).normalize();
                entity.addVelocity(knockbackDir.x * 2.0, 0.5, knockbackDir.z * 2.0);
                entity.velocityModified = true;
            }
        });
    }
    
    private static void executeWebShot(Entity helper, ServerWorld world) {
        Vec3d lookDir = helper.getRotationVector();
        Vec3d targetPos = helper.getPos().add(lookDir.multiply(12));
        
        world.spawnParticles(ParticleTypes.ITEM_SNOWBALL,
            targetPos.x, targetPos.y + 1, targetPos.z,
            15, 0.3, 0.3, 0.3, 0.02);
        
        world.getOtherEntities(helper, helper.getBoundingBox().expand(12)).forEach(entity -> {
            if (entity.distanceTo(helper) < 12 && entity instanceof net.minecraft.entity.LivingEntity living) {
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 200, 3));
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200, 1));
            }
        });
    }
    
    private static void executeThorns(Entity helper, ServerWorld world) {
        for (int i = 0; i < 20; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.DAMAGE_INDICATOR,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        world.getOtherEntities(helper, helper.getBoundingBox().expand(3)).forEach(entity -> {
            if (entity.distanceTo(helper) < 3) {
                entity.damage(world.getDamageSources().thorns(helper), 6.0f);
            }
        });
        
        if (helper instanceof net.minecraft.entity.LivingEntity living) {
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 200, 1));
        }
    }
    
    private static void executeProjectileShot(Entity helper, ServerWorld world) {
        Vec3d lookDir = helper.getRotationVector();
        Vec3d targetPos = helper.getPos().add(lookDir.multiply(15));
        
        world.spawnParticles(ParticleTypes.SPLASH,
            targetPos.x, targetPos.y + 1, targetPos.z,
            10, 0.2, 0.2, 0.2, 0.01);
        
        world.getOtherEntities(helper, helper.getBoundingBox().expand(15)).forEach(entity -> {
            if (entity.distanceTo(helper) < 15) {
                entity.damage(world.getDamageSources().magic(), 6.0f);
            }
        });
    }
    
    private static void executeExplosion(Entity helper, ServerWorld world) {
        Vec3d lookDir = helper.getRotationVector();
        Vec3d targetPos = helper.getPos().add(lookDir.multiply(8));
        
        world.spawnParticles(ParticleTypes.EXPLOSION_EMITTER,
            targetPos.x, targetPos.y + 1, targetPos.z,
            1, 0.0, 0.0, 0.0, 0.0);
        
        world.getOtherEntities(helper, helper.getBoundingBox().expand(8)).forEach(entity -> {
            if (entity.distanceTo(helper) < 8) {
                entity.damage(world.getDamageSources().magic(), 10.0f);
            }
        });
    }
    
    private static void executeHealing(Entity helper, ServerWorld world) {
        for (int i = 0; i < 20; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.HEART,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.entity.LivingEntity living) {
            living.heal(8.0f);
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1));
        }
    }
    
    private static void executeInvisibility(Entity helper, ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.EFFECT,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.entity.LivingEntity living) {
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 300, 0));
        }
    }
    
    private static void executeFireResistance(Entity helper, ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.FLAME,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.entity.LivingEntity living) {
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 600, 0));
            living.extinguish();
        }
    }
    
    private static void executeWaterBreath(Entity helper, ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.BUBBLE,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.entity.LivingEntity living) {
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 600, 0));
        }
    }
    
    private static void executeNightVision(Entity helper, ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.END_ROD,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.entity.LivingEntity living) {
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 600, 0));
        }
    }
    
    private static void executeSpeedBoost(Entity helper, ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.CLOUD,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.entity.LivingEntity living) {
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 400, 2));
        }
    }
    
    private static void executeKnockbackWave(Entity helper, ServerWorld world) {
        for (int i = 0; i < 30; i++) {
            double angle = (i / 30.0) * Math.PI * 2;
            double offsetX = Math.cos(angle) * 5;
            double offsetZ = Math.sin(angle) * 5;
            world.spawnParticles(ParticleTypes.EXPLOSION,
                helper.getX() + offsetX,
                helper.getY() + 1,
                helper.getZ() + offsetZ,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        world.getOtherEntities(helper, helper.getBoundingBox().expand(8)).forEach(entity -> {
            if (entity.distanceTo(helper) < 8) {
                Vec3d knockbackDir = entity.getPos().subtract(helper.getPos()).normalize();
                entity.addVelocity(knockbackDir.x * 3.0, 0.8, knockbackDir.z * 3.0);
                entity.velocityModified = true;
            }
        });
    }
    
    private static void executeLifeSteal(Entity helper, ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.DAMAGE_INDICATOR,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        world.getOtherEntities(helper, helper.getBoundingBox().expand(5)).forEach(entity -> {
            if (entity.distanceTo(helper) < 5 && entity instanceof net.minecraft.entity.LivingEntity living && helper instanceof net.minecraft.entity.LivingEntity helperLiving) {
                living.damage(world.getDamageSources().magic(), 5.0f);
                helperLiving.heal(3.0f);
            }
        });
    }
    
    private static void executeShieldBash(Entity helper, ServerWorld world) {
        Vec3d lookDir = helper.getRotationVector();
        
        for (int i = 0; i < 20; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.CLOUD,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        Vec3d velocity = lookDir.multiply(2.0);
        helper.addVelocity(velocity);
        helper.velocityModified = true;
        
        world.getOtherEntities(helper, helper.getBoundingBox().expand(3)).forEach(entity -> {
            if (entity.distanceTo(helper) < 3) {
                entity.damage(world.getDamageSources().magic(), 8.0f);
                Vec3d knockbackDir = entity.getPos().subtract(helper.getPos()).normalize();
                entity.addVelocity(knockbackDir.x * 2.5, 0.5, knockbackDir.z * 2.5);
                entity.velocityModified = true;
            }
        });
    }
    
    private static void executeLevitation(Entity helper, ServerWorld world) {
        for (int i = 0; i < 20; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.DRAGON_BREATH,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        helper.addVelocity(0, 1.5, 0);
        helper.velocityModified = true;
        
        if (helper instanceof net.minecraft.entity.LivingEntity living) {
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 200, 0));
        }
    }
    
    private static void executeUndeadHealing(Entity helper, ServerWorld world) {
        for (int i = 0; i < 20; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.DAMAGE_INDICATOR,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.entity.LivingEntity living) {
            living.heal(10.0f);
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 400, 2));
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 400, 1));
        }
    }
    
    private static void executeRegeneration(Entity helper, ServerWorld world) {
        for (int i = 0; i < 25; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.HEART,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.entity.LivingEntity living) {
            living.heal(12.0f);
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 600, 2));
        }
    }
    
    private static void executeStrengthBoost(Entity helper, ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.RAID_OMEN,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.entity.LivingEntity living) {
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 600, 2));
        }
    }
    
    private static void executeResistanceBoost(Entity helper, ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.entity.LivingEntity living) {
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 600, 1));
        }
    }
    
    private static void executeFortune(Entity helper, ServerWorld world) {
        for (int i = 0; i < 10; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.ENCHANT,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.entity.LivingEntity living) {
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 300, 1));
        }
    }
    
    private static void executeLooting(Entity helper, ServerWorld world) {
        for (int i = 0; i < 10; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.ENCHANT,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.entity.LivingEntity living) {
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 300, 2));
        }
    }
    
    private static void executeSaturation(Entity helper, ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.entity.LivingEntity living) {
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 400, 1));
        }
    }
    
    private static void executeHaste(Entity helper, ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.SPLASH,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.entity.LivingEntity living) {
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 600, 2));
        }
    }
    
    private static void executeMiningFatigueCure(Entity helper, ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.MYCELIUM,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.entity.LivingEntity living) {
            living.removeStatusEffect(StatusEffects.MINING_FATIGUE);
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 200, 1));
        }
    }
    
    private static void executeWitherCure(Entity helper, ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.DRIPPING_HONEY,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.entity.LivingEntity living) {
            living.removeStatusEffect(StatusEffects.WITHER);
            living.heal(5.0f);
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1));
        }
    }
    
    private static void executeBlindnessCure(Entity helper, ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.END_ROD,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.entity.LivingEntity living) {
            living.removeStatusEffect(StatusEffects.BLINDNESS);
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 300, 0));
        }
    }
    
    private static void executePoisonCure(Entity helper, ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.HEART,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.entity.LivingEntity living) {
            living.removeStatusEffect(StatusEffects.POISON);
            living.heal(4.0f);
        }
    }
}
