"""V19 guards: observe vanilla loaded blocks, never infer real infection rates."""
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[1]
JAVA = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga"
class EnvironmentAndAccounting(unittest.TestCase):
    def test_real_world_observation_is_not_fake_epidemiology(self):
        observation = (JAVA/"pathogens/FieldEnvironmentRuntime.java").read_text()
        pure = (JAVA/"pathogens/FieldEnvironmentObservation.java").read_text()
        command = (JAVA/"command/PathogenSpatialFieldCommands.java").read_text()
        self.assertIn("level.hasChunkAt(pos)",observation)
        self.assertIn("getFluidState(pos).is(FluidTags.WATER)",observation)
        self.assertIn("getBiome(pos).value().getBaseTemperature()",observation)
        self.assertIn("level.isRainingAt(pos)",observation)
        self.assertIn("getBrightness(LightLayer.SKY, pos)",observation)
        self.assertIn("getBlockState(pos.below()).is(Blocks.FARMLAND)",observation)
        self.assertIn("Float.isFinite",pure)
        self.assertIn("not Celsius or real pathogen fitness",pure)
        self.assertIn("FieldEnvironmentRuntime.observe(",command)
        for forbidden in ("setBlock(", "setHealth(", "addEffect(", "forceChunk(",
                          "infect(", "transmissionProbability(", "growthRate("):
            self.assertNotIn(forbidden,observation+pure)
    def test_numerical_work_cache_and_exact_validation(self):
        runtime=(JAVA/"pathogens/PathogenSpatialFieldRuntime.java").read_text()
        workflow=(ROOT/".github/workflows/build.yml").read_text()
        self.assertIn("world.getGameTime()",runtime)
        self.assertIn("Map<PathogenSpatialFieldMath.Cell,Boolean> activeCache",runtime)
        self.assertIn("field.step(worldTime/INTERVAL,checked)",runtime)
        self.assertIn("field.viralWave(worldTime/INTERVAL,checked)",runtime)
        self.assertIn("FieldGameNumericsSmoke",workflow)
        self.assertIn("FieldEnvironmentObservationSmoke",workflow)

if __name__=="__main__": unittest.main()
