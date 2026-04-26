package com.wayacreate.frogslimegamemode.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.wayacreate.frogslimegamemode.entity.FrogHelperEntity;
import com.wayacreate.frogslimegamemode.entity.SlimeHelperEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class HelperCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("helper")
            .requires(source -> source.hasPermissionLevel(2))
            .then(CommandManager.argument("helper", EntityArgumentType.entity())
                .executes(HelperCommand::showHelperInfo)
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
}
