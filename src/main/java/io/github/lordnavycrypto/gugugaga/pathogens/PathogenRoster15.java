package io.github.lordnavycrypto.gugugaga.pathogens;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Authentic taxon labels used ONLY for opt-in Minecraft scenarios. No epidemiology. */
public final class PathogenRoster15 {
    private PathogenRoster15() {}
    public enum Type { VIRUS, BACTERIUM, OOMYCETE }
    public record Entry(String id,int ncbiTaxonId,String scientificName,
                        Type type,String context,String visualKey) {}
    public static final List<Entry> FIRST_FIFTEEN=List.of(
        new Entry("lyssavirus_rabies",11292,"Lyssavirus rabies",Type.VIRUS,"animal","wildlife_context"),
        new Entry("influenza_a_virus",11320,"Influenza A virus",Type.VIRUS,"animal","avian_context"),
        new Entry("west_nile_virus",11082,"West Nile virus",Type.VIRUS,"animal","bird_context"),
        new Entry("foot_and_mouth_disease_virus",12110,"Foot-and-mouth disease virus",Type.VIRUS,"animal","livestock_context"),
        new Entry("african_swine_fever_virus",10497,"African swine fever virus",Type.VIRUS,"animal","swine_context"),
        new Entry("salmonella_enterica",28901,"Salmonella enterica",Type.BACTERIUM,"animal","animal_context"),
        new Entry("listeria_monocytogenes",1639,"Listeria monocytogenes",Type.BACTERIUM,"animal","animal_context"),
        new Entry("campylobacter_jejuni",197,"Campylobacter jejuni",Type.BACTERIUM,"animal","animal_context"),
        new Entry("staphylococcus_aureus",1280,"Staphylococcus aureus",Type.BACTERIUM,"animal","animal_context"),
        new Entry("tobacco_mosaic_virus",12242,"Tobacco mosaic virus",Type.VIRUS,"plant","plant_mosaic_visual"),
        new Entry("cucumber_mosaic_virus",12305,"Cucumber mosaic virus",Type.VIRUS,"plant","plant_mosaic_visual"),
        new Entry("potato_virus_y",12216,"Potato virus Y",Type.VIRUS,"plant","plant_mosaic_visual"),
        new Entry("phytophthora_infestans",4787,"Phytophthora infestans",Type.OOMYCETE,"plant","plant_blight_visual"),
        new Entry("xanthomonas_campestris",339,"Xanthomonas campestris",Type.BACTERIUM,"plant","plant_leaf_visual"),
        new Entry("erwinia_amylovora",552,"Erwinia amylovora",Type.BACTERIUM,"plant","plant_wilt_visual")
    );
    private static final Map<String,Entry> INDEX=FIRST_FIFTEEN.stream()
            .collect(Collectors.toUnmodifiableMap(Entry::id,x->x));
    public static Entry byId(String id) {
        Entry result=INDEX.get(id);
        if(result==null)throw new IllegalArgumentException("Unknown taxon id: "+id);
        return result;
    }
    static {
        if(FIRST_FIFTEEN.size()!=15 || INDEX.size()!=15 ||
                FIRST_FIFTEEN.stream().map(Entry::ncbiTaxonId).distinct().count()!=15)
            throw new IllegalStateException("Expected 15 real independent taxa");
    }
}
