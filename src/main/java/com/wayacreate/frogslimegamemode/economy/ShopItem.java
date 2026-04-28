package com.wayacreate.frogslimegamemode.economy;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class ShopItem {
    private final UUID sellerUuid;
    private final String sellerName;
    private final ItemStack item;
    private final int price;
    private final long listedTime;
    
    public ShopItem(UUID sellerUuid, String sellerName, ItemStack item, int price) {
        this.sellerUuid = sellerUuid;
        this.sellerName = sellerName;
        this.item = item.copy();
        this.price = price;
        this.listedTime = System.currentTimeMillis();
    }
    
    public UUID getSellerUuid() {
        return sellerUuid;
    }
    
    public String getSellerName() {
        return sellerName;
    }
    
    public ItemStack getItem() {
        return item.copy();
    }
    
    public int getPrice() {
        return price;
    }
    
    public long getListedTime() {
        return listedTime;
    }
    
    public CompoundTag toNbt() {
        CompoundTag nbt = new CompoundTag();
        nbt.putUuid("seller", sellerUuid);
        nbt.putString("sellerName", sellerName);
        nbt.put("item", item.writeNbt(new CompoundTag()));
        nbt.putInt("price", price);
        nbt.putLong("listedTime", listedTime);
        return nbt;
    }
    
    public static ShopItem fromNbt(CompoundTag nbt) {
        UUID sellerUuid = nbt.getUuid("seller");
        String sellerName = nbt.getString("sellerName");
        ItemStack item = ItemStack.fromNbt(nbt.getCompound("item"));
        int price = nbt.getInt("price");
        ShopItem shopItem = new ShopItem(sellerUuid, sellerName, item, price);
        return shopItem;
    }
}
