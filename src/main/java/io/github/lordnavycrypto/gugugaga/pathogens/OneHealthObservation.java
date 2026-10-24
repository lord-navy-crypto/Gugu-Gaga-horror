package io.github.lordnavycrypto.gugugaga.pathogens;

import java.util.Objects;

/**
 * Real counts of VANILLA Minecraft objects, but never real disease cases.
 * A game signal is explicitly separated from observations and source facts.
 */
public final class OneHealthObservation {
    private OneHealthObservation(){}
    public static final int MAX_HOSTS_SCANNED=32, MAX_CROPS_SCANNED=81;
    public enum EvidenceState {
        NO_MATCHING_VANILLA_OBJECTS,
        GAME_OBJECTS_PRESENT,
        GAME_SIGNAL_VISIBLE
    }
    public record Result(String label,OneHealthEvidence5.Field domain,
            int vanillaObjects,int virtualGameScore,EvidenceState state,
            String message){
        public Result{
            if(label.isBlank()||vanillaObjects<0
               ||vanillaObjects>MAX_CROPS_SCANNED
               ||virtualGameScore<0||virtualGameScore>15)
                throw new IllegalArgumentException("Invalid non-clinical observation");
        }
    }
    public static Result interpret(String taxonId,int matchingVanillaObjects,
                                    int virtualScore){
        var evidence=OneHealthEvidence5.byId(Objects.requireNonNull(taxonId));
        if(matchingVanillaObjects<0||matchingVanillaObjects>MAX_CROPS_SCANNED)
            throw new IllegalArgumentException("Outside sampling budget");
        var state=matchingVanillaObjects==0
                ?EvidenceState.NO_MATCHING_VANILLA_OBJECTS
                :virtualScore>=5?EvidenceState.GAME_SIGNAL_VISIBLE
                :EvidenceState.GAME_OBJECTS_PRESENT;
        String description=switch(state){
            case NO_MATCHING_VANILLA_OBJECTS -> "No matching vanilla object observed in this bounded survey.";
            case GAME_OBJECTS_PRESENT -> "Vanilla objects were observed; there is no diagnostic evidence.";
            case GAME_SIGNAL_VISIBLE -> "Only a fictional game-data indicator was displayed; no disease confirmed.";
        };
        return new Result(taxonId,evidence.domain(),matchingVanillaObjects,
                virtualScore,state,description);
    }
}
