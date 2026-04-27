package com.wayacreate.frogslimegamemode.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.wayacreate.frogslimegamemode.entity.FrogHelperEntity;
import com.wayacreate.frogslimegamemode.entity.SlimeHelperEntity;
import com.wayacreate.frogslimegamemode.integration.BaritoneIntegration;
import com.wayacreate.frogslimegamemode.item.RoleItem;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class HelperCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("helper")
            .requires(source -> source.hasPermissionLevel(0))
            .then(CommandManager.literal("giverole")
                .then(CommandManager.argument("role", StringArgumentType.string())
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
            .then(CommandManager.argument("helper", EntityArgumentType.entity())
                .executes(HelperCommand::showHelperInfo)
                .then(CommandManager.literal("build")
                    .then(CommandManager.argument("schematic", StringArgumentType.string())
                        .executes(HelperCommand::setSchematicBuild)
                    )
                )
                .then(CommandManager.literal("stop")
                    .executes(HelperCommand::stopBuilding)
                )
                .then(CommandManager.literal("progress")
                    .executes(HelperCommand::getBuildProgress)
                )
            )
        );
    }
    
    private static int showHelperInfo(CommandContext<ServerCommandSource> context) {
        try {
            Entity entity = EntityArgumentType.getEntity(context, "helper");
            
            if (!(entity instanceof FrogHelperEntity) && !(entity instanceof SlimeHelperEntity)) {
                context.getSource().sendError(Text.literal("Target must be a frog or slime helper!"));
                return 0;
            }
            
            if (!(entity instanceof net.minecraft.entity.passive.TameableEntity tameable)) {
                context.getSource().sendError(Text.literal("Target is not tameable!"));
                return 0;
            }
            
            if (!tameable.isTamed() || tameable.getOwner() == null) {
                context.getSource().sendError(Text.literal("Helper is not tamed!"));
                return 0;
            }
            
            PlayerEntity owner = (PlayerEntity) tameable.getOwner();
            context.getSource().sendFeedback(() -> Text.literal("Helper owned by: " + owner.getName().getString()), true);
            
            if (entity instanceof FrogHelperEntity frog) {
                context.getSource().sendFeedback(() -> Text.literal("Type: Frog Helper, Evolution: " + frog.getEvolutionStage() + ", Role: " + frog.getRole()), true);
            } else if (entity instanceof SlimeHelperEntity slime) {
                context.getSource().sendFeedback(() -> Text.literal("Type: Slime Helper, Evolution: " + slime.getEvolutionStage() + ", Role: " + slime.getRole()), true);
            }
            
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("Failed to get helper info: " + e.getMessage()));
            return 0;
        }
    }
    
    private static int setSchematicBuild(CommandContext<ServerCommandSource> context) {
        try {
            Entity entity = EntityArgumentType.getEntity(context, "helper");
            String schematic = StringArgumentType.getString(context, "schematic");
            
            if (!(entity instanceof net.minecraft.entity.passive.TameableEntity tameable)) {
                context.getSource().sendError(Text.literal("Target is not tameable!"));
                return 0;
            }
            
            BaritoneIntegration.setSchematicBuild(tameable, schematic);
            context.getSource().sendFeedback(() -> Text.literal("Set helper to build schematic: " + schematic), true);
            
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("Failed to set schematic: " + e.getMessage()));
            return 0;
        }
    }
    
    private static int stopBuilding(CommandContext<ServerCommandSource> context) {
        try {
            Entity entity = EntityArgumentType.getEntity(context, "helper");
            
            if (!(entity instanceof net.minecraft.entity.passive.TameableEntity tameable)) {
                context.getSource().sendError(Text.literal("Target is not tameable!"));
                return 0;
            }
            
            BaritoneIntegration.stopBuilding(tameable);
            context.getSource().sendFeedback(() -> Text.literal("Helper stopped building"), true);
            
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("Failed to stop building: " + e.getMessage()));
            return 0;
        }
    }
    
    private static int getBuildProgress(CommandContext<ServerCommandSource> context) {
        try {
            Entity entity = EntityArgumentType.getEntity(context, "helper");
            
            if (!(entity instanceof net.minecraft.entity.passive.TameableEntity tameable)) {
                context.getSource().sendError(Text.literal("Target is not tameable!"));
                return 0;
            }
            
            int progress = BaritoneIntegration.getBuildProgress(tameable);
            context.getSource().sendFeedback(() -> Text.literal("Build progress: " + progress + "%"), true);
            
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("Failed to get progress: " + e.getMessage()));
            return 0;
        }
    }
    
    private static int giveRoleItem(CommandContext<ServerCommandSource> context) {
        try {
            String role = StringArgumentType.getString(context, "role");
            PlayerEntity player = context.getSource().getPlayer();
            
            if (player == null) {
                context.getSource().sendError(Text.literal("This command can only be used by players!"));
                return 0;
            }
            
            ItemStack roleItem = RoleItem.createRoleItem(role);
            player.getInventory().insertStack(roleItem);
            
            context.getSource().sendFeedback(() -> Text.literal("Gave " + role + " role assignment item to " + player.getName().getString()), true);
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("Failed to give role item: " + e.getMessage()));
            return 0;
        }
    }
}
