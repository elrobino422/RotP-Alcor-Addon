package com.rotpaddon.exampleaddon.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rotpaddon.exampleaddon.AddonMain;
import com.rotpaddon.exampleaddon.entity.SBHVoidOrbEntity;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

/**
 * Renders the user's Blockbench singularity, 16-piece accretion disk, and
 * orbiting fragments. All animation is client-side; projectile hit logic
 * remains in SBHVoidOrbEntity.
 */
public class SBHVoidOrbRenderer extends EntityRenderer<SBHVoidOrbEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(
            AddonMain.MOD_ID, "textures/entity/sbh_void_orb.png");
    private static final int FULL_BRIGHT = 0xF000F0;

    private final SBHVoidOrbModel model = new SBHVoidOrbModel();

    public SBHVoidOrbRenderer(EntityRendererManager manager) {
        super(manager);
        this.shadowRadius = 0.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(SBHVoidOrbEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(SBHVoidOrbEntity entity, float entityYaw, float partialTicks,
                       MatrixStack matrixStack, IRenderTypeBuffer buffer,
                       int packedLight) {
        matrixStack.pushPose();
        // Visible as a compact but detailed 3D orb around the projectile.
        matrixStack.scale(0.8F, 0.8F, 0.8F);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(55.0F));

        model.setupAnim(entity, 0.0F, 0.0F,
                entity.tickCount + partialTicks, 0.0F, 0.0F);

        IVertexBuilder vertices = buffer.getBuffer(
                RenderType.entityCutoutNoCull(TEXTURE));
        model.renderToBuffer(matrixStack, vertices, FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        matrixStack.popPose();
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer,
                packedLight);
    }
}
