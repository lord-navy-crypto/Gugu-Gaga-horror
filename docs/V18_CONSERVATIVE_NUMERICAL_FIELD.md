# V18: mathematically consistent symbolic field and runtime optimizations

**Scope:** This implements **numerically conserved GAME indicator movement**, not any physical concentration, environmental survival, disease transmission, infection probability, or real pathogen-specific coefficient. The 15 original and eight extension channel identifiers remain sourced biological taxonomy reference labels; only one generic 0..15 Minecraft signal ledger is being audited.

## Why the existing simulation was inaccurate

`ViralGameSignalWave.advance` previously added a destination score without removing that score from its source. Across ticks, those fictional indicator units could be repeatedly duplicated. For a scalar meant to represent conserved display intensity, this made observed trend comparisons misleading. The corrected wave computes desired destination score using the previous *game* display policy, then transfers no more than that gain and the source's initially available units; every +N destination change has an exact -N source change. Newly created cells are not processed as sources during the same frame.

## Proven properties, limitations and performance

- Exact per-channel conservation for wave-only updates: `sum(before[channel]) == sum(after[channel])` for eight virus-labelled game channels, independent of hash insertion order. Other taxonomy channels remain unchanged.
- Hard caps: at most 64 existing regions visited per frame, 1024 region storage cap, 0..15 for each of 15 original channels, loaded-and-near-player checks in production. Empty rows are purged before returning.
- The older generic field step has an *independent intentionally authored game fade*. Conservation must be checked **per phase**: `total(afterGeneric) = total(before) - faded`; route-event steps preserve totals when they can move. Do not confuse the intentionally configured game fade with experimentally measured pathogen lifetime.
- O(1) owned-map cell `set` replaces per-write cloning of up to 1024 15-integer arrays. No-op updates avoid `SavedData.setDirty`; zero-only rows are removed. External `snapshot()` remains deep copied to prevent aliasing.
- A cursor-only generic `step` now updates fairness cursor and marks its SavedData dirty as required, but does not rehydrate the entire 1024-cell map when no numeric value has changed. Per-dimension NBT file names and channel indexes are preserved.
- These changes rely on Java 21 compile and real dedicated-server/client CI plus existing in-memory and disk-save smoke tests. The extended new mass-balance pure Java test also checks deterministic update results, boundaries and loaded-area isolation. Do not treat server startup as in-game multiplayer screenshot acceptance.

## Scientific interpretation

See CDC Center for Forecasting and Outbreak Analytics **Modeling Handbook, Model Appraisal** (https://www.cdc.gov/cfa-modeling-and-forecasting/modeling-handbook/mh-modelappraisal.html). Scientific realism requires observed, context-appropriate inputs, sensitivity analyses, validation on held-out observations, and uncertainty reporting. The new strict game-ledger invariants make this toy engine **more internally correct** but do not validate its biological assumptions or justify calling the 0..15 scores actual disease prevalence.

## Test in Minecraft

Use a separate test save with `/gugugaga field seed potato_virus_y`, `/gugugaga field map potato_virus_y`, and optionally `/gugugaga field particles select potato_virus_y`. Before/after maps can show a game indicator shift to neighboring loaded regions, but the wave step itself can no longer make extra indicator units. Additional generic fade/route rules remain independently enabled in existing server config.
