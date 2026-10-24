"""The five focused reference examples must be actual live NeoForge features."""
from pathlib import Path
import unittest
ROOT=Path(__file__).resolve().parents[1]
J=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga"
class FocusFiveWiring(unittest.TestCase):
 def test_registered_game_system(self):
  mod=(J/"GuguGagaMod.java").read_text()
  config=(J/"config/NightwatchConfig.java").read_text()
  cmd=(J/"command/FocusFiveCommands.java").read_text()
  visual=(J/"pathogens/FocusFiveVisualRuntime.java").read_text()
  policy=(J/"pathogens/FocusFiveProfiles.java").read_text()
  self.assertIn("FocusFiveCommands::register",mod)
  self.assertIn("FocusFiveVisualRuntime::onTick",mod)
  self.assertIn("FOCUS_FIVE_VISUALS_ENABLED",config)
  for s in ('Commands.literal("focus")','Commands.literal("list")',
            'Commands.literal("status")','Commands.literal("guide")',
            'Commands.literal("demo")','Commands.literal("clear")',
            'hasPermission(2)'):
   self.assertIn(s,cmd)
  self.assertIn("FocusFiveReadout.compatible(",visual)
  self.assertIn("getEntitiesOfClass(Animal.class",visual)
  self.assertIn("seenAnimals.add(",visual)
  self.assertIn("seenBlocks.add(",visual)
  self.assertIn("MAX_ANIMALS=24, MAX_PLANTS=64",visual)
  self.assertIn("hasChunkAt(",visual)
  self.assertIn("ORIGINAL_15",policy)
  self.assertIn("EXTENDED_8",policy)
  for forbidden in ("DeferredRegister.Blocks","EntityType.Builder",
      "createExplosion(","setBlock(","getAllEntities(","forceChunk(","setHealth("):
   self.assertNotIn(forbidden,visual+cmd+policy)
 def test_original_save_schema_preserved(self):
  original=(J/"pathogens/PathogenSpatialFieldSavedData.java").read_text()
  extended=(J/"pathogens/EcologyExtendedSavedData.java").read_text()
  self.assertIn('FILE_ID="gugugaga_spatial_pathogen_field_v1"',original)
  self.assertIn('FILE="gugugaga_ecology_extension_v1"',extended)
  self.assertIn("FocusFiveSmoke",(ROOT/".github/workflows/build.yml").read_text())
if __name__=="__main__":unittest.main()
