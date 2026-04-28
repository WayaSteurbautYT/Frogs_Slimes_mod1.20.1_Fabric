package com.wayacreate.frogslimegamemode.block;

import com.wayacreate.frogslimegamemode.achievements.AchievementManager;
import com.wayacreate.frogslimegamemode.item.MobAbilityItem;
import com.wayacreate.frogslimegamemode.tasks.TaskManager;
import com.wayacreate.frogslimegamemode.tasks.TaskType;
import com.wayacreate.frogslimegamemode.item.AbilityDropItem;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class AbilityCraftingTableBlock extends AnvilBlock {
    public AbilityCraftingTableBlock(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        
        if (!world.isClient && !stack.isEmpty()) {
            String abilityId = getAbilityIdFromItem(stack);
            if (abilityId != null) {
                ItemStack mobAbility = MobAbilityItem.createMobAbility(abilityId);
                stack.decrement(1);

                if (!player.getInventory().insertStack(mobAbility)) {
                    player.dropItem(mobAbility, false);
                }

                TaskManager.completeTask(player, TaskType.CRAFT_ABILITY);
                if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    AchievementManager.unlockAchievement(serverPlayer, "mob_smith");
                }

                player.sendMessage(Component.literal("Forged " + mobAbility.getName().getString() + " from " + stack.getItem().getName().getString() + ".")
                    .formatted(net.minecraft.ChatFormatting.GREEN), true);
                return InteractionResult.SUCCESS;
            }
        }
        
        // Open anvil GUI with custom title
        if (!world.isClient) {
            MenuProvider factory = new SimpleMenuProvider(
                (syncId, playerInventory, playerEntity) -> 
                    new com.wayacreate.frogslimegamemode.screen.AbilityCraftingScreenHandler(syncId, playerInventory),
                Component.literal("Ability Forge")
            );
            player.openHandledScreen(factory);
            player.sendMessage(Component.literal("Tip: right-click this table with a mob drop to forge the matching ability item.")
                .formatted(net.minecraft.ChatFormatting.YELLOW), true);
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.CONSUME;
    }
    
    private String getAbilityIdFromItem(ItemStack stack) {
        String itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getId(stack.getItem()).getPath();
        return switch (itemId) {
            case "rotten_flesh" -> "zombie";
            case "bone" -> "skeleton";
            case "spider_eye" -> "spider";
            case "gunpowder" -> "creeper";
            case "ender_pearl" -> "enderman";
            case "blaze_rod" -> "blaze";
            case "slime_ball" -> "slime";
            case "string" -> "cave_spider";
            case "ghast_tear" -> "ghast";
            case "phantom_membrane" -> "phantom";
            case "shulker_shell" -> "shulker";
            case "dragon_egg" -> "ender_dragon";
            case "rabbit_foot" -> "rabbit";
            case "turtle_shell" -> "turtle";
            case "trident" -> "drowned";
            case "nautilus_shell" -> "elder_guardian";
            case "music_disc_cat" -> "parrot";
            default -> null;
        };
    }
}
