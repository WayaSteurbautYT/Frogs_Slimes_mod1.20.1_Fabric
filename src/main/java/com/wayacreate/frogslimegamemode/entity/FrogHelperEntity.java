package com.wayacreate.frogslimegamemode.entity;

import com.wayacreate.frogslimegamemode.achievements.AchievementManager;
import com.wayacreate.frogslimegamemode.eating.MobAbility;
import com.wayacreate.frogslimegamemode.entity.ai.BuilderGoal;
import com.wayacreate.frogslimegamemode.entity.ai.FarmerGoal;
import com.wayacreate.frogslimegamemode.entity.ai.HelperRoleManager;
import com.wayacreate.frogslimegamemode.entity.ai.LumberjackGoal;
import com.wayacreate.frogslimegamemode.entity.ai.MiningGoal;
import com.wayacreate.frogslimegamemode.evolution.EvolutionStage;
import com.wayacreate.frogslimegamemode.evolution.MobTransformation;
import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import com.wayacreate.frogslimegamemode.gamemode.PlayerLevel;
import com.wayacreate.frogslimegamemode.tasks.TaskManager;
import com.wayacreate.frogslimegamemode.tasks.TaskType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.level.Level;

import java.util.Objects;

import java.util.ArrayList;
import java.util.List;

public class FrogHelperEntity extends TamableAnimal {
    private static final EntityDataAccessor<Integer> EVOLUTION_STAGE = SynchedEntityData.registerData(FrogHelperEntity.class, EntityDataSerializers.INTEGER);
    private static final EntityDataAccessor<Integer> MOBS_KILLED = SynchedEntityData.registerData(FrogHelperEntity.class, EntityDataSerializers.INTEGER);
    private static final EntityDataAccessor<String> ROLE = SynchedEntityData.registerData(FrogHelperEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> TRANSFORMATION = SynchedEntityData.registerData(FrogHelperEntity.class, EntityDataSerializers.STRING);
    private int particleCooldown = 0;
    private final List<String> abilities = new ArrayList<>();
    private final List<Goal> activeRoleGoals = new ArrayList<>();
    private String lastRole = "";
    private int abilityCooldown = 0;
    private int tongueExtensionTicks = 0;
    private net.minecraft.world.entity.Entity tongueTarget = null;
    
    public FrogHelperEntity(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
        this.setTamed(false);
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.GENERIC_MAX_HEALTH, 20.0)
            .add(Attributes.GENERIC_ATTACK_DAMAGE, 4.0)
            .add(Attributes.GENERIC_MOVEMENT_SPEED, 0.3)
            .add(Attributes.GENERIC_FOLLOW_RANGE, 32.0);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(EVOLUTION_STAGE, 0);
        this.dataTracker.startTracking(MOBS_KILLED, 0);
        this.dataTracker.startTracking(ROLE, "");
        this.dataTracker.startTracking(TRANSFORMATION, "frog");
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new SitGoal(this));
        this.goalSelector.add(3, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.add(4, new FollowOwnerGoal(this, 1.0, 10.0f, 2.0f, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(6, new LookAtEntityGoal(this, Player.class, 8.0f));
        this.goalSelector.add(7, new LookAroundGoal(this));
        
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, Monster.class, true));
    }
    
