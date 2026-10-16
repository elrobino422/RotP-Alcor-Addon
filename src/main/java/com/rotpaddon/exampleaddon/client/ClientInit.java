package com.rotpaddon.exampleaddon.client;

import com.rotpaddon.exampleaddon.AddonMain;
import com.rotpaddon.exampleaddon.client.render.ExamplePickaxeRenderer;
import com.rotpaddon.exampleaddon.client.render.SBHStandRenderer;
import com.rotpaddon.exampleaddon.client.render.SBHGravityWellCoreRenderer;
import com.rotpaddon.exampleaddon.client.render.SBHSupermassiveBlackHoleRenderer;
import com.rotpaddon.exampleaddon.client.particle.SBHGravityWellParticle;
import com.rotpaddon.exampleaddon.init.InitEntities;
import com.rotpaddon.exampleaddon.init.InitParticles;
import com.rotpaddon.exampleaddon.init.InitStands;
import com.rotpaddon.exampleaddon.client.render.BlueNightStandRenderer;
import com.rotpaddon.exampleaddon.client.render.BlueNightRingLayer;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import com.rotpaddon.exampleaddon.client.render.SBHVoidOrbRenderer;

@EventBusSubscriber(modid = AddonMain.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientInit {
    
    @SubscribeEvent
    public static void onFMLClientSetup(FMLClientSetupEvent event) {
        AddonMain.LOGGER.info("SBH CHECK: CLIENT SETUP EXECUTED");
        RenderingRegistry.registerEntityRenderingHandler(
                InitStands.STAND_SBH.getEntityType(), SBHStandRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(
                InitEntities.EXAMPLE_PICKAXE.get(), manager -> new ExamplePickaxeRenderer(manager));
        RenderingRegistry.registerEntityRenderingHandler(
                InitEntities.SBH_VOID_ORB.get(),
                SBHVoidOrbRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(
                InitEntities.SBH_GRAVITY_WELL_CORE.get(),
                SBHGravityWellCoreRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(
                InitEntities.SBH_SUPERMASSIVE_BLACK_HOLE.get(),
                SBHSupermassiveBlackHoleRenderer::new);

        RenderingRegistry.registerEntityRenderingHandler(
                InitStands.STAND_BLUE_NIGHT.getEntityType(),
                BlueNightStandRenderer::new);
        event.enqueueWork(() -> {
            Minecraft.getInstance()
                    .getEntityRenderDispatcher()
                    .getSkinMap()
                    .forEach((skinType, renderer) -> {
                        renderer.addLayer(
                                new BlueNightRingLayer(
                                        renderer,
                                        "slim".equals(skinType)
                                )
                        );
                    });
        });
    }
    @SubscribeEvent
    public static void registerGravityWellParticles(ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particleEngine.register(
                InitParticles.GRAVITY_WELL_SPARK.get(),
                SBHGravityWellParticle.Factory::new);
    }
}

