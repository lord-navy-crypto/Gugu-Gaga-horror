"""V18: require exact game-ledger accounting, not visually duplicated numbers."""
from pathlib import Path
import unittest
ROOT=Path(__file__).resolve().parents[1]
SRC=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga/pathogens"
class ConservativeUpdate(unittest.TestCase):
 def test_no_unfunded_signal_in_numerical_field(self):
  wave=(SRC/"ViralGameSignalWave.java").read_text()
  saved=(SRC/"PathogenSpatialFieldSavedData.java").read_text()
  workflow=(ROOT/".github/workflows/build.yml").read_text()
  self.assertIn("sourceNow[channel]-=transfer",wave)
  self.assertIn("target[channel]+=transfer",wave)
  self.assertIn("source[channel],sourceNow[channel]",wave)
  self.assertIn("result.entrySet().removeIf(",wave)
  self.assertIn("Frame(result,visit.nextCursor()",wave)
  self.assertIn("FieldMassBalanceSmoke",workflow)
  self.assertIn("ViralGameSignalWaveSmoke",workflow)
  self.assertIn("ViralGameSignalsSmoke",workflow)
  self.assertIn("if(frame.moved()>0||frame.faded()>0)",saved)
  self.assertIn("row[channel]=value;",saved)
  self.assertIn("if(cells.size()>=PathogenSpatialFieldMath.MAX_CELLS)",saved)
  self.assertNotIn("PathogenSpatialFieldMath.withPressure(cells,cell,channel,value)",saved)
if __name__=="__main__":unittest.main()
