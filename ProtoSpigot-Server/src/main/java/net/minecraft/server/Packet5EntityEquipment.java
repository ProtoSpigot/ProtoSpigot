package net.minecraft.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet5EntityEquipment extends Packet {
    public int a;
    public int b;
    private ItemStack c;

    public Packet5EntityEquipment() {
    }

    public Packet5EntityEquipment(int var1, int var2, ItemStack var3) {
        this.a = var1;
        this.b = var2;
        this.c = var3 == null ? null : var3.cloneItemStack();
    }

    public void a(DataInputStream var1) throws IOException {
        this.a = var1.readInt();
        this.b = var1.readShort();
        this.c = c(var1);
    }

    public void a(DataOutputStream var1) throws IOException {
        var1.writeInt(this.a);
        var1.writeShort(this.b);
        a(this.c, var1);
    }

    public void handle(Connection var1) {
        var1.a(this);
    }

    public int a() {
        return 8;
    }

    public boolean e() {
        return true;
    }

    public boolean a(Packet var1) {
        Packet5EntityEquipment var2 = (Packet5EntityEquipment)var1;
        return var2.a == this.a && var2.b == this.b;
    }

    // ProtoSpigot start - obfuscation helpers
    public int getEntityId() {
        return this.a;
    }

    public int getSlot() {
        return this.b;
    }

    public ItemStack getItemStack() {
        return this.c;
    }
    // ProtoSpigot end
}
