# V17 — Terrain-aware, per-player cosmetic field particles

## Purpose and strict boundary
This feature is a visualization of existing **fictional Minecraft game values**, not a model of infection, survival, dose, real dispersal, animal infection or epidemiological risk. The original 15+8 numeric channels remain in their exact existing SavedData files. No new blocks, entities, items, NBT schema, infection statuses, or pathogen coefficients are introduced by this patch.

## Render anchoring, without world mutation
The selected 7x7 region around one opted-in viewer still samples **at most 49 already-loaded 4x4 cells** and selects **up to six** non-zero markers. Particle packets still target only the requesting `ServerPlayer`.

For each marker, in order:
1. Inspect at most sixteen surface columns in that already-loaded 4x4 cell. If vanilla `CropBlock` positions are within 12 vertical blocks of the observer, align the particle over a crop.
2. Otherwise, align over an existing vanilla animal inside the same cell, if the animal is within eight vertical blocks of the observer. Animals are obtained from **one bounded nearby-entity query per viewer frame**, and receive no state changes.
3. Otherwise, position just above the heightmap surface when the surface is within the observer's 12-block height range.
4. In deep caves, check at most three already-loaded air blocks around the local player's Y coordinate. If they are solid, **skip the marker** instead of projecting through rock or placing particles on the mountain far above.

An anchor is merely the **display position** for a non-biological toy signal. Showing a marker above an animal or crop does *not* assert that it is infected or is a suitable host.

## Networking and performance
- Existing opt-in command set remains unchanged. Sessions and selected channels are isolated by server and UUID; logout, switching selection/dimension, and server stop clear applicable temporary data.
- Still at most **12 per-player send events** per sampled frame and at most **12 viewers per 60-tick frame**, with a deterministic rotation that services every opted-in observer within `ceil(online/12)` frames.
- Nonzero marker limit stays **six**, and the 49-cell scan avoids loading new chunks. The 16-column crop scan and the nearby-animal search happen only for selected markers and opted-in viewers. Height queries are pre-guarded by chunk-loaded checks.
- The new `FieldParticleViewPolicy` contains only coordinate and scheduling rules, intentionally free of game-level or biological calculations.

## Checks and limitations
The new Java21 `FieldParticleViewPolicySmoke` tests negative X/Z coordinates, near/far surface and animal boundaries, empty sessions, and fairness with 1 to 200 simulated players. Source checks enforce targeted player particle networking and crop/entity anchoring. Existing CI also builds NeoForge and performs real graphical client, dedicated server and SavedData restart checks.

**Automated validation is not a replacement for two-player visual testing.** The next manual session should confirm a cave, steep slope, open field, potatoes/wheat farmland, barn, player A ON / player B OFF, two players choosing different catalog labels, teleport across dimensions, logout/login, and high-entity-density performance. Existing legacy global particle effects are separately configured and may still be visible to everyone.

Review CI results of the exact new HEAD before declaring the V17 patch tested. Keep PR #44 Draft and leave `main` unchanged.
