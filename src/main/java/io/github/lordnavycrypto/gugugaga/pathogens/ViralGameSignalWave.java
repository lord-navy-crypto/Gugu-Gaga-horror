package io.github.lordnavycrypto.gugugaga.pathogens;
import java.util.*;
import java.util.function.Predicate;

/**
 * Small fictional Minecraft DATA-wave pass, not real virus propagation.
 * Eight virus-labelled channels move bounded symbolic GAME scores.
 * Every increase at a destination is debited from its source: exact
 * discrete conservation, NOT biological mass, infectivity or kinetics.
 * Snapshot input prevents new destinations from acting in this frame.
 * Both source and destination must be active and already chunk-loaded
 * according to the caller; at most 64 sources / 1024 world cells.
 */
public final class ViralGameSignalWave {
    private ViralGameSignalWave(){}
    public static final int MAX_SOURCES=64;
    public record Frame(Map<PathogenSpatialFieldMath.Cell,int[]> cells,
                        int nextCursor,int visited,int emitted,int newCells) {
        public Frame{cells=PathogenSpatialFieldMath.immutableCopy(cells);}
    }
    public static Frame advance(
            Map<PathogenSpatialFieldMath.Cell,int[]> before,
            int cursor,int budget,long gameFrame,
            Predicate<PathogenSpatialFieldMath.Cell> loadedActive){
        Objects.requireNonNull(before);
        Objects.requireNonNull(loadedActive);
        if(gameFrame<0||budget<1||budget>MAX_SOURCES||
                before.size()>PathogenSpatialFieldMath.MAX_CELLS)
            throw new IllegalArgumentException("Invalid game wave budget");
        var snapshot=PathogenSpatialFieldMath.immutableCopy(before);
        var visit=PathogenRouteFairScheduler.next(snapshot.keySet(),cursor,budget);
        if(snapshot.isEmpty())return new Frame(Map.of(),0,0,0,0);
        Map<PathogenSpatialFieldMath.Cell,int[]> result=new HashMap<>();
        snapshot.forEach((cell,levels)->result.put(cell,levels.clone()));
        int emitted=0,created=0;
        for(var sourceCell:visit.cells()){
            if(!loadedActive.test(sourceCell))continue;
            int[] source=snapshot.get(sourceCell);
            int start=PathogenRouteFairScheduler.startChannel(sourceCell,gameFrame);
            for(int offset=0;offset<15;offset++){
                int channel=(start+offset)%15;
                if(!ViralGameSignalPolicy.isVirusChannel(channel)||
                        source[channel]<ViralGameSignalPolicy.MIN_SOURCE_GAME_SIGNAL)
                    continue;
                var destination=sourceCell.adjacent(
                    ViralGameSignalPolicy.direction(sourceCell,channel,gameFrame));
                if(!loadedActive.test(destination))continue;
                int[] target=result.get(destination);
                if(target==null &&
                        result.size()>=PathogenSpatialFieldMath.MAX_CELLS)
                    continue;
                int beforeLevel=target==null?0:target[channel];
                int desired=ViralGameSignalPolicy.nextLevel(channel,
                        source[channel],beforeLevel);
                // Only the initial snapshot can fund the move; don't duplicate
                // virtual units or send just-received numbers the same frame.
                int[] sourceNow=result.get(sourceCell);
                int transfer=Math.min(Math.max(0,desired-beforeLevel),
                        Math.min(source[channel],sourceNow[channel]));
                if(transfer>0){
                    if(target==null){
                        target=new int[15];
                        result.put(destination,target);
                        created++;
                    }
                    sourceNow[channel]-=transfer;
                    target[channel]+=transfer;
                    emitted++;
                }
                break; // At most one numeric event per source and tick.
            }
        }
        result.entrySet().removeIf(entry->{
            for(int n:entry.getValue())if(n>0)return false;
            return true;
        });
        return new Frame(result,visit.nextCursor(),visit.cells().size(),
                emitted,created);
    }
}
