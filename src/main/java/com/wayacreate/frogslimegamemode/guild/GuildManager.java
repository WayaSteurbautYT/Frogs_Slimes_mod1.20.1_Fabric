package com.wayacreate.frogslimegamemode.guild;

import com.wayacreate.frogslimegamemode.economy.EconomyManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GuildManager {
    private static final Map<UUID, Guild> guildsById = new ConcurrentHashMap<>();
    private static final Map<UUID, UUID> playerGuilds = new ConcurrentHashMap<>(); // playerUuid -> guildId
    private static final Map<UUID, Set<UUID>> guildInvites = new ConcurrentHashMap<>(); // playerUuid -> set of guilds
    
    public static Guild createGuild(String name, ServerPlayerEntity owner) {
        // Check if player is already in a guild
        if (playerGuilds.containsKey(owner.getUuid())) {
            owner.sendMessage(Text.literal("You are already in a guild! Leave it first.").formatted(Formatting.RED), false);
            return null;
        }
        
        // Check name length
        if (name.length() < 3 || name.length() > 20) {
            owner.sendMessage(Text.literal("Guild name must be 3-20 characters!").formatted(Formatting.RED), false);
            return null;
        }
        
        // Check if name is taken
        for (Guild guild : guildsById.values()) {
            if (guild.getName().equalsIgnoreCase(name)) {
                owner.sendMessage(Text.literal("A guild with that name already exists!").formatted(Formatting.RED), false);
                return null;
            }
        }
        
        Guild guild = new Guild(name, owner.getUuid());
        guildsById.put(guild.getId(), guild);
        playerGuilds.put(owner.getUuid(), guild.getId());
        
        owner.sendMessage(Text.literal("Created guild: ").formatted(Formatting.GREEN)
            .append(Text.literal(name).formatted(Formatting.GOLD)), false);
        
        return guild;
    }
    
    public static boolean disbandGuild(ServerPlayerEntity player) {
        Guild guild = getPlayerGuild(player);
        
        if (guild == null) {
            player.sendMessage(Text.literal("You are not in a guild!").formatted(Formatting.RED), false);
            return false;
        }
        
        if (!guild.getOwnerUuid().equals(player.getUuid())) {
            player.sendMessage(Text.literal("Only the guild owner can disband!").formatted(Formatting.RED), false);
            return false;
        }
        
        // Remove all members
        for (UUID member : guild.getMembers()) {
            playerGuilds.remove(member);
        }
        
        guildsById.remove(guild.getId());
        
        player.sendMessage(Text.literal("Disbanded guild: ").formatted(Formatting.RED)
            .append(Text.literal(guild.getName()).formatted(Formatting.GOLD)), false);
        
        return true;
    }
    
    public static boolean invitePlayer(ServerPlayerEntity inviter, ServerPlayerEntity target) {
        Guild guild = getPlayerGuild(inviter);
        
        if (guild == null) {
            inviter.sendMessage(Text.literal("You are not in a guild!").formatted(Formatting.RED), false);
            return false;
        }
        
        if (!guild.hasPermission(inviter.getUuid(), Guild.GuildRank.OFFICER)) {
            inviter.sendMessage(Text.literal("Only officers and above can invite!").formatted(Formatting.RED), false);
            return false;
        }
        
        if (playerGuilds.containsKey(target.getUuid())) {
            inviter.sendMessage(Text.literal("That player is already in a guild!").formatted(Formatting.RED), false);
            return false;
        }
        
        // Add invite
        guildInvites.computeIfAbsent(target.getUuid(), k -> ConcurrentHashMap.newKeySet()).add(guild.getId());
        
        inviter.sendMessage(Text.literal("Invited ").formatted(Formatting.GREEN)
            .append(target.getName().getString())
            .append(Text.literal(" to your guild!")), false);
        
        target.sendMessage(Text.literal("").formatted(Formatting.YELLOW)
            .append(inviter.getName().getString())
            .append(Text.literal(" invited you to join "))
            .append(Text.literal(guild.getName()).formatted(Formatting.GOLD))
            .append(Text.literal("! "))
            .append(Text.literal("[Accept]").formatted(Formatting.GREEN)
                .styled(s -> s.withClickEvent(
                    new net.minecraft.text.ClickEvent(net.minecraft.text.ClickEvent.Action.RUN_COMMAND, 
                        "/guild join " + guild.getName())))),
            false);
        
        return true;
    }
    
    public static boolean joinGuild(ServerPlayerEntity player, String guildName) {
        // Find guild
        Guild guild = null;
        for (Guild g : guildsById.values()) {
            if (g.getName().equalsIgnoreCase(guildName)) {
                guild = g;
                break;
            }
        }
        
        if (guild == null) {
            player.sendMessage(Text.literal("Guild not found!").formatted(Formatting.RED), false);
            return false;
        }
        
        // Check if invited
        Set<UUID> invites = guildInvites.getOrDefault(player.getUuid(), new HashSet<>());
        if (!invites.contains(guild.getId())) {
            player.sendMessage(Text.literal("You have not been invited to this guild!").formatted(Formatting.RED), false);
            return false;
        }
        
        // Check if already in guild
        if (playerGuilds.containsKey(player.getUuid())) {
            player.sendMessage(Text.literal("You are already in a guild!").formatted(Formatting.RED), false);
            return false;
        }
        
        // Add to guild
        guild.addMember(player.getUuid());
        playerGuilds.put(player.getUuid(), guild.getId());
        invites.remove(guild.getId());
        
        player.sendMessage(Text.literal("Joined guild: ").formatted(Formatting.GREEN)
            .append(Text.literal(guild.getName()).formatted(Formatting.GOLD)), false);
        
        // Notify members
        for (UUID memberId : guild.getMembers()) {
            if (!memberId.equals(player.getUuid())) {
                ServerPlayerEntity member = player.getServer().getPlayerManager().getPlayer(memberId);
                if (member != null) {
                    member.sendMessage(Text.literal("").formatted(Formatting.YELLOW)
                        .append(player.getName().getString())
                        .append(Text.literal(" joined the guild!")), false);
                }
            }
        }
        
        return true;
    }
    
    public static boolean leaveGuild(ServerPlayerEntity player) {
        Guild guild = getPlayerGuild(player);
        
        if (guild == null) {
            player.sendMessage(Text.literal("You are not in a guild!").formatted(Formatting.RED), false);
            return false;
        }
        
        if (guild.getOwnerUuid().equals(player.getUuid())) {
            player.sendMessage(Text.literal("You must disband the guild or transfer ownership first!").formatted(Formatting.RED), false);
            return false;
        }
        
        guild.removeMember(player.getUuid());
        playerGuilds.remove(player.getUuid());
        
        player.sendMessage(Text.literal("Left guild: ").formatted(Formatting.RED)
            .append(Text.literal(guild.getName()).formatted(Formatting.GOLD)), false);
        
        return true;
    }
    
    public static Guild getPlayerGuild(ServerPlayerEntity player) {
        UUID guildId = playerGuilds.get(player.getUuid());
        if (guildId == null) {
            return null;
        }
        return guildsById.get(guildId);
    }
    
    public static Guild getGuildById(UUID guildId) {
        return guildsById.get(guildId);
    }
    
    public static Collection<Guild> getAllGuilds() {
        return new ArrayList<>(guildsById.values());
    }
    
    public static boolean contributeToMission(ServerPlayerEntity player, UUID missionId, List<net.minecraft.item.ItemStack> items) {
        Guild guild = getPlayerGuild(player);
        
        if (guild == null) {
            player.sendMessage(Text.literal("You are not in a guild!").formatted(Formatting.RED), false);
            return false;
        }
        
        GuildMission mission = null;
        for (GuildMission m : guild.getMissions()) {
            if (m.getId().equals(missionId)) {
                mission = m;
                break;
            }
        }
        
        if (mission == null) {
            player.sendMessage(Text.literal("Mission not found!").formatted(Formatting.RED), false);
            return false;
        }
        
        if (!mission.isActive()) {
            player.sendMessage(Text.literal("This mission has expired!").formatted(Formatting.RED), false);
            return false;
        }
        
        if (mission.isCompletedBy(player.getUuid())) {
            player.sendMessage(Text.literal("You have already completed this mission!").formatted(Formatting.YELLOW), false);
            return false;
        }
        
        // Check if player has contributed all required items
        if (mission.hasPlayerContributed(player.getUuid(), items)) {
            // Mark complete and give rewards
            mission.markCompleted(player.getUuid());
            
            // Give rewards
            for (net.minecraft.item.ItemStack reward : mission.getItemRewards()) {
                player.getInventory().offerOrDrop(reward);
            }
            
            if (mission.getCoinReward() > 0) {
                EconomyManager.addBalance(player, mission.getCoinReward());
            }
            
            player.sendMessage(Text.literal("Mission completed! ").formatted(Formatting.GREEN)
                .append(Text.literal("Rewards received!").formatted(Formatting.GOLD)), false);
            
            return true;
        } else {
            player.sendMessage(Text.literal("You haven't contributed all required items!").formatted(Formatting.RED), false);
            return false;
        }
    }
    
    public static boolean createMission(ServerPlayerEntity player, GuildMission mission) {
        Guild guild = getPlayerGuild(player);
        
        if (guild == null) {
            player.sendMessage(Text.literal("You are not in a guild!").formatted(Formatting.RED), false);
            return false;
        }
        
        if (!guild.hasPermission(player.getUuid(), Guild.GuildRank.OFFICER)) {
            player.sendMessage(Text.literal("Only officers and above can create missions!").formatted(Formatting.RED), false);
            return false;
        }
        
        // Cost to create mission (from guild coins)
        int cost = 100;
        if (!guild.removeCoins(cost)) {
            player.sendMessage(Text.literal("Guild doesn't have enough coins! (Need " + cost + ")").formatted(Formatting.RED), false);
            return false;
        }
        
        guild.addMission(mission);
        
        player.sendMessage(Text.literal("Created mission: ").formatted(Formatting.GREEN)
            .append(Text.literal(mission.getName()).formatted(Formatting.GOLD)), false);
        
        return true;
    }
}
