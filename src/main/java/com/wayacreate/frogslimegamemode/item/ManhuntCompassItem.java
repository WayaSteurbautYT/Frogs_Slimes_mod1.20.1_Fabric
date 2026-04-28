package com.wayacreate.frogslimegamemode.item;

import com.wayacreate.frogslimegamemode.gamemode.ManhuntManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class ManhuntCompassItem extends Item {
    public static final String MANHUNT_COMPASS_NBT = "ManhuntCompass";
    
    public ManhuntCompassItem(Properties settings) {
        super(settings);
    }
    
    @Override
    public ItemStack getDefaultStack() {
        ItemStack compass = new ItemStack(Items.COMPASS);
        CompoundTag nbt = compass.getOrCreateNbt();
        nbt.putBoolean(MANHUNT_COMPASS_NBT, true);
        compass.setCustomName(Component.literal("Manhunt Compass").formatted(ChatFormatting.RED, ChatFormatting.BOLD));
        return compass;
    }
    
    public static boolean isManhuntCompass(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (!(stack.isOf(Items.COMPASS) || (ModItems.MANHUNT_COMPASS != null && stack.isOf(ModItems.MANHUNT_COMPASS)))) return false;
        CompoundTag nbt = stack.getNbt();
        return stack.isOf(ModItems.MANHUNT_COMPASS) || (nbt != null && nbt.getBoolean(MANHUNT_COMPASS_NBT));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, net.minecraft.world.entity.player.Player user, InteractionHand hand) {
        if (!world.isClient && user instanceof ServerPlayer serverPlayer) {
            if (ManhuntManager.isHunter(serverPlayer)) {
                ManhuntManager.useContextualAbility(serverPlayer);
            } else {
                user.sendMessage(Component.literal("Only hunters can use this item!")
                    .formatted(ChatFormatting.RED), true);
            }
        }
        return InteractionResultHolder.success(user.getStackInHand(hand));
    }
    
    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        if (!isManhuntCompass(stack)) return;
        
        if (!(entity instanceof ServerPlayer player)) return;
        
        if (!world.isClient && world instanceof ServerLevel) {
            // Only update every 20 ticks (1 second) to reduce server load
            if (world.getTime() % 20 == 0) {
                // Use ManhuntManager to get target
                ServerPlayer target = ManhuntManager.getTarget(player);
                
                CompoundTag nbt = stack.getOrCreateNbt();
                
                if (target != null && target.isAlive()) {
                    // Update tooltip with target info
                    stack.setCustomName(Component.literal("Tracking: " + target.getName().getString())
                        .formatted(ChatFormatting.RED, ChatFormatting.BOLD));
                    
                    // Store target position in NBT for compass tracking
                    BlockPos targetPos = new BlockPos(
                        (int) target.getX(),
                        (int) target.getY(),
                        (int) target.getZ()
                    );
                    nbt.putInt("TargetX", targetPos.getX());
                    nbt.putInt("TargetY", targetPos.getY());
                    nbt.putInt("TargetZ", targetPos.getZ());
                    
                    // Send direction message only when selected
                    if (selected) {
                        double distance = player.squaredDistanceTo(target);
                        int distanceBlocks = (int) Math.sqrt(distance);
                        player.sendMessage(Component.literal("Target is " + distanceBlocks + " blocks away")
                            .formatted(ChatFormatting.YELLOW), true);
                    }
                } else {
                    stack.setCustomName(Component.literal("No target")
                        .formatted(ChatFormatting.GRAY));
                    nbt.remove("TargetX");
                    nbt.remove("TargetY");
                    nbt.remove("TargetZ");
                }
            }
        }
        
        super.inventoryTick(stack, world, entity, slot, selected);
    }
}
