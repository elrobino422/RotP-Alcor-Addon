package com.rotpaddon.exampleaddon.network;

import com.rotpaddon.exampleaddon.client.gui.BlueNightInsomniaHud;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Server -> client snapshot of the player's Insomnia resource.
 * Never trust the client to set this value on the server.
 */
public final class BlueNightInsomniaSyncPacket {
    private final int stacks;

    public BlueNightInsomniaSyncPacket(int stacks) {
        this.stacks = Math.max(0, Math.min(100, stacks));
    }

    public static void encode(BlueNightInsomniaSyncPacket packet, PacketBuffer buf) {
        buf.writeVarInt(packet.stacks);
    }

    public static BlueNightInsomniaSyncPacket decode(PacketBuffer buf) {
        return new BlueNightInsomniaSyncPacket(buf.readVarInt());
    }

    public static void handle(BlueNightInsomniaSyncPacket packet,
                              Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(
                Dist.CLIENT, () -> () -> ClientOnly.apply(packet.stacks)));
        context.setPacketHandled(true);
    }

    // Kept separate to avoid loading client rendering code on dedicated servers.
    private static final class ClientOnly {
        private static void apply(int stacks) {
            BlueNightInsomniaHud.receive(stacks);
        }
    }
}
