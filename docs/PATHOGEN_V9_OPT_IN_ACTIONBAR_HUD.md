# V9 — Opt-in action-bar HUD (no pathogen blocks or entities)

The authoritative game environment still stores fifteen independent integer values per 4×4-block region in existing per-dimension SavedData. Each 0–15 value is a Minecraft indicator, not a pathogen concentration, transmission probability, real infection state or medical diagnosis.

## Controls

- /gugugaga field hud on: show one compact vanilla Minecraft Action Bar line, refreshed every 40 game ticks when the player is online. Shows current field region, strongest symbolic label, game level, active-label count and sum of current region game units.
- /gugugaga field hud off: turn off personal continuous updates.
- /gugugaga field hud status: report if the player is currently opted in.
- /gugugaga field overview: retain a detailed top-five plus 7x7 numeric map on request.
- /gugugaga field map <taxon>: retain individual 7x7 taxon reading.

The HUD is OFF by default for every player, including new joins. Consent is session-only, so the opt-in UUID is cleared by PlayerLoggedOutEvent; there is no permanent client setting or new gamerule. This is server-owned and one player cannot enable another player's display. The previous unsolicited game-field action bar and particle pulses were removed.

## Costs and limitations

Because Minecraft itself already supports server-to-client Action Bar text, this version uses player.displayClientMessage(..., true) rather than introducing CustomPacketPayload, a Client HUD renderer, a new block or an entity. The server performs exactly 15 numerical field reads for each opted-in player per refresh, zero for those opted out. No world-wide deep copy, forced chunks, field export or player health effect. The rendering is a single simple line, not an interactive graphical panel.

Java21 and Python tests verify bounds, label order, nonmutation, default-off opt-in/opt-out event hooks and no unsolicited overlays. NeoForge CI compiles and boots an actual client and a real dedicated server. The GUI's final position on screen and coexistence with other mods still need a manual screenshot acceptance test.

Versioned official NeoForge docs: https://docs.neoforged.net/docs/1.21.1/networking/ .
