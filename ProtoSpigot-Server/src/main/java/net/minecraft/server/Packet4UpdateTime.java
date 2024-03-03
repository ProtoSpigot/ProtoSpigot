package net.minecraft.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet4UpdateTime extends Packet {
    public long a;
    public long b;

    public Packet4UpdateTime() {
    }

    public Packet4UpdateTime(long var1, long var3) {
        this.a = var1;
        this.b = var3;
    }

    public void a(DataInputStream var1) throws IOException {
        this.a = var1.readLong();
        this.b = var1.readLong();
    }

    public void a(DataOutputStream var1) throws IOException {
        var1.writeLong(this.a);
        var1.writeLong(this.b);
    }

    public void handle(Connection var1) {
        var1.a(this);
    }

    public int a() {
        return 16;
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
    public long getWorldAge() {
        return this.a;
    }

    public long getTime() {
        return this.b;
    }
    // ProtoSpigot end
}
