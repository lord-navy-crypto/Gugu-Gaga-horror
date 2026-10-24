"""Disk restart CI uses isolated world and native per-world SavedData."""
import unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
SRC=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga/pathogens/"
class DiskRestartSourceSmoke(unittest.TestCase):
 def test_java_isolated_phases_and_native_store(self):
  java=(SRC/"PathogenFieldServerSavedDataSmoke.java").read_text()
  for expected in ("GUGU_FIELD_SELFTEST","GUGU_FIELD_DISK_PHASE",
         "GUGUGAGA_FIELD_DISK_WRITE_PASS","GUGUGAGA_FIELD_DISK_RESTART_PASS",
         "PathogenSpatialFieldSavedData.forLevel(","live.routeCandidates(1)"):
   self.assertIn(expected,java)
 def test_ephemeral_two_process_persistence(self):
  script=(ROOT/"qa/smoke_pathogen_disk_restart.py").read_text()
  workflow=(ROOT/".github/workflows/build.yml").read_text()
  for phrase in ('GITHUB_ACTIONS','secrets.token_hex(24)',
                 'server-ip=127.0.0.1','"save-all flush"','"stop"',
                 'run_phase("write"','run_phase("verify"',
                 'NBT_NAME','GUGUGAGA_REAL_WORLD_DISK_RESTART_PASS'):
   self.assertIn(phrase,script)
  self.assertIn("qa/smoke_pathogen_disk_restart.py",workflow)
  self.assertNotIn("ROOT / 'saves'",script)
  self.assertNotIn('ROOT / "saves"',script)
if __name__=="__main__":unittest.main()
