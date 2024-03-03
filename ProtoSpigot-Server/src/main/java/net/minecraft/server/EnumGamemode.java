package net.minecraft.server;

public enum EnumGamemode {
    NONE(-1, ""),
    SURVIVAL(0, "survival"),
    CREATIVE(1, "creative"),
    ADVENTURE(2, "adventure");

    int e;
    String f;

    private EnumGamemode(int var3, String var4) {
        this.e = var3;
        this.f = var4;
    }

    public int a() {
        return this.e;
    }

    public String b() {
        return this.f;
    }

    public void a(PlayerAbilities var1) {
        if (this == CREATIVE) {
            var1.canFly = true;
            var1.canInstantlyBuild = true;
            var1.isInvulnerable = true;
        } else {
            var1.canFly = false;
            var1.canInstantlyBuild = false;
            var1.isInvulnerable = false;
            var1.isFlying = false;
        }

        var1.mayBuild = !this.isAdventure();
    }

    public boolean isAdventure() {
        return this == ADVENTURE;
    }

    public boolean d() {
        return this == CREATIVE;
    }

    public static EnumGamemode a(int var0) {
        EnumGamemode[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EnumGamemode var4 = var1[var3];
            if (var4.e == var0) {
                return var4;
            }
        }

        return SURVIVAL;
    }

    // ProtoSpigot start - obfuscation helpers
    public int getId() {
        return this.e;
    }
    // ProtoSpigot end
}
