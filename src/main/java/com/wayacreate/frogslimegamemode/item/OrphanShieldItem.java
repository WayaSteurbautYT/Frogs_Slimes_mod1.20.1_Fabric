package com.wayacreate.frogslimegamemode.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.UUID;

public class OrphanShieldItem extends ShieldItem {
    public static final String ORPHAN_SHIELD_NBT = "OrphanShield";
    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150");
    private static final UUID DAMAGE_REDUCTION_UUID = UUID.fromString("7E0292F2-9434-48D5-A629-615F8EEDAF29");
    
    public OrphanShieldItem(Properties settings) {
        super(settings);
    }
    
    public static boolean isOrphanShield(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        CompoundTag nbt = stack.getNbt();
        return nbt != null && nbt.getBoolean(ORPHAN_SHIELD_NBT);
    }
    
    @Override
    public ItemStack getDefaultStack() {
        ItemStack shield = new ItemStack(Items.SHIELD);
        CompoundTag nbt = shield.getOrCreateNbt();
        nbt.putBoolean(ORPHAN_SHIELD_NBT, true);
        shield.setCustomName(Component.literal("Orphan Shield").formatted(ChatFormatting.DARK_GRAY, ChatFormatting.BOLD));
        return shield;
    }
    
    @Override
    public Component getName(ItemStack stack) {
        return Component.literal("Orphan Shield")
            .formatted(ChatFormatting.DARK_GRAY, ChatFormatting.BOLD);
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
    public void inventoryTick(ItemStack stack, net.minecraft.world.level.Level world, net.minecraft.world.entity.Entity entity, int slot, boolean selected) {
        if (!isOrphanShield(stack)) return;
        if (!(entity instanceof net.minecraft.world.entity.LivingEntity living)) return;
        
        // Add armor modifier when held or equipped
        if (selected || Objects.equals(living.getEquippedStack(EquipmentSlot.OFFHAND), stack) || 
            Objects.equals(living.getEquippedStack(EquipmentSlot.MAINHAND), stack) ||
            Objects.equals(living.getEquippedStack(EquipmentSlot.HEAD), stack) ||
            Objects.equals(living.getEquippedStack(EquipmentSlot.CHEST), stack) ||
            Objects.equals(living.getEquippedStack(EquipmentSlot.LEGS), stack) ||
            Objects.equals(living.getEquippedStack(EquipmentSlot.FEET), stack)) {
            
            // Add armor
            AttributeModifier armorModifier = new AttributeModifier(
                ARMOR_MODIFIER_UUID,
                "Orphan Shield Armor",
                10.0,
                AttributeModifier.Operation.ADDITION
            );
            if (!living.getAttributeInstance(Attributes.GENERIC_ARMOR).hasModifier(armorModifier)) {
                living.getAttributeInstance(Attributes.GENERIC_ARMOR).addPersistentModifier(armorModifier);
            }
            
            // Add damage reduction (toughness)
            AttributeModifier toughnessModifier = new AttributeModifier(
                DAMAGE_REDUCTION_UUID,
                "Orphan Shield Toughness",
                5.0,
                AttributeModifier.Operation.ADDITION
            );
            if (!living.getAttributeInstance(Attributes.GENERIC_ARMOR_TOUGHNESS).hasModifier(toughnessModifier)) {
                living.getAttributeInstance(Attributes.GENERIC_ARMOR_TOUGHNESS).addPersistentModifier(toughnessModifier);
            }
        } else {
            // Remove modifiers when not held
            for (AttributeModifier modifier : living.getAttributeInstance(Attributes.GENERIC_ARMOR).getModifiers(AttributeModifier.Operation.ADDITION)) {
                if (Objects.equals(modifier.getId(), ARMOR_MODIFIER_UUID)) {
                    living.getAttributeInstance(Attributes.GENERIC_ARMOR).removeModifier(modifier);
                    break;
                }
            }
            for (AttributeModifier modifier : living.getAttributeInstance(Attributes.GENERIC_ARMOR_TOUGHNESS).getModifiers(AttributeModifier.Operation.ADDITION)) {
                if (Objects.equals(modifier.getId(), DAMAGE_REDUCTION_UUID)) {
                    living.getAttributeInstance(Attributes.GENERIC_ARMOR_TOUGHNESS).removeModifier(modifier);
                    break;
                }
            }
        }
    }
    
    @Override
    public UseAnim getUseAction(ItemStack stack) {
        return UseAnim.BLOCK;
    }
    
    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000; // Allow holding for a long time (1 hour effectively)
    }
    
    @Override
    public void onStoppedUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        // Shield blocking logic is handled by vanilla ShieldItem
        // This is called when the player stops holding right-click
        super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }
}
