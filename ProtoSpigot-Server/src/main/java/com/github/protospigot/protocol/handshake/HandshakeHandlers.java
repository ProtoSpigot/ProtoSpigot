package com.github.protospigot.protocol.handshake;

import com.github.protospigot.handler.PacketReader;
import net.minecraft.server.Packet2Handshake;

public final class HandshakeHandlers {

    public static final PacketReader<Packet2Handshake> LEGACY = new LegacyHandshakeHandler();
    public static final PacketReader<Packet2Handshake> MODERN = new ModernHandshakeHandler();

    private HandshakeHandlers() {}
}