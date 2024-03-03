package com.github.protospigot.protocol.protocol_1_5_2.clientbound;

import com.github.protospigot.handler.PacketWriter;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet5EntityEquipment;

import java.io.DataOutputStream;
import java.io.IOException;

public final class EntityEquipmentPacketWriter implements PacketWriter<Packet5EntityEquipment> {
    @Override
    public void write(Packet5EntityEquipment packet, DataOutputStream stream) throws IOException {
        stream.writeInt(packet.getEntityId());
        stream.writeShort(packet.getSlot());
        // Write item stack to the stream
        Packet.a(packet.getItemStack(), stream);
    }
}
