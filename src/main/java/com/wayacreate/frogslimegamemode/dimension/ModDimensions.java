package com.wayacreate.frogslimegamemode.dimension;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class ModDimensions {
    public static final ResourceLocation TRANSFORMED_END_ID = ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "transformed_end");
    public static final ResourceKey<Level> TRANSFORMED_END_KEY = ResourceKey.create(Registries.DIMENSION, TRANSFORMED_END_ID);
    
    public static void register() {
        FrogSlimeGamemode.LOGGER.info("Registering dimensions for " + FrogSlimeGamemode.MOD_ID);
        FrogSlimeGamemode.LOGGER.info("Transformed End dimension ID: " + TRANSFORMED_END_ID);
        // Dimension is registered via datapack JSON files
        // Teleportation available via /frogslime dimension command
    }
}
