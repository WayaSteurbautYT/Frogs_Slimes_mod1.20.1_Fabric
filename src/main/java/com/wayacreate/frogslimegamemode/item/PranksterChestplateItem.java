package com.wayacreate.frogslimegamemode.item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class PranksterChestplateItem extends ArmorItem {
    public PranksterChestplateItem(Properties settings) {
        super(ArmorMaterials.DIAMOND, Type.CHESTPLATE, settings);
    }
    
    @Override
    public Component getName(ItemStack stack) {
        return Component.literal("Prankster Chestplate")
            .formatted(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD);
    }
    
    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
