package net.minecraft.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet0KeepAlive extends Packet {
    public int a;

    public Packet0KeepAlive() {
    }

    public Packet0KeepAlive(int var1) {
        this.a = var1;
    }

    public void handle(Connection var1) {
        var1.a(this);
    }

    public void a(DataInputStream var1) throws IOException {
        this.a = var1.readInt();
    }

    public void a(DataOutputStream var1) throws IOException {
        var1.writeInt(this.a);
    }

    public int a() {
        return 4;
    }

    public boolean e() {
        return true;
    }

    public boolean a(Packet var1) {
        return true;
    }

    public boolean a_() {
        return true;
    }

    // ProtoSpigot start - obfuscation helpers
    public int getKeepAliveId() {
        return this.a;
    }
    // ProtoSpigot end
}
