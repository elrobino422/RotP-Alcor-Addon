package com.rotpaddon.exampleaddon.client.render;

import com.github.standobyte.jojo.client.render.entity.model.stand.StandEntityModel;
import com.github.standobyte.jojo.client.render.entity.model.stand.StandModelRegistry;
import com.github.standobyte.jojo.client.render.entity.renderer.stand.StandEntityRenderer;
import com.rotpaddon.exampleaddon.AddonMain;
import com.rotpaddon.exampleaddon.entity.BlueNightStandEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import com.rotpaddon.exampleaddon.entity.BlueNightStandEntity;

/** Temporary renderer: displays the existing example texture and a copied geo model. */
public class BlueNightStandRenderer extends StandEntityRenderer<BlueNightStandEntity, StandEntityModel<BlueNightStandEntity>> {
    public BlueNightStandRenderer(EntityRendererManager renderManager) {
        super(renderManager,
                StandModelRegistry.registerModel(
                        new ResourceLocation(AddonMain.MOD_ID, "blue_night"),
                        BlueNightStandModel::new),
                new ResourceLocation(AddonMain.MOD_ID, "textures/entity/stand/example_stand.png"),
                0);

    }
    @Override
    public void render(
            BlueNightStandEntity entity,
            float entityYaw,
            float partialTicks,
            MatrixStack stack,
            IRenderTypeBuffer buffer,
            int packedLight) {

        // Blue Night's base form is rendered on the player
        // by BlueNightRingLayer.
    }

}
