package net.minecraft.server;

// Spigot start
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
// Spigot end
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.IllegalFormatException;
import java.util.Properties;
import java.util.TreeMap;

public class LocaleLanguage {

    // Spigot - cache languages to prevent reloading on each player creation
    private static Cache<String,Properties> languages = CacheBuilder.newBuilder().weakValues().build(
        new CacheLoader<String, Properties>() {
            public Properties load(String key) {
                return loadLanguage(key);
            }
        });
    private static LocaleLanguage a = new LocaleLanguage("en_US");
    private volatile Properties b = new Properties(); // Spigot - volatile
    private static TreeMap c; // Spigot - static
    // private TreeMap d = new TreeMap(); // Spigot - Unused map
    private String e;
    // private boolean f; // Spigot - removed
    static { e(); } // Spigot - initializer

    public LocaleLanguage(String s) {
        // this.e(); // Spigot: moved up
        this.a(s, false);
    }

    public static LocaleLanguage a() {
        return a;
    }

    private static void e() { // Spigot - static
        TreeMap treemap = new TreeMap();

        try {
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(LocaleLanguage.class.getResourceAsStream("/lang/languages.txt"), "UTF-8"));

            for (String s = bufferedreader.readLine(); s != null; s = bufferedreader.readLine()) {
                String[] astring = s.trim().split("=");

                if (astring != null && astring.length == 2) {
                    treemap.put(astring[0], astring[1]);
                }
            }
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
            return;
        }

        c = treemap; // Spigot - this => static
        c.put("en_US", "English (US)"); // Spigot - this => static
    }

    public TreeMap b() {
        return this.c;
    }

    private static void a(Properties properties, String s) throws IOException { // Spigot - static
        BufferedReader bufferedreader = null;

        /* Spigot - unused map
        if (this.d.containsKey(s)) {
            bufferedreader = new BufferedReader(new FileReader((File) this.d.get(s)));
        } else {
        */
            bufferedreader = new BufferedReader(new InputStreamReader(LocaleLanguage.class.getResourceAsStream("/lang/" + s + ".lang"), "UTF-8"));
        //} // Spigot: unused map
        try { // Spigot: close reader
        for (String s1 = bufferedreader.readLine(); s1 != null; s1 = bufferedreader.readLine()) {
            s1 = s1.trim();
            if (!s1.startsWith("#")) {
                String[] astring = s1.split("=");

                if (astring != null && astring.length == 2) {
                    properties.setProperty(astring[0], astring[1]);
                }
            }
        }
        } finally { bufferedreader.close(); } // Spigot - close reader
    }

    public synchronized void a(String s, boolean flag) {
        if (flag || !s.equals(this.e)) {
            // Spigot start - Move loading code to new static method
            this.e = s;
            this.b = languages.getUnchecked(s);
        }
    }
    
    private static Properties loadLanguage(String s) {
            // Spigot end
            Properties properties = new Properties();

            try {
                a(properties, "en_US"); // Spigot - this => static
            } catch (IOException ioexception) {
                ;
            }

            // this.f = false; // Spigot - removed variable
            if (!"en_US".equals(s)) {
                try {
                    a(properties, s); // Spigot - this => static
                    /* Spigot - f is unused, so unneeded code
                    Enumeration enumeration = properties.propertyNames();

                    while (enumeration.hasMoreElements() && !this.f) {
                        Object object = enumeration.nextElement();
                        Object object1 = properties.get(object);

                        if (object1 != null) {
                            String s1 = object1.toString();

                            for (int i = 0; i < s1.length(); ++i) {
                                if (s1.charAt(i) >= 256) {
                                    this.f = true;
                                    break;
                                }
                            }
                        }
                    }
                    */
                } catch (IOException ioexception1) {
                    ioexception1.printStackTrace();
                    //return; // Spigot - moved down
                }
            }
            return properties; // Spigot - return properties
            /* Spigot - moved up
            this.e = s;
            this.b = properties;
        }
        */
    }

    public String a(String s) { // Spigot - removed synchronized, b is volatile
        return this.b.getProperty(s, s);
    }

    public String a(String s, Object... aobject) { // Spigot - removed synchronized, b is volatile
        String s1 = this.b.getProperty(s, s);

        try {
            return String.format(s1, aobject);
        } catch (IllegalFormatException illegalformatexception) {
            return "Format error: " + s1;
        }
    }

    public boolean b(String s) { // Spigot - removed synchronized, b is volatile
        return this.b.containsKey(s);
    }

    public String c(String s) { // Spigot - removed synchronized, b is volatile
        return this.b.getProperty(s + ".name", "");
    }
}
