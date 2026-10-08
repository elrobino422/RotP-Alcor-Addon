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
                    .punchSound(InitSounds.SBH_PUNCH_LIGHT)));

    public static final RegistryObject<StandEntityAction> SBH_BARRAGE = ACTIONS.register("sbh_barrage",
            () -> new StandEntityMeleeBarrage(new StandEntityMeleeBarrage.Builder()
                    .barrageHitSound(InitSounds.SBH_PUNCH_BARRAGE)));

    public static final RegistryObject<StandEntityHeavyAttack> SBH_FINISHER_PUNCH = ACTIONS.register("sbh_finisher_punch",
            () -> new StandEntityHeavyAttack(new StandEntityHeavyAttack.Builder()
                    .punchSound(InitSounds.SBH_PUNCH_HEAVY)
                    .partsRequired(StandPart.ARMS)));

    public static final RegistryObject<StandEntityHeavyAttack> SBH_HEAVY_PUNCH = ACTIONS.register("sbh_heavy_punch",
            () -> new StandEntityHeavyAttack(new StandEntityHeavyAttack.Builder()
                    .shiftVariationOf(SBH_PUNCH).shiftVariationOf(SBH_BARRAGE)
                    .setFinisherVariation(SBH_FINISHER_PUNCH)
                    .punchSound(InitSounds.SBH_PUNCH_HEAVY)
                    .partsRequired(StandPart.ARMS)));

    public static final RegistryObject<StandEntityAction> SBH_BLOCK = ACTIONS.register("sbh_block",
            () -> new StandEntityBlock());

    public static final RegistryObject<StandEntityAction> SBH_THROW_PICKAXE = ACTIONS.register("sbh_throw_pickaxe",
            () -> new SBHStandThrowPickaxe(new StandEntityAction.Builder()
                    .standPose(SBHStandThrowPickaxe.PICKAXE_THROW_ANIM)
                    .holdToFire(20, true)
                    .standRecoveryTicks(20)
                    .standSound(InitSounds.SBH_THROW_PICKAXE)
                    .staminaCost(75)
                    .partsRequired(StandPart.ARMS)));

    public static final EntityStandRegistryObject<EntityStandType<StandStats>, StandEntityType<SBHStandEntity>> STAND_SBH =
            new EntityStandRegistryObject<>("sbh",
                    STANDS,
                    () -> new EntityStandType.Builder<StandStats>()
                    .color(0xA600FF)
                    .storyPartName(ModStandsInit.PART_3_NAME)
                    .leftClickHotbar(
                            SBH_PUNCH.get(),
                            SBH_BARRAGE.get()
                            )
                    .rightClickHotbar(
                            SBH_BLOCK.get(),
                            SBH_THROW_PICKAXE.get()
                            )
                    .defaultStats(StandStats.class, new StandStats.Builder()
                            .tier(6)
                            .power(18)
                            .speed(20)
                            .range(80, 120)
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
