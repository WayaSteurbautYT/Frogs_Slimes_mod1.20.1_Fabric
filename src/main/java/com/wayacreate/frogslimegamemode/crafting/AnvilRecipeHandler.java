package com.wayacreate.frogslimegamemode.crafting;

import com.wayacreate.frogslimegamemode.item.ModItems;
import com.wayacreate.frogslimegamemode.item.MobAbilityItem;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;

public class AnvilRecipeHandler {
    
    public static void register() {
        // Register anvil right-click event
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient) return ActionResult.PASS;
            
            // Check if clicking on an anvil
            if (world.getBlockState(hitResult.getBlockPos()).getBlock() != Blocks.ANVIL) {
                return ActionResult.PASS;
            }
            
            // Check if player is holding ability_stick in main hand
            ItemStack mainHand = player.getMainHandStack();
            if (!mainHand.isOf(ModItems.ABILITY_STICK)) {
                return ActionResult.PASS;
            }
            
            // Check if player has mob drop in offhand
            ItemStack offHand = player.getOffHandStack();
            if (offHand.isEmpty()) {
                player.sendMessage(Text.literal("Hold the mob drop in your offhand (e.g., rotten flesh, bone, spider eye)!")
                    .formatted(Formatting.YELLOW), false);
                return ActionResult.PASS;
            }
            
            // Get ability ID from the mob drop
            String abilityId = getAbilityIdFromItem(offHand);
            if (abilityId == null) {
                player.sendMessage(Text.literal("Unknown mob drop! Use vanilla mob drops like rotten flesh, bones, spider eyes, etc.")
                    .formatted(Formatting.RED), false);
                return ActionResult.PASS;
            }
            
            // Validate that the mob item matches the ability
            if (!isMatchingMobItem(offHand, abilityId)) {
                player.sendMessage(Text.literal("Invalid mob drop combination!")
                    .formatted(Formatting.RED), false);
                return ActionResult.PASS;
            }
            
            // Create mob ability item
            ItemStack mobAbility = MobAbilityItem.createMobAbility(abilityId);
            if (mobAbility.isEmpty()) return ActionResult.PASS;
            
            // Consume items (ability stick + mob drop)
            mainHand.decrement(1);
            offHand.decrement(1);
            
            // Give player the mob ability
            if (!player.getInventory().insertStack(mobAbility)) {
                player.dropItem(mobAbility, false);
            }
            
            // Play sound
            player.playSound(SoundEvents.BLOCK_ANVIL_USE, 1.0f, 1.0f);
            
            // Send message
            player.sendMessage(Text.literal("Created " + mobAbility.getName().getString() + "!")
                .formatted(Formatting.GREEN), false);
            
