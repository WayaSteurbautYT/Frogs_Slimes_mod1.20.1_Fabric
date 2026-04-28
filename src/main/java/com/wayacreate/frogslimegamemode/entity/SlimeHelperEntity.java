package com.wayacreate.frogslimegamemode.entity;

import com.wayacreate.frogslimegamemode.achievements.AchievementManager;
import com.wayacreate.frogslimegamemode.eating.MobAbility;
import com.wayacreate.frogslimegamemode.entity.ai.HelperRoleManager;
import com.wayacreate.frogslimegamemode.evolution.EvolutionStage;
import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import com.wayacreate.frogslimegamemode.gamemode.PlayerLevel;
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
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;

import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.EntityGetter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SlimeHelperEntity extends TamableAnimal {
    private static final EntityDataAccessor<Integer> EVOLUTION_STAGE = SynchedEntityData.registerData(SlimeHelperEntity.class, EntityDataSerializers.INTEGER);
    private static final EntityDataAccessor<Integer> MOBS_KILLED = SynchedEntityData.registerData(SlimeHelperEntity.class, EntityDataSerializers.INTEGER);
    private static final EntityDataAccessor<Boolean> FINAL_FORM = SynchedEntityData.registerData(SlimeHelperEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> ROLE = SynchedEntityData.registerData(SlimeHelperEntity.class, EntityDataSerializers.STRING);
    private final List<String> abilities = new ArrayList<>();
    private final List<net.minecraft.world.entity.ai.goal.Goal> activeRoleGoals = new ArrayList<>();
    private int abilityCooldown = 0;
    private String lastRole = "";
    
    public SlimeHelperEntity(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
        this.setTamed(false);
    }

    @Override
    public EntityGetter method_48926() {
        return this.getWorld();
    }

    @Override
    public AgeableMob createChild(ServerLevel world, AgeableMob entity) {
        return null;
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.GENERIC_MAX_HEALTH, 25.0)
            .add(Attributes.GENERIC_ATTACK_DAMAGE, 5.0)
            .add(Attributes.GENERIC_MOVEMENT_SPEED, 0.25)
            .add(Attributes.GENERIC_FOLLOW_RANGE, 32.0)
            .add(Attributes.GENERIC_KNOCKBACK_RESISTANCE, 0.3);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(EVOLUTION_STAGE, 0);
        this.dataTracker.startTracking(MOBS_KILLED, 0);
        this.dataTracker.startTracking(FINAL_FORM, false);
        this.dataTracker.startTracking(ROLE, "");
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new SitGoal(this));
        this.goalSelector.add(3, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(4, new FollowOwnerGoal(this, 0.9, 10.0f, 2.0f, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.7));
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
                    player.sendMessage(Component.literal("Slime Helper Stats:")
                        .formatted(ChatFormatting.GREEN, ChatFormatting.BOLD), false);
                    player.sendMessage(Component.literal("Evolution: " + EvolutionStage.fromLevel(getEvolutionStage()).getName())
                        .formatted(ChatFormatting.YELLOW), false);
                    if (isFinalForm()) {
                        player.sendMessage(Component.literal("[FINAL FORM UNLOCKED]")
                            .formatted(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false);
                    }
                    player.sendMessage(Component.literal("Mobs Killed: " + getMobsKilled())
                        .formatted(ChatFormatting.AQUA), false);
                    player.sendMessage(Component.literal("Attack Damage: " + getAttributeValue(Attributes.GENERIC_ATTACK_DAMAGE))
                        .formatted(ChatFormatting.RED), false);
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
                GamemodeManager.grantAdvancement((net.minecraft.server.level.ServerPlayer) player, "frogslimegamemode:tame_slime");
                player.sendMessage(Component.literal("Slime helper joined your team!")
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
            owner.sendMessage(Component.literal("Your slime helper has been defeated!")
                .formatted(ChatFormatting.RED), false);
        }
    }
    
    public void onKilledMob() {
        int killed = getMobsKilled() + 1;
        setMobsKilled(killed);
        
        if (killed >= 15 && getEvolutionStage() == 0) {
            evolve();
        } else if (killed >= 35 && getEvolutionStage() == 1) {
            evolve();
        } else if (killed >= 60 && getEvolutionStage() == 2) {
            evolve();
        }
        
        if (this.getOwner() instanceof Player owner) {
            GamemodeManager.getData(owner).incrementMobsEaten();
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

            refreshAttributes();
            this.setHealth(this.getMaxHealth());
            
            spawnEvolutionParticles();
            updateCustomName();
            
            if (this.getOwner() instanceof Player owner) {
                owner.sendMessage(Component.literal("Your slime helper evolved to ")
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
    
    public void addAbility(MobAbility ability) {
        if (!abilities.contains(ability.getId())) {
            abilities.add(ability.getId());
            refreshAttributes();
        }
    }
    
    private void refreshAttributes() {
        double attackDamage;
        double movementSpeed = 0.25;
        double maxHealth;
        double knockbackResistance;

        if (isFinalForm()) {
            maxHealth = 200.0;
            attackDamage = 30.0;
            knockbackResistance = 1.0;
        } else {
            maxHealth = 25.0 + (15.0 * getEvolutionStage());
            attackDamage = 5.0 + (3.0 * getEvolutionStage());
            knockbackResistance = 0.3;
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
    
    public void unlockFinalForm() {
        if (!isFinalForm()) {
            setFinalForm(true);
            setEvolutionStage(4);

            refreshAttributes();
            this.setHealth(this.getMaxHealth());
            
            spawnFinalFormParticles();
            updateCustomName();
            
            if (this.getOwner() instanceof Player owner) {
                owner.sendMessage(Component.literal("═══════════════════════════")
                    .formatted(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false);
                owner.sendMessage(Component.literal("FINAL EVOLUTION COMPLETE")
                    .formatted(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false);
                owner.sendMessage(Component.literal("Your slime has become... unstoppable")
                    .formatted(ChatFormatting.RED), false);
                owner.sendMessage(Component.literal("═══════════════════════════")
                    .formatted(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false);
            }
        }
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
    
    public boolean isFinalForm() {
        return this.dataTracker.get(FINAL_FORM);
    }
    
    public void setFinalForm(boolean finalForm) {
        this.dataTracker.set(FINAL_FORM, finalForm);
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
        nbt.putBoolean("FinalForm", isFinalForm());
        nbt.putString("Role", getRole());
        
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
        setFinalForm(nbt.getBoolean("FinalForm"));
        setRole(nbt.getString("Role"));
        
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
    
    private void spawnFinalFormParticles() {
        if (!this.getWorld().isClient) {
            ServerLevel world = (ServerLevel) this.getWorld();
            for (int i = 0; i < 50; i++) {
                double offsetX = (world.random.nextDouble() - 0.5) * 3;
                double offsetY = world.random.nextDouble() * 3;
                double offsetZ = (world.random.nextDouble() - 0.5) * 3;
                
                world.spawnParticles(net.minecraft.core.particles.ParticleTypes.DRAGON_BREATH,
                    this.getX() + offsetX,
                    this.getY() + offsetY,
                    this.getZ() + offsetZ,
                    1, 0, 0, 0, 0);
            }
        }
    }
    
    private void spawnIdleParticles() {
        if (isFinalForm() && !this.getWorld().isClient) {
            ServerLevel world = (ServerLevel) this.getWorld();
            world.spawnParticles(ParticleTypes.DRAGON_BREATH,
                this.getX(),
                this.getY() + 0.5,
                this.getZ(),
                1, 0.3, 0.3, 0.3, 0.01);
        } else if (getEvolutionStage() >= 2 && !this.getWorld().isClient) {
            ServerLevel world = (ServerLevel) this.getWorld();
            ItemStackParticleEffect slimeParticles = new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.SLIME_BALL));
            world.spawnParticles(slimeParticles,
                this.getX(),
                this.getY() + 0.5,
                this.getZ(),
                1, 0.2, 0.2, 0.2, 0.01);
        }
    }
    
    private void updateCustomName() {
        String role = getRole();
        
        if (isFinalForm()) {
            String nameText = "ULTIMATE SLIME";
            if (!role.isEmpty()) {
                nameText += " - " + role;
            }
            this.setCustomName(Component.literal(nameText)
                .formatted(ChatFormatting.DARK_RED, ChatFormatting.BOLD));
        } else {
            EvolutionStage stage = EvolutionStage.fromLevel(getEvolutionStage());
            String nameText = "Slime Helper [" + stage.getName() + "]";
            if (!role.isEmpty()) {
                nameText += " - " + role;
            }
            this.setCustomName(Component.literal(nameText)
                .formatted(stage.getColor()));
        }
        this.setCustomNameVisible(true);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Decrease ability cooldown
        if (abilityCooldown > 0) {
            abilityCooldown--;
        }
        
        // Try to use combat ability when in combat and cooldown is ready
        if (!this.getWorld().isClient && this.getWorld() instanceof ServerLevel world) {
            if (this.getTarget() != null && abilityCooldown == 0) {
                tryUseCombatAbility(world);
            }
        }

        if (!Objects.equals(getRole(), lastRole)) {
            lastRole = getRole();
            refreshRoleGoals();
            refreshAttributes();
            updateCustomName();
        }
        
        // Spawn idle particles
        spawnIdleParticles();
    }

    private void refreshRoleGoals() {
        for (net.minecraft.world.entity.ai.goal.Goal goal : activeRoleGoals) {
            this.goalSelector.remove(goal);
        }
        activeRoleGoals.clear();

        net.minecraft.world.entity.ai.goal.Goal roleGoal = HelperRoleManager.createRoleGoal(getRole(), this);
        if (roleGoal != null) {
            activeRoleGoals.add(roleGoal);
            this.goalSelector.add(8, roleGoal);
        }
    }
}
