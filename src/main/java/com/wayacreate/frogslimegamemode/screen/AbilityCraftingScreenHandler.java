package com.wayacreate.frogslimegamemode.screen;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;

public class AbilityCraftingScreenHandler extends AnvilMenu {
    public AbilityCraftingScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, ContainerLevelAccess.EMPTY);
    }

    public AbilityCraftingScreenHandler(int syncId, Inventory playerInventory, ContainerLevelAccess context) {
        super(syncId, playerInventory, context);
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
