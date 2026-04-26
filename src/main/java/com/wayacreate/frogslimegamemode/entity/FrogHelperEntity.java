package com.wayacreate.frogslimegamemode.entity;

import com.wayacreate.frogslimegamemode.eating.MobAbility;
import com.wayacreate.frogslimegamemode.evolution.EvolutionStage;
import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import com.wayacreate.frogslimegamemode.entity.ai.MiningGoal;
import com.wayacreate.frogslimegamemode.entity.ai.LumberjackGoal;
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
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class FrogHelperEntity extends TameableEntity {
    private static final TrackedData<Integer> EVOLUTION_STAGE = DataTracker.registerData(FrogHelperEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> MOBS_KILLED = DataTracker.registerData(FrogHelperEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<String> ROLE = DataTracker.registerData(FrogHelperEntity.class, TrackedDataHandlerRegistry.STRING);
    private int particleCooldown = 0;
    private final List<String> abilities = new ArrayList<>();
    private String lastRole = "";
    
    public FrogHelperEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        this.setTamed(false);
    }
    
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(EVOLUTION_STAGE, 0);
        this.dataTracker.startTracking(MOBS_KILLED, 0);
        this.dataTracker.startTracking(ROLE, "");
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new SitGoal(this));
        this.goalSelector.add(3, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.add(4, new FollowOwnerGoal(this, 1.0, 10.0f, 2.0f, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(7, new LookAroundGoal(this));
        
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, HostileEntity.class, true));
    }
    
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        
        if (!this.getWorld().isClient) {
            if (this.isOwner(player)) {
                if (stack.isEmpty() && player.isSneaking()) {
                    player.sendMessage(Text.literal("Frog Helper Stats:")
                        .formatted(Formatting.GREEN, Formatting.BOLD), false);
                    player.sendMessage(Text.literal("Evolution: " + EvolutionStage.fromLevel(getEvolutionStage()).getName())
                        .formatted(Formatting.YELLOW), false);
                    player.sendMessage(Text.literal("Mobs Killed: " + getMobsKilled())
                        .formatted(Formatting.AQUA), false);
                    player.sendMessage(Text.literal("Attack Damage: " + getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE))
                        .formatted(Formatting.RED), false);
                    return ActionResult.SUCCESS;
                } else if (!stack.isEmpty()) {
                    spawnInteractParticles();
                }
            } else if (GamemodeManager.isInGamemode(player) && stack.isEmpty()) {
                this.setOwner(player);
                this.setTamed(true);
                GamemodeManager.getData(player).incrementHelpers();
                GamemodeManager.grantAdvancement((net.minecraft.server.network.ServerPlayerEntity) player, "frogslimegamemode:tame_frog");
                player.sendMessage(Text.literal("Frog helper joined your team!")
                    .formatted(Formatting.GREEN), false);
                spawnInteractParticles();
                return ActionResult.SUCCESS;
            }
        }
        
        return super.interactMob(player, hand);
    }
    
    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if (!this.getWorld().isClient && this.getOwner() instanceof PlayerEntity owner) {
            owner.sendMessage(Text.literal("Your frog helper has fallen!")
                .formatted(Formatting.RED), false);
        }
    }
    
    public void onKilledMob() {
        int killed = getMobsKilled() + 1;
        setMobsKilled(killed);
        
        if (killed >= 10 && getEvolutionStage() == 0) {
            evolve();
        } else if (killed >= 25 && getEvolutionStage() == 1) {
            evolve();
        } else if (killed >= 50 && getEvolutionStage() == 2) {
            evolve();
        }
        
        if (this.getOwner() instanceof PlayerEntity owner) {
            GamemodeManager.getData(owner).incrementMobsEaten();
        }
    }
    
    public void evolve() {
        int newStage = getEvolutionStage() + 1;
        if (newStage <= 3) {
            setEvolutionStage(newStage);
            
            double healthBonus = 10.0 * newStage;
            double damageBonus = 2.0 * newStage;
            
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(20.0 + healthBonus);
            this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(4.0 + damageBonus);
            this.setHealth(this.getMaxHealth());
            
            applyAbilityBonuses();
            
            spawnEvolutionParticles();
            updateCustomName();
            
            if (this.getOwner() instanceof PlayerEntity owner) {
                owner.sendMessage(Text.literal("Your frog helper evolved to ")
                    .formatted(Formatting.GOLD)
                    .append(Text.literal(EvolutionStage.fromLevel(newStage).getName())
                        .formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD))
                    .append(Text.literal("!").formatted(Formatting.GOLD)), false);
            }
        }
    }
    
    public void addAbility(MobAbility ability) {
        if (!abilities.contains(ability.getId())) {
            abilities.add(ability.getId());
            applyAbilityBonuses();
        }
    }
    
    private void applyAbilityBonuses() {
        double totalDamageBonus = 0;
        double totalSpeedBonus = 0;
        double totalHealthBonus = 0;
        double totalKnockbackBonus = 0;
        
        for (String abilityId : abilities) {
            MobAbility ability = MobAbility.getAbility(abilityId);
            if (ability != null) {
                totalDamageBonus += ability.getDamageBonus();
                totalSpeedBonus += ability.getSpeedBonus();
                totalHealthBonus += ability.getHealthBonus();
                totalKnockbackBonus += ability.getKnockbackResistance();
            }
        }
        
        // Apply bonuses on top of base stats
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(4.0 + totalDamageBonus);
        this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3 + totalSpeedBonus);
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(20.0 + totalHealthBonus);
        this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(totalKnockbackBonus);
    }
    
    public List<String> getAbilities() {
        return new ArrayList<>(abilities);
    }
    
    public int getEvolutionStage() {
        return this.dataTracker.get(EVOLUTION_STAGE);
    }
    
    public void setEvolutionStage(int stage) {
        this.dataTracker.set(EVOLUTION_STAGE, stage);
    }
    
    public int getMobsKilled() {
        return this.dataTracker.get(MOBS_KILLED);
    }
    
    public void setMobsKilled(int amount) {
        this.dataTracker.set(MOBS_KILLED, amount);
    }
    
    public String getRole() {
        return this.dataTracker.get(ROLE);
    }
    
    public void setRole(String role) {
        this.dataTracker.set(ROLE, role);
        updateCustomName();
    }
    
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("EvolutionStage", getEvolutionStage());
        nbt.putInt("MobsKilled", getMobsKilled());
        nbt.putString("Role", getRole());
        
        // Save abilities
        nbt.putInt("AbilityCount", abilities.size());
        for (int i = 0; i < abilities.size(); i++) {
            nbt.putString("Ability_" + i, abilities.get(i));
        }
    }
    
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        setEvolutionStage(nbt.getInt("EvolutionStage"));
        setMobsKilled(nbt.getInt("MobsKilled"));
        setRole(nbt.getString("Role"));
        
        // Load abilities
        abilities.clear();
        int abilityCount = nbt.getInt("AbilityCount");
        for (int i = 0; i < abilityCount; i++) {
            String abilityId = nbt.getString("Ability_" + i);
            abilities.add(abilityId);
        }
        
        applyAbilityBonuses();
        updateCustomName();
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Add role-based goals dynamically (only when role changes to prevent memory leak)
        String role = getRole();
        if (!role.equals(lastRole)) {
            lastRole = role;
            if (!role.isEmpty()) {
                if (role.equals("Miner")) {
                    this.goalSelector.add(8, new MiningGoal(this));
                } else if (role.equals("Lumberjack")) {
                    this.goalSelector.add(8, new LumberjackGoal(this));
                } else if (role.equals("Combat Specialist")) {
                    // Combat role gets enhanced attack damage
                    this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(8.0);
                }
            }
        }
        
        if (!this.getWorld().isClient && particleCooldown > 0) {
            particleCooldown--;
        }
        
        if (!this.getWorld().isClient && this.age % 40 == 0) {
            spawnIdleParticles();
        }
    }
    
    private void spawnInteractParticles() {
        if (!this.getWorld().isClient) {
            ServerWorld world = (ServerWorld) this.getWorld();
            for (int i = 0; i < 10; i++) {
                double offsetX = (world.random.nextDouble() - 0.5) * 1.5;
                double offsetY = world.random.nextDouble() * 1.5;
                double offsetZ = (world.random.nextDouble() - 0.5) * 1.5;
                
                world.spawnParticles(net.minecraft.particle.ParticleTypes.HEART,
                    this.getX() + offsetX,
                    this.getY() + offsetY,
                    this.getZ() + offsetZ,
                    1, 0, 0, 0, 0);
            }
        }
    }
    
    private void spawnEvolutionParticles() {
        if (!this.getWorld().isClient) {
            ServerWorld world = (ServerWorld) this.getWorld();
            for (int i = 0; i < 30; i++) {
                double offsetX = (world.random.nextDouble() - 0.5) * 2;
                double offsetY = world.random.nextDouble() * 2;
                double offsetZ = (world.random.nextDouble() - 0.5) * 2;
                
                world.spawnParticles(net.minecraft.particle.ParticleTypes.TOTEM_OF_UNDYING,
                    this.getX() + offsetX,
                    this.getY() + offsetY,
                    this.getZ() + offsetZ,
                    1, 0, 0, 0, 0);
            }
        }
    }
    
    private void spawnIdleParticles() {
        if (getEvolutionStage() >= 2 && !this.getWorld().isClient) {
            ServerWorld world = (ServerWorld) this.getWorld();
            world.spawnParticles(net.minecraft.particle.ParticleTypes.FLAME,
                this.getX(),
                this.getY() + 0.5,
                this.getZ(),
                1, 0.2, 0.2, 0.2, 0.01);
        }
    }
    
    private void updateCustomName() {
        EvolutionStage stage = EvolutionStage.fromLevel(getEvolutionStage());
        String role = getRole();
        String nameText = "Frog Helper [" + stage.getName() + "]";
        
        if (!role.isEmpty()) {
            nameText += " - " + role;
        }
        
        this.setCustomName(Text.literal(nameText)
            .formatted(stage.getColor()));
        this.setCustomNameVisible(true);
    }
    
    @Override
    public FrogHelperEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null; // Helpers cannot breed
    }
    
    @Override
    public double getMountedHeightOffset() {
        return this.isSitting() ? 0.0 : 0.3;
    }

    @Override
    public EntityView method_48926() {
        return this.getWorld();
    }
}