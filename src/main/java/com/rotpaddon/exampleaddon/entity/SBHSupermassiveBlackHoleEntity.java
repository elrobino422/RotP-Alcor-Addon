package com.rotpaddon.exampleaddon.entity;

import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mc.MCUtil;
import com.github.standobyte.jojo.util.mc.damage.StandEntityDamageSource;
import com.rotpaddon.exampleaddon.init.InitEntities;
import com.rotpaddon.exampleaddon.init.InitParticles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * A 7-second, server-authoritative singularity.
 *
 * During its life it pulls hostile entities in a 22-block sphere and pulses
 * modest Stand damage once per second. At the end it collapses into a
 * stronger outward blast without breaking or replacing any blocks.
 */
public class SBHSupermassiveBlackHoleEntity extends Entity {
    public static final int DURATION_TICKS = 140;
    public static final double MAX_RADIUS = 22.0D;
    private static final double MAX_RADIUS_SQ = MAX_RADIUS * MAX_RADIUS;
    private static final int PULSE_INTERVAL_TICKS = 20;
    private static final float PULSE_DAMAGE = 3.0F;
    private static final float COLLAPSE_DAMAGE = 16.0F;

    private int ownerId = -1;
    private int ageTicks = 0;

    public SBHSupermassiveBlackHoleEntity(
            EntityType<? extends SBHSupermassiveBlackHoleEntity> type, World level) {
        super(type, level);
        noPhysics = true;
        setNoGravity(true);
    }

    public SBHSupermassiveBlackHoleEntity(World world, LivingEntity caster) {
        this(InitEntities.SBH_SUPERMASSIVE_BLACK_HOLE.get(), world);
        ownerId = caster.getId();
    }

    @Override
    protected void defineSynchedData() {
        // Rendering uses the synchronized entity tickCount, not custom data.
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT tag) {
        ownerId = tag.getInt("SBHOwnerId");
        ageTicks = tag.getInt("SBHAge");
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT tag) {
        tag.putInt("SBHOwnerId", ownerId);
        tag.putInt("SBHAge", ageTicks);
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide()) {
            return;
        }

        ++ageTicks;
        if (ageTicks > DURATION_TICKS) {
            remove();
            return;
        }

        Entity ownerEntity = level.getEntity(ownerId);
        if (!(ownerEntity instanceof LivingEntity) || !ownerEntity.isAlive()) {
            remove();
            return;
        }
        LivingEntity caster = (LivingEntity) ownerEntity;

        if (ageTicks >= DURATION_TICKS) {
            collapse(caster);
            remove();
            return;
        }

        double currentRadius = Math.min(MAX_RADIUS, 7.0D + ageTicks * 0.24D);
        Vector3d center = position();
        AxisAlignedBB bounds = makeBounds(center, currentRadius);
        for (LivingEntity victim : level.getEntitiesOfClass(LivingEntity.class, bounds)) {
            if (!canAffect(caster, victim)) continue;

            Vector3d offset = center.subtract(victim.getBoundingBox().getCenter());
            double distanceSq = offset.lengthSqr();
            if (distanceSq < 0.0001D || distanceSq > currentRadius * currentRadius) {
                continue;
            }

            double distance = Math.sqrt(distanceSq);
            Vector3d motion = victim.getDeltaMovement();
            if (distance < 2.0D) {
                victim.setDeltaMovement(motion.scale(0.46D));
            }
            else {
                double pullForce = (0.09D + 0.16D * (1.0D - distance / currentRadius))
                        * Math.min(1.0D, ageTicks / 16.0D);
                Vector3d pull = offset.scale(pullForce / distance);
                victim.setDeltaMovement(
                        MathHelper.clamp(motion.x * 0.84D + pull.x, -0.65D, 0.65D),
                        MathHelper.clamp(motion.y * 0.87D + pull.y, -0.46D, 0.46D),
                        MathHelper.clamp(motion.z * 0.84D + pull.z, -0.65D, 0.65D));
            }
            victim.hasImpulse = true;
            victim.hurtMarked = true;

            // Damage once per second, not once per tick; no armor bypass.
            if (ageTicks >= PULSE_INTERVAL_TICKS
                    && ageTicks % PULSE_INTERVAL_TICKS == 0) {
                dealStandDamage(caster, victim, PULSE_DAMAGE);
            }
        }

