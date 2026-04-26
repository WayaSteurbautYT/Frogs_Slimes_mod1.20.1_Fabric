package com.wayacreate.frogslimegamemode.item;

import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Random;

public class YouTuberSwordItem extends SwordItem {
    private final String youtuberName;
    private final Random random = new Random();
    
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
        return quotes.get(random.nextInt(quotes.size()));
    }
}
