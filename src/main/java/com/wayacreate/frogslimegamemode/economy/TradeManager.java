package com.wayacreate.frogslimegamemode.economy;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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
            if (!requester.getInventory().contains(offerItem)) {
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
                        "/trade accept"))))
            .append(Text.literal(" "))
            .append(Text.literal("[Decline]").formatted(Formatting.RED)
                .styled(s -> s.withClickEvent(
                    new net.minecraft.text.ClickEvent(net.minecraft.text.ClickEvent.Action.RUN_COMMAND, 
                        "/trade decline"))));
        
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
            if (!requester.getInventory().contains(request.getOfferItem())) {
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
            // Remove from requester and give to accepter
            requester.getInventory().remove(item -> item.isOf(request.getOfferItem().getItem()) && 
                item.getCount() >= request.getOfferItem().getCount(), 
                request.getOfferItem().getCount(), requester.getInventory());
            accepter.getInventory().offerOrDrop(request.getOfferItem());
        }
        
        // Build what the accepter offered (their held item)
        ItemStack accepterOffer = accepter.getMainHandStack();
        if (!accepterOffer.isEmpty()) {
            accepter.getInventory().removeOne(accepterOffer);
            requester.getInventory().offerOrDrop(accepterOffer);
        }
        
        // Notify both
        accepter.sendMessage(Text.literal("Trade accepted with ").formatted(Formatting.GREEN)
            .append(requester.getName().getString()), false);
        requester.sendMessage(Text.literal("Trade accepted with ").formatted(Formatting.GREEN)
            .append(accepter.getName().getString()), false);
        
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
}
