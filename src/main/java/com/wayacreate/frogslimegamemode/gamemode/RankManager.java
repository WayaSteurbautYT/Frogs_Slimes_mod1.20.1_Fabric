package com.wayacreate.frogslimegamemode.gamemode;

import com.wayacreate.frogslimegamemode.guild.GuildManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RankManager {
    private static final Map<UUID, PlayerRank> playerRanks = new HashMap<>();
    
    public enum PlayerRank {
        BEGINNER("Beginner", ChatFormatting.GRAY, 0),
        YOUTUBER("YouTuber", ChatFormatting.GOLD, 1),
        CREATOR("Creator", ChatFormatting.DARK_RED, 2);
        
        private final String displayName;
        private final ChatFormatting color;
        private final int priority;
        
        PlayerRank(String displayName, ChatFormatting color, int priority) {
            this.displayName = displayName;
            this.color = color;
            this.priority = priority;
        }
        
        public String getDisplayName() { return displayName; }
        public ChatFormatting getColor() { return color; }
        public int getPriority() { return priority; }
    }
    
    public static void setPlayerRank(UUID uuid, PlayerRank rank) {
        playerRanks.put(uuid, rank);
    }
    
    public static PlayerRank getPlayerRank(UUID uuid) {
        return playerRanks.getOrDefault(uuid, PlayerRank.BEGINNER);
    }
    
    public static boolean setRankByName(String playerName, String rankName, ServerPlayer executor) {
        try {
            PlayerRank rank = PlayerRank.valueOf(rankName.toUpperCase());
            // Find player by name
            ServerPlayer target = executor.getServer().getPlayerManager().getPlayer(playerName);
            if (target == null) {
                executor.sendMessage(Component.literal("Player '" + playerName + "' not found!")
                    .formatted(ChatFormatting.RED), false);
                return false;
            }
            
            setPlayerRank(target.getUuid(), rank);
            executor.sendMessage(Component.literal("Set " + target.getName().getString() + "'s rank to " + rank.getDisplayName())
                .formatted(ChatFormatting.GREEN), false);
            target.sendMessage(Component.literal("Your rank has been set to " + rank.getDisplayName() + "!")
                .formatted(ChatFormatting.GREEN), false);
            return true;
        } catch (IllegalArgumentException e) {
            executor.sendMessage(Component.literal("Invalid rank! Available ranks: BEGINNER, YOUTUBER, CREATOR")
                .formatted(ChatFormatting.RED), false);
            return false;
        }
    }
    
    public static Component getPlayerDisplayName(ServerPlayer player) {
        PlayerRank rank = getPlayerRank(player.getUuid());
        String guildName = GuildManager.getGuildName(player.getUuid());
        String teamName = TeamManager.getPlayerTeam(player.getUuid());
        
        Component displayName = Component.literal("");
        
        // Add rank prefix
        displayName = displayName.copy().append(Component.literal("[" + rank.getDisplayName() + "] ")
            .formatted(rank.getColor()));
        
        if (guildName != null) {
            ChatFormatting guildColor = GuildManager.getGuildRankColor(player.getUuid());
            String guildRank = GuildManager.getPlayerRank(player.getUuid()).name();
            displayName = displayName.copy().append(Component.literal("[" + guildName + " | " + guildRank + "] ")
                .formatted(guildColor));
        } else if (teamName != null) {
            TeamManager.Team team = TeamManager.getTeam(teamName);
            if (team != null) {
                try {
                    ChatFormatting teamColor = ChatFormatting.valueOf(team.getColor().toUpperCase());
                    displayName = displayName.copy().append(Component.literal("[" + teamName + "] ")
                        .formatted(teamColor));
                } catch (IllegalArgumentException e) {
                    displayName = displayName.copy().append(Component.literal("[" + teamName + "] ")
                        .formatted(ChatFormatting.WHITE));
                }
            }
        }
        
        // Add player name
        displayName = displayName.copy().append(Component.literal(player.getName().getString()));
        
        return displayName;
    }
}
