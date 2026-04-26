package com.wayacreate.frogslimegamemode.entity.client;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import com.wayacreate.frogslimegamemode.entity.FrogHelperEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.util.Identifier;

public class FrogHelperRenderer extends MobEntityRenderer<FrogHelperEntity, SlimeEntityModel<FrogHelperEntity>> {
    private static final Identifier TEXTURE = new Identifier(FrogSlimeGamemode.MOD_ID, "textures/entity/frog_helper.png");
    
    public FrogHelperRenderer(EntityRendererFactory.Context context) {
        super(context, new SlimeEntityModel<>(context.getPart(EntityModelLayers.SLIME)), 0.5f);
    }
    
    @Override
    public Identifier getTexture(FrogHelperEntity entity) {
        return TEXTURE;
    }
}