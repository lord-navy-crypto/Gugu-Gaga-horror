import unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
JAVA=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga"
class EcologyExtensionsAcceptance(unittest.TestCase):
 def test_independent_data_wiring(self):
  main=(JAVA/"GuguGagaMod.java").read_text()
  store=(JAVA/"pathogens/EcologyExtendedSavedData.java").read_text()
  runtime=(JAVA/"pathogens/EcologyExtendedWorldRuntime.java").read_text()
  command=(JAVA/"command/EcologyExtensionCommands.java").read_text()
  cfg=(JAVA/"config/NightwatchConfig.java").read_text()
  self.assertIn("EcologyExtendedWorldRuntime::onTick",main)
  self.assertIn("EcologyExtensionCommands::register",main)
  self.assertIn("ECOLOGY_EXTENSIONS_ENABLED",cfg)
  self.assertIn('FILE="gugugaga_ecology_extension_v1"',store)
  self.assertIn("EcologyExtensionToyField.advance(",store)
  self.assertIn("HolderLookup.Provider",store)
  self.assertIn('"Regions"',store)
  self.assertIn("level.getChunkSource().hasChunk(",runtime)
  self.assertIn('Commands.literal("ecology")',command)
  for forbidden in ("registerBlock","DeferredRegister.Blocks","EntityType.Builder",
                    "setBlock(","setHealth(","forceChunk(","createExplosion("):
   self.assertNotIn(forbidden,store+runtime+command)
 def test_existing_fifteen_field_untouched(self):
  old=(JAVA/"pathogens/PathogenSpatialFieldSavedData.java").read_text()
  self.assertIn('FILE_ID="gugugaga_spatial_pathogen_field_v1"',old)
  self.assertIn("PathogenSpatialFieldMath.CHANNELS",old)
if __name__=="__main__":unittest.main()
