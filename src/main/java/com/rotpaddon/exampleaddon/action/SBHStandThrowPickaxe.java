package com.rotpaddon.exampleaddon.action;

import javax.annotation.Nullable;

import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.entity.stand.StandPose;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.rotpaddon.exampleaddon.entity.ExamplePickaxeEntity;
import com.rotpaddon.exampleaddon.entity.SBHStandEntity;

import net.minecraft.world.World;

public class SBHStandThrowPickaxe extends StandEntityAction {
    public static final StandPose PICKAXE_THROW_ANIM = new StandPose("pickaxeThrow");

    public SBHStandThrowPickaxe(StandEntityAction.Builder builder) {
        super(builder);
    }

    @Override
    public void standPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        if (!world.isClientSide()) {
            ExamplePickaxeEntity pickaxe = new ExamplePickaxeEntity(standEntity, world);
            standEntity.shootProjectile(pickaxe, 1.5F, 1.0F);
        }
    }

    @Override
    public void onTaskSet(World world, StandEntity standEntity, IStandPower standPower,
            Phase phase, StandEntityTask task, int ticks) {
        if (!world.isClientSide() && standEntity instanceof SBHStandEntity) {
            ((SBHStandEntity) standEntity).setHasPickaxe(true);
        }
    }

    @Override
    public void phaseTransition(World world, StandEntity standEntity, IStandPower standPower,
            @Nullable Phase from, @Nullable Phase to, StandEntityTask task, int nextPhaseTicks) {
        if (!world.isClientSide() && to == Phase.PERFORM && standEntity instanceof SBHStandEntity) {
            ((SBHStandEntity) standEntity).setHasPickaxe(false);
        }
    }
}
