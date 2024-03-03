package com.github.protospigot;

import com.github.protospigot.handler.PacketReader;
import com.github.protospigot.handler.PacketWriter;
import com.github.protospigot.protocol.Protocol;
import com.github.protospigot.protocol.ProtocolType;
import com.github.protospigot.protocol.handshake.HandshakeHandlers;
import com.github.protospigot.protocol.protocol_1_5_2.Protocol_1_5_2;
import com.github.protospigot.util.ReadUtil;
import net.minecraft.server.INetworkManager;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet2Handshake;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class ProtoSpigot {

    private static final Set<Protocol> PROTOCOLS = new HashSet<>();
    private static final Set<Protocol> UNMODIFIABLE_PROTOCOLS = Collections.unmodifiableSet(PROTOCOLS);

    static {
        PROTOCOLS.add(new Protocol_1_5_2());
    }

    /**
     * Gets a set of protocols registered on the server.
     *
     * @return the set of protocols
     */
    public static Set<Protocol> getProtocols() {
        return UNMODIFIABLE_PROTOCOLS;
    }

    /**
     * Gets a protocol by a protocol version and protocol type.
     *
     * @param protocolVersion the protocol version
     * @param protocolType the protocol type
     * @return the protocol
     */
    public static Protocol getProtocol(int protocolVersion, ProtocolType protocolType) {
        return PROTOCOLS.stream()
                .filter(protocol -> protocol.getProtocolVersion() == protocolVersion
                        && protocol.getProtocolType() == protocolType)
                .findFirst()
                .orElse(null);
    }

    /**
     * Checks if the server supports a protocol version.
     *
     * @param protocolVersion the protocol version
     * @param protocolType protocol type of the version
     * @return true if the server supports the protocol version, false otherwise
     */
    public static boolean supports(int protocolVersion, ProtocolType protocolType) {
        for (Protocol protocol : PROTOCOLS)
            if (protocol.getProtocolType() == protocolType && protocol.getProtocolVersion() == protocolVersion)
                return true;
        return false;
    }

    /**
     * Writes a packet to a stream with a network manager.
     *
     * @param packet the packet
     * @param stream the stream
     * @param networkManager the network manager
     * @param <P> the type of packet
     */
    public static <P extends Packet> void writePacket(P packet, DataOutputStream stream, INetworkManager networkManager) throws IOException {
        Protocol protocol = getProtocol(networkManager.getProtocolVersion(), networkManager.getProtocolType());
        if (protocol == null) return;

        PacketWriter<P> writer = (PacketWriter<P>) protocol.getWriter(packet.getClass());
        if (writer == null) return;

        stream.writeByte(protocol.getServerPacketId(packet.getClass()));
        writer.write(packet, stream);
    }

    /**
     * Reads a packet with from a stream with a network manager.
     *
     * @param manager the network manager
     * @param stream the stream
     * @return the packet
     */
    public static Packet readPacket(INetworkManager manager, DataInputStream stream) throws IOException {
        int protocolVersion = manager.getProtocolVersion();

        // Handshaking
        if (protocolVersion == -1) {
            PacketReader<Packet2Handshake> handshakePacketReader;
            int packetId = stream.readUnsignedByte();

            // Valid handshake packet id, legacy
            if (packetId == 2)
                handshakePacketReader = HandshakeHandlers.LEGACY;
            // Invalid handshake packet id, modern
            else {
                ReadUtil.readVarInt(packetId, stream); // Packet size
                packetId = ReadUtil.readVarInt(stream); // Packet ID

                // Make sure that the modern packet id is 0 (handshaking)
                if (packetId != 0) return null;

                handshakePacketReader = HandshakeHandlers.MODERN;
            }

            // Handle a handshake packet early to fix the login packet on modern protocols
            Packet2Handshake handshakePacket = handshakePacketReader.read(stream);
            manager.initializeSettings(handshakePacket.getProtocolVersion(), handshakePacket.getProtocolType());
            return handshakePacket;
        }

        // Other packets
        ProtocolType protocolType = manager.getProtocolType();

        int packetId;
        switch (protocolType) {
            case LEGACY:
                packetId = stream.readUnsignedByte();
                break;
            case MODERN:
                ReadUtil.readVarInt(stream); // Packet size
                packetId = ReadUtil.readVarInt(stream);
                break;
            default:
                throw new IllegalArgumentException("Unknown protocol type");
        }

        Protocol protocol = getProtocol(protocolVersion, protocolType);
        if (protocol == null) return null;

        Class<? extends Packet> packetClass = protocol.getClientPacket(packetId);
        if (packetClass == null) return null;

        return protocol.getReader(packetClass).read(stream);
    }

    private ProtoSpigot() {}
}