package com.wayacreate.frogslimegamemode.screen;

import com.wayacreate.frogslimegamemode.economy.ShopItem;
import com.wayacreate.frogslimegamemode.economy.ShopManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopScreenHandler extends ScreenHandler {
    private static final int ROWS = 6;
    private static final int TOP_INVENTORY_SIZE = ROWS * 9;
    private static final int FIRST_PLAYER_SLOT = TOP_INVENTORY_SIZE;

    private final SimpleInventory inventory;
    private final Map<Integer, ShopItem> displayedListings = new HashMap<>();

    public ShopScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(ScreenHandlerType.GENERIC_9X6, syncId);
        this.inventory = new SimpleInventory(TOP_INVENTORY_SIZE);

        buildSlots(playerInventory);
        refreshListings();
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex >= 0 && slotIndex < TOP_INVENTORY_SIZE) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                ShopItem listing = displayedListings.get(slotIndex);
                if (listing != null) {
                    ShopManager.buyItem(serverPlayer, listing);
                }
                refreshListings();
                sendContentUpdates();
            }
            return;
        }

        super.onSlotClick(slotIndex, button, actionType, player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    private void buildSlots(PlayerInventory playerInventory) {
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < 9; column++) {
                int slotIndex = column + row * 9;
                this.addSlot(new DisplaySlot(inventory, slotIndex, 8 + column * 18, 18 + row * 18));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9,
                    8 + column * 18, 140 + row * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 198));
        }
    }

    private void refreshListings() {
        displayedListings.clear();

        for (int i = 0; i < TOP_INVENTORY_SIZE; i++) {
            inventory.setStack(i, ItemStack.EMPTY);
        }

        List<ShopItem> listings = ShopManager.getListings();
        int limit = Math.min(listings.size(), TOP_INVENTORY_SIZE);

        for (int i = 0; i < limit; i++) {
            ShopItem listing = listings.get(i);
            ItemStack displayStack = listing.getItem();
            displayStack.setCustomName(Text.literal("")
                .append(displayStack.getName().copy().formatted(Formatting.WHITE))
                .append(Text.literal(" - " + listing.getPrice() + "c")
                    .formatted(Formatting.GOLD, Formatting.BOLD))
            );

            inventory.setStack(i, displayStack);
            displayedListings.put(i, listing);
        }
    }

    private static final class DisplaySlot extends Slot {
        private DisplaySlot(SimpleInventory inventory, int index, int x, int y) {
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
