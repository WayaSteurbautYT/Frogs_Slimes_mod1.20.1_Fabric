package com.wayacreate.frogslimegamemode.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.wayacreate.frogslimegamemode.achievements.AchievementManager;
import com.wayacreate.frogslimegamemode.economy.EconomyManager;
import com.wayacreate.frogslimegamemode.economy.ShopItem;
import com.wayacreate.frogslimegamemode.economy.ShopManager;
import com.wayacreate.frogslimegamemode.economy.TradeManager;
import com.wayacreate.frogslimegamemode.screen.ShopScreenHandler;
import com.wayacreate.frogslimegamemode.screen.TradeScreenHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.List;

public class EconomyCommands {
    
    private static final ArrayList<String> commandHistory = new ArrayList<>();
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sell")
            .then(Commands.argument("price", IntegerArgumentType.integer(1))
                .executes(EconomyCommands::sellItem)));
        dispatcher.register(buildShopSubcommand());
        dispatcher.register(buildBalanceSubcommand());
        dispatcher.register(buildPaySubcommand());
        dispatcher.register(buildTradeSubcommand());
        dispatcher.register(buildMessageSubcommand());
    }

    public static LiteralArgumentBuilder<CommandSourceStack> buildShopSubcommand() {
        return Commands.literal("shop")
            .executes(EconomyCommands::openShop)
            .then(Commands.literal("sell")
                .then(Commands.argument("price", IntegerArgumentType.integer(1))
                    .executes(EconomyCommands::sellItem)))
            .then(Commands.literal("buy")
                .then(Commands.argument("index", IntegerArgumentType.integer(0))
                    .executes(EconomyCommands::buyItem)))
            .then(Commands.literal("cancel")
                .then(Commands.argument("index", IntegerArgumentType.integer(0))
                    .executes(EconomyCommands::cancelListing)))
            .then(Commands.literal("mylistings")
                .executes(EconomyCommands::viewMyListings));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> buildBalanceSubcommand() {
        return Commands.literal("balance")
            .executes(EconomyCommands::checkBalance);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> buildPaySubcommand() {
        return Commands.literal("pay")
            .then(Commands.argument("player", StringArgumentType.string())
                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                    .executes(EconomyCommands::payPlayer)));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> buildTradeSubcommand() {
        return Commands.literal("trade")
            .then(Commands.argument("player", StringArgumentType.string())
                .executes(EconomyCommands::sendTradeRequest))
            .then(Commands.literal("accept")
                .executes(EconomyCommands::acceptTrade))
            .then(Commands.literal("decline")
                .executes(EconomyCommands::declineTrade))
            .then(Commands.literal("toggle")
                .executes(EconomyCommands::toggleTrading));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> buildMessageSubcommand() {
        return Commands.literal("msg")
            .then(Commands.argument("player", StringArgumentType.string())
                .then(Commands.argument("message", StringArgumentType.greedyString())
                    .executes(EconomyCommands::sendMessage)));
    }
    
    private static int sellItem(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }
        
        int price = IntegerArgumentType.getInteger(context, "price");
        ItemStack handItem = player.getMainHandStack();
        
        if (handItem.isEmpty()) {
            player.sendMessage(Component.literal("Hold an item to sell!").formatted(ChatFormatting.RED), false);
            return 0;
        }
        
        ShopManager.listItem(player, handItem, price);
        logCommandHistory("sell " + price);
        return 1;
    }
    
    private static int openShop(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }
        
        player.openHandledScreen(new SimpleMenuProvider(
            (syncId, playerInventory, ignored) -> new ShopScreenHandler(syncId, playerInventory),
            Component.literal("FrogSlime Shop")
        ));
        return 1;
    }
    
    private static int buyItem(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }
        
        int index = IntegerArgumentType.getInteger(context, "index");
        ShopManager.buyItem(player, index);
        return 1;
    }
    
    private static int cancelListing(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }
        
        int index = IntegerArgumentType.getInteger(context, "index");
        ShopManager.cancelListing(player, index);
        return 1;
    }
    
    private static int viewMyListings(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }
        
        List<ShopItem> listings = ShopManager.getListingsByPlayer(player.getUuid());
        
        if (listings.isEmpty()) {
            player.sendMessage(Component.literal("You have no active listings!").formatted(ChatFormatting.YELLOW), false);
            return 1;
        }
        
        player.sendMessage(Component.literal("===== Your Listings =====").formatted(ChatFormatting.GOLD, ChatFormatting.BOLD), false);
        
        for (int i = 0; i < listings.size(); i++) {
            ShopItem item = listings.get(i);
            player.sendMessage(Component.literal("[" + i + "] ").formatted(ChatFormatting.GRAY)
                .append(item.getItem().getName())
                .append(Component.literal(" - "))
                .append(Component.literal(item.getPrice() + " coins").formatted(ChatFormatting.GOLD)), false);
        }
        
        return 1;
    }
    
    private static int checkBalance(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }
        
        int balance = EconomyManager.getBalance(player);
        player.sendMessage(Component.literal("Balance: ").formatted(ChatFormatting.WHITE)
            .append(Component.literal(balance + " coins").formatted(ChatFormatting.GOLD)), false);
        return 1;
    }
    
    private static int payPlayer(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }
        
        String targetName = StringArgumentType.getString(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        
        ServerPlayer target = player.getServer().getPlayerManager().getPlayer(targetName);
        if (target == null) {
            player.sendMessage(Component.literal("Player not found!").formatted(ChatFormatting.RED), false);
            return 0;
        }
        
        if (EconomyManager.transfer(player, target, amount)) {
            player.sendMessage(Component.literal("Sent ").formatted(ChatFormatting.GREEN)
                .append(Component.literal(amount + " coins").formatted(ChatFormatting.GOLD))
                .append(Component.literal(" to "))
                .append(target.getName().getString()), false);
            
            target.sendMessage(Component.literal("Received ").formatted(ChatFormatting.GREEN)
                .append(Component.literal(amount + " coins").formatted(ChatFormatting.GOLD))
                .append(Component.literal(" from "))
                .append(player.getName().getString()), false);

            int totalTrades = EconomyManager.getTotalTrades(player.getUuid());
            if (totalTrades == 1) {
                AchievementManager.unlockAchievement(player, "first_trade");
            }
            if (totalTrades >= 25) {
                AchievementManager.unlockAchievement(player, "merchant");
            }
            if (totalTrades >= 100) {
                AchievementManager.unlockAchievement(player, "trade_tycoon");
            }
            return 1;
        } else {
            player.sendMessage(Component.literal("You don't have enough coins!").formatted(ChatFormatting.RED), false);
            return 0;
        }
    }
    
    private static int sendTradeRequest(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }
        
        String targetName = StringArgumentType.getString(context, "player");
        ServerPlayer target = player.getServer().getPlayerManager().getPlayer(targetName);
        
        if (target == null) {
            player.sendMessage(Component.literal("Player not found!").formatted(ChatFormatting.RED), false);
            return 0;
        }
        
        if (target.getUuid().equals(player.getUuid())) {
            player.sendMessage(Component.literal("You can't trade with yourself!").formatted(ChatFormatting.RED), false);
            return 0;
        }
        
        if (!TradeManager.isTradingEnabled(target.getUuid())) {
            player.sendMessage(Component.literal(target.getName().getString() + " has trading disabled!")
                .formatted(ChatFormatting.RED), false);
            return 0;
        }

        TradeManager.openTradeGui(player, target);
        return 1;
    }
    
    private static int acceptTrade(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }
        
        TradeManager.acceptTrade(player);
        return 1;
    }
    
    private static int declineTrade(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }
        
        TradeManager.declineTrade(player);
        return 1;
    }
    
    private static int toggleTrading(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }
        
        TradeManager.toggleTrading(player);
        return 1;
    }
    
    private static int sendMessage(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }
        
        String targetName = StringArgumentType.getString(context, "player");
        String message = StringArgumentType.getString(context, "message");
        
        ServerPlayer target = player.getServer().getPlayerManager().getPlayer(targetName);
        if (target == null) {
            player.sendMessage(Component.literal("Player not found!").formatted(ChatFormatting.RED), false);
            return 0;
        }
        
        // Send to target
        target.sendMessage(Component.literal("From ").formatted(ChatFormatting.GRAY)
            .append(player.getName().getString())
            .append(Component.literal(": ").formatted(ChatFormatting.GRAY))
            .append(Component.literal(message).formatted(ChatFormatting.WHITE)), false);
        
        // Confirm to sender
        player.sendMessage(Component.literal("To ").formatted(ChatFormatting.GRAY)
            .append(target.getName().getString())
            .append(Component.literal(": ").formatted(ChatFormatting.GRAY))
            .append(Component.literal(message).formatted(ChatFormatting.WHITE)), false);
        
        return 1;
    }
    
    private static void logCommandHistory(String command) {
        commandHistory.add(command);
    }
}
