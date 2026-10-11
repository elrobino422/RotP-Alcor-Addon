package com.rotpaddon.exampleaddon.init;

import com.rotpaddon.exampleaddon.AddonMain;
import com.rotpaddon.exampleaddon.entity.ExamplePickaxeEntity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import com.rotpaddon.exampleaddon.entity.SBHVoidOrbEntity;

public class InitEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(
            ForgeRegistries.ENTITIES, AddonMain.MOD_ID);
    
    
    
    public static final RegistryObject<EntityType<ExamplePickaxeEntity>> EXAMPLE_PICKAXE = ENTITIES.register("example_pickaxe", 
            () -> EntityType.Builder.<ExamplePickaxeEntity>of(ExamplePickaxeEntity::new, EntityClassification.MISC)
            .sized(1.0F, 1.0F)
            .build(AddonMain.MOD_ID + ":example_pickaxe"));

    public static final RegistryObject<EntityType<SBHVoidOrbEntity>> SBH_VOID_ORB =
            ENTITIES.register("sbh_void_orb",
                    () -> EntityType.Builder
                            .<SBHVoidOrbEntity>of(
                                    SBHVoidOrbEntity::new,
                                    EntityClassification.MISC)
                            .sized(0.6F, 0.6F)
                            .build(AddonMain.MOD_ID + ":sbh_void_orb"));
};
