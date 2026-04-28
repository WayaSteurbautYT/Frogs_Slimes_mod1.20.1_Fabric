package com.wayacreate.frogslimegamemode.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.BossEvent;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class FrogKingEntity extends Monster {
    private static final EntityDataAccessor<Integer> ROYAL_GUARDS = SynchedEntityData.registerData(FrogKingEntity.class, EntityDataSerializers.INTEGER);
    private static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.registerData(FrogKingEntity.class, EntityDataSerializers.INTEGER);
    
    private final ServerBossEvent bossBar;
    private int jumpCooldown = 0;
    private int breathCooldown = 0;
    private int roarCooldown = 0;
    
    public FrogKingEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
        this.experiencePoints = 500;
        this.bossBar = (ServerBossEvent) new ServerBossEvent(
            Component.literal("Giant Frog King").formatted(ChatFormatting.GOLD, ChatFormatting.BOLD),
            BossEvent.Color.GREEN,
            BossEvent.Style.PROGRESS
        ).setDarkenSky(true);
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.GENERIC_MAX_HEALTH, 300.0)
            .add(Attributes.GENERIC_ATTACK_DAMAGE, 15.0)
            .add(Attributes.GENERIC_MOVEMENT_SPEED, 0.5)
            .add(Attributes.GENERIC_FOLLOW_RANGE, 64.0)
            .add(Attributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ROYAL_GUARDS, 0);
        this.dataTracker.startTracking(PHASE, 1);
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 2.0, true));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 1.5));
        this.goalSelector.add(4, new LookAtEntityGoal(this, Player.class, 16.0f));
        this.goalSelector.add(5, new LookAroundGoal(this));
        
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, Player.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, SlimeHelperEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, FrogHelperEntity.class, true));
    }
    
    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if (!this.getWorld().isClient) {
            ServerLevel world = (ServerLevel) this.getWorld();
            
            // Spawn death particles
            for (int i = 0; i < 100; i++) {
                double offsetX = (world.random.nextDouble() - 0.5) * 6;
                double offsetY = world.random.nextDouble() * 6;
                double offsetZ = (world.random.nextDouble() - 0.5) * 6;
                
                world.spawnParticles(net.minecraft.core.particles.ParticleTypes.TOTEM_OF_UNDYING,
                    this.getX() + offsetX,
                    this.getY() + offsetY,
                    this.getZ() + offsetZ,
                    1, 0, 0, 0, 0);
            }
            
            // Drop Final Evolution Crystal
            this.dropItem(com.wayacreate.frogslimegamemode.item.ModItems.FINAL_EVOLUTION_CRYSTAL);
            
            // Broadcast death message
            if (damageSource.getAttacker() instanceof Player player) {
                this.getWorld().getPlayers().forEach(p -> 
                    p.sendMessage(Component.literal("THE GIANT FROG KING has been defeated by ")
                        .formatted(ChatFormatting.GOLD)
                        .append(Component.literal(player.getName().getString()).formatted(ChatFormatting.YELLOW, ChatFormatting.BOLD))
                        .append(Component.literal("!").formatted(ChatFormatting.GOLD)), false)
                );
            }
            
            // Remove boss bar
            this.bossBar.getPlayers().forEach(this.bossBar::removePlayer);
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (!this.getWorld().isClient) {
            // Update boss bar
            this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
            
            // Phase transitions
            updatePhase();
            
            // Jump attack cooldown
            if (jumpCooldown > 0) {
                jumpCooldown--;
            }
            
            // Breath attack cooldown
            if (breathCooldown > 0) {
                breathCooldown--;
            }
            
            // Roar cooldown
            if (roarCooldown > 0) {
                roarCooldown--;
            }
            
            // Random attacks based on phase
            if (this.getTarget() != null) {
                int phase = getPhase();
                
                // Jump attack (all phases)
                if (jumpCooldown == 0 && this.random.nextFloat() < 0.03f) {
                    performJumpAttack();
                    jumpCooldown = phase == 3 ? 40 : 60;
                }
                
                // Tongue breath (phase 2+)
                if (breathCooldown == 0 && phase >= 2 && this.random.nextFloat() < 0.02f) {
                    performTongueBreath();
                    breathCooldown = 80;
                }
                
                // Royal roar (phase 3)
                if (roarCooldown == 0 && phase == 3 && this.random.nextFloat() < 0.01f) {
                    performRoyalRoar();
                    roarCooldown = 120;
                }
            }
            
            // Royal crown particles
            if (this.age % 10 == 0) {
                ServerLevel world = (ServerLevel) this.getWorld();
                world.spawnParticles(net.minecraft.core.particles.ParticleTypes.GLOW,
                    this.getX(),
                    this.getY() + 2.0,
                    this.getZ(),
                    5, 0.3, 0.3, 0.3, 0.02);
            }
        }
    }
    
    private void updatePhase() {
        float healthPercent = this.getHealth() / this.getMaxHealth();
        int currentPhase = getPhase();
        
        int newPhase;
        if (healthPercent > 0.66f) {
            newPhase = 1;
        } else if (healthPercent > 0.33f) {
            newPhase = 2;
        } else {
            newPhase = 3;
        }
        
        if (newPhase != currentPhase) {
            setPhase(newPhase);
            this.getWorld().getPlayers().forEach(p -> 
                p.sendMessage(Component.literal("The Giant Frog King enters PHASE " + newPhase + "!")
                    .formatted(ChatFormatting.RED, ChatFormatting.BOLD), true)
            );
        }
    }
    
    private void performTongueBreath() {
        if (this.getTarget() != null) {
            ServerLevel world = (ServerLevel) this.getWorld();
            
            // Shoot tongue projectiles
            for (int i = 0; i < 5; i++) {
                double angle = (i / 5.0) * Math.PI * 2;
                double dx = Math.cos(angle) * 3;
                double dz = Math.sin(angle) * 3;
                
                world.spawnParticles(net.minecraft.core.particles.ParticleTypes.SPLASH,
                    this.getX() + dx,
                    this.getY() + 1.5,
                    this.getZ() + dz,
                    10, 0.2, 0.2, 0.2, 0.05);
                
                // Damage nearby entities
                world.getOtherEntities(this, this.getBoundingBox().expand(6)).forEach(entity -> {
                    if (entity.distanceTo(this) < 6 && entity instanceof net.minecraft.world.entity.LivingEntity living) {
                        living.damage(world.getDamageSources().mobAttack(this), 8.0f);
                        living.addVelocity(0, 0.5, 0);
                    }
                });
            }
        }
    }
    
    private void performRoyalRoar() {
        ServerLevel world = (ServerLevel) this.getWorld();
        
        // Knockback all nearby entities
        world.getOtherEntities(this, this.getBoundingBox().expand(15)).forEach(entity -> {
            if (entity.distanceTo(this) < 15) {
                Vec3 direction = entity.getPos().subtract(this.getPos()).normalize();
                entity.addVelocity(direction.x * 4.0, 1.0, direction.z * 4.0);
                entity.velocityModified = true;
                
                if (entity instanceof net.minecraft.world.entity.LivingEntity living) {
                    living.damage(world.getDamageSources().mobAttack(this), 12.0f);
                }
            }
        });
        
        // Screen shake effect
        world.spawnParticles(net.minecraft.core.particles.ParticleTypes.SONIC_BOOM,
            this.getX(), this.getY() + 1, this.getZ(),
            30, 0.5, 0.5, 0.5, 0.1);
    }
    
    private void performJumpAttack() {
        if (this.getTarget() != null) {
            double dx = this.getTarget().getX() - this.getX();
            double dz = this.getTarget().getZ() - this.getZ();
            double distance = Math.sqrt(dx * dx + dz * dz);
            
            if (distance < 12.0) {
                this.addVelocity(dx / distance * 2.0, 1.2, dz / distance * 2.0);
                
                ServerLevel world = (ServerLevel) this.getWorld();
                world.spawnParticles(net.minecraft.core.particles.ParticleTypes.EXPLOSION,
                    this.getX(), this.getY(), this.getZ(),
                    8, 0.4, 0.4, 0.4, 0.15);
            }
        }
    }
    
    public int getPhase() {
        return this.dataTracker.get(PHASE);
    }
    
    public void setPhase(int phase) {
        this.dataTracker.set(PHASE, phase);
    }
    
    public int getRoyalGuards() {
        return this.dataTracker.get(ROYAL_GUARDS);
    }
    
    public void setRoyalGuards(int guards) {
        this.dataTracker.set(ROYAL_GUARDS, guards);
    }
    
    @Override
    public void writeCustomDataToNbt(CompoundTag nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("RoyalGuards", getRoyalGuards());
    }
    
    @Override
    public void readCustomDataFromNbt(CompoundTag nbt) {
        super.readCustomDataFromNbt(nbt);
        setRoyalGuards(nbt.getInt("RoyalGuards"));
    }
    
    @Override
    public boolean cannotDespawn() {
        return true;
    }
    
    @Override
    public void onStartedTrackingBy(ServerPlayer player) {
        super.onStartedTrackingBy(player);
        this.bossBar.addPlayer(player);
    }
    
    @Override
    public void onStoppedTrackingBy(ServerPlayer player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }
}
