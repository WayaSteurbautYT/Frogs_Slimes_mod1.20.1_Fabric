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
import net.minecraft.world.item.ItemStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.List;

public class GuildCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(buildGuildSubcommand("guild"));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> buildGuildSubcommand(String literalName) {
        return Commands.literal(literalName)
            .then(Commands.literal("create")
                .then(Commands.argument("name", StringArgumentType.string())
                    .executes(GuildCommand::createGuild)))
            .then(Commands.literal("disband")
                .executes(GuildCommand::disbandGuild))
            .then(Commands.literal("invite")
                .then(Commands.argument("player", StringArgumentType.string())
                    .executes(GuildCommand::invitePlayer)))
            .then(Commands.literal("join")
                .then(Commands.argument("name", StringArgumentType.string())
                    .executes(GuildCommand::joinGuild)))
            .then(Commands.literal("leave")
                .executes(GuildCommand::leaveGuild))
            .then(Commands.literal("info")
                .executes(GuildCommand::guildInfo))
            .then(Commands.literal("missions")
                .executes(GuildCommand::listMissions)
                .then(Commands.literal("create")
                    .then(Commands.argument("name", StringArgumentType.string())
                        .executes(GuildCommand::createMission)))
                .then(Commands.literal("complete")
                    .then(Commands.argument("id", StringArgumentType.string())
                        .executes(GuildCommand::completeMission))))
            .then(Commands.literal("deposit")
                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                    .executes(GuildCommand::depositCoins)))
            .then(Commands.literal("members")
                .executes(GuildCommand::listMembers));
    }
    
    private static int createGuild(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }
        
        String name = StringArgumentType.getString(context, "name");
        Guild guild = GuildManager.createGuild(name, player);
        return guild != null ? 1 : 0;
    }
    
    private static int disbandGuild(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }
        
        return GuildManager.disbandGuild(player) ? 1 : 0;
    }
    
    private static int invitePlayer(CommandContext<CommandSourceStack> context) {
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
        
        return GuildManager.invitePlayer(player, target) ? 1 : 0;
    }
    
    private static int joinGuild(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }
        
        String name = StringArgumentType.getString(context, "name");
        return GuildManager.joinGuild(player, name) ? 1 : 0;
    }
    
    private static int leaveGuild(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }
        
        return GuildManager.leaveGuild(player) ? 1 : 0;
    }
    
    private static int guildInfo(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }
        
        Guild guild = GuildManager.getPlayerGuild(player);
        if (guild == null) {
            player.sendMessage(Component.literal("You are not in a guild!").formatted(ChatFormatting.RED), false);
            return 0;
        }
        
        player.sendMessage(Component.literal("===== " + guild.getName() + " =====").formatted(ChatFormatting.GOLD, ChatFormatting.BOLD), false);
        player.sendMessage(Component.literal("Owner: ").formatted(ChatFormatting.YELLOW)
            .append(guild.getOwnerUuid().equals(player.getUuid()) ? "You" : guild.getOwnerUuid().toString().substring(0, 8)), false);
        player.sendMessage(Component.literal("Members: " + guild.getMembers().size()).formatted(ChatFormatting.YELLOW), false);
        player.sendMessage(Component.literal("Guild Coins: ").formatted(ChatFormatting.YELLOW)
            .append(Component.literal(guild.getCoins() + "").formatted(ChatFormatting.GOLD)), false);
        player.sendMessage(Component.literal("Active Missions: " + guild.getMissions().size()).formatted(ChatFormatting.YELLOW), false);
        player.sendMessage(Component.literal("Your Rank: ").formatted(ChatFormatting.YELLOW)
            .append(Component.literal(guild.getRank(player.getUuid()).name()).formatted(ChatFormatting.GREEN)), false);
        
        return 1;
    }
    
    private static int listMissions(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }
        
        Guild guild = GuildManager.getPlayerGuild(player);
        if (guild == null) {
            player.sendMessage(Component.literal("You are not in a guild!").formatted(ChatFormatting.RED), false);
            return 0;
        }

        player.openHandledScreen(new SimpleMenuProvider(
            (syncId, playerInventory, ignored) -> new GuildMissionScreenHandler(syncId, playerInventory, player),
            Component.literal(guild.getName() + " Missions")
        ));
        return 1;
    }
    
    private static int createMission(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
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
    
    private static int completeMission(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }
        
        String missionId = StringArgumentType.getString(context, "id");
        
        try {
            java.util.UUID id = java.util.UUID.fromString(missionId);
            List<ItemStack> contributions = new ArrayList<>();
            return GuildManager.contributeToMission(player, id, contributions) ? 1 : 0;
        } catch (IllegalArgumentException e) {
            player.sendMessage(Component.literal("Invalid mission ID!").formatted(ChatFormatting.RED), false);
            return 0;
        }
    }
    
    private static int depositCoins(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }
        
        int amount = IntegerArgumentType.getInteger(context, "amount");
        Guild guild = GuildManager.getPlayerGuild(player);
        
        if (guild == null) {
            player.sendMessage(Component.literal("You are not in a guild!").formatted(ChatFormatting.RED), false);
            return 0;
        }
        
        if (!com.wayacreate.frogslimegamemode.economy.EconomyManager.removeBalance(player, amount)) {
            player.sendMessage(Component.literal("You don't have enough coins!").formatted(ChatFormatting.RED), false);
            return 0;
        }
        
        guild.addCoins(amount);
        player.sendMessage(Component.literal("Deposited ").formatted(ChatFormatting.GREEN)
            .append(Component.literal(amount + " coins").formatted(ChatFormatting.GOLD))
            .append(Component.literal(" to guild! Guild balance: " + guild.getCoins()).formatted(ChatFormatting.WHITE)), false);
        
        return 1;
    }
    
    private static int listMembers(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Component.literal("Only players can use this command!"));
            return 0;
        }
        
        Guild guild = GuildManager.getPlayerGuild(player);
        if (guild == null) {
            player.sendMessage(Component.literal("You are not in a guild!").formatted(ChatFormatting.RED), false);
            return 0;
        }
        
        player.sendMessage(Component.literal("===== Guild Members =====").formatted(ChatFormatting.GOLD, ChatFormatting.BOLD), false);
        
        for (java.util.UUID memberId : guild.getMembers()) {
            ServerPlayer member = player.getServer().getPlayerManager().getPlayer(memberId);
            String name = member != null ? member.getName().getString() : memberId.toString().substring(0, 8);
            Guild.GuildRank rank = guild.getRank(memberId);
            
            Component onlineStatus = member != null ? 
                Component.literal(" [ONLINE]").formatted(ChatFormatting.GREEN) :
                Component.literal(" [OFFLINE]").formatted(ChatFormatting.GRAY);
            
            player.sendMessage(Component.literal(name).formatted(ChatFormatting.AQUA)
                .append(Component.literal(" - " + rank.name()).formatted(ChatFormatting.YELLOW))
                .append(onlineStatus), false);
        }
        
        return 1;
    }
}
