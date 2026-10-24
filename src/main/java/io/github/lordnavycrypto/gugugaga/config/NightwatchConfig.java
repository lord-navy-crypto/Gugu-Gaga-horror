package io.github.lordnavycrypto.gugugaga.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Server-owned opt-in; config is OFF by default and never auto-connects. */
public final class NightwatchConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.BooleanValue OLLAMA_ENABLED;
    public static final ModConfigSpec.ConfigValue<String> OLLAMA_MODEL;
    public static final ModConfigSpec.BooleanValue ENCOUNTERS_ENABLED;
    public static final ModConfigSpec.IntValue ENCOUNTER_COOLDOWN_TICKS;
    public static final ModConfigSpec.BooleanValue HAUNTING_AUDIO_ENABLED;
    public static final ModConfigSpec.BooleanValue HAUNTING_PARTICLES_ENABLED;
    public static final ModConfigSpec.BooleanValue JOURNAL_ENABLED;
    public static final ModConfigSpec.BooleanValue JOURNAL_HINTS_ENABLED;
    public static final ModConfigSpec.BooleanValue VARIED_EVENTS_ENABLED;
    public static final ModConfigSpec.IntValue DIRECTOR_INTENSITY;
    public static final ModConfigSpec.BooleanValue DIALOGUE_ENABLED;
    public static final ModConfigSpec.BooleanValue DIALOGUE_LOCAL_AI_ENABLED;
    public static final ModConfigSpec.BooleanValue HOSTILITY_ENABLED;
    public static final ModConfigSpec.BooleanValue CORRUPTION_VISUALS_ENABLED;
    public static final ModConfigSpec.BooleanValue REDACTOR_HOSTILITY_ENABLED;
    public static final ModConfigSpec.BooleanValue PLAYABLE_LOOP_ENABLED;
    public static final ModConfigSpec.BooleanValue MICROBE_ECOLOGY_ENABLED;
    public static final ModConfigSpec.BooleanValue MICROBE_SATELLITES_ENABLED;
    public static final ModConfigSpec.BooleanValue MICROBE_ENTITY_CUES_ENABLED;
    public static final ModConfigSpec.BooleanValue PATHOGEN_GAMEPLAY_ENABLED;
    public static final ModConfigSpec.BooleanValue LIVING_MICROBE_SIMULATION;
    public static final ModConfigSpec.BooleanValue PATHOGEN_SCENE_ENGINE_ENABLED;
    public static final ModConfigSpec.BooleanValue PATHOGEN_SCENE_CROP_PAUSE;
    public static final ModConfigSpec.BooleanValue PATHOGEN_SCENE_ANIMAL_CUES;
    public static final ModConfigSpec.BooleanValue PATHOGEN_FIELD_ENABLED;
    public static final ModConfigSpec.BooleanValue PATHOGEN_FIELD_CROP_CUES;
    public static final ModConfigSpec.BooleanValue PATHOGEN_ROUTE_EVENTS_ENABLED;
    public static final ModConfigSpec.BooleanValue VIRAL_GAME_WAVES_ENABLED;
    public static final ModConfigSpec.BooleanValue VIRAL_HOST_GAME_EFFECTS_ENABLED;
    public static final ModConfigSpec.BooleanValue ECOLOGY_EXTENSIONS_ENABLED;
    public static final ModConfigSpec.BooleanValue FOCUS_FIVE_VISUALS_ENABLED;
    public static final ModConfigSpec.BooleanValue ECOLOGY_CROP_GAME_FEEDBACK_ENABLED;
    public static final ModConfigSpec.BooleanValue FIELD_PARTICLE_OVERLAY_ENABLED;
    public static final ModConfigSpec SPEC;

    static {
        BUILDER.push("local_ollama");
        OLLAMA_ENABLED = BUILDER.comment(
                "Optional, explicitly enabled local Ollama shadow advisor. No auto network calls.")
                .define("enabled", false);
        OLLAMA_MODEL = BUILDER.comment("Exact name from local Ollama /api/tags")
                .define("model", "");
        BUILDER.pop();
        BUILDER.push("encounters");
        ENCOUNTERS_ENABLED = BUILDER.comment(
                "Allow rare nonviolent observer encounters. OFF by default. No chunk forcing.")
                .define("enabled", true);
        ENCOUNTER_COOLDOWN_TICKS = BUILDER.comment(
                "Minimum cooldown per player, ticks. Never below 8 minutes; default 10 minutes.")
                .defineInRange("cooldown_ticks", 12000, 9600, 72000);
        HAUNTING_AUDIO_ENABLED = BUILDER.comment(
                "Use subtle, volume-capped vanilla directional cues. OFF disables all custom observer cue sounds.")
                .define("haunting_audio", true);
        HAUNTING_PARTICLES_ENABLED = BUILDER.comment(
                "Allow a few subtle soul particles during retreat; OFF disables horror particles.")
                .define("haunting_particles", true);
        VARIED_EVENTS_ENABLED = BUILDER.comment(
                "When encounters are opt-in enabled, vary them with calm intervals, distant clues and rare sightings. " +
                "OFF makes director use the original sighting-only algorithm.")
                .define("varied_events", true);
        DIRECTOR_INTENSITY = BUILDER.comment(
                "How often a generated encounter can be a visible observer: 0 gentle, 1 balanced, 2 eerie. " +
                "Does NOT reduce cooldown or increase sound volume, damage or monster count.")
                .defineInRange("director_intensity", 1, 0, 2);
        BUILDER.pop();
        BUILDER.push("witness_journal");
        JOURNAL_ENABLED = BUILDER.comment(
                "Keep a three-clue in-game witness notebook in the player's own save data.")
                .define("enabled", true);
        JOURNAL_HINTS_ENABLED = BUILDER.comment(
                "One quiet chat hint per new clue. OFF means journal-only discovery.")
                .define("hint_messages", true);
        BUILDER.pop();
        BUILDER.push("dialogue");
        DIALOGUE_ENABLED=BUILDER.comment(
                "Allow players to talk to the fictional penguin with /gugugaga talk. "
                + "Uses deterministic OFFLINE replies unless local AI is explicitly opted in.")
                .define("enabled",true);
        DIALOGUE_LOCAL_AI_ENABLED=BUILDER.comment(
                "Optional localhost Ollama for /gugugaga talk. OFF by default. "
                + "Must also enable local_ollama.enabled and select a local model. "
                + "Only an explicitly entered question is sent to local server AI.")
                .define("local_ollama_dialogue",false);
        BUILDER.pop();
        BUILDER.push("dual_nature");
        HOSTILITY_ENABLED = BUILDER.comment(
                "Allow anger/corruption to chase and give small, telegraphed "
                + "Minecraft combat damage. OFF by default. In either state the creature "
                + "cannot break blocks, chase quiet-mode players, or teleport.")
                .define("hostility_enabled",false);
        CORRUPTION_VISUALS_ENABLED = BUILDER.comment(
                "Subtle non-flashing asymmetry in an angry/corrupted observer's animation.")
                .define("visuals_enabled",true);
        BUILDER.pop();
        BUILDER.push("redactor_mimic");
        REDACTOR_HOSTILITY_ENABLED = BUILDER.comment(
                "Revealed Redactor/Lapis may give one small telegraphed hit only after "
                + "explicit opt-in. OFF by default and independent of Gugu anger.")
                .define("redactor_hostility",false);
        BUILDER.pop();
        BUILDER.push("survival_gameplay");
        PLAYABLE_LOOP_ENABLED=BUILDER.comment(
                "Five-act survival horror can be activated from the H-key panel; "
                +"world defaults ON for new profiles. Every player must voluntarily JOIN "
                +"using the UI. Server owners can pause through the same panel.")
                .define("playable_loop",true);
        BUILDER.pop();
        BUILDER.push("microbe_ecology");
        MICROBE_ECOLOGY_ENABLED=BUILDER.comment(
                "Allow native enlarged ecosystem-art patch states to advance on vanilla random ticks. "
                +"These are Minecraft art states, not real biological growth values.")
                .define("enabled",true);
        MICROBE_SATELLITES_ENABLED=BUILDER.comment(
                "Allow each naturally generated soil/leaf decoration ONE neighboring satellite, "
                +"without recursion. Turn OFF to keep the patches static.")
                .define("one_satellite_per_patch",true);
        MICROBE_ENTITY_CUES_ENABLED=BUILDER.comment(
                "Show minor passive Minecraft ecological observations for players and animals. "
                +"No health, disease, or mob attribute changes.")
                .define("entity_observation_cues",true);
        BUILDER.pop();
        BUILDER.push("pathogen_survival");
        PATHOGEN_GAMEPLAY_ENABLED=BUILDER.comment(
                "Enable optional Minecraft survival pathogen encounter scenes and clinic. "
                +"No biological transmission parameters or clinical prescriptions.")
                .define("enabled",true);
        BUILDER.pop();
        BUILDER.push("living_microbes");
        LIVING_MICROBE_SIMULATION=BUILDER.comment(
                "Enable a per-animal, scanner-enrolled Minecraft microbial population toy. "
                +"No unobserved wild animals start infected; no pathogen transfer "
                +"or real experimental population values.")
                .define("enabled",true);
        BUILDER.pop();
        BUILDER.push("vanilla_host_scenes");
        PATHOGEN_SCENE_ENGINE_ENABLED=BUILDER.comment(
                "Enable manually selected, object-bound Minecraft art scenarios "
                +"for 15 real taxon labels; never an actual infection model.")
                .define("enabled",true);
        PATHOGEN_SCENE_CROP_PAUSE=BUILDER.comment(
                "A short, reversible natural growth pause on individually enrolled crops only.")
                .define("enrolled_crop_growth_pause",true);
        PATHOGEN_SCENE_ANIMAL_CUES=BUILDER.comment(
                "Small, finite movement cue on explicitly enrolled animals only.")
                .define("enrolled_animal_movement_cue",true);
        BUILDER.pop();
        BUILDER.push("pathogen_spatial_field");
        PATHOGEN_FIELD_ENABLED=BUILDER.comment(
                "Run a 15-channel 4x4-block abstract Minecraft scalar field. "
                +"Game pressure is not real pathogen concentration or spread.")
                .define("enabled",true);
        PATHOGEN_FIELD_CROP_CUES=BUILDER.comment(
                "Optional fictional growth pause in appropriate vanilla crops "
                +"inside high-game-pressure cells. No disease diagnosis.")
                .define("crop_game_cues",true);
        BUILDER.pop();
        BUILDER.push("pathogen_information_routes");
        PATHOGEN_ROUTE_EVENTS_ENABLED=BUILDER.comment(
                "Move abstract Minecraft game SIGNALS via loaded-region event routing. "
                +"Air/rain/vector cues and player/animal/item-held trails are not "
                +"pathogen transmission physics or infection.")
                .define("enabled",true);
        BUILDER.pop();
        BUILDER.push("virus_game_signals");
        VIRAL_GAME_WAVES_ENABLED=BUILDER.comment(
                "Allow eight virus-named Minecraft information channels to form "
                +"limited neighboring visual-data waves. These are entirely "
                +"invented game numbers, not biological replication or rates.")
                .define("waves_enabled",true);
        VIRAL_HOST_GAME_EFFECTS_ENABLED=BUILDER.comment(
                "Enable cosmetic native-animal particles, optional tiny movement "
                +"cues, and vanilla crop growth feedback from matching viral "
                +"game field signals. No actual infection or clinical effect.")
                .define("host_game_cues",true);
        BUILDER.pop();
        BUILDER.push("ecology_taxa_extensions");
        ECOLOGY_EXTENSIONS_ENABLED=BUILDER.comment(
                "Enable optional fungi, parasite and phage identity plus fictional "
                +"eight-channel numeric visual scenery. Never real infectiousness.")
                .define("enabled",true);
        BUILDER.pop();
        BUILDER.push("focus_five_eco_observation");
        FOCUS_FIVE_VISUALS_ENABLED=BUILDER.comment(
                "Show small distinctive Minecraft-only particle art for five "
                +"selected source-labelled plants/animals. The values are NOT "
                +"actual pathogen concentration or clinical diagnosis.")
                .define("visuals_enabled",true);
        BUILDER.pop();
        BUILDER.push("ecology_gameplay_environment");
        ECOLOGY_CROP_GAME_FEEDBACK_ENABLED=BUILDER.comment(
                "Optional deterministic Minecraft wheat growth game feedback "
                +"for the pre-existing extended numerical signal. OFF by "
                +"default. No new pathogen blocks or calibrated biology.")
                .define("wheat_growth_feedback_enabled",false);
        BUILDER.pop();
        BUILDER.push("numeric_field_particles");
        FIELD_PARTICLE_OVERLAY_ENABLED=BUILDER.comment(
                "Allow player-initiated, per-player vanilla particle projections "
                +"of existing numeric 4x4 game regions. Personal particle view "
                +"defaults OFF; no new blocks, infected entities, or chunk loading.")
                .define("viewer_enabled",true);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private NightwatchConfig() {}
}
