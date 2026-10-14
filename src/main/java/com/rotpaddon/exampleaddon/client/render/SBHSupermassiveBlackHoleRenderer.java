package com.rotpaddon.exampleaddon.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rotpaddon.exampleaddon.AddonMain;
import com.rotpaddon.exampleaddon.entity.SBHSupermassiveBlackHoleEntity;

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
 * Multi-layered, full-bright rotating black-hole core.
 * Rendered client-side; the entity controls gravity on the server.
 */
public class SBHSupermassiveBlackHoleRenderer
        extends EntityRenderer<SBHSupermassiveBlackHoleEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
            AddonMain.MOD_ID, "textures/entity/sbh_supermassive_black_hole.png");
    private static final int FULL_LIGHT = 0xF000F0;

    public SBHSupermassiveBlackHoleRenderer(EntityRendererManager manager) {
        super(manager);
        shadowRadius = 0.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(SBHSupermassiveBlackHoleEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(SBHSupermassiveBlackHoleEntity entity, float entityYaw,
                       float partialTicks, MatrixStack pose,
                       IRenderTypeBuffer buffer, int packedLight) {
        float age = entity.tickCount + partialTicks;
        float grow = MathHelper.clamp(age / 35.0F, 0.0F, 1.0F);
        float expire = MathHelper.clamp(
                (SBHSupermassiveBlackHoleEntity.DURATION_TICKS - age) / 9.0F,
                0.0F, 1.0F);
        float alpha = grow * expire;
        if (alpha <= 0.0F) return;

        float pulse = 1.0F + (float) Math.sin(age * 0.30F) * 0.065F;
        float radius = (2.7F + 3.3F * grow) * pulse;
        float speed = age * (3.8F + age * 0.016F);

        IVertexBuilder builder = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE));
        pose.pushPose();
        pose.mulPose(entityRenderDispatcher.cameraOrientation());

        // Outer accretion disc.
        pose.pushPose();
        pose.mulPose(Vector3f.ZP.rotationDegrees(speed));
        quad(pose, builder, radius, alpha * 0.93F);
        pose.popPose();

        // Bright inner ring spins the opposite way.
        pose.pushPose();
        pose.translate(0.0D, 0.0D, 0.04D);
        pose.mulPose(Vector3f.ZP.rotationDegrees(-speed * 1.4F));
        quad(pose, builder, radius * 0.71F, alpha * 0.62F);
        pose.popPose();

        // Dense central horizon.
        pose.pushPose();
        pose.translate(0.0D, 0.0D, 0.08D);
        pose.mulPose(Vector3f.ZP.rotationDegrees(age * 0.85F));
        quad(pose, builder, radius * 0.42F, alpha * 0.98F);
        pose.popPose();

        pose.popPose();
        super.render(entity, entityYaw, partialTicks, pose, buffer, packedLight);
    }

    private static void quad(MatrixStack pose, IVertexBuilder builder,
                             float radius, float alpha) {
        Matrix4f transform = pose.last().pose();
        Matrix3f normal = pose.last().normal();
        int a = MathHelper.clamp((int) (255.0F * alpha), 0, 255);

        vertex(builder, transform, normal, -radius, -radius, 0.0F, 1.0F, a);
        vertex(builder, transform, normal, radius, -radius, 1.0F, 1.0F, a);
        vertex(builder, transform, normal, radius, radius, 1.0F, 0.0F, a);
        vertex(builder, transform, normal, -radius, radius, 0.0F, 0.0F, a);
    }

    private static void vertex(IVertexBuilder builder, Matrix4f matrix,
                               Matrix3f normal, float x, float y,
                               float u, float v, int a) {
        builder.vertex(matrix, x, y, 0.0F)
                .color(255, 255, 255, a)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(FULL_LIGHT)
                .normal(normal, 0.0F, 0.0F, 1.0F)
                .endVertex();
    }
}
