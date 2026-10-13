package com.rotpaddon.exampleaddon.init;

import com.rotpaddon.exampleaddon.AddonMain;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class InitParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, AddonMain.MOD_ID);

    public static final RegistryObject<BasicParticleType> GRAVITY_WELL_SPARK =
            PARTICLES.register("gravity_well_spark", () -> new BasicParticleType(false));
}
