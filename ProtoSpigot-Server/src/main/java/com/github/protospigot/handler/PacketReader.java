package com.github.protospigot.handler;

import net.minecraft.server.Packet;

import java.io.DataInputStream;
import java.io.IOException;

public interface PacketReader<P extends Packet> {
    /**
     * Reads a packet from a stream.
     *
     * @param stream the stream
     * @return the packet
     */
    P read(DataInputStream stream) throws IOException;
}