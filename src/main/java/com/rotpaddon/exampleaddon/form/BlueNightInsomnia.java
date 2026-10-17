package com.rotpaddon.exampleaddon.form;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import com.rotpaddon.exampleaddon.network.AlcorNetwork;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.rotpaddon.exampleaddon.init.InitStands;

/**
 * Small server-side resource store shared by Blue Night's weapon forms.
 * Numbers are prototype balance values, not final Stand design decisions.
 */
public final class BlueNightInsomnia {
    private static final String NBT_KEY = "AlcorBlueNightInsomnia";
    public static final int MAX_STACKS = 100;

    private BlueNightInsomnia() {}

    public static int get(PlayerEntity player) {
        int saved = player.getPersistentData().getInt(NBT_KEY);
        return Math.max(0, Math.min(MAX_STACKS, saved));
    }

    /**
     * Grant Insomnia only from verified server-side Blue Night combat.
     * The source of truth is the player's server NBT, not a client packet.
     */
    public static int add(ServerPlayerEntity player, int amount) {
        if (amount <= 0 || !BlueNightEquipmentManager.isScytheActive(player)) {
            return get(player);
        }
        int previous = get(player);
        int next = Math.min(MAX_STACKS, previous + amount);
        if (next != previous) {
            player.getPersistentData().putInt(NBT_KEY, next);
            // Keep the HUD in sync immediately; no repeated actionbar spam.
            AlcorNetwork.syncInsomnia(player, next);
        }
        return next;
    }

    /**
     * Spend a fixed number of stacks for a Blue Night Stand skill.
     *
     * Returns false without changing data if the player cannot afford it.
     * This is server-authoritative and reusable for the later weapon forms.
     * Individual abilities still check their own form and held weapon.
     */
    public static boolean trySpend(ServerPlayerEntity player, int amount) {
        if (amount <= 0 || player.level.isClientSide()) {
            return false;
        }

        boolean blueNightActive = IStandPower.getStandPowerOptional(player)
                .map(power -> power.isActive()
                        && power.getType() == InitStands.STAND_BLUE_NIGHT.getStandType())
                .orElse(false);
        if (!blueNightActive) {
            return false;
        }

        int available = get(player);
        if (available < amount) {
            return false;
        }

        int remaining = available - amount;
        player.getPersistentData().putInt(NBT_KEY, remaining);
        AlcorNetwork.syncInsomnia(player, remaining);
        return true;
    }
}
