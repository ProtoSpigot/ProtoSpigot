package net.minecraft.server;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

// CraftBukkit start
import java.io.PrintStream;
import java.util.logging.Level;

import org.bukkit.craftbukkit.LoggerOutputStream;
import org.bukkit.event.server.ServerCommandEvent;
// CraftBukkit end

public class DedicatedServer extends MinecraftServer implements IMinecraftServer {

    private final List k = Collections.synchronizedList(new ArrayList());
    private final IConsoleLogManager l;
    private RemoteStatusListener m;
    private RemoteControlListener n;
    public PropertyManager propertyManager; // CraftBukkit - private -> public
    private boolean generateStructures;
    private EnumGamemode q;
    private ServerConnection r;
    private boolean s = false;

    // CraftBukkit start - Signature changed
    public DedicatedServer(joptsimple.OptionSet options) {
        super(options);
        // CraftBukkit end
        this.l = new ConsoleLogManager("Minecraft-Server", (String) null, (String) null); // CraftBukkit - null last argument
        // new ThreadSleepForever(this);
    }

    protected boolean init() throws java.net.UnknownHostException { // CraftBukkit - throws UnknownHostException
        ThreadCommandReader threadcommandreader = new ThreadCommandReader(this);

        threadcommandreader.setDaemon(true);
        threadcommandreader.start();

        // CraftBukkit start
        System.setOut(new PrintStream(new LoggerOutputStream(this.getLogger().getLogger(), Level.INFO), true));
        System.setErr(new PrintStream(new LoggerOutputStream(this.getLogger().getLogger(), Level.SEVERE), true));
        // CraftBukkit end

        this.getLogger().info("Starting minecraft server version 1.5.2");
        if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
            this.getLogger().warning("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }

        this.getLogger().info("Loading properties");
        this.propertyManager = new PropertyManager(this.options, this.getLogger()); // CraftBukkit - CLI argument support
        if (this.I()) {
            this.d("127.0.0.1");
        } else {
            this.setOnlineMode(this.propertyManager.getBoolean("online-mode", true));
            this.d(this.propertyManager.getString("server-ip", ""));
        }

        this.setSpawnAnimals(this.propertyManager.getBoolean("spawn-animals", true));
        this.setSpawnNPCs(this.propertyManager.getBoolean("spawn-npcs", true));
        this.setPvP(this.propertyManager.getBoolean("pvp", true));
        this.setAllowFlight(this.propertyManager.getBoolean("allow-flight", false));
        this.setTexturePack(this.propertyManager.getString("texture-pack", ""));
        this.setMotd(this.propertyManager.getString("motd", "A Minecraft Server"));
        this.setForceGamemode(this.propertyManager.getBoolean("force-gamemode", false));
        if (this.propertyManager.getInt("difficulty", 1) < 0) {
            this.propertyManager.a("difficulty", Integer.valueOf(0));
        } else if (this.propertyManager.getInt("difficulty", 1) > 3) {
            this.propertyManager.a("difficulty", Integer.valueOf(3));
        }

        this.generateStructures = this.propertyManager.getBoolean("generate-structures", true);
        int i = this.propertyManager.getInt("gamemode", EnumGamemode.SURVIVAL.a());

        this.q = WorldSettings.a(i);
        this.getLogger().info("Default game type: " + this.q);
        InetAddress inetaddress = null;

        if (this.getServerIp().length() > 0) {
            inetaddress = InetAddress.getByName(this.getServerIp());
        }

        if (this.G() < 0) {
            this.setPort(this.propertyManager.getInt("server-port", 25565));
        }
        // Spigot start
        this.a((PlayerList) (new DedicatedPlayerList(this)));
        org.spigotmc.SpigotConfig.init();
        org.spigotmc.SpigotConfig.registerCommands();
        // Spigot end

        this.getLogger().info("Generating keypair");
        this.a(MinecraftEncryption.b());
        this.getLogger().info("Starting Minecraft server on " + (this.getServerIp().length() == 0 ? "*" : this.getServerIp()) + ":" + this.G());

        try {
            // Spigot start
            this.r = ( org.spigotmc.SpigotConfig.listeners.get( 0 ).netty )
                    ? new org.spigotmc.netty.NettyServerConnection( this, inetaddress, this.G() )
                    : new DedicatedServerConnection( this, inetaddress, this.G() );
            // Spigot end
        } catch (Throwable ioexception) { // CraftBukkit - IOException -> Throwable
            this.getLogger().warning("**** FAILED TO BIND TO PORT!");
            this.getLogger().warning("The exception was: {0}", new Object[] { ioexception.toString()});
            this.getLogger().warning("Perhaps a server is already running on that port?");
            return false;
        }

        // this.a((PlayerList) (new DedicatedPlayerList(this))); // Spigot - Moved up

        if (!this.getOnlineMode()) {
            this.getLogger().warning("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
            this.getLogger().warning("The server will make no attempt to authenticate usernames. Beware.");
            this.getLogger().warning("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
            this.getLogger().warning("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
        }

        // this.a((PlayerList) (new DedicatedPlayerList(this))); // CraftBukkit - moved up
        this.convertable = new WorldLoaderServer(server.getWorldContainer()); // CraftBukkit - moved from MinecraftServer constructor
        long j = System.nanoTime();

        if (this.J() == null) {
            this.l(this.propertyManager.getString("level-name", "world"));
        }

        String s = this.propertyManager.getString("level-seed", "");
        String s1 = this.propertyManager.getString("level-type", "DEFAULT");
        String s2 = this.propertyManager.getString("generator-settings", "");
        long k = (new Random()).nextLong();

        if (s.length() > 0) {
            try {
                long l = Long.parseLong(s);

                if (l != 0L) {
                    k = l;
                }
            } catch (NumberFormatException numberformatexception) {
                k = (long) s.hashCode();
            }
        }

        WorldType worldtype = WorldType.getType(s1);

        if (worldtype == null) {
            worldtype = WorldType.NORMAL;
        }

        this.d(this.propertyManager.getInt("max-build-height", 256));
        this.d((this.getMaxBuildHeight() + 8) / 16 * 16);
        this.d(MathHelper.a(this.getMaxBuildHeight(), 64, 256));
        this.propertyManager.a("max-build-height", Integer.valueOf(this.getMaxBuildHeight()));
        this.getLogger().info("Preparing level \"" + this.J() + "\"");
        this.a(this.J(), this.J(), k, worldtype, s2);
        long i1 = System.nanoTime() - j;
        String s3 = String.format("%.3fs", new Object[] { Double.valueOf((double) i1 / 1.0E9D)});

        this.getLogger().info("Done (" + s3 + ")! For help, type \"help\" or \"?\"");
        if (this.propertyManager.getBoolean("enable-query", false)) {
            this.getLogger().info("Starting GS4 status listener");
            this.m = new RemoteStatusListener(this);
            this.m.a();
        }

        if (this.propertyManager.getBoolean("enable-rcon", false)) {
            this.getLogger().info("Starting remote control listener");
            this.n = new RemoteControlListener(this);
            this.n.a();
            this.remoteConsole = new org.bukkit.craftbukkit.command.CraftRemoteConsoleCommandSender(); // CraftBukkit
        }

        // CraftBukkit start
        if (this.server.getBukkitSpawnRadius() > -1) {
            this.getLogger().info("'settings.spawn-radius' in bukkit.yml has been moved to 'spawn-protection' in server.properties. I will move your config for you.");
            this.propertyManager.properties.remove("spawn-protection");
            this.propertyManager.getInt("spawn-protection", this.server.getBukkitSpawnRadius());
            this.server.removeBukkitSpawnRadius();
            this.propertyManager.savePropertiesFile();
        }

        return true;
    }

    public PropertyManager getPropertyManager() {
        return this.propertyManager;
    }
    // CraftBukkit end

    public boolean getGenerateStructures() {
        return this.generateStructures;
    }

    public EnumGamemode getGamemode() {
        return this.q;
    }

    public int getDifficulty() {
        return Math.max(0, Math.min(3, this.propertyManager.getInt("difficulty", 1))); // CraftBukkit - clamp values
    }

    public boolean isHardcore() {
        return this.propertyManager.getBoolean("hardcore", false);
    }

    protected void a(CrashReport crashreport) {
        while (this.isRunning()) {
            this.an();

            try {
                Thread.sleep(10L);
            } catch (InterruptedException interruptedexception) {
                interruptedexception.printStackTrace();
            }
        }
    }

    public CrashReport b(CrashReport crashreport) {
        crashreport = super.b(crashreport);
        crashreport.g().a("Is Modded", (Callable) (new CrashReportModded(this)));
        crashreport.g().a("Type", (Callable) (new CrashReportType(this)));
        return crashreport;
    }

    protected void p() {
        System.exit(0);
    }

    public void r() { // CraftBukkit - protected -> public
        super.r();
        this.an();
    }

    public boolean getAllowNether() {
        return this.propertyManager.getBoolean("allow-nether", true);
    }

    public boolean getSpawnMonsters() {
        return this.propertyManager.getBoolean("spawn-monsters", true);
    }

    public void a(MojangStatisticsGenerator mojangstatisticsgenerator) {
        mojangstatisticsgenerator.a("whitelist_enabled", Boolean.valueOf(this.ao().getHasWhitelist()));
        mojangstatisticsgenerator.a("whitelist_count", Integer.valueOf(this.ao().getWhitelisted().size()));
        super.a(mojangstatisticsgenerator);
    }

    public boolean getSnooperEnabled() {
        return this.propertyManager.getBoolean("snooper-enabled", true);
    }

    public void issueCommand(String s, ICommandListener icommandlistener) {
        this.k.add(new ServerCommand(s, icommandlistener));
    }

    public void an() {
        while (!this.k.isEmpty()) {
            ServerCommand servercommand = (ServerCommand) this.k.remove(0);

            // CraftBukkit start - ServerCommand for preprocessing
            ServerCommandEvent event = new ServerCommandEvent(this.console, servercommand.command);
            this.server.getPluginManager().callEvent(event);
            servercommand = new ServerCommand(event.getCommand(), servercommand.source);

            // this.getCommandHandler().a(servercommand.source, servercommand.command); // Called in dispatchServerCommand
            this.server.dispatchServerCommand(this.console, servercommand);
            // CraftBukkit end
        }
    }

    public boolean T() {
        return true;
    }

    public DedicatedPlayerList ao() {
        return (DedicatedPlayerList) super.getPlayerList();
    }

    public ServerConnection ae() {
        return this.r;
    }

    public int a(String s, int i) {
        return this.propertyManager.getInt(s, i);
    }

    public String a(String s, String s1) {
        return this.propertyManager.getString(s, s1);
    }

    public boolean a(String s, boolean flag) {
        return this.propertyManager.getBoolean(s, flag);
    }

    public void a(String s, Object object) {
        this.propertyManager.a(s, object);
    }

    public void a() {
        this.propertyManager.savePropertiesFile();
    }

    public String b_() {
        File file1 = this.propertyManager.c();

        return file1 != null ? file1.getAbsolutePath() : "No settings file";
    }

    public void ap() {
        ServerGUI.a(this);
        this.s = true;
    }

    public boolean ag() {
        return this.s;
    }

    public String a(EnumGamemode enumgamemode, boolean flag) {
        return "";
    }

    public boolean getEnableCommandBlock() {
        return this.propertyManager.getBoolean("enable-command-block", false);
    }

    public int getSpawnProtection() {
        return this.propertyManager.getInt("spawn-protection", super.getSpawnProtection());
    }

    public boolean a(World world, int i, int j, int k, EntityHuman entityhuman) {
        if (world.worldProvider.dimension != 0) {
            return false;
        } else if (this.ao().getOPs().isEmpty()) {
            return false;
        } else if (this.ao().isOp(entityhuman.name)) {
            return false;
        } else if (this.getSpawnProtection() <= 0) {
            return false;
        } else {
            ChunkCoordinates chunkcoordinates = world.getSpawn();
            int l = MathHelper.a(i - chunkcoordinates.x);
            int i1 = MathHelper.a(k - chunkcoordinates.z);
            int j1 = Math.max(l, i1);

            return j1 <= this.getSpawnProtection();
        }
    }

    public IConsoleLogManager getLogger() {
        return this.l;
    }

    public PlayerList getPlayerList() {
        return this.ao();
    }
}
