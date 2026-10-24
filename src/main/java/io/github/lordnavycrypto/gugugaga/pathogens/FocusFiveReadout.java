package io.github.lordnavycrypto.gugugaga.pathogens;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/** Cosmetic gameplay indicators for five examples. NOT symptom scoring. */
public final class FocusFiveReadout {
    private FocusFiveReadout(){}
    public enum Tier { QUIET, TRACE, VISIBLE, HIGHLIGHT }
    public record Reading(FocusFiveProfiles.Profile profile,int gameValue,Tier tier){}
    public static Tier tier(int score){
        if(score<0||score>15)throw new IllegalArgumentException("Out of game bounds");
        if(score==0)return Tier.QUIET;
        if(score<5)return Tier.TRACE;
        if(score<10)return Tier.VISIBLE;
        return Tier.HIGHLIGHT;
    }
    public static int gameValue(FocusFiveProfiles.Profile profile,
                                int[] oldChannels,int[] extraChannels){
        Objects.requireNonNull(profile);
        int[] values=profile.store()==FocusFiveProfiles.Store.ORIGINAL_15
                ?oldChannels:extraChannels;
        int expected=profile.store()==FocusFiveProfiles.Store.ORIGINAL_15?15:8;
        if(values==null||values.length!=expected)
            throw new IllegalArgumentException("Unmatched saved-data schema");
        int score=values[profile.channel()];
        tier(score); // Validate numeric bounds independently of host.
        return score;
    }
    public static List<Reading> compatible(String vanillaId,boolean animal,
                                            int[] oldChannels,int[] extraChannels){
        Objects.requireNonNull(vanillaId);
        List<Reading> out=new ArrayList<>();
        for(var profile:FocusFiveProfiles.FIVE){
            if(!profile.matches(vanillaId,animal))continue;
            int score=gameValue(profile,oldChannels,extraChannels);
            out.add(new Reading(profile,score,tier(score)));
        }
        out.sort(Comparator.comparingInt(Reading::gameValue).reversed()
                .thenComparing(r->r.profile().id()));
        return List.copyOf(out);
    }
}
