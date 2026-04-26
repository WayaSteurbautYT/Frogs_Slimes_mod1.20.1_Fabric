package com.wayacreate.frogslimegamemode.entity.client;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import com.wayacreate.frogslimegamemode.entity.GiantSlimeBossEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.util.Identifier;

public class GiantSlimeBossRenderer extends MobEntityRenderer<GiantSlimeBossEntity, SlimeEntityModel<GiantSlimeBossEntity>> {
    private static final Identifier TEXTURE = new Identifier(FrogSlimeGamemode.MOD_ID, "textures/entity/giant_slime_boss.png");
    private static final Identifier ENRAGED_TEXTURE = new Identifier(FrogSlimeGamemode.MOD_ID, "textures/entity/giant_slime_boss_enraged.png");
    
    public GiantSlimeBossRenderer(EntityRendererFactory.Context context) {
        super(context, new SlimeEntityModel<>(context.getPart(EntityModelLayers.SLIME)), 1.0f);
    }
    
    @Override
    public Identifier getTexture(GiantSlimeBossEntity entity) {
        return entity.isEnraged() ? ENRAGED_TEXTURE : TEXTURE;
    }
}
