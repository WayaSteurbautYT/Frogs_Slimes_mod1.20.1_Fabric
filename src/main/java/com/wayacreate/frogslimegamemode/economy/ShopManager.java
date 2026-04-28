package com.wayacreate.frogslimegamemode.economy;

import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ShopManager {
    private static final List<ShopItem> listings = new CopyOnWriteArrayList<>();
    private static final int MAX_LISTINGS_PER_PLAYER = 10;
    
    public static boolean listItem(ServerPlayer seller, ItemStack item, int price) {
        if (price <= 0) {
            seller.sendMessage(Component.literal("Price must be greater than 0!").formatted(ChatFormatting.RED), false);
            return false;
        }
        
        if (item.isEmpty()) {
            seller.sendMessage(Component.literal("Cannot sell empty item!").formatted(ChatFormatting.RED), false);
            return false;
        }
        
        // Count current listings
        long currentListings = listings.stream()
            .filter(l -> l.getSellerUuid().equals(seller.getUuid()))
            .count();
        
        if (currentListings >= MAX_LISTINGS_PER_PLAYER) {
            seller.sendMessage(Component.literal("You can only have " + MAX_LISTINGS_PER_PLAYER + " items listed at once!")
                .formatted(ChatFormatting.RED), false);
            return false;
        }
        
        ItemStack listedStack = item.copy();
        item.setCount(0);

        ShopItem listing = new ShopItem(seller.getUuid(), seller.getName().getString(), listedStack, price);
        listings.add(listing);
        
        seller.sendMessage(Component.literal("Listed ").formatted(ChatFormatting.GREEN)
            .append(listedStack.getName())
            .append(Component.literal(" for ").formatted(ChatFormatting.WHITE))
            .append(Component.literal(price + " coins").formatted(ChatFormatting.GOLD)), false);
        
        return true;
    }
    
    public static boolean buyItem(ServerPlayer buyer, int index) {
        if (index < 0 || index >= listings.size()) {
            buyer.sendMessage(Component.literal("Invalid item!").formatted(ChatFormatting.RED), false);
            return false;
        }
        
        return buyItem(buyer, listings.get(index));
    }

    public static boolean buyItem(ServerPlayer buyer, ShopItem requestedItem) {
        ShopItem item = findListing(requestedItem);
        if (item == null) {
            buyer.sendMessage(Component.literal("That listing is no longer available!").formatted(ChatFormatting.RED), false);
            return false;
        }
        
        // Can't buy your own item
        if (item.getSellerUuid().equals(buyer.getUuid())) {
            buyer.sendMessage(Component.literal("You can't buy your own item!").formatted(ChatFormatting.RED), false);
            return false;
        }
        
        // Check balance
        if (!EconomyManager.removeBalance(buyer, item.getPrice())) {
            buyer.sendMessage(Component.literal("You don't have enough coins!").formatted(ChatFormatting.RED), false);
            return false;
        }
        
        // Give money to seller (if online)
        ServerPlayer seller = buyer.getServer().getPlayerManager().getPlayer(item.getSellerUuid());
        if (seller != null) {
            EconomyManager.addBalance(seller, item.getPrice());
            seller.sendMessage(Component.literal(buyer.getName().getString()).formatted(ChatFormatting.GREEN)
                .append(Component.literal(" bought your ").formatted(ChatFormatting.WHITE))
                .append(item.getItem().getName())
                .append(Component.literal(" for ").formatted(ChatFormatting.WHITE))
                .append(Component.literal(item.getPrice() + " coins!").formatted(ChatFormatting.GOLD)), false);
        }
        
        // Give item to buyer
        buyer.getInventory().offerOrDrop(item.getItem());
        
        // Remove listing
        listings.remove(item);
        
        buyer.sendMessage(Component.literal("Bought ").formatted(ChatFormatting.GREEN)
            .append(item.getItem().getName())
            .append(Component.literal(" for ").formatted(ChatFormatting.WHITE))
            .append(Component.literal(item.getPrice() + " coins!").formatted(ChatFormatting.GOLD)), false);
        
        return true;
    }
    
    public static boolean cancelListing(ServerPlayer seller, int index) {
        if (index < 0 || index >= listings.size()) {
            return false;
        }
        
        return cancelListing(seller, listings.get(index));
    }

    public static boolean cancelListing(ServerPlayer seller, ShopItem requestedItem) {
        ShopItem item = findListing(requestedItem);
        if (item == null) {
            seller.sendMessage(Component.literal("That listing is no longer available!").formatted(ChatFormatting.RED), false);
            return false;
        }
        
        if (!item.getSellerUuid().equals(seller.getUuid())) {
            seller.sendMessage(Component.literal("You can only cancel your own listings!").formatted(ChatFormatting.RED), false);
            return false;
        }
        
        // Return item
        seller.getInventory().offerOrDrop(item.getItem());
        listings.remove(item);
        
        seller.sendMessage(Component.literal("Cancelled listing and returned ").formatted(ChatFormatting.GREEN)
            .append(item.getItem().getName()), false);
        
        return true;
    }
    
    public static List<ShopItem> getListings() {
        return new ArrayList<>(listings);
    }
    
    public static List<ShopItem> getListingsByPlayer(UUID playerUuid) {
        return listings.stream()
            .filter(l -> l.getSellerUuid().equals(playerUuid))
            .toList();
    }
    
    public static void removeAllListings(UUID playerUuid) {
        listings.removeIf(l -> l.getSellerUuid().equals(playerUuid));
    }

    private static ShopItem findListing(ShopItem requestedItem) {
        for (ShopItem listing : listings) {
            if (!listing.getSellerUuid().equals(requestedItem.getSellerUuid())) {
                continue;
            }
            if (listing.getPrice() != requestedItem.getPrice()) {
                continue;
            }
            if (listing.getListedTime() != requestedItem.getListedTime()) {
                continue;
            }

            ItemStack listingStack = listing.getItem();
            ItemStack requestedStack = requestedItem.getItem();
            if (ItemStack.canCombine(listingStack, requestedStack) && listingStack.getCount() == requestedStack.getCount()) {
                return listing;
            }
        }
        return null;
    }
}
