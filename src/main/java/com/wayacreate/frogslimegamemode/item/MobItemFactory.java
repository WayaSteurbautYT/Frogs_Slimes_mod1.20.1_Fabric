package com.wayacreate.frogslimegamemode.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.HashMap;
import java.util.Map;

public class MobItemFactory {
    public static final String MOB_ITEM_NBT = "MobItem";
    public static final String MOB_TYPE_NBT = "MobType";
    
    private static final Map<String, MobItemDefinition> MOB_ITEMS = new HashMap<>();
    
    static {
        // Hostile mobs
        register(new MobItemDefinition("zombie", Items.ROTTEN_FLESH, "Zombie Essence", 0x555555));
        register(new MobItemDefinition("skeleton", Items.BONE, "Skeleton Essence", 0xFFFFFF));
        register(new MobItemDefinition("creeper", Items.GUNPOWDER, "Creeper Essence", 0x00AA00));
        register(new MobItemDefinition("spider", Items.STRING, "Spider Essence", 0x333333));
        register(new MobItemDefinition("enderman", Items.ENDER_PEARL, "Enderman Essence", 0x9900CC));
        register(new MobItemDefinition("witch", Items.GLASS_BOTTLE, "Witch Essence", 0x9900FF));
        register(new MobItemDefinition("blaze", Items.BLAZE_ROD, "Blaze Essence", 0xFFAA00));
        register(new MobItemDefinition("ghast", Items.GHAST_TEAR, "Ghast Essence", 0xFFFFFF));
        register(new MobItemDefinition("piglin", Items.GOLD_INGOT, "Piglin Essence", 0xFFAA00));
        register(new MobItemDefinition("warden", Items.ECHO_SHARD, "Warden Essence", 0x003366));
        
        // Passive mobs
        register(new MobItemDefinition("pig", Items.PORKCHOP, "Pig Essence", 0xFFAA00));
        register(new MobItemDefinition("cow", Items.BEEF, "Cow Essence", 0x8B4513));
        register(new MobItemDefinition("chicken", Items.CHICKEN, "Chicken Essence", 0xFFFFFF));
        register(new MobItemDefinition("sheep", Items.WHITE_WOOL, "Sheep Essence", 0xFFFFFF));
        register(new MobItemDefinition("rabbit", Items.RABBIT_HIDE, "Rabbit Essence", 0xAAAAAA));
        
        // Special mobs
        register(new MobItemDefinition("iron_golem", Items.IRON_INGOT, "Iron Golem Essence", 0xCCCCCC));
        register(new MobItemDefinition("snow_golem", Items.SNOWBALL, "Snow Golem Essence", 0xFFFFFF));
        register(new MobItemDefinition("villager", Items.EMERALD, "Villager Essence", 0x00FF00));
        
        // YouTuber themed
        register(new MobItemDefinition("dream_speedrunner", Items.NETHERITE_INGOT, "Dream Essence", 0x00FFFF));
        register(new MobItemDefinition("technoblade_pig", Items.GOLDEN_APPLE, "Technoblade Essence", 0xFFD700));
        register(new MobItemDefinition("grian_minecraft", Items.DIAMOND, "Grian Essence", 0x00FFFF));
        register(new MobItemDefinition("mumbo_jumbo", Items.REDSTONE, "Mumbo Jumbo Essence", 0xFF0000));
    }
    
    private static void register(MobItemDefinition definition) {
        MOB_ITEMS.put(definition.mobType(), definition);
    }
    
    public static ItemStack createMobItem(String mobType) {
        MobItemDefinition definition = MOB_ITEMS.get(mobType);
        if (definition == null) {
            return ItemStack.EMPTY;
        }
        
        ItemStack stack = new ItemStack(definition.baseItem());
        CompoundTag nbt = stack.getOrCreateNbt();
        nbt.putBoolean(MOB_ITEM_NBT, true);
        nbt.putString(MOB_TYPE_NBT, mobType);
        
        stack.setCustomName(Component.literal(definition.displayName())
            .formatted(ChatFormatting.BOLD)
            .styled(style -> style.withColor(definition.color())));
        
        return stack;
    }
    
    public static boolean isMobItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        CompoundTag nbt = stack.getNbt();
        return nbt != null && nbt.getBoolean(MOB_ITEM_NBT);
    }
    
    public static String getMobType(ItemStack stack) {
        if (!isMobItem(stack)) return null;
        CompoundTag nbt = stack.getNbt();
        return nbt != null ? nbt.getString(MOB_TYPE_NBT) : null;
    }
    
    public static MobItemDefinition getDefinition(String mobType) {
        return MOB_ITEMS.get(mobType);
    }
    
    public static Map<String, MobItemDefinition> getAllDefinitions() {
        return new HashMap<>(MOB_ITEMS);
    }
    
    public record MobItemDefinition(
        String mobType,
        net.minecraft.world.item.Item baseItem,
        String displayName,
        int color
    ) {}
}
