
package com.rotpaddon.exampleaddon.action;

import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class SBHVoidStep extends StandEntityAction {

    public SBHVoidStep(StandEntityAction.Builder builder) {
        super(builder);
    }

    @Override
    public void standPerform(World world, StandEntity standEntity,
                             IStandPower userPower, StandEntityTask task) {

        if (world.isClientSide()) {
            return;
        }

        LivingEntity user = userPower.getUser();

        if (!(user instanceof ServerPlayerEntity)) {
            return;
        }

        ServerPlayerEntity player = (ServerPlayerEntity) user;

        Vector3d start = player.getEyePosition(1.0F);
        Vector3d look = player.getLookAngle();
        Vector3d end = start.add(look.scale(32.0D));

        BlockRayTraceResult hit = world.clip(new RayTraceContext(
                start,
                end,
                RayTraceContext.BlockMode.COLLIDER,
                RayTraceContext.FluidMode.NONE,
                player
        ));

        if (hit.getType() != RayTraceResult.Type.BLOCK) {
            return;
        }

        BlockPos targetBlock = hit.getBlockPos();

        // Start with the space immediately above the target block.
        BlockPos destination = targetBlock.above();

        // Search upward for a safe place to stand.
        for (int i = 0; i < 3; i++) {

            BlockPos feet = destination.above(i);
            BlockPos head = feet.above();

            boolean feetClear = world.getBlockState(feet)
                    .getCollisionShape(world, feet).isEmpty();

            boolean headClear = world.getBlockState(head)
                    .getCollisionShape(world, head).isEmpty();

            if (feetClear && headClear) {

                double x = feet.getX() + 0.5D;
                double y = feet.getY();
                double z = feet.getZ() + 0.5D;

                player.teleportTo(x, y, z);

                player.fallDistance = 0;

                return;
            }
        }
    }
}
