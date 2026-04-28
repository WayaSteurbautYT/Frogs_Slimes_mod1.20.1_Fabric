package com.wayacreate.frogslimegamemode.abilities;

import com.wayacreate.frogslimegamemode.eating.MobAbility;
import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import com.wayacreate.frogslimegamemode.network.ModNetworking;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerAbilityManager {
    private static final Map<UUID, Integer> currentAbilityIndex = new HashMap<>();
    private static final Map<UUID, Integer> abilityCooldowns = new HashMap<>();
    
    public static void switchToNextAbility(ServerPlayer player) {
        if (!GamemodeManager.isInGamemode(player)) {
            player.sendSystemMessage(Component.literal("You're not in the gamemode!")
                .withStyle(ChatFormatting.RED), true);
            return;
        }
        
        var abilities = GamemodeManager.getData(player).getPlayerAbilities();
        
        if (abilities.isEmpty()) {
            player.sendSystemMessage(Component.literal("You have no abilities yet!")
                .withStyle(ChatFormatting.RED), true);
            return;
        }
        
        UUID uuid = player.getUuid();
        int currentIndex = currentAbilityIndex.getOrDefault(uuid, 0);
        int newIndex = (currentIndex + 1) % abilities.size();
        currentAbilityIndex.put(uuid, newIndex);
        
        MobAbility ability = MobAbility.getAbility(abilities.get(newIndex));
        if (ability != null) {
            player.sendSystemMessage(Component.literal("Switched to: ")
                .withStyle(ChatFormatting.GREEN)
                .append(ability.getFormattedName()), true);
            
            ModNetworking.showTitle(player, ability.getName(), ability.getDescription(), ChatFormatting.LIGHT_PURPLE);
        }
    }
    
    public static void useCurrentAbility(ServerPlayer player) {
        if (!GamemodeManager.isInGamemode(player)) {
            player.sendSystemMessage(Component.literal("You're not in the gamemode!")
                .withStyle(ChatFormatting.RED), true);
            return;
        }
        
        UUID uuid = player.getUuid();
        if (abilityCooldowns.getOrDefault(uuid, 0) > 0) {
            player.sendSystemMessage(Component.literal("Ability on cooldown: " + abilityCooldowns.get(uuid) + "s")
                .withStyle(ChatFormatting.RED), true);
            return;
        }
        
        var abilities = GamemodeManager.getData(player).getPlayerAbilities();
        if (abilities.isEmpty()) {
            player.sendSystemMessage(Component.literal("You have no abilities yet!")
                .withStyle(ChatFormatting.RED), true);
            return;
        }
        
        int index = currentAbilityIndex.getOrDefault(uuid, 0);
        MobAbility ability = MobAbility.getAbility(abilities.get(index));
        
        if (ability != null) {
            if (ability.getActiveAbility() == MobAbility.AbilityType.NONE) {
                player.sendSystemMessage(Component.literal("This ability has no active effect!")
                    .withStyle(ChatFormatting.YELLOW), true);
                return;
            }
            
            executeAbility(player, ability);
            abilityCooldowns.put(uuid, ability.getAbilityCooldown() / 20); // Convert ticks to seconds
            
            // Send totem animation when using ability
            ModNetworking.sendTotemAnimation(player, 
                "Ability Used!", 
                ability.getName() + " - " + ability.getDescription(), 
                ChatFormatting.LIGHT_PURPLE);
            
            player.sendSystemMessage(Component.literal("Used: ")
                .withStyle(ChatFormatting.GREEN)
                .append(ability.getFormattedName()), true);
        }
    }
    
    private static void executeAbility(ServerPlayer player, MobAbility ability) {
        ServerLevel world = player.serverLevel();
        
        switch (ability.getActiveAbility()) {
            case TELEPORT -> executeTeleport(player, world);
            case FIREBALL -> executeFireball(player, world);
            case ICE_SUMMON -> executeIceSummon(player, world);
            case POISON_CLOUD -> executePoisonCloud(player, world);
            case LIGHTNING_STRIKE -> executeLightningStrike(player, world);
            case LEAP_ATTACK -> executeLeapAttack(player, world);
            case SONIC_BOOM -> executeSonicBoom(player, world);
            case WEB_SHOT -> executeWebShot(player, world);
            case THORNS -> executeThorns(player, world);
            case PROJECTILE_SHOT -> executeProjectileShot(player, world);
            case EXPLOSION -> executeExplosion(player, world);
            case HEALING -> executeHealing(player, world);
            case INVISIBILITY -> executeInvisibility(player, world);
            case FIRE_RESISTANCE -> executeFireResistance(player, world);
            case WATER_BREATH -> executeWaterBreath(player, world);
            case NIGHT_VISION -> executeNightVision(player, world);
            case SPEED_BOOST -> executeSpeedBoost(player, world);
            case KNOCKBACK_WAVE -> executeKnockbackWave(player, world);
            case LIFE_STEAL -> executeLifeSteal(player, world);
            case SHIELD_BASH -> executeShieldBash(player, world);
            case LEVITATION -> executeLevitation(player, world);
            case UNDEAD_HEALING -> executeUndeadHealing(player, world);
            case REGENERATION -> executeRegeneration(player, world);
            case STRENGTH_BOOST -> executeStrengthBoost(player, world);
            case RESISTANCE_BOOST -> executeResistanceBoost(player, world);
            case FORTUNE -> executeFortune(player, world);
            case LOOTING -> executeLooting(player, world);
            case SATURATION -> executeSaturation(player, world);
            case HASTE -> executeHaste(player, world);
            case MINING_FATIGUE_CURE -> executeMiningFatigueCure(player, world);
            case WITHER_CURE -> executeWitherCure(player, world);
            case BLINDNESS_CURE -> executeBlindnessCure(player, world);
            case POISON_CURE -> executePoisonCure(player, world);
            case TONGUE_GRAB -> executeTongueGrab(player, world);
            case NONE -> {
                // No ability to execute
            }
        }
    }
    
    private static void executeTeleport(ServerPlayer player, ServerLevel world) {
        Vec3 lookDir = player.getLookAngle();
        double teleportDistance = 8.0;
        Vec3 newPos = player.position().add(lookDir.scale(teleportDistance));
        
        player.teleportTo(newPos.x, newPos.y, newPos.z);
        
        for (int i = 0; i < 20; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.PORTAL,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
    }
    
    private static void executeFireball(ServerPlayer player, ServerLevel world) {
        Vec3 lookDir = player.getLookAngle();
        
        for (int i = 0; i < 5; i++) {
            world.sendParticles(ParticleTypes.FLAME,
                player.getX() + lookDir.x * i,
                player.getY() + 1.5,
                player.getZ() + lookDir.z * i,
                3, 0.1, 0.1, 0.1, 0.02);
        }
        
        // Damage entities in front of player
        world.getEntities(player, player.getBoundingBox().inflate(10)).forEach(entity -> {
            if (entity.distanceTo(player) < 10) {
                entity.hurt(world.damageSources().magic(), 8.0f);
                entity.setRemainingFireTicks(60);
            }
        });
    }
    
    private static void executeIceSummon(ServerPlayer player, ServerLevel world) {
        for (int i = 0; i < 20; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 5;
            double offsetZ = (world.random.nextDouble() - 0.5) * 5;
            world.sendParticles(ParticleTypes.SNOWFLAKE,
                player.getX() + offsetX,
                player.getY() + 1,
                player.getZ() + offsetZ,
                2, 0.0, 0.1, 0.0, 0.01);
        }
        
        // Slow nearby entities
        world.getEntities(player, player.getBoundingBox().inflate(5)).forEach(entity -> {
            if (entity instanceof net.minecraft.world.entity.LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
            }
        });
    }
    
    private static void executePoisonCloud(ServerPlayer player, ServerLevel world) {
        for (int i = 0; i < 30; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 5;
            double offsetY = world.random.nextDouble() * 2;
            double offsetZ = (world.random.nextDouble() - 0.5) * 5;
            world.sendParticles(ParticleTypes.EFFECT,
                player.getX() + offsetX,
                player.getY() + offsetY,
                player.getZ() + offsetZ,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        // Poison nearby entities
        world.getEntities(player, player.getBoundingBox().inflate(5)).forEach(entity -> {
            if (entity instanceof net.minecraft.world.entity.LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.POISON, 150, 1));
            }
        });
    }
    
    private static void executeLightningStrike(ServerPlayer player, ServerLevel world) {
        Vec3 lookDir = player.getLookAngle();
        Vec3 targetPos = player.position().add(lookDir.scale(15));
        
        world.sendParticles(ParticleTypes.ELECTRIC_SPARK,
            targetPos.x, targetPos.y + 2, targetPos.z,
            15, 0.5, 0.5, 0.5, 0.1);
        
        // Strike lightning at target position
        net.minecraft.world.entity.LightningBolt lightning = net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create(world);
        if (lightning != null) {
            lightning.setPos(targetPos);
            world.addFreshEntity(lightning);
        }
        
        // Damage entities in area
        world.getEntities(player, player.getBoundingBox().inflate(15)).forEach(entity -> {
            if (entity.distanceTo(player) < 15) {
                entity.hurt(world.damageSources().magic(), 12.0f);
            }
        });
    }
    
    private static void executeLeapAttack(ServerPlayer player, ServerLevel world) {
        Vec3 lookDir = player.getLookAngle();
        Vec3 velocity = lookDir.scale(1.5).add(0, 0.8, 0);
        
        player.push(velocity.x, velocity.y, velocity.z);
        player.hasImpulse = true;
        
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.CLOUD,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        // Give jump boost
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, 200, 3));
    }
    
    private static void executeSonicBoom(ServerPlayer player, ServerLevel world) {
        for (int i = 0; i < 40; i++) {
            double angle = (i / 40.0) * Math.PI * 2;
            double offsetX = Math.cos(angle) * 4;
            double offsetZ = Math.sin(angle) * 4;
            world.sendParticles(ParticleTypes.SONIC_BOOM,
                player.getX() + offsetX,
                player.getY() + 1,
                player.getZ() + offsetZ,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        // Knockback and damage nearby entities
        world.getEntities(player, player.getBoundingBox().inflate(6)).forEach(entity -> {
            if (entity.distanceTo(player) < 6) {
                entity.hurt(world.damageSources().magic(), 10.0f);
                Vec3 knockbackDir = entity.position().subtract(player.position()).normalize();
                entity.push(knockbackDir.x * 2.0, 0.5, knockbackDir.z * 2.0);
                entity.hasImpulse = true;
            }
        });
    }
    
    private static void executeWebShot(ServerPlayer player, ServerLevel world) {
        Vec3 lookDir = player.getLookAngle();
        Vec3 targetPos = player.position().add(lookDir.scale(12));
        
        world.sendParticles(ParticleTypes.ITEM_SNOWBALL,
            targetPos.x, targetPos.y + 1, targetPos.z,
            15, 0.3, 0.3, 0.3, 0.02);
        
        // Web nearby entities
        world.getEntities(player, player.getBoundingBox().inflate(12)).forEach(entity -> {
            if (entity.distanceTo(player) < 12 && entity instanceof net.minecraft.world.entity.LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 3));
                living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1));
            }
        });
    }
    
    private static void executeThorns(ServerPlayer player, ServerLevel world) {
        for (int i = 0; i < 20; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        // Damage attacking entities
        world.getEntities(player, player.getBoundingBox().inflate(3)).forEach(entity -> {
            if (entity.distanceTo(player) < 3) {
                entity.hurt(world.damageSources().thorns(player), 6.0f);
            }
        });
        
        // Give thorns effect
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 1));
    }
    
    private static void executeProjectileShot(ServerPlayer player, ServerLevel world) {
        Vec3 lookDir = player.getLookAngle();
        Vec3 targetPos = player.position().add(lookDir.scale(15));
        
        world.sendParticles(ParticleTypes.SPLASH,
            targetPos.x, targetPos.y + 1, targetPos.z,
            10, 0.2, 0.2, 0.2, 0.01);
        
        // Damage entities in line of sight
        world.getEntities(player, player.getBoundingBox().inflate(15)).forEach(entity -> {
            if (entity.distanceTo(player) < 15) {
                entity.hurt(world.damageSources().mobAttack(player), 6.0f);
            }
        });
    }
    
    private static void executeExplosion(ServerPlayer player, ServerLevel world) {
        Vec3 lookDir = player.getLookAngle();
        Vec3 targetPos = player.position().add(lookDir.scale(8));
        
        world.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
            targetPos.x, targetPos.y + 1, targetPos.z,
            1, 0.0, 0.0, 0.0, 0.0);
        
        // Damage entities in area
        world.getEntities(player, player.getBoundingBox().inflate(8)).forEach(entity -> {
            if (entity.distanceTo(player) < 8) {
                entity.hurt(world.damageSources().magic(), 10.0f);
            }
        });
    }
    
    private static void executeHealing(ServerPlayer player, ServerLevel world) {
        for (int i = 0; i < 20; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.HEART,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.heal(8.0f);
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
    }
    
    private static void executeInvisibility(ServerPlayer player, ServerLevel world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.EFFECT,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 300, 0));
    }
    
    private static void executeFireResistance(ServerPlayer player, ServerLevel world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.FLAME,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 600, 0));
        player.clearFire();
    }
    
    private static void executeWaterBreath(ServerPlayer player, ServerLevel world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.BUBBLE,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 600, 0));
    }
    
    private static void executeNightVision(ServerPlayer player, ServerLevel world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.END_ROD,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 600, 0));
    }
    
    private static void executeSpeedBoost(ServerPlayer player, ServerLevel world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.CLOUD,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 2));
    }
    
    private static void executeKnockbackWave(ServerPlayer player, ServerLevel world) {
        for (int i = 0; i < 30; i++) {
            double angle = (i / 30.0) * Math.PI * 2;
            double offsetX = Math.cos(angle) * 5;
            double offsetZ = Math.sin(angle) * 5;
            world.sendParticles(ParticleTypes.EXPLOSION,
                player.getX() + offsetX,
                player.getY() + 1,
                player.getZ() + offsetZ,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        // Knockback all nearby entities
        world.getEntities(player, player.getBoundingBox().inflate(8)).forEach(entity -> {
            if (entity.distanceTo(player) < 8) {
                Vec3 knockbackDir = entity.position().subtract(player.position()).normalize();
                entity.push(knockbackDir.x * 3.0, 0.8, knockbackDir.z * 3.0);
                entity.hasImpulse = true;
            }
        });
    }
    
    private static void executeLifeSteal(ServerPlayer player, ServerLevel world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        // Damage nearby entities and heal player
        world.getEntities(player, player.getBoundingBox().inflate(5)).forEach(entity -> {
            if (entity.distanceTo(player) < 5 && entity instanceof net.minecraft.world.entity.LivingEntity living) {
                living.hurt(world.damageSources().magic(), 5.0f);
                player.heal(3.0f);
            }
        });
    }
    
    private static void executeShieldBash(ServerPlayer player, ServerLevel world) {
        Vec3 lookDir = player.getLookAngle();
        
        for (int i = 0; i < 20; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.CLOUD,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        // Dash forward and damage
        Vec3 velocity = lookDir.scale(2.0);
        player.push(velocity.x, velocity.y, velocity.z);
        player.hasImpulse = true;
        
        world.getEntities(player, player.getBoundingBox().inflate(3)).forEach(entity -> {
            if (entity.distanceTo(player) < 3) {
                entity.hurt(world.damageSources().playerAttack(player), 8.0f);
                Vec3 knockbackDir = entity.position().subtract(player.position()).normalize();
                entity.push(knockbackDir.x * 2.5, 0.5, knockbackDir.z * 2.5);
                entity.hasImpulse = true;
            }
        });
    }
    
    private static void executeLevitation(ServerPlayer player, ServerLevel world) {
        for (int i = 0; i < 20; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.DRAGON_BREATH,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.push(0, 1.5, 0);
        player.hasImpulse = true;
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200, 0));
    }
    
    private static void executeUndeadHealing(ServerPlayer player, ServerLevel world) {
        for (int i = 0; i < 20; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        // Heal player and give regeneration
        player.heal(10.0f);
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 400, 2));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 400, 1));
    }
    
    private static void executeRegeneration(ServerPlayer player, ServerLevel world) {
        for (int i = 0; i < 25; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.HEART,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.heal(12.0f);
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 2));
    }
    
    private static void executeStrengthBoost(ServerPlayer player, ServerLevel world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.ENCHANT,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 2));
    }
    
    private static void executeResistanceBoost(ServerPlayer player, ServerLevel world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 1));
    }
    
    private static void executeFortune(ServerPlayer player, ServerLevel world) {
        for (int i = 0; i < 10; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.ENCHANT,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        // Fortune is passive - give a short luck effect
        player.addEffect(new MobEffectInstance(MobEffects.LUCK, 300, 1));
    }
    
    private static void executeLooting(ServerPlayer player, ServerLevel world) {
        for (int i = 0; i < 10; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.ENCHANT,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.addEffect(new MobEffectInstance(MobEffects.LUCK, 300, 2));
    }
    
    private static void executeSaturation(ServerPlayer player, ServerLevel world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 400, 1));
    }
    
    private static void executeHaste(ServerPlayer player, ServerLevel world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.SPLASH,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 600, 2));
    }
    
    private static void executeMiningFatigueCure(ServerPlayer player, ServerLevel world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.MYCELIUM,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.removeEffect(MobEffects.DIG_SLOWDOWN);
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 200, 1));
    }
    
    private static void executeWitherCure(ServerPlayer player, ServerLevel world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.DRIPPING_HONEY,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.removeEffect(MobEffects.WITHER);
        player.heal(5.0f);
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
    }
    
    private static void executeBlindnessCure(ServerPlayer player, ServerLevel world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.END_ROD,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.removeEffect(MobEffects.BLINDNESS);
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0));
    }
    
    private static void executePoisonCure(ServerPlayer player, ServerLevel world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.sendParticles(ParticleTypes.HEART,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.removeEffect(MobEffects.POISON);
        player.heal(4.0f);
    }
    
    private static void executeTongueGrab(ServerPlayer player, ServerLevel world) {
        Vec3 lookDir = player.getLookAngle();
        double grabRange = 10.0;
        
        // Find the closest entity in front of the player
        net.minecraft.world.entity.Entity target = null;
        double closestDistance = grabRange;
        
        for (net.minecraft.world.entity.Entity entity : world.getEntities(player, player.getBoundingBox().inflate(grabRange))) {
            if (entity instanceof net.minecraft.world.entity.LivingEntity) {
                Vec3 toEntity = entity.position().subtract(player.position()).normalize();
                double dotProduct = lookDir.dot(toEntity);
                
                // Check if entity is in front of player (within 45 degree cone)
                if (dotProduct > 0.7) {
                    double distance = player.distanceTo(entity);
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        target = entity;
                    }
                }
            }
        }
        
        if (target != null) {
            // Pull target towards player
            Vec3 direction = player.position().subtract(target.position()).normalize();
            double pullStrength = 1.5;
            target.push(direction.x * pullStrength, 0.5, direction.z * pullStrength);
            target.hasImpulse = true;
            
            // Damage target slightly
            if (target instanceof net.minecraft.world.entity.LivingEntity livingTarget) {
                livingTarget.hurt(world.damageSources().mobAttack(player), 2.0f);
            }
            
            // Send tongue animation packet to client
            ModNetworking.sendPlayerTongueAnimation(player, target.getId());
            
            // Spawn particles on server
            for (int i = 0; i < 20; i++) {
                double t = i / 20.0;
                Vec3 particlePos = player.position().add(0, 1.0, 0).add(
                    target.position().subtract(player.position()).scale(t)
                );
                world.sendParticles(ParticleTypes.ITEM_SLIME,
                    particlePos.x, particlePos.y, particlePos.z,
                    1, 0.0, 0.0, 0.0, 0.0);
            }
        } else {
            // No target found, just spawn particles in front
            Vec3 targetPos = player.position().add(lookDir.scale(grabRange));
            for (int i = 0; i < 15; i++) {
                double t = i / 15.0;
                Vec3 particlePos = player.position().add(0, 1.0, 0).add(
                    targetPos.subtract(player.position()).scale(t)
                );
                world.sendParticles(ParticleTypes.ITEM_SLIME,
                    particlePos.x, particlePos.y, particlePos.z,
                    1, 0.0, 0.0, 0.0, 0.0);
            }
        }
    }

    public static void tick() {
        // Decrease cooldowns
        abilityCooldowns.replaceAll((uuid, cooldown) -> Math.max(0, cooldown - 1));
    }

    public static MobAbility getCurrentAbility(ServerPlayer player) {
        var abilities = GamemodeManager.getData(player).getPlayerAbilities();
        if (abilities.isEmpty()) return null;

        int index = currentAbilityIndex.getOrDefault(player.getUuid(), 0);
        return MobAbility.getAbility(abilities.get(index));
    }

    public static void resetPlayer(UUID uuid) {
        currentAbilityIndex.remove(uuid);
        abilityCooldowns.remove(uuid);
    }
}
