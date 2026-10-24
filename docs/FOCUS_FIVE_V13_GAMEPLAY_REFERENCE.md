# V13 — Five detailed Minecraft ecology reference scenarios

Target: Minecraft Java 1.21.1 NeoForge. These are real scientific names but fictional Minecraft 0–15 environmental game indicators, NOT actual infectious material, infection status, measured pathogen levels, spread rates, viability or replication. One Minecraft block is treated as about one metre for visual spacing, not a real physics/epidemiology model.

## Five focused profiles

| Identity | Category | Vanilla context | Minecraft-only art cue | Public educational reference |
|---|---|---|---|---|
| Influenza A virus | Virus | Chicken/parrot | Bird glow (ENCHANT) | https://www.cdc.gov/flu-in-animals/about/index.html |
| Potato virus Y | Virus | Potato crop | Plant mosaic sparkle (HAPPY_VILLAGER) | https://www.vegetables.cornell.edu/pest-management/disease-factsheets/virus-and-viroid-diseases-of-potato/ |
| Salmonella enterica | Bacterium | Chicken/cow/pig | Farm trace cloud (CLOUD) | https://www.cdc.gov/salmonella/spread/ |
| Puccinia graminis | Fungus | Wheat crop | Wheat sparkle (COMPOSTER) | https://www.ars.usda.gov/midwest-area/stpaul/cereal-disease-lab/docs/cereal-rusts/wheat-stem-rust/ |
| Varroa destructor | Ectoparasitic mite | Vanilla bee (visual proxy) | Tiny bee glow (END_ROD) | https://www.woah.org/fileadmin/Home/eng/Health_standards/tahc/2024/en_chapitre_varroa_spp.htm |

Identity cautions: Influenza A is a diverse virus category, not necessarily A(H5N1); the vanilla birds are a game subset. Salmonella enterica is not strain-level classification. Minecraft bees cannot model the species/colonies of real honey bees; their association is a visual proxy. Crop status is not a lab diagnosis.

## Implemented details

FocusFiveProfiles validates exact existing original-15 or extension-8 channel identity and vanilla host targets. FocusFiveReadout reads whichever SavedData applies, and treats 0 as quiet, 1-4 as trace, 5-9 visible, and 10-15 highlighted. These thresholds are invented UI design, not real severity. A chicken can display two distinct game reference scores; a vanilla bee cannot be incorrectly treated as a cow.

FocusFiveVisualRuntime adds a low-frequency cosmetic observation every 120 game ticks; a total of 24 animals and 64 plant samples maximum per interval, loaded chunks only, with multiplayer UUID/block deduplication. Its small one-particle cues change no blocks and do not modify health or actual animal disease. Toggle focus_five_eco_observation.visuals_enabled in the mod server config.

Commands (in an isolated Minecraft test world): /gugugaga focus list; /gugugaga focus guide influenza_a_virus; /gugugaga focus status. Operator-only /gugugaga focus demo potato_virus_y and /gugugaga focus clear potato_virus_y create/clear abstract demo number 12 at the player's current region. Demo never introduces an organism or health state.

## Save compatibility and testing

Existing original 15-channel SavedData and extension eight-channel SavedData retain the same file formats and IDs. Native NeoForge scratch NBT tests now check all five values across both saved-data systems. A second CI task writes both genuine files in a separate temporary world and verifies all five reference values after a full stop and second server process. Java21 pure tests check host matching, numerical display states, wrong-target isolation, and two-store compatibility.

Limits: existing generic visual modules can add overlapping light particles; actual graphical observation and multi-client play tests still require hands-on verification. Stable main remains unchanged while PR #44 is Draft.
