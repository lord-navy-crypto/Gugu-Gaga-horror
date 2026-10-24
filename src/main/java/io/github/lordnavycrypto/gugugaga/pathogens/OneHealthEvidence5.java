package io.github.lordnavycrypto.gugugaga.pathogens;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Source-attributed, high-level One Health EDUCATION.
 *
 * Scientific statements are not a recipe for cultivating, reproducing, or
 * disseminating biological agents. All in-game numbers belong exclusively
 * to existing invented Minecraft visual signals, not infection/diagnosis.
 */
public final class OneHealthEvidence5 {
    private OneHealthEvidence5(){}
    public enum Field { ANIMAL_HEALTH, PLANT_HEALTH, HYGIENE, APICULTURE }
    public record Fact(String taxonId,Field domain,
                       String verifiedConcept,String observationCaution,
                       String responsibleAction,String sourceUrl){
        public Fact {
            FocusFiveProfiles.byId(taxonId);
            if(verifiedConcept.isBlank()||observationCaution.isBlank()
                    ||responsibleAction.isBlank()||!sourceUrl.startsWith("https://"))
                throw new IllegalArgumentException("Incomplete One Health evidence");
        }
    }
    public static final List<Fact> FACTS=List.of(
        new Fact("influenza_a_virus",Field.ANIMAL_HEALTH,
           "Influenza A encompasses viruses detected in multiple animal groups; poultry can be part of animal health monitoring.",
           "Animal appearance and a Minecraft particle are not a laboratory result. Influenza A is not automatically H5N1.",
           "Observe abnormal animal health; avoid handling apparently sick wild birds and seek appropriate animal-health guidance.",
           "https://www.cdc.gov/flu-in-animals/prevention/index.html"),
        new Fact("potato_virus_y",Field.PLANT_HEALTH,
           "Potato virus Y is a documented viral disease agent of potatoes. Plant appearance is one part of plant-health surveillance.",
           "Leaf colour changes have multiple possible causes; Minecraft mosaic particles are not a plant disease diagnosis.",
           "Record observations and compare with extension plant-disease guidance before drawing conclusions.",
           "https://www.vegetables.cornell.edu/pest-management/disease-factsheets/virus-and-viroid-diseases-of-potato/"),
        new Fact("salmonella_enterica",Field.HYGIENE,
           "Animals may carry Salmonella without appearing visibly ill.",
           "A healthy-looking Minecraft chicken, cow or pig does not establish a real infection, and a numeric indicator is not a test.",
           "Practice hand hygiene after contact with animals and their environments, following CDC guidance.",
           "https://www.cdc.gov/hygiene/about/hygiene-practices-around-animals.html"),
        new Fact("puccinia_graminis",Field.PLANT_HEALTH,
           "Puccinia graminis is a stem-rust fungus associated with cereal crops including wheat.",
           "Rust-coloured lesions can prompt inspection, but a Minecraft visual effect is not confirmation of a fungal disease.",
           "Document visible crop changes and consult trusted agricultural extension resources.",
           "https://www.ars.usda.gov/midwest-area/stpaul/cereal-disease-lab/docs/cereal-rusts/wheat-stem-rust/"),
        new Fact("varroa_destructor",Field.APICULTURE,
           "Varroa destructor is an ectoparasitic mite of honey bees and an important colony-health concern.",
           "Minecraft bee particles cannot reveal mites or establish colony condition; vanilla bees are visual stand-ins.",
           "Observe colony health conceptually and refer real beekeeping questions to qualified local bee-health expertise.",
           "https://www.woah.org/fileadmin/Home/eng/Health_standards/tahc/2024/en_chapitre_varroa_spp.htm")
    );
    private static final Map<String,Fact> BY_ID=FACTS.stream()
        .collect(Collectors.toUnmodifiableMap(Fact::taxonId,x->x));
    public static Fact byId(String id){
        var item=BY_ID.get(id);
        if(item==null)throw new IllegalArgumentException("No reference for "+id);
        return item;
    }
    static {
        if(FACTS.size()!=5||BY_ID.size()!=5)
            throw new IllegalStateException("Five scientific evidence records required");
    }
}
