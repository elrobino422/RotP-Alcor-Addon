package com.rotpaddon.exampleaddon.action;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.rotpaddon.exampleaddon.form.BlueNightEquipmentManager;
import com.rotpaddon.exampleaddon.form.BlueNightInsomnia;
import com.rotpaddon.exampleaddon.effect.BlueNightReapFx;
import com.rotpaddon.exampleaddon.init.InitSounds;
import com.rotpaddon.exampleaddon.client.gui.BlueNightInsomniaHud;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

/**
 * RotP right-click hotbar ability for Blue Night's Moon Reaper.
 *
 * Unlike a normal item's right-click behavior, this skill can only be cast
 * if Blue Night is active, its form is Scythe and its *manifested* weapon is
 * actually held in the main hand. RotP owns the skill cooldown.
 */
public class BlueNightReapOfInsomnia extends StandAction {
    private static final double REAP_REACH = 4.5D;
    private static final double REAP_HALF_ANGLE_COS = 0.42D;
    private static final float REAP_DAMAGE = 6.0F;
    private static final int REAP_SLOWNESS_TICKS = 60;
    public static final int INSOMNIA_COST = 10; // Prototype cost, adjust after testing.

    public BlueNightReapOfInsomnia(StandAction.Builder builder) {
        super(builder);
    }

    private static boolean hasManifestedScythe(LivingEntity user) {
        return user instanceof PlayerEntity
                && BlueNightEquipmentManager.isScytheActive((PlayerEntity) user)
                && BlueNightEquipmentManager.isManifestedScythe(
                        ((PlayerEntity) user).getMainHandItem());
    }

    @Override
    protected ActionConditionResult checkSpecificConditions(
            LivingEntity user, IStandPower power, ActionTarget target) {
        if (!power.isActive() || !hasManifestedScythe(user)) {
            return ActionConditionResult.createNegative(
                    new TranslationTextComponent(
                            "message.rotpalcoraddon.blue_night.requires_moon_reaper"));
        }
        boolean canAfford;
        if (user.level.isClientSide()) {
            // Client prediction uses the synced HUD value, not unsynced
            // player NBT. The server independently verifies the cost.
            Boolean clientHasEnough = DistExecutor.unsafeCallWhenOn(
                    Dist.CLIENT,
                    () -> () -> BlueNightInsomniaHud.hasAtLeast(INSOMNIA_COST));
            canAfford = Boolean.TRUE.equals(clientHasEnough);
        }
        else {
            canAfford = user instanceof PlayerEntity
                    && BlueNightInsomnia.get((PlayerEntity) user) >= INSOMNIA_COST;
        }

        if (!canAfford) {
            return ActionConditionResult.createNegative(
                    new TranslationTextComponent(
                            "message.rotpalcoraddon.blue_night.insufficient_insomnia",
                            INSOMNIA_COST));
        }
        return ActionConditionResult.POSITIVE;
    }

    @Override
    protected void perform(World world, LivingEntity user,
                           IStandPower power, ActionTarget target) {
        // Client cannot cause damage. Re-check on server for forged inputs.
        if (!(world instanceof ServerWorld)
                || !(user instanceof ServerPlayerEntity)
                || !power.isActive()
                || !hasManifestedScythe(user)) {
            return;
        }

        ServerWorld serverWorld = (ServerWorld) world;
        ServerPlayerEntity player = (ServerPlayerEntity) user;

        // Spend exactly once per successful cast, even if the sweep misses.
        // If fewer than 10 stacks remain, do not play FX, sound or damage.
        if (!BlueNightInsomnia.trySpend(player, INSOMNIA_COST)) {
            player.displayClientMessage(
                    new TranslationTextComponent(
                            "message.rotpalcoraddon.blue_night.insufficient_insomnia",
                            INSOMNIA_COST), true);
            return;
        }

        // Plays Minecraft's synced main-hand weapon swing for the wielder and
        // all other nearby players. The separate arc is animated over 8 ticks.
        player.swing(Hand.MAIN_HAND);
        BlueNightReapFx.start(serverWorld, player);

        Vector3d look = player.getLookAngle();
        double horizontalLookLength = Math.sqrt(look.x * look.x + look.z * look.z);
        int hitCount = 0;

        if (horizontalLookLength > 0.0001D) {
            for (LivingEntity enemy : serverWorld.getEntitiesOfClass(
                    LivingEntity.class, player.getBoundingBox().inflate(REAP_REACH),
                    entity -> entity.isAlive()
                            && entity != player
                            && !(entity instanceof StandEntity)
                            && !player.isAlliedTo(entity))) {

                double dx = enemy.getX() - player.getX();
                double dz = enemy.getZ() - player.getZ();
                double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

                if (horizontalDistance < 0.01D
                        || horizontalDistance > REAP_REACH
                        || player.distanceToSqr(enemy) > REAP_REACH * REAP_REACH) {
                    continue;
                }

                double dot = (dx * look.x + dz * look.z)
                        / (horizontalDistance * horizontalLookLength);
                if (dot < REAP_HALF_ANGLE_COS) {
                    continue;
                }

                if (enemy.hurt(DamageSource.playerAttack(player), REAP_DAMAGE)) {
                    enemy.addEffect(new EffectInstance(
                            Effects.MOVEMENT_SLOWDOWN, REAP_SLOWNESS_TICKS, 0));
                    serverWorld.sendParticles(
                            ParticleTypes.SWEEP_ATTACK,
                            enemy.getX(), enemy.getY() + enemy.getBbHeight() * 0.55D,
                            enemy.getZ(), 1, 0.1D, 0.1D, 0.1D, 0.0D);
                    hitCount++;
                }
            }
        }

        serverWorld.playSound(
                null, player.getX(), player.getY(), player.getZ(),
                InitSounds.BLUE_NIGHT_REAP_OF_INSOMNIA_SFX.get(), SoundCategory.PLAYERS,
                1.0F, 1.0F);

        // Special attacks now consume Insomnia instead of replenishing it.
        // Regular Moon Reaper hits remain the source of new stacks.
        if (hitCount == 0) {
            player.displayClientMessage(
                    new TranslationTextComponent(
                            "message.rotpalcoraddon.blue_night.reap_missed"), true);
        }
    }
}
