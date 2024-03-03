package net.minecraft.server;

import java.net.SocketAddress;

public interface INetworkManager {

    void a(Connection connection);

    void queue(Packet packet);

    void a();

    void b();

    SocketAddress getSocketAddress();

    void d();

    int e();

    void a(String s, Object... aobject);
    
    java.net.Socket getSocket(); // Spigot
    
    void setSocketAddress(java.net.SocketAddress address); // Spigot

    // ProtoSpigot start - multiple protocol support
    /**
     * Gets a protocol version of the network manager.
     *
     * @return the protocol version
     */
    int getProtocolVersion();

    /**
     * Checks if the client's version is 1.7 or newer.
     *
     * @return true if the client's version is 1.7 or newer, false otherwise
     */
    boolean isModern();

    /**
     * Initializes a protocol version and "modern" setting of the network manager.
     * Should be only called while receiving a handshake packet.
     *
     * @param protocolVersion the protocol version
     * @param modern the "modern" setting
     */
    void initializeSettings(int protocolVersion, boolean modern);
    // ProtoSpigot end
}
