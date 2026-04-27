package com.wayacreate.frogslimegamemode.mixin;

import com.wayacreate.frogslimegamemode.entity.FrogHelperEntity;
import com.wayacreate.frogslimegamemode.entity.SlimeHelperEntity;
import com.wayacreate.frogslimegamemode.eating.MobAbility;
import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Inject(method = "attack", at = @At("TAIL"))
    private void onAttack(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        
        if (!player.getWorld().isClient && !target.isAlive()) {
            player.getWorld().getEntitiesByClass(
                FrogHelperEntity.class,
                player.getBoundingBox().expand(32),
                frog -> frog.isOwner(player)
            ).forEach(FrogHelperEntity::onKilledMob);
            
            player.getWorld().getEntitiesByClass(
                SlimeHelperEntity.class,
                player.getBoundingBox().expand(32),
                slime -> slime.isOwner(player)
            ).forEach(SlimeHelperEntity::onKilledMob);
            
            // Player eating mobs for abilities
            if (GamemodeManager.isInGamemode(player) && target instanceof MobEntity mob) {
                MobAbility ability = MobAbility.getAbilityFromEntity(mob.getType());
                if (ability != null) {
                    GamemodeManager.getData(player).addAbility(ability.getId());
                    player.sendMessage(Text.literal("You consumed ")
                        .formatted(Formatting.GREEN)
                        .append(Text.literal(mob.getName().getString())
                            .formatted(Formatting.YELLOW))
                        .append(Text.literal(" and gained ")
                            .formatted(Formatting.GREEN))
                        .append(ability.getFormattedName())
                        .append(Text.literal("!").formatted(Formatting.GREEN)), false);
                }
            }
            
            // Player kill rewards - grant 3 abilities when killing a player
            if (GamemodeManager.isInGamemode(player) && target instanceof PlayerEntity killedPlayer) {
                grantPlayerKillRewards(player, killedPlayer);
            }
        }
    }
    
    @Inject(method = "jump", at = @At("TAIL"))
    private void onJump(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (!player.getWorld().isClient && GamemodeManager.isInGamemode(player)) {
            GamemodeManager.getData(player).incrementJumpCount();
        }
    }
    
    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(DamageSource damageSource, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (!player.getWorld().isClient && GamemodeManager.isInGamemode(player)) {
            GamemodeManager.getData(player).incrementDeathCount();
        }
    }
    
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (!player.getWorld().isClient && GamemodeManager.isInGamemode(player)) {
            // Apply active abilities to player
            applyPlayerAbilities(player);
        }
    }
    
    private void applyPlayerAbilities(PlayerEntity player) {
        var data = GamemodeManager.getData(player);
        var abilities = data.getPlayerAbilities();
        
        for (String abilityId : abilities) {
            MobAbility ability = MobAbility.getAbilityById(abilityId);
            if (ability != null) {
                ability.applyToPlayer(player);
            }
        }
    }
    
    private void grantPlayerKillRewards(PlayerEntity killer, PlayerEntity victim) {
        // Grant WayaCreate ability
        GamemodeManager.getData(killer).addAbility("wayacreate");
        killer.sendMessage(Text.literal("You killed ")
            .formatted(Formatting.RED)
            .append(Text.literal(victim.getName().getString())
                .formatted(Formatting.YELLOW))
            .append(Text.literal(" and gained ")
                .formatted(Formatting.GREEN))
            .append(Text.literal("WayaCreate Power!").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD)), false);
        
        // Grant Derpy Derp ability with funny message
        GamemodeManager.getData(killer).addAbility("derpy_derp");
        String[] derpyMessages = {
            "Hah you suck! Really you got the worse ability!",
            "Derpy Derp activated! Enjoy your... disadvantages!",
            "You got Derpy Derp! At least it's... unique!",
            "Derpy Derp! The ability that makes you question your life choices!"
        };
        String randomMessage = derpyMessages[(int) (Math.random() * derpyMessages.length)];
        killer.sendMessage(Text.literal(randomMessage)
            .formatted(Formatting.RED, Formatting.ITALIC), false);
        
        // Grant a random player ability from the victim
        var victimAbilities = GamemodeManager.getData(victim).getPlayerAbilities();
        if (!victimAbilities.isEmpty()) {
            String randomAbility = victimAbilities.get((int) (Math.random() * victimAbilities.size()));
            GamemodeManager.getData(killer).addAbility(randomAbility);
            MobAbility ability = MobAbility.getAbilityById(randomAbility);
            if (ability != null) {
                killer.sendMessage(Text.literal("Stolen ability: ")
                    .formatted(Formatting.DARK_PURPLE)
                    .append(ability.getFormattedName()), false);
            }
        } else {
            // If victim has no abilities, give a random common ability
            String[] commonAbilities = {"zombie", "skeleton", "spider", "creeper", "slime"};
            String randomCommon = commonAbilities[(int) (Math.random() * commonAbilities.length)];
            GamemodeManager.getData(killer).addAbility(randomCommon);
            MobAbility ability = MobAbility.getAbilityById(randomCommon);
            if (ability != null) {
                killer.sendMessage(Text.literal("Bonus ability: ")
                    .formatted(Formatting.DARK_PURPLE)
                    .append(ability.getFormattedName()), false);
            }
        }
    }
}
