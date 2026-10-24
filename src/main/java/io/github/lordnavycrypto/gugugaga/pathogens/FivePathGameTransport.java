package io.github.lordnavycrypto.gugugaga.pathogens;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Bounded educational GAME information propagation on the already-existing
 * sparse 4x4 Minecraft world grid. Not a physical dispersion model.
 *
 * All five categories share precisely the same arbitrary toy scoring rule;
 * the ONLY route difference is which vanilla game-world observation admits
 * a neighboring map update. No real-pathogen distance, time or growth terms.
 */
public final class FivePathGameTransport {
    private FivePathGameTransport(){}
    public static final int MAX_SOURCES_PER_PROFILE=12,MAX_EVENTS_PER_FRAME=20;
    public static final int TOY_MIN_LEVEL=3;
    public record Proposal(FivePathGameRoutes.Profile profile,
                           PathogenSpatialFieldMath.Cell origin,
                           PathogenSpatialFieldMath.Cell destination,
                           int oldDestination,int newDestination) {}
    public record Frame(List<Proposal> proposals,int scanned){
        public Frame{proposals=List.copyOf(proposals);}
    }
    public static int nextGameScore(int from,int to,boolean gameTagAccepts){
        if(from<0||from>15||to<0||to>15)
            throw new IllegalArgumentException("Invented game values must be 0..15");
        if(!gameTagAccepts||from<TOY_MIN_LEVEL||to>=from)return to;
        return Math.max(to,Math.min(15,Math.max(1,from/2)));
    }
    public static Frame plan(
        Map<PathogenSpatialFieldMath.Cell,int[]> original15,
        Map<PathogenSpatialFieldMath.Cell,int[]> extended8,
        long frame,
        Predicate<PathogenSpatialFieldMath.Cell> activeLoaded,
        BiPredicate<FivePathGameRoutes.Profile,PathogenSpatialFieldMath.Cell>
                gameHostTagPresent) {
        if(frame<0)throw new IllegalArgumentException("Invalid frame");
        Objects.requireNonNull(activeLoaded);
        Objects.requireNonNull(gameHostTagPresent);
        if(original15.size()>PathogenSpatialFieldMath.MAX_CELLS||
           extended8.size()>EcologyExtensionToyField.MAX_REGIONS)
            throw new IllegalArgumentException("World game data cap exceeded");
        List<Proposal> planned=new ArrayList<>();
        int scanned=0;
        // Two existing SavedData schemas are read-only to the planner.
        for(var profile:FivePathGameRoutes.FIVE){
            var identity=FocusFiveProfiles.byId(profile.id());
            var snapshot=identity.store()==FocusFiveProfiles.Store.ORIGINAL_15
                    ?original15:extended8;
            var candidates=new ArrayList<>(snapshot.keySet());
            candidates.sort(Comparator.comparingInt(PathogenSpatialFieldMath.Cell::x)
                .thenComparingInt(PathogenSpatialFieldMath.Cell::z));
            if(candidates.isEmpty())continue;
            int start=(int)Math.floorMod(frame+identity.channel(),candidates.size());
            int n=Math.min(MAX_SOURCES_PER_PROFILE,candidates.size());
            for(int scan=0;scan<n;scan++){
                if(planned.size()>=MAX_EVENTS_PER_FRAME)
                    return new Frame(planned,scanned);
                var from=candidates.get((start+scan)%candidates.size());
                scanned++;
                if(!activeLoaded.test(from))continue;
                int[] numbers=snapshot.get(from);
                if(numbers==null||
                   numbers.length!=(identity.store()==FocusFiveProfiles.Store.ORIGINAL_15?15:8))
                    throw new IllegalArgumentException("Malformed original/extension field");
                int score=numbers[identity.channel()];
                if(score<TOY_MIN_LEVEL)continue;
                var to=from.adjacent((int)Math.floorMod(frame+from.x()*7L+
                    from.z()*11L+identity.channel()*3L,4L));
                if(!activeLoaded.test(to)||!gameHostTagPresent.test(profile,to))
                    continue;
                int[] dest=snapshot.get(to);
                int old=dest==null?0:dest[identity.channel()];
                int next=nextGameScore(score,old,true);
                if(next<=old)continue;
                if(dest==null&&snapshot.size()>=
                        (identity.store()==FocusFiveProfiles.Store.ORIGINAL_15
                         ?PathogenSpatialFieldMath.MAX_CELLS
                         :EcologyExtensionToyField.MAX_REGIONS))
                    continue;
                planned.add(new Proposal(profile,from,to,old,next));
            }
        }
        return new Frame(planned,scanned);
    }
}
