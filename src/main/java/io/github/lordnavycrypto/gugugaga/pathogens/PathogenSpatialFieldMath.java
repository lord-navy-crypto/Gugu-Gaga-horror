package io.github.lordnavycrypto.gugugaga.pathogens;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Bounded, abstract spatial scalar-field game engine. One cell represents a
 * 4x4-block Minecraft horizontal area; 15 channels correspond to taxon labels.
 *
 * Pressure 0..15, spill at 6, and decay period 8 are ARBITRARY GAME SETTINGS.
 * They are NOT pathogen survival, physical diffusion, infectious dose, growth,
 * airborne dispersion, virion numbers or transmissibility coefficients.
 * No microbes are implemented as Block or Entity.
 */
public final class PathogenSpatialFieldMath {
    private PathogenSpatialFieldMath(){}
    public static final int CHANNELS=15;
    public static final int MAX_PRESSURE=15;
    public static final int MAX_CELLS=1024;
    public static final int CELL_EDGE_BLOCKS=4;
    public static final int SPILL_THRESHOLD=6;
    public static final int DECAY_PERIOD=8;

    public record Cell(int x,int z) {
        public Cell adjacent(int dir){
            return switch(Math.floorMod(dir,4)){
                case 0 -> new Cell(x+1,z);
                case 1 -> new Cell(x,z+1);
                case 2 -> new Cell(x-1,z);
                default -> new Cell(x,z-1);
            };
        }
    }
    public record Frame(Map<Cell,int[]> cells,int nextCursor,int processed,
                        int moved,int faded) {
        public Frame{
            cells=immutableCopy(cells);
        }
    }
    public static Cell fromBlock(int blockX,int blockZ){
        return new Cell(Math.floorDiv(blockX,CELL_EDGE_BLOCKS),
                        Math.floorDiv(blockZ,CELL_EDGE_BLOCKS));
    }
    public static int channel(String realTaxonId) {
        Objects.requireNonNull(realTaxonId);
        for(int i=0;i<PathogenRoster15.FIRST_FIFTEEN.size();i++)
            if(PathogenRoster15.FIRST_FIFTEEN.get(i).id().equals(realTaxonId))
                return i;
        throw new IllegalArgumentException("Unknown taxon channel");
    }
    public static Map<Cell,int[]> immutableCopy(Map<Cell,int[]> input) {
        Map<Cell,int[]> out=new HashMap<>();
        for(var e:input.entrySet()){
            check(e.getValue());
            out.put(Objects.requireNonNull(e.getKey()),
                    Arrays.copyOf(e.getValue(),CHANNELS));
        }
        return Map.copyOf(out); // Values must still be treated as read-only.
    }
    private static void check(int[] channels){
        if(channels==null || channels.length!=CHANNELS)
            throw new IllegalArgumentException("Expected 15 integer channels");
        for(int v:channels)
            if(v<0||v>MAX_PRESSURE)
                throw new IllegalArgumentException("Out-of-bounds game pressure");
    }
    private static int[] copyOrEmpty(Map<Cell,int[]> map,Cell at){
        return map.containsKey(at)
                ?Arrays.copyOf(map.get(at),CHANNELS):new int[CHANNELS];
    }
    public static Map<Cell,int[]> withPressure(Map<Cell,int[]> original,
            Cell cell,int channel,int level){
        Objects.requireNonNull(original);
        Objects.requireNonNull(cell);
        if(channel<0||channel>=CHANNELS||level<0||level>MAX_PRESSURE)
            throw new IllegalArgumentException("Invalid field input");
        if(!original.containsKey(cell)&&level>0&&original.size()>=MAX_CELLS)
            return immutableCopy(original);
        Map<Cell,int[]> out=new HashMap<>(immutableCopy(original));
        int[] v=copyOrEmpty(out,cell);
        v[channel]=level;
        if(zero(v))out.remove(cell);
        else out.put(cell,v);
        return immutableCopy(out);
    }
    private static boolean zero(int[] a){
        for(int n:a)if(n!=0)return false;
        return true;
    }

    /**
     * Simultaneous snapshot update. At most budget existing field cells
     * are processed. A cell can give at most one abstract pressure unit
     * per channel to one of four neighboring cells per game frame.
     * Neighbor additions never act as a new source within the same frame.
     * Adjacent cells must pass an external loaded-chunk / active-player check.
     */
    public static Frame step(Map<Cell,int[]> old,int cursor,
            int budget,long gameStep,Predicate<Cell> loadedAndActive){
        Objects.requireNonNull(old);
        Objects.requireNonNull(loadedAndActive);
        if(budget<1||budget>128||old.size()>MAX_CELLS||gameStep<0)
            throw new IllegalArgumentException("Invalid bounded spatial update");
        var snapshot=immutableCopy(old);
        if(snapshot.isEmpty())return new Frame(Map.of(),0,0,0,0);
        List<Cell> keys=new ArrayList<>(snapshot.keySet());
        keys.sort((a,b)->{
            int x=Integer.compare(a.x(),b.x());
            return x!=0?x:Integer.compare(a.z(),b.z());
        });
        int start=Math.floorMod(cursor,keys.size());
        Map<Cell,int[]> updated=new HashMap<>();
        snapshot.forEach((cell,pressure)->updated.put(cell,Arrays.copyOf(pressure,CHANNELS)));
        int scanned=0,moved=0,faded=0;
        int visited=0;
        for(;visited<keys.size()&&visited<budget;visited++){
            Cell cell=keys.get((start+visited)%keys.size());
            if(!loadedAndActive.test(cell))continue;
            scanned++;
            int[] source=snapshot.get(cell);
            for(int ch=0;ch<CHANNELS;ch++){
                if(source[ch]==0)continue;
                int direction=Math.floorMod(
                        cell.x()*17+cell.z()*31+ch*7+(int)(gameStep&3L),4);
                Cell target=cell.adjacent(direction);
                if(source[ch]>=SPILL_THRESHOLD
                        &&PathogenRoutePolicy15.allows(ch,
                                PathogenRoutePolicy15.Route.LOCAL_GAME_FIELD)
                        &&loadedAndActive.test(target)
                        &&(snapshot.containsKey(target)
                           ||updated.containsKey(target)
                           ||updated.size()<MAX_CELLS)) {
                    int[] into=updated.get(target);
                    if(into==null){
                        into=new int[CHANNELS];
                        updated.put(target,into);
                    }
                    if(into[ch]<MAX_PRESSURE && updated.get(cell)[ch]>0){
                        updated.get(cell)[ch]--;
                        into[ch]++;
                        moved++;
                    }
                }
                if(gameStep>0&&gameStep%DECAY_PERIOD==0&&updated.get(cell)[ch]>0){
                    updated.get(cell)[ch]--;
                    faded++;
                }
            }
        }
        updated.entrySet().removeIf(entry->zero(entry.getValue()));
        int newCursor=(start+visited)%keys.size();
        return new Frame(updated,newCursor,scanned,moved,faded);
    }
}
