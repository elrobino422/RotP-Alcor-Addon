package com.rotpaddon.exampleaddon.action;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.power.impl.stand.StandUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

/**
 * Single-target gravity attack: slows an enemy and holds them down
 * while the Stand channels the ability.
 */
public class SBHGravityCrush extends StandEntityAction {
    private static final int SLOWNESS_TICKS = 80; // 4 seconds
    private static final int SLOWNESS_AMPLIFIER = 2; // Slowness III

    public SBHGravityCrush(StandEntityAction.Builder builder) {
        super(builder);
    }

    @Override
    public TargetRequirement getTargetRequirement() {
        return TargetRequirement.ENTITY;
    }

    @Override
    public double getMaxRangeSqEntityTarget() {
        return 144.0D; // 12 blocks when the target is visible
    }

    @Override
    protected ActionConditionResult checkSpecificConditions(
            LivingEntity user, IStandPower power, ActionTarget target) {
        LivingEntity victim = getLivingTarget(target);
        if (victim == null || !victim.isAlive() || victim == user) {
            return ActionConditionResult.NEGATIVE;
        }
        return ActionConditionResult.POSITIVE;
    }

    @Override
    public void standPerform(World world, StandEntity standEntity,
            IStandPower userPower, StandEntityTask task) {
        if (world.isClientSide()) {
            return;
        }

        LivingEntity victim = getLivingTarget(task.getTarget());
        if (victim == null || !victim.isAlive() || victim == userPower.getUser()) {
            return;
        }

        victim.addEffect(new EffectInstance(
                Effects.MOVEMENT_SLOWDOWN,
                SLOWNESS_TICKS,
                SLOWNESS_AMPLIFIER,
                false, true, true));
    }

    @Override
    public void standTickPerform(World world, StandEntity standEntity,
            IStandPower userPower, StandEntityTask task) {
        if (world.isClientSide()) {
            return;
        }

        LivingEntity victim = getLivingTarget(task.getTarget());
        if (victim == null || !victim.isAlive() || victim == userPower.getUser()) {
            return;
        }

        // Constant extra downward acceleration opposes jumping and flight.
        Vector3d motion = victim.getDeltaMovement();
        victim.setDeltaMovement(
                motion.x * 0.9D,
                Math.max(-0.7D, motion.y - 0.24D),
                motion.z * 0.9D);
        victim.hurtMarked = true; // Sync velocity to clients, including players.

        if (world instanceof ServerWorld && world.getGameTime() % 3 == 0) {
            ((ServerWorld) world).sendParticles(
                    ParticleTypes.PORTAL,
                    victim.getX(),
                    victim.getY() + victim.getBbHeight() * 0.5D,
                    victim.getZ(),
                    5, 0.35D, 0.4D, 0.35D, 0.04D);
        }
    }

    private static LivingEntity getLivingTarget(ActionTarget target) {
        if (target == null) {
            return null;
        }
        Entity entity = target.getEntity();
        if (!(entity instanceof LivingEntity)) {
            return null;
        }
        // If a Stand is selected, affect its user instead of the manifestation.
        return StandUtil.getStandUser((LivingEntity) entity);
    }
}
