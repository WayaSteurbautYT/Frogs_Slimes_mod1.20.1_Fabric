package com.wayacreate.frogslimegamemode.gamemode;

import com.wayacreate.frogslimegamemode.guild.GuildManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RankManager {
    private static final Map<UUID, PlayerRank> playerRanks = new HashMap<>();
    
    public enum PlayerRank {
        BEGINNER("Beginner", Formatting.GRAY, 0),
        YOUTUBER("YouTuber", Formatting.GOLD, 1),
        CREATOR("Creator", Formatting.DARK_RED, 2);
        
        private final String displayName;
        private final Formatting color;
        private final int priority;
        
        PlayerRank(String displayName, Formatting color, int priority) {
            this.displayName = displayName;
            this.color = color;
            this.priority = priority;
        }
        
        public String getDisplayName() { return displayName; }
        public Formatting getColor() { return color; }
        public int getPriority() { return priority; }
    }
    
    public static void setPlayerRank(UUID uuid, PlayerRank rank) {
        playerRanks.put(uuid, rank);
    }
    
    public static PlayerRank getPlayerRank(UUID uuid) {
        return playerRanks.getOrDefault(uuid, PlayerRank.BEGINNER);
    }
    
    public static boolean setRankByName(String playerName, String rankName, ServerPlayerEntity executor) {
        try {
            PlayerRank rank = PlayerRank.valueOf(rankName.toUpperCase());
            // Find player by name
            ServerPlayerEntity target = executor.getServer().getPlayerManager().getPlayer(playerName);
            if (target == null) {
                executor.sendMessage(Text.literal("Player '" + playerName + "' not found!")
                    .formatted(Formatting.RED), false);
                return false;
            }
            
            setPlayerRank(target.getUuid(), rank);
            executor.sendMessage(Text.literal("Set " + target.getName().getString() + "'s rank to " + rank.getDisplayName())
                .formatted(Formatting.GREEN), false);
            target.sendMessage(Text.literal("Your rank has been set to " + rank.getDisplayName() + "!")
                .formatted(Formatting.GREEN), false);
            return true;
        } catch (IllegalArgumentException e) {
            executor.sendMessage(Text.literal("Invalid rank! Available ranks: BEGINNER, YOUTUBER, CREATOR")
                .formatted(Formatting.RED), false);
            return false;
        }
    }
    
    public static Text getPlayerDisplayName(ServerPlayerEntity player) {
        PlayerRank rank = getPlayerRank(player.getUuid());
        String guildName = GuildManager.getGuildName(player.getUuid());
        String teamName = TeamManager.getPlayerTeam(player.getUuid());
        
        Text displayName = Text.literal("");
        
        // Add rank prefix
        displayName = displayName.copy().append(Text.literal("[" + rank.getDisplayName() + "] ")
            .formatted(rank.getColor()));
        
        if (guildName != null) {
            Formatting guildColor = GuildManager.getGuildRankColor(player.getUuid());
            String guildRank = GuildManager.getPlayerRank(player.getUuid()).name();
            displayName = displayName.copy().append(Text.literal("[" + guildName + " | " + guildRank + "] ")
                .formatted(guildColor));
        } else if (teamName != null) {
            TeamManager.Team team = TeamManager.getTeam(teamName);
            if (team != null) {
                try {
                    Formatting teamColor = Formatting.valueOf(team.getColor().toUpperCase());
                    displayName = displayName.copy().append(Text.literal("[" + teamName + "] ")
                        .formatted(teamColor));
                } catch (IllegalArgumentException e) {
                    displayName = displayName.copy().append(Text.literal("[" + teamName + "] ")
                        .formatted(Formatting.WHITE));
                }
            }
        }
        
        // Add player name
        displayName = displayName.copy().append(Text.literal(player.getName().getString()));
        
        return displayName;
    }
}
