package com.rotpaddon.exampleaddon.item;

import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.rotpaddon.exampleaddon.form.BlueNightEquipmentManager;
import com.rotpaddon.exampleaddon.form.BlueNightInsomnia;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.SwordItem;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

/**
 * Moon Reaper — Blue Night's manifested Scythe form.
 *
 * Regular attack: normal sword swing; successful hits grant 1 Insomnia.
 * Right click: Reap of Insomnia, a short forward sweeping attack.
 *
 * Balance constants are deliberately grouped here for easy iteration.
 * All combat/damage/resource logic runs on the logical server.
 */
public class BlueNightScytheItem extends SwordItem {
    private static final double REAP_REACH = 4.5D;
    private static final double REAP_HALF_ANGLE_COS = 0.42D;
    private static final float REAP_DAMAGE = 6.0F;
    private static final int REAP_COOLDOWN_TICKS = 100; // 5 seconds
    private static final int REAP_SLOWNESS_TICKS = 60;  // 3 seconds
    private static final int INSOMNIA_PER_MELEE_HIT = 1;
    private static final int INSOMNIA_PER_REAP_HIT = 3;

    public BlueNightScytheItem(Properties properties) {
        // Iron-based item attributes; tune damage/speed after in-game tests.
        super(ItemTier.IRON, 3, -2.7F, properties);
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack stack, PlayerEntity player) {
        // Keep the existing behavior: temporary Stand weapons cannot be dropped.
        return !BlueNightEquipmentManager.isManifestedScythe(stack);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!BlueNightEquipmentManager.isManifestedScythe(stack)) {
            return super.hurtEnemy(stack, target, attacker);
        }

        // Manifested weapons do not lose durability. Only the actual active
        // Blue Night user can earn Insomnia; copied /give items cannot.
        if (!attacker.level.isClientSide()
                && attacker instanceof ServerPlayerEntity
                && !(target instanceof StandEntity)
                && BlueNightEquipmentManager.isScytheActive((PlayerEntity) attacker)) {
            BlueNightInsomnia.add((ServerPlayerEntity) attacker, INSOMNIA_PER_MELEE_HIT);
        }
        return true;
    }

    @Override
    public boolean mineBlock(ItemStack stack, World world,
                             BlockState state, BlockPos pos, LivingEntity miner) {
        if (BlueNightEquipmentManager.isManifestedScythe(stack)) {
            return true; // A Stand manifestation cannot be consumed by mining.
        }
        return super.mineBlock(stack, world, state, pos, miner);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand != Hand.MAIN_HAND
                || !BlueNightEquipmentManager.isManifestedScythe(stack)
                || !BlueNightEquipmentManager.isScytheActive(player)) {
            return ActionResult.pass(stack);
        }

        if (player.getCooldowns().isOnCooldown(this)) {
            return ActionResult.fail(stack);
        }

        if (!world.isClientSide() && player instanceof ServerPlayerEntity) {
            performReap((ServerWorld) world, (ServerPlayerEntity) player);
            player.getCooldowns().addCooldown(this, REAP_COOLDOWN_TICKS);
        }

        return ActionResult.sidedSuccess(stack, world.isClientSide());
    }

    private void performReap(ServerWorld world, ServerPlayerEntity player) {
        Vector3d look = player.getLookAngle();
        double horizontalLookLength = Math.sqrt(look.x * look.x + look.z * look.z);
        int hitCount = 0;

        if (horizontalLookLength > 0.0001D) {
            for (LivingEntity target : world.getEntitiesOfClass(
                    LivingEntity.class, player.getBoundingBox().inflate(REAP_REACH),
                    entity -> entity.isAlive() && entity != player
                            && !(entity instanceof StandEntity)
                            && !player.isAlliedTo(entity))) {

                double dx = target.getX() - player.getX();
                double dz = target.getZ() - player.getZ();
                double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
                if (horizontalDistance < 0.01D
                        || horizontalDistance > REAP_REACH
                        || player.distanceToSqr(target) > REAP_REACH * REAP_REACH) {
                    continue;
                }

                // Only targets inside the frontal arc are hit.
                double dot = (dx * look.x + dz * look.z)
                        / (horizontalDistance * horizontalLookLength);
                if (dot < REAP_HALF_ANGLE_COS) {
                    continue;
                }

                if (target.hurt(DamageSource.playerAttack(player), REAP_DAMAGE)) {
                    target.addEffect(new EffectInstance(
                            Effects.MOVEMENT_SLOWDOWN, REAP_SLOWNESS_TICKS, 0));
                    world.sendParticles(ParticleTypes.SWEEP_ATTACK,
                            target.getX(), target.getY() + target.getBbHeight() * 0.55D,
                            target.getZ(), 1, 0.1D, 0.1D, 0.1D, 0.0D);
                    hitCount++;
                }
            }
        }

        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0F, 0.65F);

        if (hitCount > 0) {
            BlueNightInsomnia.add(player, hitCount * INSOMNIA_PER_REAP_HIT);
        }
        else {
            player.displayClientMessage(
                    new net.minecraft.util.text.StringTextComponent("Reap of Insomnia - missed"), true);
        }
    }
}
