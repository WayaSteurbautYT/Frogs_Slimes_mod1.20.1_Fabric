package com.wayacreate.frogslimegamemode.entity;

import com.wayacreate.frogslimegamemode.achievements.AchievementManager;
import com.wayacreate.frogslimegamemode.gamemode.ManhuntManager;
import com.wayacreate.frogslimegamemode.tasks.TaskManager;
import com.wayacreate.frogslimegamemode.tasks.TaskType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

public class GiantSlimeBossEntity extends Monster {
    private static final EntityDataAccessor<Integer> BOSS_PHASE = SynchedEntityData.registerData(GiantSlimeBossEntity.class, EntityDataSerializers.INTEGER);
    private static final EntityDataAccessor<Boolean> ENRAGED = SynchedEntityData.registerData(GiantSlimeBossEntity.class, EntityDataSerializers.BOOLEAN);
    private int specialAttackCooldown = 0;
    
    public GiantSlimeBossEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
        this.experiencePoints = 500;
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.GENERIC_MAX_HEALTH, 500.0)
            .add(Attributes.GENERIC_ATTACK_DAMAGE, 15.0)
            .add(Attributes.GENERIC_MOVEMENT_SPEED, 0.35)
            .add(Attributes.GENERIC_FOLLOW_RANGE, 64.0)
            .add(Attributes.GENERIC_KNOCKBACK_RESISTANCE, 0.8);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(BOSS_PHASE, 1);
        this.dataTracker.startTracking(ENRAGED, false);
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(4, new LookAtEntityGoal(this, Player.class, 16.0f));
        this.goalSelector.add(5, new LookAroundGoal(this));
        
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, Player.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, FrogHelperEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, SlimeHelperEntity.class, true));
    }
    
    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if (!this.getWorld().isClient) {
            ServerLevel world = (ServerLevel) this.getWorld();
            
            // Spawn death particles
            for (int i = 0; i < 100; i++) {
                double offsetX = (world.random.nextDouble() - 0.5) * 8;
                double offsetY = world.random.nextDouble() * 8;
                double offsetZ = (world.random.nextDouble() - 0.5) * 8;
                
                world.spawnParticles(net.minecraft.core.particles.ParticleTypes.EXPLOSION_EMITTER,
                    this.getX() + offsetX,
                    this.getY() + offsetY,
                    this.getZ() + offsetZ,
                    1, 0, 0, 0, 0);
            }
            
            // Broadcast death message
            if (damageSource.getAttacker() instanceof Player player) {
                TaskManager.completeTask(player, TaskType.DEFEAT_FINAL_BOSS);
                if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    AchievementManager.unlockAchievement(serverPlayer, "boss_killer");
                    if (ManhuntManager.isInGame(serverPlayer) && ManhuntManager.isSpeedrunner(serverPlayer)) {
                        ManhuntManager.onSpeedrunnerWin(serverPlayer);
                    }
                }
                this.getWorld().getPlayers().forEach(p -> 
                    p.sendMessage(Component.literal("The Giant Slime Boss has been defeated by ")
                        .formatted(ChatFormatting.GOLD)
                        .append(Component.literal(player.getName().getString()).formatted(ChatFormatting.YELLOW, ChatFormatting.BOLD))
                        .append(Component.literal("!").formatted(ChatFormatting.GOLD)), false)
                );
            }
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (!this.getWorld().isClient) {
            // Phase transitions based on health
            float healthPercent = this.getHealth() / this.getMaxHealth();
            int currentPhase = getBossPhase();
            
            if (healthPercent < 0.25 && currentPhase < 3) {
                setBossPhase(3);
                setEnraged(true);
                broadcastPhaseChange(3);
            } else if (healthPercent < 0.5 && currentPhase < 2) {
                setBossPhase(2);
                broadcastPhaseChange(2);
            } else if (healthPercent < 0.75 && currentPhase < 1) {
                setBossPhase(1);
                broadcastPhaseChange(1);
            }
            
            // Special attack cooldown
            if (specialAttackCooldown > 0) {
                specialAttackCooldown--;
            }
            
            // Enraged mode effects
            if (isEnraged() && this.age % 20 == 0) {
                ServerLevel world = (ServerLevel) this.getWorld();
                world.spawnParticles(net.minecraft.core.particles.ParticleTypes.LAVA,
                    this.getX(),
                    this.getY() + 2,
                    this.getZ(),
                    5, 0.5, 0.5, 0.5, 0.02);
            }
        }
    }
    
    private void broadcastPhaseChange(int phase) {
        this.getWorld().getPlayers().forEach(p -> {
            p.sendMessage(Component.literal("═══════════════════════════")
                .formatted(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false);
            p.sendMessage(Component.literal("GIANT SLIME BOSS PHASE " + phase)
                .formatted(ChatFormatting.RED, ChatFormatting.BOLD), false);
            p.sendMessage(Component.literal(getPhaseDescription(phase))
                .formatted(ChatFormatting.DARK_RED), false);
            p.sendMessage(Component.literal("═══════════════════════════")
                .formatted(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false);
        });
        
        // Apply phase bonuses
        switch (phase) {
            case 2:
                this.getAttributeInstance(Attributes.GENERIC_ATTACK_DAMAGE).setBaseValue(20.0);
                this.getAttributeInstance(Attributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.4);
                break;
            case 3:
                this.getAttributeInstance(Attributes.GENERIC_ATTACK_DAMAGE).setBaseValue(30.0);
                this.getAttributeInstance(Attributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.5);
                break;
        }
    }
    
    private String getPhaseDescription(int phase) {
        switch (phase) {
            case 1: return "The boss grows more aggressive!";
            case 2: return "The boss enters a frenzy!";
            case 3: return "THE BOSS IS ENRAGED - RUN!";
            default: return "";
        }
    }
    
    public int getBossPhase() {
        return this.dataTracker.get(BOSS_PHASE);
    }
    
    public void setBossPhase(int phase) {
        this.dataTracker.set(BOSS_PHASE, phase);
    }
    
    public boolean isEnraged() {
        return this.dataTracker.get(ENRAGED);
    }
    
    public void setEnraged(boolean enraged) {
        this.dataTracker.set(ENRAGED, enraged);
    }
    
    @Override
    public void writeCustomDataToNbt(CompoundTag nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("BossPhase", getBossPhase());
        nbt.putBoolean("Enraged", isEnraged());
    }
    
    @Override
    public void readCustomDataFromNbt(CompoundTag nbt) {
        super.readCustomDataFromNbt(nbt);
        setBossPhase(nbt.getInt("BossPhase"));
        setEnraged(nbt.getBoolean("Enraged"));
    }
    
    @Override
    public boolean cannotDespawn() {
        return true;
    }
}
