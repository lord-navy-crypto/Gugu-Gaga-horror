package io.github.lordnavycrypto.gugugaga.pathogens;
import java.util.Map;
import java.util.Set;

/** Only existing Minecraft 1.21.1 vanilla block/entity IDs, NEVER a medical host prediction.
 * Beetroot and generic tree foliage are deliberately visual proxies.
 */
public final class VanillaHostMapping15 {
    private VanillaHostMapping15() {}
    public record Targets(Set<String> entityIds,Set<String> blockIds,boolean visualProxy) {
        public Targets {
            entityIds=Set.copyOf(entityIds);blockIds=Set.copyOf(blockIds);
            if(entityIds.isEmpty()&&blockIds.isEmpty())
                throw new IllegalArgumentException("Missing Minecraft host");
        }
    }
    private static final Map<String,Targets> HOSTS=Map.ofEntries(
        Map.entry("lyssavirus_rabies",new Targets(Set.of("minecraft:wolf","minecraft:fox","minecraft:cat"),Set.of(),false)),
        Map.entry("influenza_a_virus",new Targets(Set.of("minecraft:chicken","minecraft:parrot"),Set.of(),false)),
        Map.entry("west_nile_virus",new Targets(Set.of("minecraft:chicken","minecraft:parrot","minecraft:horse"),Set.of(),false)),
        Map.entry("foot_and_mouth_disease_virus",new Targets(Set.of("minecraft:cow","minecraft:pig","minecraft:sheep","minecraft:goat"),Set.of(),false)),
        Map.entry("african_swine_fever_virus",new Targets(Set.of("minecraft:pig"),Set.of(),false)),
        Map.entry("salmonella_enterica",new Targets(Set.of("minecraft:chicken","minecraft:cow","minecraft:pig"),Set.of(),false)),
        Map.entry("listeria_monocytogenes",new Targets(Set.of("minecraft:cow","minecraft:sheep","minecraft:pig"),Set.of(),false)),
        Map.entry("campylobacter_jejuni",new Targets(Set.of("minecraft:chicken","minecraft:cow"),Set.of(),false)),
        Map.entry("staphylococcus_aureus",new Targets(Set.of("minecraft:cow","minecraft:sheep","minecraft:wolf","minecraft:cat"),Set.of(),false)),
        Map.entry("tobacco_mosaic_virus",new Targets(Set.of(),Set.of("minecraft:beetroots"),true)),
        Map.entry("cucumber_mosaic_virus",new Targets(Set.of(),Set.of("minecraft:melon_stem","minecraft:attached_melon_stem","minecraft:pumpkin_stem","minecraft:attached_pumpkin_stem"),false)),
        Map.entry("potato_virus_y",new Targets(Set.of(),Set.of("minecraft:potatoes"),false)),
        Map.entry("phytophthora_infestans",new Targets(Set.of(),Set.of("minecraft:potatoes"),false)),
        Map.entry("xanthomonas_campestris",new Targets(Set.of(),Set.of("minecraft:beetroots"),true)),
        Map.entry("erwinia_amylovora",new Targets(Set.of(),Set.of("minecraft:oak_leaves","minecraft:spruce_leaves","minecraft:birch_leaves","minecraft:jungle_leaves","minecraft:acacia_leaves","minecraft:dark_oak_leaves","minecraft:mangrove_leaves","minecraft:cherry_leaves"),true))
    );
    static {
        if(HOSTS.size()!=15 || !HOSTS.keySet().equals(
                PathogenRoster15.FIRST_FIFTEEN.stream().map(
                        PathogenRoster15.Entry::id).collect(java.util.stream.Collectors.toSet())))
            throw new IllegalStateException("15 taxa need Minecraft targets");
    }
    public static Targets byId(String taxonId){
        PathogenRoster15.byId(taxonId);
        return HOSTS.get(taxonId);
    }
    public static boolean supportsEntity(String taxonId,String entityId){
        return byId(taxonId).entityIds().contains(entityId);
    }
    public static boolean supportsBlock(String taxonId,String blockId){
        return byId(taxonId).blockIds().contains(blockId);
    }
    public static boolean sameStemFamily(String first,String second){
        if(first==null||second==null)return false;
        return (first.equals("minecraft:melon_stem")&&second.equals("minecraft:attached_melon_stem"))
            ||(first.equals("minecraft:attached_melon_stem")&&second.equals("minecraft:melon_stem"))
            ||(first.equals("minecraft:pumpkin_stem")&&second.equals("minecraft:attached_pumpkin_stem"))
            ||(first.equals("minecraft:attached_pumpkin_stem")&&second.equals("minecraft:pumpkin_stem"));
    }
    public static boolean keepsHost(String taxonId,String previous,String current){
        return (previous.equals(current)||sameStemFamily(previous,current))
                &&supportsBlock(taxonId,current);
    }
}
