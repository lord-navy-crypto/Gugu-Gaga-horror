package io.github.lordnavycrypto.gugugaga.pathogens;

/**
 * Extra, OPTIONAL botanical mini-game based solely on invented *game*
 * signal scores. Does not estimate plant infection, real fungal growth,
 * actual disease incidence, moisture response or biological transmission.
 */
public final class VanillaCropGameFeedback {
    private VanillaCropGameFeedback(){}
    public enum Growth { UNCHANGED, SHORT_GAME_PAUSE }
    // Cap gameplay impact, never stop a crop permanently.
    public static Growth growth(String blockId,int extendedScore,
                                int blockX,int blockZ,long gameTick){
        if(extendedScore<0||extendedScore>15||gameTick<0)
            throw new IllegalArgumentException("Invalid Minecraft game input");
        if(!"minecraft:wheat".equals(blockId)||extendedScore<10)
            return Growth.UNCHANGED;
        // Fictional deterministic 1-in-4 growth-attempt cue, reversible when
        // the local in-game score drops or the optional feature is disabled.
        int phase=Math.floorMod(gameTick+((long)blockX*17L)
                +((long)blockZ*31L),4);
        return phase==0?Growth.SHORT_GAME_PAUSE:Growth.UNCHANGED;
    }
}
