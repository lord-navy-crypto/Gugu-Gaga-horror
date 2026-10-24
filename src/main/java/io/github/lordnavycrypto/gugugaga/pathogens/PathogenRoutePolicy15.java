package io.github.lordnavycrypto.gugugaga.pathogens;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Qualitative SCIENTIFIC context labels plus intentionally NON-BIOLOGICAL
 * Minecraft game channels. Routes describe only allowed virtual game SIGNALS,
 * never infection, actual concentration, viability, dose or transmissibility.
 *
 * Public-health context: CDC, WHO, USDA/WOAH; plant context: Cornell, UMD,
 * Pacific Northwest extension. Sources and interpretation in docs.
 */
public final class PathogenRoutePolicy15 {
    private PathogenRoutePolicy15(){}
    public enum Route {
        LOCAL_GAME_FIELD, AIR_VISUAL, RAIN_VISUAL,
        ANIMAL_MOTION, PLAYER_MOTION, HELD_ITEM_MOTION,
        VIRTUAL_VECTOR, PLANT_INTERACTION
    }
    public record Profile(String taxonId,Set<Route> gameRoutes,String evidenceUrl){
        public Profile{
            PathogenRoster15.byId(taxonId);
            gameRoutes=Set.copyOf(gameRoutes);
            if(gameRoutes.isEmpty()||!evidenceUrl.startsWith("https://"))
                throw new IllegalArgumentException("Missing source-backed category");
        }
        public boolean allows(Route route){return gameRoutes.contains(route);}
    }
    private static Profile p(String id,String url,Route... routes){
        return new Profile(id,EnumSet.copyOf(List.of(routes)),url);
    }
    private static final List<Profile> ALL=List.of(
        p("lyssavirus_rabies","https://www.cdc.gov/rabies/about/",
            Route.ANIMAL_MOTION),
        p("influenza_a_virus","https://www.who.int/en/news-room/fact-sheets/detail/influenza-%28avian-and-other-zoonotic%29",
            Route.ANIMAL_MOTION,Route.AIR_VISUAL,Route.PLAYER_MOTION,
            Route.HELD_ITEM_MOTION),
        p("west_nile_virus","https://www.cdc.gov/west-nile-virus/causes/",
            Route.VIRTUAL_VECTOR,Route.ANIMAL_MOTION),
        p("foot_and_mouth_disease_virus","https://www.woah.org/en/disease/foot-and-mouth-disease/",
            Route.ANIMAL_MOTION,Route.AIR_VISUAL,Route.RAIN_VISUAL,
            Route.PLAYER_MOTION,Route.HELD_ITEM_MOTION),
        p("african_swine_fever_virus","https://www.aphis.usda.gov/animal-disease/swine/protect-pigs/youth-center/how-african-swine-fever-spreads",
            Route.ANIMAL_MOTION,Route.PLAYER_MOTION,
            Route.HELD_ITEM_MOTION,Route.VIRTUAL_VECTOR),
        p("salmonella_enterica","https://www.cdc.gov/salmonella/spread/",
            Route.ANIMAL_MOTION,Route.RAIN_VISUAL,Route.LOCAL_GAME_FIELD,
            Route.PLAYER_MOTION,Route.HELD_ITEM_MOTION),
        p("listeria_monocytogenes","https://www.cdc.gov/listeria/causes/index.html",
            Route.LOCAL_GAME_FIELD,Route.HELD_ITEM_MOTION,Route.PLAYER_MOTION),
        p("campylobacter_jejuni","https://www.cdc.gov/campylobacter/index.html/",
            Route.ANIMAL_MOTION,Route.RAIN_VISUAL,
            Route.PLAYER_MOTION,Route.HELD_ITEM_MOTION),
        p("staphylococcus_aureus","https://www.cdc.gov/staph-infection/about/index.html",
            Route.ANIMAL_MOTION,Route.PLAYER_MOTION,Route.HELD_ITEM_MOTION),
        p("tobacco_mosaic_virus","https://extension.umd.edu/resource/viruses-vegetables",
            Route.PLANT_INTERACTION,Route.HELD_ITEM_MOTION,Route.PLAYER_MOTION),
        p("cucumber_mosaic_virus","https://www.rhs.org.uk/disease/cucumber-mosaic-virus",
            Route.VIRTUAL_VECTOR,Route.PLANT_INTERACTION),
        p("potato_virus_y","https://www.vegetables.cornell.edu/pest-management/disease-factsheets/virus-and-viroid-diseases-of-potato/",
            Route.VIRTUAL_VECTOR,Route.PLANT_INTERACTION,
            Route.HELD_ITEM_MOTION),
        p("phytophthora_infestans","https://pnwhandbooks.org/node/3325/print",
            Route.LOCAL_GAME_FIELD,Route.AIR_VISUAL,Route.RAIN_VISUAL),
        p("xanthomonas_campestris","https://horticulture.ahdb.org.uk/knowledge-library/black-rot-of-brassicas-symptoms-and-biology",
            Route.LOCAL_GAME_FIELD,Route.RAIN_VISUAL,
            Route.PLANT_INTERACTION,Route.HELD_ITEM_MOTION),
        p("erwinia_amylovora","https://content.ces.ncsu.edu/fire-blight",
            Route.RAIN_VISUAL,Route.AIR_VISUAL,
            Route.VIRTUAL_VECTOR,Route.PLANT_INTERACTION)
    );
    private static final Map<String,Profile> INDEX=ALL.stream()
            .collect(java.util.stream.Collectors.toUnmodifiableMap(
                    Profile::taxonId,x->x));
    static{
        if(INDEX.size()!=15 || !INDEX.keySet().equals(
                PathogenRoster15.FIRST_FIFTEEN.stream()
                    .map(PathogenRoster15.Entry::id)
                    .collect(java.util.stream.Collectors.toSet())))
            throw new IllegalStateException("All 15 taxon labels require game route categories");
    }
    public static Profile forTaxon(String id){
        var profile=INDEX.get(id);
        if(profile==null)throw new IllegalArgumentException("Unknown taxon");
        return profile;
    }
    public static boolean allows(int channel,Route route){
        if(channel<0||channel>=15)throw new IllegalArgumentException("Invalid channel");
        return forTaxon(PathogenRoster15.FIRST_FIFTEEN.get(channel).id()).allows(route);
    }
    public static List<Profile> all(){return ALL;}
}
