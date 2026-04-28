package com.wayacreate.frogslimegamemode.screen;

import com.wayacreate.frogslimegamemode.economy.TradeSession;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import java.util.UUID;

public class TradeScreenHandler extends ScreenHandler {
    private static final int TOP_SIZE = 27;

    private final TradeSession session;
    private final UUID viewerUuid;

    public TradeScreenHandler(int syncId, PlayerInventory playerInventory, TradeSession session, UUID viewerUuid) {
        super(ScreenHandlerType.GENERIC_9X3, syncId);
        this.session = session;
        this.viewerUuid = viewerUuid;

        buildSlots(playerInventory, session.getInventory());
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex >= 0 && slotIndex < TOP_SIZE) {
            if (session.isViewerAcceptSlot(viewerUuid, slotIndex)) {
                session.toggleAccept(player);
                return;
            }

            if (!session.isViewerOfferSlot(viewerUuid, slotIndex)) {
                return;
            }

            session.resetAccepts();
            super.onSlotClick(slotIndex, button, actionType, player);
            session.refreshDisplay();
            sendContentUpdates();
            return;
        }

        super.onSlotClick(slotIndex, button, actionType, player);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        session.onClosed(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    private void buildSlots(PlayerInventory playerInventory, Inventory tradeInventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                int slotIndex = column + row * 9;
                Slot slot = session.isOfferSlot(slotIndex)
                    ? new Slot(tradeInventory, slotIndex, 8 + column * 18, 18 + row * 18)
                    : new DisplaySlot(tradeInventory, slotIndex, 8 + column * 18, 18 + row * 18);
                this.addSlot(slot);
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9,
                    8 + column * 18, 86 + row * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 144));
        }
    }

    private static final class DisplaySlot extends Slot {
        private DisplaySlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            return false;
        }
    }
}
