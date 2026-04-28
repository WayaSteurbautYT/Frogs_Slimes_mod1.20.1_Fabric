package com.wayacreate.frogslimegamemode.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.wayacreate.frogslimegamemode.economy.BountyManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Collection;

public class BountyCommand {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("bounty")
            .then(CommandManager.literal("add")
                .then(CommandManager.argument("player", StringArgumentType.string())
                    .then(CommandManager.argument("coins", IntegerArgumentType.integer(0))
                        .executes(BountyCommand::addBounty))))
            .then(CommandManager.literal("list")
                .executes(BountyCommand::listBounties))
            .then(CommandManager.literal("check")
                .executes(BountyCommand::checkMyBounty)));
    }
    
    private static int addBounty(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }
        
        String targetName = StringArgumentType.getString(context, "player");
        int coins = IntegerArgumentType.getInteger(context, "coins");
        
        ServerPlayerEntity target = player.getServer().getPlayerManager().getPlayer(targetName);
        if (target == null) {
            player.sendMessage(Text.literal("Player not found!").formatted(Formatting.RED), false);
            return 0;
        }
        
        if (target.getUuid().equals(player.getUuid())) {
            player.sendMessage(Text.literal("You can't place a bounty on yourself!").formatted(Formatting.RED), false);
            return 0;
        }
        
        BountyManager.addBounty(target, player, coins, new ArrayList<>());
        return 1;
    }
    
    private static int listBounties(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }
        
        Collection<BountyManager.Bounty> bounties = BountyManager.getAllBounties();
        
        if (bounties.isEmpty()) {
            player.sendMessage(Text.literal("No active bounties!").formatted(Formatting.YELLOW), false);
            return 1;
        }
        
        player.sendMessage(Text.literal("===== Active Bounties =====").formatted(Formatting.RED, Formatting.BOLD), false);
        
        for (BountyManager.Bounty bounty : bounties) {
            player.sendMessage(Text.literal(bounty.getTargetName()).formatted(Formatting.RED)
                .append(Text.literal(" - Reward: ").formatted(Formatting.WHITE))
                .append(Text.literal(bounty.getTotalReward() + " coins").formatted(Formatting.GOLD)), false);
        }
        
        return 1;
    }
    
    private static int checkMyBounty(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }
        
        BountyManager.Bounty bounty = BountyManager.getBounty(player.getUuid());
        
        if (bounty == null) {
            player.sendMessage(Text.literal("You have no bounty on your head!").formatted(Formatting.GREEN), false);
        } else {
            player.sendMessage(Text.literal("Bounty on your head: ").formatted(Formatting.RED)
                .append(Text.literal(bounty.getTotalReward() + " coins").formatted(Formatting.GOLD)), false);
        }
        
        return 1;
    }
}
