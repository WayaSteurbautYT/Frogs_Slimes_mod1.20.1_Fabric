package com.wayacreate.frogslimegamemode.gamemode;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.*;

public class TeamManager {
    private static final Map<String, Team> teams = new HashMap<>();
    private static final Map<UUID, String> playerToTeam = new HashMap<>();
    
    public static class Team {
        private final String name;
        private final Set<UUID> members = new HashSet<>();
        private final String color;
        
        public Team(String name, String color) {
            this.name = name;
            this.color = color;
        }
        
        public String getName() { return name; }
        public String getColor() { return color; }
        public Set<UUID> getMembers() { return members; }
        
        public void addMember(UUID uuid) { members.add(uuid); }
        public void removeMember(UUID uuid) { members.remove(uuid); }
        public boolean hasMember(UUID uuid) { return members.contains(uuid); }
    }
    
    public static boolean createTeam(String name, String color, ServerPlayer owner) {
        if (teams.containsKey(name)) {
            return false;
        }
        
        Team team = new Team(name, color);
        team.addMember(owner.getUuid());
        teams.put(name, team);
        playerToTeam.put(owner.getUuid(), name);
        
        owner.sendMessage(Component.literal("Team '" + name + "' created!")
            .formatted(ChatFormatting.GREEN), false);
        return true;
    }
    
    public static boolean joinTeam(String name, ServerPlayer player) {
        Team team = teams.get(name);
        if (team == null) {
            player.sendMessage(Component.literal("Team '" + name + "' does not exist!")
                .formatted(ChatFormatting.RED), false);
            return false;
        }
        
        String currentTeam = playerToTeam.get(player.getUuid());
        if (currentTeam != null) {
            leaveTeam(player);
        }
        
        team.addMember(player.getUuid());
        playerToTeam.put(player.getUuid(), name);
        
        // Notify all team members
        for (UUID memberUuid : team.getMembers()) {
            ServerPlayer member = player.getServer().getPlayerManager().getPlayer(memberUuid);
            if (member != null) {
                member.sendMessage(Component.literal(player.getName().getString() + " joined the team!")
                    .formatted(ChatFormatting.YELLOW), false);
            }
        }
        
        player.sendMessage(Component.literal("You joined team '" + name + "'!")
            .formatted(ChatFormatting.GREEN), false);
        return true;
    }
    
    public static boolean leaveTeam(ServerPlayer player) {
        String teamName = playerToTeam.get(player.getUuid());
        if (teamName == null) {
            player.sendMessage(Component.literal("You are not in a team!")
                .formatted(ChatFormatting.RED), false);
            return false;
        }
        
        Team team = teams.get(teamName);
        if (team != null) {
            team.removeMember(player.getUuid());
            
            // If team is empty, remove it
            if (team.getMembers().isEmpty()) {
                teams.remove(teamName);
            }
        }
        
        playerToTeam.remove(player.getUuid());
        player.sendMessage(Component.literal("You left the team.")
            .formatted(ChatFormatting.YELLOW), false);
        return true;
    }
    
    public static String getPlayerTeam(UUID uuid) {
        return playerToTeam.get(uuid);
    }
    
    public static Team getTeam(String name) {
        return teams.get(name);
    }
    
    public static void listTeams(ServerPlayer player) {
        if (teams.isEmpty()) {
            player.sendMessage(Component.literal("No teams exist yet.")
                .formatted(ChatFormatting.GRAY), false);
            return;
        }
        
        player.sendMessage(Component.literal("Teams:").formatted(ChatFormatting.BOLD), false);
        for (Team team : teams.values()) {
            try {
                ChatFormatting color = ChatFormatting.valueOf(team.getColor().toUpperCase());
                player.sendMessage(Component.literal("- " + team.getName() + " (" + team.getMembers().size() + " members)")
                    .formatted(color), false);
            } catch (IllegalArgumentException e) {
                // Fallback to white if color is invalid
                player.sendMessage(Component.literal("- " + team.getName() + " (" + team.getMembers().size() + " members)")
                    .formatted(ChatFormatting.WHITE), false);
            }
        }
    }
    
    public static void teleportToTeamMember(ServerPlayer player, String targetName) {
        String teamName = playerToTeam.get(player.getUuid());
        if (teamName == null) {
            player.sendMessage(Component.literal("You are not in a team!")
                .formatted(ChatFormatting.RED), false);
            return;
        }
        
        Team team = teams.get(teamName);
        if (team == null) return;
        
        ServerPlayer target = null;
        for (UUID memberUuid : team.getMembers()) {
            ServerPlayer member = player.getServer().getPlayerManager().getPlayer(memberUuid);
            if (member != null && member.getName().getString().equalsIgnoreCase(targetName)) {
                target = member;
                break;
            }
        }
        
        if (target == null) {
            player.sendMessage(Component.literal("Team member '" + targetName + "' not found or offline.")
                .formatted(ChatFormatting.RED), false);
            return;
        }
        
        if (target == player) {
            player.sendMessage(Component.literal("You cannot teleport to yourself!")
                .formatted(ChatFormatting.RED), false);
            return;
        }
        
        player.teleport(target.getServerWorld(), target.getX(), target.getY(), target.getZ(), target.getYaw(), target.getPitch());
        player.sendMessage(Component.literal("Teleported to " + target.getName().getString() + "!")
            .formatted(ChatFormatting.GREEN), false);
    }
}
