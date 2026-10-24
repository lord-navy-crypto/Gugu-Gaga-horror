# Pathogen Simulation Mod — Competitor Research and Next Architecture

Date: October 24, 2026. Target runtime: Minecraft Java 1.21.1, NeoForge 21.1.x.

## Decision

Keep real-world **pathogen taxonomy and qualitative educational context** separate from Minecraft-only integer and visual mechanics. All fifteen labels have a sparse spatial information field (0..15 invented game units per 4x4 area, max 1024 active cells/dimension). The field is persisted in world SavedData, not as new living pathogen Entities, physical virus items, or special pathogen Blocks. These game units are not measured biological concentrations, dose, reproduction, survival time, clinical severity, or infection probabilities. No real-world pathogenic spread or experiments are derived from this system.

Focus next development on a small, explainable world-ecology simulator with reversible cues and robust modpack performance, not new laboratory, medical or boss-mob features.

## Minecraft competitor findings (verified original project pages)

| Project | Compatibility / observation | Worth learning | Do not copy |
|---|---|---|---|
| BioForge 2.2 — https://www.curseforge.com/minecraft/mc-mods/bioforge | NeoForge 1.21.1 release dated October 18 2026; modular/data-driven project, tools and research progression | Configurable modules, validated datapack definitions, extensibility, readable diagnostics, robust migration | Its medical laboratory apparatus and complex biomedical systems; this project prioritizes the world information field |
| Diseases: A crude world — https://www.curseforge.com/minecraft/mc-mods/diseases-a-crude-world | NeoForge 1.21.1 release dated October 3 2026; compact HUD and configurable scenarios | In-world discoverability, presets, separated visual/performance switches, command completion | Its clinical/treatment gameplay and real-world efficacy claims |
| Fungal Infection: Spore — https://www.curseforge.com/minecraft/mc-mods/fungal-infection-spore | 1.21.1 NeoForge release; stages of striking world-visible change | Clear gradual environmental art cues | Dense new mob ecosystems and pervasive block replacement |
| Sculk Horde — https://modrinth.com/mod/sculk-horde and https://github.com/TeamPeril/Sculk-Horde | Forge versions 1.16–1.20, not a direct drop-in 1.21.1 NeoForge mod; project values reversible world change | Visible regional status, reversibility, fairness and large-scale systems | Core invasion terrain-block transformation |
| Scape and Run: Parasites — https://www.curseforge.com/minecraft/mc-mods/scape-and-run-parasites | Forge 1.12.2, primarily host-specific monsters | Distinguish categories and progression visually | Overproduction of entities and copying legacy architecture |
| The Hordes — https://modrinth.com/mod/the-hordes | Optional rules and variants for vanilla zombie scenarios | Independent toggles and sandbox balance | Reproducing player-killing infection stages |

Plague Inc. (https://www.plagueinc.com/) is useful only as an example of legible abstract **maps and high-level reports**, not as a source of accurate epidemiological mechanics to port. Its explicit game objective is incompatible with this mod's gentle observation-first environmental simulation.

## Authoritative scientific framing

WHO One Health (https://www.who.int/news-room/fact-sheets/detail/one-health) and CDC One Health (https://www.cdc.gov/onehealth/index.html) link animals, people, crops and environments. That justifies separate Minecraft observers for animals, plants and players reading a *shared* information field. It does NOT justify turning every observed game signal into a diagnosed infection or exposing operational parameters. The existing roster taxon labels and host mappings should retain citations and clearly mark beetroots and generic vanilla tree leaves as visual proxies.

## API/version decisions

- Official **NeoForge 1.21.1 SavedData** guide: https://docs.neoforged.net/docs/1.21.1/datastorage/saveddata/ . Maintain existing per-dimension world storage; call setDirty when changed. Do not accidentally replace old 1.21.1 factory APIs with 1.21.5 SavedDataType APIs without upgrading.
- Official **NeoForge 1.21.1 Data Attachments**: https://docs.neoforged.net/docs/1.21.1/datastorage/attachments/ . Optional per-entity game display status can use attachments; use vanilla Data Components if optional item-local cosmetic tags are ever required. Neither should own the authoritative per-position field.
- Official reload listeners: https://docs.neoforged.net/docs/resources/reloadlisteners/ . A future schema-validated, versioned JSON game presentation profile should reload atomically. Invalid input must preserve last-known-valid definitions and saved worlds.
- Rendering: the server owns calculations. The client receives only sparse nearby game data when requested, not all 15 channels for every remote chunk each tick. Keep particle and UI effects independently configurable.

## Priority and acceptance

P0 (implemented in this PR): **/gugugaga field overview** — one command gives the number of locally active channels, five highest symbolic readings and a seven-by-seven summary of all channels (each square shows the maximum game value, displayed as 0..F). No new world blocks or entities; readable when no fields exist. Add Java 21 and Python regression tests.

P1: Data-debug overlay that shows source position, sampled region ID, route-event counters and last update time in a small HUD. Opt-in and throttled, no intrusive effects.

P2: Versioned JSON scene/presentation profiles with strict validation and non-destructive reload; scientific identity and invented gameplay rules must be separate.

P3: Better observational scenes on existing potatoes, melon/pumpkin stems, chickens, wolves and vanilla tree leaves. Signal presence should create generic ecological visual observations only, not automatic biological diagnosis. Keep reversible toggles.

P4: Dedicated integration gates: save physical world to disk; close JVM, restart same world; check area and cursor state; test multi-player overlap without duplicate scans; test cross-chunk boundaries; measure server frame cost at 1024 occupied cells and cap client overlays. Native NeoForge compile/boot alone does not prove these manual game workflows.

Avoid real pathogenic replication parameters, transmission-rate calibration, experimental cultivation/engineering features or human-to-human infection modeling. Gameplay examples should use arbitrary virtual information exchange.

## What distinguishes Gugu-Gaga

**Other mods often visualize infection by adding monsters, transforming terrain or building medical equipment. Gugu-Gaga should visualize the hidden world of existing Minecraft ecosystems without replacing them.** Shared field data is primary; host status, player HUD and flora visuals are only separate readers. This avoids a duplicated world state and provides a clear low-overhead identity for the mod.
