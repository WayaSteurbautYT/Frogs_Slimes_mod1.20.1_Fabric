package com.wayacreate.frogslimegamemode.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SlimeEndermanEntity extends EndermanEntity {
    private static final TrackedData<Boolean> IS_SLIME_ENDERMAN = DataTracker.registerData(SlimeEndermanEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> SLIME_SIZE = DataTracker.registerData(SlimeEndermanEntity.class, TrackedDataHandlerRegistry.INTEGER);
    
    private int teleportCooldown = 0;
    private int splitCooldown = 0;
    
    public SlimeEndermanEntity(EntityType<? extends EndermanEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 50;
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(IS_SLIME_ENDERMAN, true);
        this.dataTracker.startTracking(SLIME_SIZE, 2);
    }
    
    public static DefaultAttributeContainer.Builder createAttributes() {
        return EndermanEntity.createEndermanAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.0)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0)
            .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.8);
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.add(3, new TeleportTowardsTargetGoal(this));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 16.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));
        
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, FrogHelperEntity.class, true));
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (!this.getWorld().isClient) {
            // Update cooldowns
            if (teleportCooldown > 0) teleportCooldown--;
            if (splitCooldown > 0) splitCooldown--;
            
            // Slime appearance - green particles instead of purple
            if (this.age % 20 == 0) {
                ServerWorld world = (ServerWorld) this.getWorld();
                for (int i = 0; i < 3; i++) {
                    double offsetX = (world.random.nextDouble() - 0.5) * 1.5;
                    double offsetY = world.random.nextDouble() * 2;
                    double offsetZ = (world.random.nextDouble() - 0.5) * 1.5;
                    
                    world.spawnParticles(net.minecraft.particle.ParticleTypes.ITEM_SNOWBALL,
                        this.getX() + offsetX,
                        this.getY() + offsetY,
                        this.getZ() + offsetZ,
                        1, 0, 0, 0, 0);
                }
            }
            
            // Split into smaller slimes when damaged
            if (this.getHealth() < this.getMaxHealth() * 0.5 && splitCooldown == 0) {
                performSlimeSplit();
                splitCooldown = 200;
            }
        }
    }
    
    private void performSlimeSplit() {
        ServerWorld world = (ServerWorld) this.getWorld();
        
        // Spawn 2 smaller slime endermen
        for (int i = 0; i < 2; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 3;
            double offsetZ = (world.random.nextDouble() - 0.5) * 3;
            
            SlimeEndermanEntity miniSlime = ModEntities.SLIME_ENDERMAN.create(world);
            if (miniSlime != null) {
                miniSlime.setPosition(this.getX() + offsetX, this.getY(), this.getZ() + offsetZ);
                miniSlime.setHealth(10.0f);
                world.spawnEntity(miniSlime);
            }
        }
        
        // Particle effect
        world.spawnParticles(net.minecraft.particle.ParticleTypes.SQUID_INK,
            this.getX(), this.getY(), this.getZ(),
            20, 0.5, 0.5, 0.5, 0.1);
    }
    
    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        
        if (!this.getWorld().isClient) {
            ServerWorld world = (ServerWorld) this.getWorld();
            
            // Spawn slime particles on death
            for (int i = 0; i < 30; i++) {
                double offsetX = (world.random.nextDouble() - 0.5) * 3;
                double offsetY = world.random.nextDouble() * 2;
                double offsetZ = (world.random.nextDouble() - 0.5) * 3;
                
                world.spawnParticles(net.minecraft.particle.ParticleTypes.ITEM_SNOWBALL,
                    this.getX() + offsetX,
                    this.getY() + offsetY,
                    this.getZ() + offsetZ,
                    1, 0, 0, 0, 0);
            }
            
            // Drop ender pearls and slime balls
            this.dropItem(net.minecraft.item.Items.ENDER_PEARL);
            this.dropItem(net.minecraft.item.Items.SLIME_BALL);
        }
    }
    
    @Override
    public boolean damage(DamageSource source, float amount) {
        // Water still hurts (enderman trait)
        if (source.getAttacker() != null && source.getAttacker() instanceof PlayerEntity) {
            // Teleport away when attacked by player
            if (teleportCooldown == 0 && this.random.nextFloat() < 0.3f) {
                teleportRandomly();
                teleportCooldown = 40;
                return false; // Avoid damage
            }
        }
        return super.damage(source, amount);
    }
    
    public void setSlimeSize(int size) {
        this.dataTracker.set(SLIME_SIZE, size);
        this.calculateDimensions();
    }
    
    public int getSlimeSize() {
        return this.dataTracker.get(SLIME_SIZE);
    }
    
    @Override
    public float getScaleFactor() {
        return 1.0f;
    }
    
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("IsSlimeEnderman", isSlimeEnderman());
        nbt.putInt("SlimeSize", getSlimeSize());
    }
    
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        setSlimeEnderman(nbt.getBoolean("IsSlimeEnderman"));
        setSlimeSize(nbt.getInt("SlimeSize"));
    }
    
    public boolean isSlimeEnderman() {
        return this.dataTracker.get(IS_SLIME_ENDERMAN);
    }
    
    public void setSlimeEnderman(boolean isSlimeEnderman) {
        this.dataTracker.set(IS_SLIME_ENDERMAN, isSlimeEnderman);
    }
    
    // Custom goal for teleporting towards target
    static class TeleportTowardsTargetGoal extends Goal {
        private final SlimeEndermanEntity enderman;
        
        public TeleportTowardsTargetGoal(SlimeEndermanEntity enderman) {
            this.enderman = enderman;
        }
        
        @Override
        public boolean canStart() {
            return enderman.getTarget() != null && enderman.teleportCooldown == 0;
        }
        
        @Override
        public void tick() {
            if (enderman.getTarget() != null) {
                double distance = enderman.squaredDistanceTo(enderman.getTarget());
                if (distance > 16.0 && distance < 64.0 && enderman.random.nextFloat() < 0.02f) {
                    // Try to teleport closer to target
                    Vec3d targetPos = enderman.getTarget().getPos();
                    Vec3d teleportPos = new Vec3d(
                        targetPos.x + (enderman.random.nextDouble() - 0.5) * 8,
                        targetPos.y,
                        targetPos.z + (enderman.random.nextDouble() - 0.5) * 8
                    );
                    enderman.teleport(teleportPos.x, teleportPos.y, teleportPos.z);
                    enderman.teleportCooldown = 60;
                }
            }
        }
    }
}
