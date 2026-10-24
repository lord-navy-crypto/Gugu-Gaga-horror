package io.github.lordnavycrypto.gugugaga.pathogens;

/**
 * Descriptive, read-only Minecraft environment observation. These values
 * NEVER estimate real pathogen fitness, survival or infection risk.
 */
public final class FieldEnvironmentObservation {
    private FieldEnvironmentObservation() {}

    public enum Habitat {
        UNLOADED, AQUATIC, CULTIVATED, VEGETATED, EXPOSED, SHELTERED
    }

    public record Sample(boolean loaded, boolean waterHere,
            boolean farmlandBelow, boolean vegetationHere, boolean localRain,
            boolean skyVisible, int skyLight, float biomeTemperatureIndex) {
        public Sample {
            if (skyLight < 0 || skyLight > 15
                    || !Float.isFinite(biomeTemperatureIndex))
                throw new IllegalArgumentException("Invalid Minecraft observation");
        }
    }

    /** Classifies a Minecraft block observation, not a host/pathogen. */
    public static Habitat classify(Sample sample) {
        if (sample == null) throw new NullPointerException("sample");
        if (!sample.loaded()) return Habitat.UNLOADED;
        if (sample.waterHere()) return Habitat.AQUATIC;
        if (sample.farmlandBelow()) return Habitat.CULTIVATED;
        if (sample.vegetationHere()) return Habitat.VEGETATED;
        return sample.skyVisible() ? Habitat.EXPOSED : Habitat.SHELTERED;
    }

    public static String describe(Sample sample) {
        Habitat habitat = classify(sample);
        if (habitat == Habitat.UNLOADED)
            return "UNLOADED: Minecraft observation skipped (no chunk load)";
        return "Minecraft habitat=" + habitat
                + "; water=" + sample.waterHere()
                + "; farmland=" + sample.farmlandBelow()
                + "; vegetation=" + sample.vegetationHere()
                + "; local-rain=" + sample.localRain()
                + "; sky-visible=" + sample.skyVisible()
                + "; sky-light=" + sample.skyLight() + "/15"
                + "; biome-temperature-index=" + sample.biomeTemperatureIndex()
                + " (game index, not Celsius or real pathogen fitness)";
    }
}
