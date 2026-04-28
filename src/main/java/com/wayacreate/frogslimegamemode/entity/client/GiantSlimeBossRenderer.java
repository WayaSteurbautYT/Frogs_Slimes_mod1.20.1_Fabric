package com.wayacreate.frogslimegamemode.entity.client;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import com.wayacreate.frogslimegamemode.entity.GiantSlimeBossEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.resources.ResourceLocation;

public class GiantSlimeBossRenderer extends MobRenderer<GiantSlimeBossEntity, SlimeModel<GiantSlimeBossEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "textures/entity/giant_slime_boss.png");
    private static final ResourceLocation ENRAGED_TEXTURE = ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "textures/entity/giant_slime_boss_enraged.png");
    
    public GiantSlimeBossRenderer(EntityRendererProvider.Context context) {
        super(context, new SlimeModel<>(context.getPart(ModelLayers.SLIME)), 1.0f);
    }
    
    @Override
    public ResourceLocation getTexture(GiantSlimeBossEntity entity) {
        return entity.isEnraged() ? ENRAGED_TEXTURE : TEXTURE;
    }
    
    @Override
    public boolean hasLabel(GiantSlimeBossEntity entity) {
        return false;
    }
}
