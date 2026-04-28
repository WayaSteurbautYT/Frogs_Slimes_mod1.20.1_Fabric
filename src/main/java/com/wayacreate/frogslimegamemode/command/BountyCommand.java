package com.wayacreate.frogslimegamemode.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.wayacreate.frogslimegamemode.economy.BountyManager;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.Collection;

public class BountyCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("bounty")
            .then(Commands.literal("add")
                .then(Commands.argument("player", StringArgumentType.string())
                    .then(Commands.argument("coins", IntegerArgumentType.integer(0))
                        .executes(BountyCommand::addBounty))))
            .then(Commands.literal("list")
                .executes(BountyCommand::listBounties))
            .then(Commands.literal("check")
                .executes(BountyCommand::checkMyBounty)));
    }
    
    private static int addBounty(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }
        
        String targetName = StringArgumentType.getString(context, "player");
        int coins = IntegerArgumentType.getInteger(context, "coins");
        
        ServerPlayer target = player.getServer().getPlayerManager().getPlayer(targetName);
        if (target == null) {
            player.sendMessage(Component.literal("Player not found!").formatted(ChatFormatting.RED), false);
            return 0;
        }
        
        if (target.getUuid().equals(player.getUuid())) {
            player.sendMessage(Component.literal("You can't place a bounty on yourself!").formatted(ChatFormatting.RED), false);
            return 0;
        }
        
        BountyManager.addBounty(target, player, coins, new ArrayList<>());
        return 1;
    }
    
    private static int listBounties(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }
        
        Collection<BountyManager.Bounty> bounties = BountyManager.getAllBounties();
        
        if (bounties.isEmpty()) {
            player.sendMessage(Component.literal("No active bounties!").formatted(ChatFormatting.YELLOW), false);
            return 1;
        }
        
        player.sendMessage(Component.literal("===== Active Bounties =====").formatted(ChatFormatting.RED, ChatFormatting.BOLD), false);
        
        for (BountyManager.Bounty bounty : bounties) {
            player.sendMessage(Component.literal(bounty.getTargetName()).formatted(ChatFormatting.RED)
                .append(Component.literal(" - Reward: ").formatted(ChatFormatting.WHITE))
                .append(Component.literal(bounty.getTotalReward() + " coins").formatted(ChatFormatting.GOLD)), false);
        }
        
        return 1;
    }
    
    private static int checkMyBounty(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }
        
        BountyManager.Bounty bounty = BountyManager.getBounty(player.getUuid());
        
        if (bounty == null) {
            player.sendMessage(Component.literal("You have no bounty on your head!").formatted(ChatFormatting.GREEN), false);
        } else {
            player.sendMessage(Component.literal("Bounty on your head: ").formatted(ChatFormatting.RED)
                .append(Component.literal(bounty.getTotalReward() + " coins").formatted(ChatFormatting.GOLD)), false);
        }
        
        return 1;
    }
}
