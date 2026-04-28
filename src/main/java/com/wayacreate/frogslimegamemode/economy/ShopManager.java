package com.wayacreate.frogslimegamemode.economy;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.text.Text;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ShopManager {
    private static final List<ShopItem> listings = new CopyOnWriteArrayList<>();
    private static final int MAX_LISTINGS_PER_PLAYER = 10;
    
    public static boolean listItem(ServerPlayerEntity seller, ItemStack item, int price) {
        if (price <= 0) {
            seller.sendMessage(Text.literal("Price must be greater than 0!").formatted(Formatting.RED), false);
            return false;
        }
        
        if (item.isEmpty()) {
            seller.sendMessage(Text.literal("Cannot sell empty item!").formatted(Formatting.RED), false);
            return false;
        }
        
        // Count current listings
        long currentListings = listings.stream()
            .filter(l -> l.getSellerUuid().equals(seller.getUuid()))
            .count();
        
        if (currentListings >= MAX_LISTINGS_PER_PLAYER) {
            seller.sendMessage(Text.literal("You can only have " + MAX_LISTINGS_PER_PLAYER + " items listed at once!")
                .formatted(Formatting.RED), false);
            return false;
        }
        
        ItemStack listedStack = item.copy();
        item.setCount(0);

        ShopItem listing = new ShopItem(seller.getUuid(), seller.getName().getString(), listedStack, price);
        listings.add(listing);
        
        seller.sendMessage(Text.literal("Listed ").formatted(Formatting.GREEN)
            .append(listedStack.getName())
            .append(Text.literal(" for ").formatted(Formatting.WHITE))
            .append(Text.literal(price + " coins").formatted(Formatting.GOLD)), false);
        
        return true;
    }
    
    public static boolean buyItem(ServerPlayerEntity buyer, int index) {
        if (index < 0 || index >= listings.size()) {
            buyer.sendMessage(Text.literal("Invalid item!").formatted(Formatting.RED), false);
            return false;
        }
        
        return buyItem(buyer, listings.get(index));
    }

    public static boolean buyItem(ServerPlayerEntity buyer, ShopItem requestedItem) {
        ShopItem item = findListing(requestedItem);
        if (item == null) {
            buyer.sendMessage(Text.literal("That listing is no longer available!").formatted(Formatting.RED), false);
            return false;
        }
        
        // Can't buy your own item
        if (item.getSellerUuid().equals(buyer.getUuid())) {
            buyer.sendMessage(Text.literal("You can't buy your own item!").formatted(Formatting.RED), false);
            return false;
        }
        
        // Check balance
        if (!EconomyManager.removeBalance(buyer, item.getPrice())) {
            buyer.sendMessage(Text.literal("You don't have enough coins!").formatted(Formatting.RED), false);
            return false;
        }
        
        // Give money to seller (if online)
        ServerPlayerEntity seller = buyer.getServer().getPlayerManager().getPlayer(item.getSellerUuid());
        if (seller != null) {
            EconomyManager.addBalance(seller, item.getPrice());
            seller.sendMessage(Text.literal(buyer.getName().getString()).formatted(Formatting.GREEN)
                .append(Text.literal(" bought your ").formatted(Formatting.WHITE))
                .append(item.getItem().getName())
                .append(Text.literal(" for ").formatted(Formatting.WHITE))
                .append(Text.literal(item.getPrice() + " coins!").formatted(Formatting.GOLD)), false);
        }
        
        // Give item to buyer
        buyer.getInventory().offerOrDrop(item.getItem());
        
        // Remove listing
        listings.remove(item);
        
        buyer.sendMessage(Text.literal("Bought ").formatted(Formatting.GREEN)
            .append(item.getItem().getName())
            .append(Text.literal(" for ").formatted(Formatting.WHITE))
            .append(Text.literal(item.getPrice() + " coins!").formatted(Formatting.GOLD)), false);
        
        return true;
    }
    
    public static boolean cancelListing(ServerPlayerEntity seller, int index) {
        if (index < 0 || index >= listings.size()) {
            return false;
        }
        
        return cancelListing(seller, listings.get(index));
    }

    public static boolean cancelListing(ServerPlayerEntity seller, ShopItem requestedItem) {
        ShopItem item = findListing(requestedItem);
        if (item == null) {
            seller.sendMessage(Text.literal("That listing is no longer available!").formatted(Formatting.RED), false);
            return false;
        }
        
        if (!item.getSellerUuid().equals(seller.getUuid())) {
            seller.sendMessage(Text.literal("You can only cancel your own listings!").formatted(Formatting.RED), false);
            return false;
        }
        
        // Return item
        seller.getInventory().offerOrDrop(item.getItem());
        listings.remove(item);
        
        seller.sendMessage(Text.literal("Cancelled listing and returned ").formatted(Formatting.GREEN)
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
