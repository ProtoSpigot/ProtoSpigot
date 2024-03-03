package com.github.protospigot;

import com.github.protospigot.handler.PacketReader;
import com.github.protospigot.handler.PacketWriter;
import com.github.protospigot.protocol.Protocol;
import com.github.protospigot.protocol.handshake.HandshakeHandlers;
import com.github.protospigot.protocol.protocol_1_5_2.Protocol_1_5_2;
import com.github.protospigot.util.ReadUtil;
import net.minecraft.server.Packet;

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
     * Gets a protocol by a protocol version.
     *
     * @param protocolVersion the protocol version
     * @return the protocol
     */
    public static Protocol getProtocol(int protocolVersion) {
        return PROTOCOLS.stream()
                .filter(protocol -> protocol.getProtocolVersion() == protocolVersion)
                .findFirst()
                .orElse(null);
    }

    /**
     * Checks if the server supports a protocol version.
     *
     * @param protocolVersion the protocol version
     * @return true if the server supports the protocol version, false otherwise
     */
    public static boolean supports(int protocolVersion) {
        return PROTOCOLS.stream()
                .map(Protocol::getProtocolVersion)
                .anyMatch(protocol -> protocol == protocolVersion);
    }

    /**
     * Writes a packet to a stream with a protocol version.
     *
     * @param packet the packet
     * @param stream the stream
     * @param protocolVersion the protocol version
     * @param <P> the type of packet
     */
    public static <P extends Packet> void writePacket(P packet, DataOutputStream stream, int protocolVersion) throws IOException {
        Protocol protocol = getProtocol(protocolVersion);
        if (protocol == null) return;

        PacketWriter<P> writer = (PacketWriter<P>) protocol.getWriter(packet.getClass());
        if (writer == null) return;

        stream.writeByte(protocol.getServerPacketId(packet.getClass()));
        writer.write(packet, stream);
    }

    public static Packet readPacket(DataInputStream stream, int protocolVersion, boolean modern) throws IOException {
        // Handshaking
        if (protocolVersion == -1) {
            PacketReader<?> handshakePacketReader;
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

            return handshakePacketReader.read(stream);
        }

        // Other packets
        int packetId;

        if (modern) {
            ReadUtil.readVarInt(stream); // Packet size
            packetId = ReadUtil.readVarInt(stream);
        } else
            packetId = stream.readUnsignedByte();

        Protocol protocol = getProtocol(protocolVersion);
        if (protocol == null) return null;

        Class<? extends Packet> packetClass = protocol.getClientPacket(packetId);
        if (packetClass == null) return null;

        return protocol.getReader(packetClass).read(stream);
    }

    private ProtoSpigot() {}
}