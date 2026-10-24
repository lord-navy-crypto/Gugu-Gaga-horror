# V19 — Minecraft environment observations and verified symbolic transport

## Objective
Improve the **observed world context** and the **numerical reliability** of the existing 15+8 identity-labeled Minecraft game data field, while staying precise about what can and cannot be claimed scientifically.

A real organism's identity may appear as an educational catalog label. The existing **0–15 per-cell numbers are game indicators, not measured concentration, infectious dose, replication, incidence, particle count, or realistic transmission risk**. The updated code must not derive organism-specific infection probabilities or biological coefficients from game data.

## 1. Vanilla environment layer: actual observations
New `FieldEnvironmentRuntime.observe(ServerLevel, BlockPos)` reads, when the chunk is loaded:
- Minecraft water fluid state at the sampled block
- farmland directly below
- leaves and standard vegetation/crop blocks at the sampled block
- local rain, sky visibility, and sky-light level 0..15
- biome's **Minecraft base-temperature index** (not degrees Celsius, not real weather and not organism fitness)

`FieldEnvironmentObservation` assigns one descriptive context (`AQUATIC`, `CULTIVATED`, `VEGETATED`, `EXPOSED`, `SHELTERED`, or `UNLOADED`). It **never** modifies the world. There is no environmental risk score, implied infected animal or human, or invented organism-specific response coefficient.

The existing `/gugugaga field status` command now reports both the current fictional field and the **real Minecraft observation at the player's block**. This is one position, **not a biome-wide average, humidity measurement or whole-cell survey**.

## 2. Computing efficiency and correct dimension clocks
The primary numerical field now gets `world.getGameTime()` separately for each dimension, rather than applying the Overworld's clock to all dimensions. This preserves the server tick interval policy while respecting individual dimension clocks.

The field transport and optional game signal wave share a **per-frame bounded cache** for chunk-loaded and near-player eligibility checks. The cache is discarded immediately after the frame; it is never serialized or held across changes in chunk/player locations. At most 4,096 cached region-eligibility booleans per checked dimension/frame.

Existing independent saved files (`gugugaga_spatial_pathogen_field_v1`, `gugugaga_ecology_extension_v1`) and all 15+8 channels remain untouched.

## 3. New numerical and API verification
- `FieldEnvironmentObservationSmoke`: classification priority, unavailable observations, local rain, temperature index labeling, invalid NaN and sky-light bounds.
- `FieldGameNumericsSmoke`: 150 game frames of sparse game-indicator movement and arbitrary game fading; accounting checks that **only game fading decreases the sum** and symbolic transport never creates new numbers, with per-channel constraints. It checks deterministic results from reverse insertion order, active-region masking, negative-coordinate floor division, the 64-source budget, and the 1,024-cell/0..15 caps.
- `qa/test_v19_minecraft_environment_wiring.py`: source-level guard that reads are Minecraft-native and that no mutation/infection routines appear in the new observational layer.
- Both Java 21 smoke tests are now mandatory in `.github/workflows/build.yml`, alongside the native NeoForge compilation, dedicated-server, graphical client, and two-process world-restart jobs.

## 4. Scientific boundary and next research validation
A correct numerical accounting ledger and observations of real vanilla blocks make the **Minecraft simulation more reliable**. They do not demonstrate biological predictive validity, true airborne or waterborne dispersal, any pathogenic infection or ecological growth dynamics. In particular, the 4×4 XZ game grid is 2-D and cannot distinguish caves and mountaintops by height; the environment observation at the player is only a single voxel.

For any serious claim about real transmission accuracy, external matched measured data, appropriate scales, explicit mechanistic assumptions, held-out validation and uncertainty analysis would be needed. **None of those have been established by this mod.** Do not label the 0..15 game scores as a real health hazard or animal infection.

### References
- CDC Center for Forecasting and Outbreak Analytics, *How to assess a modeling framework and its results* (2026): https://www.cdc.gov/cfa-modeling-and-forecasting/modeling-handbook/mh-modelappraisal.html
- NeoForge Minecraft 1.21.1 `Biome` API (base temperature / precipitation): https://lexxie.dev/neoforge/1.21.1/net/minecraft/world/level/biome/Biome.html
- NeoForge in-world fluid state overview: https://docs.neoforged.net/docs/fluids/inworld/

## Manual Minecraft checks remaining
Verify via actual local graphics clients in a disposable world: crop field, lake, leaf blocks, covered mine, cave, uncovered daytime sky, daylight/night, raining outside versus under a roof, Nether, unknown chunks, different biomes and two different players with independent viewer toggles. Capture console output of `field status`, verify no new chunks loaded, then profile TPS and memory with and without the numeric field enabled. Do not treat isolated CPU benchmark smoke results as multiplayer load measurements.

**Keep PR #44 Draft, without merging into main, until exact HEAD has 4/4 GitHub checks passing and player-visible environment reports checked.**
