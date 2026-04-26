package com.wayacreate.frogslimegamemode.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MustardHelmetItem extends ArmorItem {
    public MustardHelmetItem(Settings settings) {
        super(ArmorMaterials.GOLD, Type.HELMET, settings);
    }
    
    @Override
    public Text getName(ItemStack stack) {
        return Text.literal("Mustard Helmet")
            .formatted(Formatting.YELLOW, Formatting.BOLD);
    }
    
    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
