package com.wayacreate.frogslimegamemode;

import com.wayacreate.frogslimegamemode.achievements.AchievementManager;
import com.wayacreate.frogslimegamemode.command.FrogSlimeCommand;
import com.wayacreate.frogslimegamemode.command.HelperCommand;
import com.wayacreate.frogslimegamemode.eating.EatingSystem;
import com.wayacreate.frogslimegamemode.entity.ModEntities;
import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import com.wayacreate.frogslimegamemode.gamemode.ModGameRules;
import com.wayacreate.frogslimegamemode.item.ModItems;
import com.wayacreate.frogslimegamemode.network.ModNetworking;
import com.wayacreate.frogslimegamemode.tasks.TaskManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
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
        ModItems.register();
        ModNetworking.registerServer();
        TaskManager.init();
        AchievementManager.init();
        ModGameRules.register();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            FrogSlimeCommand.register(dispatcher);
            HelperCommand.register(dispatcher, registryAccess, environment);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            GamemodeManager.tick(server);
            EatingSystem.tick(server);
            TaskManager.tick(server);
        });

        LOGGER.info("Frog & Slime Gamemode initialized! Prepare for an unexpected ending...");
    }
}