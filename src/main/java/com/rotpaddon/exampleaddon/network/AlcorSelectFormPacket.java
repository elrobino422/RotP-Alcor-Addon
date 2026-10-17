package com.rotpaddon.exampleaddon.network;

import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.rotpaddon.exampleaddon.form.AlcorFormRegistry;
import com.rotpaddon.exampleaddon.form.IAlcorFormHolder;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import com.rotpaddon.exampleaddon.form.BlueNightEquipmentManager;

import java.util.function.Supplier;

/** Client sends only a requested form ID. The server decides which Stand the player has. */
public class AlcorSelectFormPacket {
    private final int requestedId;

    public AlcorSelectFormPacket(int requestedId) {
        this.requestedId = requestedId;
    }

    public static void encode(AlcorSelectFormPacket packet, PacketBuffer buffer) {
        buffer.writeInt(packet.requestedId);
    }

    public static AlcorSelectFormPacket decode(PacketBuffer buffer) {
        return new AlcorSelectFormPacket(buffer.readInt());
    }

    public static void handle(AlcorSelectFormPacket packet,
                              Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity player = context.getSender();
            if (player == null) return;

            IStandPower power = IStandPower.getPlayerStandPower(player);
            if (power == null || !power.hasPower() || !power.isActive()) return;
            if (!(power.getStandManifestation() instanceof IAlcorFormHolder)) return;

            ResourceLocation standId = power.getType().getRegistryName();
            if (standId == null || !AlcorFormRegistry.isAllowed(
                    standId, packet.requestedId, power.getResolveLevel())) return;


            IAlcorFormHolder holder =
                    (IAlcorFormHolder) power.getStandManifestation();

            if (AlcorFormRegistry.BLUE_NIGHT.equals(standId)) {

                BlueNightEquipmentManager.selectForm(
                        player,
                        holder,
                        packet.requestedId
                );

            }
            else {
                // Other Stands, such as Fortunate Son,
                // can use their own form-specific behavior later.
                holder.alcorSetForm(packet.requestedId);
            }



        });
        context.setPacketHandled(true);

    }
}
