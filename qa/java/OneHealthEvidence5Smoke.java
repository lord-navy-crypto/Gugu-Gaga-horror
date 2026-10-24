import io.github.lordnavycrypto.gugugaga.pathogens.*;
import java.util.*;
public final class OneHealthEvidence5Smoke {
    static int checks;
    static void yes(boolean ok,String message) {checks++;if(!ok)throw new AssertionError(message);}
    public static void main(String[]args) {
        yes(OneHealthEvidence5.FACTS.size()==5,"five curated source records");
        for(var profile:FocusFiveProfiles.FIVE){
            var fact=OneHealthEvidence5.byId(profile.id());
            yes(fact.sourceUrl().startsWith("https://"),"public source");
            yes(!fact.responsibleAction().isBlank(),"appropriate professional action");
            yes(!fact.observationCaution().isBlank(),"uncertainty documented");
            for(int number=0;number<=15;number++){
                var result=OneHealthObservation.interpret(profile.id(),4,number);
                yes(result.virtualGameScore()==number,"invented game indicator unchanged");
                yes(result.vanillaObjects()==4,"real vanilla-object observation count");
                yes(!result.message().contains("diagnosed"),"never diagnose a patient");
            }
        }
        var first=FocusFiveProfiles.FIVE.get(0);
        yes(OneHealthObservation.interpret(first.id(),0,15).state()
                ==OneHealthObservation.EvidenceState.NO_MATCHING_VANILLA_OBJECTS,
                "even a high game score cannot create a host");
        yes(OneHealthObservation.interpret(first.id(),1,0).state()
                ==OneHealthObservation.EvidenceState.GAME_OBJECTS_PRESENT,
                "seeing an animal does not imply disease");
        yes(OneHealthObservation.interpret(first.id(),1,15).state()
                ==OneHealthObservation.EvidenceState.GAME_SIGNAL_VISIBLE,
                "higher game indicators remain explicitly fictional");
        yes(OneHealthEvidence5.byId("varroa_destructor").domain()
                ==OneHealthEvidence5.Field.APICULTURE,"bee-health domain distinct");
        try{
            OneHealthObservation.interpret(first.id(),1000,7);
            throw new AssertionError("unbounded scan accepted");
        }catch(IllegalArgumentException expected){checks++;}
        System.out.println("GUGUGAGA_ONE_HEALTH_EVIDENCE_PASS checks="+checks
                +" noInfectiousRates=true noMedicalDiagnosis=true");
    }
}
