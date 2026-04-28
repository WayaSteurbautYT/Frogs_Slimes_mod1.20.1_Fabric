package com.wayacreate.frogslimegamemode.entity;

import com.wayacreate.frogslimegamemode.achievements.AchievementManager;
import com.wayacreate.frogslimegamemode.gamemode.ManhuntManager;
import com.wayacreate.frogslimegamemode.tasks.TaskManager;
import com.wayacreate.frogslimegamemode.tasks.TaskType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class GiantSlimeBossEntity extends HostileEntity {
    private static final TrackedData<Integer> BOSS_PHASE = DataTracker.registerData(GiantSlimeBossEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> ENRAGED = DataTracker.registerData(GiantSlimeBossEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private int specialAttackCooldown = 0;
    
    public GiantSlimeBossEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 500;
    }
    
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 500.0)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 15.0)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 64.0)
            .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.8);
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
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 16.0f));
        this.goalSelector.add(5, new LookAroundGoal(this));
        
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, FrogHelperEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, SlimeHelperEntity.class, true));
    }
    
    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if (!this.getWorld().isClient) {
            ServerWorld world = (ServerWorld) this.getWorld();
            
            // Spawn death particles
            for (int i = 0; i < 100; i++) {
                double offsetX = (world.random.nextDouble() - 0.5) * 8;
                double offsetY = world.random.nextDouble() * 8;
                double offsetZ = (world.random.nextDouble() - 0.5) * 8;
                
                world.spawnParticles(net.minecraft.particle.ParticleTypes.EXPLOSION_EMITTER,
                    this.getX() + offsetX,
                    this.getY() + offsetY,
                    this.getZ() + offsetZ,
                    1, 0, 0, 0, 0);
            }
            
            // Broadcast death message
            if (damageSource.getAttacker() instanceof PlayerEntity player) {
                TaskManager.completeTask(player, TaskType.DEFEAT_FINAL_BOSS);
                if (player instanceof net.minecraft.server.network.ServerPlayerEntity serverPlayer) {
                    AchievementManager.unlockAchievement(serverPlayer, "boss_killer");
                    if (ManhuntManager.isInGame(serverPlayer) && ManhuntManager.isSpeedrunner(serverPlayer)) {
                        ManhuntManager.onSpeedrunnerWin(serverPlayer);
                    }
                }
                this.getWorld().getPlayers().forEach(p -> 
                    p.sendMessage(Text.literal("The Giant Slime Boss has been defeated by ")
                        .formatted(Formatting.GOLD)
                        .append(Text.literal(player.getName().getString()).formatted(Formatting.YELLOW, Formatting.BOLD))
                        .append(Text.literal("!").formatted(Formatting.GOLD)), false)
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
                ServerWorld world = (ServerWorld) this.getWorld();
                world.spawnParticles(net.minecraft.particle.ParticleTypes.LAVA,
                    this.getX(),
                    this.getY() + 2,
                    this.getZ(),
                    5, 0.5, 0.5, 0.5, 0.02);
            }
        }
    }
    
    private void broadcastPhaseChange(int phase) {
        this.getWorld().getPlayers().forEach(p -> {
            p.sendMessage(Text.literal("═══════════════════════════")
                .formatted(Formatting.DARK_RED, Formatting.BOLD), false);
            p.sendMessage(Text.literal("GIANT SLIME BOSS PHASE " + phase)
                .formatted(Formatting.RED, Formatting.BOLD), false);
            p.sendMessage(Text.literal(getPhaseDescription(phase))
                .formatted(Formatting.DARK_RED), false);
            p.sendMessage(Text.literal("═══════════════════════════")
                .formatted(Formatting.DARK_RED, Formatting.BOLD), false);
        });
        
        // Apply phase bonuses
        switch (phase) {
            case 2:
                this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(20.0);
                this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.4);
                break;
            case 3:
                this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(30.0);
                this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.5);
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
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("BossPhase", getBossPhase());
        nbt.putBoolean("Enraged", isEnraged());
    }
    
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        setBossPhase(nbt.getInt("BossPhase"));
        setEnraged(nbt.getBoolean("Enraged"));
    }
    
    @Override
    public boolean cannotDespawn() {
        return true;
    }
}
