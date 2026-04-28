package com.wayacreate.frogslimegamemode.guild;

import com.wayacreate.frogslimegamemode.economy.EconomyManager;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GuildManager {
    private static final Map<UUID, Guild> guildsById = new ConcurrentHashMap<>();
    private static final Map<UUID, UUID> playerGuilds = new ConcurrentHashMap<>(); // playerUuid -> guildId
    private static final Map<UUID, Set<UUID>> guildInvites = new ConcurrentHashMap<>(); // playerUuid -> set of guilds
    
    public static Guild createGuild(String name, ServerPlayer owner) {
        // Check if player is already in a guild
        if (playerGuilds.containsKey(owner.getUuid())) {
            owner.sendMessage(Component.literal("You are already in a guild! Leave it first.").formatted(ChatFormatting.RED), false);
            return null;
        }
        
        // Check name length
        if (name.length() < 3 || name.length() > 20) {
            owner.sendMessage(Component.literal("Guild name must be 3-20 characters!").formatted(ChatFormatting.RED), false);
            return null;
        }
        
        // Check if name is taken
        for (Guild guild : guildsById.values()) {
            if (guild.getName().equalsIgnoreCase(name)) {
                owner.sendMessage(Component.literal("A guild with that name already exists!").formatted(ChatFormatting.RED), false);
                return null;
            }
        }
        
        Guild guild = new Guild(name, owner.getUuid());
        guildsById.put(guild.getId(), guild);
        playerGuilds.put(owner.getUuid(), guild.getId());
        
        owner.sendMessage(Component.literal("Created guild: ").formatted(ChatFormatting.GREEN)
            .append(Component.literal(name).formatted(ChatFormatting.GOLD)), false);
        
        return guild;
    }
    
    public static boolean disbandGuild(ServerPlayer player) {
        Guild guild = getPlayerGuild(player);
        
        if (guild == null) {
            player.sendMessage(Component.literal("You are not in a guild!").formatted(ChatFormatting.RED), false);
            return false;
        }
        
        if (!guild.getOwnerUuid().equals(player.getUuid())) {
            player.sendMessage(Component.literal("Only the guild owner can disband!").formatted(ChatFormatting.RED), false);
            return false;
        }
        
        // Remove all members
        for (UUID member : guild.getMembers()) {
            playerGuilds.remove(member);
        }
        
        guildsById.remove(guild.getId());
        
        player.sendMessage(Component.literal("Disbanded guild: ").formatted(ChatFormatting.RED)
            .append(Component.literal(guild.getName()).formatted(ChatFormatting.GOLD)), false);
        
        return true;
    }
    
    public static boolean invitePlayer(ServerPlayer inviter, ServerPlayer target) {
        Guild guild = getPlayerGuild(inviter);
        
        if (guild == null) {
            inviter.sendMessage(Component.literal("You are not in a guild!").formatted(ChatFormatting.RED), false);
            return false;
        }
        
        if (!guild.hasPermission(inviter.getUuid(), Guild.GuildRank.OFFICER)) {
            inviter.sendMessage(Component.literal("Only officers and above can invite!").formatted(ChatFormatting.RED), false);
            return false;
        }
        
        if (playerGuilds.containsKey(target.getUuid())) {
            inviter.sendMessage(Component.literal("That player is already in a guild!").formatted(ChatFormatting.RED), false);
            return false;
        }
        
        // Add invite
        guildInvites.computeIfAbsent(target.getUuid(), k -> ConcurrentHashMap.newKeySet()).add(guild.getId());
        
        inviter.sendMessage(Component.literal("Invited ").formatted(ChatFormatting.GREEN)
            .append(target.getName().getString())
            .append(Component.literal(" to your guild!")), false);
        
        target.sendMessage(Component.literal("").formatted(ChatFormatting.YELLOW)
            .append(inviter.getName().getString())
            .append(Component.literal(" invited you to join "))
            .append(Component.literal(guild.getName()).formatted(ChatFormatting.GOLD))
            .append(Component.literal("! "))
            .append(Component.literal("[Accept]").formatted(ChatFormatting.GREEN)
                .styled(s -> s.withClickEvent(
                    new net.minecraft.text.ClickEvent(net.minecraft.text.ClickEvent.Action.RUN_COMMAND, 
                        "/frogslime guild join " + guild.getName())))),
            false);
        
        return true;
    }
    
    public static boolean joinGuild(ServerPlayer player, String guildName) {
        // Find guild
        Guild guild = null;
        for (Guild g : guildsById.values()) {
            if (g.getName().equalsIgnoreCase(guildName)) {
                guild = g;
                break;
            }
        }
        
        if (guild == null) {
            player.sendMessage(Component.literal("Guild not found!").formatted(ChatFormatting.RED), false);
            return false;
        }
        
        // Check if invited
        Set<UUID> invites = guildInvites.getOrDefault(player.getUuid(), new HashSet<>());
        if (!invites.contains(guild.getId())) {
            player.sendMessage(Component.literal("You have not been invited to this guild!").formatted(ChatFormatting.RED), false);
            return false;
        }
        
        // Check if already in guild
        if (playerGuilds.containsKey(player.getUuid())) {
            player.sendMessage(Component.literal("You are already in a guild!").formatted(ChatFormatting.RED), false);
            return false;
        }
        
        // Add to guild
        guild.addMember(player.getUuid());
        playerGuilds.put(player.getUuid(), guild.getId());
        invites.remove(guild.getId());
        
        player.sendMessage(Component.literal("Joined guild: ").formatted(ChatFormatting.GREEN)
            .append(Component.literal(guild.getName()).formatted(ChatFormatting.GOLD)), false);
        
        // Notify members
        for (UUID memberId : guild.getMembers()) {
            if (!memberId.equals(player.getUuid())) {
                ServerPlayer member = player.getServer().getPlayerManager().getPlayer(memberId);
                if (member != null) {
                    member.sendMessage(Component.literal("").formatted(ChatFormatting.YELLOW)
                        .append(player.getName().getString())
                        .append(Component.literal(" joined the guild!")), false);
                }
            }
        }
        
        return true;
    }
    
    public static boolean leaveGuild(ServerPlayer player) {
        Guild guild = getPlayerGuild(player);
        
        if (guild == null) {
            player.sendMessage(Component.literal("You are not in a guild!").formatted(ChatFormatting.RED), false);
            return false;
        }
        
        if (guild.getOwnerUuid().equals(player.getUuid())) {
            player.sendMessage(Component.literal("You must disband the guild or transfer ownership first!").formatted(ChatFormatting.RED), false);
            return false;
        }
        
        guild.removeMember(player.getUuid());
        playerGuilds.remove(player.getUuid());
        
        player.sendMessage(Component.literal("Left guild: ").formatted(ChatFormatting.RED)
            .append(Component.literal(guild.getName()).formatted(ChatFormatting.GOLD)), false);
        
        return true;
    }
    
    public static Guild getPlayerGuild(ServerPlayer player) {
        return getPlayerGuild(player.getUuid());
    }

    public static Guild getPlayerGuild(UUID playerUuid) {
        UUID guildId = playerGuilds.get(playerUuid);
        if (guildId == null) {
            return null;
        }
        return guildsById.get(guildId);
    }

    public static Guild.GuildRank getPlayerRank(UUID playerUuid) {
        Guild guild = getPlayerGuild(playerUuid);
        return guild != null ? guild.getRank(playerUuid) : null;
    }

    public static ChatFormatting getGuildRankColor(UUID playerUuid) {
        Guild.GuildRank rank = getPlayerRank(playerUuid);
        if (rank == null) {
            return ChatFormatting.WHITE;
        }

        return switch (rank) {
            case OWNER -> ChatFormatting.GOLD;
            case OFFICER -> ChatFormatting.RED;
            case VETERAN -> ChatFormatting.GREEN;
            case MEMBER -> ChatFormatting.AQUA;
        };
    }

    public static String getGuildName(UUID playerUuid) {
        Guild guild = getPlayerGuild(playerUuid);
        return guild != null ? guild.getName() : null;
    }

    public static Guild getGuildById(UUID guildId) {
        return guildsById.get(guildId);
    }
    
    public static Collection<Guild> getAllGuilds() {
        return new ArrayList<>(guildsById.values());
    }
    
    public static boolean contributeToMission(ServerPlayer player, UUID missionId, List<net.minecraft.world.item.ItemStack> items) {
        Guild guild = getPlayerGuild(player);
        
        if (guild == null) {
            player.sendMessage(Component.literal("You are not in a guild!").formatted(ChatFormatting.RED), false);
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
            player.sendMessage(Component.literal("Mission not found!").formatted(ChatFormatting.RED), false);
            return false;
        }
        
        if (!mission.isActive()) {
            player.sendMessage(Component.literal("This mission has expired!").formatted(ChatFormatting.RED), false);
            return false;
        }
        
        if (mission.isCompletedBy(player.getUuid())) {
            player.sendMessage(Component.literal("You have already completed this mission!").formatted(ChatFormatting.YELLOW), false);
            return false;
        }
        
        // Check if player has contributed all required items
        if (mission.hasPlayerContributed(player.getUuid(), items)) {
            // Mark complete and give rewards
            mission.markCompleted(player.getUuid());
            
            // Give rewards
            for (net.minecraft.world.item.ItemStack reward : mission.getItemRewards()) {
                player.getInventory().offerOrDrop(reward);
            }
            
            if (mission.getCoinReward() > 0) {
                EconomyManager.addBalance(player, mission.getCoinReward());
            }
            
            player.sendMessage(Component.literal("Mission completed! ").formatted(ChatFormatting.GREEN)
                .append(Component.literal("Rewards received!").formatted(ChatFormatting.GOLD)), false);
            
            return true;
        } else {
            player.sendMessage(Component.literal("You haven't contributed all required items!").formatted(ChatFormatting.RED), false);
            return false;
        }
    }

    public static boolean contributeToMissionFromInventory(ServerPlayer player, UUID missionId) {
        Guild guild = getPlayerGuild(player);

        if (guild == null) {
            player.sendMessage(Component.literal("You are not in a guild!").formatted(ChatFormatting.RED), false);
            return false;
        }

        GuildMission mission = null;
        for (GuildMission currentMission : guild.getMissions()) {
            if (currentMission.getId().equals(missionId)) {
                mission = currentMission;
                break;
            }
        }

        if (mission == null) {
            player.sendMessage(Component.literal("Mission not found!").formatted(ChatFormatting.RED), false);
            return false;
        }

        if (!mission.isActive()) {
            player.sendMessage(Component.literal("This mission has expired!").formatted(ChatFormatting.RED), false);
            return false;
        }

        if (mission.isCompletedBy(player.getUuid())) {
            player.sendMessage(Component.literal("You already completed this mission.").formatted(ChatFormatting.YELLOW), false);
            return false;
        }

        if (!hasRequiredItems(player.getInventory(), mission.getRequiredItems())) {
            player.sendMessage(Component.literal("You do not have the required items for this mission!")
                .formatted(ChatFormatting.RED), false);
            return false;
        }

        removeRequiredItems(player.getInventory(), mission.getRequiredItems());
        mission.markCompleted(player.getUuid());

        for (ItemStack reward : mission.getItemRewards()) {
            player.getInventory().offerOrDrop(reward.copy());
        }

        if (mission.getCoinReward() > 0) {
            EconomyManager.addBalance(player, mission.getCoinReward());
        }

        player.sendMessage(Component.literal("Mission completed! Rewards delivered.")
            .formatted(ChatFormatting.GREEN), false);
        return true;
    }
    
    public static boolean createMission(ServerPlayer player, GuildMission mission) {
        Guild guild = getPlayerGuild(player);
        
        if (guild == null) {
            player.sendMessage(Component.literal("You are not in a guild!").formatted(ChatFormatting.RED), false);
            return false;
        }
        
        if (!guild.hasPermission(player.getUuid(), Guild.GuildRank.OFFICER)) {
            player.sendMessage(Component.literal("Only officers and above can create missions!").formatted(ChatFormatting.RED), false);
            return false;
        }
        
        // Cost to create mission (from guild coins)
        int cost = 100;
        if (!guild.removeCoins(cost)) {
            player.sendMessage(Component.literal("Guild doesn't have enough coins! (Need " + cost + ")").formatted(ChatFormatting.RED), false);
            return false;
        }
        
        guild.addMission(mission);
        
        player.sendMessage(Component.literal("Created mission: ").formatted(ChatFormatting.GREEN)
            .append(Component.literal(mission.getName()).formatted(ChatFormatting.GOLD)), false);
        
        return true;
    }

    private static boolean hasRequiredItems(Inventory inventory, List<ItemStack> requiredItems) {
        for (ItemStack requiredItem : requiredItems) {
            int collected = 0;
            for (int slot = 0; slot < inventory.size(); slot++) {
                ItemStack stack = inventory.getStack(slot);
                if (ItemStack.canCombine(stack, requiredItem)) {
                    collected += stack.getCount();
                }
            }

            if (collected < requiredItem.getCount()) {
                return false;
            }
        }

        return true;
    }

    private static void removeRequiredItems(Inventory inventory, List<ItemStack> requiredItems) {
        for (ItemStack requiredItem : requiredItems) {
            int remaining = requiredItem.getCount();
            for (int slot = 0; slot < inventory.size() && remaining > 0; slot++) {
                ItemStack stack = inventory.getStack(slot);
                if (!ItemStack.canCombine(stack, requiredItem)) {
                    continue;
                }

                int removed = Math.min(stack.getCount(), remaining);
                stack.decrement(removed);
                remaining -= removed;
            }
        }
    }
}
