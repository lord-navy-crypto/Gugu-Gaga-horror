package io.github.lordnavycrypto.gugugaga.pathogens;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/** Rotating, bounded Minecraft save-entry processing, not epidemiology. */
public final class PathogenSceneFairSchedule {
    private PathogenSceneFairSchedule(){}
    public record Batch<T>(List<T> orderedCandidates,int nextCursor) {
        public Batch{orderedCandidates=List.copyOf(orderedCandidates);}
    }
    public static <T> Batch<T> rotate(List<T> input,int cursor,int budget){
        Objects.requireNonNull(input);
        if(budget<1||budget>512||input.size()>512)
            throw new IllegalArgumentException("Invalid performance budget");
        if(input.isEmpty())return new Batch<>(List.of(),0);
        int size=input.size(),start=Math.floorMod(cursor,size);
        List<T> out=new ArrayList<>(size);
        for(int i=0;i<size;i++)out.add(input.get((start+i)%size));
        return new Batch<>(out,(start+Math.min(size,budget))%size);
    }
}
