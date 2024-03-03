package com.github.protospigot.protocol.handshake;

import com.github.protospigot.handler.PacketReader;
import com.github.protospigot.protocol.ProtocolType;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet2Handshake;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * A packet reader that handles 1.6 and earlier handshake packets.
 */
final class LegacyHandshakeHandler implements PacketReader<Packet2Handshake> {
    @Override
    public Packet2Handshake read(DataInputStream stream) throws IOException {
        byte protocolVersion = stream.readByte();
        String username = Packet.a(stream, 16); // Read the username string
        String serverAddress = Packet.a(stream, 255); // Read the server address string
        int serverPort = stream.readInt();

        if (!Packet2Handshake.validName.matcher(username).matches())
            throw new IOException("Invalid name!");

        return new Packet2Handshake(protocolVersion, username, serverAddress, serverPort, ProtocolType.LEGACY);
    }
}