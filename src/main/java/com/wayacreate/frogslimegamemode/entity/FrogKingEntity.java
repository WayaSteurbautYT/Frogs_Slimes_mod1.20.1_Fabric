package com.wayacreate.frogslimegamemode.entity;

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

public class FrogKingEntity extends HostileEntity {
    private static final TrackedData<Integer> ROYAL_GUARDS = DataTracker.registerData(FrogKingEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private int jumpCooldown = 0;
    
    public FrogKingEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 150;
    }
    
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 80.0)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0)
            .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ROYAL_GUARDS, 0);
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.5, true));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 12.0f));
        this.goalSelector.add(5, new LookAroundGoal(this));
        
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, SlimeHelperEntity.class, true));
    }
    
    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if (!this.getWorld().isClient) {
            ServerWorld world = (ServerWorld) this.getWorld();
            
            // Spawn death particles
            for (int i = 0; i < 50; i++) {
                double offsetX = (world.random.nextDouble() - 0.5) * 4;
                double offsetY = world.random.nextDouble() * 4;
                double offsetZ = (world.random.nextDouble() - 0.5) * 4;
                
                world.spawnParticles(net.minecraft.particle.ParticleTypes.TOTEM_OF_UNDYING,
                    this.getX() + offsetX,
                    this.getY() + offsetY,
                    this.getZ() + offsetZ,
                    1, 0, 0, 0, 0);
            }
            
            // Broadcast death message
            if (damageSource.getAttacker() instanceof PlayerEntity player) {
                this.getWorld().getPlayers().forEach(p -> 
                    p.sendMessage(Text.literal("The Frog King has been dethroned by ")
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
            // Jump attack cooldown
            if (jumpCooldown > 0) {
                jumpCooldown--;
            }
            
            // Random jump attack
            if (jumpCooldown == 0 && this.getTarget() != null && this.random.nextFloat() < 0.02f) {
                performJumpAttack();
                jumpCooldown = 60;
            }
            
            // Royal crown particles
            if (this.age % 20 == 0) {
                ServerWorld world = (ServerWorld) this.getWorld();
                world.spawnParticles(net.minecraft.particle.ParticleTypes.GLOW,
                    this.getX(),
                    this.getY() + 1.5,
                    this.getZ(),
                    3, 0.2, 0.2, 0.2, 0.01);
            }
        }
    }
    
    private void performJumpAttack() {
        if (this.getTarget() != null) {
            double dx = this.getTarget().getX() - this.getX();
            double dz = this.getTarget().getZ() - this.getZ();
            double distance = Math.sqrt(dx * dx + dz * dz);
            
            if (distance < 8.0) {
                this.addVelocity(dx / distance * 1.5, 0.8, dz / distance * 1.5);
                
                ServerWorld world = (ServerWorld) this.getWorld();
                world.spawnParticles(net.minecraft.particle.ParticleTypes.EXPLOSION,
                    this.getX(), this.getY(), this.getZ(),
                    5, 0.3, 0.3, 0.3, 0.1);
            }
        }
    }
    
    public int getRoyalGuards() {
        return this.dataTracker.get(ROYAL_GUARDS);
    }
    
    public void setRoyalGuards(int guards) {
        this.dataTracker.set(ROYAL_GUARDS, guards);
    }
    
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("RoyalGuards", getRoyalGuards());
    }
    
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        setRoyalGuards(nbt.getInt("RoyalGuards"));
    }
    
    @Override
    public boolean cannotDespawn() {
        return true;
    }
}
