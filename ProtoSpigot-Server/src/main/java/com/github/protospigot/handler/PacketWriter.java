package com.github.protospigot.handler;

import net.minecraft.server.Packet;

import java.io.DataOutputStream;
import java.io.IOException;

public interface PacketWriter<P extends Packet> {
    /**
     * Writes a packet to a stream.
     *
     * @param packet the packet
     * @param stream the stream
     */
    void write(P packet, DataOutputStream stream) throws IOException;
}