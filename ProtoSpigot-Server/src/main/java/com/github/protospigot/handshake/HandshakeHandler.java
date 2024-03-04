package com.github.protospigot.handshake;

import com.github.protospigot.handler.PacketReader;
import com.github.protospigot.util.ReadUtil;
import net.minecraft.server.INetworkManager;
import net.minecraft.server.Packet2Handshake;

import java.io.DataInputStream;
import java.io.IOException;

public final class HandshakeHandler {

    private static final int LEGACY_HANDSHAKE_ID = 2;

    private static final PacketReader<Packet2Handshake> LEGACY = new LegacyHandshakeHandler();
    private static final PacketReader<Packet2Handshake> MODERN = new ModernHandshakeHandler();

    /**
     * Handles a handshake packet with a network manager and input stream.
     *
     * @param networkManager the network manager
     * @param stream the input stream
     * @return the handshake packet
     */
    public static Packet2Handshake handle(INetworkManager networkManager, DataInputStream stream) throws IOException {
        PacketReader<Packet2Handshake> handshakePacketReader;
        int packetId = stream.readUnsignedByte();

        // Valid handshake packet id, legacy
        if (packetId == LEGACY_HANDSHAKE_ID)
            handshakePacketReader = LEGACY;
        // Invalid handshake packet id, modern
        else {
            ReadUtil.readVarInt(packetId, stream); // Packet size
            packetId = ReadUtil.readVarInt(stream); // Packet ID

            // Make sure that the modern packet id is 0 (handshaking)
            if (packetId != 0) return null;

            handshakePacketReader = MODERN;
        }

        // Handle a handshake packet early to fix the login packet on modern protocols
        Packet2Handshake handshakePacket = handshakePacketReader.read(stream);
        networkManager.initializeSettings(handshakePacket.getProtocolVersion(), handshakePacket.getProtocolType());
        return handshakePacket;
    }

    private HandshakeHandler() {}
}