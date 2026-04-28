package com.wayacreate.frogslimegamemode.entity;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

public class ModEntities {
    public static final EntityType<FrogHelperEntity> FROG_HELPER =
        EntityType.Builder.create(FrogHelperEntity::new, MobCategory.CREATURE)
            .setDimensions(0.8f, 0.8f)
            .maxTrackingRange(8)
            .build("frog_helper");

    public static final EntityType<SlimeHelperEntity> SLIME_HELPER =
        EntityType.Builder.create(SlimeHelperEntity::new, MobCategory.CREATURE)
            .setDimensions(1.0f, 1.0f)
            .maxTrackingRange(8)
            .build("slime_helper");

    public static final EntityType<GiantSlimeBossEntity> GIANT_SLIME_BOSS =
        EntityType.Builder.create(GiantSlimeBossEntity::new, MobCategory.MONSTER)
            .setDimensions(8.0f, 8.0f)
            .maxTrackingRange(16)
            .build("giant_slime_boss");

    public static final EntityType<FrogKingEntity> FROG_KING =
        EntityType.Builder.create(FrogKingEntity::new, MobCategory.MONSTER)
            .setDimensions(2.0f, 2.0f)
            .maxTrackingRange(12)
            .build("frog_king");

    public static final EntityType<SlimeEndermanEntity> SLIME_ENDERMAN =
        EntityType.Builder.create(SlimeEndermanEntity::new, MobCategory.MONSTER)
            .setDimensions(2.5f, 3.0f)
            .maxTrackingRange(16)
            .build("slime_enderman");

    public static void register(IEventBus modBus) {
        modBus.addListener(ModEntities::onRegister);
        modBus.addListener(ModEntities::onRegisterAttributes);
    }

    private static void onRegister(RegisterEvent event) {
        event.register(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "frog_helper"), () -> FROG_HELPER);
        event.register(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "slime_helper"), () -> SLIME_HELPER);
        event.register(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "giant_slime_boss"), () -> GIANT_SLIME_BOSS);
        event.register(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "frog_king"), () -> FROG_KING);
        event.register(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "slime_enderman"), () -> SLIME_ENDERMAN);

        FrogSlimeGamemode.LOGGER.info("Registered 5 entity types");
    }

    private static void onRegisterAttributes(EntityAttributeCreationEvent event) {
        event.put(FROG_HELPER, FrogHelperEntity.createAttributes());
        event.put(SLIME_HELPER, SlimeHelperEntity.createAttributes());
        event.put(GIANT_SLIME_BOSS, GiantSlimeBossEntity.createAttributes());
        event.put(FROG_KING, FrogKingEntity.createAttributes());
        event.put(SLIME_ENDERMAN, SlimeEndermanEntity.createAttributes());
    }
}
