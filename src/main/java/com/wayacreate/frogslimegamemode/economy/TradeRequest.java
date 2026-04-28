package com.wayacreate.frogslimegamemode.economy;

import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class TradeRequest {
    private final UUID requesterUuid;
    private final String requesterName;
    private final UUID targetUuid;
    private final ItemStack offerItem;
    private final int offerCoins;
    private final long requestTime;
    private boolean accepted;
    
    public TradeRequest(ServerPlayer requester, ServerPlayer target, ItemStack offerItem, int offerCoins) {
        this.requesterUuid = requester.getUuid();
        this.requesterName = requester.getName().getString();
        this.targetUuid = target.getUuid();
        this.offerItem = offerItem.isEmpty() ? ItemStack.EMPTY : offerItem.copy();
        this.offerCoins = offerCoins;
        this.requestTime = System.currentTimeMillis();
        this.accepted = false;
    }
    
    public UUID getRequesterUuid() {
        return requesterUuid;
    }
    
    public String getRequesterName() {
        return requesterName;
    }
    
    public UUID getTargetUuid() {
        return targetUuid;
    }
    
    public ItemStack getOfferItem() {
        return offerItem.copy();
    }
    
    public int getOfferCoins() {
        return offerCoins;
    }
    
    public long getRequestTime() {
        return requestTime;
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() - requestTime > 60000; // 60 seconds
    }
    
    public boolean isAccepted() {
        return accepted;
    }
    
    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
