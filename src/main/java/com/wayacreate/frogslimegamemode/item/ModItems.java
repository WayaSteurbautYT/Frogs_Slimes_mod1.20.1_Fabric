package com.wayacreate.frogslimegamemode.item;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import com.wayacreate.frogslimegamemode.entity.ModEntities;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item FROG_HELPER_SPAWN_EGG = registerItem("frog_helper_spawn_egg",
        new SpawnEggItem(ModEntities.FROG_HELPER, 0x6B8E23, 0x9ACD32, new FabricItemSettings()));
    
    public static final Item SLIME_HELPER_SPAWN_EGG = registerItem("slime_helper_spawn_egg",
        new SpawnEggItem(ModEntities.SLIME_HELPER, 0x51A03E, 0x7FFF00, new FabricItemSettings()));
    
    public static final Item EVOLUTION_STONE = registerItem("evolution_stone",
        new EvolutionStoneItem(new FabricItemSettings().maxCount(16)));
    
    public static final Item SLIME_FOOD = registerItem("slime_food",
        new SlimeFoodItem(new FabricItemSettings().maxCount(64).food(SlimeFoodItem.SLIME_FOOD_COMPONENT)));
    
    public static final Item FROG_FOOD = registerItem("frog_food",
        new FrogFoodItem(new FabricItemSettings().maxCount(64).food(FrogFoodItem.FROG_FOOD_COMPONENT)));
    
    public static final Item FINAL_EVOLUTION_CRYSTAL = registerItem("final_evolution_crystal",
        new FinalEvolutionCrystal(new FabricItemSettings().maxCount(1)));
    
    public static final Item TASK_BOOK = registerItem("task_book",
        new TaskBookItem(new FabricItemSettings().maxCount(1)));
    
    // YouTuber swords with funny twists
    public static final Item DREAM_SWORD = registerItem("dream_sword",
        new YouTuberSwordItem("Dream", ToolMaterials.NETHERITE, 3, -2.4f, new FabricItemSettings().maxCount(1)));
    
    public static final Item TECHNOBLADE_SWORD = registerItem("technoblade_sword",
        new YouTuberSwordItem("Technoblade", ToolMaterials.NETHERITE, 5, -2.2f, new FabricItemSettings().maxCount(1)));
    
    public static final Item GRIAN_SWORD = registerItem("grian_sword",
        new YouTuberSwordItem("Grian", ToolMaterials.DIAMOND, 3, -2.4f, new FabricItemSettings().maxCount(1)));
    
    public static final Item MUMBO_JUMBO_SWORD = registerItem("mumbo_jumbo_sword",
        new YouTuberSwordItem("Mumbo Jumbo", ToolMaterials.DIAMOND, 3, -2.4f, new FabricItemSettings().maxCount(1)));
    
    // Funny armor pieces
    public static final Item MUSTARD_HELMET = registerItem("mustard_helmet",
        new Item(new FabricItemSettings().maxCount(1)));
    
    public static final Item ORPHAN_SHIELD = registerItem("orphan_shield",
        new Item(new FabricItemSettings().maxCount(1)));
    
    public static final Item PRANKSTER_CHESTPLATE = registerItem("prankster_chestplate",
        new Item(new FabricItemSettings().maxCount(1)));
    
    // Role items for helpers
    public static final Item MINER_ROLE = registerItem("miner_role",
        new RoleItem("Miner", new FabricItemSettings().maxCount(1)));
    
    public static final Item LUMBERJACK_ROLE = registerItem("lumberjack_role",
        new RoleItem("Lumberjack", new FabricItemSettings().maxCount(1)));
    
    public static final Item COMBAT_ROLE = registerItem("combat_role",
        new RoleItem("Combat Specialist", new FabricItemSettings().maxCount(1)));
    
    // Manhunt compass
    public static final Item MANHUNT_COMPASS = registerItem("manhunt_compass",
        new ManhuntCompassItem(new FabricItemSettings().maxCount(1)));
    
    public static final RegistryKey<ItemGroup> FROG_SLIME_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP,
        new Identifier(FrogSlimeGamemode.MOD_ID, "frog_slime_group"));
    
    public static final ItemGroup FROG_SLIME_ITEM_GROUP = Registry.register(Registries.ITEM_GROUP,
        FROG_SLIME_GROUP,
        FabricItemGroup.builder()
            .icon(() -> new ItemStack(SLIME_HELPER_SPAWN_EGG))
            .displayName(Text.literal("Frog & Slime Gamemode"))
            .entries((context, entries) -> {
                entries.add(FROG_HELPER_SPAWN_EGG);
                entries.add(SLIME_HELPER_SPAWN_EGG);
                entries.add(EVOLUTION_STONE);
                entries.add(SLIME_FOOD);
                entries.add(FROG_FOOD);
                entries.add(FINAL_EVOLUTION_CRYSTAL);
                entries.add(TASK_BOOK);
                // YouTuber swords
                entries.add(DREAM_SWORD);
                entries.add(TECHNOBLADE_SWORD);
                entries.add(GRIAN_SWORD);
                entries.add(MUMBO_JUMBO_SWORD);
                // Funny armor
                entries.add(MUSTARD_HELMET);
                entries.add(ORPHAN_SHIELD);
                entries.add(PRANKSTER_CHESTPLATE);
                // Role items
                entries.add(MINER_ROLE);
                entries.add(LUMBERJACK_ROLE);
                entries.add(COMBAT_ROLE);
                // Manhunt
                entries.add(MANHUNT_COMPASS);
            })
            .build()
    );
    
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(FrogSlimeGamemode.MOD_ID, name), item);
    }
    
    public static void register() {
        FrogSlimeGamemode.LOGGER.info("Registering items for " + FrogSlimeGamemode.MOD_ID);
    }
}