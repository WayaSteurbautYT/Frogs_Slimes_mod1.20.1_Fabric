package com.wayacreate.frogslimegamemode.item;

import com.wayacreate.frogslimegamemode.gamemode.ManhuntManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.level.Level;

public class EscapePearlItem extends Item {
    public static final String ESCAPE_PEARL_NBT = "EscapePearl";
    
    public EscapePearlItem(Properties settings) {
        super(settings);
    }
    
    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(Items.ENDER_PEARL);
        CompoundTag nbt = stack.getOrCreateNbt();
        nbt.putBoolean(ESCAPE_PEARL_NBT, true);
        stack.setCustomName(Component.literal("Escape Pearl").formatted(ChatFormatting.AQUA, ChatFormatting.BOLD));
        return stack;
    }
    
    @Override
    public Component getName(ItemStack stack) {
        return Component.literal("Escape Pearl").formatted(ChatFormatting.AQUA, ChatFormatting.BOLD);
    }
    
    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        if (!world.isClient && user instanceof ServerPlayer serverPlayer) {
            if (ManhuntManager.isSpeedrunner(serverPlayer)) {
                ManhuntManager.useSpeedrunnerEscapeAbility(serverPlayer);
                if (!user.getAbilities().creativeMode) {
                    user.getStackInHand(hand).decrement(1);
                }
            } else {
                user.sendMessage(Component.literal("Only speedrunners can use this item!")
                    .formatted(ChatFormatting.RED), true);
            }
        }
        return InteractionResultHolder.success(user.getStackInHand(hand));
    }
}