    @Override
    public InteractionResult interactMob(Player player, InteractionHand hand) {
        ItemStack stack = player.getStackInHand(hand);
        
        if (!this.getWorld().isClient) {
            if (this.isOwner(player)) {
                if (stack.isEmpty() && player.isSneaking()) {
                    player.sendMessage(Component.literal("Frog Helper Stats:")
                        .formatted(ChatFormatting.GREEN, ChatFormatting.BOLD), false);
                    player.sendMessage(Component.literal("Evolution: " + EvolutionStage.fromLevel(getEvolutionStage()).getName())
                        .formatted(ChatFormatting.YELLOW), false);
                    player.sendMessage(Component.literal("Mobs Killed: " + getMobsKilled())
                        .formatted(ChatFormatting.AQUA), false);
                    player.sendMessage(Component.literal("Attack Damage: " + getAttributeValue(Attributes.GENERIC_ATTACK_DAMAGE))
                        .formatted(ChatFormatting.RED), false);
                    player.sendMessage(Component.literal("Abilities: " + abilities.size())
                        .formatted(ChatFormatting.LIGHT_PURPLE), false);
                    return InteractionResult.SUCCESS;
                } else if (!stack.isEmpty()) {
                    spawnInteractParticles();
                }
            } else if (GamemodeManager.isInGamemode(player) && stack.isEmpty()) {
                this.setOwner(player);
                this.setTamed(true);
                GamemodeManager.getData(player).incrementHelpers();
                TaskManager.completeTask(player, TaskType.TAME_HELPER);
                if (player instanceof ServerPlayer serverPlayer) {
                    AchievementManager.unlockAchievement(serverPlayer, "first_helper");
                }
                GamemodeManager.grantAdvancement((net.minecraft.server.level.ServerPlayer) player, "frogslimegamemode:tame_frog");
                player.sendMessage(Component.literal("Frog helper joined your team!")
                    .formatted(ChatFormatting.GREEN), false);
                spawnInteractParticles();
                return InteractionResult.SUCCESS;
            }
        }
        
        return super.interactMob(player, hand);
    }
    
    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if (!this.getWorld().isClient && this.getOwner() instanceof Player owner) {
            owner.sendMessage(Component.literal("Your frog helper has fallen!")
                .formatted(ChatFormatting.RED), false);
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
        
        if (this.getOwner() instanceof Player owner) {
            var gamemodeData = GamemodeManager.getData(owner);
            if (gamemodeData != null) {
                gamemodeData.incrementMobsEaten();
            }
            // Grant XP to player for mob kills
            if (owner instanceof ServerPlayer serverPlayer) {
                PlayerLevel.addXP(serverPlayer, 10.0);
            }
        }
    }
    
    public void evolve() {
        int newStage = getEvolutionStage() + 1;
        if (newStage <= 3) {
            setEvolutionStage(newStage);

            // Apply transformation at higher evolution stages
            if (newStage >= 2) {
                applyTransformation();
            }

            refreshAttributes();
            this.setHealth(this.getMaxHealth());
            
            spawnEvolutionParticles();
            updateCustomName();
            
            if (this.getOwner() instanceof Player owner) {
                owner.sendMessage(Component.literal("Your frog helper evolved to ")
                    .formatted(ChatFormatting.GOLD)
                    .append(Component.literal(EvolutionStage.fromLevel(newStage).getName())
                        .formatted(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD))
                    .append(Component.literal("!").formatted(ChatFormatting.GOLD)), false);
                TaskManager.completeTask(owner, TaskType.EVOLVE_HELPER);
                if (owner instanceof ServerPlayer serverPlayer) {
                    AchievementManager.unlockAchievement(serverPlayer, "first_evolution");
                    if (newStage >= 2) {
                        AchievementManager.unlockAchievement(serverPlayer, "elite_helper");
                    }
                    if (newStage >= 3) {
                        AchievementManager.unlockAchievement(serverPlayer, "master_helper");
                    }
                }
            }
        }
    }
    
    public void applyTransformation() {
        String currentTransform = getTransformation();
        MobTransformation current = MobTransformation.fromId(currentTransform);
        MobTransformation next = MobTransformation.getNextTransformation(current, getEvolutionStage());
        
        if (next != current) {
            setTransformation(next.getId());

            if (this.getOwner() instanceof Player owner) {
                owner.sendMessage(Component.literal("Your helper transformed into ")
                    .formatted(ChatFormatting.AQUA)
                    .append(next.getFormattedName())
                    .append(Component.literal("!").formatted(ChatFormatting.AQUA)), false);
            }
        }
    }
    
