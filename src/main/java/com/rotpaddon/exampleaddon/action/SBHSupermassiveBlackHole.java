package com.rotpaddon.exampleaddon.action;

import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.rotpaddon.exampleaddon.entity.SBHSupermassiveBlackHoleEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

/**
 * SBH's ultimate: an independent, timed singularity that draws in enemies,
 * deals gradual gravitational damage and ends in a non-block-breaking collapse.
 * The Stand can use other actions once the short casting recovery is finished.
 */
public class SBHSupermassiveBlackHole extends StandEntityAction {
    private static final double CAST_RANGE = 48.0D;
    private static final double AIR_FALLBACK = 38.0D;

    public SBHSupermassiveBlackHole(StandEntityAction.Builder builder) {
        super(builder);
    }

    @Override
    public TargetRequirement getTargetRequirement() {
        return TargetRequirement.ANY;
    }

    @Override
    public double getMaxRangeSqEntityTarget() {
        return CAST_RANGE * CAST_RANGE;
    }

    @Override
    public double getMaxRangeSqBlockTarget() {
        return CAST_RANGE * CAST_RANGE;
    }

    @Override
    public void standPerform(World world, StandEntity standEntity,
                             IStandPower userPower, StandEntityTask task) {
        if (world.isClientSide()) return;

        LivingEntity user = userPower.getUser();
        if (user == null || !user.isAlive()) return;

        Vector3d center = getTargetCenter(world, user, task.getTarget());
        SBHSupermassiveBlackHoleEntity singularity =
                new SBHSupermassiveBlackHoleEntity(world, user);
        singularity.setPos(center.x, center.y, center.z);
        world.addFreshEntity(singularity);
    }

    private static Vector3d getTargetCenter(World world, LivingEntity user, ActionTarget target) {
        if (target != null && target.getType() == ActionTarget.TargetType.ENTITY) {
            Entity hit = target.getEntity();
            if (hit != null && hit.isAlive() && hit != user) {
                // Fix the point in space rather than following a target entity.
                return hit.getBoundingBox().getCenter();
            }
        }

        Vector3d eye = user.getEyePosition(1.0F);
        Vector3d look = user.getLookAngle();
        RayTraceResult hit = world.clip(new RayTraceContext(
                eye, eye.add(look.scale(CAST_RANGE)),
                RayTraceContext.BlockMode.COLLIDER,
                RayTraceContext.FluidMode.NONE,
                user));

        return hit.getType() == RayTraceResult.Type.BLOCK
                ? hit.getLocation().add(0.0D, 2.0D, 0.0D)
                : eye.add(look.scale(AIR_FALLBACK));
    }
}
