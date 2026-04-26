package com.wayacreate.frogslimegamemode.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class OrphanShieldItem extends Item {
    public static final String ORPHAN_SHIELD_NBT = "OrphanShield";
    
    public OrphanShieldItem(Settings settings) {
        super(settings);
    }
    
    public static ItemStack createOrphanShield() {
        ItemStack shield = new ItemStack(Items.SHIELD);
        NbtCompound nbt = shield.getOrCreateNbt();
        nbt.putBoolean(ORPHAN_SHIELD_NBT, true);
        shield.setCustomName(Text.literal("Orphan Shield").formatted(Formatting.DARK_GRAY, Formatting.BOLD));
        return shield;
    }
    
    public static boolean isOrphanShield(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (!stack.isOf(Items.SHIELD)) return false;
        NbtCompound nbt = stack.getNbt();
        return nbt != null && nbt.getBoolean(ORPHAN_SHIELD_NBT);
    }
    
    @Override
    public Text getName(ItemStack stack) {
        return Text.literal("Orphan Shield")
            .formatted(Formatting.DARK_GRAY, Formatting.BOLD);
    }
    
    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
