package com.wayacreate.frogslimegamemode.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.wayacreate.frogslimegamemode.entity.FrogHelperEntity;
import com.wayacreate.frogslimegamemode.entity.SlimeHelperEntity;
import com.wayacreate.frogslimegamemode.integration.BaritoneIntegration;
import com.wayacreate.frogslimegamemode.item.RoleItem;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class HelperCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("helper")
            .requires(source -> source.hasPermissionLevel(0))
            .then(Commands.literal("giverole")
                .then(Commands.argument("role", StringArgumentType.string())
                    .suggests((context, builder) -> {
                        builder.suggest("Miner");
                        builder.suggest("Lumberjack");
                        builder.suggest("Combat Specialist");
                        builder.suggest("Farmer");
                        builder.suggest("Builder");
                        return builder.buildFuture();
                    })
                    .executes(context -> giveRoleItem(context))
                )
            )
            .then(Commands.argument("helper", EntityArgument.entity())
                .executes(HelperCommand::showHelperInfo)
                .then(Commands.literal("build")
                    .then(Commands.argument("schematic", StringArgumentType.string())
                        .executes(HelperCommand::setSchematicBuild)
                    )
                )
                .then(Commands.literal("stop")
                    .executes(HelperCommand::stopBuilding)
                )
                .then(Commands.literal("progress")
                    .executes(HelperCommand::getBuildProgress)
                )
            )
        );
    }
    
    private static int showHelperInfo(CommandContext<CommandSourceStack> context) {
        try {
            Entity entity = EntityArgument.getEntity(context, "helper");
            
            if (!(entity instanceof FrogHelperEntity) && !(entity instanceof SlimeHelperEntity)) {
                context.getSource().sendError(Component.literal("Target must be a frog or slime helper!"));
                return 0;
            }
            
            if (!(entity instanceof net.minecraft.world.entity.TamableAnimal tameable)) {
                context.getSource().sendError(Component.literal("Target is not tameable!"));
                return 0;
            }
            
            if (!tameable.isTamed() || tameable.getOwner() == null) {
                context.getSource().sendError(Component.literal("Helper is not tamed!"));
                return 0;
            }
            
            Player owner = (Player) tameable.getOwner();
            context.getSource().sendFeedback(() -> Component.literal("Helper owned by: " + owner.getName().getString()), true);
            
            if (entity instanceof FrogHelperEntity frog) {
                context.getSource().sendFeedback(() -> Component.literal("Type: Frog Helper, Evolution: " + frog.getEvolutionStage() + ", Role: " + frog.getRole()), true);
            } else if (entity instanceof SlimeHelperEntity slime) {
                context.getSource().sendFeedback(() -> Component.literal("Type: Slime Helper, Evolution: " + slime.getEvolutionStage() + ", Role: " + slime.getRole()), true);
            }
            
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(Component.literal("Failed to get helper info: " + e.getMessage()));
            return 0;
        }
    }
    
    private static int setSchematicBuild(CommandContext<CommandSourceStack> context) {
        try {
            Entity entity = EntityArgument.getEntity(context, "helper");
            String schematic = StringArgumentType.getString(context, "schematic");
            
            if (!(entity instanceof net.minecraft.world.entity.TamableAnimal tameable)) {
                context.getSource().sendError(Component.literal("Target is not tameable!"));
                return 0;
            }
            
            BaritoneIntegration.setSchematicBuild(tameable, schematic);
            context.getSource().sendFeedback(() -> Component.literal("Set helper to build schematic: " + schematic), true);
            
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(Component.literal("Failed to set schematic: " + e.getMessage()));
            return 0;
        }
    }
    
    private static int stopBuilding(CommandContext<CommandSourceStack> context) {
        try {
            Entity entity = EntityArgument.getEntity(context, "helper");
            
            if (!(entity instanceof net.minecraft.world.entity.TamableAnimal tameable)) {
                context.getSource().sendError(Component.literal("Target is not tameable!"));
                return 0;
            }
            
            BaritoneIntegration.stopBuilding(tameable);
            context.getSource().sendFeedback(() -> Component.literal("Helper stopped building"), true);
            
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(Component.literal("Failed to stop building: " + e.getMessage()));
            return 0;
        }
    }
    
    private static int getBuildProgress(CommandContext<CommandSourceStack> context) {
        try {
            Entity entity = EntityArgument.getEntity(context, "helper");
            
            if (!(entity instanceof net.minecraft.world.entity.TamableAnimal tameable)) {
                context.getSource().sendError(Component.literal("Target is not tameable!"));
                return 0;
            }
            
            int progress = BaritoneIntegration.getBuildProgress(tameable);
            context.getSource().sendFeedback(() -> Component.literal("Build progress: " + progress + "%"), true);
            
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(Component.literal("Failed to get progress: " + e.getMessage()));
            return 0;
        }
    }
    
    private static int giveRoleItem(CommandContext<CommandSourceStack> context) {
        try {
            String role = StringArgumentType.getString(context, "role");
            Player player = context.getSource().getPlayer();
            
            if (player == null) {
                context.getSource().sendError(Component.literal("This command can only be used by players!"));
                return 0;
            }
            
            ItemStack roleItem = RoleItem.createRoleItem(role);
            player.getInventory().insertStack(roleItem);
            
            context.getSource().sendFeedback(() -> Component.literal("Gave " + role + " role assignment item to " + player.getName().getString()), true);
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(Component.literal("Failed to give role item: " + e.getMessage()));
            return 0;
        }
    }
}
