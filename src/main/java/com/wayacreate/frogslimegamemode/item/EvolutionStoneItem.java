package com.wayacreate.frogslimegamemode.item;

import com.wayacreate.frogslimegamemode.entity.FrogHelperEntity;
import com.wayacreate.frogslimegamemode.entity.SlimeHelperEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

public class EvolutionStoneItem extends Item {
    public EvolutionStoneItem(Settings settings) {
        super(settings);
    }
    
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient) {
            if (entity instanceof FrogHelperEntity frog && frog.isOwner(user)) {
                if (frog.getEvolutionStage() < 3) {
                    frog.evolve();
                    stack.decrement(1);
                    user.sendMessage(Text.literal("Evolution stone used! Your frog helper evolved!")
                        .formatted(Formatting.GREEN), false);
                    return ActionResult.SUCCESS;
                } else {
                    user.sendMessage(Text.literal("Your frog helper is already at max evolution!")
                        .formatted(Formatting.RED), false);
                    return ActionResult.FAIL;
                }
            } else if (entity instanceof SlimeHelperEntity slime && slime.isOwner(user)) {
                if (slime.getEvolutionStage() < 3) {
                    slime.evolve();
                    stack.decrement(1);
                    user.sendMessage(Text.literal("Evolution stone used! Your slime helper evolved!")
                        .formatted(Formatting.GREEN), false);
                    return ActionResult.SUCCESS;
                } else {
                    user.sendMessage(Text.literal("Your slime helper is already at max evolution!")
                        .formatted(Formatting.RED), false);
                    user.sendMessage(Text.literal("(Try defeating the Ender Dragon for something special...)")
                        .formatted(Formatting.GRAY, Formatting.ITALIC), false);
                    return ActionResult.FAIL;
                }
            }
        }
        return ActionResult.PASS;
    }
}