package com.wayacreate.frogslimegamemode.entity.client;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import com.wayacreate.frogslimegamemode.entity.SlimeHelperEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.util.Identifier;

public class SlimeHelperRenderer extends MobEntityRenderer<SlimeHelperEntity, SlimeEntityModel<SlimeHelperEntity>> {
    private static final Identifier TEXTURE = new Identifier(FrogSlimeGamemode.MOD_ID, "textures/entity/slime_helper.png");
    private static final Identifier FINAL_FORM_TEXTURE = new Identifier(FrogSlimeGamemode.MOD_ID, "textures/entity/slime_helper_final.png");
    
    public SlimeHelperRenderer(EntityRendererFactory.Context context) {
        super(context, new SlimeEntityModel<>(context.getPart(EntityModelLayers.SLIME)), 0.5f);
    }
    
    @Override
    public Identifier getTexture(SlimeHelperEntity entity) {
        return entity.isFinalForm() ? FINAL_FORM_TEXTURE : TEXTURE;
    }
    
    @Override
    public boolean hasLabel(SlimeHelperEntity entity) {
        return false;
    }
}