package io.github.lordnavycrypto.gugugaga.pathogens;

import java.util.*;

/**
 * Source-supported CATEGORIES paired with fictional Minecraft game event
 * labels. Scientific sources justify only the qualitative category, NOT
 * any distance, time, replication, survival or infectious coefficient.
 */
public final class FivePathGameRoutes {
    private FivePathGameRoutes(){}
    public enum Path {
        BIRD_ACTIVITY_AIR_CUE,
        POTATO_PATCH_INTERACTION,
        FARM_ACTIVITY_WATER_CUE,
        WHEAT_PATCH_WEATHER_CUE,
        BEE_TRAIL
    }
    public record Profile(String id,Path route,String scientificCategory,
                          String sourceUrl) {
        public Profile {
            FocusFiveProfiles.byId(id);
            if(scientificCategory.isBlank()||!sourceUrl.startsWith("https://"))
                throw new IllegalArgumentException("Invalid evidence-backed identity");
        }
    }
    public static final List<Profile> FIVE=List.of(
        new Profile("influenza_a_virus",Path.BIRD_ACTIVITY_AIR_CUE,
            "Influenza A in animals: contact with birds and their environments; airborne droplet/dust routes can occur for avian subtypes.",
            "https://www.cdc.gov/bird-flu/virus-transmission/"),
        new Profile("potato_virus_y",Path.POTATO_PATCH_INTERACTION,
            "Potato virus Y: aphid-associated, tuber-associated and mechanical routes; vanilla has no aphid species.",
            "https://www.vegetables.cornell.edu/pest-management/disease-factsheets/virus-and-viroid-diseases-of-potato/"),
        new Profile("salmonella_enterica",Path.FARM_ACTIVITY_WATER_CUE,
            "Salmonella: animal/habitat, food and water associations; these are abstract farm/water world cues.",
            "https://www.cdc.gov/salmonella/spread/"),
        new Profile("puccinia_graminis",Path.WHEAT_PATCH_WEATHER_CUE,
            "Wheat stem rust: wheat host association and weather-related spore travel; no physical wind or spore model.",
            "https://www.ars.usda.gov/midwest-area/stpaul/cereal-disease-lab/docs/cereal-rusts/wheat-stem-rust/"),
        new Profile("varroa_destructor",Path.BEE_TRAIL,
            "Varroa: honey-bee-to-bee and movement-associated ectoparasite; bee is a vanilla visual stand-in.",
            "https://www.woah.org/fileadmin/Home/eng/Health_standards/tahc/2024/en_chapitre_varroa_spp.htm")
    );
    public static Profile byId(String id){
        for(var p:FIVE)if(p.id().equals(id))return p;
        throw new IllegalArgumentException("Unknown five-focus route");
    }
    public record GameTags(boolean bird,boolean potato,boolean farmAnimal,
                           boolean water,boolean wheat,boolean bee,
                           boolean sky,boolean rain){
        public boolean accepts(Path path){
            // These booleans are sampled from existing vanilla game objects,
            // NOT real biological viability or real transmission.
            return switch(path){
                case BIRD_ACTIVITY_AIR_CUE -> bird;
                case POTATO_PATCH_INTERACTION -> potato;
                case FARM_ACTIVITY_WATER_CUE -> farmAnimal||water;
                case WHEAT_PATCH_WEATHER_CUE -> wheat;
                case BEE_TRAIL -> bee;
            };
        }
        public String label(Path path){
            return switch(path){
                case BIRD_ACTIVITY_AIR_CUE -> sky?"BIRD_OUTDOOR_GAME_CUE":"BIRD_SHELTER_GAME_CUE";
                case POTATO_PATCH_INTERACTION -> "VANILLA_POTATO_PATCH";
                case FARM_ACTIVITY_WATER_CUE -> water?"VANILLA_WATER_GAME_CUE":"VANILLA_FARM_GAME_CUE";
                case WHEAT_PATCH_WEATHER_CUE -> rain?"VANILLA_RAIN_WHEAT":"VANILLA_WHEAT";
                case BEE_TRAIL -> "VANILLA_BEE_GAME_TRAIL";
            };
        }
    }
    static{
        if(FIVE.size()!=5||FIVE.stream().map(Profile::id).distinct().count()!=5)
            throw new IllegalStateException("Five unique source-linked routes required");
    }
}
