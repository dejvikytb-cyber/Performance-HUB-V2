package cz.perfhub;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

/** Minimal diagnostic overlay; intentionally does no per-frame allocations beyond displayed strings. */
public final class PerformanceHud {
    private PerformanceHud() {}

    public static void register() {
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(PerfHubClient.MOD_ID, "performance_hud"), (graphics, tickCounter) -> {
            PerfConfig cfg = PerfConfig.get();
            Minecraft client = Minecraft.getInstance();
            if (!cfg.performanceHud || client.level == null) return;

            int fps = client.getFps();
            long usedMb = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024L * 1024L);
            long maxMb = Runtime.getRuntime().maxMemory() / (1024L * 1024L);
            int distance = client.options.renderDistance().get();
            int color = fps < 45 ? 0xFFFF6868 : fps < 90 ? 0xFFFFD166 : 0xFF75F0A0;
            int x = 6;
            int y = 6;
            graphics.fill(x - 3, y - 3, x + 112, y + 34, 0x99090D12);
            graphics.drawString(client.font, "PERFORMANCE HUB", x, y, 0xFF75F0A0, false);
            graphics.drawString(client.font, "FPS " + fps + "  RD " + distance, x, y + 11, color, false);
            graphics.drawString(client.font, "RAM " + usedMb + "/" + maxMb + " MB", x, y + 22, 0xFFE5E7EB, false);
        });
    }
}
