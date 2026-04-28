package com.wayacreate.frogslimegamemode.item;

import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import com.wayacreate.frogslimegamemode.network.ModNetworking;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.level.Level;

public class TaskBookItem extends Item {
    public TaskBookItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        if (!world.isClient && user instanceof ServerPlayer serverPlayer) {
            if (!GamemodeManager.isInGamemode(user)) {
                user.sendMessage(Component.literal("You must be in Frog & Slime Gamemode to use this book!")
                    .formatted(ChatFormatting.RED), false);
                user.sendMessage(Component.literal("Use /frogslime enable to activate the route.")
                    .formatted(ChatFormatting.GRAY), false);
                return InteractionResultHolder.success(user.getStackInHand(hand));
            }

            ModNetworking.openTasksScreen(serverPlayer);
            user.sendMessage(Component.literal("Journey menu opened. Sneak-right-click your guide book if you need the full route again.")
                .formatted(ChatFormatting.YELLOW), true);
        }
        return InteractionResultHolder.success(user.getStackInHand(hand));
    }
}
