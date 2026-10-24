"""Science sources remain separate from game-only scores and journal NBT."""
from pathlib import Path
import unittest
R=Path(__file__).resolve().parents[1]
J=R/"src/main/java/io/github/lordnavycrypto/gugugaga"
class OneHealthAcceptance(unittest.TestCase):
 def test_command_registration(self):
  mod=(J/"GuguGagaMod.java").read_text()
  cmd=(J/"command/OneHealthCommands.java").read_text()
  journal=(J/"pathogens/OneHealthObservationLog.java").read_text()
  evidence=(J/"pathogens/OneHealthEvidence5.java").read_text()
  ci=(R/".github/workflows/build.yml").read_text()
  self.assertIn("OneHealthCommands::register",mod)
  for command in ('Commands.literal("health")','Commands.literal("survey")',
                  'Commands.literal("evidence")','Commands.literal("journal")'):
   self.assertIn(command,cmd)
  self.assertIn("OneHealthObservation.interpret(",cmd)
  self.assertIn("OneHealthObservationLog.forLevel(level).append(",cmd)
  self.assertIn("MAX_HOSTS_SCANNED",cmd)
  self.assertIn("MAX_CROPS_SCANNED",cmd)
  self.assertIn("hasChunkAt(pos)",cmd)
  self.assertIn("CAPACITY=128",journal)
  self.assertIn("HolderLookup.Provider",journal)
  self.assertIn('FILE="gugugaga_one_health_observations_v1"',journal)
  self.assertIn("OneHealthEvidence5Smoke",ci)
  self.assertIn("FACTS=List.of(",evidence)
  for forbidden in ("registerBlock(","EntityType.Builder","setHealth(",
         "spawnAtLocation(","forceChunk(","setBlock("):
   self.assertNotIn(forbidden,cmd+journal+evidence)
 def test_compatible_saved_data_and_native_roundtrip(self):
  saved=(J/"pathogens/FocusFiveServerSelftest.java").read_text()
  boot=(R/"qa/smoke_server_boot.py").read_text()
  self.assertIn("OneHealthObservationLog.load(",saved)
  self.assertIn("OneHealthObservationLog.Record(",saved)
  self.assertIn("GUGUGAGA_ONE_HEALTH_NBT_PASS",saved)
  self.assertIn("HEALTH_MARKER in log",boot)
  old=(J/"pathogens/PathogenSpatialFieldSavedData.java").read_text()
  self.assertIn('FILE_ID="gugugaga_spatial_pathogen_field_v1"',old)
if __name__=="__main__":unittest.main()
