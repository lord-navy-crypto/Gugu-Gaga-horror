# Minecraft data-only pathogen routes: V6

This is **not a laboratory, infection or outbreak model**. Real disease names and qualitative route categories are cited to prevent misleading claims; all game values are arbitrary integer signals with no relationship to real pathogen shedding, survival, concentrations, virulence, incubation, physical dispersion, susceptibility or probabilities. No clinical or decontamination features are changed.

## Data architecture

Each dimension stores up to 1,024 sparse 4×4-block regions containing a separate 0–15 signal for each of 15 real-taxonomy labels. `PathogenRoutePolicy15` independently describes broad, source-backed route *categories* for each label. `PathogenRouteGameMath.move()` transfers **one game point**, not any actual microorganism. The transfer requires an eligible route, two existing loaded/near-player areas, a bounded 8-cell Manhattan distance, available game pressure and a destination below the 15-point cap. Invalid routes never move signals. The original ambient grid step is restricted to `LOCAL_GAME_FIELD` labels so e.g. rabies cannot behave like a universally diffusing environmental cloud.

`PathogenDataRoutesRuntime` is a server-tick listener, once per 80 game ticks. Its movement paths are gameplay events, not biological contamination: nearby chicken/wolf/other vanilla animal position changes may move a context-eligible game signal; player movement and a held vanilla item select distinct trail modes. The `HELD_ITEM_MOTION` path is an **in-memory observation associated with a player's currently held item**; it is NOT a persistent property of a specific item stack. Operators still initialize/clear virtual channels with `/gugugaga field seed <taxon>`, `/gugugaga field clear <taxon>`; `/gugugaga field status` inspects them. Minecraft rain/air and a strictly **virtual vector cue** allow separate data visualizations; there is no mosquito entity or claim of actual insect-borne transmission. Spatial ambience is limited to 24 game transfers per dimension per update, player trails to 64 and animal trails to 64 total; loaded nearby chunks only, no forced chunk loads. None of these signals infect an animal, crop, item or player.

## Authoritative qualitative reference context

- [CDC rabies](https://www.cdc.gov/rabies/about/): infected-animal bites/scratches, not arbitrary waterborne environmental spread.
- [WHO zoonotic influenza](https://www.who.int/en/news-room/fact-sheets/detail/influenza-%28avian-and-other-zoonotic%29): animal-human interface depends on specific strains/exposures, not sustained human transmission of current zoonotic influenza viruses.
- [CDC West Nile](https://www.cdc.gov/west-nile-virus/causes/): mosquito vector and bird context; humans are not routine spreading hosts.
- [WOAH foot-and-mouth disease](https://www.woah.org/en/disease/foot-and-mouth-disease/): multiple livestock/environment contexts.
- [USDA African swine fever](https://www.aphis.usda.gov/animal-disease/swine/protect-pigs/youth-center/how-african-swine-fever-spreads): pig context and direct/indirect movement; ASF does not infect people.
- [CDC Salmonella](https://www.cdc.gov/salmonella/spread/), [Listeria](https://www.cdc.gov/listeria/causes/index.html), [Campylobacter](https://www.cdc.gov/campylobacter/index.html/): food, animal and environmental route categories vary.
- [Cornell potato viruses](https://www.vegetables.cornell.edu/pest-management/disease-factsheets/virus-and-viroid-diseases-of-potato/), [Maryland extension mosaic](https://extension.umd.edu/resource/viruses-vegetables), [PNW late blight](https://pnwhandbooks.org/node/3325/print), [AHDB brassica black rot](https://horticulture.ahdb.org.uk/knowledge-library/black-rot-of-brassicas-symptoms-and-biology), [NC State fire blight](https://content.ces.ncsu.edu/fire-blight).

## Remaining acceptance

Needs compilation and Minecraft boot CI. In-game validation of cross-chunk data routes, save/reload, multi-player motion duplication, distinct held-item visual trails, and config off is not yet done. Existing old microbe-themed decorative blocks and medical stations remain in ancestor branches for compatibility but are **not** the source of this numerical system; none were added in this PR.
