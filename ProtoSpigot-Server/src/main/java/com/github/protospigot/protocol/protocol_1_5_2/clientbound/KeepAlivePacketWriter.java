package com.github.protospigot.protocol.protocol_1_5_2.clientbound;

import com.github.protospigot.handler.PacketWriter;
import net.minecraft.server.Packet0KeepAlive;

import java.io.DataOutputStream;
import java.io.IOException;

public final class KeepAlivePacketWriter implements PacketWriter<Packet0KeepAlive> {
    @Override
    public void write(Packet0KeepAlive packet, DataOutputStream stream) throws IOException {
        stream.writeInt(packet.getKeepAliveId());
    }
}
