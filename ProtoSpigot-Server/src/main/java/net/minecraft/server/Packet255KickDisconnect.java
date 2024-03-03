package net.minecraft.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet255KickDisconnect extends Packet {
    public String a;

    public Packet255KickDisconnect() {
    }

    public Packet255KickDisconnect(String var1) {
        this.a = var1;
    }

    public void a(DataInputStream var1) throws IOException {
        this.a = a(var1, 256);
    }

    public void a(DataOutputStream var1) throws IOException {
        a(this.a, var1);
    }

    public void handle(Connection var1) {
        var1.a(this);
    }

    public int a() {
        return this.a.length();
    }

    public boolean e() {
        return true;
    }

    public boolean a(Packet var1) {
        return true;
    }

    // ProtoSpigot start - obfuscation helper
    public String getReason() {
        return this.a;
    }
    // ProtoSpigot end
}
