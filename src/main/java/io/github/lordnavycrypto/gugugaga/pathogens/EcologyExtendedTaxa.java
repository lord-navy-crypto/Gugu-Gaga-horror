package io.github.lordnavycrypto.gugugaga.pathogens;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Independently sourced BIOLOGICAL IDENTITY and conservative vanilla art
 * targets. Does not represent host infection, transmissibility, growth,
 * persistence or any real biological kinetics.
 *
 * This is an additive catalog: do not change the original 15-channel NBT
 * contract in PathogenSpatialFieldSavedData.
 */
public final class EcologyExtendedTaxa {
    private EcologyExtendedTaxa(){}
    public enum Group { FUNGUS, ECTOPARASITE, HELMINTH, PROTOZOAN, BACTERIOPHAGE }
    public record Entry(String id,String scientificName,Group group,
                        Set<String> vanillaEntities,Set<String> vanillaBlocks,
                        boolean artProxy,boolean virtualOnly,String sourceUrl){
        public Entry{
            vanillaEntities=Set.copyOf(vanillaEntities);
            vanillaBlocks=Set.copyOf(vanillaBlocks);
            if(!id.matches("[a-z][a-z0-9_]+")||scientificName.isBlank()
                  ||!sourceUrl.startsWith("https://")
                  ||(virtualOnly&&(!vanillaEntities.isEmpty()||!vanillaBlocks.isEmpty()))
                  ||(!virtualOnly&&vanillaEntities.isEmpty()&&vanillaBlocks.isEmpty()))
                throw new IllegalArgumentException("Invalid real taxon/vanilla art catalog");
        }
        public boolean matchesEntity(String vanillaId){
            return vanillaEntities.contains(vanillaId);
        }
        public boolean matchesBlock(String vanillaId){
            return vanillaBlocks.contains(vanillaId);
        }
    }
    public static final int ORIGINAL_CHANNELS=15;
    public static final int ADDITIONAL_CHANNELS=8;
    public static final int BLOCK_EDGE_METERS=1;
    public static final int FIELD_CELL_EDGE_BLOCKS=4;
    public static final List<Entry> ENTRIES=List.of(
        new Entry("puccinia_graminis","Puccinia graminis",Group.FUNGUS,
            Set.of(),Set.of("minecraft:wheat"),false,false,
            "https://www.ars.usda.gov/midwest-area/stpaul/cereal-disease-lab/docs/cereal-rusts/wheat-stem-rust/"),
        new Entry("blumeria_graminis","Blumeria graminis",Group.FUNGUS,
            Set.of(),Set.of("minecraft:wheat"),false,false,
            "https://www.ars.usda.gov/ARSUserFiles/60701500/Publications/Marshall/TuckerBrownGuediraMarshallEuphytica07.pdf"),
        new Entry("botrytis_cinerea","Botrytis cinerea",Group.FUNGUS,
            Set.of(),Set.of("minecraft:sweet_berry_bush"),true,false,
            "https://extension.psu.edu/strawberry-disease-gray-mold"),
        new Entry("microsporum_canis","Microsporum canis",Group.FUNGUS,
            Set.of("minecraft:cat"),Set.of(),false,false,
            "https://wwwn.cdc.gov/phil/details.aspx?pid=3209"),
        new Entry("varroa_destructor","Varroa destructor",Group.ECTOPARASITE,
            Set.of("minecraft:bee"),Set.of(),true,false,
            "https://www.woah.org/fileadmin/Home/eng/Health_standards/tahc/2024/en_chapitre_varroa_spp.htm"),
        new Entry("haemonchus_contortus","Haemonchus contortus",Group.HELMINTH,
            Set.of("minecraft:sheep","minecraft:goat"),Set.of(),false,false,
            "https://www.msdvetmanual.com/digestive-system/gastrointestinal-parasites-of-ruminants/common-gastrointestinal-parasites-of-small-ruminants"),
        new Entry("eimeria_tenella","Eimeria tenella",Group.PROTOZOAN,
            Set.of("minecraft:chicken"),Set.of(),false,false,
            "https://www.merckvetmanual.com/poultry/coccidiosis-in-poultry/coccidiosis-in-poultry"),
        new Entry("escherichia_phage_t4","Enterobacteria phage T4",Group.BACTERIOPHAGE,
            Set.of(),Set.of(),false,true,
            "https://ictv.global/report_9th/dsDNA/Myoviridae")
    );
    private static final Map<String,Entry> BY_ID=ENTRIES.stream()
        .collect(Collectors.toUnmodifiableMap(Entry::id,item->item));
    public static Entry byId(String id){
        Entry match=BY_ID.get(id);
        if(match==null)throw new IllegalArgumentException("Unknown extension identity: "+id);
        return match;
    }
    public static int index(String id){
        for(int i=0;i<ENTRIES.size();i++)if(ENTRIES.get(i).id().equals(id))return i;
        throw new IllegalArgumentException("Unknown extended taxon: "+id);
    }
    static{
        if(ENTRIES.size()!=ADDITIONAL_CHANNELS ||BY_ID.size()!=ADDITIONAL_CHANNELS)
            throw new IllegalStateException("Extended taxon catalog must remain 8");
    }
}
