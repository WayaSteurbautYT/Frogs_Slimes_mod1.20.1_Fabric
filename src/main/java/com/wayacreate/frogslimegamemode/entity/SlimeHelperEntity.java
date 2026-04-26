package com.wayacreate.frogslimegamemode.entity;

import com.wayacreate.frogslimegamemode.eating.MobAbility;
import com.wayacreate.frogslimegamemode.evolution.EvolutionStage;
import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import com.wayacreate.frogslimegamemode.gamemode.PlayerLevel;
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
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.EntityView;

import java.util.ArrayList;
import java.util.List;

public class SlimeHelperEntity extends TameableEntity {
    private static final TrackedData<Integer> EVOLUTION_STAGE = DataTracker.registerData(SlimeHelperEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> MOBS_KILLED = DataTracker.registerData(SlimeHelperEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> FINAL_FORM = DataTracker.registerData(SlimeHelperEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<String> ROLE = DataTracker.registerData(SlimeHelperEntity.class, TrackedDataHandlerRegistry.STRING);
    private final List<String> abilities = new ArrayList<>();
    private int abilityCooldown = 0;
    
    public SlimeHelperEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        this.setTamed(false);
    }

    @Override
    public EntityView method_48926() {
        return this.getWorld();
    }

    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }
    
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 25.0)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0)
            .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.3);
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
                    player.sendMessage(Text.literal("Slime Helper Stats:")
                        .formatted(Formatting.GREEN, Formatting.BOLD), false);
                    player.sendMessage(Text.literal("Evolution: " + EvolutionStage.fromLevel(getEvolutionStage()).getName())
                        .formatted(Formatting.YELLOW), false);
                    if (isFinalForm()) {
                        player.sendMessage(Text.literal("[FINAL FORM UNLOCKED]")
                            .formatted(Formatting.DARK_RED, Formatting.BOLD), false);
                    }
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
                GamemodeManager.grantAdvancement((net.minecraft.server.network.ServerPlayerEntity) player, "frogslimegamemode:tame_slime");
                player.sendMessage(Text.literal("Slime helper joined your team!")
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
            owner.sendMessage(Text.literal("Your slime helper has been defeated!")
                .formatted(Formatting.RED), false);
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
        
        if (this.getOwner() instanceof PlayerEntity owner) {
            GamemodeManager.getData(owner).incrementMobsEaten();
            // Grant XP to player for mob kills
            if (owner instanceof ServerPlayerEntity serverPlayer) {
                PlayerLevel.addXP(serverPlayer, 10.0);
            }
        }
    }
    
    public void evolve() {
        int newStage = getEvolutionStage() + 1;
        if (newStage <= 3) {
            setEvolutionStage(newStage);
            
            double healthBonus = 15.0 * newStage;
            double damageBonus = 3.0 * newStage;
            
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(25.0 + healthBonus);
            this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(5.0 + damageBonus);
            this.setHealth(this.getMaxHealth());
            
            applyAbilityBonuses();
            
            spawnEvolutionParticles();
            updateCustomName();
            
            if (this.getOwner() instanceof PlayerEntity owner) {
                owner.sendMessage(Text.literal("Your slime helper evolved to ")
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
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(5.0 + totalDamageBonus);
        this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25 + totalSpeedBonus);
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(25.0 + totalHealthBonus);
        this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.3 + totalKnockbackBonus);
    }
    
    public List<String> getAbilities() {
        return new ArrayList<>(abilities);
    }
    
    public void unlockFinalForm() {
        if (!isFinalForm()) {
            setFinalForm(true);
            setEvolutionStage(4);
            
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(200.0);
            this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(30.0);
            this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1.0);
            this.setHealth(this.getMaxHealth());
            
            spawnFinalFormParticles();
            updateCustomName();
            
            if (this.getOwner() instanceof PlayerEntity owner) {
                owner.sendMessage(Text.literal("═══════════════════════════")
                    .formatted(Formatting.DARK_RED, Formatting.BOLD), false);
                owner.sendMessage(Text.literal("FINAL EVOLUTION COMPLETE")
                    .formatted(Formatting.DARK_RED, Formatting.BOLD), false);
                owner.sendMessage(Text.literal("Your slime has become... unstoppable")
                    .formatted(Formatting.RED), false);
                owner.sendMessage(Text.literal("═══════════════════════════")
                    .formatted(Formatting.DARK_RED, Formatting.BOLD), false);
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
        this.dataTracker.set(ROLE, role);
        updateCustomName();
    }
    
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
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
    public void readCustomDataFromNbt(NbtCompound nbt) {
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
        
        applyAbilityBonuses();
        updateCustomName();
    }
    
    private void tryUseCombatAbility(ServerWorld world) {
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
    
    private void executeAbility(ServerWorld world, MobAbility ability) {
        com.wayacreate.frogslimegamemode.abilities.HelperAbilityManager.executeAbility(this, ability, world);
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
    
    private void spawnFinalFormParticles() {
        if (!this.getWorld().isClient) {
            ServerWorld world = (ServerWorld) this.getWorld();
            for (int i = 0; i < 50; i++) {
                double offsetX = (world.random.nextDouble() - 0.5) * 3;
                double offsetY = world.random.nextDouble() * 3;
                double offsetZ = (world.random.nextDouble() - 0.5) * 3;
                
                world.spawnParticles(net.minecraft.particle.ParticleTypes.DRAGON_BREATH,
                    this.getX() + offsetX,
                    this.getY() + offsetY,
                    this.getZ() + offsetZ,
                    1, 0, 0, 0, 0);
            }
        }
    }
    
    private void spawnIdleParticles() {
        if (isFinalForm() && !this.getWorld().isClient) {
            ServerWorld world = (ServerWorld) this.getWorld();
            world.spawnParticles(ParticleTypes.DRAGON_BREATH,
                this.getX(),
                this.getY() + 0.5,
                this.getZ(),
                1, 0.3, 0.3, 0.3, 0.01);
        } else if (getEvolutionStage() >= 2 && !this.getWorld().isClient) {
            ServerWorld world = (ServerWorld) this.getWorld();
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
            this.setCustomName(Text.literal(nameText)
                .formatted(Formatting.DARK_RED, Formatting.BOLD));
        } else {
            EvolutionStage stage = EvolutionStage.fromLevel(getEvolutionStage());
            String nameText = "Slime Helper [" + stage.getName() + "]";
            if (!role.isEmpty()) {
                nameText += " - " + role;
            }
            this.setCustomName(Text.literal(nameText)
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
        if (!this.getWorld().isClient && this.getWorld() instanceof ServerWorld world) {
            if (this.getTarget() != null && abilityCooldown == 0) {
                tryUseCombatAbility(world);
            }
        }
        
        // Spawn idle particles
        spawnIdleParticles();
    }
}
