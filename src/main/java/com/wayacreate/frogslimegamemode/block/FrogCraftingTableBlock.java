package com.wayacreate.frogslimegamemode.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FrogCraftingTableBlock extends CraftingTableBlock {
    public FrogCraftingTableBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory factory = new SimpleNamedScreenHandlerFactory(
                (syncId, playerInventory, playerEntity) -> 
                    new com.wayacreate.frogslimegamemode.screen.FrogCraftingScreenHandler(syncId, playerInventory),
                Text.literal("Frog Crafting Table")
            );
            player.openHandledScreen(factory);
            return ActionResult.SUCCESS;
        }
        return ActionResult.CONSUME;
    }
}
