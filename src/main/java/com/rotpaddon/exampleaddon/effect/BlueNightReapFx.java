package com.rotpaddon.exampleaddon.effect;

import com.rotpaddon.exampleaddon.AddonMain;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A brief, server-synchronized crescent sweeping across the player's facing
 * direction. Spreading the particles over eight world ticks makes the slash
 * actually animate instead of spawning its full shape in a single frame.
 *
 * No custom renderer or client-only class is needed: ServerWorld sends the
 * particles to nearby players in singleplayer and dedicated multiplayer.
 */
@Mod.EventBusSubscriber(modid = AddonMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class BlueNightReapFx {
    private static final int DURATION_TICKS = 8;

    // Moon Reaper's midnight blue and cyan palette.
    private static final RedstoneParticleData MIDNIGHT =
            new RedstoneParticleData(0.06F, 0.11F, 0.37F, 1.35F);
    private static final RedstoneParticleData MOONLIGHT =
            new RedstoneParticleData(0.26F, 0.78F, 1.00F, 1.10F);

    private static final List<ReapArc> ARCS = new ArrayList<>();

    private BlueNightReapFx() {}

    public static void start(ServerWorld world, ServerPlayerEntity player) {
        Vector3d look = player.getLookAngle();
        double flatLength = Math.sqrt(look.x * look.x + look.z * look.z);
        if (flatLength < 0.0001D) return;

        Vector3d forward = new Vector3d(look.x / flatLength, 0.0D, look.z / flatLength);
        Vector3d right = new Vector3d(-forward.z, 0.0D, forward.x);
        Vector3d origin = new Vector3d(player.getX(), player.getY() + 1.10D, player.getZ());
        ARCS.add(new ReapArc(world, origin, forward, right));
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.world instanceof ServerWorld)) return;

        ServerWorld world = (ServerWorld) event.world;
        Iterator<ReapArc> iterator = ARCS.iterator();
        while (iterator.hasNext()) {
            ReapArc arc = iterator.next();
            if (arc.world != world) continue;
            arc.drawFrame();
            if (++arc.age >= DURATION_TICKS) iterator.remove();
        }
    }

    private static final class ReapArc {
        private final ServerWorld world;
        private final Vector3d origin;
        private final Vector3d forward;
        private final Vector3d right;
        private int age;

        private ReapArc(ServerWorld world, Vector3d origin, Vector3d forward, Vector3d right) {
            this.world = world;
            this.origin = origin;
            this.forward = forward;
            this.right = right;
        }

        private void drawFrame() {
            // The crescent traces from the player's left to right.
            // Previous dust particles linger briefly, producing an afterimage.
            for (int segment = 0; segment < 6; segment++) {
                double progress = (age + (segment + 0.5D) / 6.0D) / DURATION_TICKS;
                double angle = -1.25D + progress * 2.5D;
                double curve = Math.sin(progress * Math.PI);

                for (int band = 0; band < 3; band++) {
                    double radius = 2.35D + band * 0.29D + 0.28D * curve;
                    double forwardOffset = Math.cos(angle) * radius;
                    double sidewaysOffset = Math.sin(angle) * radius;
                    double x = origin.x + forward.x * forwardOffset + right.x * sidewaysOffset;
                    double z = origin.z + forward.z * forwardOffset + right.z * sidewaysOffset;
                    double y = origin.y + 0.50D * curve - 0.07D * band;

                    world.sendParticles(band == 1 ? MOONLIGHT : MIDNIGHT,
                            x, y, z, 1, 0, 0, 0, 0);
                    if (band == 1 && segment == 5) {
                        world.sendParticles(ParticleTypes.END_ROD,
                                x, y, z, 1, 0, 0, 0, 0);
                    }
                }
            }

            // Extra blue sparks follow the moving tip of the blade.
            double tipProgress = Math.min(1.0D, (age + 1.0D) / DURATION_TICKS);
            double angle = -1.25D + tipProgress * 2.5D;
            double radius = 3.15D + 0.28D * Math.sin(tipProgress * Math.PI);
            double f = radius * Math.cos(angle);
            double r = radius * Math.sin(angle);
            world.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    origin.x + forward.x * f + right.x * r,
                    origin.y + 0.50D * Math.sin(tipProgress * Math.PI),
                    origin.z + forward.z * f + right.z * r,
                    2, 0.09D, 0.10D, 0.09D, 0.005D);
        }
    }
}
