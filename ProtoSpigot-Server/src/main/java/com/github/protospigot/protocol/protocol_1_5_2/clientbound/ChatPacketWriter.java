package com.github.protospigot.protocol.protocol_1_5_2.clientbound;

import com.github.protospigot.handler.PacketWriter;
import net.minecraft.server.Packet3Chat;

import java.io.DataOutputStream;
import java.io.IOException;

import static com.github.protospigot.protocol.protocol_1_5_2.Protocol_1_5_2.writeString;

public final class ChatPacketWriter implements PacketWriter<Packet3Chat> {
    @Override
    public void write(Packet3Chat packet, DataOutputStream stream) throws IOException {
        writeString(packet.message, stream);
    }
}