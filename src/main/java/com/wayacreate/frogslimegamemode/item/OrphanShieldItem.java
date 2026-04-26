package com.wayacreate.frogslimegamemode.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;

import java.util.UUID;

public class OrphanShieldItem extends ShieldItem {
    public static final String ORPHAN_SHIELD_NBT = "OrphanShield";
    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150");
    
    public OrphanShieldItem(Settings settings) {
        super(settings);
    }
    
    public static boolean isOrphanShield(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (!stack.isOf(ModItems.ORPHAN_SHIELD)) return false;
        NbtCompound nbt = stack.getNbt();
        return nbt != null && nbt.getBoolean(ORPHAN_SHIELD_NBT);
    }
    
    @Override
    public ItemStack getDefaultStack() {
        ItemStack shield = new ItemStack(this);
        NbtCompound nbt = shield.getOrCreateNbt();
        nbt.putBoolean(ORPHAN_SHIELD_NBT, true);
        shield.setCustomName(Text.literal("Orphan Shield").formatted(Formatting.DARK_GRAY, Formatting.BOLD));
        return shield;
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
    
    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.RARE;
    }
    
    @Override
    public void inventoryTick(ItemStack stack, net.minecraft.world.World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (!isOrphanShield(stack)) return;
        if (!(entity instanceof net.minecraft.entity.LivingEntity living)) return;
        
        // Add armor modifier when held or equipped
        if (selected || living.getEquippedStack(EquipmentSlot.OFFHAND).equals(stack)) {
            EntityAttributeModifier armorModifier = new EntityAttributeModifier(
                ARMOR_MODIFIER_UUID,
                "Orphan Shield Armor",
                10.0,
                EntityAttributeModifier.Operation.ADDITION
            );
            if (!living.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).hasModifier(armorModifier)) {
                living.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).addPersistentModifier(armorModifier);
            }
        } else {
            // Remove modifier when not held
            for (EntityAttributeModifier modifier : living.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).getModifiers(EntityAttributeModifier.Operation.ADDITION)) {
                if (modifier.getId().equals(ARMOR_MODIFIER_UUID)) {
                    living.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).removeModifier(modifier);
                    break;
                }
            }
        }
    }
}
