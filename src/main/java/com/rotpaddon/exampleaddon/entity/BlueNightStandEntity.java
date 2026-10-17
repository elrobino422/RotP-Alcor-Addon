package com.rotpaddon.exampleaddon.entity;

import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityType;
import com.rotpaddon.exampleaddon.form.IAlcorFormHolder;

import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

/** Shared-form system: 0 Bracelet, 1 Scythe, others reserved. */
public class BlueNightStandEntity extends StandEntity implements IAlcorFormHolder {

    private static final DataParameter<Integer> ALCOR_FORM =
            EntityDataManager.defineId(BlueNightStandEntity.class, DataSerializers.INT);

    public BlueNightStandEntity(StandEntityType<BlueNightStandEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ALCOR_FORM, 0);
    }

    @Override
    public int alcorGetForm() {
        return entityData.get(ALCOR_FORM);
    }

    @Override
    public void alcorSetForm(int formId) {
        if (!level.isClientSide()) {
            entityData.set(ALCOR_FORM, formId);
        }
    }
}
