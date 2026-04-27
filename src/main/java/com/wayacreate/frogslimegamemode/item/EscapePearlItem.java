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

public class EscapePearlItem extends Item {
    public static final String ESCAPE_PEARL_NBT = "EscapePearl";
    
    public EscapePearlItem(Settings settings) {
        super(settings);
    }
    
    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(Items.ENDER_PEARL);
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putBoolean(ESCAPE_PEARL_NBT, true);
        stack.setCustomName(Text.literal("Escape Pearl").formatted(Formatting.AQUA, Formatting.BOLD));
        return stack;
    }
    
    @Override
    public Text getName(ItemStack stack) {
        return Text.literal("Escape Pearl").formatted(Formatting.AQUA, Formatting.BOLD);
    }
    
    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer) {
            if (ManhuntManager.isSpeedrunner(serverPlayer)) {
                ManhuntManager.useSpeedrunnerEscapeAbility(serverPlayer);
                if (!user.getAbilities().creativeMode) {
                    user.getStackInHand(hand).decrement(1);
                }
            } else {
                user.sendMessage(Text.literal("Only speedrunners can use this item!")
                    .formatted(Formatting.RED), true);
            }
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
