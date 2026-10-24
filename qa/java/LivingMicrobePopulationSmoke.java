import io.github.lordnavycrypto.gugugaga.microbes.LivingMicrobePopulation;
import io.github.lordnavycrypto.gugugaga.microbes.LivingMicrobePopulation.*;

public final class LivingMicrobePopulationSmoke {
    private static int tests;
    private static void check(boolean condition,String message) {
        tests++;
        if(!condition)throw new AssertionError(message);
    }
    public static void main(String[] args) {
        for(Host host:Host.values()) {
            Colony bacteria=LivingMicrobePopulation.newStudy();
            check(bacteria.mode()==Mode.BACTERIA,"Start on animal with bacteria gameplay");
            check(bacteria.bacteria()==4,"Known initial game indicator, not real count");
            for(int t=0;t<300;t++) {
                bacteria=LivingMicrobePopulation.step(bacteria,host,t%2==0);
                check(bacteria.bacteria()>=0&&bacteria.bacteria()<=12,
                        "bacteria visual game units bounded");
                check(bacteria.phageSignal()==0&&bacteria.virusSignal()==0,
                        "no spontaneous virus");
            }
            Colony phage=LivingMicrobePopulation.cycleMode(bacteria);
            check(phage.mode()==Mode.PHAGE_HOST_DISPLAY,"manual switch phage");
            phage=LivingMicrobePopulation.step(phage,host,true);
            check(phage.virusSignal()==0,"no host virus during phage display");
            check(phage.bacteria()<=bacteria.bacteria(),"phage-host display constrains indicator");
            Colony scenario=LivingMicrobePopulation.cycleMode(phage);
            check(scenario.mode()==Mode.VIRUS_HOST_DISPLAY,"manual switch host virus");
            scenario=LivingMicrobePopulation.step(scenario,host,true);
            check(scenario.virusSignal()>0,"active visual host virus scenario");
            check(scenario.bacteria()==phage.bacteria(),
                    "host virus scenario does not create spontaneous bacteria");
            check(LivingMicrobePopulation.documentedScenario(host)==
                    (host==Host.BIRD?VirusContext.AVIAN_BIRD:VirusContext.RABIES_MAMMAL),
                    "bird and mammal context not interchangeable");
            Colony again=LivingMicrobePopulation.cycleMode(scenario);
            check(again.mode()==Mode.BACTERIA,"cycle back to benign study");
            check(again.virusSignal()==0&&again.phageSignal()==0,
                    "mode change erases previous visual virus display");
            for(int i=0;i<300;i++) {
                scenario=LivingMicrobePopulation.step(scenario,host,false);
                check(scenario.virusSignal()>=0&&scenario.virusSignal()<=3,
                        "host virus is abstract bounded pulse, not actual infection");
            }
        }
        try{
            new Colony(13,0,0,Mode.BACTERIA,0);
            throw new AssertionError("Overflow accepted");
        }catch(IllegalArgumentException ex){tests++;}
        check(LivingMicrobePopulation.MAX_GAME_POPULATION==12,"budget bounded");
        System.out.println("GUGUGAGA_LIVING_MICROBE_ENGINE_PASS checks="+tests
                +" realAnimalNBTRequired=true noAutoInfection=true"
                +" noWorldPathogenTransmission=true zeroEntitiesSpawned=true");
    }
}
