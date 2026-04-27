package com.wayacreate.frogslimegamemode.gamemode;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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
    
    public static boolean createTeam(String name, String color, ServerPlayerEntity owner) {
        if (teams.containsKey(name)) {
            return false;
        }
        
        Team team = new Team(name, color);
        team.addMember(owner.getUuid());
        teams.put(name, team);
        playerToTeam.put(owner.getUuid(), name);
        
        owner.sendMessage(Text.literal("Team '" + name + "' created!")
            .formatted(Formatting.GREEN), false);
        return true;
    }
    
    public static boolean joinTeam(String name, ServerPlayerEntity player) {
        Team team = teams.get(name);
        if (team == null) {
            player.sendMessage(Text.literal("Team '" + name + "' does not exist!")
                .formatted(Formatting.RED), false);
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
            ServerPlayerEntity member = player.getServer().getPlayerManager().getPlayer(memberUuid);
            if (member != null) {
                member.sendMessage(Text.literal(player.getName().getString() + " joined the team!")
                    .formatted(Formatting.YELLOW), false);
            }
        }
        
        player.sendMessage(Text.literal("You joined team '" + name + "'!")
            .formatted(Formatting.GREEN), false);
        return true;
    }
    
    public static boolean leaveTeam(ServerPlayerEntity player) {
        String teamName = playerToTeam.get(player.getUuid());
        if (teamName == null) {
            player.sendMessage(Text.literal("You are not in a team!")
                .formatted(Formatting.RED), false);
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
        player.sendMessage(Text.literal("You left the team.")
            .formatted(Formatting.YELLOW), false);
        return true;
    }
    
    public static String getPlayerTeam(UUID uuid) {
        return playerToTeam.get(uuid);
    }
    
    public static Team getTeam(String name) {
        return teams.get(name);
    }
    
    public static void listTeams(ServerPlayerEntity player) {
        if (teams.isEmpty()) {
            player.sendMessage(Text.literal("No teams exist yet.")
                .formatted(Formatting.GRAY), false);
            return;
        }
        
        player.sendMessage(Text.literal("Teams:").formatted(Formatting.BOLD), false);
        for (Team team : teams.values()) {
            try {
                Formatting color = Formatting.valueOf(team.getColor().toUpperCase());
                player.sendMessage(Text.literal("- " + team.getName() + " (" + team.getMembers().size() + " members)")
                    .formatted(color), false);
            } catch (IllegalArgumentException e) {
                // Fallback to white if color is invalid
                player.sendMessage(Text.literal("- " + team.getName() + " (" + team.getMembers().size() + " members)")
                    .formatted(Formatting.WHITE), false);
            }
        }
    }
    
    public static void teleportToTeamMember(ServerPlayerEntity player, String targetName) {
        String teamName = playerToTeam.get(player.getUuid());
        if (teamName == null) {
            player.sendMessage(Text.literal("You are not in a team!")
                .formatted(Formatting.RED), false);
            return;
        }
        
        Team team = teams.get(teamName);
        if (team == null) return;
        
        ServerPlayerEntity target = null;
        for (UUID memberUuid : team.getMembers()) {
            ServerPlayerEntity member = player.getServer().getPlayerManager().getPlayer(memberUuid);
            if (member != null && member.getName().getString().equalsIgnoreCase(targetName)) {
                target = member;
                break;
            }
        }
        
        if (target == null) {
            player.sendMessage(Text.literal("Team member '" + targetName + "' not found or offline.")
                .formatted(Formatting.RED), false);
            return;
        }
        
        if (target == player) {
            player.sendMessage(Text.literal("You cannot teleport to yourself!")
                .formatted(Formatting.RED), false);
            return;
        }
        
        player.teleport(target.getServerWorld(), target.getX(), target.getY(), target.getZ(), target.getYaw(), target.getPitch());
        player.sendMessage(Text.literal("Teleported to " + target.getName().getString() + "!")
            .formatted(Formatting.GREEN), false);
    }
}
