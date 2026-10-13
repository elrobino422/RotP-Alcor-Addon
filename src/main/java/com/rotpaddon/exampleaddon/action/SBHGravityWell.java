package com.rotpaddon.exampleaddon.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mc.MCUtil;
import com.rotpaddon.exampleaddon.AddonMain;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Places a temporary gravity field at the target location. The field keeps
 * running after the casting task ends, allowing follow-up Stand abilities.
 *
 * Fields are tracked per ServerWorld and updated only on the server tick.
 */
@Mod.EventBusSubscriber(modid = AddonMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SBHGravityWell extends StandEntityAction {
    private static final int DURATION_TICKS = 100; // 5 seconds
    private static final double RADIUS = 10.0D;
    private static final double RADIUS_SQ = RADIUS * RADIUS;
    private static final double CAST_RANGE = 24.0D;

    // Weak keys prevent a world from being held in memory by an inactive field.
    private static final Map<ServerWorld, List<GravityField>> ACTIVE_FIELDS =
            new WeakHashMap<>();

    public SBHGravityWell(StandEntityAction.Builder builder) {
        super(builder);
    }

    // Allow aiming at a block, a mob, or empty air.
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
        if (!(world instanceof ServerWorld)) {
            return;
        }

        LivingEntity user = userPower.getUser();
        if (user == null || !user.isAlive()) {
            return;
        }

        Vector3d center = findCenter(world, user, task.getTarget());
        ServerWorld serverWorld = (ServerWorld) world;
        ACTIVE_FIELDS.computeIfAbsent(serverWorld, w -> new ArrayList<>())
                .add(new GravityField(center, user.getId()));
    }

    private static Vector3d findCenter(World world, LivingEntity user, ActionTarget target) {
        if (target != null && target.getType() == ActionTarget.TargetType.ENTITY) {
            Entity entity = target.getEntity();
            if (entity instanceof LivingEntity && entity != user) {
                return entity.getBoundingBox().getCenter();
            }
        }

        // Block collision picks a point on the visible surface; no hit makes
        // the well appear in the air ahead of the caster.
        Vector3d start = user.getEyePosition(1.0F);
        Vector3d look = user.getLookAngle();
        Vector3d end = start.add(look.scale(CAST_RANGE));
        RayTraceResult hit = world.clip(new RayTraceContext(
                start, end,
                RayTraceContext.BlockMode.COLLIDER,
                RayTraceContext.FluidMode.NONE,
                user));

        return hit.getType() == RayTraceResult.Type.BLOCK
                ? hit.getLocation().add(0.0D, 1.0D, 0.0D)
                : start.add(look.scale(16.0D));
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.world instanceof ServerWorld)) {
            return;
        }

        ServerWorld world = (ServerWorld) event.world;
        List<GravityField> fields = ACTIVE_FIELDS.get(world);
        if (fields == null || fields.isEmpty()) {
            return;
        }

        Iterator<GravityField> iterator = fields.iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().tick(world)) {
                iterator.remove();
            }
        }
        if (fields.isEmpty()) {
            ACTIVE_FIELDS.remove(world);
        }
    }

    private static class GravityField {
        private final Vector3d center;
        private final int ownerId;
        private int age;

        private GravityField(Vector3d center, int ownerId) {
            this.center = center;
            this.ownerId = ownerId;
        }

        private boolean tick(ServerWorld world) {
            if (age >= DURATION_TICKS) {
                return false;
            }

            Entity ownerEntity = world.getEntity(ownerId);
            if (!(ownerEntity instanceof LivingEntity) || !ownerEntity.isAlive()) {
                return false;
            }
            LivingEntity owner = (LivingEntity) ownerEntity;
            age++;

            AxisAlignedBB area = new AxisAlignedBB(
                    center.x - RADIUS, center.y - RADIUS, center.z - RADIUS,
                    center.x + RADIUS, center.y + RADIUS, center.z + RADIUS);

            for (LivingEntity target : world.getEntitiesOfClass(LivingEntity.class, area)) {
                if (!canPull(owner, target)) {
                    continue;
                }

                Vector3d offset = center.subtract(target.getBoundingBox().getCenter());
                double distanceSq = offset.lengthSqr();
                if (distanceSq > RADIUS_SQ || distanceSq < 0.0001D) {
                    continue;
                }

                Vector3d motion = target.getDeltaMovement();
                double distance = Math.sqrt(distanceSq);

                if (distance < 1.25D) {
                    // Avoid throwing victims from one side of the core to the other.
                    target.setDeltaMovement(motion.scale(0.55D));
                }
                else {
                    double strength = 0.065D + 0.11D * (1.0D - distance / RADIUS);
                    Vector3d pull = offset.scale(strength / distance);

                    target.setDeltaMovement(
                            MathHelper.clamp(motion.x * 0.86D + pull.x, -0.45D, 0.45D),
                            MathHelper.clamp(motion.y * 0.88D + pull.y, -0.30D, 0.30D),
                            MathHelper.clamp(motion.z * 0.86D + pull.z, -0.45D, 0.45D));
                }

                target.hasImpulse = true;
                target.hurtMarked = true; // Sync affected player velocity.
            }

            spawnParticles(world);
            return true;
        }

        private static boolean canPull(LivingEntity owner, LivingEntity target) {
            if (!target.isAlive() || target.isSpectator() || target == owner
                    || target instanceof StandEntity || !MCUtil.canHarm(owner, target)) {
                return false;
            }
            // Protect the caster's own tamed animals.
            if (target instanceof TameableEntity
                    && ((TameableEntity) target).getOwner() == owner) {
                return false;
            }
            return true;
        }

        private void spawnParticles(ServerWorld world) {
            // Four spiraling portal particles every other tick, plus a dark core.
            if (age % 2 == 0) {
                for (int i = 0; i < 4; i++) {
                    double angle = age * 0.20D + i * Math.PI / 2.0D;
                    double radius = 1.0D + (7.0D -
                            ((age * 0.12D + i * 1.75D) % 7.0D));
                    double x = center.x + Math.cos(angle) * radius;
                    double y = center.y + Math.sin(angle * 1.4D) * 1.15D;
                    double z = center.z + Math.sin(angle) * radius;
                    world.sendParticles(ParticleTypes.PORTAL,
                            x, y, z, 1, 0.02D, 0.02D, 0.02D, 0.01D);
                }
            }

            if (age % 4 == 0) {
                world.sendParticles(ParticleTypes.LARGE_SMOKE,
                        center.x, center.y, center.z,
                        2, 0.20D, 0.20D, 0.20D, 0.005D);
            }
        }
    }
}
