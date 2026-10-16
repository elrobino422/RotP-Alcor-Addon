
package com.rotpaddon.exampleaddon.client.render;

import com.github.standobyte.jojo.client.ClientUtil;
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

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
        modid = AddonMain.MOD_ID,
        bus = EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class BlueNightRingLayer extends LayerRenderer<
        AbstractClientPlayerEntity,
        PlayerModel<AbstractClientPlayerEntity>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(
                    AddonMain.MOD_ID,
                    "textures/entity/blue_night_ring.png"
            );

    private final PlayerModel<AbstractClientPlayerEntity> braceletModel;

    private static final PlayerModel<AbstractClientPlayerEntity> FP_DEFAULT =
            new PlayerModel<>(0.05F, false);

    private static final PlayerModel<AbstractClientPlayerEntity> FP_SLIM =
            new PlayerModel<>(0.05F, true);

    private static boolean debugLogged = false;

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

    // ==========================================
    // THIRD PERSON — Existing working rendering
    // ==========================================

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
                buffer.getBuffer(
                        RenderType.entityTranslucent(TEXTURE)
                ),
                packedLight,
                OverlayTexture.NO_OVERLAY
        );
    }

    // ==========================================
    // FIRST PERSON — Forge RenderArmEvent
    // ==========================================

    @SubscribeEvent
    public static void renderFirstPerson(RenderArmEvent event) {

        if (!debugLogged) {
            AddonMain.LOGGER.info(
                    "[BLUE NIGHT] Forge RenderArmEvent reached!"
            );
            debugLogged = true;
        }

        if (event.getArm() != HandSide.RIGHT) {
            return;
        }

        AbstractClientPlayerEntity player = event.getPlayer();

        if (player == null
                || player.isSpectator()
                || !isBlueNightActive(player)) {
            return;
        }

        // Support both Steve and Alex skin models.
        PlayerModel<AbstractClientPlayerEntity> model =
                "slim".equals(player.getModelName())
                        ? FP_SLIM : FP_DEFAULT;

        // Prepare the model for first-person rendering.
        ClientUtil.setupForFirstPersonRender(model, player);

        model.rightSleeve.xRot = 0.0F;
        model.rightSleeve.visible = true;

        // Draw only the bracelet overlay.
        model.rightSleeve.render(
                event.getPoseStack(),
                event.getMultiBufferSource().getBuffer(
                        RenderType.entityTranslucent(TEXTURE)
                ),
                event.getPackedLight(),
                OverlayTexture.NO_OVERLAY
        );
    }
}
