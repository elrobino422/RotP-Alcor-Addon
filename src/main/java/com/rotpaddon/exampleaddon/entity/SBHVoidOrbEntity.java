
package com.rotpaddon.exampleaddon.entity;

import com.github.standobyte.jojo.entity.damaging.projectile.ModdedProjectileEntity;
import com.rotpaddon.exampleaddon.init.InitEntities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class SBHVoidOrbEntity extends ModdedProjectileEntity {

    public SBHVoidOrbEntity(EntityType<SBHVoidOrbEntity> type, World world) {
        super(type, world);
    }

    public SBHVoidOrbEntity(LivingEntity shooter, World world) {
        super(InitEntities.SBH_VOID_ORB.get(), shooter, world);
    }

    @Override
    protected float getBaseDamage() {
        return 6.0F;
    }

    @Override
    protected float getMaxHardnessBreakable() {
        return 0.0F;
    }

    @Override
    public boolean standDamage() {
        return true;
    }

    @Override
    public int ticksLifespan() {
        return 60;
    }
}
