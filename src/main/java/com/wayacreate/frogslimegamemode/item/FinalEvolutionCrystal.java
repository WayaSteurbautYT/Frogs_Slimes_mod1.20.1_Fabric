package com.wayacreate.frogslimegamemode.item;

import com.wayacreate.frogslimegamemode.achievements.AchievementManager;
import com.wayacreate.frogslimegamemode.entity.SlimeHelperEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;

public class FinalEvolutionCrystal extends Item {
    public FinalEvolutionCrystal(Properties settings) {
        super(settings);
    }
    
    @Override
    public InteractionResult useOnEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
        if (!user.getWorld().isClient) {
            if (entity instanceof SlimeHelperEntity slime && slime.isOwner(user)) {
                if (!slime.isFinalForm()) {
                    slime.unlockFinalForm();
                    stack.decrement(1);
                    if (user instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                        AchievementManager.unlockAchievement(serverPlayer, "final_form");
                    }
                    user.sendMessage(Component.literal("THE FINAL EVOLUTION HAS BEGUN...")
                        .formatted(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false);
                    return InteractionResult.SUCCESS;
                } else {
                    user.sendMessage(Component.literal("This slime has already reached its final form...")
                        .formatted(ChatFormatting.DARK_GRAY), false);
                    return InteractionResult.FAIL;
                }
            } else {
                user.sendMessage(Component.literal("This crystal can only be used on your slime helper!")
                    .formatted(ChatFormatting.RED), false);
            }
        }
        return InteractionResult.PASS;
    }
}