        if (level instanceof ServerWorld) {
            spawnVortexParticles((ServerWorld) level, currentRadius);
        }
    }

    private static AxisAlignedBB makeBounds(Vector3d point, double radius) {
        return new AxisAlignedBB(
                point.x - radius, point.y - radius, point.z - radius,
                point.x + radius, point.y + radius, point.z + radius);
    }

    private static boolean canAffect(LivingEntity caster, LivingEntity victim) {
        if (victim == caster || !victim.isAlive() || victim.isSpectator()
                || victim instanceof StandEntity || !MCUtil.canHarm(caster, victim)) {
            return false;
        }
        if (victim instanceof TameableEntity
                && ((TameableEntity) victim).getOwner() == caster) {
            return false;
        }
        return true;
    }

    private void dealStandDamage(LivingEntity caster, LivingEntity target, float amount) {
        IStandPower.getStandPowerOptional(caster).ifPresent(standPower -> {
            StandEntityDamageSource source =
                    new StandEntityDamageSource("sbhSingularity", caster, standPower)
                            .setKnockbackReduction(0.0F);
            target.hurt(source, amount);
        });
    }

    private void collapse(LivingEntity caster) {
        Vector3d center = position();
        for (LivingEntity victim :
                level.getEntitiesOfClass(LivingEntity.class, makeBounds(center, MAX_RADIUS))) {
            if (!canAffect(caster, victim)) continue;

            Vector3d displacement = victim.getBoundingBox().getCenter().subtract(center);
            double distance = displacement.length();
            if (distance > MAX_RADIUS) continue;

            float damage = COLLAPSE_DAMAGE
                    * (float) (1.0D - 0.55D * distance / MAX_RADIUS);
            dealStandDamage(caster, victim, damage);

            // The final implosion rebounds into a brief outward shockwave.
            Vector3d outward = distance < 0.001D
                    ? new Vector3d(0, 0.4D, 0)
                    : displacement.scale(1.0D / distance);
            victim.setDeltaMovement(victim.getDeltaMovement().add(
                    outward.x * 0.9D, Math.max(0.25D, outward.y * 0.6D + 0.3D),
                    outward.z * 0.9D));
            victim.hasImpulse = true;
            victim.hurtMarked = true;
        }

        // Visual-only collapse. Intentionally DO NOT call world.explode:
        // this ultimate does not grief builds or destroy the environment.
        if (level instanceof ServerWorld) {
            ServerWorld server = (ServerWorld) level;
            server.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                    center.x, center.y, center.z, 1, 0, 0, 0, 0);
            for (int i = 0; i < 72; i++) {
                double angle = i * Math.PI * 2.0D / 72.0D;
                double height = (i % 9 - 4) * 0.25D;
                double xSpeed = Math.cos(angle) * 0.45D;
                double zSpeed = Math.sin(angle) * 0.45D;
                server.sendParticles(InitParticles.GRAVITY_WELL_SPARK.get(),
                        center.x, center.y + height, center.z,
                        0, xSpeed, height * 0.25D, zSpeed, 1.0D);
            }
            level.playSound(null, center.x, center.y, center.z,
                    SoundEvents.GENERIC_EXPLODE, SoundCategory.HOSTILE, 2.0F, 0.65F);
        }
    }

    private void spawnVortexParticles(ServerWorld server, double radius) {
        Vector3d center = position();

        if (ageTicks % 2 == 0) {
            for (int i = 0; i < 10; ++i) {
                double theta = ageTicks * 0.25D + i * Math.PI * 0.2D;
                double r = 3.0D + (radius - 3.0D) *
                        (1.0D - (ageTicks * 0.035D + i * 0.13D) % 1.0D);
                double x = center.x + r * Math.cos(theta);
                double y = center.y + 3.0D * Math.sin(theta * 1.6D + i);
                double z = center.z + r * Math.sin(theta);

                Vector3d towardCenter = center.subtract(x, y, z).normalize().scale(0.20D);
                server.sendParticles(InitParticles.GRAVITY_WELL_SPARK.get(),
                        x, y, z, 0,
                        towardCenter.x, towardCenter.y, towardCenter.z, 1.0D);
            }
        }

        if (ageTicks % 5 == 0) {
            server.sendParticles(ParticleTypes.PORTAL,
                    center.x, center.y, center.z,
                    15, 3.2D, 3.2D, 3.2D, 0.4D);
            server.sendParticles(ParticleTypes.DRAGON_BREATH,
                    center.x, center.y, center.z,
                    3, 2.5D, 2.0D, 2.5D, 0.01D);
        }
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
