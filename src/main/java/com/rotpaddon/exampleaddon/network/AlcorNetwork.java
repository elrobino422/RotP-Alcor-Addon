package com.rotpaddon.exampleaddon.network;

import com.rotpaddon.exampleaddon.AddonMain;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Optional;

public final class AlcorNetwork {
    private static final String PROTOCOL = "2";

    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(AddonMain.MOD_ID, "form_selector"))
            .clientAcceptedVersions(PROTOCOL::equals)
            .serverAcceptedVersions(PROTOCOL::equals)
            .networkProtocolVersion(() -> PROTOCOL)
            .simpleChannel();

    private AlcorNetwork() {}

    public static void register() {
        CHANNEL.registerMessage(0,
                AlcorSelectFormPacket.class,
                AlcorSelectFormPacket::encode,
                AlcorSelectFormPacket::decode,
                AlcorSelectFormPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        // Insomnia is authoritative on the server. Only snapshots travel to the client.
        CHANNEL.registerMessage(1,
                BlueNightInsomniaSyncPacket.class,
                BlueNightInsomniaSyncPacket::encode,
                BlueNightInsomniaSyncPacket::decode,
                BlueNightInsomniaSyncPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void syncInsomnia(ServerPlayerEntity player, int amount) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new BlueNightInsomniaSyncPacket(amount));
    }

    public static void sendToServer(int formId) {
        CHANNEL.sendToServer(new AlcorSelectFormPacket(formId));
    }
}
