# V8 — Minecraft pathogen DATA-field performance and integrity

The 15 named pathogen channels remain fictional Minecraft **numeric signals** in 4×4 horizontal game areas, not infectious virions, microbes, or additional Minecraft blocks/entities. The user requested ongoing calculation-system optimization before illness effects; no new medical/infection algorithms are added here.

## Confirmed implementation changes

**Route-event allocation hot spot:** the old server `PathogenSpatialFieldSavedData.transfer` called the pure `PathogenRouteGameMath.move` on each animal/player/environment event, creating and deep-copying the entire 1,024-cell field multiple times for each one-unit event. The new `canMove` method checks exactly two existing source/target area records and all previous route/cap/distance guards in O(1). The server-owned SavedData then changes exactly the two affected integer counters **in-place**, optionally creating/removing one empty 15-channel area. The old pure copy-based `move` remains for deterministic reference tests, and a new Java21 test checks that fast eligibility and the full-copy reference agree over randomized populations of 1, 16, 128 and 1,024 game cells.

**Save safety:** field state is now held in a mutable private HashMap even after the snapshot-oriented area update. The field copies result arrays into its own map, so modifying an external `Frame` result cannot modify saved game data. Route-scan cursors and position-scan cursors remain persisted, including when only the simulation cursor advances without moving a value. Saved data validates numeric channel indices and ignores same-level seed writes.

**Channel scheduling fairness:** in addition to scanning different occupied game areas across time, a region's first considered pathogen *label* rotates through all 15 game channels. This avoids permanent dominance of a low-index label where only one virtual ambient game action is allowed per scanned area per frame.

**Unchanged boundaries:** No true infection rates, biological growth, actual disease transmission across players/animals or item contamination, and no game item newly stores contagious biological data. Existing normalized transfer is a data move in Minecraft, not a practical pathogen spread model.

Full Gradle/NeoForge build and dedicated-server/graphical-client smoke must be re-run. Actual Minecraft save/reload, multiplayer movement trails and cross-dimension integration still need an in-world acceptance test before a release.
