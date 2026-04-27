package com.wayacreate.frogslimegamemode.eating;

import com.wayacreate.frogslimegamemode.entity.FrogHelperEntity;
import com.wayacreate.frogslimegamemode.entity.SlimeHelperEntity;
import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import com.wayacreate.frogslimegamemode.item.AbilityDropItem;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;

import java.util.List;

public class EatingSystem {
    private static final int TICK_INTERVAL = 10;
    private static final double HELPER_SEARCH_RADIUS = 48.0;
    private static final double PLAYER_EAT_RADIUS = 2.5;

    public static void tick(MinecraftServer server) {
        if (server.getTicks() % TICK_INTERVAL != 0) {
            return;
        }

        for (ServerWorld world : server.getWorlds()) {
            for (ServerPlayerEntity player : world.getPlayers()) {
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
    
    private static void playerEatNearbyMobs(ServerPlayerEntity player, ServerWorld world) {
        Box searchBox = player.getBoundingBox().expand(PLAYER_EAT_RADIUS);
        
        List<MobEntity> mobs = world.getEntitiesByClass(MobEntity.class, searchBox, e -> 
            e.isAlive() && !(e instanceof FrogHelperEntity) && !(e instanceof SlimeHelperEntity) && e.getHealth() <= 8
        );
        
        for (MobEntity mob : mobs) {
            if (mob.getHealth() <= 4) {
                // Send tongue animation packet to client
                com.wayacreate.frogslimegamemode.network.ModNetworking.sendPlayerTongueAnimation(player, mob.getId());
                
                // Kill the mob to drop items naturally
                mob.damage(world.getDamageSources().generic(), Float.MAX_VALUE);
                
                // Give ability drop to player
                MobAbility ability = MobAbility.getAbilityFromEntity(mob.getType());
                
                if (ability != null) {
                    // 40% chance to drop ability (slower progression)
                    if (world.random.nextFloat() < 0.4f) {
                        ItemStack abilityDrop = AbilityDropItem.createAbilityDrop(ability.getId());
                        if (!abilityDrop.isEmpty()) {
                            // Try to add to hotbar first, then main inventory
                            if (!player.getInventory().insertStack(abilityDrop)) {
                                // Drop on ground if inventory full
                                world.spawnEntity(new ItemEntity(world, player.getX(), player.getY(), player.getZ(), abilityDrop));
                            }
                        
                        player.sendMessage(Text.literal("You ate a ")
                            .formatted(Formatting.GREEN)
                            .append(Text.literal(mob.getName().getString())
                                .formatted(Formatting.YELLOW))
                            .append(Text.literal(" and gained ")
                                .formatted(Formatting.GREEN))
                            .append(ability.getFormattedName())
                            .append(Text.literal("!").formatted(Formatting.GREEN)), false);
                        }
                    } else {
                        player.sendMessage(Text.literal("The ")
                            .formatted(Formatting.GRAY)
                            .append(Text.literal(mob.getName().getString())
                                .formatted(Formatting.YELLOW))
                            .append(Text.literal(" was too slippery to catch!")
                                .formatted(Formatting.GRAY)), false);
                    }
                }
                
                spawnEatParticles(world, mob.getX(), mob.getY(), mob.getZ());
            }
        }
    }
    
    private static void collectNearbyItems(Object helper, ServerWorld world) {
        Box searchBox;
        PlayerEntity owner = null;
        
        if (helper instanceof FrogHelperEntity frog) {
            searchBox = frog.getBoundingBox().expand(3.0);
            owner = (PlayerEntity) frog.getOwner();
        } else if (helper instanceof SlimeHelperEntity slime) {
            searchBox = slime.getBoundingBox().expand(3.0);
            owner = (PlayerEntity) slime.getOwner();
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
    
    private static void eatNearbyMobs(Object helper, ServerWorld world) {
        Box searchBox;
        PlayerEntity owner = null;
        int evolutionStage = 0;
        
        if (helper instanceof FrogHelperEntity frog) {
            searchBox = frog.getBoundingBox().expand(2.0 + (frog.getEvolutionStage() * 0.5));
            owner = (PlayerEntity) frog.getOwner();
            evolutionStage = frog.getEvolutionStage();
        } else if (helper instanceof SlimeHelperEntity slime) {
            searchBox = slime.getBoundingBox().expand(2.0 + (slime.getEvolutionStage() * 0.5));
            owner = (PlayerEntity) slime.getOwner();
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
        
        List<MobEntity> mobs = world.getEntitiesByClass(MobEntity.class, searchBox, e -> 
            e.isAlive() && !(e instanceof FrogHelperEntity) && !(e instanceof SlimeHelperEntity) && e.getHealth() <= 10
        );
        
        for (MobEntity mob : mobs) {
            if (mob.getHealth() <= 5) {
                // Kill the mob to drop items naturally
                mob.damage(world.getDamageSources().generic(), Float.MAX_VALUE);
                
                // Eat the mob (give ability) - 50% chance for helpers
                MobAbility ability = MobAbility.getAbilityFromEntity(mob.getType());
                
                if (ability != null && world.random.nextFloat() < 0.5f) {
                    if (helper instanceof FrogHelperEntity frog) {
                        frog.addAbility(ability);
                        if (owner != null) {
                            owner.sendMessage(Text.literal("Your frog ate a ")
                                .formatted(Formatting.GREEN)
                                .append(Text.literal(mob.getName().getString())
                                    .formatted(Formatting.YELLOW))
                                .append(Text.literal(" and gained ")
                                    .formatted(Formatting.GREEN))
                                .append(ability.getFormattedName())
                                .append(Text.literal("!").formatted(Formatting.GREEN)), false);
                        }
                    } else if (helper instanceof SlimeHelperEntity slime) {
                        slime.addAbility(ability);
                        if (owner != null) {
                            owner.sendMessage(Text.literal("Your slime ate a ")
                                .formatted(Formatting.GREEN)
                                .append(Text.literal(mob.getName().getString())
                                    .formatted(Formatting.YELLOW))
                                .append(Text.literal(" and gained ")
                                    .formatted(Formatting.GREEN))
                                .append(ability.getFormattedName())
                                .append(Text.literal("!").formatted(Formatting.GREEN)), false);
                        }
                    }
                } else if (ability != null) {
                    if (owner != null) {
                        owner.sendMessage(Text.literal("Your helper missed the ")
                            .formatted(Formatting.GRAY)
                            .append(Text.literal(mob.getName().getString())
                                .formatted(Formatting.YELLOW))
                            .append(Text.literal("!").formatted(Formatting.GRAY)), false);
                    }
                }
                
                spawnEatParticles(world, mob.getX(), mob.getY(), mob.getZ());
            }
        }
    }
    
    private static void spawnEatParticles(ServerWorld world, double x, double y, double z) {
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
