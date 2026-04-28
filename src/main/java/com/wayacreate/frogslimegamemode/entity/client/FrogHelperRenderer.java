package com.wayacreate.frogslimegamemode.entity.client;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import com.wayacreate.frogslimegamemode.entity.FrogHelperEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.resources.ResourceLocation;

public class FrogHelperRenderer extends MobRenderer<FrogHelperEntity, SlimeModel<FrogHelperEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "textures/entity/frog_helper.png");
    
    public FrogHelperRenderer(EntityRendererProvider.Context context) {
        super(context, new SlimeModel<>(context.getPart(ModelLayers.SLIME)), 0.5f);
    }
    
    @Override
    public ResourceLocation getTexture(FrogHelperEntity entity) {
        return TEXTURE;
    }
    
    @Override
    public boolean hasLabel(FrogHelperEntity entity) {
        return false;
    }
}
