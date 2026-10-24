# Performance Hub (Fabric, Minecraft 1.21.11)

Klientský mod pro rychlé profily a výkonové nastavení. GUI otevřeš klávesou **Right Shift**, diagnostický HUD přepínáš **F8** (obě klávesy lze změnit v Ovládání).

## Funkce
- Profily Competitive / max FPS, Balanced a Visuals.
- Video, vzdálenost světa, entity, částice a nastavení kvality dostupná v 1.21.11.
- Volitelný adaptivní režim: každých 5 sekund sleduje FPS a po několika vzorcích po jednom kroku upraví render distance v nastavených mezích. Je vypnutý ve výchozím stavu a lze jej zapnout v Pokročilé.
- Lehký diagnostický HUD: FPS, render distance a využití Java heapu.
- Před prvním použitím profilu zálohuje původní vanilla nastavení.

## Důležité limity
Mod nemůže přepnout Minecraft 1.21.11 z OpenGL na DirectX/Vulkan pouhou změnou nastavení. To vyžaduje jiný renderovací backend. Tento mod zatím také necachuje vanilla HUD do framebufferu: takový zásah vyžaduje opatrné mixiny a může rozbít chat, crosshair, status efekty nebo HUD jiných modů. Pro existující optimalizaci HUD se podívej na Gnetum; pro celkové vykreslování na Sodium a ImmediatelyFast (ověř kompatibilitu konkrétních verzí).

## Build přes GitHub Actions
1. Nahraj celý obsah této složky do repozitáře.
2. Otevři Actions a počkej na build.
3. Hotový JAR stáhni z Artifacts.
4. Použij Fabric Loader, Fabric API pro 1.21.11 a Java 21.
