package com.wayacreate.frogslimegamemode.abilities;

import com.wayacreate.frogslimegamemode.eating.MobAbility;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public class HelperAbilityManager {
    
    public static void executeAbility(Entity helper, MobAbility ability, ServerLevel world) {
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
            case TONGUE_GRAB -> executeTongueGrab(helper, world);
            case NONE -> {
                // No ability to execute
            }
        }
    }
    
    private static void executeTeleport(Entity helper, ServerLevel world) {
        Vec3 lookDir = helper.getLookAngle();
        double teleportDistance = 8.0;
        Vec3 newPos = helper.position().add(lookDir.scale(teleportDistance));
        
        helper.teleportTo(newPos.x, newPos.y, newPos.z);
        
        for (int i = 0; i < 20; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.PORTAL,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
    }
    
    private static void executeFireball(Entity helper, ServerLevel world) {
        Vec3 lookDir = helper.getLookAngle();
        
        for (int i = 0; i < 5; i++) {
            world.sendParticles(ParticleTypes.FLAME,
                helper.getX() + lookDir.x * i,
                helper.getY() + 1.5,
                helper.getZ() + lookDir.z * i,
                3, 0.1, 0.1, 0.1, 0.02);
        }
        
        world.getEntities(helper, helper.getBoundingBox().inflate(10)).forEach(entity -> {
            if (entity.distanceTo(helper) < 10) {
                entity.hurt(world.damageSources().magic(), 8.0f);
                entity.setRemainingFireTicks(60);
            }
        });
    }
    
    private static void executeIceSummon(Entity helper, ServerLevel world) {
        for (int i = 0; i < 20; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 5;
            double offsetZ = (world.random.nextDouble() - 0.5) * 5;
            world.sendParticles(ParticleTypes.SNOWFLAKE,
                helper.getX() + offsetX,
                helper.getY() + 1,
                helper.getZ() + offsetZ,
                2, 0.0, 0.1, 0.0, 0.01);
        }
        
        world.getEntities(helper, helper.getBoundingBox().inflate(5)).forEach(entity -> {
            if (entity instanceof net.minecraft.world.entity.LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
            }
        });
    }
    
    private static void executePoisonCloud(Entity helper, ServerLevel world) {
        for (int i = 0; i < 30; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 5;
            double offsetY = world.random.nextDouble() * 2;
            double offsetZ = (world.random.nextDouble() - 0.5) * 5;
            world.sendParticles(ParticleTypes.EFFECT,
                helper.getX() + offsetX,
                helper.getY() + offsetY,
                helper.getZ() + offsetZ,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        world.getEntities(helper, helper.getBoundingBox().inflate(5)).forEach(entity -> {
            if (entity instanceof net.minecraft.world.entity.LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.POISON, 150, 1));
            }
        });
    }
    
    private static void executeLightningStrike(Entity helper, ServerLevel world) {
        Vec3 lookDir = helper.getLookAngle();
        Vec3 targetPos = helper.position().add(lookDir.scale(15));
        
        world.sendParticles(ParticleTypes.ELECTRIC_SPARK,
            targetPos.x, targetPos.y + 2, targetPos.z,
            15, 0.5, 0.5, 0.5, 0.1);
        
        net.minecraft.world.entity.LightningBolt lightning = net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create(world);
        if (lightning != null) {
            lightning.setPos(targetPos);
            world.addFreshEntity(lightning);
        }
        
        world.getEntities(helper, helper.getBoundingBox().inflate(15)).forEach(entity -> {
            if (entity.distanceTo(helper) < 15) {
                entity.hurt(world.damageSources().magic(), 12.0f);
            }
        });
    }
    
    private static void executeLeapAttack(Entity helper, ServerLevel world) {
        Vec3 lookDir = helper.getLookAngle();
        Vec3 velocity = lookDir.scale(1.5).add(0, 0.8, 0);
        
        helper.push(velocity.x, velocity.y, velocity.z);
        helper.hasImpulse = true;
        
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.CLOUD,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.world.entity.LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.JUMP, 200, 3));
        }
    }
    
    private static void executeSonicBoom(Entity helper, ServerLevel world) {
        for (int i = 0; i < 40; i++) {
            double angle = (i / 40.0) * Math.PI * 2;
            double offsetX = Math.cos(angle) * 4;
            double offsetZ = Math.sin(angle) * 4;
            world.sendParticles(ParticleTypes.SONIC_BOOM,
                helper.getX() + offsetX,
                helper.getY() + 1,
                helper.getZ() + offsetZ,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        world.getEntities(helper, helper.getBoundingBox().inflate(6)).forEach(entity -> {
            if (entity.distanceTo(helper) < 6) {
                entity.hurt(world.damageSources().magic(), 10.0f);
                Vec3 knockbackDir = entity.position().subtract(helper.position()).normalize();
                entity.push(knockbackDir.x * 2.0, 0.5, knockbackDir.z * 2.0);
                entity.hasImpulse = true;
            }
        });
    }
    
    private static void executeWebShot(Entity helper, ServerLevel world) {
        Vec3 lookDir = helper.getLookAngle();
        Vec3 targetPos = helper.position().add(lookDir.scale(12));
        
        world.sendParticles(ParticleTypes.ITEM_SNOWBALL,
            targetPos.x, targetPos.y + 1, targetPos.z,
            15, 0.3, 0.3, 0.3, 0.02);
        
        world.getEntities(helper, helper.getBoundingBox().inflate(12)).forEach(entity -> {
            if (entity.distanceTo(helper) < 12 && entity instanceof net.minecraft.world.entity.LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 3));
                living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1));
            }
        });
    }
    
    private static void executeThorns(Entity helper, ServerLevel world) {
        for (int i = 0; i < 20; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        world.getEntities(helper, helper.getBoundingBox().inflate(3)).forEach(entity -> {
            if (entity.distanceTo(helper) < 3) {
                entity.hurt(world.damageSources().thorns(helper), 6.0f);
            }
        });
        
        if (helper instanceof net.minecraft.world.entity.LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 1));
        }
    }
    
    private static void executeProjectileShot(Entity helper, ServerLevel world) {
        Vec3 lookDir = helper.getLookAngle();
        Vec3 targetPos = helper.position().add(lookDir.scale(15));
        
        world.sendParticles(ParticleTypes.SPLASH,
            targetPos.x, targetPos.y + 1, targetPos.z,
            10, 0.2, 0.2, 0.2, 0.01);
        
        world.getEntities(helper, helper.getBoundingBox().inflate(15)).forEach(entity -> {
            if (entity.distanceTo(helper) < 15) {
                entity.hurt(world.damageSources().magic(), 6.0f);
            }
        });
    }
    
    private static void executeExplosion(Entity helper, ServerLevel world) {
        Vec3 lookDir = helper.getLookAngle();
        Vec3 targetPos = helper.position().add(lookDir.scale(8));
        
        world.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
            targetPos.x, targetPos.y + 1, targetPos.z,
            1, 0.0, 0.0, 0.0, 0.0);
        
        world.getEntities(helper, helper.getBoundingBox().inflate(8)).forEach(entity -> {
            if (entity.distanceTo(helper) < 8) {
                entity.hurt(world.damageSources().magic(), 10.0f);
            }
        });
    }
    
    private static void executeHealing(Entity helper, ServerLevel world) {
        for (int i = 0; i < 20; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.HEART,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.world.entity.LivingEntity living) {
            living.heal(8.0f);
            living.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
        }
    }
    
    private static void executeInvisibility(Entity helper, ServerLevel world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.EFFECT,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.world.entity.LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 300, 0));
        }
    }
    
    private static void executeFireResistance(Entity helper, ServerLevel world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.FLAME,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.world.entity.LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 600, 0));
            living.clearFire();
        }
    }
    
    private static void executeWaterBreath(Entity helper, ServerLevel world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.BUBBLE,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.world.entity.LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 600, 0));
        }
    }
    
    private static void executeNightVision(Entity helper, ServerLevel world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.END_ROD,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.world.entity.LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 600, 0));
        }
    }
    
    private static void executeSpeedBoost(Entity helper, ServerLevel world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.CLOUD,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.world.entity.LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 2));
        }
    }
    
    private static void executeKnockbackWave(Entity helper, ServerLevel world) {
        for (int i = 0; i < 30; i++) {
            double angle = (i / 30.0) * Math.PI * 2;
            double offsetX = Math.cos(angle) * 5;
            double offsetZ = Math.sin(angle) * 5;
            world.sendParticles(ParticleTypes.EXPLOSION,
                helper.getX() + offsetX,
                helper.getY() + 1,
                helper.getZ() + offsetZ,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        world.getEntities(helper, helper.getBoundingBox().inflate(8)).forEach(entity -> {
            if (entity.distanceTo(helper) < 8) {
                Vec3 knockbackDir = entity.position().subtract(helper.position()).normalize();
                entity.push(knockbackDir.x * 3.0, 0.8, knockbackDir.z * 3.0);
                entity.hasImpulse = true;
            }
        });
    }
    
    private static void executeLifeSteal(Entity helper, ServerLevel world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        world.getEntities(helper, helper.getBoundingBox().inflate(5)).forEach(entity -> {
            if (entity.distanceTo(helper) < 5 && entity instanceof net.minecraft.world.entity.LivingEntity living && helper instanceof net.minecraft.world.entity.LivingEntity helperLiving) {
                living.hurt(world.damageSources().magic(), 5.0f);
                helperLiving.heal(3.0f);
            }
        });
    }
    
    private static void executeShieldBash(Entity helper, ServerLevel world) {
        Vec3 lookDir = helper.getLookAngle();
        
        for (int i = 0; i < 20; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.CLOUD,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        Vec3 velocity = lookDir.scale(2.0);
        helper.push(velocity.x, velocity.y, velocity.z);
        helper.hasImpulse = true;
        
        world.getEntities(helper, helper.getBoundingBox().inflate(3)).forEach(entity -> {
            if (entity.distanceTo(helper) < 3) {
                entity.hurt(world.damageSources().magic(), 8.0f);
                Vec3 knockbackDir = entity.position().subtract(helper.position()).normalize();
                entity.push(knockbackDir.x * 2.5, 0.5, knockbackDir.z * 2.5);
                entity.hasImpulse = true;
            }
        });
    }
    
    private static void executeLevitation(Entity helper, ServerLevel world) {
        for (int i = 0; i < 20; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.DRAGON_BREATH,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        helper.push(0, 1.5, 0);
        helper.hasImpulse = true;
        
        if (helper instanceof net.minecraft.world.entity.LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200, 0));
        }
    }
    
    private static void executeUndeadHealing(Entity helper, ServerLevel world) {
        for (int i = 0; i < 20; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.world.entity.LivingEntity living) {
            living.heal(10.0f);
            living.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 400, 2));
            living.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 400, 1));
        }
    }
    
    private static void executeRegeneration(Entity helper, ServerLevel world) {
        for (int i = 0; i < 25; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.HEART,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.world.entity.LivingEntity living) {
            living.heal(12.0f);
            living.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 2));
        }
    }
    
    private static void executeStrengthBoost(Entity helper, ServerLevel world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.ENCHANT,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.world.entity.LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 2));
        }
    }
    
    private static void executeResistanceBoost(Entity helper, ServerLevel world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.world.entity.LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 1));
        }
    }
    
    private static void executeFortune(Entity helper, ServerLevel world) {
        for (int i = 0; i < 10; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.ENCHANT,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.world.entity.LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.LUCK, 300, 1));
        }
    }
    
    private static void executeLooting(Entity helper, ServerLevel world) {
        for (int i = 0; i < 10; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.ENCHANT,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.world.entity.LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.LUCK, 300, 2));
        }
    }
    
    private static void executeSaturation(Entity helper, ServerLevel world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.world.entity.LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.SATURATION, 400, 1));
        }
    }
    
    private static void executeHaste(Entity helper, ServerLevel world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.SPLASH,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.world.entity.LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 600, 2));
        }
    }
    
    private static void executeMiningFatigueCure(Entity helper, ServerLevel world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.MYCELIUM,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.world.entity.LivingEntity living) {
            living.removeEffect(MobEffects.DIG_SLOWDOWN);
            living.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 200, 1));
        }
    }
    
    private static void executeWitherCure(Entity helper, ServerLevel world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.DRIPPING_HONEY,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.world.entity.LivingEntity living) {
            living.removeEffect(MobEffects.WITHER);
            living.heal(5.0f);
            living.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
        }
    }
    
    private static void executeBlindnessCure(Entity helper, ServerLevel world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.END_ROD,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.world.entity.LivingEntity living) {
            living.removeEffect(MobEffects.BLINDNESS);
            living.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0));
        }
    }
    
    private static void executePoisonCure(Entity helper, ServerLevel world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.HEART,
                helper.getX() + px, helper.getY() + py, helper.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (helper instanceof net.minecraft.world.entity.LivingEntity living) {
            living.removeEffect(MobEffects.POISON);
            living.heal(4.0f);
        }
    }
    
    private static void executeTongueGrab(Entity helper, ServerLevel world) {
        Vec3 lookDir = helper.getLookAngle();
        double grabRange = 10.0;
        
        // Find the closest entity in front of the helper
        net.minecraft.world.entity.Entity target = null;
        double closestDistance = grabRange;
        
        for (net.minecraft.world.entity.Entity entity : world.getEntities(helper, helper.getBoundingBox().inflate(grabRange))) {
            if (entity instanceof net.minecraft.world.entity.LivingEntity) {
                Vec3 toEntity = entity.position().subtract(helper.position()).normalize();
                double dotProduct = lookDir.dot(toEntity);
                
                // Check if entity is in front of helper (within 45 degree cone)
                if (dotProduct > 0.7) {
                    double distance = helper.distanceTo(entity);
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        target = entity;
                    }
                }
            }
        }
        
        if (target != null) {
            // Pull target towards helper
            Vec3 direction = helper.position().subtract(target.position()).normalize();
            double pullStrength = 1.5;
            target.push(direction.x * pullStrength, 0.5, direction.z * pullStrength);
            target.hasImpulse = true;
            
            // Damage target slightly
            if (target instanceof net.minecraft.world.entity.LivingEntity livingTarget) {
                if (helper instanceof net.minecraft.world.entity.LivingEntity livingHelper) {
                    livingTarget.hurt(world.damageSources().mobAttack(livingHelper), 2.0f);
                } else {
                    livingTarget.hurt(world.damageSources().magic(), 2.0f);
                }
            }
            
            // Spawn tongue particles
            for (int i = 0; i < 20; i++) {
                double t = i / 20.0;
                Vec3 particlePos = helper.position().add(0, 0.5, 0).add(
                    target.position().subtract(helper.position()).scale(t)
                );
                world.sendParticles(ParticleTypes.ITEM_SLIME,
                    particlePos.x, particlePos.y, particlePos.z,
                    1, 0.0, 0.0, 0.0, 0.0);
            }
            
            // Spawn heart particles at target
            world.sendParticles(ParticleTypes.HEART,
                target.getX(), target.getY() + 1, target.getZ(),
                3, 0.0, 0.0, 0.0, 0.0);
        } else {
            // No target found, just spawn particles in front
            Vec3 targetPos = helper.position().add(lookDir.scale(grabRange));
            for (int i = 0; i < 15; i++) {
                double t = i / 15.0;
                Vec3 particlePos = helper.position().add(0, 0.5, 0).add(
                    targetPos.subtract(helper.position()).scale(t)
                );
                world.sendParticles(ParticleTypes.ITEM_SLIME,
                    particlePos.x, particlePos.y, particlePos.z,
                    1, 0.0, 0.0, 0.0, 0.0);
            }
        }
    }
}
