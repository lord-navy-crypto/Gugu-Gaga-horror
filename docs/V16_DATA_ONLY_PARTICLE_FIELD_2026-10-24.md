# V16 — Minecraft numeric field rendered as opt-in, per-viewer particles

### Background and references

The published **Micro Mayhem** project describes a persistent hidden ecology under Minecraft worlds (https://www.curseforge.com/minecraft/mc-mods/micromayhem). We borrow the *architectural goal* of world-owned state separate from graphical cues, not any biological kinetics or medical effects. NeoForge **1.21.1 official particles documentation** (https://docs.neoforged.net/docs/1.21.1/resources/client/particles/) supports server-pushed particles, including the ServerLevel per-player sendParticles signature documented in https://lexxie.dev/neoforge/1.21.1/net/minecraft/server/level/ServerLevel.html .

### Implementation, not new blocks

- 15 existing source-labelled numeric channels and 8 extension channels remain stored in their original **unchanged SavedData/NBT files**. No block, entity, BlockEntity, new virtual-material item, or extra world-save schema is introduced.
- 1 game block approximates 1 metre for **display only**. The projection uses 4x4-block XZ region centres and floats near the observer's height. No surface placement, chunk generation or block replacement occurs.
- Each observer must actively enable `/gugugaga field particles on` (default selected taxon potato_virus_y) or directly use `/gugugaga field particles select <taxon>` (which opts that player in). `/gugugaga field particles off` clears personal state, and `status` reports current focus.
- Choice supports all **22 physical-world-visible** taxon identity references across original and extension world fields; the 23rd reference is a virtual-only bacteriophage and correctly has no in-world particle projection.
- Particle style uses an exact focus-five mapping (avian ENCHANT, potato HAPPY_VILLAGER, Salmonella CLOUD, wheat COMPOSTER, Varroa END_ROD) with sensible **category-level fallbacks** for other 17 labels. Every effect uses **vanilla client particles** sent to the requesting player, not custom infectious mobs.
- Every 60 nominal world game ticks the viewer samples at most 49 loaded nearby XZ cells from only **one selected channel**. Display is capped at 6 highest non-zero cells and at most 12 actual particle events per viewer. Server samples at most 12 opted-in observers per cycle with round-robin fairness. No player names/selection data are persisted to NBT; subscription ends on logout or server shutdown.
- Since the observer stores the previous sparse local snapshot (at most 49 positions), a **new or increased** game value receives one additional END_ROD sparkle next to its regular identity-category particle. Stable or decreasing values show only the base indicator. This is a visual change detector, not a physical dispersal or infection mechanism.
- The old generic survival/scene particle emitters remain separate legacy functionality (they may overlap visually in a test world). This new viewer never changes pathogen numbers, entity health, attributes, blocks or other players' preferences.

### Verification

`FieldParticleSamplingSmoke` checks the 22 viewer labels, virtual-only phage exclusion, all families, bounded 7x7 samples, six marker ceiling, zero unneeded chunk reads, stateless input, stable second scan and one sample decreasing value. `qa/test_field_particle_overlay_wiring.py` asserts opt-in lifecycle, targeted networking, caps and original-save compatibility. Full Gradle compile, native dedicated-server startup, graphical client startup and two-process world-save restart still run in Actions.

### Minecraft in-world checks

Run in a disposable world: `/gugugaga field seed potato_virus_y` near vanilla potatoes; then `/gugugaga field particles select potato_virus_y`. Observe small friendly particles around nearby numeric-area centres, including brighter sparkles when a regional score rises. Switch to `/gugugaga field particles select puccinia_graminis` for the eight-channel extension and use `/gugugaga ecology seed puccinia_graminis` in the same area if needed. Verify `/gugugaga field particles off` hides only the viewer's own projection. See whether existing legacy host cues also emit particles (they are independently configured), and use two separate clients to validate per-player isolation and particle visibility. No special blocks or virus mobs should spawn.
