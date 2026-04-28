package com.wayacreate.frogslimegamemode.item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class MustardHelmetItem extends ArmorItem {
    public MustardHelmetItem(Properties settings) {
        super(ArmorMaterials.GOLD, Type.HELMET, settings);
    }
    
    @Override
    public Component getName(ItemStack stack) {
        return Component.literal("Mustard Helmet")
            .formatted(ChatFormatting.YELLOW, ChatFormatting.BOLD);
    }
    
    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
