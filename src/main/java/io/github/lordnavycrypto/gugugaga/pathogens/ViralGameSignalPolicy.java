package io.github.lordnavycrypto.gugugaga.pathogens;

/**
 * Entirely fictional Minecraft world-data wave. A game signal is an integer,
 * NOT virions, infectiousness, virus survival, a transmission probability,
 * or a reproduction mechanism. Every viral label uses the SAME game rule.
 * This never creates a Block, BlockEntity, Item or Entity.
 */
public final class ViralGameSignalPolicy {
    private ViralGameSignalPolicy(){}
    public static final int MIN_SOURCE_GAME_SIGNAL=3;
    public static final int MAX_GAME_SIGNAL=PathogenSpatialFieldMath.MAX_PRESSURE;

    public static boolean isVirusChannel(int channel){
        if(channel<0||channel>=PathogenRoster15.FIRST_FIFTEEN.size())
            throw new IllegalArgumentException("Unknown game channel");
        return PathogenRoster15.FIRST_FIFTEEN.get(channel).type()
                ==PathogenRoster15.Type.VIRUS;
    }

    /** Game-only destination level for one adjacent 4x4-block area. */
    public static int nextLevel(int channel,int source,int destination){
        if(source<0||source>MAX_GAME_SIGNAL
                ||destination<0||destination>MAX_GAME_SIGNAL)
            throw new IllegalArgumentException("Invalid game signal");
        if(!isVirusChannel(channel)||source<MIN_SOURCE_GAME_SIGNAL
                ||source<=destination)return destination;
        // This is a legibility score, not replication or biological growth.
        int pulse=Math.max(1,source/2);
        return Math.min(MAX_GAME_SIGNAL,Math.max(destination+1,pulse));
    }
    public static int direction(PathogenSpatialFieldMath.Cell at,int channel,long frame){
        if(frame<0)throw new IllegalArgumentException("Invalid game frame");
        isVirusChannel(channel);
        return (int)Math.floorMod(at.x()*7L+at.z()*11L+
                frame+channel*3L,4L);
    }
}
