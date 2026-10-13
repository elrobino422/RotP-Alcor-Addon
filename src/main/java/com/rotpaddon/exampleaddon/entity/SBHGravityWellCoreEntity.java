package com.rotpaddon.exampleaddon.entity;

import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.util.mc.MCUtil;
import com.rotpaddon.exampleaddon.init.InitEntities;
import com.rotpaddon.exampleaddon.init.InitParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * A stationary, synced, non-colliding black hole visual which also contains
 * all the Gravity Well physics. No global world tick map is needed.
 */
public class SBHGravityWellCoreEntity extends Entity {
    public static final int LIFESPAN = 100; // 5 seconds
    public static final double PULL_RADIUS = 14.0D;
    private static final double PULL_RADIUS_SQ = PULL_RADIUS * PULL_RADIUS;

    private int ownerId = -1;
    private int lifetimeTicks = 0;

    public SBHGravityWellCoreEntity(EntityType<? extends SBHGravityWellCoreEntity> type, World world) {
        super(type, world);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public SBHGravityWellCoreEntity(World world, LivingEntity owner) {
        this(InitEntities.SBH_GRAVITY_WELL_CORE.get(), world);
        this.ownerId = owner.getId();
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT nbt) {
        this.ownerId = nbt.getInt("OwnerId");
        this.lifetimeTicks = nbt.getInt("Lifetime");
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT nbt) {
        nbt.putInt("OwnerId", ownerId);
        nbt.putInt("Lifetime", lifetimeTicks);
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
        if (++lifetimeTicks > LIFESPAN) {
            remove();
            return;
        }

        Entity e = level.getEntity(ownerId);
        if (!(e instanceof LivingEntity) || !e.isAlive()) {
            remove();
            return;
        }
        LivingEntity caster = (LivingEntity) e;

        Vector3d center = position();
        AxisAlignedBB area = new AxisAlignedBB(
                center.x - PULL_RADIUS, center.y - PULL_RADIUS, center.z - PULL_RADIUS,
                center.x + PULL_RADIUS, center.y + PULL_RADIUS, center.z + PULL_RADIUS);

        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, area)) {
            if (!canPull(caster, target)) continue;

            Vector3d offset = center.subtract(target.getBoundingBox().getCenter());
            double distanceSq = offset.lengthSqr();
            if (distanceSq > PULL_RADIUS_SQ || distanceSq < 0.0001D) continue;

            Vector3d velocity = target.getDeltaMovement();
            double distance = Math.sqrt(distanceSq);
            if (distance < 1.25D) {
                // Dampen movement at the core to stop oscillation.
                target.setDeltaMovement(velocity.scale(0.55D));
            }
            else {
                double strength = 0.08D + 0.13D * (1.0D - distance / PULL_RADIUS);
                Vector3d pull = offset.scale(strength / distance);
                target.setDeltaMovement(
                        MathHelper.clamp(velocity.x * 0.87D + pull.x, -0.5D, 0.5D),
                        MathHelper.clamp(velocity.y * 0.88D + pull.y, -0.36D, 0.36D),
                        MathHelper.clamp(velocity.z * 0.87D + pull.z, -0.5D, 0.5D));
            }
            target.hasImpulse = true;
            target.hurtMarked = true;
        }

        // All motion and particles are server-authoritative, so multiplayer
        // clients see identical targeting, positioning, and visual timing.
        if (level instanceof ServerWorld) {
            spawnVortexParticles((ServerWorld) level, center);
        }
    }

    private boolean canPull(LivingEntity caster, LivingEntity target) {
        if (!target.isAlive() || target.isSpectator()
                || target == caster || target instanceof StandEntity
                || !MCUtil.canHarm(caster, target)) {
            return false;
        }
        if (target instanceof TameableEntity
                && ((TameableEntity) target).getOwner() == caster) {
            return false;
        }
        return true;
    }

    private void spawnVortexParticles(ServerWorld world, Vector3d center) {
        // Six bright custom spiraling particles orbit toward the center.
        if (lifetimeTicks % 2 == 0) {
            for (int i = 0; i < 6; i++) {
                double theta = lifetimeTicks * 0.19D + i * Math.PI / 3.0D;
                double radius = 2.0D + (10.0D -
                        ((lifetimeTicks * 0.16D + i * 1.7D) % 10.0D));
                double x = center.x + Math.cos(theta) * radius;
                double y = center.y + Math.sin(theta * 1.7D) * 1.4D;
                double z = center.z + Math.sin(theta) * radius;

                // Zero count tells vanilla to use these motion components.
                Vector3d inward = center.subtract(x, y, z).normalize().scale(0.13D);
                world.sendParticles(InitParticles.GRAVITY_WELL_SPARK.get(),
                        x, y, z, 0, inward.x, inward.y, inward.z, 1.0D);
            }
        }

        if (lifetimeTicks % 4 == 0) {
            for (int i = 0; i < 4; i++) {
                double angle = lifetimeTicks * 0.13D + i * Math.PI / 2.0D;
                world.sendParticles(ParticleTypes.PORTAL,
                        center.x + Math.cos(angle) * 2.0D,
                        center.y + Math.sin(angle * 1.3D) * 1.4D,
                        center.z + Math.sin(angle) * 2.0D,
                        1, 0.05D, 0.12D, 0.05D, 0.04D);
            }
            world.sendParticles(ParticleTypes.LARGE_SMOKE,
                    center.x, center.y, center.z,
                    1, 0.35D, 0.35D, 0.35D, 0.005D);
        }
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
