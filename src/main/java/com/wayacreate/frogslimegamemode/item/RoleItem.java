package com.wayacreate.frogslimegamemode.item;

import com.wayacreate.frogslimegamemode.achievements.AchievementManager;
import com.wayacreate.frogslimegamemode.entity.FrogHelperEntity;
import com.wayacreate.frogslimegamemode.entity.SlimeHelperEntity;
import com.wayacreate.frogslimegamemode.entity.ai.HelperRoleManager;
import com.wayacreate.frogslimegamemode.tasks.TaskManager;
import com.wayacreate.frogslimegamemode.tasks.TaskType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

import java.util.Comparator;
import java.util.stream.Stream;

public class RoleItem extends Item {
    public static final String ROLE_ITEM_NBT = "RoleItem";
    public static final String ROLE_TYPE_NBT = "RoleType";
    private final String roleType;
    
    public RoleItem(Properties settings) {
        super(settings);
        this.roleType = null;
    }
    
    public RoleItem(Properties settings, String roleType) {
        super(settings);
        this.roleType = roleType;
    }
    
    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        return configureRoleStack(stack, roleType);
    }
    
    private ChatFormatting getRoleColor(String role) {
        return HelperRoleManager.getRoleColor(role);
    }
    
    public static ItemStack createRoleItem(String role) {
        String normalizedRole = HelperRoleManager.normalizeRole(role);
        Item roleItem = getRoleItemForType(normalizedRole);
        if (roleItem == null) {
            return ItemStack.EMPTY;
        }

        return configureRoleStack(new ItemStack(roleItem), normalizedRole);
    }
    
    private static ChatFormatting getRoleColorStatic(String role) {
        return HelperRoleManager.getRoleColor(role);
    }
    
    public static boolean isRoleItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        CompoundTag nbt = stack.getNbt();
        return stack.getItem() instanceof RoleItem || (nbt != null && nbt.getBoolean(ROLE_ITEM_NBT));
    }
    
    public static String getRoleType(ItemStack stack) {
        if (!isRoleItem(stack)) return null;
        CompoundTag nbt = stack.getNbt();
        if (nbt != null && nbt.contains(ROLE_TYPE_NBT)) {
            return HelperRoleManager.normalizeRole(nbt.getString(ROLE_TYPE_NBT));
        }
        if (stack.getItem() instanceof RoleItem roleItem && roleItem.roleType != null) {
            return HelperRoleManager.normalizeRole(roleItem.roleType);
        }
        return null;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getStackInHand(hand);
        
        String role = HelperRoleManager.normalizeRole(getRoleType(stack));
        if (role == null) {
            return InteractionResultHolder.pass(stack);
        }
        
        if (!world.isClient) {
            double searchRadius = 5.0;

            var frogs = world.getEntitiesByClass(
                FrogHelperEntity.class,
                user.getBoundingBox().expand(searchRadius),
                e -> e.isOwner(user)
            );
            
            var slimes = world.getEntitiesByClass(
                SlimeHelperEntity.class,
                user.getBoundingBox().expand(searchRadius),
                e -> e.isOwner(user)
            );
            
            TamableAnimal nearestHelper = Stream.concat(frogs.stream(), slimes.stream())
                .min(Comparator.comparingDouble(entity -> entity.squaredDistanceTo(user)))
                .orElse(null);

            if (nearestHelper != null) {
                assignRole(nearestHelper, role, user);
                stack.decrement(1);
                return InteractionResultHolder.success(stack, false);
            } else {
                user.sendMessage(Component.literal("No helper nearby to assign role!")
                    .formatted(ChatFormatting.RED), true);
            }
        }
        
        return InteractionResultHolder.pass(stack);
    }
    
    private void assignRole(TamableAnimal helper, String role, Player player) {
        String message = "";
        
        if (helper instanceof FrogHelperEntity frog) {
            frog.setRole(role);
            message = "Your frog helper is now a " + role + "!";
        } else if (helper instanceof SlimeHelperEntity slime) {
            slime.setRole(role);
            message = "Your slime helper is now a " + role + "!";
        }
        
        player.sendMessage(Component.literal(message)
            .formatted(ChatFormatting.GREEN, ChatFormatting.BOLD), true);

        TaskManager.completeTask(player, TaskType.ASSIGN_ROLE);
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            AchievementManager.unlockAchievement(serverPlayer, "helper_commander");
        }
    }

    private static ItemStack configureRoleStack(ItemStack stack, String role) {
        String normalizedRole = HelperRoleManager.normalizeRole(role);
        CompoundTag nbt = stack.getOrCreateNbt();
        nbt.putBoolean(ROLE_ITEM_NBT, true);
        nbt.putString(ROLE_TYPE_NBT, normalizedRole);

        ChatFormatting color = getRoleColorStatic(normalizedRole);
        stack.setCustomName(Component.literal(normalizedRole + " Assignment Stick")
            .formatted(color, ChatFormatting.BOLD));
        return stack;
    }

    private static Item getRoleItemForType(String role) {
        return switch (HelperRoleManager.normalizeRole(role)) {
            case HelperRoleManager.MINER -> ModItems.MINER_ROLE;
            case HelperRoleManager.LUMBERJACK -> ModItems.LUMBERJACK_ROLE;
            case HelperRoleManager.COMBAT_SPECIALIST -> ModItems.COMBAT_ROLE;
            case HelperRoleManager.BUILDER -> ModItems.BUILDER_ROLE;
            case HelperRoleManager.FARMER -> ModItems.FARMER_ROLE;
            default -> null;
        };
    }
}
