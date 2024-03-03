package net.minecraft.server;

import com.github.protospigot.protocol.ProtocolType; // ProtoSpigot

import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.io.IOException; // CraftBukkit

public class Packet2Handshake extends Packet {

    private int a;
    private String b;
    public String c; // CraftBukkit private -> public
    public int d; // CraftBukkit private -> public

    private final ProtocolType protocolType; // ProtoSpigot - 1.7+

    public Packet2Handshake() {
        this.protocolType = ProtocolType.LEGACY; // ProtoSpigot - 1.7+
    }

    // ProtoSpigot start
    public Packet2Handshake(int protocolVersion, String username, String serverHost, int serverPort, ProtocolType protocolType) {
        this.a = protocolVersion;
        this.b = username;
        this.c = serverHost;
        this.d = serverPort;
        this.protocolType = protocolType;
    }
    // ProtoSpigot end

    public static final java.util.regex.Pattern validName = java.util.regex.Pattern.compile("^[a-zA-Z0-9_-]{2,16}$");
    public void a(DataInputStream datainputstream) throws IOException { // CraftBukkit - throws IOException
        this.a = datainputstream.readByte();
        this.b = a(datainputstream, 16);
        this.c = a(datainputstream, 255);
        this.d = datainputstream.readInt();
        if(!validName.matcher(this.b).matches()) throw new IOException("Invalid name!"); // Spigot
    }

    public void a(DataOutputStream dataoutputstream) throws IOException { // CraftBukkit - throws IOException
        dataoutputstream.writeByte(this.a);
        a(this.b, dataoutputstream);
        a(this.c, dataoutputstream);
        dataoutputstream.writeInt(this.d);
    }

    public void handle(Connection connection) {
        connection.a(this);
    }

    public int a() {
        return 3 + 2 * this.b.length();
    }

    public int d() {
        return this.a;
    }

    public String f() {
        return this.b;
    }

    // ProtoSpigot start - obfuscation helpers
    public int getProtocolVersion() {
        return this.a;
    }
    // ProtoSpigot end

    // ProtoSpigot start - 1.7+
    public ProtocolType getProtocolType() {
        return this.protocolType;
    }
    // ProtoSpigot end
}
