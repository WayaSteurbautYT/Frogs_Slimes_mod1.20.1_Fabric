package com.wayacreate.frogslimegamemode.item;

import com.wayacreate.frogslimegamemode.gamemode.ManhuntManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.level.Level;

public class HunterNetItem extends Item {
    public HunterNetItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        if (!world.isClient && user instanceof ServerPlayer serverPlayer) {
            if (ManhuntManager.isHunter(serverPlayer)) {
                ManhuntManager.useHunterSlowAbility(serverPlayer);
                if (!user.getAbilities().creativeMode) {
                    user.getStackInHand(hand).decrement(1);
                }
            } else {
                user.sendMessage(Component.literal("Only hunters can use this item!")
                    .formatted(ChatFormatting.RED), true);
            }
        }
        return InteractionResultHolder.success(user.getStackInHand(hand));
    }
}
