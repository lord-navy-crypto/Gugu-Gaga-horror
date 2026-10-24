"""Ensure CI actually exercises the NeoForge-world NBT codec, not just boots."""
import unittest
from pathlib import Path
BASE=Path(__file__).resolve().parents[1]
class SpatialSavedDataCI(unittest.TestCase):
 def test_real_server_wiring(self):
  main=(BASE/"src/main/java/io/github/lordnavycrypto/gugugaga/GuguGagaMod.java").read_text()
  selftest=(BASE/"src/main/java/io/github/lordnavycrypto/gugugaga/pathogens/PathogenFieldServerSavedDataSmoke.java").read_text()
  smoke=(BASE/"qa/smoke_server_boot.py").read_text()
  workflow=(BASE/".github/workflows/build.yml").read_text()
  self.assertIn("PathogenFieldServerSavedDataSmoke::onServerStarted",main)
  self.assertIn("ServerStartedEvent",selftest)
  self.assertIn('System.getenv("GUGU_FIELD_SELFTEST")',selftest)
  self.assertIn("PathogenSpatialFieldSavedData.load(",selftest)
  self.assertIn("PathogenRoutePolicy15.Route.VIRTUAL_VECTOR",selftest)
  self.assertNotIn("getDataStorage()",selftest.split("public static void onServerStarted")[1].split("private static void check")[0].replace("// No call to world.getDataStorage():",""))
  self.assertIn('SAVEDATA_MARKER in log',smoke)
  self.assertIn('GUGU_FIELD_SELFTEST: "1"',workflow)
if __name__=="__main__":unittest.main()
