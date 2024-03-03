package com.github.protospigot.protocol.handshake;

import com.github.protospigot.handler.PacketReader;
import com.github.protospigot.util.ReadUtil;
import net.minecraft.server.Packet2Handshake;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * A packet reader that handles 1.7 and newer handshake packets.
 */
final class ModernHandshakeHandler implements PacketReader<Packet2Handshake> {
    @Override
    public Packet2Handshake read(DataInputStream stream) throws IOException {
        int protocolVersion = ReadUtil.readVarInt(stream);
        String serverAddress = ReadUtil.readModernString(stream, Short.MAX_VALUE);
        int serverPort = stream.readUnsignedShort();

        ReadUtil.readVarInt(stream); // Read next protocol state, unused (yet)

        return new Packet2Handshake(protocolVersion, "", serverAddress, serverPort, true);
    }
}
