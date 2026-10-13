package com.rotpaddon.exampleaddon.init;

import java.util.function.Supplier;

import com.github.standobyte.jojo.init.ModSounds;
import com.github.standobyte.jojo.util.mc.OstSoundList;
import com.rotpaddon.exampleaddon.AddonMain;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class InitSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(
            ForgeRegistries.SOUND_EVENTS, AddonMain.MOD_ID);

    public static final RegistryObject<SoundEvent> SBH_VOICE_SUMMON = SOUNDS.register(
            "sbh_stand_summon_voiceline",
            () -> new SoundEvent(new ResourceLocation(AddonMain.MOD_ID, "sbh_stand_summon_voiceline")));

    public static final Supplier<SoundEvent> SBH_SUMMON_SOUND = ModSounds.STAND_SUMMON_DEFAULT;

    public static final Supplier<SoundEvent> SBH_UNSUMMON_SOUND = ModSounds.STAND_UNSUMMON_DEFAULT;

    public static final Supplier<SoundEvent> SBH_PUNCH_LIGHT = ModSounds.STAND_PUNCH_LIGHT;

    public static final Supplier<SoundEvent> SBH_PUNCH_HEAVY = ModSounds.STAND_PUNCH_HEAVY;

    public static final Supplier<SoundEvent> SBH_PUNCH_BARRAGE = ModSounds.STAND_PUNCH_LIGHT;

    public static final Supplier<SoundEvent> SBH_THROW_PICKAXE = ModSounds.STAND_PUNCH_LIGHT;

    public static final OstSoundList SBH_OST = new OstSoundList(
            new ResourceLocation(AddonMain.MOD_ID, "sbh_stand_ost"), SOUNDS);


    public static final RegistryObject<SoundEvent> SBH_TRACK_1 =
            SOUNDS.register("sbh_track_1",
                    () -> new SoundEvent(new ResourceLocation(
                            AddonMain.MOD_ID, "sbh_track_1")));

    public static final RegistryObject<SoundEvent> SBH_TRACK_2 =
            SOUNDS.register("sbh_track_2",
                    () -> new SoundEvent(new ResourceLocation(
                            AddonMain.MOD_ID, "sbh_track_2")));

    public static final RegistryObject<SoundEvent> SBH_TRACK_3 =
            SOUNDS.register("sbh_track_3",
                    () -> new SoundEvent(new ResourceLocation(
                            AddonMain.MOD_ID, "sbh_track_3")));

    public static final RegistryObject<SoundEvent> SBH_TRACK_4 =
            SOUNDS.register("sbh_track_4",
                    () -> new SoundEvent(new ResourceLocation(
                            AddonMain.MOD_ID, "sbh_track_4")));

    public static final RegistryObject<SoundEvent> SBH_VOID_ORB =
            SOUNDS.register("sbh_void_orb",
                    () -> new SoundEvent(
                            new ResourceLocation(
                                    AddonMain.MOD_ID,
                                    "sbh_void_orb"
                            )));

    public static final RegistryObject<SoundEvent> SBH_VOID_STEP =
            SOUNDS.register("sbh_void_step",
                    () -> new SoundEvent(
                            new ResourceLocation(
                                    AddonMain.MOD_ID,
                                    "sbh_void_step")));


    public static final RegistryObject<SoundEvent> SBH_VOID_ORB_VOICE =
            SOUNDS.register("sbh_void_orb_voice",
                    () -> new SoundEvent(
                            new ResourceLocation(
                                    AddonMain.MOD_ID,
                                    "sbh_void_orb_voice"
                            )));

    public static final RegistryObject<SoundEvent> SBH_PUNCH = SOUNDS.register(
            "sbh_punch",
            () -> new SoundEvent(new ResourceLocation(AddonMain.MOD_ID, "sbh_punch")));

    public static final RegistryObject<SoundEvent> SBH_BARRAGE = SOUNDS.register(
            "sbh_barrage",
            () -> new SoundEvent(new ResourceLocation(AddonMain.MOD_ID, "sbh_barrage")));

    public static final RegistryObject<SoundEvent> SBH_HEAVY_PUNCH = SOUNDS.register(
            "sbh_heavy_punch",
            () -> new SoundEvent(new ResourceLocation(AddonMain.MOD_ID, "sbh_heavy_punch")));

    public static final RegistryObject<SoundEvent> SBH_FINISHER = SOUNDS.register(
            "sbh_finisher",
            () -> new SoundEvent(new ResourceLocation(AddonMain.MOD_ID, "sbh_finisher")));

    public static final RegistryObject<SoundEvent> SBH_VOID_ORB_VOICE = SOUNDS.register(
            "sbh_void_orb_voice",
            () -> new SoundEvent(new ResourceLocation(AddonMain.MOD_ID, "sbh_void_orb_voice")));

    public static final RegistryObject<SoundEvent> SBH_VOID_STEP_VOICELINE = SOUNDS.register(
            "sbh_void_step_voiceline",
            () -> new SoundEvent(new ResourceLocation(AddonMain.MOD_ID, "sbh_void_step_voiceline")));


}
