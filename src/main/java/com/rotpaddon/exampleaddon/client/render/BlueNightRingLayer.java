
package com.rotpaddon.exampleaddon.client.render;

import com.github.standobyte.jojo.client.render.entity.layerrenderer.IFirstPersonHandLayer;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.rotpaddon.exampleaddon.AddonMain;
import com.rotpaddon.exampleaddon.init.InitStands;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;

public class BlueNightRingLayer extends LayerRenderer<
        AbstractClientPlayerEntity,
        PlayerModel<AbstractClientPlayerEntity>>
        implements IFirstPersonHandLayer {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(
                    AddonMain.MOD_ID,
                    "textures/entity/blue_night_ring.png"
            );

    private final PlayerModel<AbstractClientPlayerEntity> braceletModel;

    private boolean hookLogged = false;
    private boolean activeLogged = false;

    public BlueNightRingLayer(PlayerRenderer renderer, boolean slim) {
        super(renderer);
        this.braceletModel = new PlayerModel<>(0.05F, slim);
    }

    private static boolean isBlueNightActive(
            AbstractClientPlayerEntity player) {

        return IStandPower.getStandPowerOptional(player)
                .map(power ->
                        power.isActive()
                                && power.getType() ==
                                InitStands.STAND_BLUE_NIGHT.getStandType())
                .orElse(false);
    }

    // Third-person bracelet rendering
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

        if (!isBlueNightActive(player)) {
            return;
        }

        braceletModel.rightSleeve.copyFrom(
                getParentModel().rightArm
        );

        braceletModel.rightSleeve.visible = true;

        braceletModel.rightSleeve.render(
                stack,
                buffer.getBuffer(RenderType.entityTranslucent(TEXTURE)),
                packedLight,
                OverlayTexture.NO_OVERLAY
        );
    }

    // First-person bracelet rendering
    @Override
    public void renderHandFirstPerson(
            HandSide side,
            MatrixStack stack,
            IRenderTypeBuffer buffer,
            int packedLight,
            AbstractClientPlayerEntity player,
            PlayerRenderer playerRenderer) {

        if (!hookLogged) {
            AddonMain.LOGGER.info(
                    "[BLUE NIGHT] First-person hook called! Side: {}",
                    side
            );
            hookLogged = true;
        }

        if (side == HandSide.RIGHT
                && isBlueNightActive(player)
                && !activeLogged) {

            AddonMain.LOGGER.info(
                    "[BLUE NIGHT] Right-hand bracelet should render!"
            );

            activeLogged = true;
        }


        if (side != HandSide.RIGHT || !isBlueNightActive(player)) {
            return;
        }

        IFirstPersonHandLayer.defaultRender(
                side,
                stack,
                buffer,
                packedLight,
                player,
                playerRenderer,
                braceletModel,
                TEXTURE
        );
    }
}
