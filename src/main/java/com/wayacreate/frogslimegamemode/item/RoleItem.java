package com.wayacreate.frogslimegamemode.item;

import com.wayacreate.frogslimegamemode.entity.FrogHelperEntity;
import com.wayacreate.frogslimegamemode.entity.SlimeHelperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class RoleItem extends Item {
    private final String role;
    
    public RoleItem(String role, Settings settings) {
        super(settings);
        this.role = role;
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        
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
                assignRole(frogs.get(0), user);
                stack.decrement(1);
                return TypedActionResult.success(stack);
            } else if (!slimes.isEmpty()) {
                assignRole(slimes.get(0), user);
                stack.decrement(1);
                return TypedActionResult.success(stack);
            } else {
                user.sendMessage(Text.literal("No helper nearby to assign role!")
                    .formatted(Formatting.RED), true);
            }
        }
        
        return TypedActionResult.pass(stack);
    }
    
    private void assignRole(Object helper, PlayerEntity player) {
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