    public String getTransformation() {
        return this.dataTracker.get(TRANSFORMATION);
    }
    
    public void setTransformation(String transformation) {
        this.dataTracker.set(TRANSFORMATION, transformation);
    }
    
    public void addAbility(MobAbility ability) {
        if (!abilities.contains(ability.getId())) {
            abilities.add(ability.getId());
            refreshAttributes();
        }
    }
    
    private void refreshAttributes() {
        double maxHealth = 20.0 + (10.0 * getEvolutionStage());
        double attackDamage = 4.0 + (2.0 * getEvolutionStage());
        double movementSpeed = 0.3;
        double knockbackResistance = 0.0;

        switch (MobTransformation.fromId(getTransformation())) {
            case ENDERMAN -> {
                movementSpeed += 0.1;
                maxHealth += 10.0;
            }
            case BLAZE -> attackDamage += 3.0;
            case IRON_GOLEM -> {
                maxHealth += 20.0;
                knockbackResistance += 0.5;
            }
            case WARDEN -> {
                maxHealth += 30.0;
                attackDamage += 5.0;
            }
            default -> {
            }
        }

        if (HelperRoleManager.isCombatRole(getRole())) {
            attackDamage += 4.0;
        }
        
        for (String abilityId : abilities) {
            MobAbility ability = MobAbility.getAbility(abilityId);
            if (ability != null) {
                attackDamage += ability.getDamageBonus();
                movementSpeed += ability.getSpeedBonus();
                maxHealth += ability.getHealthBonus();
                knockbackResistance += ability.getKnockbackResistance();
            }
        }
        
        this.getAttributeInstance(Attributes.GENERIC_ATTACK_DAMAGE).setBaseValue(attackDamage);
        this.getAttributeInstance(Attributes.GENERIC_MOVEMENT_SPEED).setBaseValue(movementSpeed);
        this.getAttributeInstance(Attributes.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
        this.getAttributeInstance(Attributes.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(knockbackResistance);
        this.setHealth(Math.min(this.getHealth(), this.getMaxHealth()));
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
        this.dataTracker.set(ROLE, HelperRoleManager.normalizeRole(role));
        refreshRoleGoals();
        refreshAttributes();
        updateCustomName();
    }
    
    @Override
    public void writeCustomDataToNbt(CompoundTag nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("EvolutionStage", getEvolutionStage());
        nbt.putInt("MobsKilled", getMobsKilled());
        nbt.putString("Role", getRole());
        nbt.putString("Transformation", getTransformation());
        
        // Save abilities
        nbt.putInt("AbilityCount", abilities.size());
        for (int i = 0; i < abilities.size(); i++) {
            nbt.putString("Ability_" + i, abilities.get(i));
        }
    }
    
    @Override
    public void readCustomDataFromNbt(CompoundTag nbt) {
        super.readCustomDataFromNbt(nbt);
        setEvolutionStage(nbt.getInt("EvolutionStage"));
        setMobsKilled(nbt.getInt("MobsKilled"));
        setRole(nbt.getString("Role"));
        setTransformation(nbt.getString("Transformation"));
        
        // Load abilities
        abilities.clear();
        int abilityCount = nbt.getInt("AbilityCount");
        for (int i = 0; i < abilityCount; i++) {
            String abilityId = nbt.getString("Ability_" + i);
            abilities.add(abilityId);
        }
        
        refreshRoleGoals();
        refreshAttributes();
        updateCustomName();
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Handle ability cooldowns and execution
        if (!this.getWorld().isClient && this.getWorld() instanceof ServerLevel serverWorld) {
            if (abilityCooldown > 0) {
                abilityCooldown--;
            } else if (this.getTarget() != null && getEvolutionStage() >= 1) {
                // Try to use an active ability during combat
                tryUseCombatAbility(serverWorld);
            }
            
            // Handle tongue eating mechanic
            handleTongueEating(serverWorld);
        }
        
        // Handle tongue animation on both client and server
        if (tongueExtensionTicks > 0) {
            tongueExtensionTicks--;
            if (this.getWorld() instanceof ServerLevel serverWorld) {
                spawnTongueParticles(serverWorld);
            }
        }
        
        String role = getRole();
        if (!Objects.equals(role, lastRole)) {
            lastRole = role;
            refreshRoleGoals();
            refreshAttributes();
            updateCustomName();
        }
        
        if (!this.getWorld().isClient && particleCooldown > 0) {
            particleCooldown--;
        }
        
        if (!this.getWorld().isClient && this.age % 40 == 0) {
            spawnIdleParticles();
        }
    }
    
    private void tryUseCombatAbility(ServerLevel world) {
        if (abilities.isEmpty()) return;
        
        // Pick a random ability with active combat effect
        List<String> activeAbilities = new ArrayList<>();
        for (String abilityId : abilities) {
            MobAbility ability = MobAbility.getAbility(abilityId);
            if (ability != null && ability.getActiveAbility() != MobAbility.AbilityType.NONE) {
                activeAbilities.add(abilityId);
            }
        }
        
        if (activeAbilities.isEmpty()) return;
        
        // 20% chance to use ability each tick when in combat
        if (world.random.nextFloat() < 0.2) {
            String selectedAbilityId = activeAbilities.get(world.random.nextInt(activeAbilities.size()));
            MobAbility ability = MobAbility.getAbility(selectedAbilityId);
            if (ability != null) {
                executeAbility(world, ability);
                abilityCooldown = ability.getAbilityCooldown();
            }
        }
    }
    
    private void executeAbility(ServerLevel world, MobAbility ability) {
        com.wayacreate.frogslimegamemode.abilities.HelperAbilityManager.executeAbility(this, ability, world);
    }
    
    private void handleTongueEating(ServerLevel world) {
        // Only attempt tongue eating if not already extending tongue
        if (tongueExtensionTicks > 0) return;
        
        // Check if we have a target and it's within range
        if (this.getTarget() != null && this.getTarget().isAlive()) {
            double distance = this.distanceTo(this.getTarget());
            double tongueRange = 6.0;
            
            // 10% chance to use tongue attack when in range
            if (distance <= tongueRange && world.random.nextFloat() < 0.1) {
                startTongueAttack(this.getTarget());
            }
        }
    }
    
    private void startTongueAttack(net.minecraft.world.entity.Entity target) {
        tongueTarget = target;
        tongueExtensionTicks = 20; // 1 second animation
        
        // Pull target towards the frog
        Vec3 direction = this.getPos().subtract(target.getPos()).normalize();
        double pullStrength = 0.8;
        target.addVelocity(direction.x * pullStrength, 0.3, direction.z * pullStrength);
        target.velocityModified = true;
        
        // Damage target when pulled close
        if (target instanceof net.minecraft.world.entity.LivingEntity livingTarget) {
            livingTarget.damage(this.getWorld().getDamageSources().mobAttack(this), 3.0f);
        }
    }
    
    private void spawnTongueParticles(ServerLevel world) {
        if (tongueTarget != null && tongueTarget.isAlive()) {
            // Create particle line from frog to target
            Vec3 startPos = this.getPos().add(0, 0.5, 0);
            Vec3 endPos = tongueTarget.getPos().add(0, 0.5, 0);
            Vec3 direction = endPos.subtract(startPos);
            double distance = direction.length();
            int particleCount = (int) (distance * 2);
            
            for (int i = 0; i < particleCount; i++) {
                double t = i / (double) particleCount;
                Vec3 particlePos = startPos.add(direction.multiply(t));
                
                // Add some randomness for organic look
                double offsetX = (world.random.nextDouble() - 0.5) * 0.2;
                double offsetY = (world.random.nextDouble() - 0.5) * 0.2;
                double offsetZ = (world.random.nextDouble() - 0.5) * 0.2;
                
                world.spawnParticles(ParticleTypes.ITEM_SLIME,
                    particlePos.x + offsetX,
                    particlePos.y + offsetY,
                    particlePos.z + offsetZ,
                    1, 0.0, 0.0, 0.0, 0.0);
            }
            
            // Spawn particles at the target's position
            world.spawnParticles(ParticleTypes.HEART,
                tongueTarget.getX(),
                tongueTarget.getY() + 1,
                tongueTarget.getZ(),
                1, 0.0, 0.0, 0.0, 0.0);
        } else {
            tongueTarget = null;
        }
    }
    
    private void spawnInteractParticles() {
        if (!this.getWorld().isClient) {
            ServerLevel world = (ServerLevel) this.getWorld();
            for (int i = 0; i < 10; i++) {
                double offsetX = (world.random.nextDouble() - 0.5) * 1.5;
                double offsetY = world.random.nextDouble() * 1.5;
                double offsetZ = (world.random.nextDouble() - 0.5) * 1.5;
                
                world.spawnParticles(net.minecraft.core.particles.ParticleTypes.HEART,
                    this.getX() + offsetX,
                    this.getY() + offsetY,
                    this.getZ() + offsetZ,
                    1, 0, 0, 0, 0);
            }
        }
    }
    
    private void spawnEvolutionParticles() {
        if (!this.getWorld().isClient) {
            ServerLevel world = (ServerLevel) this.getWorld();
            for (int i = 0; i < 30; i++) {
                double offsetX = (world.random.nextDouble() - 0.5) * 2;
                double offsetY = world.random.nextDouble() * 2;
                double offsetZ = (world.random.nextDouble() - 0.5) * 2;
                
                world.spawnParticles(net.minecraft.core.particles.ParticleTypes.TOTEM_OF_UNDYING,
                    this.getX() + offsetX,
                    this.getY() + offsetY,
                    this.getZ() + offsetZ,
                    1, 0, 0, 0, 0);
            }
        }
    }
    
    private void spawnIdleParticles() {
        if (getEvolutionStage() >= 2 && !this.getWorld().isClient) {
            ServerLevel world = (ServerLevel) this.getWorld();
            world.spawnParticles(net.minecraft.core.particles.ParticleTypes.FLAME,
                this.getX(),
                this.getY() + 0.5,
                this.getZ(),
                1, 0.2, 0.2, 0.2, 0.01);
        }
    }
    
    private void updateCustomName() {
        EvolutionStage stage = EvolutionStage.fromLevel(getEvolutionStage());
        String role = getRole();
        String transformation = getTransformation();
        MobTransformation mobTransform = MobTransformation.fromId(transformation);
        
        String nameText = mobTransform.getDisplayName() + " [" + stage.getName() + "]";
        
        if (!role.isEmpty()) {
            nameText += " - " + role;
        }
        
        this.setCustomName(Component.literal(nameText)
            .formatted(mobTransform.getColor()));
        this.setCustomNameVisible(true);
    }

    private void refreshRoleGoals() {
        for (Goal goal : activeRoleGoals) {
            this.goalSelector.remove(goal);
        }
        activeRoleGoals.clear();

        Goal roleGoal = HelperRoleManager.createRoleGoal(getRole(), this);
        if (roleGoal != null) {
            activeRoleGoals.add(roleGoal);
            this.goalSelector.add(8, roleGoal);
        }
    }
    
    @Override
    public FrogHelperEntity createChild(ServerLevel world, AgeableMob entity) {
        return null; // Helpers cannot breed
    }
    
    @Override
    public double getMountedHeightOffset() {
        return this.isSitting() ? 0.0 : 0.3;
    }

    @Override
    public EntityGetter method_48926() {
        return this.getWorld();
    }
}
