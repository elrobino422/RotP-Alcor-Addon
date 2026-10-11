
package com.rotpaddon.exampleaddon.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rotpaddon.exampleaddon.entity.SBHVoidOrbEntity;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

import com.rotpaddon.exampleaddon.AddonMain;

public class SBHVoidOrbRenderer extends EntityRenderer<SBHVoidOrbEntity> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(AddonMain.MOD_ID, "textures/entity/sbh_void_orb.png");

    private final ModelRenderer orb;

    public SBHVoidOrbRenderer(EntityRendererManager manager) {
        super(manager);

        orb = new ModelRenderer(16, 16, 0, 0);
        orb.addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
    }

    @Override
    public void render(SBHVoidOrbEntity entity, float entityYaw, float partialTicks,
                       MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {

        matrixStack.pushPose();
        matrixStack.scale(0.125F, 0.125F, 0.125F);

        IVertexBuilder vertexBuilder = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));

        orb.render(matrixStack, vertexBuilder, packedLight,
                OverlayTexture.NO_OVERLAY);

        matrixStack.popPose();

        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SBHVoidOrbEntity entity) {
        return TEXTURE;
    }
}
