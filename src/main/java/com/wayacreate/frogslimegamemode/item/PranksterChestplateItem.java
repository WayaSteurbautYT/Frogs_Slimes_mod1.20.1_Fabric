package com.wayacreate.frogslimegamemode.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class PranksterChestplateItem extends ArmorItem {
    public PranksterChestplateItem(Settings settings) {
        super(ArmorMaterials.DIAMOND, Type.CHESTPLATE, settings);
    }
    
    @Override
    public Text getName(ItemStack stack) {
        return Text.literal("Prankster Chestplate")
            .formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD);
    }
    
    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
