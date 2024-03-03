package com.github.protospigot.protocol.protocol_1_5_2.clientbound;

import com.github.protospigot.handler.PacketWriter;
import net.minecraft.server.Packet4UpdateTime;

import java.io.DataOutputStream;
import java.io.IOException;

public class UpdateTimePacketWriter implements PacketWriter<Packet4UpdateTime> {
    @Override
    public void write(Packet4UpdateTime packet, DataOutputStream stream) throws IOException {
        stream.writeLong(packet.getWorldAge());
        stream.writeLong(packet.getTime());
    }
}
