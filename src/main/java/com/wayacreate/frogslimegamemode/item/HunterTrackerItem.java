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

public class HunterTrackerItem extends Item {
    public static final String HUNTER_TRACKER_NBT = "HunterTracker";
    
    public HunterTrackerItem(Properties settings) {
        super(settings);
    }
    
    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(Items.COMPASS);
        CompoundTag nbt = stack.getOrCreateNbt();
        nbt.putBoolean(HUNTER_TRACKER_NBT, true);
        stack.setCustomName(Component.literal("Hunter Tracker").formatted(ChatFormatting.RED, ChatFormatting.BOLD));
        return stack;
    }
    
    @Override
    public Component getName(ItemStack stack) {
        return Component.literal("Hunter Tracker").formatted(ChatFormatting.RED, ChatFormatting.BOLD);
    }
    
    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        if (!world.isClient && user instanceof ServerPlayer serverPlayer) {
            if (ManhuntManager.isHunter(serverPlayer)) {
                if (!ManhuntManager.useContextualAbility(serverPlayer)) {
                    ManhuntManager.useHunterTrackAbility(serverPlayer);
                }
            } else {
                user.sendMessage(Component.literal("Only hunters can use this item!")
                    .formatted(ChatFormatting.RED), true);
            }
        }
        return InteractionResultHolder.success(user.getStackInHand(hand));
    }
}
