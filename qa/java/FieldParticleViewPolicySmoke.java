import io.github.lordnavycrypto.gugugaga.pathogens.FieldParticleViewPolicy;
import java.util.HashSet;

public final class FieldParticleViewPolicySmoke {
    private static int checks;
    private static void ok(boolean passes, String reason) {
        checks++;
        if (!passes) throw new AssertionError(reason);
    }
    public static void main(String[] ignored) {
        var p = FieldParticleViewPolicy.class;
        ok(FieldParticleViewPolicy.insideCell(-1, -4, -1, -1),
                "negative coordinates use floorDiv");
        ok(!FieldParticleViewPolicy.insideCell(-1, -4, 0, -1),
                "unrelated neighboring field excluded");
        ok(FieldParticleViewPolicy.insideCell(7, 11, 1, 2),
                "4x4 field membership");
        ok(FieldParticleViewPolicy.surfaceInView(64.0, 67),
                "surface may anchor near camera");
        ok(!FieldParticleViewPolicy.surfaceInView(10, 90),
                "no faraway surface particles in caves");
        ok(FieldParticleViewPolicy.animalInView(63, 65),
                "nearby animals can anchor cosmetic signals");
        ok(!FieldParticleViewPolicy.animalInView(63, 80),
                "unrelated far-height animals rejected");
        for (int population : new int[]{1,2,12,13,24,25,36,97,200}) {
            var seen = new HashSet<Integer>();
            int frames = (population + 11) / 12;
            for (int frame = 0; frame < frames; frame++) {
                var slots = FieldParticleViewPolicy.viewerSlots(population, 12, frame);
                ok(slots.size() <= 12, "twelve observers per frame maximum");
                ok(new HashSet<>(slots).size() == slots.size(),
                        "no duplicate observer in one frame");
                seen.addAll(slots);
            }
            ok(seen.size() == population,
                    "all opted-in observers serviced fairly");
        }
        ok(FieldParticleViewPolicy.viewerSlots(0,12,0).isEmpty(),
                "zero observers does no work");
        System.out.println("FIELD_PARTICLE_V17_VIEW_POLICY_PASS checks="+checks+
                " cosmeticOnly=true playerIsolation=true");
    }
}
