package cz.perfhub;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/** Minimalistické GUI: boční lišta se sekcemi + obsah vpravo. */
public class PerfScreen extends Screen {
    private enum Tab {
        PROFILES("Profily"), VIDEO("Video"), WORLD("Svět"), ENTITIES("Entity"),
        PARTICLES("Částice"), HUD("HUD"), ADVANCED("Pokročilé");
        final String label;
        Tab(String label) { this.label = label; }
    }

    private static final int W = 400, H = 252, SIDE = 100, PAD = 12;
    private static final int ACCENT = 0xFF4ADE80, MUTED = 0xFF8A8F98, TEXT = 0xFFD7DAE0;

    private final Screen parent;
    private Tab tab = Tab.PROFILES;
    private String status = "";
    private int left, top;

    public PerfScreen(Screen parent) {
        super(Component.literal("Performance Hub"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        rebuild();
    }

    private void rebuild() {
        clearWidgets();
        left = (width - W) / 2;
        top = Math.max(4, (height - H) / 2);

        for (Tab t : Tab.values()) {
            addRenderableWidget(Button.builder(Component.literal(t.label), b -> {
                        tab = t;
                        status = "";
                        rebuild();
                    })
                    .bounds(left + PAD, top + 36 + t.ordinal() * 24, SIDE - PAD, 20).build());
        }

        int cx = left + SIDE + PAD;
        int cw = W - SIDE - 2 * PAD;
        int y = top + 36;

        switch (tab) {
            case PROFILES -> {
                for (Profile p : Profile.values()) {
                    addRenderableWidget(Button.builder(Component.literal(p.label), b -> applyProfile(p))
                            .bounds(cx, y, cw, 20).build());
                    y += 24;
                }
                Hardware hw = Hardware.get();
                addRenderableWidget(Button.builder(
                                Component.literal("Doporučeno pro tvůj HW: ")
                                        .append(Component.literal(hw.recommended.label).withStyle(ChatFormatting.GREEN)),
                                b -> applyProfile(hw.recommended))
                        .bounds(cx, y, cw, 20).build());
                y += 24;
                addRenderableWidget(Button.builder(Component.literal("Obnovit původní nastavení"), b -> {
                    status = PerfApply.restoreOriginal() ? "Původní nastavení obnoveno." : "Záloha zatím neexistuje.";
                }).bounds(cx, y, cw, 20).build());
            }
            case VIDEO -> addSettings(Settings.VIDEO, cx, y, cw);
            case WORLD -> addSettings(Settings.WORLD, cx, y, cw);
            case ENTITIES -> addSettings(Settings.ENTITIES, cx, y, cw);
            case PARTICLES -> addSettings(Settings.PARTICLES, cx, y, cw);
            case HUD -> addSettings(Settings.HUD, cx, y, cw);
            case ADVANCED -> {
                addRenderableWidget(Button.builder(autoLabel(), b -> {
                    PerfConfig cfg = PerfConfig.get();
                    cfg.autoProfileOnFirstRun = !cfg.autoProfileOnFirstRun;
                    PerfConfig.save();
                    b.setMessage(autoLabel());
                }).bounds(cx, y, cw, 20).build());
                y += 24;
                addRenderableWidget(Button.builder(adaptiveLabel(), b -> {
                    PerfConfig cfg = PerfConfig.get();
                    cfg.adaptivePerformance = !cfg.adaptivePerformance;
                    PerfConfig.save();
                    b.setMessage(adaptiveLabel());
                }).bounds(cx, y, cw, 20).build());
                y += 24;
                addRenderableWidget(Button.builder(hudLabel(), b -> {
                    PerfConfig cfg = PerfConfig.get();
                    cfg.performanceHud = !cfg.performanceHud;
                    PerfConfig.save();
                    b.setMessage(hudLabel());
                }).bounds(cx, y, cw, 20).build());
            }
        }

        addRenderableWidget(Button.builder(Component.literal("Hotovo"), b -> onClose())
                .bounds(cx, top + H - 30, cw, 20).build());
    }

    private void addSettings(List<Setting<?>> list, int x, int y, int w) {
        for (Setting<?> s : list) {
            addRenderableWidget(Button.builder(s.label(), b -> {
                s.cycle();
                b.setMessage(s.label());
            }).bounds(x, y, w, 20).build());
            y += 24;
        }
    }

    private static Component autoLabel() {
        boolean on = PerfConfig.get().autoProfileOnFirstRun;
        return toggleLabel("Auto-profil při 1. spuštění", on);
    }

    private static Component adaptiveLabel() {
        return toggleLabel("Adaptivní FPS / vzdálenost", PerfConfig.get().adaptivePerformance);
    }

    private static Component hudLabel() {
        return toggleLabel("Výkonnostní HUD (F8)", PerfConfig.get().performanceHud);
    }

    private static Component toggleLabel(String label, boolean on) {
        return Component.literal(label + "  ")
                .append(Component.literal(on ? "ZAP" : "VYP")
                        .withStyle(on ? ChatFormatting.GREEN : ChatFormatting.RED));
    }

    private void applyProfile(Profile p) {
        try {
            PerfApply.apply(p);
            status = "Použito: " + p.label;
        } catch (Throwable t) {
            PerfHubClient.LOGGER.warn("Použití profilu selhalo", t);
            status = "Chyba při použití profilu (viz log).";
        }
    }

    private String fit(String s, int maxW) {
        while (font.width(s) > maxW && s.length() > 4) s = s.substring(0, s.length() - 1);
        return s;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.fill(left - 1, top - 1, left + W + 1, top + H + 1, 0xFF2A2E35);
        g.fill(left, top, left + W, top + H, 0xF0121316);
        g.fill(left + SIDE, top + 30, left + SIDE + 1, top + H - 8, 0xFF2A2E35);

        g.drawString(font, "Performance Hub", left + PAD, top + 11, ACCENT);

        // zvýraznění aktivní sekce
        int ty = top + 36 + tab.ordinal() * 24;
        g.fill(left + 4, ty, left + 6, ty + 20, ACCENT);

        int cx = left + SIDE + PAD;
        int cw = W - SIDE - 2 * PAD;

        switch (tab) {
            case PROFILES -> {
                Hardware hw = Hardware.get();
                int y = top + 36 + 5 * 24 + 4;
                g.drawString(font, fit("CPU: " + hw.cores + " vláken | Heap Javy: " + hw.heapMb + " MB", cw), cx, y, TEXT);
                g.drawString(font, fit("GPU: " + hw.gpu, cw), cx, y + 11, TEXT);
                if (hw.heapMb < 3000) {
                    g.drawString(font, fit("Tip: přidej Javě RAM (-Xmx4G v launcheru).", cw), cx, y + 22, MUTED);
                }
            }
            case WORLD -> g.drawString(font, fit("Nižší vzdálenosti = výrazně vyšší FPS.", cw), cx, top + 36 + 3 * 24 + 4, MUTED);
            case HUD -> {
                g.drawString(font, fit("F8 zapne/vypne lehký diagnostický overlay.", cw), cx, top + 36 + 30, MUTED);
                g.drawString(font, fit("HUD cache vanilla prvků není aktivní.", cw), cx, top + 36 + 41, MUTED);
                g.drawString(font, fit("Pro optimalizaci vykreslování HUD zvaž Gnetum.", cw), cx, top + 36 + 52, MUTED);
            }
            case ADVANCED -> {
                g.drawString(font, fit("Adaptivní režim upravuje render distance podle FPS.", cw), cx, top + 36 + 78, MUTED);
                g.drawString(font, fit("HUD: F8 | Cíl: " + PerfConfig.get().targetFps + " FPS", cw), cx, top + 36 + 89, MUTED);
                g.drawString(font, fit("Původní nastavení obnovíš v sekci Profily.", cw), cx, top + 36 + 100, MUTED);
            }
            default -> { }
        }

        if (!status.isEmpty()) {
            g.drawString(font, fit(status, cw), cx, top + H - 44, ACCENT);
        }

        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        PerfConfig.save();
        if (minecraft != null) minecraft.setScreen(parent);
    }
}
