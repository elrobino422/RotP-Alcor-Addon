package com.rotpaddon.exampleaddon.form;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import com.rotpaddon.exampleaddon.network.AlcorNetwork;

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
}
