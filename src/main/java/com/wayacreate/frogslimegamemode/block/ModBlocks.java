package com.wayacreate.frogslimegamemode.block;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;

public class ModBlocks {
    public static final Block FROG_CRAFTING_TABLE =
        new FrogCraftingTableBlock(Block.Properties.create()
            .strength(2.5f));

    public static final Block FROG_POTION_STAND =
        new FrogPotionStandBlock(Block.Properties.create()
            .strength(2.0f));

    public static final Block ABILITY_CRAFTING_TABLE =
        new AbilityCraftingTableBlock(Block.Properties.create()
            .strength(3.0f));

    public static void register(IEventBus modBus) {
        modBus.addListener(ModBlocks::onRegister);
    }

    private static void onRegister(RegisterEvent event) {
        event.register(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "frog_crafting_table"), () -> FROG_CRAFTING_TABLE);
        event.register(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "frog_potion_stand"), () -> FROG_POTION_STAND);
        event.register(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "ability_crafting_table"), () -> ABILITY_CRAFTING_TABLE);
    }
}
