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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Moon Reaper's basic physical item behavior.
 *
 * Reap of Insomnia is now a separate RotP Stand hotbar skill
 * (BlueNightReapOfInsomnia), not a right-click Item.use() ability.
 */
public class BlueNightScytheItem extends SwordItem {
    private static final int INSOMNIA_PER_MELEE_HIT = 1;

    public BlueNightScytheItem(Properties properties) {
        super(ItemTier.IRON, 3, -2.7F, properties);
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack stack, PlayerEntity player) {
        // Stand-created copies cannot be dropped, ordinary /give copies can.
        return !BlueNightEquipmentManager.isManifestedScythe(stack);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!BlueNightEquipmentManager.isManifestedScythe(stack)) {
            return super.hurtEnemy(stack, target, attacker);
        }

        // Manifested weapons do not lose durability.
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
            return true;
        }
        return super.mineBlock(stack, world, state, pos, miner);
    }
}
