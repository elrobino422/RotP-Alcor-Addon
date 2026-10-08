package com.rotpaddon.exampleaddon.client.render;

import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.client.render.entity.model.stand.HumanoidStandModel;
import com.github.standobyte.jojo.client.render.entity.pose.IModelPose;
import com.github.standobyte.jojo.client.render.entity.pose.ModelPose;
import com.github.standobyte.jojo.client.render.entity.pose.RotationAngle;
import com.github.standobyte.jojo.client.render.entity.pose.anim.PosedActionAnimation;
import com.github.standobyte.jojo.entity.stand.StandPose;
import com.rotpaddon.exampleaddon.entity.SBHStandEntity;

import net.minecraft.client.renderer.model.ModelRenderer;

public class SBHStandModel extends HumanoidStandModel<SBHStandEntity> {
    private ModelRenderer pickaxe;

    public SBHStandModel() {
        super();
    }

    @Override
    protected RotationAngle[][] initSummonPoseRotations() {
        return new RotationAngle[][] {
            new RotationAngle[] {},
            new RotationAngle[] {}
        };
    }

    @Override
    protected void initActionPoses() {
        actionAnim.put(StandPose.RANGED_ATTACK, new PosedActionAnimation.Builder<SBHStandEntity>()
                .addPose(StandEntityAction.Phase.BUTTON_HOLD, new ModelPose<>(new RotationAngle[] {
                        new RotationAngle(body, 0.0F, -0.48F, 0.0F),
                        new RotationAngle(leftArm, 0.0F, 0.0F, -0.7854F),
                        new RotationAngle(leftForeArm, 0.0F, 0.0F, 0.6109F),
                        new RotationAngle(rightArm, -1.0908F, 0.0F, 1.5708F),
                        new RotationAngle(rightForeArm, 0.0F, 0.0F, 0.0F)
                }))
                .build(idlePose));

        super.initActionPoses();
    }

    @Override
    public void prepareMobModel(SBHStandEntity entity, float walkAnimPos, float walkAnimSpeed, float partialTick) {
        super.prepareMobModel(entity, walkAnimPos, walkAnimSpeed, partialTick);
        if (pickaxe != null) {
            pickaxe.visible = entity.hasPickaxe();
        }
    }

    @Override
    protected ModelPose<SBHStandEntity> initIdlePose() {
        return super.initIdlePose();
    }

    @Override
    protected IModelPose<SBHStandEntity> initIdlePose2Loop() {
        return super.initIdlePose2Loop();
    }
}
