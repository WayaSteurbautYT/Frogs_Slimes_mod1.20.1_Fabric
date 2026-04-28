package com.wayacreate.frogslimegamemode.entity.client;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import com.wayacreate.frogslimegamemode.entity.SlimeHelperEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.resources.ResourceLocation;

public class SlimeHelperRenderer extends MobRenderer<SlimeHelperEntity, SlimeModel<SlimeHelperEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "textures/entity/slime_helper.png");
    private static final ResourceLocation FINAL_FORM_TEXTURE = ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "textures/entity/slime_helper_final.png");
    
    public SlimeHelperRenderer(EntityRendererProvider.Context context) {
        super(context, new SlimeModel<>(context.getPart(ModelLayers.SLIME)), 0.5f);
    }
    
    @Override
    public ResourceLocation getTexture(SlimeHelperEntity entity) {
        return entity.isFinalForm() ? FINAL_FORM_TEXTURE : TEXTURE;
    }
    
    @Override
    public boolean hasLabel(SlimeHelperEntity entity) {
        return false;
    }
}
