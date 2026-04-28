package com.wayacreate.frogslimegamemode;

import com.wayacreate.frogslimegamemode.abilities.PlayerAbilityManager;
import com.wayacreate.frogslimegamemode.achievements.AchievementManager;
import com.wayacreate.frogslimegamemode.block.ModBlocks;
import com.wayacreate.frogslimegamemode.command.BountyCommand;
import com.wayacreate.frogslimegamemode.command.EconomyCommands;
import com.wayacreate.frogslimegamemode.command.FrogSlimeCommand;
import com.wayacreate.frogslimegamemode.command.GuildCommand;
import com.wayacreate.frogslimegamemode.command.HelperCommand;
import com.wayacreate.frogslimegamemode.crafting.AnvilRecipeHandler;
import com.wayacreate.frogslimegamemode.dimension.ModDimensions;
import com.wayacreate.frogslimegamemode.eating.EatingSystem;
import com.wayacreate.frogslimegamemode.economy.BountyManager;
import com.wayacreate.frogslimegamemode.entity.ModEntities;
import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import com.wayacreate.frogslimegamemode.gamemode.ManhuntManager;
import com.wayacreate.frogslimegamemode.gamemode.ModGameRules;
import com.wayacreate.frogslimegamemode.gamemode.RankManager;
import com.wayacreate.frogslimegamemode.item.ModItems;
import com.wayacreate.frogslimegamemode.item.ModPotions;
import com.wayacreate.frogslimegamemode.network.ModNetworking;
import com.wayacreate.frogslimegamemode.tasks.TaskManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(FrogSlimeGamemode.MOD_ID)
public class FrogSlimeGamemode {
    public static final String MOD_ID = "frogslimegamemode";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public FrogSlimeGamemode(IEventBus modBus) {
        LOGGER.info("Initializing Frog & Slime Gamemode by WayaCreate!");

        ModEntities.register(modBus);
        ModBlocks.register(modBus);
        ModItems.register(modBus);
        ModPotions.register(modBus);
        ModNetworking.register(modBus);
        ModDimensions.register();
        TaskManager.init();
        AchievementManager.init();
        ModGameRules.register();
        AnvilRecipeHandler.register();

        NeoForge.EVENT_BUS.addListener(FrogSlimeGamemode::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(FrogSlimeGamemode::onServerChat);
        NeoForge.EVENT_BUS.addListener(FrogSlimeGamemode::onServerTick);
        NeoForge.EVENT_BUS.addListener(FrogSlimeGamemode::onServerStopping);
        NeoForge.EVENT_BUS.addListener(FrogSlimeGamemode::onLivingDeath);

        LOGGER.info("Dimension teleportation available via /frogslime dimension command");
        LOGGER.info("Frog & Slime Gamemode initialized! Prepare for an unexpected ending...");
    }

    private static void onRegisterCommands(RegisterCommandsEvent event) {
        FrogSlimeCommand.register(event.getDispatcher());
        HelperCommand.register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
        EconomyCommands.register(event.getDispatcher());
        GuildCommand.register(event.getDispatcher());
        BountyCommand.register(event.getDispatcher());
    }

    private static void onServerChat(ServerChatEvent event) {
        Component displayName = RankManager.getPlayerDisplayName(event.getPlayer());
        Component formattedMessage = Component.literal("")
            .append(displayName)
            .append(Component.literal(": "))
            .append(Component.literal(event.getRawText()));
        event.setMessage(formattedMessage);
    }

    private static void onServerTick(ServerTickEvent.Post event) {
        var server = event.getServer();
        GamemodeManager.tick(server);
        EatingSystem.tick(server);
        TaskManager.tick(server);
        PlayerAbilityManager.tick();
        ManhuntManager.tick(server);
    }

    private static void onServerStopping(ServerStoppingEvent event) {
        for (var player : event.getServer().getPlayerManager().getPlayerList()) {
            GamemodeManager.onPlayerLeave(player);
        }
    }

    private static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player
            && event.getSource().getAttacker() instanceof ServerPlayer killer) {
            BountyManager.onPlayerDeath(player, killer);
        }
    }
}
