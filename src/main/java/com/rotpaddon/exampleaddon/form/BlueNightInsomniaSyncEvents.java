package com.rotpaddon.exampleaddon.form;

import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.rotpaddon.exampleaddon.AddonMain;
import com.rotpaddon.exampleaddon.init.InitStands;
import com.rotpaddon.exampleaddon.network.AlcorNetwork;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Ensures the HUD gets the right number after login, respawn, Stand summon,
 * form changes, and even if the client missed the first update packet.
 *
 * This does not change Insomnia gameplay: the server's existing NBT is still
 * the sole source of truth. The HUD merely displays a synchronized copy.
 */
@Mod.EventBusSubscriber(modid = AddonMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class BlueNightInsomniaSyncEvents {
    private static final Map<UUID, Integer> LAST_SENT = new HashMap<>();
    private static final int HEARTBEAT_TICKS = 100; // every five seconds

    private BlueNightInsomniaSyncEvents() {}

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END
                || !(event.player instanceof ServerPlayerEntity)
                || event.player.level.isClientSide()) {
            return;
        }

        ServerPlayerEntity player = (ServerPlayerEntity) event.player;
        UUID id = player.getUUID();

        boolean activeBlueNight = IStandPower.getStandPowerOptional(player)
                .map(power -> power.isActive()
                        && power.getType() == InitStands.STAND_BLUE_NIGHT.getStandType())
                .orElse(false);

        if (!activeBlueNight) {
            // Re-synchronize on the next summon, even if the value is unchanged.
            LAST_SENT.remove(id);
            return;
        }

        int current = BlueNightInsomnia.get(player);
        Integer previous = LAST_SENT.get(id);
        if (previous == null || previous != current
                || player.tickCount % HEARTBEAT_TICKS == 0) {
            AlcorNetwork.syncInsomnia(player, current);
            LAST_SENT.put(id, current);
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        LAST_SENT.remove(event.getPlayer().getUUID());
    }

    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        LAST_SENT.remove(event.getPlayer().getUUID());
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        LAST_SENT.remove(event.getPlayer().getUUID());
    }
}
