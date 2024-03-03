package com.github.protospigot.protocol.protocol_1_5_2.clientbound;

import com.github.protospigot.handler.PacketWriter;
import net.minecraft.server.Packet255KickDisconnect;

import java.io.DataOutputStream;
import java.io.IOException;

import static com.github.protospigot.protocol.protocol_1_5_2.Protocol_1_5_2.writeString;

public final class KickPacketWriter implements PacketWriter<Packet255KickDisconnect> {
    @Override
    public void write(Packet255KickDisconnect packet, DataOutputStream stream) throws IOException {
        writeString(packet.getReason(), stream);
    }
}