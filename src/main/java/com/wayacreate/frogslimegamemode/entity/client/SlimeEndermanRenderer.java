package com.wayacreate.frogslimegamemode.entity.client;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import com.wayacreate.frogslimegamemode.entity.SlimeEndermanEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;

public class SlimeEndermanRenderer extends MobRenderer<SlimeEndermanEntity, EndermanModel<SlimeEndermanEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "textures/entity/slime_enderman.png");
    
    public SlimeEndermanRenderer(EntityRendererProvider.Context context) {
        super(context, new EndermanModel<>(context.getPart(ModelLayers.ENDERMAN)), 0.5f);
    }
    
    @Override
    public ResourceLocation getTexture(SlimeEndermanEntity entity) {
        return TEXTURE;
    }
    
    @Override
    public boolean hasLabel(SlimeEndermanEntity entity) {
        return false;
    }
}