            // Also works with left click - but right click is more convenient
            return ActionResult.SUCCESS;
        });
    }
    
    private static String getAbilityIdFromItem(ItemStack stack) {
        if (stack.isEmpty()) return null;
        
        net.minecraft.util.Identifier itemId = net.minecraft.registry.Registries.ITEM.getId(stack.getItem());
        String itemIdStr = itemId.toString();
        
        if (itemIdStr.equals("minecraft:rotten_flesh")) return "zombie";
        if (itemIdStr.equals("minecraft:bone")) return "skeleton";
        if (itemIdStr.equals("minecraft:spider_eye")) return "spider";
        if (itemIdStr.equals("minecraft:gunpowder")) return "creeper";
        if (itemIdStr.equals("minecraft:ender_pearl")) return "enderman";
        if (itemIdStr.equals("minecraft:potion")) return "witch";
        if (itemIdStr.equals("minecraft:blaze_rod")) return "blaze";
        if (itemIdStr.equals("minecraft:slime_ball")) return "slime";
        if (itemIdStr.equals("minecraft:leather")) return "cow";
        if (itemIdStr.equals("minecraft:porkchop")) return "pig";
        if (itemIdStr.equals("minecraft:white_wool")) return "sheep";
        if (itemIdStr.equals("minecraft:feather")) return "chicken";
        if (itemIdStr.equals("minecraft:rabbit_foot")) return "rabbit";
        if (itemIdStr.equals("minecraft:prismarine_crystals")) return "guardian";
        if (itemIdStr.equals("minecraft:ghast_tear")) return "ghast";
        if (itemIdStr.equals("minecraft:phantom_membrane")) return "phantom";
        if (itemIdStr.equals("minecraft:string")) return "cave_spider";
        if (itemIdStr.equals("minecraft:shulker_shell")) return "shulker";
        if (itemIdStr.equals("minecraft:dragon_egg")) return "ender_dragon";
        if (itemIdStr.equals("minecraft:turtle_helmet")) return "turtle";
        if (itemIdStr.equals("minecraft:trident")) return "drowned";
        if (itemIdStr.equals("minecraft:nautilus_shell")) return "elder_guardian";
        if (itemIdStr.equals("minecraft:honeycomb")) return "bee";
        if (itemIdStr.equals("minecraft:axolotl_bucket")) return "axolotl";
        if (itemIdStr.equals("minecraft:amethyst_shard")) return "allay";
        if (itemIdStr.equals("minecraft:emerald")) return "villager";
        if (itemIdStr.equals("minecraft:iron_ingot")) return "iron_golem";
        if (itemIdStr.equals("minecraft:snowball")) return "snow_golem";
        if (itemIdStr.equals("minecraft:golden_apple")) return "technoblade_pig";
        if (itemIdStr.equals("minecraft:redstone")) return "mumbo_jumbo";
        if (itemIdStr.equals("minecraft:dirt")) return "derpy";
        if (itemIdStr.equals("minecraft:basalt")) return "basalt_giant";
        if (itemIdStr.equals("minecraft:gold_ingot")) return "piglin_beast";
        if (itemIdStr.equals("minecraft:sculk")) return "sculk_crawler";
        if (itemIdStr.equals("minecraft:prismarine_shard")) return "ancient_guardian";
        if (itemIdStr.equals("minecraft:nether_star")) return "nether_star_born";
        if (itemIdStr.equals("minecraft:wither_rose")) return "wither_king";
        if (itemIdStr.equals("minecraft:packed_ice")) return "ice_walker";
        if (itemIdStr.equals("minecraft:vine")) return "jungle_trap";
        if (itemIdStr.equals("minecraft:sand")) return "desert_pharaoh";
        if (itemIdStr.equals("minecraft:heart_of_the_sea")) return "ocean_kraken";
        if (itemIdStr.equals("minecraft:elytra")) return "sky_leviathan";
        if (itemIdStr.equals("minecraft:red_mushroom")) return "mushroom_giant";
        if (itemIdStr.equals("minecraft:glowstone")) return "pokemon_master";
        if (itemIdStr.equals("minecraft:clock")) return "overwatch_tracer";
        if (itemIdStr.equals("minecraft:lightning_rod")) return "mcu_thor";
        if (itemIdStr.equals("minecraft:blaze_powder")) return "harry_potter";
        if (itemIdStr.equals("minecraft:green_dye")) return "matrix_ninja";
        if (itemIdStr.equals("minecraft:iron_block")) return "terminator";
        if (itemIdStr.equals("minecraft:spyglass")) return "predator";
        if (itemIdStr.equals("minecraft:fermented_spider_eye")) return "alien_queen";
        if (itemIdStr.equals("minecraft:tnt")) return "godzilla";
        if (itemIdStr.equals("minecraft:dragon_head")) return "dovahkiin";
        if (itemIdStr.equals("minecraft:netherite_sword")) return "witcher";
        if (itemIdStr.equals("minecraft:diamond_sword")) return "geralt";
        if (itemIdStr.equals("minecraft:netherite_axe")) return "kratos";
        if (itemIdStr.equals("minecraft:shield")) return "master_chief";
        if (itemIdStr.equals("minecraft:crossbow")) return "doom_slayer";
        if (itemIdStr.equals("minecraft:trident")) return "link";
        if (itemIdStr.equals("minecraft:firework_rocket")) return "samus";
        if (itemIdStr.equals("minecraft:diamond_pickaxe")) return "minecraft_steve";
        if (itemIdStr.equals("minecraft:apple")) return "notch";
        if (itemIdStr.equals("minecraft:coal")) return "herobrine";
        if (itemIdStr.equals("minecraft:wither_skeleton_skull")) return "wither_boss";
        if (itemIdStr.equals("minecraft:golden_carrot")) return "king_kong";
        if (itemIdStr.equals("minecraft:cod")) return "dolphin";
        if (itemIdStr.equals("minecraft:ink_sac")) return "squid";
        
        return null;
    }
    
    private static boolean isMatchingMobItem(ItemStack stack, String abilityId) {
        if (stack.isEmpty()) return false;
        
        net.minecraft.util.Identifier itemId = net.minecraft.registry.Registries.ITEM.getId(stack.getItem());
        String itemIdStr = itemId.toString();
        
        return switch (abilityId) {
            case "zombie" -> itemIdStr.equals("minecraft:rotten_flesh");
            case "skeleton" -> itemIdStr.equals("minecraft:bone");
            case "spider" -> itemIdStr.equals("minecraft:spider_eye");
            case "creeper" -> itemIdStr.equals("minecraft:gunpowder");
            case "enderman" -> itemIdStr.equals("minecraft:ender_pearl");
            case "witch" -> itemIdStr.equals("minecraft:potion");
            case "blaze" -> itemIdStr.equals("minecraft:blaze_rod");
            case "slime" -> itemIdStr.equals("minecraft:slime_ball");
            case "cow", "mooshroom" -> itemIdStr.equals("minecraft:leather");
            case "pig" -> itemIdStr.equals("minecraft:porkchop");
            case "sheep" -> itemIdStr.equals("minecraft:white_wool");
            case "chicken" -> itemIdStr.equals("minecraft:feather");
            case "rabbit" -> itemIdStr.equals("minecraft:rabbit_foot");
            case "guardian" -> itemIdStr.equals("minecraft:prismarine_crystals");
            case "ghast" -> itemIdStr.equals("minecraft:ghast_tear");
            case "phantom" -> itemIdStr.equals("minecraft:phantom_membrane");
            case "cave_spider" -> itemIdStr.equals("minecraft:string");
            case "shulker" -> itemIdStr.equals("minecraft:shulker_shell");
            case "ender_dragon" -> itemIdStr.equals("minecraft:dragon_egg");
            case "turtle" -> itemIdStr.equals("minecraft:turtle_helmet");
            case "drowned" -> itemIdStr.equals("minecraft:trident");
            case "elder_guardian" -> itemIdStr.equals("minecraft:nautilus_shell");
            case "bee" -> itemIdStr.equals("minecraft:honeycomb");
            case "axolotl" -> itemIdStr.equals("minecraft:axolotl_bucket");
            case "allay" -> itemIdStr.equals("minecraft:amethyst_shard");
            case "villager", "wandering_trader" -> itemIdStr.equals("minecraft:emerald");
            case "iron_golem" -> itemIdStr.equals("minecraft:iron_ingot");
            case "snow_golem" -> itemIdStr.equals("minecraft:snowball");
            case "technoblade_pig" -> itemIdStr.equals("minecraft:golden_apple");
            case "mumbo_jumbo" -> itemIdStr.equals("minecraft:redstone");
            case "derpy" -> itemIdStr.equals("minecraft:dirt");
            case "basalt_giant" -> itemIdStr.equals("minecraft:basalt");
            case "piglin_beast" -> itemIdStr.equals("minecraft:gold_ingot");
            case "sculk_crawler" -> itemIdStr.equals("minecraft:sculk");
            case "ancient_guardian" -> itemIdStr.equals("minecraft:prismarine_shard");
            case "nether_star_born" -> itemIdStr.equals("minecraft:nether_star");
            case "dragon_soul" -> itemIdStr.equals("minecraft:dragon_egg");
            case "wither_king" -> itemIdStr.equals("minecraft:wither_rose");
            case "ice_walker" -> itemIdStr.equals("minecraft:packed_ice");
            case "jungle_trap" -> itemIdStr.equals("minecraft:vine");
            case "desert_pharaoh" -> itemIdStr.equals("minecraft:sand");
            case "ocean_kraken" -> itemIdStr.equals("minecraft:heart_of_the_sea");
            case "sky_leviathan" -> itemIdStr.equals("minecraft:elytra");
            case "mushroom_giant" -> itemIdStr.equals("minecraft:red_mushroom");
            case "hyrule_guardian" -> itemIdStr.equals("minecraft:emerald");
            case "pokemon_master" -> itemIdStr.equals("minecraft:glowstone");
            case "overwatch_tracer" -> itemIdStr.equals("minecraft:clock");
            case "mcu_thor" -> itemIdStr.equals("minecraft:lightning_rod");
            case "harry_potter" -> itemIdStr.equals("minecraft:blaze_powder");
            case "matrix_ninja" -> itemIdStr.equals("minecraft:green_dye");
            case "terminator" -> itemIdStr.equals("minecraft:iron_block");
            case "predator" -> itemIdStr.equals("minecraft:spyglass");
            case "alien_queen" -> itemIdStr.equals("minecraft:fermented_spider_eye");
            case "godzilla" -> itemIdStr.equals("minecraft:tnt");
            case "dovahkiin" -> itemIdStr.equals("minecraft:dragon_head");
            case "witcher" -> itemIdStr.equals("minecraft:netherite_sword");
            case "geralt" -> itemIdStr.equals("minecraft:diamond_sword");
            case "kratos" -> itemIdStr.equals("minecraft:netherite_axe");
            case "master_chief" -> itemIdStr.equals("minecraft:shield");
            case "doom_slayer" -> itemIdStr.equals("minecraft:crossbow");
            case "link" -> itemIdStr.equals("minecraft:trident");
            case "samus" -> itemIdStr.equals("minecraft:firework_rocket");
            case "minecraft_steve" -> itemIdStr.equals("minecraft:diamond_pickaxe");
            case "notch" -> itemIdStr.equals("minecraft:apple");
            case "herobrine" -> itemIdStr.equals("minecraft:coal");
            case "wither_boss" -> itemIdStr.equals("minecraft:wither_skeleton_skull");
            case "king_kong" -> itemIdStr.equals("minecraft:golden_carrot");
            case "dolphin" -> itemIdStr.equals("minecraft:cod");
            case "squid", "glow_squid" -> itemIdStr.equals("minecraft:ink_sac");
            case "pufferfish", "tropical_fish", "salmon", "cod" -> itemIdStr.equals("minecraft:cod");
            case "goat", "panda", "polar_bear" -> itemIdStr.equals("minecraft:leather");
            case "frog", "tadpole" -> itemIdStr.equals("minecraft:slime_ball");
            case "fox", "wolf", "cat", "ocelot" -> itemIdStr.equals("minecraft:bone");
            case "parrot" -> itemIdStr.equals("minecraft:feather");
            default -> false;
        };
    }
}
