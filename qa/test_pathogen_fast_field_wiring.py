"""Source-level checks for no whole-map copying on each server event."""
import unittest
from pathlib import Path
R=Path(__file__).resolve().parents[1]
P=R/"src/main/java/io/github/lordnavycrypto/gugugaga/pathogens"
class FastPathogenGameDataWiring(unittest.TestCase):
 def test_server_transfers_two_cells_only(self):
  store=(P/"PathogenSpatialFieldSavedData.java").read_text()
  math=(P/"PathogenRouteGameMath.java").read_text()
  self.assertIn("PathogenRouteGameMath.canMove(cells,from,to,channel,route,loaded)",store)
  self.assertIn("source[channel]--",store)
  self.assertIn("destination[channel]++",store)
  self.assertNotIn("PathogenRouteGameMath.move(cells,from,to",store)
  self.assertNotIn("cells=change.cells()",store)
  self.assertIn("frame.moved()>0||frame.faded()>0||cursor!=previousCursor",store)
  self.assertIn("cells=new HashMap<>(PathogenSpatialFieldMath.immutableCopy(frame.cells()))",store)
  self.assertIn("public static boolean canMove(",math)
 def test_channel_starvation_and_visual_data(self):
  scheduler=(P/"PathogenRouteFairScheduler.java").read_text()
  runtime=(P/"PathogenDataRoutesRuntime.java").read_text()
  self.assertIn("startChannel(",scheduler)
  self.assertIn("PathogenRouteFairScheduler.startChannel(from,frame)",runtime)
  self.assertIn("field.routeCandidates(MAX_AMBIENT_SOURCES_SCANNED)",runtime)
if __name__=="__main__":unittest.main()
