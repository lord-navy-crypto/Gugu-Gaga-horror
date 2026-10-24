package io.github.lordnavycrypto.gugugaga.pathogens;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

/**
 * A viewer's low-cost read-only sampling of pre-existing Minecraft numeric
 * world signals. NEVER registers a pathogen Block/Entity or changes the field.
 * 15 legacy + 8 extension channel labels remain in their existing NBT stores.
 */
public final class FieldParticleSampling {
    private FieldParticleSampling(){}
    public static final int RADIUS_CELLS=3, MAX_VISITS=49, MAX_MARKERS=6;
    public enum Store { ORIGINAL_15, EXTENSION_8 }
    public enum Family { VIRUS, BACTERIUM, OOMYCETE, FUNGUS, PARASITE, PHAGE }
    public enum Change { NEW_OR_INCREASED, UNCHANGED, DECREASED }
    public record Choice(String taxon,Store store,int channel,Family family,
                         boolean worldVisualAllowed){}
    public record Marker(PathogenSpatialFieldMath.Cell cell,int gameLevel,Change change){}
    public record Scan(List<Marker> markers,Map<PathogenSpatialFieldMath.Cell,Integer> current,
                       int visited,int occupied){
        public Scan{
            markers=List.copyOf(markers);
            current=Map.copyOf(current);
        }
    }
    public static Choice choose(String id){
        Objects.requireNonNull(id);
        for(int i=0;i<PathogenRoster15.FIRST_FIFTEEN.size();i++){
            var item=PathogenRoster15.FIRST_FIFTEEN.get(i);
            if(!item.id().equals(id))continue;
            Family family=switch(item.type()){
                case VIRUS -> Family.VIRUS;
                case BACTERIUM -> Family.BACTERIUM;
                case OOMYCETE -> Family.OOMYCETE;
            };
            return new Choice(id,Store.ORIGINAL_15,i,family,true);
        }
        for(int i=0;i<EcologyExtendedTaxa.ENTRIES.size();i++){
            var item=EcologyExtendedTaxa.ENTRIES.get(i);
            if(!item.id().equals(id))continue;
            Family family=switch(item.group()){
                case FUNGUS -> Family.FUNGUS;
                case ECTOPARASITE,HELMINTH,PROTOZOAN -> Family.PARASITE;
                case BACTERIOPHAGE -> Family.PHAGE;
            };
            return new Choice(id,Store.EXTENSION_8,i,family,!item.virtualOnly());
        }
        throw new IllegalArgumentException("Unknown catalog label: "+id);
    }
    public static List<String> selectableIds(){
        List<String> ids=new ArrayList<>(23);
        for(var item:PathogenRoster15.FIRST_FIFTEEN)ids.add(item.id());
        for(var item:EcologyExtendedTaxa.ENTRIES)
            if(!item.virtualOnly())ids.add(item.id());
        return List.copyOf(ids);
    }
    public static Scan scan(PathogenSpatialFieldMath.Cell center,
                            Choice selected,
                            Predicate<PathogenSpatialFieldMath.Cell> isLoaded,
                            ToIntFunction<PathogenSpatialFieldMath.Cell> readGameLevel,
                            Map<PathogenSpatialFieldMath.Cell,Integer> previous) {
        Objects.requireNonNull(center);
        Objects.requireNonNull(selected);
        Objects.requireNonNull(isLoaded);
        Objects.requireNonNull(readGameLevel);
        Objects.requireNonNull(previous);
        if(!selected.worldVisualAllowed())
            return new Scan(List.of(),Map.of(),0,0);
        Map<PathogenSpatialFieldMath.Cell,Integer> now=new HashMap<>();
        List<Marker> matching=new ArrayList<>();
        int visited=0;
        for(int dz=-RADIUS_CELLS;dz<=RADIUS_CELLS;dz++){
            for(int dx=-RADIUS_CELLS;dx<=RADIUS_CELLS;dx++){
                var cell=new PathogenSpatialFieldMath.Cell(center.x()+dx,center.z()+dz);
                if(!isLoaded.test(cell))continue; // do not force world loading
                visited++;
                int level=readGameLevel.applyAsInt(cell);
                if(level<0||level>15)
                    throw new IllegalArgumentException("Invalid game signal");
                if(level==0)continue;
                now.put(cell,level);
                int old=previous.getOrDefault(cell,0);
                Change change=level>old?Change.NEW_OR_INCREASED:
                    level<old?Change.DECREASED:Change.UNCHANGED;
                matching.add(new Marker(cell,level,change));
            }
        }
        matching.sort(Comparator.comparingInt(Marker::gameLevel).reversed()
                .thenComparingInt(m->Math.abs(m.cell().x()-center.x())
                               +Math.abs(m.cell().z()-center.z()))
                .thenComparingInt(m->m.cell().x())
                .thenComparingInt(m->m.cell().z()));
        return new Scan(matching.subList(0,Math.min(MAX_MARKERS,matching.size())),
                now,visited,matching.size());
    }
}
