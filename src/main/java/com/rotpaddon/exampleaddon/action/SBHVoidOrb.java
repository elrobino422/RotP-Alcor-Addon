
package com.rotpaddon.exampleaddon.action;

import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandPose;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.rotpaddon.exampleaddon.entity.SBHVoidOrbEntity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import com.github.standobyte.jojo.entity.stand.StandPose;

public class SBHVoidOrb extends StandEntityAction {

    public SBHVoidOrb(StandEntityAction.Builder builder) {
        super(builder);
    }

    public static final StandPose VOID_ORB_POSE =
            new StandPose("voidOrb");

    @Override
    public void standPerform(World world, StandEntity standEntity,
                             IStandPower userPower, StandEntityTask task) {

        if (world.isClientSide()) {
            return;
        }

        LivingEntity user = userPower.getUser();
        if (user == null) {
            return;
        }

        // Player eye position and look direction
        Vector3d eyePos = user.getEyePosition(1.0F);
        Vector3d look = user.getLookAngle();

        double maxRange = 64.0D;
        Vector3d endPos = eyePos.add(look.scale(maxRange));

        // Ray trace blocks from the player's eyes
        RayTraceResult blockHit = world.clip(new RayTraceContext(
                eyePos,
                endPos,
                RayTraceContext.BlockMode.COLLIDER,
                RayTraceContext.FluidMode.NONE,
                user
        ));

        Vector3d targetPos = blockHit.getLocation();

        // Spawn the projectile using RotP's normal system
        SBHVoidOrbEntity orb = new SBHVoidOrbEntity(standEntity, world);

        // Correct direction from projectile to crosshair target
        Vector3d spawnPos = standEntity.getEyePosition(1.0F);
        Vector3d direction = targetPos.subtract(spawnPos).normalize();

        // Launch the orb
        orb.shoot(direction.x, direction.y, direction.z, 1.5F, 0.0F);

        world.addFreshEntity(orb);
    }
}
