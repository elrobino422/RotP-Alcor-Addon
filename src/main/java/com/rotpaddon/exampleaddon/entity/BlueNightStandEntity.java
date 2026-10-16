package com.rotpaddon.exampleaddon.entity;

import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityType;
import net.minecraft.world.World;

/** Temporary Stand entity. Later we will render the ring on the player. */
public class BlueNightStandEntity extends StandEntity {
    public BlueNightStandEntity(StandEntityType<BlueNightStandEntity> type, World world) {
        super(type, world);
    }
}
