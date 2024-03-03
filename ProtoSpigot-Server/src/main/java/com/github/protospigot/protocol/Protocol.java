package com.github.protospigot.protocol;

import com.github.protospigot.handler.PacketReader;
import com.github.protospigot.handler.PacketWriter;
import net.minecraft.server.Packet;

import java.util.HashMap;
import java.util.Map;

public class Protocol {

    private final int protocolVersion;

    private final Map<Class<? extends Packet>, PacketWriter<? extends Packet>> writers = new HashMap<>();
    private final Map<Class<? extends Packet>, Integer> clientBoundPackets = new HashMap<>();

    private final Map<Class<? extends Packet>, PacketReader<? extends Packet>> readers = new HashMap<>();
    private final Map<Integer, Class<? extends Packet>> serverBoundPackets = new HashMap<>();

    public Protocol(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    /**
     * Gets a packet writer.
     *
     * @param packet a class of the packet
     * @return the packet writer
     * @param <P> the type of packet
     */
    public <P extends Packet> PacketWriter<P> getWriter(Class<P> packet) {
        return (PacketWriter<P>) this.writers.get(packet);
    }

    /**
     * Gets a packet reader.
     *
     * @param packet a class of the packet
     * @return the packet reader
     * @param <P> the type of packet
     */
    public <P extends Packet> PacketReader<P> getReader(Class<P> packet) {
        return (PacketReader<P>) this.readers.get(packet);
    }

    /**
     * Gets an id of a client-side packet.
     *
     * @param packet the packet
     * @return the id
     */
    public int getServerPacketId(Class<? extends Packet> packet) {
        return this.clientBoundPackets.get(packet);
    }

    /**
     * Gets an id of a server-bound packet with a packet id.
     *
     * @param packetId the packet id
     * @return the packet class
     */
    public Class<? extends Packet> getClientPacket(int packetId) {
        return this.serverBoundPackets.get(packetId);
    }

    /**
     * Gets a version of the protocol.
     *
     * @return the version
     */
    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    /**
     * Registers a client-bound packet with an id and writer.
     *
     * @param id the id
     * @param packet the packet
     * @param writer the writer
     */
    protected <P extends Packet> void registerClientPacket(int id, Class<P> packet, PacketWriter<P> writer) {
        this.clientBoundPackets.put(packet, id);
        this.writers.put(packet, writer);
    }

    /**
     * Registers a server-bound packet with an id and reader.
     *
     * @param id the id
     * @param packet the packet
     * @param reader the reader
     */
    protected <P extends Packet> void registerClientPacket(int id, Class<P> packet, PacketReader<P> reader) {
        this.serverBoundPackets.put(id, packet);
        this.readers.put(packet, reader);
    }
}