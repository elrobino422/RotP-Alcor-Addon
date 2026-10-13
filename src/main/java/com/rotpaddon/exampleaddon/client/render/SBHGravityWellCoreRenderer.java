package com.rotpaddon.exampleaddon.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rotpaddon.exampleaddon.AddonMain;
import com.rotpaddon.exampleaddon.entity.SBHGravityWellCoreEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

/**
 * Two full-bright, rotating, camera-facing textured quads form a small
 * singularity / accretion disc. This is visual-only; no collision box.
 */
public class SBHGravityWellCoreRenderer extends EntityRenderer<SBHGravityWellCoreEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(
            AddonMain.MOD_ID, "textures/entity/sbh_gravity_well_core.png");
    private static final int FULL_BRIGHT = 0xF000F0;

    public SBHGravityWellCoreRenderer(EntityRendererManager manager) {
        super(manager);
        this.shadowRadius = 0.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(SBHGravityWellCoreEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(SBHGravityWellCoreEntity entity, float entityYaw,
                       float partialTicks, MatrixStack pose, IRenderTypeBuffer buffers,
                       int packedLight) {
        float age = entity.tickCount + partialTicks;
        float fadeIn = MathHelper.clamp(age / 6.0F, 0.0F, 1.0F);
        float fadeOut = MathHelper.clamp((SBHGravityWellCoreEntity.LIFESPAN - age) / 12.0F, 0.0F, 1.0F);
        float alpha = fadeIn * fadeOut;
        if (alpha <= 0.0F) return;

        IVertexBuilder builder = buffers.getBuffer(RenderType.entityTranslucent(TEXTURE));
        pose.pushPose();
        pose.mulPose(entityRenderDispatcher.cameraOrientation());

        // Outer purple/cyan ring.
        pose.pushPose();
        pose.mulPose(Vector3f.ZP.rotationDegrees(age * 4.4F));
        renderQuad(pose, builder, 1.85F, 0.92F * alpha);
        pose.popPose();

        // Counter-rotating inner halo enhances the gravitational shimmer.
        pose.pushPose();
        pose.translate(0.0D, 0.0D, 0.02D);
        pose.mulPose(Vector3f.ZP.rotationDegrees(-age * 6.3F));
        renderQuad(pose, builder, 1.25F, 0.48F * alpha);
        pose.popPose();

        pose.popPose();
        super.render(entity, entityYaw, partialTicks, pose, buffers, packedLight);
    }

    private static void renderQuad(MatrixStack pose, IVertexBuilder builder,
                                   float halfSize, float alpha) {
        Matrix4f position = pose.last().pose();
        Matrix3f normal = pose.last().normal();
        int a = MathHelper.clamp((int) (255.0F * alpha), 0, 255);

        vertex(builder, position, normal, -halfSize, -halfSize, 0.0F, 1.0F, a);
        vertex(builder, position, normal, halfSize, -halfSize, 1.0F, 1.0F, a);
        vertex(builder, position, normal, halfSize, halfSize, 1.0F, 0.0F, a);
        vertex(builder, position, normal, -halfSize, halfSize, 0.0F, 0.0F, a);
    }

    private static void vertex(IVertexBuilder builder, Matrix4f position, Matrix3f normal,
                               float x, float y, float u, float v, int alpha) {
        builder.vertex(position, x, y, 0.0F)
                .color(255, 255, 255, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(FULL_BRIGHT)
                .normal(normal, 0.0F, 0.0F, 1.0F)
                .endVertex();
    }
}
