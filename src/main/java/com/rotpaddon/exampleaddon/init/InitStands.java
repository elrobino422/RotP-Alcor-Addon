package com.rotpaddon.exampleaddon.init;

import com.github.standobyte.jojo.action.Action;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.action.stand.StandEntityBlock;
import com.github.standobyte.jojo.action.stand.StandEntityHeavyAttack;
import com.github.standobyte.jojo.action.stand.StandEntityLightAttack;
import com.github.standobyte.jojo.action.stand.StandEntityMeleeBarrage;
import com.github.standobyte.jojo.entity.stand.StandEntityType;
import com.github.standobyte.jojo.init.power.stand.EntityStandRegistryObject;
import com.github.standobyte.jojo.init.power.stand.ModStandsInit;
import com.github.standobyte.jojo.power.impl.stand.StandInstance.StandPart;
import com.github.standobyte.jojo.power.impl.stand.stats.StandStats;
import com.github.standobyte.jojo.power.impl.stand.type.EntityStandType;
import com.github.standobyte.jojo.power.impl.stand.type.StandType;
import com.rotpaddon.exampleaddon.AddonMain;
import com.rotpaddon.exampleaddon.action.SBHStandThrowPickaxe;
import com.rotpaddon.exampleaddon.entity.SBHStandEntity;
import com.rotpaddon.exampleaddon.action.SBHVoidOrb;
import com.rotpaddon.exampleaddon.action.SBHVoidStep;
import com.rotpaddon.exampleaddon.action.SBHGravityCrush;
import com.rotpaddon.exampleaddon.action.SBHGravityWell;
import com.rotpaddon.exampleaddon.action.SBHSupermassiveBlackHole;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.Color;



