package com.rotpaddon.exampleaddon.network;

import com.rotpaddon.exampleaddon.AddonMain;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Optional;

public final class AlcorNetwork {
    private static final String PROTOCOL = "1";

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
    }

    public static void sendToServer(int formId) {
        CHANNEL.sendToServer(new AlcorSelectFormPacket(formId));
    }
}
