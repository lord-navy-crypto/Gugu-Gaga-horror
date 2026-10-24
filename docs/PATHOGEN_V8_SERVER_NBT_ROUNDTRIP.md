# NeoForge actual server-side NBT round-trip verification

An additional **CI-only** server-started event, gated by environment variable `GUGU_FIELD_SELFTEST=1`, creates two ephemeral in-memory SavedData instances using the **real Minecraft HolderLookup registry**. The test initializes independent 15-channel per-position game numbers, advances the region routing cursor, serializes to native Minecraft NBT, calls the actual game-field NBT load handler, performs a bounded numeric game-route transfer, serializes and reloads again, and verifies all numbers, cursor state, independent channels, no aliasing, and empty cell cleanup.

The test does NOT call `world.getDataStorage()` and cannot modify a user save. `qa/smoke_server_boot.py` will only pass when both the normal NeoForge ready markers and the special `GUGUGAGA_SPATIAL_SAVEDDATA_ROUNDTRIP_PASS` marker are logged in an ephemeral CI server. If the test crashes, the existing server boot CI fails. Outside CI, the startup handler is inert.

This validates NBT serialization/deserialization in a real NeoForge server process, but still does not prove save-file persistence across killing and restarting an actual populated world, multiplayer interaction or cross-chunk visual UI in a client. Those remain separate acceptance criteria. No special pathogen Block, entity, infection or biological transmission method is introduced.
