package io.github.lordnavycrypto.gugugaga.microbes;

import java.util.Objects;

/**
 * Bounded agent-based Minecraft population GAME, attached to each real mob.
 *
 * Numbers are abstract "visual population units", NOT microbial counts,
 * infective virions, growth rates, transmission coefficients, dose, timing,
 * host susceptibility or any experimental biological parameter.
 */
public final class LivingMicrobePopulation {
    private LivingMicrobePopulation(){}

    public enum Host { BIRD, MAMMAL }
    public enum Mode { BACTERIA, PHAGE_HOST_DISPLAY, VIRUS_HOST_DISPLAY }
    public enum VirusContext { NONE, RABIES_MAMMAL, AVIAN_BIRD }

    public static final int MAX_GAME_POPULATION=12;
    public static final int MAX_SIGNAL=3;
    public record Colony(int bacteria,int phageSignal,int virusSignal,
                         Mode mode,int gameSteps) {
        public Colony {
            Objects.requireNonNull(mode);
            if(bacteria<0||bacteria>MAX_GAME_POPULATION
                ||phageSignal<0||phageSignal>MAX_SIGNAL
                ||virusSignal<0||virusSignal>MAX_SIGNAL
                ||gameSteps<0||gameSteps>2048)
                throw new IllegalArgumentException("Invalid bounded microbe game state");
        }
    }

    public static Colony newStudy() {
        // Starting "population units" are illustrative, not an animal diagnosis.
        return new Colony(4,0,0,Mode.BACTERIA,0);
    }

    public static VirusContext documentedScenario(Host host) {
        return switch (Objects.requireNonNull(host)) {
            case BIRD -> VirusContext.AVIAN_BIRD;
            case MAMMAL -> VirusContext.RABIES_MAMMAL;
        };
    }

    /** A PLAYER must explicitly select each scenario; never auto-infect hosts. */
    public static Colony cycleMode(Colony current) {
        Objects.requireNonNull(current);
        Mode next=switch(current.mode()) {
            case BACTERIA -> Mode.PHAGE_HOST_DISPLAY;
            case PHAGE_HOST_DISPLAY -> Mode.VIRUS_HOST_DISPLAY;
            case VIRUS_HOST_DISPLAY -> Mode.BACTERIA;
        };
        return new Colony(current.bacteria(),0,0,next,current.gameSteps());
    }

    /**
     * One budgeted game-step for ONE enrolled animal.
     * Bacteria represent host-associated fictional study units; phage markers
     * indicate a source-documented T4/E. coli relationship but never claim
     * strain compatibility or actual viral replication. Virus markers mean
     * selected host-context scenarios, not actual infection.
     */
    public static Colony step(Colony current,Host host,boolean dampMinecraftWeather) {
        Objects.requireNonNull(current);
        Objects.requireNonNull(host);
        int bacteria=current.bacteria();
        int phage=0,virus=0;

        if(current.mode()==Mode.BACTERIA) {
            // A deliberately coarse game fluctuation, not biology kinetics.
            bacteria=Math.max(0,Math.min(MAX_GAME_POPULATION,
                    bacteria+(dampMinecraftWeather?1:-1)));
        }else if(current.mode()==Mode.PHAGE_HOST_DISPLAY) {
            if(bacteria>0) {
                phage=(current.phageSignal()+1)%(MAX_SIGNAL+1);
                // Game ecological competition, not any quantified T4 lysis.
                bacteria=Math.max(0,bacteria-1);
            }
        }else if(current.mode()==Mode.VIRUS_HOST_DISPLAY) {
            // A host context marker only; NEVER generates new infected mobs.
            virus=(current.virusSignal()+1)%(MAX_SIGNAL+1);
        }
        return new Colony(bacteria,phage,virus,current.mode(),
                          Math.min(2048,current.gameSteps()+1));
    }
}
