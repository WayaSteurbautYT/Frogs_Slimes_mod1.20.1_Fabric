package com.wayacreate.frogslimegamemode.item;

import com.wayacreate.frogslimegamemode.entity.SlimeHelperEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

public class FinalEvolutionCrystal extends Item {
    public FinalEvolutionCrystal(Settings settings) {
        super(settings);
    }
    
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient) {
            if (entity instanceof SlimeHelperEntity slime && slime.isOwner(user)) {
                if (!slime.isFinalForm()) {
                    slime.unlockFinalForm();
                    stack.decrement(1);
                    user.sendMessage(Text.literal("THE FINAL EVOLUTION HAS BEGUN...")
                        .formatted(Formatting.DARK_RED, Formatting.BOLD), false);
                    return ActionResult.SUCCESS;
                } else {
                    user.sendMessage(Text.literal("This slime has already reached its final form...")
                        .formatted(Formatting.DARK_GRAY), false);
                    return ActionResult.FAIL;
                }
            } else {
                user.sendMessage(Text.literal("This crystal can only be used on your slime helper!")
                    .formatted(Formatting.RED), false);
            }
        }
        return ActionResult.PASS;
    }
}