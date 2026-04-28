package com.wayacreate.frogslimegamemode.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.wayacreate.frogslimegamemode.guild.Guild;
import com.wayacreate.frogslimegamemode.guild.GuildManager;
import com.wayacreate.frogslimegamemode.guild.GuildMission;
import com.wayacreate.frogslimegamemode.screen.GuildMissionScreenHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class GuildCommand {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(buildGuildSubcommand("guild"));
    }

    public static LiteralArgumentBuilder<ServerCommandSource> buildGuildSubcommand(String literalName) {
        return CommandManager.literal(literalName)
            .then(CommandManager.literal("create")
                .then(CommandManager.argument("name", StringArgumentType.string())
                    .executes(GuildCommand::createGuild)))
            .then(CommandManager.literal("disband")
                .executes(GuildCommand::disbandGuild))
            .then(CommandManager.literal("invite")
                .then(CommandManager.argument("player", StringArgumentType.string())
                    .executes(GuildCommand::invitePlayer)))
            .then(CommandManager.literal("join")
                .then(CommandManager.argument("name", StringArgumentType.string())
                    .executes(GuildCommand::joinGuild)))
            .then(CommandManager.literal("leave")
                .executes(GuildCommand::leaveGuild))
            .then(CommandManager.literal("info")
                .executes(GuildCommand::guildInfo))
            .then(CommandManager.literal("missions")
                .executes(GuildCommand::listMissions)
                .then(CommandManager.literal("create")
                    .then(CommandManager.argument("name", StringArgumentType.string())
                        .executes(GuildCommand::createMission)))
                .then(CommandManager.literal("complete")
                    .then(CommandManager.argument("id", StringArgumentType.string())
                        .executes(GuildCommand::completeMission))))
            .then(CommandManager.literal("deposit")
                .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                    .executes(GuildCommand::depositCoins)))
            .then(CommandManager.literal("members")
                .executes(GuildCommand::listMembers));
    }
    
    private static int createGuild(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }
        
        String name = StringArgumentType.getString(context, "name");
        Guild guild = GuildManager.createGuild(name, player);
        return guild != null ? 1 : 0;
    }
    
    private static int disbandGuild(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }
        
        return GuildManager.disbandGuild(player) ? 1 : 0;
    }
    
    private static int invitePlayer(CommandContext<ServerCommandSource> context) {
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
        
        return GuildManager.invitePlayer(player, target) ? 1 : 0;
    }
    
    private static int joinGuild(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }
        
        String name = StringArgumentType.getString(context, "name");
        return GuildManager.joinGuild(player, name) ? 1 : 0;
    }
    
    private static int leaveGuild(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }
        
        return GuildManager.leaveGuild(player) ? 1 : 0;
    }
    
    private static int guildInfo(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }
        
        Guild guild = GuildManager.getPlayerGuild(player);
        if (guild == null) {
            player.sendMessage(Text.literal("You are not in a guild!").formatted(Formatting.RED), false);
            return 0;
        }
        
        player.sendMessage(Text.literal("===== " + guild.getName() + " =====").formatted(Formatting.GOLD, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Owner: ").formatted(Formatting.YELLOW)
            .append(guild.getOwnerUuid().equals(player.getUuid()) ? "You" : guild.getOwnerUuid().toString().substring(0, 8)), false);
        player.sendMessage(Text.literal("Members: " + guild.getMembers().size()).formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("Guild Coins: ").formatted(Formatting.YELLOW)
            .append(Text.literal(guild.getCoins() + "").formatted(Formatting.GOLD)), false);
        player.sendMessage(Text.literal("Active Missions: " + guild.getMissions().size()).formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("Your Rank: ").formatted(Formatting.YELLOW)
            .append(Text.literal(guild.getRank(player.getUuid()).name()).formatted(Formatting.GREEN)), false);
        
        return 1;
    }
    
    private static int listMissions(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }
        
        Guild guild = GuildManager.getPlayerGuild(player);
        if (guild == null) {
            player.sendMessage(Text.literal("You are not in a guild!").formatted(Formatting.RED), false);
            return 0;
        }

        player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
            (syncId, playerInventory, ignored) -> new GuildMissionScreenHandler(syncId, playerInventory, player),
            Text.literal(guild.getName() + " Missions")
        ));
        return 1;
    }
    
    private static int createMission(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }
        
        String name = StringArgumentType.getString(context, "name");
        
        // Simple mission with just coins as cost for now
        List<ItemStack> requiredItems = new ArrayList<>();
        List<ItemStack> itemRewards = new ArrayList<>();
        
        GuildMission mission = new GuildMission(
            name, 
            "Complete guild mission", 
            requiredItems, 
            50, // coin reward
            itemRewards, 
            100, // xp reward
            24 // 24 hour duration
        );
        
        return GuildManager.createMission(player, mission) ? 1 : 0;
    }
    
    private static int completeMission(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }
        
        String missionId = StringArgumentType.getString(context, "id");
        
        try {
            java.util.UUID id = java.util.UUID.fromString(missionId);
            List<ItemStack> contributions = new ArrayList<>();
            return GuildManager.contributeToMission(player, id, contributions) ? 1 : 0;
        } catch (IllegalArgumentException e) {
            player.sendMessage(Text.literal("Invalid mission ID!").formatted(Formatting.RED), false);
            return 0;
        }
    }
    
    private static int depositCoins(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }
        
        int amount = IntegerArgumentType.getInteger(context, "amount");
        Guild guild = GuildManager.getPlayerGuild(player);
        
        if (guild == null) {
            player.sendMessage(Text.literal("You are not in a guild!").formatted(Formatting.RED), false);
            return 0;
        }
        
        if (!com.wayacreate.frogslimegamemode.economy.EconomyManager.removeBalance(player, amount)) {
            player.sendMessage(Text.literal("You don't have enough coins!").formatted(Formatting.RED), false);
            return 0;
        }
        
        guild.addCoins(amount);
        player.sendMessage(Text.literal("Deposited ").formatted(Formatting.GREEN)
            .append(Text.literal(amount + " coins").formatted(Formatting.GOLD))
            .append(Text.literal(" to guild! Guild balance: " + guild.getCoins()).formatted(Formatting.WHITE)), false);
        
        return 1;
    }
    
    private static int listMembers(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("Only players can use this command!"));
            return 0;
        }
        
        Guild guild = GuildManager.getPlayerGuild(player);
        if (guild == null) {
            player.sendMessage(Text.literal("You are not in a guild!").formatted(Formatting.RED), false);
            return 0;
        }
        
        player.sendMessage(Text.literal("===== Guild Members =====").formatted(Formatting.GOLD, Formatting.BOLD), false);
        
        for (java.util.UUID memberId : guild.getMembers()) {
            ServerPlayerEntity member = player.getServer().getPlayerManager().getPlayer(memberId);
            String name = member != null ? member.getName().getString() : memberId.toString().substring(0, 8);
            Guild.GuildRank rank = guild.getRank(memberId);
            
            Text onlineStatus = member != null ? 
                Text.literal(" [ONLINE]").formatted(Formatting.GREEN) :
                Text.literal(" [OFFLINE]").formatted(Formatting.GRAY);
            
            player.sendMessage(Text.literal(name).formatted(Formatting.AQUA)
                .append(Text.literal(" - " + rank.name()).formatted(Formatting.YELLOW))
                .append(onlineStatus), false);
        }
        
        return 1;
    }
}
