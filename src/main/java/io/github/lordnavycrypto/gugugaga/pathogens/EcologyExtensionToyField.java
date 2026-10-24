package io.github.lordnavycrypto.gugugaga.pathogens;

import java.util.*;
import java.util.function.Predicate;

/**
 * Eight-channel Minecraft data art field. This uses INVENTED integer scores,
 * shared equally by fungi, parasites, and a bacteriophage reference.
 * No actual biology, pathogen transport, infection kinetics or dose models.
 *
 * One block is treated as 1 metre for VISUAL SPATIAL SCALE alone; the 4x4
 * map cell describes a 4x4-metre game patch, not a physical fluid solver.
 */
public final class EcologyExtensionToyField {
    private EcologyExtensionToyField(){}
    public static final int CHANNELS=EcologyExtendedTaxa.ADDITIONAL_CHANNELS;
    public static final int MAX_REGIONS=256, MAX_VALUE=15, MAX_VISITS=24;
    public record Frame(Map<PathogenSpatialFieldMath.Cell,int[]> cells,
                        int nextCursor,int visited,int changed){
        public Frame{cells=deepCopy(cells);}
    }
    public static Map<PathogenSpatialFieldMath.Cell,int[]> deepCopy(
        Map<PathogenSpatialFieldMath.Cell,int[]> input){
        Map<PathogenSpatialFieldMath.Cell,int[]> result=new HashMap<>();
        if(input.size()>MAX_REGIONS)
            throw new IllegalArgumentException("Too many decorative game patches");
        for(var entry:input.entrySet()){
            if(entry.getKey()==null||entry.getValue()==null||
                    entry.getValue().length!=CHANNELS)
                throw new IllegalArgumentException("Malformed eight-channel art data");
            int[] numbers=entry.getValue().clone();
            for(int n:numbers)if(n<0||n>MAX_VALUE)
                throw new IllegalArgumentException("Illegal invented score");
            result.put(entry.getKey(),numbers);
        }
        return result;
    }
    public static Frame advance(Map<PathogenSpatialFieldMath.Cell,int[]> current,
                int cursor,long frame,Predicate<PathogenSpatialFieldMath.Cell> active){
        if(frame<0)throw new IllegalArgumentException("Negative Minecraft frame");
        var prior=deepCopy(current);
        if(prior.isEmpty())return new Frame(Map.of(),0,0,0);
        var positions=new ArrayList<>(prior.keySet());
        positions.sort(Comparator.comparingInt(PathogenSpatialFieldMath.Cell::x)
                .thenComparingInt(PathogenSpatialFieldMath.Cell::z));
        int start=Math.floorMod(cursor,positions.size());
        int count=Math.min(MAX_VISITS,positions.size());
        var next=deepCopy(prior);
        int changes=0;
        for(int visit=0;visit<count;visit++){
            var source=positions.get((start+visit)%positions.size());
            if(!active.test(source))continue;
            int[] values=prior.get(source);
            int ch=(int)Math.floorMod(frame+visit,CHANNELS);
            // Virtual-only phages are purely microscopic reference data;
            // they have no corresponding vanilla world block/animal.
            if(EcologyExtendedTaxa.ENTRIES.get(ch).virtualOnly())continue;
            int score=values[ch];
            if(score<5)continue;  // Not a viability or reproductive threshold.
            var destination=source.adjacent((int)Math.floorMod(
                    frame+source.x()*7L+source.z()*11L,4L));
            if(!active.test(destination))continue;
            int[] target=next.get(destination);
            if(target==null){
                if(next.size()>=MAX_REGIONS)continue;
                target=new int[CHANNELS];
                next.put(destination,target);
            }
            int visual=Math.max(target[ch],Math.max(1,score/2));
            if(visual>target[ch]){
                target[ch]=Math.min(MAX_VALUE,visual);
                changes++;
            }
        }
        // Score modulation is intentionally independent of real organisms.
        if(frame>0&&frame%12==0){
            for(var entry:next.entrySet()){
                int[] values=entry.getValue();
                for(int i=0;i<CHANNELS;i++){
                    if(values[i]>0){values[i]--;changes++;}
                }
            }
            next.entrySet().removeIf(e->Arrays.stream(e.getValue()).allMatch(n->n==0));
        }
        return new Frame(next,(start+count)%positions.size(),count,changes);
    }
}
