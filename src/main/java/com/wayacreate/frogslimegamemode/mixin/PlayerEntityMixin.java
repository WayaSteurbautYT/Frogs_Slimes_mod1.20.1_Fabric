package com.wayacreate.frogslimegamemode.mixin;

import com.wayacreate.frogslimegamemode.entity.FrogHelperEntity;
import com.wayacreate.frogslimegamemode.entity.SlimeHelperEntity;
import com.wayacreate.frogslimegamemode.eating.MobAbility;
import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import com.wayacreate.frogslimegamemode.network.ModNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Inject(method = "attack", at = @At("HEAD"))
    private void onAttack(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        
        if (!player.getWorld().isClient) {
            // Check if player is holding a mob ability item and left-clicking air
            if (target == null) {
                ItemStack stack = player.getMainHandStack();
                if (com.wayacreate.frogslimegamemode.item.MobAbilityItem.isMobAbility(stack)) {
                    consumeMobAbility(player, stack);
                    ci.cancel();
                    return;
                }
            }
            
            if (!target.isAlive()) {
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
                    
                    // Broadcast notification
                    player.getServer().getPlayerManager().broadcast(
                        Text.literal("[KILL] ")
                            .formatted(Formatting.RED, Formatting.BOLD)
                            .append(Text.literal(player.getName().getString())
                                .formatted(Formatting.YELLOW))
                            .append(Text.literal(" killed ")
                                .formatted(Formatting.GRAY))
                            .append(Text.literal(killedPlayer.getName().getString())
                                .formatted(Formatting.YELLOW))
                            .append(Text.literal(" and stole their abilities!")
                                .formatted(Formatting.RED)),
                        false
                    );
                    
                    // Speedrun mode: if speedrunner kills hunter, give extra reward
                    if (com.wayacreate.frogslimegamemode.gamemode.ManhuntManager.isSpeedrunner(player) && 
                        com.wayacreate.frogslimegamemode.gamemode.ManhuntManager.isHunter(killedPlayer)) {
                        // Drop ability item for killer
                        var abilities = GamemodeManager.getData(killedPlayer).getPlayerAbilities();
                        if (!abilities.isEmpty()) {
                            String randomAbility = abilities.get((int) (Math.random() * abilities.size()));
                            ItemStack abilityDrop = com.wayacreate.frogslimegamemode.item.AbilityDropItem.createAbilityDrop(randomAbility);
                            player.dropItem(abilityDrop, false);
                            player.sendMessage(Text.literal("Hunter dropped an ability!")
                                .formatted(Formatting.GREEN, Formatting.BOLD), false);
                        }
                    }
                }
            }
        }
    }
    
    private void consumeMobAbility(PlayerEntity player, ItemStack stack) {
        String abilityId = com.wayacreate.frogslimegamemode.item.MobAbilityItem.getAbilityId(stack);
        
        if (abilityId != null && !abilityId.isEmpty()) {
            MobAbility ability = MobAbility.getAbility(abilityId);
            
            if (ability != null) {
                // Check if player is sneaking to add to helper, otherwise add to player
                boolean addToHelper = player.isSneaking();
                
                if (player instanceof net.minecraft.server.network.ServerPlayerEntity serverPlayer) {
                    // Get the item to display in the animation
                    net.minecraft.item.Item displayItem = com.wayacreate.frogslimegamemode.item.AbilityDropItem.getDropItemForAbility(abilityId);
                    
                    if (addToHelper) {
                        // Add to helper abilities
                        var helpers = player.getWorld().getEntitiesByClass(
                            FrogHelperEntity.class,
                            player.getBoundingBox().expand(32),
                            frog -> frog.isOwner(player)
                        );
                        
                        var slimes = player.getWorld().getEntitiesByClass(
                            SlimeHelperEntity.class,
                            player.getBoundingBox().expand(32),
                            slime -> slime.isOwner(player)
                        );
                        
                        if (!helpers.isEmpty()) {
                            helpers.get(0).addAbility(ability);
                            ModNetworking.sendTotemAnimation(serverPlayer, 
                                "Helper Ability Added!", 
                                ability.getName() + " - " + ability.getDescription(), 
                                Formatting.GREEN,
                                displayItem);
                            serverPlayer.playSound(net.minecraft.sound.SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                            player.sendMessage(Text.literal("Your frog gained ")
                                .formatted(Formatting.GREEN)
                                .append(ability.getFormattedName())
                                .append(Text.literal("!").formatted(Formatting.GREEN)), false);
                        } else if (!slimes.isEmpty()) {
                            slimes.get(0).addAbility(ability);
                            ModNetworking.sendTotemAnimation(serverPlayer, 
                                "Helper Ability Added!", 
                                ability.getName() + " - " + ability.getDescription(), 
                                Formatting.GREEN,
                                displayItem);
                            serverPlayer.playSound(net.minecraft.sound.SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                            player.sendMessage(Text.literal("Your slime gained ")
                                .formatted(Formatting.GREEN)
                                .append(ability.getFormattedName())
                                .append(Text.literal("!").formatted(Formatting.GREEN)), false);
                        } else {
                            player.sendMessage(Text.literal("No helper nearby! Ability added to you instead.")
                                .formatted(Formatting.YELLOW), false);
                            com.wayacreate.frogslimegamemode.gamemode.GamemodeManager.getData(serverPlayer).addAbility(abilityId);
                            ModNetworking.sendTotemAnimation(serverPlayer, 
                                "Ability Unlocked!", 
                                ability.getName() + " - " + ability.getDescription(), 
                                Formatting.LIGHT_PURPLE,
                                displayItem);
                            serverPlayer.playSound(net.minecraft.sound.SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                            player.sendMessage(Text.literal("You unlocked the ")
                                .formatted(Formatting.LIGHT_PURPLE)
                                .append(ability.getFormattedName())
                                .append(Text.literal("! Press [TAB] to switch abilities.").formatted(Formatting.YELLOW)), false);
                        }
                    } else {
                        // Add to player's unlocked abilities
                        com.wayacreate.frogslimegamemode.gamemode.GamemodeManager.getData(serverPlayer).addAbility(abilityId);
                        
                        // Send totem animation packet with item for particles
                        ModNetworking.sendTotemAnimation(serverPlayer, 
                            "Ability Unlocked!", 
                            ability.getName() + " - " + ability.getDescription(), 
                            Formatting.LIGHT_PURPLE,
                            displayItem);
                        
                        // Send title animation
                        ModNetworking.showTitle(serverPlayer, 
                            "Ability Unlocked!", 
                            ability.getName() + " - " + ability.getDescription(), 
                            Formatting.LIGHT_PURPLE);
                        
                        // Play level-up sound directly on server (client will hear it)
                        serverPlayer.playSound(net.minecraft.sound.SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                        
                        // Apply ability bonuses to the player
                        com.wayacreate.frogslimegamemode.item.AbilityDropItem.applyAbilityToPlayerStatic(player, ability);
                        
                        // Send message
                        player.sendMessage(Text.literal("You unlocked the ")
                            .formatted(Formatting.LIGHT_PURPLE)
                            .append(ability.getFormattedName())
                            .append(Text.literal("! Press [TAB] to switch abilities.").formatted(Formatting.YELLOW)), false);
                    }
                }
                
                // Consume the item
                stack.decrement(1);
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

            if (player instanceof net.minecraft.server.network.ServerPlayerEntity serverPlayer
                && com.wayacreate.frogslimegamemode.gamemode.ManhuntManager.isInGame(serverPlayer)
                && com.wayacreate.frogslimegamemode.gamemode.ManhuntManager.isSpeedrunner(serverPlayer)) {
                com.wayacreate.frogslimegamemode.gamemode.ManhuntManager.onSpeedrunnerDeath(serverPlayer);
            }
            
            // Drop raw ability drop items on death (need to be crafted with normal drops)
            var abilities = GamemodeManager.getData(player).getPlayerAbilities();
            for (String abilityId : abilities) {
                ItemStack drop = com.wayacreate.frogslimegamemode.item.AbilityDropItem.createAbilityDrop(abilityId);
                player.dropItem(drop, false);
            }
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
