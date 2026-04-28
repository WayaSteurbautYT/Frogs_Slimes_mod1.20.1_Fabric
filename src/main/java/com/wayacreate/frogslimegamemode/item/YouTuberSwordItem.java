package com.wayacreate.frogslimegamemode.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class YouTuberSwordItem extends SwordItem {
    private final String youtuberName;
    
    public YouTuberSwordItem(String youtuberName, Tier toolMaterial, int attackDamage, float attackSpeed, Properties settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
        this.youtuberName = youtuberName;
    }
    
    @Override
    public Component getName(ItemStack stack) {
        return Component.literal(youtuberName + "'s Blade")
            .formatted(ChatFormatting.GOLD, ChatFormatting.BOLD);
    }
    
    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
    
    @Override
    public void onStoppedUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        if (!world.isClient && user instanceof Player player) {
            applySwordAbility(player);
        }
        super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }
    
    private void applySwordAbility(Player player) {
        switch (youtuberName.toLowerCase()) {
            case "dream" -> {
                // Speed boost for "speedrunning"
                player.addStatusEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.SPEED, 200, 2));
                player.sendMessage(Component.literal("Speedrun activated!")
                    .formatted(ChatFormatting.GREEN), true);
            }
            case "technoblade" -> {
                // Strength and resistance
                player.addStatusEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.STRENGTH, 200, 1));
                player.addStatusEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.RESISTANCE, 200, 1));
                player.sendMessage(Component.literal("Blood for the Blood God!")
                    .formatted(ChatFormatting.DARK_RED), true);
            }
            case "grian" -> {
                // Invisibility for "pranking"
                player.addStatusEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.INVISIBILITY, 100, 0));
                player.sendMessage(Component.literal("Prank time!")
                    .formatted(ChatFormatting.LIGHT_PURPLE), true);
            }
            case "mumbo jumbo" -> {
                // Haste for "engineering"
                player.addStatusEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.HASTE, 200, 2));
                player.sendMessage(Component.literal("Engineering mode activated!")
                    .formatted(ChatFormatting.YELLOW), true);
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
