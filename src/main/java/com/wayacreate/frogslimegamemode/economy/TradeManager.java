package com.wayacreate.frogslimegamemode.economy;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import com.wayacreate.frogslimegamemode.screen.TradeScreenHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TradeManager {
    private static final Map<UUID, TradeRequest> pendingRequests = new ConcurrentHashMap<>();
    private static final Set<UUID> disabledTrading = ConcurrentHashMap.newKeySet();
    
    public static boolean sendTradeRequest(ServerPlayer requester, ServerPlayer target, 
                                          ItemStack offerItem, int offerCoins) {
        // Check if trading is disabled for target
        if (disabledTrading.contains(target.getUuid())) {
            requester.sendMessage(Component.literal(target.getName().getString() + " has trading disabled!")
                .formatted(ChatFormatting.RED), false);
            return false;
        }
        
        // Check if target already has a pending request from this player
        TradeRequest existing = pendingRequests.get(target.getUuid());
        if (existing != null && existing.getRequesterUuid().equals(requester.getUuid()) && !existing.isExpired()) {
            requester.sendMessage(Component.literal("You already have a pending trade request with this player!")
                .formatted(ChatFormatting.RED), false);
            return false;
        }
        
        // Verify requester has the items/coins
        if (!offerItem.isEmpty()) {
            if (!hasRequiredItems(requester.getInventory(), offerItem)) {
                requester.sendMessage(Component.literal("You don't have the item you're offering!").formatted(ChatFormatting.RED), false);
                return false;
            }
        }
        
        if (offerCoins > 0) {
            if (EconomyManager.getBalance(requester) < offerCoins) {
                requester.sendMessage(Component.literal("You don't have enough coins!").formatted(ChatFormatting.RED), false);
                return false;
            }
        }
        
        // Create request
        TradeRequest request = new TradeRequest(requester, target, offerItem, offerCoins);
        pendingRequests.put(target.getUuid(), request);
        
        // Notify target
        Component requestMsg = Component.literal("").formatted(ChatFormatting.WHITE)
            .append(requester.getName().getString())
            .append(Component.literal(" wants to trade! "))
            .append(Component.literal("[Accept]").formatted(ChatFormatting.GREEN)
                .styled(s -> s.withClickEvent(
                    new net.minecraft.text.ClickEvent(net.minecraft.text.ClickEvent.Action.RUN_COMMAND, 
                        "/frogslime trade accept"))))
            .append(Component.literal(" "))
            .append(Component.literal("[Decline]").formatted(ChatFormatting.RED)
                .styled(s -> s.withClickEvent(
                    new net.minecraft.text.ClickEvent(net.minecraft.text.ClickEvent.Action.RUN_COMMAND, 
                        "/frogslime trade decline"))));
        
        target.sendMessage(requestMsg, false);
        
        String offerDesc = "";
        if (!offerItem.isEmpty()) {
            offerDesc += offerItem.getCount() + " " + offerItem.getName().getString();
        }
        if (offerCoins > 0) {
            if (!offerDesc.isEmpty()) offerDesc += " and ";
            offerDesc += offerCoins + " coins";
        }
        if (offerDesc.isEmpty()) {
            offerDesc = "nothing";
        }
        
        target.sendMessage(Component.literal("They are offering: ").formatted(ChatFormatting.YELLOW)
            .append(Component.literal(offerDesc).formatted(ChatFormatting.GOLD)), false);
        
        requester.sendMessage(Component.literal("Trade request sent to ").formatted(ChatFormatting.GREEN)
            .append(target.getName().getString()), false);
        
        return true;
    }
    
    public static boolean acceptTrade(ServerPlayer accepter) {
        TradeRequest request = pendingRequests.get(accepter.getUuid());
        
        if (request == null || request.isExpired()) {
            accepter.sendMessage(Component.literal("No pending trade requests!").formatted(ChatFormatting.RED), false);
            return false;
        }
        
        ServerPlayer requester = accepter.getServer().getPlayerManager().getPlayer(request.getRequesterUuid());
        
        if (requester == null) {
            accepter.sendMessage(Component.literal("The requester is offline!").formatted(ChatFormatting.RED), false);
            pendingRequests.remove(accepter.getUuid());
            return false;
        }
        
        // Verify requester still has items/coins
        if (!request.getOfferItem().isEmpty()) {
            if (!hasRequiredItems(requester.getInventory(), request.getOfferItem())) {
                accepter.sendMessage(Component.literal(request.getRequesterName() + " no longer has the item!").formatted(ChatFormatting.RED), false);
                requester.sendMessage(Component.literal("Trade failed: You no longer have the item!").formatted(ChatFormatting.RED), false);
                pendingRequests.remove(accepter.getUuid());
                return false;
            }
        }
        
        if (request.getOfferCoins() > 0) {
            if (EconomyManager.getBalance(requester) < request.getOfferCoins()) {
                accepter.sendMessage(Component.literal(request.getRequesterName() + " doesn't have enough coins!").formatted(ChatFormatting.RED), false);
                requester.sendMessage(Component.literal("Trade failed: You don't have enough coins!").formatted(ChatFormatting.RED), false);
                pendingRequests.remove(accepter.getUuid());
                return false;
            }
        }
        
        // Execute trade
        if (request.getOfferCoins() > 0) {
            EconomyManager.removeBalance(requester, request.getOfferCoins());
            EconomyManager.addBalance(accepter, request.getOfferCoins());
        }
        
        if (!request.getOfferItem().isEmpty()) {
            if (!removeMatchingItems(requester.getInventory(), request.getOfferItem())) {
                accepter.sendMessage(Component.literal(request.getRequesterName() + " no longer has the item!").formatted(ChatFormatting.RED), false);
                requester.sendMessage(Component.literal("Trade failed: You no longer have the item!").formatted(ChatFormatting.RED), false);
                pendingRequests.remove(accepter.getUuid());
                return false;
            }
            accepter.getInventory().offerOrDrop(request.getOfferItem().copy());
        }
        
        ItemStack accepterOffer = accepter.getMainHandStack().copy();
        if (!accepterOffer.isEmpty()) {
            if (!removeMatchingItems(accepter.getInventory(), accepterOffer)) {
                accepter.sendMessage(Component.literal("Trade failed: your offered item changed before confirmation.")
                    .formatted(ChatFormatting.RED), false);
                requester.sendMessage(Component.literal("Trade failed: " + accepter.getName().getString() + "'s offered item changed.")
                    .formatted(ChatFormatting.RED), false);
                pendingRequests.remove(accepter.getUuid());
                return false;
            }
            requester.getInventory().offerOrDrop(accepterOffer);
        }
        
        // Notify both
        accepter.sendMessage(Component.literal("Trade accepted with ").formatted(ChatFormatting.GREEN)
            .append(requester.getName().getString()), false);
        requester.sendMessage(Component.literal("Trade accepted with ").formatted(ChatFormatting.GREEN)
            .append(accepter.getName().getString()), false);

        EconomyManager.recordTrade(requester, accepter);
        
        pendingRequests.remove(accepter.getUuid());
        return true;
    }
    
    public static boolean declineTrade(ServerPlayer decliner) {
        TradeRequest request = pendingRequests.get(decliner.getUuid());
        
        if (request == null || request.isExpired()) {
            decliner.sendMessage(Component.literal("No pending trade requests!").formatted(ChatFormatting.RED), false);
            return false;
        }
        
        ServerPlayer requester = decliner.getServer().getPlayerManager().getPlayer(request.getRequesterUuid());
        
        decliner.sendMessage(Component.literal("Trade declined").formatted(ChatFormatting.RED), false);
        if (requester != null) {
            requester.sendMessage(Component.literal(decliner.getName().getString() + " declined your trade request")
                .formatted(ChatFormatting.RED), false);
        }
        
        pendingRequests.remove(decliner.getUuid());
        return true;
    }
    
    public static void toggleTrading(ServerPlayer player) {
        if (disabledTrading.contains(player.getUuid())) {
            disabledTrading.remove(player.getUuid());
            player.sendMessage(Component.literal("Trading enabled!").formatted(ChatFormatting.GREEN), false);
        } else {
            disabledTrading.add(player.getUuid());
            player.sendMessage(Component.literal("Trading disabled!").formatted(ChatFormatting.RED), false);
        }
    }
    
    public static boolean isTradingEnabled(UUID playerUuid) {
        return !disabledTrading.contains(playerUuid);
    }

    public static void openTradeGui(ServerPlayer requester, ServerPlayer target) {
        TradeSession session = new TradeSession(requester, target);

        requester.openHandledScreen(new SimpleMenuProvider(
            (syncId, playerInventory, ignored) -> new TradeScreenHandler(syncId, playerInventory, session, requester.getUuid()),
            Component.literal("Trade: " + target.getName().getString())
        ));
        target.openHandledScreen(new SimpleMenuProvider(
            (syncId, playerInventory, ignored) -> new TradeScreenHandler(syncId, playerInventory, session, target.getUuid()),
            Component.literal("Trade: " + requester.getName().getString())
        ));

        requester.sendMessage(Component.literal("Opened trade with " + target.getName().getString() + ".")
            .formatted(ChatFormatting.GREEN), false);
        target.sendMessage(Component.literal(requester.getName().getString() + " opened a trade with you.")
            .formatted(ChatFormatting.GREEN), false);
    }

    private static boolean hasRequiredItems(Inventory inventory, ItemStack template) {
        return countMatchingItems(inventory, template) >= template.getCount();
    }

    private static int countMatchingItems(Inventory inventory, ItemStack template) {
        int count = 0;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (canStackTogether(stack, template)) {
                count += stack.getCount();
            }
        }

        return count;
    }

    private static boolean removeMatchingItems(Inventory inventory, ItemStack template) {
        int remaining = template.getCount();

        if (countMatchingItems(inventory, template) < remaining) {
            return false;
        }

        for (int i = 0; i < inventory.size() && remaining > 0; i++) {
            ItemStack stack = inventory.getStack(i);
            if (!canStackTogether(stack, template)) {
                continue;
            }

            int removed = Math.min(stack.getCount(), remaining);
            stack.decrement(removed);
            remaining -= removed;
        }

        return remaining == 0;
    }

    private static boolean canStackTogether(ItemStack stack, ItemStack template) {
        return !stack.isEmpty() && ItemStack.canCombine(stack, template);
    }
}
