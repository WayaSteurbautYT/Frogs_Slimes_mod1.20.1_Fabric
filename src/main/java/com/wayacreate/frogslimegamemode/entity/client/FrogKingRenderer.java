package com.wayacreate.frogslimegamemode.entity.client;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import com.wayacreate.frogslimegamemode.entity.FrogKingEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.resources.ResourceLocation;

public class FrogKingRenderer extends MobRenderer<FrogKingEntity, SlimeModel<FrogKingEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "textures/entity/frog_king.png");
    
    public FrogKingRenderer(EntityRendererProvider.Context context) {
        super(context, new SlimeModel<>(context.getPart(ModelLayers.SLIME)), 0.8f);
    }
    
    @Override
    public ResourceLocation getTexture(FrogKingEntity entity) {
        return TEXTURE;
    }
    
    @Override
    public boolean hasLabel(FrogKingEntity entity) {
        return false;
    }
}
