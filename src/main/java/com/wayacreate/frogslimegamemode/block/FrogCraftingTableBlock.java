package com.wayacreate.frogslimegamemode.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class FrogCraftingTableBlock extends CraftingTableBlock {
    public FrogCraftingTableBlock(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClient) {
            MenuProvider factory = new SimpleMenuProvider(
                (syncId, playerInventory, playerEntity) -> 
                    new com.wayacreate.frogslimegamemode.screen.FrogCraftingScreenHandler(syncId, playerInventory),
                Component.literal("Frog Crafting Table")
            );
            player.openHandledScreen(factory);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }
}
