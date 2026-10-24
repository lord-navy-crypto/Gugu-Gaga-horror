"""HUD must be vanilla, opt-in, private and have no special pathogen blocks."""
import unittest
from pathlib import Path
R=Path(__file__).resolve().parents[1]
J=R/"src/main/java/io/github/lordnavycrypto/gugugaga"
class HudOptInAcceptance(unittest.TestCase):
 def test_command_and_event_wiring(self):
  main=(J/"GuguGagaMod.java").read_text()
  commands=(J/"command/PathogenSpatialFieldCommands.java").read_text()
  runtime=(J/"pathogens/PathogenFieldHudRuntime.java").read_text()
  old=(J/"pathogens/PathogenSpatialFieldRuntime.java").read_text()
  self.assertIn("PathogenFieldHudRuntime::onTick",main)
  self.assertIn("PathogenFieldHudRuntime::onLogout",main)
  for value in ('Commands.literal("hud")','Commands.literal("on")',
       'Commands.literal("off")','Commands.literal("status")',
       "PathogenFieldHudRuntime.setEnabled(player,enable)"):
   self.assertIn(value,commands)
  self.assertIn("if(!isEnabled(server,player.getUUID()))continue;",runtime)
  self.assertIn("HUD_INTERVAL=40",runtime)
  self.assertIn("PlayerEvent.PlayerLoggedOutEvent",runtime)
  self.assertIn("player.displayClientMessage(",runtime)
  self.assertNotIn("player.displayClientMessage(",old)
  self.assertNotIn("PacketDistributor.sendToAllPlayers(",runtime)
  for forbidden in ("registerBlock","EntityType.Builder","setBlock(","setHealth("):
   self.assertNotIn(forbidden,runtime)
 def test_hud_has_own_smoke_job(self):
  ci=(R/".github/workflows/build.yml").read_text()
  self.assertIn("PathogenHudTextSmoke",ci)
if __name__=="__main__":unittest.main()
