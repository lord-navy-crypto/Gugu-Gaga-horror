"""Real vanilla Minecraft environmental measurements, no fake climates."""
from pathlib import Path
import unittest
R=Path(__file__).resolve().parents[1]
J=R/"src/main/java/io/github/lordnavycrypto/gugugaga"
class MinecraftVanillaEnvironment(unittest.TestCase):
 def test_native_environment_and_opt_in_crop_effect(self):
  reader=(J/"pathogens/MinecraftHabitatRuntime.java").read_text()
  logic=(J/"pathogens/MinecraftHabitatScience.java").read_text()
  command=(J/"command/OneHealthCommands.java").read_text()
  crop=(J/"pathogens/PathogenSceneRuntime.java").read_text()
  config=(J/"config/NightwatchConfig.java").read_text()
  self.assertIn("level.hasChunkAt(pos)",reader)
  self.assertIn("level.getBrightness(LightLayer.SKY,pos)",reader)
  self.assertIn("level.isRainingAt(pos)",reader)
  self.assertIn("Blocks.FARMLAND",reader)
  self.assertIn("MinecraftHabitatScience.interpret(sample)",command)
  self.assertIn('Commands.literal("environment")',command)
  self.assertIn("ECOLOGY_CROP_GAME_FEEDBACK_ENABLED.get()",crop)
  self.assertIn('VanillaCropGameFeedback.growth(',crop)
  self.assertIn('.define("wheat_growth_feedback_enabled",false)',config)
  self.assertIn("Math.floorDiv",logic.replace("PathogenSpatialFieldMath.fromBlock","Math.floorDiv"))
  for forbidden in ("forceChunk(", "setBlock(", "EntityType.Builder",
                    "ClientPacketListener","createExplosion("):
   self.assertNotIn(forbidden,reader+logic+command)
 def test_java21_gate_exists(self):
  self.assertIn("MinecraftHabitatScienceSmoke",
       (R/".github/workflows/build.yml").read_text())
if __name__=="__main__":unittest.main()
