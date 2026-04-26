package com.wayacreate.frogslimegamemode.abilities;

import com.wayacreate.frogslimegamemode.eating.MobAbility;
import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import com.wayacreate.frogslimegamemode.network.ModNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerAbilityManager {
    private static final Map<UUID, Integer> currentAbilityIndex = new HashMap<>();
    private static final Map<UUID, Integer> abilityCooldowns = new HashMap<>();
    
    public static void switchToNextAbility(ServerPlayerEntity player) {
        if (!GamemodeManager.isInGamemode(player)) {
            player.sendMessage(Text.literal("You're not in the gamemode!")
                .formatted(Formatting.RED), true);
            return;
        }
        
        var abilities = GamemodeManager.getData(player).getPlayerAbilities();
        if (abilities.isEmpty()) {
            player.sendMessage(Text.literal("You have no abilities yet!")
                .formatted(Formatting.RED), true);
            return;
        }
        
        UUID uuid = player.getUuid();
        int currentIndex = currentAbilityIndex.getOrDefault(uuid, 0);
        int newIndex = (currentIndex + 1) % abilities.size();
        currentAbilityIndex.put(uuid, newIndex);
        
        MobAbility ability = MobAbility.getAbility(abilities.get(newIndex));
        if (ability != null) {
            player.sendMessage(Text.literal("Switched to: ")
                .formatted(Formatting.GREEN)
                .append(ability.getFormattedName()), true);
            
            ModNetworking.showTitle(player, ability.getName(), ability.getDescription(), Formatting.LIGHT_PURPLE);
        }
    }
    
    public static void useCurrentAbility(ServerPlayerEntity player) {
        if (!GamemodeManager.isInGamemode(player)) {
            player.sendMessage(Text.literal("You're not in the gamemode!")
                .formatted(Formatting.RED), true);
            return;
        }
        
        UUID uuid = player.getUuid();
        if (abilityCooldowns.getOrDefault(uuid, 0) > 0) {
            player.sendMessage(Text.literal("Ability on cooldown: " + abilityCooldowns.get(uuid) + "s")
                .formatted(Formatting.RED), true);
            return;
        }
        
        var abilities = GamemodeManager.getData(player).getPlayerAbilities();
        if (abilities.isEmpty()) {
            player.sendMessage(Text.literal("You have no abilities yet!")
                .formatted(Formatting.RED), true);
            return;
        }
        
        int index = currentAbilityIndex.getOrDefault(uuid, 0);
        MobAbility ability = MobAbility.getAbility(abilities.get(index));
        
        if (ability != null) {
            if (ability.getActiveAbility() == MobAbility.AbilityType.NONE) {
                player.sendMessage(Text.literal("This ability has no active effect!")
                    .formatted(Formatting.YELLOW), true);
                return;
            }
            
            executeAbility(player, ability);
            abilityCooldowns.put(uuid, ability.getAbilityCooldown() / 20); // Convert ticks to seconds
            
            // Send totem animation when using ability
            ModNetworking.sendTotemAnimation(player, 
                "Ability Used!", 
                ability.getName() + " - " + ability.getDescription(), 
                Formatting.LIGHT_PURPLE);
            
            player.sendMessage(Text.literal("Used: ")
                .formatted(Formatting.GREEN)
                .append(ability.getFormattedName()), true);
        }
    }
    
    private static void executeAbility(ServerPlayerEntity player, MobAbility ability) {
        ServerWorld world = (ServerWorld) player.getWorld();
        
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
            case NONE -> {}
        }
    }
    
    private static void executeTeleport(ServerPlayerEntity player, ServerWorld world) {
        Vec3d lookDir = player.getRotationVector();
        double teleportDistance = 8.0;
        Vec3d newPos = player.getPos().add(lookDir.multiply(teleportDistance));
        
        player.teleport(newPos.x, newPos.y, newPos.z);
        
        for (int i = 0; i < 20; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.PORTAL,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
    }
    
    private static void executeFireball(ServerPlayerEntity player, ServerWorld world) {
        Vec3d lookDir = player.getRotationVector();
        
        for (int i = 0; i < 5; i++) {
            world.spawnParticles(ParticleTypes.FLAME,
                player.getX() + lookDir.x * i,
                player.getY() + 1.5,
                player.getZ() + lookDir.z * i,
                3, 0.1, 0.1, 0.1, 0.02);
        }
        
        // Damage entities in front of player
        world.getOtherEntities(player, player.getBoundingBox().expand(10)).forEach(entity -> {
            if (entity.distanceTo(player) < 10) {
                entity.damage(world.getDamageSources().magic(), 8.0f);
                entity.setFireTicks(60);
            }
        });
    }
    
    private static void executeIceSummon(ServerPlayerEntity player, ServerWorld world) {
        for (int i = 0; i < 20; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 5;
            double offsetZ = (world.random.nextDouble() - 0.5) * 5;
            world.spawnParticles(ParticleTypes.SNOWFLAKE,
                player.getX() + offsetX,
                player.getY() + 1,
                player.getZ() + offsetZ,
                2, 0.0, 0.1, 0.0, 0.01);
        }
        
        // Slow nearby entities
        world.getOtherEntities(player, player.getBoundingBox().expand(5)).forEach(entity -> {
            if (entity instanceof net.minecraft.entity.LivingEntity living) {
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 2));
            }
        });
    }
    
    private static void executePoisonCloud(ServerPlayerEntity player, ServerWorld world) {
        for (int i = 0; i < 30; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 5;
            double offsetY = world.random.nextDouble() * 2;
            double offsetZ = (world.random.nextDouble() - 0.5) * 5;
            world.spawnParticles(ParticleTypes.EFFECT,
                player.getX() + offsetX,
                player.getY() + offsetY,
                player.getZ() + offsetZ,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        // Poison nearby entities
        world.getOtherEntities(player, player.getBoundingBox().expand(5)).forEach(entity -> {
            if (entity instanceof net.minecraft.entity.LivingEntity living) {
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 150, 1));
            }
        });
    }
    
    private static void executeLightningStrike(ServerPlayerEntity player, ServerWorld world) {
        Vec3d lookDir = player.getRotationVector();
        Vec3d targetPos = player.getPos().add(lookDir.multiply(15));
        
        world.spawnParticles(ParticleTypes.ELECTRIC_SPARK,
            targetPos.x, targetPos.y + 2, targetPos.z,
            15, 0.5, 0.5, 0.5, 0.1);
        
        // Strike lightning at target position
        net.minecraft.entity.LightningEntity lightning = net.minecraft.entity.EntityType.LIGHTNING_BOLT.create(world);
        if (lightning != null) {
            lightning.setPosition(targetPos);
            world.spawnEntity(lightning);
        }
        
        // Damage entities in area
        world.getOtherEntities(player, player.getBoundingBox().expand(15)).forEach(entity -> {
            if (entity.distanceTo(player) < 15) {
                entity.damage(world.getDamageSources().magic(), 12.0f);
            }
        });
    }
    
    private static void executeLeapAttack(ServerPlayerEntity player, ServerWorld world) {
        Vec3d lookDir = player.getRotationVector();
        Vec3d velocity = lookDir.multiply(1.5).add(0, 0.8, 0);
        
        player.addVelocity(velocity);
        player.velocityModified = true;
        
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.CLOUD,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        // Give jump boost
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 200, 3));
    }
    
    private static void executeSonicBoom(ServerPlayerEntity player, ServerWorld world) {
        for (int i = 0; i < 40; i++) {
            double angle = (i / 40.0) * Math.PI * 2;
            double offsetX = Math.cos(angle) * 4;
            double offsetZ = Math.sin(angle) * 4;
            world.spawnParticles(ParticleTypes.SONIC_BOOM,
                player.getX() + offsetX,
                player.getY() + 1,
                player.getZ() + offsetZ,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        // Knockback and damage nearby entities
        world.getOtherEntities(player, player.getBoundingBox().expand(6)).forEach(entity -> {
            if (entity.distanceTo(player) < 6) {
                entity.damage(world.getDamageSources().magic(), 10.0f);
                Vec3d knockbackDir = entity.getPos().subtract(player.getPos()).normalize();
                entity.addVelocity(knockbackDir.x * 2.0, 0.5, knockbackDir.z * 2.0);
                entity.velocityModified = true;
            }
        });
    }
    
    private static void executeWebShot(ServerPlayerEntity player, ServerWorld world) {
        Vec3d lookDir = player.getRotationVector();
        Vec3d targetPos = player.getPos().add(lookDir.multiply(12));
        
        world.spawnParticles(ParticleTypes.ITEM_SNOWBALL,
            targetPos.x, targetPos.y + 1, targetPos.z,
            15, 0.3, 0.3, 0.3, 0.02);
        
        // Web nearby entities
        world.getOtherEntities(player, player.getBoundingBox().expand(12)).forEach(entity -> {
            if (entity.distanceTo(player) < 12 && entity instanceof net.minecraft.entity.LivingEntity living) {
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 200, 3));
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200, 1));
            }
        });
    }
    
    private static void executeThorns(ServerPlayerEntity player, ServerWorld world) {
        for (int i = 0; i < 20; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.DAMAGE_INDICATOR,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        // Damage attacking entities
        world.getOtherEntities(player, player.getBoundingBox().expand(3)).forEach(entity -> {
            if (entity.distanceTo(player) < 3) {
                entity.damage(world.getDamageSources().thorns(player), 6.0f);
            }
        });
        
        // Give thorns effect
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 200, 1));
    }
    
    private static void executeProjectileShot(ServerPlayerEntity player, ServerWorld world) {
        Vec3d lookDir = player.getRotationVector();
        Vec3d targetPos = player.getPos().add(lookDir.multiply(15));
        
        world.spawnParticles(ParticleTypes.SPLASH,
            targetPos.x, targetPos.y + 1, targetPos.z,
            10, 0.2, 0.2, 0.2, 0.01);
        
        // Damage entities in line of sight
        world.getOtherEntities(player, player.getBoundingBox().expand(15)).forEach(entity -> {
            if (entity.distanceTo(player) < 15) {
                entity.damage(world.getDamageSources().mobAttack(player), 6.0f);
            }
        });
    }
    
    private static void executeExplosion(ServerPlayerEntity player, ServerWorld world) {
        Vec3d lookDir = player.getRotationVector();
        Vec3d targetPos = player.getPos().add(lookDir.multiply(8));
        
        world.spawnParticles(ParticleTypes.EXPLOSION_EMITTER,
            targetPos.x, targetPos.y + 1, targetPos.z,
            1, 0.0, 0.0, 0.0, 0.0);
        
        // Damage entities in area
        world.getOtherEntities(player, player.getBoundingBox().expand(8)).forEach(entity -> {
            if (entity.distanceTo(player) < 8) {
                entity.damage(world.getDamageSources().magic(), 10.0f);
            }
        });
    }
    
    private static void executeHealing(ServerPlayerEntity player, ServerWorld world) {
        for (int i = 0; i < 20; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.HEART,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.heal(8.0f);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1));
    }
    
    private static void executeInvisibility(ServerPlayerEntity player, ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.EFFECT,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 300, 0));
    }
    
    private static void executeFireResistance(ServerPlayerEntity player, ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.FLAME,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 600, 0));
        player.extinguish();
    }
    
    private static void executeWaterBreath(ServerPlayerEntity player, ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.BUBBLE,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 600, 0));
    }
    
    private static void executeNightVision(ServerPlayerEntity player, ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.END_ROD,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 600, 0));
    }
    
    private static void executeSpeedBoost(ServerPlayerEntity player, ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.CLOUD,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 400, 2));
    }
    
    private static void executeKnockbackWave(ServerPlayerEntity player, ServerWorld world) {
        for (int i = 0; i < 30; i++) {
            double angle = (i / 30.0) * Math.PI * 2;
            double offsetX = Math.cos(angle) * 5;
            double offsetZ = Math.sin(angle) * 5;
            world.spawnParticles(ParticleTypes.EXPLOSION,
                player.getX() + offsetX,
                player.getY() + 1,
                player.getZ() + offsetZ,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        // Knockback all nearby entities
        world.getOtherEntities(player, player.getBoundingBox().expand(8)).forEach(entity -> {
            if (entity.distanceTo(player) < 8) {
                Vec3d knockbackDir = entity.getPos().subtract(player.getPos()).normalize();
                entity.addVelocity(knockbackDir.x * 3.0, 0.8, knockbackDir.z * 3.0);
                entity.velocityModified = true;
            }
        });
    }
    
    private static void executeLifeSteal(ServerPlayerEntity player, ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.DAMAGE_INDICATOR,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        // Damage nearby entities and heal player
        world.getOtherEntities(player, player.getBoundingBox().expand(5)).forEach(entity -> {
            if (entity.distanceTo(player) < 5 && entity instanceof net.minecraft.entity.LivingEntity living) {
                living.damage(world.getDamageSources().magic(), 5.0f);
                player.heal(3.0f);
            }
        });
    }
    
    private static void executeShieldBash(ServerPlayerEntity player, ServerWorld world) {
        Vec3d lookDir = player.getRotationVector();
        
        for (int i = 0; i < 20; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.CLOUD,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        // Dash forward and damage
        Vec3d velocity = lookDir.multiply(2.0);
        player.addVelocity(velocity);
        player.velocityModified = true;
        
        world.getOtherEntities(player, player.getBoundingBox().expand(3)).forEach(entity -> {
            if (entity.distanceTo(player) < 3) {
                entity.damage(world.getDamageSources().playerAttack(player), 8.0f);
                Vec3d knockbackDir = entity.getPos().subtract(player.getPos()).normalize();
                entity.addVelocity(knockbackDir.x * 2.5, 0.5, knockbackDir.z * 2.5);
                entity.velocityModified = true;
            }
        });
    }
    
    private static void executeLevitation(ServerPlayerEntity player, ServerWorld world) {
        for (int i = 0; i < 20; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.DRAGON_BREATH,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.addVelocity(0, 1.5, 0);
        player.velocityModified = true;
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 200, 0));
    }
    
    private static void executeUndeadHealing(ServerPlayerEntity player, ServerWorld world) {
        for (int i = 0; i < 20; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.DAMAGE_INDICATOR,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        // Heal player and give regeneration
        player.heal(10.0f);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 400, 2));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 400, 1));
    }
    
    private static void executeRegeneration(ServerPlayerEntity player, ServerWorld world) {
        for (int i = 0; i < 25; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.HEART,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.heal(12.0f);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 600, 2));
    }
    
    private static void executeStrengthBoost(ServerPlayerEntity player, ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.RAID_OMEN,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 600, 2));
    }
    
    private static void executeResistanceBoost(ServerPlayerEntity player, ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 600, 1));
    }
    
    private static void executeFortune(ServerPlayerEntity player, ServerWorld world) {
        for (int i = 0; i < 10; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.ENCHANT,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        // Fortune is passive - give a short luck effect
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 300, 1));
    }
    
    private static void executeLooting(ServerPlayerEntity player, ServerWorld world) {
        for (int i = 0; i < 10; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.ENCHANT,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 300, 2));
    }
    
    private static void executeSaturation(ServerPlayerEntity player, ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 400, 1));
    }
    
    private static void executeHaste(ServerPlayerEntity player, ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.SPLASH,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 600, 2));
    }
    
    private static void executeMiningFatigueCure(ServerPlayerEntity player, ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.MYCELIUM,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.removeStatusEffect(StatusEffects.MINING_FATIGUE);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 200, 1));
    }
    
    private static void executeWitherCure(ServerPlayerEntity player, ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.DRIPPING_HONEY,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.removeStatusEffect(StatusEffects.WITHER);
        player.heal(5.0f);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1));
    }
    
    private static void executeBlindnessCure(ServerPlayerEntity player, ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.END_ROD,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.removeStatusEffect(StatusEffects.BLINDNESS);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 300, 0));
    }
    
    private static void executePoisonCure(ServerPlayerEntity player, ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.HEART,
                player.getX() + px, player.getY() + py, player.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        player.removeStatusEffect(StatusEffects.POISON);
        player.heal(4.0f);
    }
    
    public static void tick() {
        // Decrease cooldowns
        abilityCooldowns.replaceAll((uuid, cooldown) -> Math.max(0, cooldown - 1));
    }
    
    public static MobAbility getCurrentAbility(ServerPlayerEntity player) {
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
