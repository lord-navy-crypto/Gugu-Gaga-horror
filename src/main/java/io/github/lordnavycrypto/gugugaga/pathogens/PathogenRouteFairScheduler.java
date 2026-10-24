package io.github.lordnavycrypto.gugugaga.pathogens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Reproducible round-robin selection over at most 1,024 existing Minecraft
 * DATA cells. Selection and cursor advancement never depend on whether an
 * area is currently loaded or whether its abstract signals can move.
 */
public final class PathogenRouteFairScheduler {
    private PathogenRouteFairScheduler(){}

    public static final int MAX_SCAN_PER_FRAME=64;
    public record Batch(List<PathogenSpatialFieldMath.Cell> cells,int nextCursor){
        public Batch { cells=List.copyOf(cells); }
    }
    /**
     * Rotate the first channel considered at each cell. Without this, the
     * lowest-index game taxon can monopolize a busy 15-channel source.
     */
    public static int startChannel(PathogenSpatialFieldMath.Cell cell,long gameFrame){
        Objects.requireNonNull(cell);
        return (int)Math.floorMod(gameFrame+cell.x()*3L+cell.z()*5L,
                PathogenSpatialFieldMath.CHANNELS);
    }
    public static Batch next(Collection<PathogenSpatialFieldMath.Cell> coordinates,
                             int cursor,int budget){
        Objects.requireNonNull(coordinates);
        if(budget<1||budget>MAX_SCAN_PER_FRAME||
                coordinates.size()>PathogenSpatialFieldMath.MAX_CELLS)
            throw new IllegalArgumentException("Invalid bounded event-scan budget");
        if(coordinates.isEmpty())return new Batch(List.of(),0);
        List<PathogenSpatialFieldMath.Cell> sorted=new ArrayList<>(coordinates);
        sorted.sort(Comparator.comparingInt(PathogenSpatialFieldMath.Cell::x)
                .thenComparingInt(PathogenSpatialFieldMath.Cell::z));
        int start=Math.floorMod(cursor,sorted.size());
        int count=Math.min(budget,sorted.size());
        List<PathogenSpatialFieldMath.Cell> chosen=new ArrayList<>(count);
        for(int i=0;i<count;i++)chosen.add(sorted.get((start+i)%sorted.size()));
        return new Batch(chosen,(start+count)%sorted.size());
    }
}
