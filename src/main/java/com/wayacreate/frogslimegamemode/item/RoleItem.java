package com.wayacreate.frogslimegamemode.item;

import com.wayacreate.frogslimegamemode.entity.FrogHelperEntity;
import com.wayacreate.frogslimegamemode.entity.SlimeHelperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class RoleItem extends Item {
    public static final String ROLE_ITEM_NBT = "RoleItem";
    public static final String ROLE_TYPE_NBT = "RoleType";
    private final String roleType;
    
    public RoleItem(Settings settings) {
        super(settings);
        this.roleType = null;
    }
    
    public RoleItem(Settings settings, String roleType) {
        super(settings);
        this.roleType = roleType;
    }
    
    @Override
    public ItemStack getDefaultStack() {
        ItemStack stick = new ItemStack(Items.STICK);
        NbtCompound nbt = stick.getOrCreateNbt();
        nbt.putBoolean(ROLE_ITEM_NBT, true);
        nbt.putString(ROLE_TYPE_NBT, roleType != null ? roleType : "Unknown");
        
        Formatting color = getRoleColor(roleType);
        stick.setCustomName(Text.literal((roleType != null ? roleType : "Role") + " Assignment Stick")
            .formatted(color, Formatting.BOLD));
        return stick;
    }
    
    private Formatting getRoleColor(String role) {
        if (role == null) return Formatting.GOLD;
        switch (role.toLowerCase()) {
            case "miner":
                return Formatting.DARK_GRAY;
            case "lumberjack":
                return Formatting.DARK_GREEN;
            case "combat specialist":
                return Formatting.RED;
            default:
                return Formatting.GOLD;
        }
    }
    
    public static ItemStack createRoleItem(String role) {
        ItemStack stick = new ItemStack(Items.STICK);
        NbtCompound nbt = stick.getOrCreateNbt();
        nbt.putBoolean(ROLE_ITEM_NBT, true);
        nbt.putString(ROLE_TYPE_NBT, role);
        Formatting color = getRoleColorStatic(role);
        stick.setCustomName(Text.literal(role + " Assignment Stick").formatted(color, Formatting.BOLD));
        return stick;
    }
    
    private static Formatting getRoleColorStatic(String role) {
        if (role == null) return Formatting.GOLD;
        switch (role.toLowerCase()) {
            case "miner":
                return Formatting.DARK_GRAY;
            case "lumberjack":
                return Formatting.DARK_GREEN;
            case "combat specialist":
                return Formatting.RED;
            default:
                return Formatting.GOLD;
        }
    }
    
    public static boolean isRoleItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (!stack.isOf(Items.STICK)) return false;
        NbtCompound nbt = stack.getNbt();
        return nbt != null && nbt.getBoolean(ROLE_ITEM_NBT);
    }
    
    public static String getRoleType(ItemStack stack) {
        if (!isRoleItem(stack)) return null;
        NbtCompound nbt = stack.getNbt();
        return nbt != null ? nbt.getString(ROLE_TYPE_NBT) : null;
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        
        // Determine role based on item ID
        String role = getRoleFromItemId(stack);
        if (role == null) {
            return TypedActionResult.pass(stack);
        }
        
        if (!world.isClient) {
            // Find nearby helper to assign role
            double searchRadius = 5.0;
            
            java.util.List<FrogHelperEntity> frogs = world.getEntitiesByClass(
                FrogHelperEntity.class,
                user.getBoundingBox().expand(searchRadius),
                e -> e.isOwner(user)
            );
            
            java.util.List<SlimeHelperEntity> slimes = world.getEntitiesByClass(
                SlimeHelperEntity.class,
                user.getBoundingBox().expand(searchRadius),
                e -> e.isOwner(user)
            );
            
            if (!frogs.isEmpty()) {
                assignRole(frogs.get(0), role, user);
                stack.decrement(1);
                return TypedActionResult.success(stack);
            } else if (!slimes.isEmpty()) {
                assignRole(slimes.get(0), role, user);
                stack.decrement(1);
                return TypedActionResult.success(stack);
            } else {
                user.sendMessage(Text.literal("No helper nearby to assign role!")
                    .formatted(Formatting.RED), true);
            }
        }
        
        return TypedActionResult.pass(stack);
    }
    
    private String getRoleFromItemId(ItemStack stack) {
        if (stack.getItem() == com.wayacreate.frogslimegamemode.item.ModItems.MINER_ROLE) {
            return "Miner";
        } else if (stack.getItem() == com.wayacreate.frogslimegamemode.item.ModItems.LUMBERJACK_ROLE) {
            return "Lumberjack";
        } else if (stack.getItem() == com.wayacreate.frogslimegamemode.item.ModItems.COMBAT_ROLE) {
            return "Combat Specialist";
        }
        return null;
    }
    
    private void assignRole(Object helper, String role, PlayerEntity player) {
        String message = "";
        
        if (helper instanceof FrogHelperEntity frog) {
            frog.setRole(role);
            message = "Your frog helper is now a " + role + "!";
        } else if (helper instanceof SlimeHelperEntity slime) {
            slime.setRole(role);
            message = "Your slime helper is now a " + role + "!";
        }
        
        player.sendMessage(Text.literal(message)
            .formatted(Formatting.GREEN, Formatting.BOLD), true);
    }
}
