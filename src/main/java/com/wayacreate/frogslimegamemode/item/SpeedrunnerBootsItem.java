package com.wayacreate.frogslimegamemode.item;

import com.wayacreate.frogslimegamemode.gamemode.ManhuntManager;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

public class SpeedrunnerBootsItem extends ArmorItem {
    public SpeedrunnerBootsItem(Properties settings) {
        super(ArmorMaterials.LEATHER, Type.BOOTS, settings);
    }

    @Override
    public void onCraft(ItemStack stack, Level world, Player player) {
        if (!world.isClient && player instanceof ServerPlayer serverPlayer) {
            if (ManhuntManager.isSpeedrunner(serverPlayer)) {
                serverPlayer.sendMessage(Component.literal("Speedrunner Boots equipped! Passive speed boost active.")
                    .formatted(ChatFormatting.GREEN), false);
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, net.minecraft.world.entity.Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof ServerPlayer player) {
            if (slot == EquipmentSlot.FEET.getEntitySlotId() && ManhuntManager.isSpeedrunner(player)) {
                // Apply passive speed boost when boots are equipped
                if (!player.hasStatusEffect(net.minecraft.world.effect.MobEffects.SPEED)) {
                    player.addStatusEffect(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.SPEED, 100, 0, false, false));
                }
            }
        }
    }
}
