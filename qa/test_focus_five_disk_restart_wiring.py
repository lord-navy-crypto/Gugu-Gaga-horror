"""Two separate numeric world-data files must survive a real server restart."""
import unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
class FiveFocusRestart(unittest.TestCase):
 def test_real_separate_save_files(self):
  java=(ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga/pathogens/PathogenFieldServerSavedDataSmoke.java").read_text()
  python=(ROOT/"qa/smoke_pathogen_disk_restart.py").read_text()
  for name in ("influenza_a_virus","potato_virus_y","salmonella_enterica",
               "puccinia_graminis","varroa_destructor"):
   self.assertIn(name,java)
  self.assertIn("EcologyExtendedSavedData.forLevel(",java)
  self.assertIn("GUGUGAGA_FIVE_FOCUS_EXTENSION_DISK_FOUND",python)
  self.assertIn('EXTENSION_NBT_NAME = "gugugaga_ecology_extension_v1.dat"',python)
  self.assertIn('run_phase("write"',python)
  self.assertIn('run_phase("verify"',python)
if __name__=="__main__":unittest.main()
