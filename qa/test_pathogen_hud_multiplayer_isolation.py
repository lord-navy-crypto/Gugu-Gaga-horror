"""Verify real NeoForge HUD handler uses tested per-player opt-in state."""
import unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
JAVA=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga"
class HudIsolation(unittest.TestCase):
 def test_real_server_wiring(self):
  hud=(JAVA/"pathogens/PathogenFieldHudRuntime.java").read_text()
  main=(JAVA/"GuguGagaMod.java").read_text()
  policy=(JAVA/"pathogens/PathogenHudSubscriptions.java").read_text()
  workflow=(ROOT/".github/workflows/build.yml").read_text()
  self.assertIn("new PathogenHudSubscriptions()",hud)
  self.assertIn("ENABLED.isEnabled(server,player)",hud)
  self.assertIn("ENABLED.set(server,player.getUUID(),enable)",hud)
  self.assertIn("ENABLED.clearServer(event.getServer())",hud)
  self.assertIn("PathogenFieldHudRuntime::onServerStopped",main)
  self.assertIn("if(!isEnabled(server,player.getUUID()))continue;",hud)
  self.assertIn("PathogenHudSubscriptionsSmoke",workflow)
  self.assertIn("new WeakHashMap<>()",policy)
  self.assertNotIn("static final Set<UUID>",policy)
if __name__=="__main__":unittest.main()
