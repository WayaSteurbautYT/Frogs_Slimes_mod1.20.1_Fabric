package com.wayacreate.frogslimegamemode.item;

import com.wayacreate.frogslimegamemode.gamemode.ManhuntManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class HunterTrackerItem extends Item {
    public static final String HUNTER_TRACKER_NBT = "HunterTracker";
    
    public HunterTrackerItem(Settings settings) {
        super(settings);
    }
    
    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(Items.COMPASS);
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putBoolean(HUNTER_TRACKER_NBT, true);
        stack.setCustomName(Text.literal("Hunter Tracker").formatted(Formatting.RED, Formatting.BOLD));
        return stack;
    }
    
    @Override
    public Text getName(ItemStack stack) {
        return Text.literal("Hunter Tracker").formatted(Formatting.RED, Formatting.BOLD);
    }
    
    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer) {
            if (ManhuntManager.isHunter(serverPlayer)) {
                ManhuntManager.useHunterTrackAbility(serverPlayer);
            } else {
                user.sendMessage(Text.literal("Only hunters can use this item!")
                    .formatted(Formatting.RED), true);
            }
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
