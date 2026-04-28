package com.wayacreate.frogslimegamemode.economy;

import com.wayacreate.frogslimegamemode.screen.TradeScreenHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.UUID;

public class TradeSession {
    public static final int OFFER_SLOT_REQUESTER = 11;
    public static final int OFFER_SLOT_TARGET = 15;
    public static final int ACCEPT_SLOT_REQUESTER = 13;
    public static final int ACCEPT_SLOT_TARGET = 17;

    private final ServerPlayer requester;
    private final ServerPlayer target;
    private final SimpleContainer inventory = new SimpleContainer(27);
    private boolean requesterAccepted;
    private boolean targetAccepted;
    private boolean closed;

    public TradeSession(ServerPlayer requester, ServerPlayer target) {
        this.requester = requester;
        this.target = target;
        refreshDisplay();
    }

    public SimpleContainer getInventory() {
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

    public void toggleAccept(Player player) {
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

    public void onClosed(Player player) {
        if (closed) {
            return;
        }

        closed = true;
        returnOfferTo(requester, inventory.removeStack(OFFER_SLOT_REQUESTER));
        returnOfferTo(target, inventory.removeStack(OFFER_SLOT_TARGET));

        requester.sendMessage(Component.literal("Trade cancelled.")
            .formatted(ChatFormatting.YELLOW), false);
        target.sendMessage(Component.literal("Trade cancelled.")
            .formatted(ChatFormatting.YELLOW), false);

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

        requester.sendMessage(Component.literal("Trade completed with " + target.getName().getString() + "!")
            .formatted(ChatFormatting.GREEN), false);
        target.sendMessage(Component.literal("Trade completed with " + requester.getName().getString() + "!")
            .formatted(ChatFormatting.GREEN), false);

        closeScreens();
    }

    public void refreshDisplay() {
        setReadonlySlot(4, infoStack("Trading", "Place your offer, then click your wool to accept.", Items.PAPER, ChatFormatting.AQUA));
        setReadonlySlot(ACCEPT_SLOT_REQUESTER, requesterAccepted
            ? infoStack("Accepted", "Waiting for " + target.getName().getString(), Items.LIME_WOOL, ChatFormatting.GREEN)
            : infoStack("Accept Trade", "Click when your offer is ready.", Items.RED_WOOL, ChatFormatting.RED));
        setReadonlySlot(ACCEPT_SLOT_TARGET, targetAccepted
            ? infoStack("Accepted", "Waiting for " + requester.getName().getString(), Items.LIME_WOOL, ChatFormatting.GREEN)
            : infoStack("Accept Trade", "Click when your offer is ready.", Items.RED_WOOL, ChatFormatting.RED));
    }

    private void setReadonlySlot(int slotIndex, ItemStack stack) {
        inventory.setStack(slotIndex, stack);
    }

    private ItemStack infoStack(String title, String subtitle, net.minecraft.world.item.Item item, ChatFormatting color) {
        ItemStack stack = new ItemStack(item);
        stack.setCustomName(Component.literal(title).formatted(color, ChatFormatting.BOLD));
        stack.getOrCreateNbt().putString("TradeInfo", subtitle);
        return stack;
    }

    private void returnOfferTo(ServerPlayer player, ItemStack stack) {
        if (!stack.isEmpty()) {
            player.getInventory().offerOrDrop(stack);
        }
    }

    private void closePartnerScreen(Player closer) {
        if (!(closer instanceof ServerPlayer)) {
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
