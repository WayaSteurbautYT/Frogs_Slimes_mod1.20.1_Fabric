package com.wayacreate.frogslimegamemode.entity.client;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import com.wayacreate.frogslimegamemode.entity.SlimeEndermanEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;

public class SlimeEndermanRenderer extends MobEntityRenderer<SlimeEndermanEntity, EndermanEntityModel<SlimeEndermanEntity>> {
    private static final Identifier TEXTURE = new Identifier(FrogSlimeGamemode.MOD_ID, "textures/entity/slime_enderman.png");
    
    public SlimeEndermanRenderer(EntityRendererFactory.Context context) {
        super(context, new EndermanEntityModel<>(context.getPart(EntityModelLayers.ENDERMAN)), 0.5f);
    }
    
    @Override
    public Identifier getTexture(SlimeEndermanEntity entity) {
        return TEXTURE;
    }
    
    @Override
    public boolean hasLabel(SlimeEndermanEntity entity) {
        return false;
    }
}
