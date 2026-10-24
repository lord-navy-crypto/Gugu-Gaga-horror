package io.github.lordnavycrypto.gugugaga.pathogens;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Five scientifically named reference examples, with deliberately fictional
 * Minecraft observational cues. No contagious processes, doses, pathogen
 * quantities, infection predictions or experimentally calibrated kinetics.
 * The two existing SavedData schemas remain unchanged (15 + 8 channels).
 */
public final class FocusFiveProfiles {
    private FocusFiveProfiles(){}
    public enum Store { ORIGINAL_15, EXTENDED_8 }
    public enum Art { BIRD_AURA, POTATO_MOSAIC, FARM_TRACE, WHEAT_GLOW, BEE_SPARKLE }
    public record Profile(String id,String displayName,String group,Store store,
                          Set<String> entityIds,Set<String> blockIds,
                          Art art,String context,String sourceUrl){
        public Profile {
            entityIds=Set.copyOf(entityIds);blockIds=Set.copyOf(blockIds);
            if(id.isBlank()||sourceUrl.isBlank()||
                    (!sourceUrl.startsWith("https://"))||
                    (entityIds.isEmpty()&&blockIds.isEmpty())||
                    (entityIds.size()>0&&blockIds.size()>0))
                throw new IllegalArgumentException("Invalid five-focus entry");
            // Validate ONLY taxon identity and Minecraft visual host IDs.
            if(store==Store.ORIGINAL_15){
                var entry=PathogenRoster15.byId(id);
                for(String e:entityIds)
                    if(!VanillaHostMapping15.supportsEntity(entry.id(),e))
                        throw new IllegalArgumentException("Incorrect vanilla animal proxy");
                for(String b:blockIds)
                    if(!VanillaHostMapping15.supportsBlock(entry.id(),b))
                        throw new IllegalArgumentException("Incorrect vanilla plant proxy");
            }else{
                var entry=EcologyExtendedTaxa.byId(id);
                if(!entry.vanillaEntities().containsAll(entityIds)||
                        !entry.vanillaBlocks().containsAll(blockIds))
                    throw new IllegalArgumentException("Incorrect extension visual proxy");
            }
        }
        public boolean matches(String target,boolean animal){
            return (animal?entityIds:blockIds).contains(target);
        }
        public int channel(){
            return store==Store.ORIGINAL_15
                    ?PathogenSpatialFieldMath.channel(id)
                    :EcologyExtendedTaxa.index(id);
        }
    }
    public static final List<Profile> FIVE=List.of(
      new Profile("influenza_a_virus","甲型流感病毒","VIRUS",
          Store.ORIGINAL_15,Set.of("minecraft:chicken","minecraft:parrot"),
          Set.of(),Art.BIRD_AURA,
          "鸟类观察情景。Influenza A 是广义类型，并非特指 H5N1。",
          "https://www.cdc.gov/flu-in-animals/about/index.html"),
      new Profile("potato_virus_y","马铃薯Y病毒","VIRUS",
          Store.ORIGINAL_15,Set.of(),Set.of("minecraft:potatoes"),
          Art.POTATO_MOSAIC,"观察原版马铃薯叶片附近的虚拟数值提示。",
          "https://www.vegetables.cornell.edu/pest-management/disease-factsheets/virus-and-viroid-diseases-of-potato/"),
      new Profile("salmonella_enterica","肠道沙门氏菌","BACTERIUM",
          Store.ORIGINAL_15,Set.of("minecraft:chicken","minecraft:cow","minecraft:pig"),
          Set.of(),Art.FARM_TRACE,"农场动物仅用于展示共享位置数据，不代表感染诊断。",
          "https://www.cdc.gov/salmonella/spread/"),
      new Profile("puccinia_graminis","麦秆锈菌","FUNGUS",
          Store.EXTENDED_8,Set.of(),Set.of("minecraft:wheat"),
          Art.WHEAT_GLOW,"小麦观察情景；颜色为游戏视觉标记。",
          "https://www.ars.usda.gov/midwest-area/stpaul/cereal-disease-lab/docs/cereal-rusts/wheat-stem-rust/"),
      new Profile("varroa_destructor","狄斯瓦螨","ECTOPARASITE",
          Store.EXTENDED_8,Set.of("minecraft:bee"),Set.of(),Art.BEE_SPARKLE,
          "蜜蜂模型是视觉替代，不模拟真实蜂群或螨虫生活史。",
          "https://www.woah.org/fileadmin/Home/eng/Health_standards/tahc/2024/en_chapitre_varroa_spp.htm")
    );
    private static final Map<String,Profile> INDEX=FIVE.stream()
        .collect(Collectors.toUnmodifiableMap(Profile::id,p->p));
    public static Profile byId(String id){
        var profile=INDEX.get(id);
        if(profile==null)throw new IllegalArgumentException("Not one of the five references");
        return profile;
    }
    static{
        if(FIVE.size()!=5||INDEX.size()!=5)throw new IllegalStateException("Exactly five");
    }
}
