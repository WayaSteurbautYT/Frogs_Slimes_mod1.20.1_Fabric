package com.wayacreate.frogslimegamemode.mixin;

import com.wayacreate.frogslimegamemode.entity.SlimeHelperEntity;
import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import com.wayacreate.frogslimegamemode.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EnderDragonEntity.class)
public abstract class EnderDragonEntityMixin extends MobEntity {
    protected EnderDragonEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }
    
    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void onTickMovement(CallbackInfo ci) {
        if (!this.getWorld().isClient && this.getHealth() <= 0 && !this.isRemoved()) {
            List<PlayerEntity> nearbyPlayers = this.getWorld().getEntitiesByClass(
                PlayerEntity.class,
                this.getBoundingBox().expand(100),
                player -> GamemodeManager.isInGamemode(player)
            );
            
            for (PlayerEntity player : nearbyPlayers) {
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    GamemodeManager.triggerEnding(serverPlayer, true);
                    
                    List<SlimeHelperEntity> slimes = this.getWorld().getEntitiesByClass(
                        SlimeHelperEntity.class,
                        this.getBoundingBox().expand(100),
                        slime -> slime.isOwner(player)
                    );
                    
                    if (!slimes.isEmpty()) {
                        SlimeHelperEntity slime = slimes.get(0);
                        
                        // Schedule the final evolution after 3 seconds (60 ticks) using a simple delay
                        this.getWorld().getServer().execute(() -> {
                            new java.util.Timer().schedule(
                                new java.util.TimerTask() {
                                    @Override
                                    public void run() {
                                        if (slime.isAlive() && !slime.getWorld().isClient) {
                                            slime.getWorld().getServer().execute(() -> {
                                                slime.unlockFinalForm();
                                                
                                                if (!player.getInventory().insertStack(new ItemStack(ModItems.FINAL_EVOLUTION_CRYSTAL))) {
                                                    player.dropItem(new ItemStack(ModItems.FINAL_EVOLUTION_CRYSTAL), false);
                                                }
                                                
                                                player.sendMessage(Text.literal("A mysterious crystal materialized from the dragon's essence!")
                                                    .formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), false);
                                                player.sendMessage(Text.literal("Your slime helper absorbed the dragon's power!")
                                                    .formatted(Formatting.DARK_RED), false);
                                                
                                                if (player instanceof ServerPlayerEntity serverPlayer) {
                                                    GamemodeManager.grantAdvancement(serverPlayer, "frogslimegamemode:final_evolution");
                                                }
                                            });
                                        }
                                    }
                                },
                                3000
                            );
                        });
                    }
                }
            }
        }
    }
}
