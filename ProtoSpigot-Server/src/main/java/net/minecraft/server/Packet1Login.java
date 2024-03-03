package net.minecraft.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet1Login extends Packet {
    public int a = 0;
    public WorldType b;
    public boolean c;
    public EnumGamemode d;
    public int e;
    public byte f;
    public byte g;
    public byte h;

    public Packet1Login() {
    }

    public Packet1Login(int var1, WorldType var2, EnumGamemode var3, boolean var4, int var5, int var6, int var7, int var8) {
        this.a = var1;
        this.b = var2;
        this.e = var5;
        this.f = (byte)var6;
        this.d = var3;
        this.g = (byte)var7;
        this.h = (byte)var8;
        this.c = var4;
    }

    public void a(DataInputStream var1) throws IOException {
        this.a = var1.readInt();
        String var2 = a(var1, 16);
        this.b = WorldType.getType(var2);
        if (this.b == null) {
            this.b = WorldType.NORMAL;
        }

        int var3 = var1.readByte();
        this.c = (var3 & 8) == 8;
        var3 &= -9;
        this.d = EnumGamemode.a(var3);
        this.e = var1.readByte();
        this.f = var1.readByte();
        this.g = var1.readByte();
        this.h = var1.readByte();
    }

    public void a(DataOutputStream var1) throws IOException {
        var1.writeInt(this.a);
        a(this.b == null ? "" : this.b.name(), var1);
        int var2 = this.d.a();
        if (this.c) {
            var2 |= 8;
        }

        var1.writeByte(var2);
        var1.writeByte(this.e);
        var1.writeByte(this.f);
        var1.writeByte(this.g);
        var1.writeByte(this.h);
    }

    public void handle(Connection var1) {
        var1.a(this);
    }

    public int a() {
        int var1 = 0;
        if (this.b != null) {
            var1 = this.b.name().length();
        }

        return 6 + 2 * var1 + 4 + 4 + 1 + 1 + 1;
    }

    // ProtoSpigot start - obfuscation helpers
    public int getEntityId() {
        return this.a;
    }

    public WorldType getWorldType() {
        return this.b;
    }

    public EnumGamemode getGameMode() {
        return this.d;
    }

    public boolean isHardcore() {
        return this.c;
    }

    public int getDimension() {
        return this.e;
    }

    public byte getDifficulty() {
        return this.f;
    }

    public byte getWorldHeight() {
        return this.g;
    }

    public byte getMaxPlayers() {
        return this.h;
    }
    // ProtoSpigot end
}
