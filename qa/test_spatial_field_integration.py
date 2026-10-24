"""Source-level acceptance for 15-channel location data in NeoForge runtime."""
import unittest
from pathlib import Path
R=Path(__file__).resolve().parents[1]
J=R/"src/main/java/io/github/lordnavycrypto/gugugaga"
class SpatialFieldIntegration(unittest.TestCase):
 def test_real_runtime_and_registered_command(self):
  main=(J/"GuguGagaMod.java").read_text()
  config=(J/"config/NightwatchConfig.java").read_text()
  rt=(J/"pathogens/PathogenSpatialFieldRuntime.java").read_text()
  storage=(J/"pathogens/PathogenSpatialFieldSavedData.java").read_text()
  command=(J/"command/PathogenSpatialFieldCommands.java").read_text()
  old=(J/"pathogens/PathogenSceneRuntime.java").read_text()
  for name in ("PathogenSpatialFieldRuntime::onTick",
               "PathogenSpatialFieldCommands::register"):self.assertIn(name,main)
  for name in ("PATHOGEN_FIELD_ENABLED","PATHOGEN_FIELD_CROP_CUES"):self.assertIn(name,config)
  for name in ("PathogenSpatialFieldMath.step(","PathogenSpatialFieldSavedData.forLevel("):self.assertIn(name,rt+storage)
  for name in ("onTick(ServerTickEvent.Post","CELL_EDGE_BLOCKS=4"):self.assertIn(name,rt+(J/"pathogens/PathogenSpatialFieldMath.java").read_text())
  for name in ("gugugaga","field","seed","clear","status","hasPermission(2)"):self.assertIn(name,command)
  self.assertIn("PathogenSpatialFieldRuntime.set(",old)
  self.assertIn("PathogenSpatialFieldRuntime.pauseCrop(",old)
  for blocked in ("forceChunk(","setBlock(","spawnEntity(","spreadVirions(",
                  "realTransmissionRate(","infectionProbability("):
   self.assertNotIn(blocked,rt+storage+command)
 def test_not_an_entity_or_block(self):
  p=J/"pathogens"
  for name in ("PathogenSpatialFieldMath.java",
               "PathogenSpatialFieldSavedData.java",
               "PathogenSpatialFieldRuntime.java"):
   text=(p/name).read_text()
   self.assertNotIn("DeferredRegister.Blocks",text)
   self.assertNotIn("EntityType.Builder",text)
   self.assertNotIn("BlockItem",text)
if __name__=="__main__":unittest.main()
