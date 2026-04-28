package com.wayacreate.frogslimegamemode.economy;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import com.wayacreate.frogslimegamemode.screen.TradeScreenHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TradeManager {
    private static final Map<UUID, TradeRequest> pendingRequests = new ConcurrentHashMap<>();
    private static final Set<UUID> disabledTrading = ConcurrentHashMap.newKeySet();
    
    public static boolean sendTradeRequest(ServerPlayerEntity requester, ServerPlayerEntity target, 
                                          ItemStack offerItem, int offerCoins) {
        // Check if trading is disabled for target
        if (disabledTrading.contains(target.getUuid())) {
            requester.sendMessage(Text.literal(target.getName().getString() + " has trading disabled!")
                .formatted(Formatting.RED), false);
            return false;
        }
        
        // Check if target already has a pending request from this player
        TradeRequest existing = pendingRequests.get(target.getUuid());
        if (existing != null && existing.getRequesterUuid().equals(requester.getUuid()) && !existing.isExpired()) {
            requester.sendMessage(Text.literal("You already have a pending trade request with this player!")
                .formatted(Formatting.RED), false);
            return false;
        }
        
        // Verify requester has the items/coins
        if (!offerItem.isEmpty()) {
            if (!hasRequiredItems(requester.getInventory(), offerItem)) {
                requester.sendMessage(Text.literal("You don't have the item you're offering!").formatted(Formatting.RED), false);
                return false;
            }
        }
        
        if (offerCoins > 0) {
            if (EconomyManager.getBalance(requester) < offerCoins) {
                requester.sendMessage(Text.literal("You don't have enough coins!").formatted(Formatting.RED), false);
                return false;
            }
        }
        
        // Create request
        TradeRequest request = new TradeRequest(requester, target, offerItem, offerCoins);
        pendingRequests.put(target.getUuid(), request);
        
        // Notify target
        Text requestMsg = Text.literal("").formatted(Formatting.WHITE)
            .append(requester.getName().getString())
            .append(Text.literal(" wants to trade! "))
            .append(Text.literal("[Accept]").formatted(Formatting.GREEN)
                .styled(s -> s.withClickEvent(
                    new net.minecraft.text.ClickEvent(net.minecraft.text.ClickEvent.Action.RUN_COMMAND, 
                        "/frogslime trade accept"))))
            .append(Text.literal(" "))
            .append(Text.literal("[Decline]").formatted(Formatting.RED)
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
        
        target.sendMessage(Text.literal("They are offering: ").formatted(Formatting.YELLOW)
            .append(Text.literal(offerDesc).formatted(Formatting.GOLD)), false);
        
        requester.sendMessage(Text.literal("Trade request sent to ").formatted(Formatting.GREEN)
            .append(target.getName().getString()), false);
        
        return true;
    }
    
    public static boolean acceptTrade(ServerPlayerEntity accepter) {
        TradeRequest request = pendingRequests.get(accepter.getUuid());
        
        if (request == null || request.isExpired()) {
            accepter.sendMessage(Text.literal("No pending trade requests!").formatted(Formatting.RED), false);
            return false;
        }
        
        ServerPlayerEntity requester = accepter.getServer().getPlayerManager().getPlayer(request.getRequesterUuid());
        
        if (requester == null) {
            accepter.sendMessage(Text.literal("The requester is offline!").formatted(Formatting.RED), false);
            pendingRequests.remove(accepter.getUuid());
            return false;
        }
        
        // Verify requester still has items/coins
        if (!request.getOfferItem().isEmpty()) {
            if (!hasRequiredItems(requester.getInventory(), request.getOfferItem())) {
                accepter.sendMessage(Text.literal(request.getRequesterName() + " no longer has the item!").formatted(Formatting.RED), false);
                requester.sendMessage(Text.literal("Trade failed: You no longer have the item!").formatted(Formatting.RED), false);
                pendingRequests.remove(accepter.getUuid());
                return false;
            }
        }
        
        if (request.getOfferCoins() > 0) {
            if (EconomyManager.getBalance(requester) < request.getOfferCoins()) {
                accepter.sendMessage(Text.literal(request.getRequesterName() + " doesn't have enough coins!").formatted(Formatting.RED), false);
                requester.sendMessage(Text.literal("Trade failed: You don't have enough coins!").formatted(Formatting.RED), false);
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
                accepter.sendMessage(Text.literal(request.getRequesterName() + " no longer has the item!").formatted(Formatting.RED), false);
                requester.sendMessage(Text.literal("Trade failed: You no longer have the item!").formatted(Formatting.RED), false);
                pendingRequests.remove(accepter.getUuid());
                return false;
            }
            accepter.getInventory().offerOrDrop(request.getOfferItem().copy());
        }
        
        ItemStack accepterOffer = accepter.getMainHandStack().copy();
        if (!accepterOffer.isEmpty()) {
            if (!removeMatchingItems(accepter.getInventory(), accepterOffer)) {
                accepter.sendMessage(Text.literal("Trade failed: your offered item changed before confirmation.")
                    .formatted(Formatting.RED), false);
                requester.sendMessage(Text.literal("Trade failed: " + accepter.getName().getString() + "'s offered item changed.")
                    .formatted(Formatting.RED), false);
                pendingRequests.remove(accepter.getUuid());
                return false;
            }
            requester.getInventory().offerOrDrop(accepterOffer);
        }
        
        // Notify both
        accepter.sendMessage(Text.literal("Trade accepted with ").formatted(Formatting.GREEN)
            .append(requester.getName().getString()), false);
        requester.sendMessage(Text.literal("Trade accepted with ").formatted(Formatting.GREEN)
            .append(accepter.getName().getString()), false);

        EconomyManager.recordTrade(requester, accepter);
        
        pendingRequests.remove(accepter.getUuid());
        return true;
    }
    
    public static boolean declineTrade(ServerPlayerEntity decliner) {
        TradeRequest request = pendingRequests.get(decliner.getUuid());
        
        if (request == null || request.isExpired()) {
            decliner.sendMessage(Text.literal("No pending trade requests!").formatted(Formatting.RED), false);
            return false;
        }
        
        ServerPlayerEntity requester = decliner.getServer().getPlayerManager().getPlayer(request.getRequesterUuid());
        
        decliner.sendMessage(Text.literal("Trade declined").formatted(Formatting.RED), false);
        if (requester != null) {
            requester.sendMessage(Text.literal(decliner.getName().getString() + " declined your trade request")
                .formatted(Formatting.RED), false);
        }
        
        pendingRequests.remove(decliner.getUuid());
        return true;
    }
    
    public static void toggleTrading(ServerPlayerEntity player) {
        if (disabledTrading.contains(player.getUuid())) {
            disabledTrading.remove(player.getUuid());
            player.sendMessage(Text.literal("Trading enabled!").formatted(Formatting.GREEN), false);
        } else {
            disabledTrading.add(player.getUuid());
            player.sendMessage(Text.literal("Trading disabled!").formatted(Formatting.RED), false);
        }
    }
    
    public static boolean isTradingEnabled(UUID playerUuid) {
        return !disabledTrading.contains(playerUuid);
    }

    public static void openTradeGui(ServerPlayerEntity requester, ServerPlayerEntity target) {
        TradeSession session = new TradeSession(requester, target);

        requester.openHandledScreen(new SimpleNamedScreenHandlerFactory(
            (syncId, playerInventory, ignored) -> new TradeScreenHandler(syncId, playerInventory, session, requester.getUuid()),
            Text.literal("Trade: " + target.getName().getString())
        ));
        target.openHandledScreen(new SimpleNamedScreenHandlerFactory(
            (syncId, playerInventory, ignored) -> new TradeScreenHandler(syncId, playerInventory, session, target.getUuid()),
            Text.literal("Trade: " + requester.getName().getString())
        ));

        requester.sendMessage(Text.literal("Opened trade with " + target.getName().getString() + ".")
            .formatted(Formatting.GREEN), false);
        target.sendMessage(Text.literal(requester.getName().getString() + " opened a trade with you.")
            .formatted(Formatting.GREEN), false);
    }

    private static boolean hasRequiredItems(PlayerInventory inventory, ItemStack template) {
        return countMatchingItems(inventory, template) >= template.getCount();
    }

    private static int countMatchingItems(PlayerInventory inventory, ItemStack template) {
        int count = 0;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (canStackTogether(stack, template)) {
                count += stack.getCount();
            }
        }

        return count;
    }

    private static boolean removeMatchingItems(PlayerInventory inventory, ItemStack template) {
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
