"""Check the native NeoForge module uses persistent round-robin state and map."""
from pathlib import Path
import unittest
ROOT=Path(__file__).resolve().parents[1]
PATH=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga"
class RouteFairnessIntegration(unittest.TestCase):
 def test_scheduler_is_wired_to_saved_data(self):
  store=(PATH/"pathogens/PathogenSpatialFieldSavedData.java").read_text()
  route=(PATH/"pathogens/PathogenDataRoutesRuntime.java").read_text()
  assert "PathogenRouteFairScheduler.next(" in store
  assert '"RouteCursor"' in store
  assert "field.routeCandidates(MAX_AMBIENT_SOURCES_SCANNED)" in route
  assert "MAX_AMBIENT_SOURCES_SCANNED=64" in route
  assert "new LinkedHashMap<>(256,0.75f,true)" in route
  self.assertNotIn("history.animals.clear()",route)
  self.assertNotIn("history.animals.keySet().retainAll",route)
 def test_text_map_is_not_a_new_world_block(self):
  commands=(PATH/"command/PathogenSpatialFieldCommands.java").read_text()
  printer=(PATH/"pathogens/PathogenFieldTextMap.java").read_text()
  self.assertIn('Commands.literal("map")',commands)
  self.assertIn("PathogenFieldTextMap.render(",commands)
  self.assertIn("GRID_SIDE=RADIUS*2+1",printer)
  self.assertNotIn("setBlock(",printer+commands)
  self.assertNotIn("DeferredRegister.Blocks",printer)
if __name__=="__main__":unittest.main()
