package com.wayacreate.frogslimegamemode.item;

import com.wayacreate.frogslimegamemode.gamemode.ManhuntManager;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class SpeedrunnerBootsItem extends ArmorItem {
    public SpeedrunnerBootsItem(Settings settings) {
        super(ArmorMaterials.LEATHER, Type.BOOTS, settings);
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
            if (ManhuntManager.isSpeedrunner(serverPlayer)) {
                serverPlayer.sendMessage(Text.literal("Speedrunner Boots equipped! Passive speed boost active.")
                    .formatted(Formatting.GREEN), false);
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof ServerPlayerEntity player) {
            if (slot == EquipmentSlot.FEET.getEntitySlotId() && ManhuntManager.isSpeedrunner(player)) {
                // Apply passive speed boost when boots are equipped
                if (!player.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.SPEED)) {
                    player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                        net.minecraft.entity.effect.StatusEffects.SPEED, 100, 0, false, false));
                }
            }
        }
    }
}
