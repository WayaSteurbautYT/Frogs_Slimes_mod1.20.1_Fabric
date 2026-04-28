package com.wayacreate.frogslimegamemode;

import com.wayacreate.frogslimegamemode.abilities.PlayerAbilityManager;
import com.wayacreate.frogslimegamemode.achievements.AchievementManager;
import com.wayacreate.frogslimegamemode.block.ModBlocks;
import com.wayacreate.frogslimegamemode.command.BountyCommand;
import com.wayacreate.frogslimegamemode.command.EconomyCommands;
import com.wayacreate.frogslimegamemode.command.FrogSlimeCommand;
import com.wayacreate.frogslimegamemode.command.GuildCommand;
import com.wayacreate.frogslimegamemode.command.HelperCommand;
import com.wayacreate.frogslimegamemode.economy.BountyManager;
import com.wayacreate.frogslimegamemode.crafting.AnvilRecipeHandler;
import com.wayacreate.frogslimegamemode.dimension.ModDimensions;
import com.wayacreate.frogslimegamemode.eating.EatingSystem;
import com.wayacreate.frogslimegamemode.entity.ModEntities;
import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import com.wayacreate.frogslimegamemode.gamemode.ManhuntManager;
import com.wayacreate.frogslimegamemode.gamemode.ModGameRules;
import com.wayacreate.frogslimegamemode.gamemode.RankManager;
import com.wayacreate.frogslimegamemode.item.ModItems;
import com.wayacreate.frogslimegamemode.item.ModPotions;
import com.wayacreate.frogslimegamemode.network.ModNetworking;
import com.wayacreate.frogslimegamemode.tasks.TaskManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrogSlimeGamemode implements ModInitializer {
    public static final String MOD_ID = "frogslimegamemode";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Frog & Slime Gamemode by WayaCreate!");

        // Register entities first (items depend on entity types)
        ModEntities.register();
        ModBlocks.register();
        ModItems.register();
        ModPotions.register();
        ModDimensions.register();
        ModNetworking.registerServer();
        TaskManager.init();
        AchievementManager.init();
        ModGameRules.register();
        AnvilRecipeHandler.register();
        
        FrogSlimeGamemode.LOGGER.info("Dimension teleportation available via /frogslime dimension command");

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            FrogSlimeCommand.register(dispatcher);
            HelperCommand.register(dispatcher, registryAccess, environment);
            EconomyCommands.register(dispatcher);
            GuildCommand.register(dispatcher);
            BountyCommand.register(dispatcher);
        });

        // Register chat message event for rank and team display
        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, player, params) -> {
            if (player != null) {
                // Send a custom message with rank/team prefix to all players
                net.minecraft.text.Text displayName = RankManager.getPlayerDisplayName(player);
                net.minecraft.text.Text formattedMessage = net.minecraft.text.Text.literal("")
                    .append(displayName)
                    .append(net.minecraft.text.Text.literal(": "))
                    .append(net.minecraft.text.Text.literal(message.getContent().getString()));
                
                // Send to all players
                player.getServer().getPlayerManager().broadcast(formattedMessage, false);
                
                // Return false to prevent the original message from being sent
                return false;
            }
            return true;
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            GamemodeManager.tick(server);
            EatingSystem.tick(server);
            TaskManager.tick(server);
            PlayerAbilityManager.tick();
            ManhuntManager.tick(server);
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            // Save all player data when server stops
            for (var player : server.getPlayerManager().getPlayerList()) {
                GamemodeManager.onPlayerLeave(player);
            }
        });
        
        // Register player death event for bounties
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof ServerPlayerEntity player && 
                damageSource.getAttacker() instanceof ServerPlayerEntity killer) {
                BountyManager.onPlayerDeath(player, killer);
            }
        });

        LOGGER.info("Frog & Slime Gamemode initialized! Prepare for an unexpected ending...");
    }
}