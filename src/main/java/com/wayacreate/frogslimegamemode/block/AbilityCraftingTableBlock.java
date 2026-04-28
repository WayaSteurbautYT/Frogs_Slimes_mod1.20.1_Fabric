package com.wayacreate.frogslimegamemode.block;

import com.wayacreate.frogslimegamemode.achievements.AchievementManager;
import com.wayacreate.frogslimegamemode.item.MobAbilityItem;
import com.wayacreate.frogslimegamemode.tasks.TaskManager;
import com.wayacreate.frogslimegamemode.tasks.TaskType;
import com.wayacreate.frogslimegamemode.item.AbilityDropItem;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AbilityCraftingTableBlock extends AnvilBlock {
    public AbilityCraftingTableBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
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
                if (player instanceof net.minecraft.server.network.ServerPlayerEntity serverPlayer) {
                    AchievementManager.unlockAchievement(serverPlayer, "mob_smith");
                }

                player.sendMessage(Text.literal("Forged " + mobAbility.getName().getString() + " from " + stack.getItem().getName().getString() + ".")
                    .formatted(net.minecraft.util.Formatting.GREEN), true);
                return ActionResult.SUCCESS;
            }
        }
        
        // Open anvil GUI with custom title
        if (!world.isClient) {
            NamedScreenHandlerFactory factory = new SimpleNamedScreenHandlerFactory(
                (syncId, playerInventory, playerEntity) -> 
                    new com.wayacreate.frogslimegamemode.screen.AbilityCraftingScreenHandler(syncId, playerInventory),
                Text.literal("Ability Forge")
            );
            player.openHandledScreen(factory);
            player.sendMessage(Text.literal("Tip: right-click this table with a mob drop to forge the matching ability item.")
                .formatted(net.minecraft.util.Formatting.YELLOW), true);
            return ActionResult.SUCCESS;
        }
        
        return ActionResult.CONSUME;
    }
    
    private String getAbilityIdFromItem(ItemStack stack) {
        String itemId = net.minecraft.registry.Registries.ITEM.getId(stack.getItem()).getPath();
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
