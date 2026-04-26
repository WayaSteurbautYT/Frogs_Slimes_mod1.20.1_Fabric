package com.wayacreate.frogslimegamemode.entity;

import com.wayacreate.frogslimegamemode.eating.MobAbility;
import com.wayacreate.frogslimegamemode.evolution.EvolutionStage;
import com.wayacreate.frogslimegamemode.evolution.MobTransformation;
import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import com.wayacreate.frogslimegamemode.gamemode.PlayerLevel;
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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class FrogHelperEntity extends TameableEntity {
    private static final TrackedData<Integer> EVOLUTION_STAGE = DataTracker.registerData(FrogHelperEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> MOBS_KILLED = DataTracker.registerData(FrogHelperEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<String> ROLE = DataTracker.registerData(FrogHelperEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<String> TRANSFORMATION = DataTracker.registerData(FrogHelperEntity.class, TrackedDataHandlerRegistry.STRING);
    private int particleCooldown = 0;
    private final List<String> abilities = new ArrayList<>();
    private String lastRole = "";
    private int abilityCooldown = 0;
    private int tongueExtensionTicks = 0;
    private net.minecraft.entity.Entity tongueTarget = null;
    
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
        this.dataTracker.startTracking(TRANSFORMATION, "frog");
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
                    player.sendMessage(Text.literal("Abilities: " + abilities.size())
                        .formatted(Formatting.LIGHT_PURPLE), false);
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
            
            double healthBonus = 10.0 * newStage;
            double damageBonus = 2.0 * newStage;
            
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(20.0 + healthBonus);
            this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(4.0 + damageBonus);
            this.setHealth(this.getMaxHealth());
            
            // Apply transformation at higher evolution stages
            if (newStage >= 2) {
                applyTransformation();
            }
            
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
    
    public void applyTransformation() {
        String currentTransform = getTransformation();
        MobTransformation current = MobTransformation.fromId(currentTransform);
        MobTransformation next = MobTransformation.getNextTransformation(current, getEvolutionStage());
        
        if (next != current) {
            setTransformation(next.getId());
            
            // Apply transformation-specific bonuses
            switch (next) {
                case ENDERMAN -> {
                    this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.4);
                    this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(this.getMaxHealth() + 10);
                }
                case BLAZE -> {
                    this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) + 3);
                }
                case IRON_GOLEM -> {
                    this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(this.getMaxHealth() + 20);
                    this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.5);
                }
                case WARDEN -> {
                    this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(this.getMaxHealth() + 30);
                    this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) + 5);
                }
                default -> {} // No special bonuses for other transformations
            }
            
            this.setHealth(this.getMaxHealth());
            
            if (this.getOwner() instanceof PlayerEntity owner) {
                owner.sendMessage(Text.literal("Your helper transformed into ")
                    .formatted(Formatting.AQUA)
                    .append(next.getFormattedName())
                    .append(Text.literal("!").formatted(Formatting.AQUA)), false);
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
        nbt.putString("Transformation", getTransformation());
        
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
        setTransformation(nbt.getString("Transformation"));
        
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
        
        // Handle ability cooldowns and execution
        if (!this.getWorld().isClient && this.getWorld() instanceof ServerWorld serverWorld) {
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
            if (this.getWorld() instanceof ServerWorld serverWorld) {
                spawnTongueParticles(serverWorld);
            }
        }
        
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
        switch (ability.getActiveAbility()) {
            case NONE -> {}
            case TELEPORT -> executeTeleport(world);
            case FIREBALL -> executeFireball(world);
            case ICE_SUMMON -> executeIceSummon(world);
            case POISON_CLOUD -> executePoisonCloud(world);
            case LIGHTNING_STRIKE -> executeLightningStrike(world);
            case LEAP_ATTACK -> executeLeapAttack(world);
            case SONIC_BOOM -> executeSonicBoom(world);
            case WEB_SHOT -> executeWebShot(world);
            case THORNS -> executeThorns(world);
        }
    }
    
    private void executeTeleport(ServerWorld world) {
        if (this.getTarget() != null) {
            Vec3d targetPos = this.getTarget().getPos();
            double teleportDistance = 5.0;
            double angle = world.random.nextDouble() * Math.PI * 2;
            double offsetX = Math.cos(angle) * teleportDistance;
            double offsetZ = Math.sin(angle) * teleportDistance;
            
            this.teleport(targetPos.x + offsetX, targetPos.y, targetPos.z + offsetZ);
            for (int i = 0; i < 20; i++) {
                double px = (world.random.nextDouble() - 0.5) * 2;
                double py = world.random.nextDouble() * 2;
                double pz = (world.random.nextDouble() - 0.5) * 2;
                world.spawnParticles(ParticleTypes.PORTAL,
                    this.getX() + px, this.getY() + py, this.getZ() + pz,
                    1, 0.0, 0.0, 0.0, 0.0);
            }
        }
    }
    
    private void executeFireball(ServerWorld world) {
        if (this.getTarget() != null) {
            Vec3d targetPos = this.getTarget().getPos();
            Vec3d direction = targetPos.subtract(this.getPos()).normalize();
            
            for (int i = 0; i < 5; i++) {
                world.spawnParticles(ParticleTypes.FLAME,
                    this.getX() + direction.x * i,
                    this.getY() + 1.0,
                    this.getZ() + direction.z * i,
                    3, 0.1, 0.1, 0.1, 0.02);
            }
            
            if (this.getTarget().distanceTo(this) < 8) {
                this.getTarget().damage(world.getDamageSources().magic(), 6.0f);
                this.getTarget().setFireTicks(60);
            }
        }
    }
    
    private void executeIceSummon(ServerWorld world) {
        if (this.getTarget() != null) {
            Vec3d targetPos = this.getTarget().getPos();
            
            for (int i = 0; i < 15; i++) {
                double offsetX = (world.random.nextDouble() - 0.5) * 3;
                double offsetZ = (world.random.nextDouble() - 0.5) * 3;
                world.spawnParticles(ParticleTypes.SNOWFLAKE,
                    targetPos.x + offsetX,
                    targetPos.y + 1,
                    targetPos.z + offsetZ,
                    2, 0.0, 0.1, 0.0, 0.01);
            }
            
            if (this.getTarget().distanceTo(this) < 6) {
                this.getTarget().addVelocity(0, -0.5, 0);
            }
        }
    }
    
    private void executePoisonCloud(ServerWorld world) {
        for (int i = 0; i < 20; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 4;
            double offsetY = world.random.nextDouble() * 2;
            double offsetZ = (world.random.nextDouble() - 0.5) * 4;
            world.spawnParticles(ParticleTypes.EFFECT,
                this.getX() + offsetX,
                this.getY() + offsetY,
                this.getZ() + offsetZ,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (this.getTarget() != null && this.getTarget().distanceTo(this) < 5) {
            this.getTarget().addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.POISON, 100, 1));
        }
    }
    
    private void executeLightningStrike(ServerWorld world) {
        if (this.getTarget() != null) {
            Vec3d targetPos = this.getTarget().getPos();
            world.spawnParticles(ParticleTypes.ELECTRIC_SPARK,
                targetPos.x, targetPos.y + 2, targetPos.z,
                10, 0.5, 0.5, 0.5, 0.1);
            
            if (this.getTarget().distanceTo(this) < 10) {
                this.getTarget().damage(world.getDamageSources().magic(), 10.0f);
            }
        }
    }
    
    private void executeLeapAttack(ServerWorld world) {
        Vec3d velocity = new Vec3d(0, 0.8, 0);
        this.setVelocity(velocity);
        this.velocityModified = true;
        for (int i = 0; i < 10; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.CLOUD,
                this.getX() + px, this.getY() + py, this.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
    }
    
    private void executeSonicBoom(ServerWorld world) {
        for (int i = 0; i < 30; i++) {
            double angle = (i / 30.0) * Math.PI * 2;
            double offsetX = Math.cos(angle) * 3;
            double offsetZ = Math.sin(angle) * 3;
            world.spawnParticles(ParticleTypes.SONIC_BOOM,
                this.getX() + offsetX,
                this.getY() + 1,
                this.getZ() + offsetZ,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (this.getTarget() != null && this.getTarget().distanceTo(this) < 6) {
            this.getTarget().damage(world.getDamageSources().magic(), 8.0f);
            this.getTarget().takeKnockback(2.0, this.getX() - this.getTarget().getX(), this.getZ() - this.getTarget().getZ());
        }
    }
    
    private void executeWebShot(ServerWorld world) {
        if (this.getTarget() != null) {
            Vec3d targetPos = this.getTarget().getPos();
            world.spawnParticles(ParticleTypes.ITEM_SNOWBALL,
                targetPos.x, targetPos.y + 1, targetPos.z,
                10, 0.3, 0.3, 0.3, 0.02);
            
            if (this.getTarget().distanceTo(this) < 8) {
                this.getTarget().addVelocity(0, -0.3, 0);
            }
        }
    }
    
    private void executeThorns(ServerWorld world) {
        for (int i = 0; i < 15; i++) {
            double px = (world.random.nextDouble() - 0.5) * 2;
            double py = world.random.nextDouble() * 2;
            double pz = (world.random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.DAMAGE_INDICATOR,
                this.getX() + px, this.getY() + py, this.getZ() + pz,
                1, 0.0, 0.0, 0.0, 0.0);
        }
        
        if (this.getTarget() != null && this.getTarget().distanceTo(this) < 3) {
            this.getTarget().damage(world.getDamageSources().thorns(this), 4.0f);
        }
    }
    
    private void handleTongueEating(ServerWorld world) {
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
    
    private void startTongueAttack(net.minecraft.entity.Entity target) {
        tongueTarget = target;
        tongueExtensionTicks = 20; // 1 second animation
        
        // Pull target towards the frog
        Vec3d direction = this.getPos().subtract(target.getPos()).normalize();
        double pullStrength = 0.8;
        target.addVelocity(direction.x * pullStrength, 0.3, direction.z * pullStrength);
        target.velocityModified = true;
        
        // Damage target when pulled close
        if (target instanceof net.minecraft.entity.LivingEntity livingTarget) {
            livingTarget.damage(this.getWorld().getDamageSources().mobAttack(this), 3.0f);
        }
    }
    
    private void spawnTongueParticles(ServerWorld world) {
        if (tongueTarget != null && tongueTarget.isAlive()) {
            // Create particle line from frog to target
            Vec3d startPos = this.getPos().add(0, 0.5, 0);
            Vec3d endPos = tongueTarget.getPos().add(0, 0.5, 0);
            Vec3d direction = endPos.subtract(startPos);
            double distance = direction.length();
            int particleCount = (int) (distance * 2);
            
            for (int i = 0; i < particleCount; i++) {
                double t = i / (double) particleCount;
                Vec3d particlePos = startPos.add(direction.multiply(t));
                
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
        String transformation = getTransformation();
        MobTransformation mobTransform = MobTransformation.fromId(transformation);
        
        String nameText = mobTransform.getDisplayName() + " [" + stage.getName() + "]";
        
        if (!role.isEmpty()) {
            nameText += " - " + role;
        }
        
        this.setCustomName(Text.literal(nameText)
            .formatted(mobTransform.getColor()));
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