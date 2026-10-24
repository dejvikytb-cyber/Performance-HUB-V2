package cz.perfhub;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public final class PerfConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static PerfConfig instance = new PerfConfig();

    /** Při úplně prvním spuštění použít doporučený profil. */
    public boolean autoProfileOnFirstRun = true;
    public boolean firstRunDone = false;
    public boolean adaptivePerformance = false;
    public boolean performanceHud = true;
    public int targetFps = 120;
    public int adaptiveMinRenderDistance = 6;
    public int adaptiveMaxRenderDistance = 12;
    /** Záloha původního nastavení před prvním zásahem modu. */
    public Snapshot original;

    public static final class Snapshot {
        public int renderDistance, simulationDistance, fpsLimit, mipmaps, biomeBlend;
        public String particles, graphics, clouds;
        public double entityDistance, textBackground;
        public boolean entityShadows, vsync;
        public boolean ambientOcclusion, improvedTransparency, vignette, cutoutLeaves;
    }

    public static PerfConfig get() {
        return instance;
    }

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("performancehub.json");
    }

    public static void load() {
        try {
            Path p = path();
            if (Files.exists(p)) {
                PerfConfig loaded = GSON.fromJson(Files.readString(p), PerfConfig.class);
                if (loaded != null) instance = loaded;
            }
        } catch (Exception e) {
            PerfHubClient.LOGGER.warn("Konfiguraci se nepodařilo načíst, použiji výchozí.", e);
            instance = new PerfConfig();
        }
    }

    public static void save() {
        try {
            Files.writeString(path(), GSON.toJson(instance));
        } catch (Exception e) {
            PerfHubClient.LOGGER.warn("Konfiguraci se nepodařilo uložit.", e);
        }
    }
}
