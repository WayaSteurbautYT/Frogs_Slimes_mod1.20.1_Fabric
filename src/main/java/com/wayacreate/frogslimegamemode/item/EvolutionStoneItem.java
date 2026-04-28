package com.wayacreate.frogslimegamemode.item;

import com.wayacreate.frogslimegamemode.entity.FrogHelperEntity;
import com.wayacreate.frogslimegamemode.entity.SlimeHelperEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;

public class EvolutionStoneItem extends Item {
    public EvolutionStoneItem(Properties settings) {
        super(settings);
    }
    
    @Override
    public InteractionResult useOnEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
        if (!user.getWorld().isClient) {
            if (entity instanceof FrogHelperEntity frog && frog.isOwner(user)) {
                if (frog.getEvolutionStage() < 3) {
                    frog.evolve();
                    stack.decrement(1);
                    user.sendMessage(Component.literal("Evolution stone used! Your frog helper evolved!")
                        .formatted(ChatFormatting.GREEN), false);
                    return InteractionResult.SUCCESS;
                } else {
                    user.sendMessage(Component.literal("Your frog helper is already at max evolution!")
                        .formatted(ChatFormatting.RED), false);
                    return InteractionResult.FAIL;
                }
            } else if (entity instanceof SlimeHelperEntity slime && slime.isOwner(user)) {
                if (slime.getEvolutionStage() < 3) {
                    slime.evolve();
                    stack.decrement(1);
                    user.sendMessage(Component.literal("Evolution stone used! Your slime helper evolved!")
                        .formatted(ChatFormatting.GREEN), false);
                    return InteractionResult.SUCCESS;
                } else {
                    user.sendMessage(Component.literal("Your slime helper is already at max evolution!")
                        .formatted(ChatFormatting.RED), false);
                    user.sendMessage(Component.literal("(Try defeating the Ender Dragon for something special...)")
                        .formatted(ChatFormatting.GRAY, ChatFormatting.ITALIC), false);
                    return InteractionResult.FAIL;
                }
            }
        }
        return InteractionResult.PASS;
    }
}