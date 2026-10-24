package cz.perfhub;

import net.minecraft.client.CloudStatus;
import net.minecraft.client.GraphicsPreset;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.server.level.ParticleStatus;

/** Aplikace profilů, záloha a obnova původního nastavení. */
public final class PerfApply {
    private PerfApply() {}

    public static void apply(Profile p) {
        Options o = Minecraft.getInstance().options;
        backupIfNeeded(o);

        o.renderDistance().set(p.renderDistance);
        o.simulationDistance().set(p.simulationDistance);
        o.framerateLimit().set(p.fpsLimit);
        o.mipmapLevels().set(p.mipmaps);
        o.biomeBlendRadius().set(p.biomeBlend);
        o.particles().set(p.particles);
        o.graphicsPreset().set(p.graphics);
        o.cloudStatus().set(p.clouds);
        o.entityDistanceScaling().set(p.entityDistance);
        o.entityShadows().set(p.entityShadows);
        o.enableVsync().set(p.vsync);
        o.textBackgroundOpacity().set(p.textBackground);
        o.save();
    }

    private static void backupIfNeeded(Options o) {
        PerfConfig cfg = PerfConfig.get();
        if (cfg.original != null) return;
        PerfConfig.Snapshot s = new PerfConfig.Snapshot();
        s.renderDistance = o.renderDistance().get();
        s.simulationDistance = o.simulationDistance().get();
        s.fpsLimit = o.framerateLimit().get();
        s.mipmaps = o.mipmapLevels().get();
        s.biomeBlend = o.biomeBlendRadius().get();
        s.particles = o.particles().get().name();
        s.graphics = o.graphicsPreset().get().name();
        s.clouds = o.cloudStatus().get().name();
        s.entityDistance = o.entityDistanceScaling().get();
        s.entityShadows = o.entityShadows().get();
        s.vsync = o.enableVsync().get();
        s.textBackground = o.textBackgroundOpacity().get();
        s.ambientOcclusion = o.ambientOcclusion().get();
        s.improvedTransparency = o.improvedTransparency().get();
        s.vignette = o.vignette().get();
        s.cutoutLeaves = o.cutoutLeaves().get();
        cfg.original = s;
        PerfConfig.save();
    }

    /** @return true, pokud existovala záloha a byla obnovena. */
    public static boolean restoreOriginal() {
        PerfConfig.Snapshot s = PerfConfig.get().original;
        if (s == null) return false;
        Options o = Minecraft.getInstance().options;
        o.renderDistance().set(s.renderDistance);
        o.simulationDistance().set(s.simulationDistance);
        o.framerateLimit().set(s.fpsLimit);
        o.mipmapLevels().set(s.mipmaps);
        o.biomeBlendRadius().set(s.biomeBlend);
        o.particles().set(ParticleStatus.valueOf(s.particles));
        o.graphicsPreset().set(GraphicsPreset.valueOf(s.graphics));
        o.cloudStatus().set(CloudStatus.valueOf(s.clouds));
        o.entityDistanceScaling().set(s.entityDistance);
        o.entityShadows().set(s.entityShadows);
        o.enableVsync().set(s.vsync);
        o.textBackgroundOpacity().set(s.textBackground);
        o.ambientOcclusion().set(s.ambientOcclusion);
        o.improvedTransparency().set(s.improvedTransparency);
        o.vignette().set(s.vignette);
        o.cutoutLeaves().set(s.cutoutLeaves);
        o.save();
        return true;
    }

    public static void onStartup() {
        PerfConfig cfg = PerfConfig.get();
        if (cfg.autoProfileOnFirstRun && !cfg.firstRunDone) {
            try {
                apply(Hardware.get().recommended);
            } catch (Throwable t) {
                PerfHubClient.LOGGER.warn("Automatický profil se nepodařilo použít.", t);
            }
            cfg.firstRunDone = true;
            PerfConfig.save();
        }
    }
}
