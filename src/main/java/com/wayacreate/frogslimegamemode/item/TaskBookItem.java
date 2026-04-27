package com.wayacreate.frogslimegamemode.item;

import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class TaskBookItem extends Item {
    public TaskBookItem(Settings settings) {
        super(settings);
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && user instanceof ServerPlayerEntity) {
            if (GamemodeManager.isInGamemode(user)) {
                var data = GamemodeManager.getData(user);
                
                user.sendMessage(Text.literal("═══════════════════════════")
                    .formatted(Formatting.GOLD, Formatting.BOLD), false);
                user.sendMessage(Text.literal("FROG & SLIME GAMEMODE GUIDE")
                    .formatted(Formatting.AQUA, Formatting.BOLD), false);
                user.sendMessage(Text.literal("═══════════════════════════")
                    .formatted(Formatting.GOLD, Formatting.BOLD), false);
                user.sendMessage(Text.literal(""), false);
                
                // Rules Section
                user.sendMessage(Text.literal("GAMEPLAY RULES:")
                    .formatted(Formatting.YELLOW, Formatting.BOLD), false);
                user.sendMessage(Text.literal("• Tame Frog and Slime Helpers using spawn eggs")
                    .formatted(Formatting.GRAY), false);
                user.sendMessage(Text.literal("• Kill mobs to evolve your helpers")
                    .formatted(Formatting.GRAY), false);
                user.sendMessage(Text.literal("• Assign roles: Miner, Lumberjack, Builder, Farmer")
                    .formatted(Formatting.GRAY), false);
                user.sendMessage(Text.literal("• Helpers can mine, build, farm, and fight for you")
                    .formatted(Formatting.GRAY), false);
                user.sendMessage(Text.literal("• Eat mobs to gain their abilities")
                    .formatted(Formatting.GRAY), false);
                user.sendMessage(Text.literal("• Kill players to steal their abilities")
                    .formatted(Formatting.RED), false);
                user.sendMessage(Text.literal("• Defeat the Ender Dragon to complete the game")
                    .formatted(Formatting.DARK_RED), false);
                user.sendMessage(Text.literal("• WARNING: Unexpected ending awaits...")
                    .formatted(Formatting.DARK_PURPLE, Formatting.ITALIC), false);
                user.sendMessage(Text.literal(""), false);
                
                // Starter Kit
                user.sendMessage(Text.literal("STARTER KIT:")
                    .formatted(Formatting.YELLOW, Formatting.BOLD), false);
                user.sendMessage(Text.literal("• Task Book (this item)")
                    .formatted(Formatting.GREEN), false);
                user.sendMessage(Text.literal("• Orphan Shield (protects your helpers)")
                    .formatted(Formatting.GREEN), false);
                user.sendMessage(Text.literal("• 16 Cooked Beef")
                    .formatted(Formatting.GREEN), false);
                user.sendMessage(Text.literal("• Iron Sword & Pickaxe")
                    .formatted(Formatting.GREEN), false);
                user.sendMessage(Text.literal("• 32 Torches")
                    .formatted(Formatting.GREEN), false);
                user.sendMessage(Text.literal(""), false);
                
                // Progress Section
                user.sendMessage(Text.literal("CURRENT PROGRESS:")
                    .formatted(Formatting.YELLOW, Formatting.BOLD), false);
                user.sendMessage(Text.literal("• Helpers Tamed: " + data.getHelpersSpawned())
                    .formatted(Formatting.GREEN), false);
                user.sendMessage(Text.literal("• Mobs Eaten: " + data.getMobsEaten())
                    .formatted(Formatting.GREEN), false);
                user.sendMessage(Text.literal("• Items Collected: " + data.getItemsCollected())
                    .formatted(Formatting.GREEN), false);
                user.sendMessage(Text.literal("• Jumps: " + data.getJumpCount())
                    .formatted(Formatting.GREEN), false);
                user.sendMessage(Text.literal("• Deaths: " + data.getDeathCount())
                    .formatted(Formatting.RED), false);
                user.sendMessage(Text.literal(""), false);
                
                // Current Objectives
                user.sendMessage(Text.literal("CURRENT OBJECTIVES:")
                    .formatted(Formatting.YELLOW, Formatting.BOLD), false);
                user.sendMessage(Text.literal("• Tame a Frog Helper")
                    .formatted(Formatting.GRAY), false);
                user.sendMessage(Text.literal("• Tame a Slime Helper")
                    .formatted(Formatting.GRAY), false);
                user.sendMessage(Text.literal("• Evolve your helpers by killing mobs")
                    .formatted(Formatting.GRAY), false);
                user.sendMessage(Text.literal("• Defeat the Ender Dragon")
                    .formatted(Formatting.DARK_RED), false);
                user.sendMessage(Text.literal(""), false);
                
                // Manhunt Section
                user.sendMessage(Text.literal("MANHUNT MODE:")
                    .formatted(Formatting.YELLOW, Formatting.BOLD), false);
                user.sendMessage(Text.literal("• Use /manhunt auto for auto-assignment")
                    .formatted(Formatting.GRAY), false);
                user.sendMessage(Text.literal("• Hunters track and stop the speedrunner")
                    .formatted(Formatting.RED), false);
                user.sendMessage(Text.literal("• Speedrunner must beat the game")
                    .formatted(Formatting.GREEN), false);
                user.sendMessage(Text.literal("• Hunters have: Track, Block, Slow abilities")
                    .formatted(Formatting.GRAY), false);
                user.sendMessage(Text.literal("• Speedrunner has: Escape, Speed, Invis abilities")
                    .formatted(Formatting.GRAY), false);
                user.sendMessage(Text.literal(""), false);
                
                user.sendMessage(Text.literal("═══════════════════════════")
                    .formatted(Formatting.GOLD, Formatting.BOLD), false);
            } else {
                user.sendMessage(Text.literal("You must be in Frog & Slime Gamemode to use this book!")
                    .formatted(Formatting.RED), false);
                user.sendMessage(Text.literal("Use /frogslime enable to activate the gamemode")
                    .formatted(Formatting.GRAY), false);
            }
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
