package com.wayacreate.frogslimegamemode.item;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import com.wayacreate.frogslimegamemode.block.ModBlocks;
import com.wayacreate.frogslimegamemode.entity.ModEntities;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

public class ModItems {
    public static final Item FROG_HELPER_SPAWN_EGG =
        new SpawnEggItem(ModEntities.FROG_HELPER, 0x6B8E23, 0x9ACD32, new Item.Properties());
    public static final Item SLIME_HELPER_SPAWN_EGG =
        new SpawnEggItem(ModEntities.SLIME_HELPER, 0x51A03E, 0x7FFF00, new Item.Properties());
    public static final Item EVOLUTION_STONE = new EvolutionStoneItem(new Item.Properties().maxCount(16));
    public static final Item SLIME_FOOD = new SlimeFoodItem(new Item.Properties().maxCount(64).food(SlimeFoodItem.SLIME_FOOD_COMPONENT));
    public static final Item FROG_FOOD = new FrogFoodItem(new Item.Properties().maxCount(64).food(FrogFoodItem.FROG_FOOD_COMPONENT));
    public static final Item FINAL_EVOLUTION_CRYSTAL = new FinalEvolutionCrystal(new Item.Properties().maxCount(1));
    public static final Item TASK_BOOK = new TaskBookItem(new Item.Properties().maxCount(1));
    
    // YouTuber swords with funny twists
    public static final Item DREAM_SWORD = new YouTuberSwordItem("Dream", Tiers.NETHERITE, 3, -2.4f, new Item.Properties().maxCount(1));
    public static final Item TECHNOBLADE_SWORD = new YouTuberSwordItem("Technoblade", Tiers.NETHERITE, 5, -2.2f, new Item.Properties().maxCount(1));
    public static final Item GRIAN_SWORD = new YouTuberSwordItem("Grian", Tiers.DIAMOND, 3, -2.4f, new Item.Properties().maxCount(1));
    public static final Item MUMBO_JUMBO_SWORD = new YouTuberSwordItem("Mumbo Jumbo", Tiers.DIAMOND, 3, -2.4f, new Item.Properties().maxCount(1));
    
    // Funny armor pieces
    public static final Item MUSTARD_HELMET = new MustardHelmetItem(new Item.Properties().maxCount(1));
    public static final Item ORPHAN_SHIELD = new OrphanShieldItem(new Item.Properties().maxCount(1));
    public static final Item PRANKSTER_CHESTPLATE = new PranksterChestplateItem(new Item.Properties().maxCount(1));
    
    // Role items for helpers (using vanilla sticks with NBT)
    // These are created as pre-configured sticks
    public static final Item MINER_ROLE = new RoleItem(new Item.Properties().maxCount(1), "Miner");
    public static final Item LUMBERJACK_ROLE = new RoleItem(new Item.Properties().maxCount(1), "Lumberjack");
    public static final Item COMBAT_ROLE = new RoleItem(new Item.Properties().maxCount(1), "Combat Specialist");
    public static final Item BUILDER_ROLE = new RoleItem(new Item.Properties().maxCount(1), "Builder");
    public static final Item FARMER_ROLE = new RoleItem(new Item.Properties().maxCount(1), "Farmer");
    
    // Manhunt items
    public static final Item MANHUNT_COMPASS = new ManhuntCompassItem(new Item.Properties().maxCount(1));
    public static final Item HUNTER_TRACKER = new HunterTrackerItem(new Item.Properties().maxCount(1));
    public static final Item SPEEDRUNNER_BOOTS = new SpeedrunnerBootsItem(new Item.Properties().maxCount(1));
    public static final Item HUNTER_NET = new HunterNetItem(new Item.Properties().maxCount(16));
    public static final Item ESCAPE_PEARL = new EscapePearlItem(new Item.Properties().maxCount(16));
    
    // Ability drop item (placeholder for crafting)
    public static final Item ABILITY_DROP = new AbilityDropItem(new Item.Properties().maxCount(64).maxDamage(0));
    
    // Mob ability item (final crafted ability)
    public static final Item MOB_ABILITY = new MobAbilityItem(new Item.Properties().maxCount(64).maxDamage(0));
    
    // Ability stick - beginner-friendly crafting component
    public static final Item ABILITY_STICK = new Item(new Item.Properties().maxCount(64));
    
