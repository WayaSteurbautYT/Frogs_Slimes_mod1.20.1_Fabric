package com.wayacreate.frogslimegamemode.item;

import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import com.wayacreate.frogslimegamemode.network.ModNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class TaskBookItem extends Item {
    public TaskBookItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer) {
            if (!GamemodeManager.isInGamemode(user)) {
                user.sendMessage(Text.literal("You must be in Frog & Slime Gamemode to use this book!")
                    .formatted(Formatting.RED), false);
                user.sendMessage(Text.literal("Use /frogslime enable to activate the route.")
                    .formatted(Formatting.GRAY), false);
                return TypedActionResult.success(user.getStackInHand(hand));
            }

            ModNetworking.openTasksScreen(serverPlayer);
            user.sendMessage(Text.literal("Journey menu opened. Sneak-right-click your guide book if you need the full route again.")
                .formatted(Formatting.YELLOW), true);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