import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class InitStands {
    @SuppressWarnings("unchecked")
    public static final DeferredRegister<Action<?>> ACTIONS = DeferredRegister.create(
            (Class<Action<?>>) ((Class<?>) Action.class), AddonMain.MOD_ID);
    @SuppressWarnings("unchecked")
    public static final DeferredRegister<StandType<?>> STANDS = DeferredRegister.create(
            (Class<StandType<?>>) ((Class<?>) StandType.class), AddonMain.MOD_ID);

    // ======================================== Super Massive Black Hole (SBH) ========================================

    public static final RegistryObject<StandEntityAction> SBH_PUNCH = ACTIONS.register("sbh_punch",
            () -> new StandEntityLightAttack(new StandEntityLightAttack.Builder()
                    .cooldown(4, 0)
                    .standSound(InitSounds.SBH_PUNCH_VOICE)
                    .punchSound(InitSounds.SBH_PUNCH_LIGHT)));

    public static final RegistryObject<StandEntityAction> SBH_BARRAGE = ACTIONS.register("sbh_barrage",
            () -> new StandEntityMeleeBarrage(new StandEntityMeleeBarrage.Builder()
                    .cooldown(20, 0)
                    .standSound(InitSounds.SBH_BARRAGE_VOICE)
                    .barrageHitSound(InitSounds.SBH_PUNCH_BARRAGE)));

    public static final RegistryObject<StandEntityHeavyAttack> SBH_FINISHER_PUNCH = ACTIONS.register("sbh_finisher_punch",
            () -> new StandEntityHeavyAttack(new StandEntityHeavyAttack.Builder()
                    .cooldown(40, 0)
                    .punchSound(InitSounds.SBH_PUNCH_HEAVY)
                    .standSound(InitSounds.SBH_HEAVY_PUNCH_VOICE)
                    .partsRequired(StandPart.ARMS)));

    public static final RegistryObject<StandEntityHeavyAttack> SBH_HEAVY_PUNCH = ACTIONS.register("sbh_heavy_punch",
            () -> new StandEntityHeavyAttack(new StandEntityHeavyAttack.Builder()
                    .cooldown(25, 0)
                    .shiftVariationOf(SBH_PUNCH).shiftVariationOf(SBH_BARRAGE)
                    .setFinisherVariation(SBH_FINISHER_PUNCH)
                    .punchSound(InitSounds.SBH_PUNCH_HEAVY)
                    .standSound(InitSounds.SBH_FINISHER_VOICE)
                    .partsRequired(StandPart.ARMS)));

    public static final RegistryObject<StandEntityAction> SBH_BLOCK = ACTIONS.register("sbh_block",
            () -> new StandEntityBlock());


    public static final RegistryObject<StandEntityAction> SBH_VOID_ORB =
            ACTIONS.register("sbh_void_orb",
                    () -> new SBHVoidOrb(new StandEntityAction.Builder()
                            .standPose(SBHVoidOrb.VOID_ORB_POSE)

                            // SBH moves in front of the player
                            .standOffsetFromUser(0, 1.5)

                            // Projectile fires after 0.6 seconds
                            .standWindupDuration(12)

                            // Cooldown After 3 seconds
                            .cooldown(60, 0)

                            // Animation finishes during recovery
                            .standRecoveryTicks(8)

                            // Voice begins during preparation
                            .standSound(
                                    StandEntityAction.Phase.WINDUP,
                                    InitSounds.SBH_VOID_ORB_VOICE)

                            // Existing projectile launch sound
                            .standSound(
                                    StandEntityAction.Phase.PERFORM,
                                    InitSounds.SBH_VOID_ORB)

                            .staminaCost(75)
                            .partsRequired(StandPart.ARMS)));




    // Gravity Well persists after the cast, so SBH can combo with other moves.
    public static final RegistryObject<StandEntityAction> SBH_GRAVITY_WELL =
            ACTIONS.register("sbh_gravity_well",
                    () -> new SBHGravityWell(new StandEntityAction.Builder()
                            .standUserWalkSpeed(1.0F)
                            .standWindupDuration(12)
                            .standPerformDuration(1)
                            .standRecoveryTicks(10)
                            .cooldown(300, 0)
                            .staminaCost(125)
                            .partsRequired(StandPart.ARMS)));

    public static final RegistryObject<StandEntityAction> SBH_GRAVITY_CRUSH =
            ACTIONS.register("sbh_gravity_crush",
                    () -> new SBHGravityCrush(new StandEntityAction.Builder()
                            // Shift + Gravity Well = Gravity Crush.
                            .shiftVariationOf(SBH_GRAVITY_WELL)
                            .standUserWalkSpeed(1.0F)
                            .standWindupDuration(10)
                            .standPerformDuration(80)
                            .standRecoveryTicks(10)
                            .standSound(StandEntityAction.Phase.WINDUP,
                                    InitSounds.SBH_GRAVITY_CRUSH_VOICE)
                            .standSound(StandEntityAction.Phase.PERFORM,
                                    InitSounds.SBH_GRAVITY_CRUSH)
                            .cooldown(280, 0)
                            .staminaCost(100)
                            .partsRequired(StandPart.ARMS)));

    // Ultimate: a delayed area singularity with a powerful final collapse.
    public static final RegistryObject<StandEntityAction> SBH_SUPERMASSIVE_BLACK_HOLE =
            ACTIONS.register("sbh_supermassive_black_hole",
                    () -> new SBHSupermassiveBlackHole(new StandEntityAction.Builder()
                            .standPose(SBHVoidOrb.VOID_ORB_POSE)
                            .standOffsetFromUser(0, 1.5)
                            .standUserWalkSpeed(1.0F)
                            .standWindupDuration(50)
                            .standPerformDuration(1)
                            .standRecoveryTicks(18)
                            .cooldown(1800, 0)
                            .staminaCost(350)
                            .partsRequired(StandPart.ARMS)));

    public static final RegistryObject<StandEntityAction> SBH_VOID_STEP =
            ACTIONS.register("sbh_void_step",
                    () -> new SBHVoidStep(new StandEntityAction.Builder()
                            .standRecoveryTicks(10)
                            .cooldown(160, 0)
                            .standSound(InitSounds.SBH_VOID_STEP)
                            .standSound(InitSounds.SBH_VOID_STEP_VOICE)
                            .staminaCost(100)
                            .partsRequired(StandPart.ARMS)));


    public static final EntityStandRegistryObject<EntityStandType<StandStats>, StandEntityType<SBHStandEntity>> STAND_SBH =
            new EntityStandRegistryObject<>("sbh",
                    STANDS,
                    () -> new EntityStandType.Builder<StandStats>()
                    .color(0xA600FF)
                    .storyPartName(new TranslationTextComponent("story_part.rotpalcoraddon.alcor")
                            .withStyle(style -> style.withColor(Color.fromRgb(0xB3D8E5)))
                    )
                    .leftClickHotbar(
                            SBH_PUNCH.get(),
                            SBH_BARRAGE.get()
                            )
                    // Gravity Crush is Gravity Well's Sneak variant.
                    // The ultimate remains a standalone right-click action.
                    .rightClickHotbar(
                            SBH_BLOCK.get(),
                            SBH_VOID_ORB.get(),
                            SBH_VOID_STEP.get(),
                            SBH_GRAVITY_WELL.get(),
                            SBH_SUPERMASSIVE_BLACK_HOLE.get()
                            )
                    .defaultStats(StandStats.class, new StandStats.Builder()
                            .tier(6)
                            .power(18)
                            .speed(20)
                            .range(20, 30)
                            .durability(14)
                            .precision(20)
                            .build())
                    .addSummonShout(InitSounds.SBH_VOICE_SUMMON)
                    .addOst(InitSounds.SBH_OST)
                    .build(),

                    InitEntities.ENTITIES,
                    () -> new StandEntityType<SBHStandEntity>(SBHStandEntity::new, 0.7F, 2.1F)
                    .summonSound(InitSounds.SBH_SUMMON_SOUND)
                    .unsummonSound(InitSounds.SBH_UNSUMMON_SOUND))
            .withDefaultStandAttributes();
}
