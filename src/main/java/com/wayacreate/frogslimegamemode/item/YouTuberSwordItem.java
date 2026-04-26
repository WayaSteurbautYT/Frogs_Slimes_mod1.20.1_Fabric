package com.wayacreate.frogslimegamemode.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class YouTuberSwordItem extends SwordItem {
    private final String youtuberName;
    
    public YouTuberSwordItem(String youtuberName, ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
        this.youtuberName = youtuberName;
    }
    
    @Override
    public Text getName(ItemStack stack) {
        return Text.literal(youtuberName + "'s Blade")
            .formatted(Formatting.GOLD, Formatting.BOLD);
    }
    
    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
    
    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!world.isClient && user instanceof PlayerEntity player) {
            applySwordAbility(player);
        }
        super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }
    
    private void applySwordAbility(PlayerEntity player) {
        switch (youtuberName.toLowerCase()) {
            case "dream" -> {
                // Speed boost for "speedrunning"
                player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.SPEED, 200, 2));
                player.sendMessage(Text.literal("Speedrun activated!")
                    .formatted(Formatting.GREEN), true);
            }
            case "technoblade" -> {
                // Strength and resistance
                player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.STRENGTH, 200, 1));
                player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.RESISTANCE, 200, 1));
                player.sendMessage(Text.literal("Blood for the Blood God!")
                    .formatted(Formatting.DARK_RED), true);
            }
            case "grian" -> {
                // Invisibility for "pranking"
                player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.INVISIBILITY, 100, 0));
                player.sendMessage(Text.literal("Prank time!")
                    .formatted(Formatting.LIGHT_PURPLE), true);
            }
            case "mumbo jumbo" -> {
                // Haste for "engineering"
                player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.HASTE, 200, 2));
                player.sendMessage(Text.literal("Engineering mode activated!")
                    .formatted(Formatting.YELLOW), true);
            }
        }
    }
    
    public String getYoutuberName() {
        return youtuberName;
    }
    
    public String getRandomQuote() {
        List<String> quotes = switch (youtuberName.toLowerCase()) {
            case "dream" -> List.of(
                "Hehe boi!",
                "Impossible luck!",
                "Speedrun!",
                "Minecraft but..."
            );
            case "technoblade" -> List.of(
                "Technoblade never dies!",
                "Blood for the Blood God!",
                "Orphans!",
                "Crown!"
            );
            case "grian" -> List.of(
                "Prank!",
                "Not a grian!",
                "Build tips!",
                "Mumbo!"
            );
            case "mumbo jumbo" -> List.of(
                "Hello there!",
                "Redstone!",
                "Mustard!",
                "Engineering!"
            );
            default -> List.of("Subscribe!", "Like!", "Comment!", "Notification squad!");
        };
        return quotes.get(ThreadLocalRandom.current().nextInt(quotes.size()));
    }
}
