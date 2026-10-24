import unittest
from pathlib import Path
R=Path(__file__).resolve().parents[1]
J=R/"src/main/java/io/github/lordnavycrypto/gugugaga"
class NumericOverviewWiring(unittest.TestCase):
 def test_read_only_server_command(self):
  command=(J/"command/PathogenSpatialFieldCommands.java").read_text()
  overview=(J/"pathogens/PathogenFieldOverview.java").read_text()
  ci=(R/".github/workflows/build.yml").read_text()
  self.assertIn('Commands.literal("overview")',command)
  self.assertIn("PathogenFieldOverview.inspect(field,at)",command)
  self.assertIn("new StringBuilder(7)",overview)
  self.assertIn("PathogenFieldOverviewSmoke",ci)
  for bad in ("EntityType.Builder","DeferredRegister.Blocks","setBlock(", "setHealth("):
   self.assertNotIn(bad,overview+command)
if __name__=="__main__":unittest.main()