    // Block items
    public static final Item FROG_CRAFTING_TABLE_ITEM = new BlockItem(ModBlocks.FROG_CRAFTING_TABLE, new Item.Properties());
    public static final Item FROG_POTION_STAND_ITEM = new BlockItem(ModBlocks.FROG_POTION_STAND, new Item.Properties());
    public static final Item ABILITY_CRAFTING_TABLE_ITEM = new BlockItem(ModBlocks.ABILITY_CRAFTING_TABLE, new Item.Properties());

    public static void register(IEventBus modBus) {
        modBus.addListener(ModItems::onRegisterItems);
        modBus.addListener(ModItems::onBuildCreativeTabContents);
    }

    private static void onRegisterItems(RegisterEvent event) {
        FrogSlimeGamemode.LOGGER.info("Registering items for {}", FrogSlimeGamemode.MOD_ID);

        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "frog_helper_spawn_egg"), () -> FROG_HELPER_SPAWN_EGG);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "slime_helper_spawn_egg"), () -> SLIME_HELPER_SPAWN_EGG);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "evolution_stone"), () -> EVOLUTION_STONE);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "slime_food"), () -> SLIME_FOOD);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "frog_food"), () -> FROG_FOOD);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "final_evolution_crystal"), () -> FINAL_EVOLUTION_CRYSTAL);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "task_book"), () -> TASK_BOOK);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "dream_sword"), () -> DREAM_SWORD);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "technoblade_sword"), () -> TECHNOBLADE_SWORD);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "grian_sword"), () -> GRIAN_SWORD);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "mumbo_jumbo_sword"), () -> MUMBO_JUMBO_SWORD);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "mustard_helmet"), () -> MUSTARD_HELMET);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "orphan_shield"), () -> ORPHAN_SHIELD);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "prankster_chestplate"), () -> PRANKSTER_CHESTPLATE);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "miner_role"), () -> MINER_ROLE);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "lumberjack_role"), () -> LUMBERJACK_ROLE);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "combat_role"), () -> COMBAT_ROLE);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "builder_role"), () -> BUILDER_ROLE);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "farmer_role"), () -> FARMER_ROLE);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "manhunt_compass"), () -> MANHUNT_COMPASS);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "hunter_tracker"), () -> HUNTER_TRACKER);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "speedrunner_boots"), () -> SPEEDRUNNER_BOOTS);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "hunter_net"), () -> HUNTER_NET);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "escape_pearl"), () -> ESCAPE_PEARL);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "ability_drop"), () -> ABILITY_DROP);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "mob_ability"), () -> MOB_ABILITY);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "ability_stick"), () -> ABILITY_STICK);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "frog_crafting_table"), () -> FROG_CRAFTING_TABLE_ITEM);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "frog_potion_stand"), () -> FROG_POTION_STAND_ITEM);
        event.register(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "ability_crafting_table"), () -> ABILITY_CRAFTING_TABLE_ITEM);
    }

    private static void onBuildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() != CreativeModeTabs.INGREDIENTS) {
            return;
        }

        event.accept(FROG_HELPER_SPAWN_EGG);
        event.accept(SLIME_HELPER_SPAWN_EGG);
        event.accept(EVOLUTION_STONE);
        event.accept(SLIME_FOOD);
        event.accept(FROG_FOOD);
        event.accept(FINAL_EVOLUTION_CRYSTAL);
        event.accept(TASK_BOOK);
        event.accept(DREAM_SWORD);
        event.accept(TECHNOBLADE_SWORD);
        event.accept(GRIAN_SWORD);
        event.accept(MUMBO_JUMBO_SWORD);
        event.accept(MUSTARD_HELMET);
        event.accept(ORPHAN_SHIELD);
        event.accept(PRANKSTER_CHESTPLATE);
        event.accept(MINER_ROLE);
        event.accept(LUMBERJACK_ROLE);
        event.accept(COMBAT_ROLE);
        event.accept(BUILDER_ROLE);
        event.accept(FARMER_ROLE);
        event.accept(MANHUNT_COMPASS);
        event.accept(HUNTER_TRACKER);
        event.accept(SPEEDRUNNER_BOOTS);
        event.accept(HUNTER_NET);
        event.accept(ESCAPE_PEARL);
        event.accept(ABILITY_DROP);
        event.accept(MOB_ABILITY);
        event.accept(ABILITY_STICK);
        event.accept(FROG_CRAFTING_TABLE_ITEM);
        event.accept(FROG_POTION_STAND_ITEM);
        event.accept(ABILITY_CRAFTING_TABLE_ITEM);
    }
}
