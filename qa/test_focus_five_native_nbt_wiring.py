from pathlib import Path
import unittest
R=Path(__file__).resolve().parents[1]
J=R/"src/main/java/io/github/lordnavycrypto/gugugaga"
class FocusFiveNativeNbt(unittest.TestCase):
 def test_real_native_codec_ci_gate(self):
  server=(J/"pathogens/FocusFiveServerSelftest.java").read_text()
  main=(J/"GuguGagaMod.java").read_text()
  smoke=(R/"qa/smoke_server_boot.py").read_text()
  for value in ("EcologyExtendedSavedData.load(","PathogenSpatialFieldSavedData.load(",
                "registryAccess()","FocusFiveProfiles.FIVE",
                "GUGUGAGA_FIVE_FOCUS_NATIVE_NBT_PASS"):
   self.assertIn(value,server)
  self.assertIn("FocusFiveServerSelftest::onServerStarted",main)
  self.assertIn("FOCUS_MARKER in log",smoke)
  self.assertNotIn("getDataStorage()",server)
if __name__=="__main__":unittest.main()
