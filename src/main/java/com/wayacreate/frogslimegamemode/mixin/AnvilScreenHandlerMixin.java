package com.wayacreate.frogslimegamemode.mixin;

import com.wayacreate.frogslimegamemode.item.AbilityDropItem;
import com.wayacreate.frogslimegamemode.item.MobAbilityItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {
    @Inject(method = "onTakeOutput", at = @At("HEAD"), cancellable = true)
    private void onTakeOutput(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        AnvilScreenHandler handler = (AnvilScreenHandler) (Object) this;
        ItemStack leftSlot = handler.getSlot(0).getStack();
        ItemStack rightSlot = handler.getSlot(1).getStack();
        
        // Check if combining ability drop with matching mob item to create MobAbilityItem
        if (AbilityDropItem.isAbilityDrop(leftSlot) && !rightSlot.isEmpty()) {
            String abilityId = AbilityDropItem.getAbilityId(leftSlot);
            String mobItemId = net.minecraft.registry.Registries.ITEM.getId(rightSlot.getItem()).getPath();
            
            // Map mob drops to abilities
            String targetAbility = getAbilityFromMobDrop(mobItemId);
            if (targetAbility != null && targetAbility.equals(abilityId)) {
                // Create the final MobAbilityItem
                ItemStack mobAbility = MobAbilityItem.createMobAbility(abilityId);
                if (!mobAbility.isEmpty()) {
                    // Set output
                    handler.getSlot(2).setStack(mobAbility);
                    
                    // Consume items
                    leftSlot.decrement(1);
                    rightSlot.decrement(1);
                    
                    ci.cancel();
                }
            }
        }
    }
    
    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    private void updateResult(CallbackInfo ci) {
        AnvilScreenHandler handler = (AnvilScreenHandler) (Object) this;
        ItemStack leftSlot = handler.getSlot(0).getStack();
        ItemStack rightSlot = handler.getSlot(1).getStack();
        
        // Check if combining ability drop with matching mob item
        if (AbilityDropItem.isAbilityDrop(leftSlot) && !rightSlot.isEmpty()) {
            String abilityId = AbilityDropItem.getAbilityId(leftSlot);
            String mobItemId = net.minecraft.registry.Registries.ITEM.getId(rightSlot.getItem()).getPath();
            
            // Map mob drops to abilities
            String targetAbility = getAbilityFromMobDrop(mobItemId);
            if (targetAbility != null && targetAbility.equals(abilityId)) {
                // Show the MobAbilityItem as preview output
                ItemStack mobAbility = MobAbilityItem.createMobAbility(abilityId);
                if (!mobAbility.isEmpty()) {
                    handler.getSlot(2).setStack(mobAbility);
                    ci.cancel();
                }
            }
        }
    }
    
    private String getAbilityFromMobDrop(String itemId) {
        return switch (itemId) {
            case "rotten_flesh" -> "zombie";
            case "bone" -> "skeleton";
            case "spider_eye" -> "spider";
            case "gunpowder" -> "creeper";
            case "ender_pearl" -> "enderman";
            case "potion" -> "witch";
            case "blaze_rod" -> "blaze";
            case "slime_ball" -> "slime";
            case "leather" -> "cow";
            case "porkchop" -> "pig";
            case "white_wool" -> "sheep";
            case "feather" -> "chicken";
            case "rabbit_foot" -> "rabbit";
            case "prismarine_crystals" -> "guardian";
            case "ghast_tear" -> "ghast";
            case "phantom_membrane" -> "phantom";
            case "string" -> "cave_spider";
            case "shulker_shell" -> "shulker";
            case "dragon_egg" -> "ender_dragon";
            case "turtle_helmet" -> "turtle";
            case "trident" -> "drowned";
            case "nautilus_shell" -> "elder_guardian";
            case "honeycomb" -> "bee";
            case "axolotl_bucket" -> "axolotl";
            case "amethyst_shard" -> "allay";
            case "emerald" -> "villager";
            case "iron_ingot" -> "iron_golem";
            case "snowball" -> "snow_golem";
            case "golden_apple" -> "technoblade_pig";
            case "redstone" -> "mumbo_jumbo";
            case "dirt" -> "derpy";
            case "basalt" -> "basalt_giant";
            case "gold_ingot" -> "piglin_beast";
            case "sculk" -> "sculk_crawler";
            case "prismarine_shard" -> "ancient_guardian";
            case "nether_star" -> "nether_star_born";
            case "wither_rose" -> "wither_king";
            case "packed_ice" -> "ice_walker";
            case "vine" -> "jungle_trap";
            case "sand" -> "desert_pharaoh";
            case "heart_of_the_sea" -> "ocean_kraken";
            case "elytra" -> "sky_leviathan";
            case "red_mushroom" -> "mushroom_giant";
            case "glowstone" -> "pokemon_master";
            case "clock" -> "overwatch_tracer";
            case "lightning_rod" -> "mcu_thor";
            case "blaze_powder" -> "harry_potter";
            case "green_dye" -> "matrix_ninja";
            case "iron_block" -> "terminator";
            case "spyglass" -> "predator";
            case "fermented_spider_eye" -> "alien_queen";
            case "tnt" -> "godzilla";
            case "dragon_head" -> "dovahkiin";
            case "netherite_sword" -> "witcher";
            case "diamond_sword" -> "geralt";
            case "netherite_axe" -> "kratos";
            case "shield" -> "master_chief";
            case "crossbow" -> "doom_slayer";
            case "firework_rocket" -> "samus";
            case "diamond_pickaxe" -> "minecraft_steve";
            case "apple" -> "notch";
            case "coal" -> "herobrine";
            case "wither_skeleton_skull" -> "wither_boss";
            case "golden_carrot" -> "king_kong";
            case "cod" -> "dolphin";
            case "ink_sac" -> "squid";
            default -> null;
        };
    }
}
