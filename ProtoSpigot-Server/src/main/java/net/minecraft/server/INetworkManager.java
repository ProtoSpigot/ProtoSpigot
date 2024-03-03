package net.minecraft.server;

import com.github.protospigot.protocol.ProtocolType; // ProtoSpigot

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
     * Gets a protocol type of the network manager.
     *
     * @return the protocol type
     */
    ProtocolType getProtocolType();

    /**
     * Initializes a protocol version and protocol type of the network manager.
     * Should be only called while receiving a handshake packet.
     *
     * @param protocolVersion the protocol version
     * @param protocolType the protocol type
     */
    void initializeSettings(int protocolVersion, ProtocolType protocolType);
    // ProtoSpigot end
}
