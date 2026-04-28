package com.wayacreate.frogslimegamemode.economy;

import com.wayacreate.frogslimegamemode.screen.TradeScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class TradeSession {
    public static final int OFFER_SLOT_REQUESTER = 11;
    public static final int OFFER_SLOT_TARGET = 15;
    public static final int ACCEPT_SLOT_REQUESTER = 13;
    public static final int ACCEPT_SLOT_TARGET = 17;

    private final ServerPlayerEntity requester;
    private final ServerPlayerEntity target;
    private final SimpleInventory inventory = new SimpleInventory(27);
    private boolean requesterAccepted;
    private boolean targetAccepted;
    private boolean closed;

    public TradeSession(ServerPlayerEntity requester, ServerPlayerEntity target) {
        this.requester = requester;
        this.target = target;
        refreshDisplay();
    }

    public SimpleInventory getInventory() {
        return inventory;
    }

    public UUID getRequesterUuid() {
        return requester.getUuid();
    }

    public UUID getTargetUuid() {
        return target.getUuid();
    }

    public boolean isOfferSlot(int slotIndex) {
        return slotIndex == OFFER_SLOT_REQUESTER || slotIndex == OFFER_SLOT_TARGET;
    }

    public boolean isViewerOfferSlot(UUID viewerUuid, int slotIndex) {
        return OFFER_SLOT_REQUESTER == slotIndex && viewerUuid.equals(requester.getUuid())
            || OFFER_SLOT_TARGET == slotIndex && viewerUuid.equals(target.getUuid());
    }

    public boolean isViewerAcceptSlot(UUID viewerUuid, int slotIndex) {
        return ACCEPT_SLOT_REQUESTER == slotIndex && viewerUuid.equals(requester.getUuid())
            || ACCEPT_SLOT_TARGET == slotIndex && viewerUuid.equals(target.getUuid());
    }

    public void resetAccepts() {
        requesterAccepted = false;
        targetAccepted = false;
        refreshDisplay();
    }

    public void toggleAccept(PlayerEntity player) {
        if (closed) {
            return;
        }

        if (player.getUuid().equals(requester.getUuid())) {
            requesterAccepted = !requesterAccepted;
        } else if (player.getUuid().equals(target.getUuid())) {
            targetAccepted = !targetAccepted;
        }

        refreshDisplay();
        syncHandlers();

        if (requesterAccepted && targetAccepted) {
            completeTrade();
        }
    }

    public void onClosed(PlayerEntity player) {
        if (closed) {
            return;
        }

        closed = true;
        returnOfferTo(requester, inventory.removeStack(OFFER_SLOT_REQUESTER));
        returnOfferTo(target, inventory.removeStack(OFFER_SLOT_TARGET));

        requester.sendMessage(Text.literal("Trade cancelled.")
            .formatted(Formatting.YELLOW), false);
        target.sendMessage(Text.literal("Trade cancelled.")
            .formatted(Formatting.YELLOW), false);

        closePartnerScreen(player);
    }

    private void completeTrade() {
        if (closed) {
            return;
        }

        closed = true;

        ItemStack requesterOffer = inventory.removeStack(OFFER_SLOT_REQUESTER);
        ItemStack targetOffer = inventory.removeStack(OFFER_SLOT_TARGET);

        if (!requesterOffer.isEmpty()) {
            target.getInventory().offerOrDrop(requesterOffer);
        }
        if (!targetOffer.isEmpty()) {
            requester.getInventory().offerOrDrop(targetOffer);
        }

        EconomyManager.recordTrade(requester, target);

        requester.sendMessage(Text.literal("Trade completed with " + target.getName().getString() + "!")
            .formatted(Formatting.GREEN), false);
        target.sendMessage(Text.literal("Trade completed with " + requester.getName().getString() + "!")
            .formatted(Formatting.GREEN), false);

        closeScreens();
    }

    public void refreshDisplay() {
        setReadonlySlot(4, infoStack("Trading", "Place your offer, then click your wool to accept.", Items.PAPER, Formatting.AQUA));
        setReadonlySlot(ACCEPT_SLOT_REQUESTER, requesterAccepted
            ? infoStack("Accepted", "Waiting for " + target.getName().getString(), Items.LIME_WOOL, Formatting.GREEN)
            : infoStack("Accept Trade", "Click when your offer is ready.", Items.RED_WOOL, Formatting.RED));
        setReadonlySlot(ACCEPT_SLOT_TARGET, targetAccepted
            ? infoStack("Accepted", "Waiting for " + requester.getName().getString(), Items.LIME_WOOL, Formatting.GREEN)
            : infoStack("Accept Trade", "Click when your offer is ready.", Items.RED_WOOL, Formatting.RED));
    }

    private void setReadonlySlot(int slotIndex, ItemStack stack) {
        inventory.setStack(slotIndex, stack);
    }

    private ItemStack infoStack(String title, String subtitle, net.minecraft.item.Item item, Formatting color) {
        ItemStack stack = new ItemStack(item);
        stack.setCustomName(Text.literal(title).formatted(color, Formatting.BOLD));
        stack.getOrCreateNbt().putString("TradeInfo", subtitle);
        return stack;
    }

    private void returnOfferTo(ServerPlayerEntity player, ItemStack stack) {
        if (!stack.isEmpty()) {
            player.getInventory().offerOrDrop(stack);
        }
    }

    private void closePartnerScreen(PlayerEntity closer) {
        if (!(closer instanceof ServerPlayerEntity)) {
            return;
        }

        if (!closer.getUuid().equals(requester.getUuid()) && requester.currentScreenHandler instanceof TradeScreenHandler) {
            requester.closeHandledScreen();
        }
        if (!closer.getUuid().equals(target.getUuid()) && target.currentScreenHandler instanceof TradeScreenHandler) {
            target.closeHandledScreen();
        }
    }

    private void closeScreens() {
        if (requester.currentScreenHandler instanceof TradeScreenHandler) {
            requester.closeHandledScreen();
        }
        if (target.currentScreenHandler instanceof TradeScreenHandler) {
            target.closeHandledScreen();
        }
    }

    private void syncHandlers() {
        if (requester.currentScreenHandler instanceof TradeScreenHandler) {
            requester.currentScreenHandler.sendContentUpdates();
        }
        if (target.currentScreenHandler instanceof TradeScreenHandler) {
            target.currentScreenHandler.sendContentUpdates();
        }
    }
}
