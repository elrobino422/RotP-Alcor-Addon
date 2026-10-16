
package com.rotpaddon.exampleaddon.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rotpaddon.exampleaddon.AddonMain;
import com.rotpaddon.exampleaddon.init.InitStands;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;

public class BlueNightRingLayer extends LayerRenderer<
        AbstractClientPlayerEntity,
        PlayerModel<AbstractClientPlayerEntity>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(
                    AddonMain.MOD_ID,
                    "textures/entity/blue_night_ring.png"
            );

    private final PlayerModel<AbstractClientPlayerEntity> ringModel;

    public BlueNightRingLayer(PlayerRenderer renderer, boolean slim) {
        super(renderer);
        this.ringModel = new PlayerModel<>(0.05F, slim);
    }

    @Override
    public void render(
            MatrixStack stack,
            IRenderTypeBuffer buffer,
            int packedLight,
            AbstractClientPlayerEntity player,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch) {

        boolean hasBlueNight = IStandPower
                .getStandPowerOptional(player)
                .map(power ->
                        power.hasPower()
                                && power.getType()
                                == InitStands.STAND_BLUE_NIGHT.getStandType())
                .orElse(false);

        if (!hasBlueNight) {
            return;
        }

        // Follow the player's animated right arm.
        ringModel.rightSleeve.copyFrom(
                getParentModel().rightArm
        );

        ringModel.rightSleeve.visible = true;

        // Render only the textured overlay on the arm.
        ringModel.rightSleeve.render(
                stack,
                buffer.getBuffer(
                        RenderType.entityTranslucent(TEXTURE)
                ),
                packedLight,
                OverlayTexture.NO_OVERLAY
        );
    }
}
