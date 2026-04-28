package com.wayacreate.frogslimegamemode.mixin;

import com.wayacreate.frogslimegamemode.achievements.AchievementManager;
import com.wayacreate.frogslimegamemode.entity.SlimeHelperEntity;
import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import com.wayacreate.frogslimegamemode.gamemode.ManhuntManager;
import com.wayacreate.frogslimegamemode.item.ModItems;
import com.wayacreate.frogslimegamemode.tasks.TaskManager;
import com.wayacreate.frogslimegamemode.tasks.TaskType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EnderDragon.class)
public abstract class EnderDragonEntityMixin {
    
    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void onTickMovement(CallbackInfo ci) {
        EnderDragon dragon = (EnderDragon) (Object) this;
        if (!dragon.getWorld().isClient && dragon.getHealth() <= 0 && !dragon.isRemoved()) {
            List<Player> nearbyPlayers = dragon.getWorld().getEntitiesByClass(
                Player.class,
                dragon.getBoundingBox().expand(100),
                player -> GamemodeManager.isInGamemode(player)
            );
            
            for (Player player : nearbyPlayers) {
                if (player instanceof ServerPlayer serverPlayer) {
                    GamemodeManager.triggerEnding(serverPlayer, true);
                    TaskManager.completeTask(serverPlayer, TaskType.DEFEAT_FINAL_BOSS);
                    AchievementManager.unlockAchievement(serverPlayer, "dragon_slayer");
                    if (ManhuntManager.isInGame(serverPlayer) && ManhuntManager.isSpeedrunner(serverPlayer)) {
                        ManhuntManager.onSpeedrunnerWin(serverPlayer);
                    }
                    
                    List<SlimeHelperEntity> slimes = dragon.getWorld().getEntitiesByClass(
                        SlimeHelperEntity.class,
                        dragon.getBoundingBox().expand(100),
                        slime -> slime.isOwner(player)
                    );
                    
                    if (!slimes.isEmpty()) {
                        SlimeHelperEntity slime = slimes.get(0);
                        
                        // Schedule the final evolution after 3 seconds (60 ticks) using a simple delay
                        dragon.getWorld().getServer().execute(() -> {
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
                                                
                                                player.sendMessage(Component.literal("A mysterious crystal materialized from the dragon's essence!")
                                                    .formatted(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD), false);
                                                player.sendMessage(Component.literal("Your slime helper absorbed the dragon's power!")
                                                    .formatted(ChatFormatting.DARK_RED), false);
                                                
                                                if (player instanceof ServerPlayer serverPlayer) {
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
