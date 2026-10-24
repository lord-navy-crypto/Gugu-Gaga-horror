import io.github.lordnavycrypto.gugugaga.pathogens.FieldEnvironmentObservation;
import static io.github.lordnavycrypto.gugugaga.pathogens.FieldEnvironmentObservation.*;
public final class FieldEnvironmentObservationSmoke {
    static int assertions;
    static void ok(boolean yes, String message) {
        assertions++; if (!yes) throw new AssertionError(message);
    }
    static Sample sample(boolean water, boolean farm, boolean leaves, boolean sky) {
        return new Sample(true,water,farm,leaves,false,sky,12,0.8f);
    }
    public static void main(String[] args) {
        ok(classify(sample(true,true,true,true))==Habitat.AQUATIC,
                "water observation takes precedence");
        ok(classify(sample(false,true,true,true))==Habitat.CULTIVATED,
                "actual farmland is shown");
        ok(classify(sample(false,false,true,true))==Habitat.VEGETATED,
                "actual vegetation is shown");
        ok(classify(sample(false,false,false,true))==Habitat.EXPOSED,
                "sky-visible ground");
        ok(classify(sample(false,false,false,false))==Habitat.SHELTERED,
                "underground shelter");
        var unloaded = new Sample(false,false,false,false,false,false,0,0);
        ok(classify(unloaded)==Habitat.UNLOADED,
                "unloaded worlds never invent environmental readings");
        ok(describe(unloaded).contains("skipped"), "missingness is explicit");
        var rain = new Sample(true,false,false,false,true,true,8,1.25f);
        ok(describe(rain).contains("local-rain=true"),
                "actual Minecraft local precipitation indicated");
        ok(describe(rain).contains("not Celsius"),
                "game temperature index not confused with real weather");
        try { new Sample(true,false,false,false,false,false,16,0.0f);
            throw new AssertionError("invalid light accepted");
        } catch (IllegalArgumentException expected) { assertions++; }
        try { new Sample(true,false,false,false,false,false,3,Float.NaN);
            throw new AssertionError("invalid biome index accepted");
        } catch (IllegalArgumentException expected) { assertions++; }
        System.out.println("V19_MINECRAFT_ENVIRONMENT_PASS checks="+assertions
                +" noRealTransmissionParameters=true");
    }
}
