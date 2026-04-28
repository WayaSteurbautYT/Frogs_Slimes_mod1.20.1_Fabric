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
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class EconomyCommands {
    
    private static final ArrayList<String> commandHistory = new ArrayList<>();
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("sell")
            .then(CommandManager.argument("price", IntegerArgumentType.integer(1))
                .executes(EconomyCommands::sellItem)));
        dispatcher.register(buildShopSubcommand());
        dispatcher.register(buildBalanceSubcommand());
        dispatcher.register(buildPaySubcommand());
        dispatcher.register(buildTradeSubcommand());
        dispatcher.register(buildMessageSubcommand());
    }

    public static LiteralArgumentBuilder<ServerCommandSource> buildShopSubcommand() {
        return CommandManager.literal("shop")
            .executes(EconomyCommands::openShop)
            .then(CommandManager.literal("sell")
                .then(CommandManager.argument("price", IntegerArgumentType.integer(1))
                    .executes(EconomyCommands::sellItem)))
            .then(CommandManager.literal("buy")
                .then(CommandManager.argument("index", IntegerArgumentType.integer(0))
                    .executes(EconomyCommands::buyItem)))
            .then(CommandManager.literal("cancel")
                .then(CommandManager.argument("index", IntegerArgumentType.integer(0))
                    .executes(EconomyCommands::cancelListing)))
            .then(CommandManager.literal("mylistings")
                .executes(EconomyCommands::viewMyListings));
    }

    public static LiteralArgumentBuilder<ServerCommandSource> buildBalanceSubcommand() {
        return CommandManager.literal("balance")
            .executes(EconomyCommands::checkBalance);
    }

    public static LiteralArgumentBuilder<ServerCommandSource> buildPaySubcommand() {
        return CommandManager.literal("pay")
            .then(CommandManager.argument("player", StringArgumentType.string())
                .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                    .executes(EconomyCommands::payPlayer)));
    }

    public static LiteralArgumentBuilder<ServerCommandSource> buildTradeSubcommand() {
        return CommandManager.literal("trade")
            .then(CommandManager.argument("player", StringArgumentType.string())
                .executes(EconomyCommands::sendTradeRequest))
            .then(CommandManager.literal("accept")
                .executes(EconomyCommands::acceptTrade))
            .then(CommandManager.literal("decline")
                .executes(EconomyCommands::declineTrade))
            .then(CommandManager.literal("toggle")
                .executes(EconomyCommands::toggleTrading));
    }

    public static LiteralArgumentBuilder<ServerCommandSource> buildMessageSubcommand() {
        return CommandManager.literal("msg")
            .then(CommandManager.argument("player", StringArgumentType.string())
                .then(CommandManager.argument("message", StringArgumentType.greedyString())
                    .executes(EconomyCommands::sendMessage)));
    }
    
    private static int sellItem(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }
        
        int price = IntegerArgumentType.getInteger(context, "price");
        ItemStack handItem = player.getMainHandStack();
        
        if (handItem.isEmpty()) {
            player.sendMessage(Text.literal("Hold an item to sell!").formatted(Formatting.RED), false);
            return 0;
        }
        
        ShopManager.listItem(player, handItem, price);
        logCommandHistory("sell " + price);
        return 1;
    }
    
    private static int openShop(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }
        
        player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
            (syncId, playerInventory, ignored) -> new ShopScreenHandler(syncId, playerInventory),
            Text.literal("FrogSlime Shop")
        ));
        return 1;
    }
    
    private static int buyItem(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }
        
        int index = IntegerArgumentType.getInteger(context, "index");
        ShopManager.buyItem(player, index);
        return 1;
    }
    
    private static int cancelListing(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }
        
        int index = IntegerArgumentType.getInteger(context, "index");
        ShopManager.cancelListing(player, index);
        return 1;
    }
    
    private static int viewMyListings(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }
        
        List<ShopItem> listings = ShopManager.getListingsByPlayer(player.getUuid());
        
        if (listings.isEmpty()) {
            player.sendMessage(Text.literal("You have no active listings!").formatted(Formatting.YELLOW), false);
            return 1;
        }
        
        player.sendMessage(Text.literal("===== Your Listings =====").formatted(Formatting.GOLD, Formatting.BOLD), false);
        
        for (int i = 0; i < listings.size(); i++) {
            ShopItem item = listings.get(i);
            player.sendMessage(Text.literal("[" + i + "] ").formatted(Formatting.GRAY)
                .append(item.getItem().getName())
                .append(Text.literal(" - "))
                .append(Text.literal(item.getPrice() + " coins").formatted(Formatting.GOLD)), false);
        }
        
        return 1;
    }
    
    private static int checkBalance(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }
        
        int balance = EconomyManager.getBalance(player);
        player.sendMessage(Text.literal("Balance: ").formatted(Formatting.WHITE)
            .append(Text.literal(balance + " coins").formatted(Formatting.GOLD)), false);
        return 1;
    }
    
    private static int payPlayer(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }
        
        String targetName = StringArgumentType.getString(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        
        ServerPlayerEntity target = player.getServer().getPlayerManager().getPlayer(targetName);
        if (target == null) {
            player.sendMessage(Text.literal("Player not found!").formatted(Formatting.RED), false);
            return 0;
        }
        
        if (EconomyManager.transfer(player, target, amount)) {
            player.sendMessage(Text.literal("Sent ").formatted(Formatting.GREEN)
                .append(Text.literal(amount + " coins").formatted(Formatting.GOLD))
                .append(Text.literal(" to "))
                .append(target.getName().getString()), false);
            
            target.sendMessage(Text.literal("Received ").formatted(Formatting.GREEN)
                .append(Text.literal(amount + " coins").formatted(Formatting.GOLD))
                .append(Text.literal(" from "))
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
            player.sendMessage(Text.literal("You don't have enough coins!").formatted(Formatting.RED), false);
            return 0;
        }
    }
    
    private static int sendTradeRequest(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }
        
        String targetName = StringArgumentType.getString(context, "player");
        ServerPlayerEntity target = player.getServer().getPlayerManager().getPlayer(targetName);
        
        if (target == null) {
            player.sendMessage(Text.literal("Player not found!").formatted(Formatting.RED), false);
            return 0;
        }
        
        if (target.getUuid().equals(player.getUuid())) {
            player.sendMessage(Text.literal("You can't trade with yourself!").formatted(Formatting.RED), false);
            return 0;
        }
        
        if (!TradeManager.isTradingEnabled(target.getUuid())) {
            player.sendMessage(Text.literal(target.getName().getString() + " has trading disabled!")
                .formatted(Formatting.RED), false);
            return 0;
        }

        TradeManager.openTradeGui(player, target);
        return 1;
    }
    
    private static int acceptTrade(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }
        
        TradeManager.acceptTrade(player);
        return 1;
    }
    
    private static int declineTrade(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }
        
        TradeManager.declineTrade(player);
        return 1;
    }
    
    private static int toggleTrading(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }
        
        TradeManager.toggleTrading(player);
        return 1;
    }
    
    private static int sendMessage(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }
        
        String targetName = StringArgumentType.getString(context, "player");
        String message = StringArgumentType.getString(context, "message");
        
        ServerPlayerEntity target = player.getServer().getPlayerManager().getPlayer(targetName);
        if (target == null) {
            player.sendMessage(Text.literal("Player not found!").formatted(Formatting.RED), false);
            return 0;
        }
        
        // Send to target
        target.sendMessage(Text.literal("From ").formatted(Formatting.GRAY)
            .append(player.getName().getString())
            .append(Text.literal(": ").formatted(Formatting.GRAY))
            .append(Text.literal(message).formatted(Formatting.WHITE)), false);
        
        // Confirm to sender
        player.sendMessage(Text.literal("To ").formatted(Formatting.GRAY)
            .append(target.getName().getString())
            .append(Text.literal(": ").formatted(Formatting.GRAY))
            .append(Text.literal(message).formatted(Formatting.WHITE)), false);
        
        return 1;
    }
    
    private static void logCommandHistory(String command) {
        commandHistory.add(command);
    }
}
