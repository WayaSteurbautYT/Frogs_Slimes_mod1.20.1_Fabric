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
    public static Item FROG_HELPER_SPAWN_EGG;
    public static Item SLIME_HELPER_SPAWN_EGG;
    public static Item EVOLUTION_STONE;
    public static Item SLIME_FOOD;
    public static Item FROG_FOOD;
    public static Item FINAL_EVOLUTION_CRYSTAL;
    public static Item TASK_BOOK;
    
    // YouTuber swords with funny twists
    public static Item DREAM_SWORD;
    public static Item TECHNOBLADE_SWORD;
    public static Item GRIAN_SWORD;
    public static Item MUMBO_JUMBO_SWORD;
    
    // Funny armor pieces
    public static Item MUSTARD_HELMET;
    public static Item ORPHAN_SHIELD;
    public static Item PRANKSTER_CHESTPLATE;
    
    // Role items for helpers (using vanilla sticks with NBT)
    // These are created as pre-configured sticks
    public static Item MINER_ROLE;
    public static Item LUMBERJACK_ROLE;
    public static Item COMBAT_ROLE;
    public static Item BUILDER_ROLE;
    public static Item FARMER_ROLE;
    
    // Manhunt items
    public static Item MANHUNT_COMPASS;
    public static Item HUNTER_TRACKER;
    public static Item SPEEDRUNNER_BOOTS;
    public static Item HUNTER_NET;
    public static Item ESCAPE_PEARL;
    
    // Ability drop item
    public static Item ABILITY_DROP;
    
    public static final RegistryKey<ItemGroup> FROG_SLIME_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP,
        new Identifier(FrogSlimeGamemode.MOD_ID, "frog_slime_group"));
    
    public static ItemGroup FROG_SLIME_ITEM_GROUP;
    
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(FrogSlimeGamemode.MOD_ID, name), item);
    }
    
    public static void register() {
        FrogSlimeGamemode.LOGGER.info("Registering items for " + FrogSlimeGamemode.MOD_ID);
        
        // Register spawn eggs after entities are registered
        FROG_HELPER_SPAWN_EGG = registerItem("frog_helper_spawn_egg",
            new SpawnEggItem(ModEntities.FROG_HELPER, 0x6B8E23, 0x9ACD32, new FabricItemSettings()));
        
        SLIME_HELPER_SPAWN_EGG = registerItem("slime_helper_spawn_egg",
            new SpawnEggItem(ModEntities.SLIME_HELPER, 0x51A03E, 0x7FFF00, new FabricItemSettings()));
        
        EVOLUTION_STONE = registerItem("evolution_stone",
            new EvolutionStoneItem(new FabricItemSettings().maxCount(16)));
        
        SLIME_FOOD = registerItem("slime_food",
            new SlimeFoodItem(new FabricItemSettings().maxCount(64).food(SlimeFoodItem.SLIME_FOOD_COMPONENT)));
        
        FROG_FOOD = registerItem("frog_food",
            new FrogFoodItem(new FabricItemSettings().maxCount(64).food(FrogFoodItem.FROG_FOOD_COMPONENT)));
        
        FINAL_EVOLUTION_CRYSTAL = registerItem("final_evolution_crystal",
            new FinalEvolutionCrystal(new FabricItemSettings().maxCount(1)));
        
        TASK_BOOK = registerItem("task_book",
            new TaskBookItem(new FabricItemSettings().maxCount(1)));
        
        // YouTuber swords with funny twists
        DREAM_SWORD = registerItem("dream_sword",
            new YouTuberSwordItem("Dream", ToolMaterials.NETHERITE, 3, -2.4f, new FabricItemSettings().maxCount(1)));
        
        TECHNOBLADE_SWORD = registerItem("technoblade_sword",
            new YouTuberSwordItem("Technoblade", ToolMaterials.NETHERITE, 5, -2.2f, new FabricItemSettings().maxCount(1)));
        
        GRIAN_SWORD = registerItem("grian_sword",
            new YouTuberSwordItem("Grian", ToolMaterials.DIAMOND, 3, -2.4f, new FabricItemSettings().maxCount(1)));
        
        MUMBO_JUMBO_SWORD = registerItem("mumbo_jumbo_sword",
            new YouTuberSwordItem("Mumbo Jumbo", ToolMaterials.DIAMOND, 3, -2.4f, new FabricItemSettings().maxCount(1)));
        
        // Funny armor pieces
        MUSTARD_HELMET = registerItem("mustard_helmet",
            new MustardHelmetItem(new FabricItemSettings().maxCount(1)));
        
        // Orphan Shield - custom item with custom texture
        ORPHAN_SHIELD = registerItem("orphan_shield",
            new OrphanShieldItem(new FabricItemSettings().maxCount(1)));
        
        PRANKSTER_CHESTPLATE = registerItem("prankster_chestplate",
            new PranksterChestplateItem(new FabricItemSettings().maxCount(1)));
        
        // Role items for helpers
        MINER_ROLE = registerItem("miner_role",
            new RoleItem(new FabricItemSettings().maxCount(1), "Miner"));
        
        LUMBERJACK_ROLE = registerItem("lumberjack_role",
            new RoleItem(new FabricItemSettings().maxCount(1), "Lumberjack"));
        
        COMBAT_ROLE = registerItem("combat_role",
            new RoleItem(new FabricItemSettings().maxCount(1), "Combat Specialist"));
        
        BUILDER_ROLE = registerItem("builder_role",
            new RoleItem(new FabricItemSettings().maxCount(1), "Builder"));
        
        FARMER_ROLE = registerItem("farmer_role",
            new RoleItem(new FabricItemSettings().maxCount(1), "Farmer"));
        
        // Manhunt items
        MANHUNT_COMPASS = registerItem("manhunt_compass",
            new ManhuntCompassItem(new FabricItemSettings().maxCount(1)));
        
        HUNTER_TRACKER = registerItem("hunter_tracker",
            new HunterTrackerItem(new FabricItemSettings().maxCount(1)));
        
        SPEEDRUNNER_BOOTS = registerItem("speedrunner_boots",
            new SpeedrunnerBootsItem(new FabricItemSettings().maxCount(1)));
        
        HUNTER_NET = registerItem("hunter_net",
            new HunterNetItem(new FabricItemSettings().maxCount(16)));
        
        ESCAPE_PEARL = registerItem("escape_pearl",
            new EscapePearlItem(new FabricItemSettings().maxCount(16)));
        
        // Ability drop item
        ABILITY_DROP = registerItem("ability_drop",
            new AbilityDropItem(new FabricItemSettings().maxCount(64).maxDamage(0)));
        
        // Register item group
        FROG_SLIME_ITEM_GROUP = Registry.register(Registries.ITEM_GROUP,
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
                    entries.add(BUILDER_ROLE);
                    entries.add(FARMER_ROLE);
                    // Manhunt
                    entries.add(MANHUNT_COMPASS);
                    entries.add(HUNTER_TRACKER);
                    entries.add(SPEEDRUNNER_BOOTS);
                    entries.add(HUNTER_NET);
                    entries.add(ESCAPE_PEARL);
                    // Ability drops
                    entries.add(ABILITY_DROP);
                })
                .build()
        );
    }
}