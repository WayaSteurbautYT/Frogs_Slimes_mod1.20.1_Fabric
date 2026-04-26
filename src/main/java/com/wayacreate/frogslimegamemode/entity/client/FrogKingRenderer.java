package com.wayacreate.frogslimegamemode.entity.client;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import com.wayacreate.frogslimegamemode.entity.FrogKingEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.util.Identifier;

public class FrogKingRenderer extends MobEntityRenderer<FrogKingEntity, SlimeEntityModel<FrogKingEntity>> {
    private static final Identifier TEXTURE = new Identifier(FrogSlimeGamemode.MOD_ID, "textures/entity/frog_king.png");
    
    public FrogKingRenderer(EntityRendererFactory.Context context) {
        super(context, new SlimeEntityModel<>(context.getPart(EntityModelLayers.SLIME)), 0.8f);
    }
    
    @Override
    public Identifier getTexture(FrogKingEntity entity) {
        return TEXTURE;
    }
    
    @Override
    public boolean hasLabel(FrogKingEntity entity) {
        return false;
    }
}
