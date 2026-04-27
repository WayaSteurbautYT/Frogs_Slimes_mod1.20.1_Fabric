package com.wayacreate.frogslimegamemode.dimension;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ModDimensions {
    public static final Identifier TRANSFORMED_END_ID = new Identifier(FrogSlimeGamemode.MOD_ID, "transformed_end");
    public static final RegistryKey<World> TRANSFORMED_END_KEY = RegistryKey.of(RegistryKeys.WORLD, TRANSFORMED_END_ID);
    
    public static void register() {
        FrogSlimeGamemode.LOGGER.info("Registering dimensions for " + FrogSlimeGamemode.MOD_ID);
        FrogSlimeGamemode.LOGGER.info("Transformed End dimension ID: " + TRANSFORMED_END_ID);
        // Dimension is registered via datapack JSON files
        // Teleportation available via /frogslime dimension command
    }
}
