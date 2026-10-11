
package com.rotpaddon.exampleaddon.action;

import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.rotpaddon.exampleaddon.entity.SBHVoidOrbEntity;

import net.minecraft.world.World;

public class SBHVoidOrb extends StandEntityAction {

    public SBHVoidOrb(StandEntityAction.Builder builder) {
        super(builder);
    }

    @Override
    public void standPerform(World world, StandEntity standEntity,
                             IStandPower userPower, StandEntityTask task) {

        if (!world.isClientSide()) {
            SBHVoidOrbEntity orb = new SBHVoidOrbEntity(standEntity, world);
            standEntity.shootProjectile(orb, 1.5F, 1.0F);
        }
    }
}
