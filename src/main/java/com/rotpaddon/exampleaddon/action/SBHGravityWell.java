package com.rotpaddon.exampleaddon.action;

import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.rotpaddon.exampleaddon.entity.SBHGravityWellCoreEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

/**
 * Places a persistent gravity core at the aimed location.
 * The core entity owns the five-second effect, so using another Stand action
 * after this one's short recovery will not cancel the field.
 */
public class SBHGravityWell extends StandEntityAction {
    private static final double CAST_RANGE = 40.0D;
    private static final double AIR_FALLBACK_RANGE = 32.0D;

    public SBHGravityWell(StandEntityAction.Builder builder) {
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

        Vector3d center = findCenter(world, user, task.getTarget());
        SBHGravityWellCoreEntity core = new SBHGravityWellCoreEntity(world, user);
        core.setPos(center.x, center.y, center.z);
        world.addFreshEntity(core);
    }

    private static Vector3d findCenter(World world, LivingEntity user, ActionTarget target) {
        // The HUD can supply a nearby entity directly.
        if (target != null && target.getType() == ActionTarget.TargetType.ENTITY) {
            Entity entity = target.getEntity();
            if (entity != null && entity != user && entity.isAlive()) {
                return entity.getBoundingBox().getCenter();
            }
        }

        // For distant targets, trace the block yourself instead of relying
        // on the shorter default entity selection/pick range in the HUD.
        Vector3d start = user.getEyePosition(1.0F);
        Vector3d look = user.getLookAngle();
        RayTraceResult hit = world.clip(new RayTraceContext(
                start,
                start.add(look.scale(CAST_RANGE)),
                RayTraceContext.BlockMode.COLLIDER,
                RayTraceContext.FluidMode.NONE,
                user));

        if (hit.getType() == RayTraceResult.Type.BLOCK) {
            return hit.getLocation().add(0.0D, 1.0D, 0.0D);
        }
        return start.add(look.scale(AIR_FALLBACK_RANGE));
    }
}
