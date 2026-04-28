package com.wayacreate.frogslimegamemode.eating;

import com.wayacreate.frogslimegamemode.entity.FrogHelperEntity;
import com.wayacreate.frogslimegamemode.entity.SlimeHelperEntity;
import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import com.wayacreate.frogslimegamemode.item.AbilityDropItem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class EatingSystem {
    private static final int TICK_INTERVAL = 10;
    private static final double HELPER_SEARCH_RADIUS = 48.0;
    private static final double PLAYER_EAT_RADIUS = 2.5;

    public static void tick(MinecraftServer server) {
        if (server.getTicks() % TICK_INTERVAL != 0) {
            return;
        }

        for (ServerLevel world : server.getWorlds()) {
            for (ServerPlayer player : world.getPlayers()) {
                if (!GamemodeManager.isInGamemode(player)) {
                    continue;
                }

                // Player eating mobs
                playerEatNearbyMobs(player, world);

                Box helperSearchBox = player.getBoundingBox().expand(HELPER_SEARCH_RADIUS);

                world.getEntitiesByClass(
                    FrogHelperEntity.class,
                    helperSearchBox,
                    frog -> frog.isAlive() && frog.getOwner() == player
                ).forEach(frog -> {
                    collectNearbyItems(frog, world);
                    eatNearbyMobs(frog, world);
                });

                world.getEntitiesByClass(
                    SlimeHelperEntity.class,
                    helperSearchBox,
                    slime -> slime.isAlive() && slime.getOwner() == player
                ).forEach(slime -> {
                    collectNearbyItems(slime, world);
                    eatNearbyMobs(slime, world);
                });
            }
        }
    }
    
    private static void playerEatNearbyMobs(ServerPlayer player, ServerLevel world) {
        Box searchBox = player.getBoundingBox().expand(PLAYER_EAT_RADIUS);
        
        List<Mob> mobs = world.getEntitiesByClass(Mob.class, searchBox, e -> 
            e.isAlive() && !(e instanceof FrogHelperEntity) && !(e instanceof SlimeHelperEntity) && e.getHealth() <= 8
        );
        
        for (Mob mob : mobs) {
            if (mob.getHealth() <= 4) {
                // Send tongue animation packet to client
                com.wayacreate.frogslimegamemode.network.ModNetworking.sendPlayerTongueAnimation(player, mob.getId());
                
                // Give ability drop to player before killing the mob
                MobAbility ability = MobAbility.getAbilityFromEntity(mob.getType());
                
                if (ability != null) {
                    // 40% chance to drop ability (slower progression)
                    if (world.random.nextFloat() < 0.4f) {
                        ItemStack abilityDrop = AbilityDropItem.createAbilityDrop(ability.getId());
                        if (!abilityDrop.isEmpty()) {
                            // Drop the ability totem on the ground at mob's location
                            world.spawnEntity(new ItemEntity(world, mob.getX(), mob.getY(), mob.getZ(), abilityDrop));
                        }
                        
                        player.sendMessage(Component.literal("You ate a ")
                            .formatted(ChatFormatting.GREEN)
                            .append(Component.literal(mob.getName().getString())
                                .formatted(ChatFormatting.YELLOW))
                            .append(Component.literal(" and a totem dropped!")
                                .formatted(ChatFormatting.GREEN)), false);
                    } else {
                        player.sendMessage(Component.literal("The ")
                            .formatted(ChatFormatting.GRAY)
                            .append(Component.literal(mob.getName().getString())
                                .formatted(ChatFormatting.YELLOW))
                            .append(Component.literal(" was too slippery to catch!")
                                .formatted(ChatFormatting.GRAY)), false);
                    }
                }
                
                // Kill the mob to drop items naturally (normal drops + ability totem on ground)
                mob.damage(world.getDamageSources().generic(), Float.MAX_VALUE);
                
                spawnEatParticles(world, mob.getX(), mob.getY(), mob.getZ());
            }
        }
    }
    
    private static void collectNearbyItems(Object helper, ServerLevel world) {
        Box searchBox;
        Player owner = null;
        
        if (helper instanceof FrogHelperEntity frog) {
            searchBox = frog.getBoundingBox().expand(3.0);
            owner = (Player) frog.getOwner();
        } else if (helper instanceof SlimeHelperEntity slime) {
            searchBox = slime.getBoundingBox().expand(3.0);
            owner = (Player) slime.getOwner();
        } else {
            return;
        }
        
        if (owner == null || !GamemodeManager.isInGamemode(owner)) {
            return;
        }
        
        List<ItemEntity> items = world.getEntitiesByClass(ItemEntity.class, searchBox, e -> true);
        for (ItemEntity item : items) {
            if (!item.isRemoved() && !item.cannotPickup()) {
                if (!owner.getInventory().insertStack(item.getStack())) {
                    continue;
                }
                
                GamemodeManager.getData(owner).addItemsCollected(item.getStack().getCount());
                item.discard();
            }
        }
    }
    
    private static void eatNearbyMobs(Object helper, ServerLevel world) {
        Box searchBox;
        Player owner = null;
        int evolutionStage = 0;
        
        if (helper instanceof FrogHelperEntity frog) {
            searchBox = frog.getBoundingBox().expand(2.0 + (frog.getEvolutionStage() * 0.5));
            owner = (Player) frog.getOwner();
            evolutionStage = frog.getEvolutionStage();
        } else if (helper instanceof SlimeHelperEntity slime) {
            searchBox = slime.getBoundingBox().expand(2.0 + (slime.getEvolutionStage() * 0.5));
            owner = (Player) slime.getOwner();
            evolutionStage = slime.getEvolutionStage();
        } else {
            return;
        }
        
        if (owner == null || !GamemodeManager.isInGamemode(owner)) {
            return;
        }
        
        // Only eat mobs if evolution stage >= 1
        if (evolutionStage < 1) {
            return;
        }
        
        List<Mob> mobs = world.getEntitiesByClass(Mob.class, searchBox, e -> 
            e.isAlive() && !(e instanceof FrogHelperEntity) && !(e instanceof SlimeHelperEntity) && e.getHealth() <= 10
        );
        
        for (Mob mob : mobs) {
            if (mob.getHealth() <= 5) {
                // Drop ability totem before killing the mob - 50% chance for helpers
                MobAbility ability = MobAbility.getAbilityFromEntity(mob.getType());
                
                if (ability != null && world.random.nextFloat() < 0.5f) {
                    ItemStack abilityDrop = AbilityDropItem.createAbilityDrop(ability.getId());
                    if (!abilityDrop.isEmpty()) {
                        // Drop the ability totem on the ground at mob's location
                        world.spawnEntity(new ItemEntity(world, mob.getX(), mob.getY(), mob.getZ(), abilityDrop));
                    }
                    
                    if (owner != null) {
                        owner.sendMessage(Component.literal("Your helper ate a ")
                            .formatted(ChatFormatting.GREEN)
                            .append(Component.literal(mob.getName().getString())
                                .formatted(ChatFormatting.YELLOW))
                            .append(Component.literal(" and a totem dropped!")
                                .formatted(ChatFormatting.GREEN)), false);
                    }
                } else if (ability != null) {
                    if (owner != null) {
                        owner.sendMessage(Component.literal("Your helper missed the ")
                            .formatted(ChatFormatting.GRAY)
                            .append(Component.literal(mob.getName().getString())
                                .formatted(ChatFormatting.YELLOW))
                            .append(Component.literal("!").formatted(ChatFormatting.GRAY)), false);
                    }
                }
                
                // Kill the mob to drop items naturally (normal drops + ability totem on ground)
                mob.damage(world.getDamageSources().generic(), Float.MAX_VALUE);
                
                spawnEatParticles(world, mob.getX(), mob.getY(), mob.getZ());
            }
        }
    }
    
    private static void spawnEatParticles(ServerLevel world, double x, double y, double z) {
        ItemStackParticleEffect slimeParticles = new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.SLIME_BALL));
        for (int i = 0; i < 15; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 1.0;
            double offsetY = world.random.nextDouble() * 1.0;
            double offsetZ = (world.random.nextDouble() - 0.5) * 1.0;
            
            world.spawnParticles(slimeParticles,
                x + offsetX, y + offsetY, z + offsetZ,
                1, 0, 0, 0, 0);
        }
    }
}
