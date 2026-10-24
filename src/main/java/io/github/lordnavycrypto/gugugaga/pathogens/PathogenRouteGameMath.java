package io.github.lordnavycrypto.gugugaga.pathogens;

import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Objects;

/**
 * An EVENT moves exactly one abstract Minecraft SIGNAL unit (never a pathogen
 * or infected host) from one loaded 4x4 game cell to another. No real-world
 * biological values, actual airborne dynamics, infection or laboratory model.
 */
public final class PathogenRouteGameMath {
    private PathogenRouteGameMath(){}
    public static final int MAX_ROUTE_STEPS=8;
    public record Transfer(Map<PathogenSpatialFieldMath.Cell,int[]> cells,
                           boolean applied,int sourceBefore,int targetAfter){
        public Transfer{cells=PathogenSpatialFieldMath.immutableCopy(cells);}
    }
    /**
     * O(1) preflight on two existing region records, unlike a full map clone.
     * Intended for the server-owned SavedData fast path. All numbers are game
     * tokens only, not actual biological rates or pathogen particles.
     */
    public static boolean canMove(Map<PathogenSpatialFieldMath.Cell,int[]> input,
            PathogenSpatialFieldMath.Cell source,PathogenSpatialFieldMath.Cell target,
            int channel,PathogenRoutePolicy15.Route route,boolean bothLoaded){
        Objects.requireNonNull(input);
        Objects.requireNonNull(source);
        Objects.requireNonNull(target);
        Objects.requireNonNull(route);
        if(channel<0||channel>=PathogenSpatialFieldMath.CHANNELS)
            throw new IllegalArgumentException("Invalid channel");
        long distance=Math.abs((long)source.x()-target.x())+
                Math.abs((long)source.z()-target.z());
        int[] from=input.get(source),to=input.get(target);
        return bothLoaded && !source.equals(target) && distance>=1
                && distance<=MAX_ROUTE_STEPS
                && PathogenRoutePolicy15.allows(channel,route)
                && from!=null && from[channel]>0
                && (to==null||to[channel]<PathogenSpatialFieldMath.MAX_PRESSURE)
                && (to!=null||input.size()<PathogenSpatialFieldMath.MAX_CELLS);
    }

    public static Transfer move(Map<PathogenSpatialFieldMath.Cell,int[]> input,
            PathogenSpatialFieldMath.Cell source,PathogenSpatialFieldMath.Cell target,
            int channel,PathogenRoutePolicy15.Route route,boolean bothLoaded) {
        boolean allowed=canMove(input,source,target,channel,route,bothLoaded);
        int[] from=input.get(source),to=input.get(target);
        int supply=from==null?0:from[channel];
        int targetLevel=to==null?0:to[channel];
        if(!allowed)
            return new Transfer(input,false,supply,targetLevel);
        Map<PathogenSpatialFieldMath.Cell,int[]> next=new HashMap<>(input);
        int[] left=Arrays.copyOf(from,15);
        int[] right=to==null?new int[15]:Arrays.copyOf(to,15);
        left[channel]--;
        right[channel]++;
        boolean empty=true;
        for(int n:left)if(n!=0){empty=false;break;}
        if(empty)next.remove(source);
        else next.put(source,left);
        next.put(target,right);
        return new Transfer(next,true,supply,right[channel]);
    }
}
