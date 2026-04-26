package com.wayacreate.frogslimegamemode.entity;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<FrogHelperEntity> FROG_HELPER = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(FrogSlimeGamemode.MOD_ID, "frog_helper"),
        EntityType.Builder.create(FrogHelperEntity::new, SpawnGroup.CREATURE)
            .setDimensions(0.8f, 0.8f)
            .maxTrackingRange(8)
            .build("frog_helper")
    );
    
    public static final EntityType<SlimeHelperEntity> SLIME_HELPER = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(FrogSlimeGamemode.MOD_ID, "slime_helper"),
        EntityType.Builder.create(SlimeHelperEntity::new, SpawnGroup.CREATURE)
            .setDimensions(1.0f, 1.0f)
            .maxTrackingRange(8)
            .build("slime_helper")
    );
    
    public static final EntityType<GiantSlimeBossEntity> GIANT_SLIME_BOSS = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(FrogSlimeGamemode.MOD_ID, "giant_slime_boss"),
        EntityType.Builder.create(GiantSlimeBossEntity::new, SpawnGroup.MONSTER)
            .setDimensions(8.0f, 8.0f)
            .maxTrackingRange(16)
            .build("giant_slime_boss")
    );
    
    public static final EntityType<FrogKingEntity> FROG_KING = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(FrogSlimeGamemode.MOD_ID, "frog_king"),
        EntityType.Builder.create(FrogKingEntity::new, SpawnGroup.MONSTER)
            .setDimensions(2.0f, 2.0f)
            .maxTrackingRange(12)
            .build("frog_king")
    );
    
    public static void register() {
        FabricDefaultAttributeRegistry.register(FROG_HELPER, FrogHelperEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(SLIME_HELPER, SlimeHelperEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(GIANT_SLIME_BOSS, GiantSlimeBossEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(FROG_KING, FrogKingEntity.createAttributes());
        FrogSlimeGamemode.LOGGER.info("Registered " + 4 + " entity types");
    }
}