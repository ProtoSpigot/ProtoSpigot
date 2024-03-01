package net.minecraft.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Packet63WorldParticles extends Packet {

    private String a;
    private float b;
    private float c;
    private float d;
    private float e;
    private float f;
    private float g;
    private float h;
    private int i;

    public Packet63WorldParticles() {}

    // Spigot start - Added constructor
    public Packet63WorldParticles(String particleName, float x, float y, float z, float offsetX, float offsetY, float offsetZ, float speed, int count) {
        a = particleName;
        b = x;
        c = y;
        d = z;
        e = offsetX;
        f = offsetY;
        g = offsetZ;
        h = speed;
        i = count;
    }
    // Spigot end

    public void a(DataInputStream datainputstream) throws java.io.IOException {
        this.a = a(datainputstream, 64);
        this.b = datainputstream.readFloat();
        this.c = datainputstream.readFloat();
        this.d = datainputstream.readFloat();
        this.e = datainputstream.readFloat();
        this.f = datainputstream.readFloat();
        this.g = datainputstream.readFloat();
        this.h = datainputstream.readFloat();
        this.i = datainputstream.readInt();
    }

    public void a(DataOutputStream dataoutputstream) throws java.io.IOException {
        a(this.a, dataoutputstream);
        dataoutputstream.writeFloat(this.b);
        dataoutputstream.writeFloat(this.c);
        dataoutputstream.writeFloat(this.d);
        dataoutputstream.writeFloat(this.e);
        dataoutputstream.writeFloat(this.f);
        dataoutputstream.writeFloat(this.g);
        dataoutputstream.writeFloat(this.h);
        dataoutputstream.writeInt(this.i);
    }

    public void handle(Connection connection) {
        connection.a(this);
    }

    public int a() {
        return 64;
    }
}
