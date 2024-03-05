package com.github.protospigot;

import com.github.protospigot.handler.PacketWriter;
import com.github.protospigot.protocol.Protocol;
import com.github.protospigot.protocol.ProtocolType;
import com.github.protospigot.handshake.HandshakeHandler;
import com.github.protospigot.protocol.protocol_1_5_2.Protocol_1_5_2;
import com.github.protospigot.util.ReadUtil;
import com.github.protospigot.util.WriteUtil;
import net.minecraft.server.INetworkManager;
import net.minecraft.server.Packet;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A main class for manipulating minecraft protocol.
 */
public final class ProtoSpigot {

    public static final int UNKNOWN_PROTOCOL_VERSION = -1;

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
        ProtocolType protocolType = networkManager.getProtocolType();

        Protocol protocol = getProtocol(networkManager.getProtocolVersion(), protocolType);
        if (protocol == null) return;

        PacketWriter<P> writer = (PacketWriter<P>) protocol.getWriter(packet.getClass());
        if (writer == null) return;

        int packetId = protocol.getServerPacketId(packet.getClass());

        // Packet header
        switch (protocolType) {
            case LEGACY:
                stream.writeByte(packetId);
                writer.write(packet, stream);
                break;
            case MODERN:
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                DataOutputStream dataStream = new DataOutputStream(byteStream);

                // Packet ID and body
                WriteUtil.writeVarInt(dataStream, packetId);
                writer.write(packet, dataStream);
                byte[] idAndBody = byteStream.toByteArray();

                WriteUtil.writeVarInt(stream, idAndBody.length);
                stream.write(idAndBody);
                break;
            default:
                // Code that never can't be reached, but java compiler says something else...
                throw new IllegalArgumentException("Unknown protocol type");
        }
    }

    /**
     * Reads a packet with from a stream with a network manager.
     *
     * @param networkManager the network manager
     * @param stream the stream
     * @return the packet
     */
    public static Packet readPacket(INetworkManager networkManager, DataInputStream stream) throws IOException {
        int protocolVersion = networkManager.getProtocolVersion();

        // Protocol version is an unknown version, we're sure that the packet must be handshaking
        if (protocolVersion == UNKNOWN_PROTOCOL_VERSION)
            return HandshakeHandler.handle(networkManager, stream);

        ProtocolType protocolType = networkManager.getProtocolType();

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
                // Code that never can't be reached, but java compiler says something else...
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