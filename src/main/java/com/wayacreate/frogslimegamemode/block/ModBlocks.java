package com.wayacreate.frogslimegamemode.block;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block FROG_CRAFTING_TABLE = registerBlock("frog_crafting_table",
        new FrogCraftingTableBlock(Block.Settings.create()
            .strength(2.5f)
            .requiresTool()));
    
    public static final Block FROG_POTION_STAND = registerBlock("frog_potion_stand",
        new FrogPotionStandBlock(Block.Settings.create()
            .strength(2.0f)
            .requiresTool()));
    
    public static final Block ABILITY_CRAFTING_TABLE = registerBlock("ability_crafting_table",
        new AbilityCraftingTableBlock(Block.Settings.create()
            .strength(3.0f)
            .requiresTool()));
    
    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier("frogslimegamemode", name), block);
    }
    
    public static void register() {
        // Blocks are registered via static initialization above
    }
}
