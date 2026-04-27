package com.wayacreate.frogslimegamemode.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;

public class FrogCraftingScreenHandler extends CraftingScreenHandler {
    private final CraftingInventory input;
    private final CraftingResultInventory result;
    private final SimpleInventory inventory;

    public FrogCraftingScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public FrogCraftingScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(syncId, playerInventory, context);
        this.input = new CraftingInventory(this, 3, 3);
        this.result = new CraftingResultInventory();
        this.inventory = new SimpleInventory(9);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slotObj = this.slots.get(slot);
        if (slotObj != null && slotObj.hasStack()) {
            ItemStack itemStack2 = slotObj.getStack();
            itemStack = itemStack2.copy();
            if (slot < 9) {
                if (!this.insertItem(itemStack2, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 0, 9, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slotObj.setStack(ItemStack.EMPTY);
            } else {
                slotObj.markDirty();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slotObj.onTakeItem(player, itemStack2);
        }
        return itemStack;
    }

    public CraftingInventory getInput() {
        return input;
    }

    public CraftingResultInventory getResult() {
        return result;
    }

    public SimpleInventory getInventory() {
        return inventory;
    }
}
