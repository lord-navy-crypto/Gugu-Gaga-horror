import unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
JAVA=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga"
class TestPathogenRouting(unittest.TestCase):
 def test_game_route_wiring(self):
  mod=(JAVA/"GuguGagaMod.java").read_text()
  cfg=(JAVA/"config/NightwatchConfig.java").read_text()
  rt=(JAVA/"pathogens/PathogenDataRoutesRuntime.java").read_text()
  field=(JAVA/"pathogens/PathogenSpatialFieldSavedData.java").read_text()
  policy=(JAVA/"pathogens/PathogenRoutePolicy15.java").read_text()
  math=(JAVA/"pathogens/PathogenSpatialFieldMath.java").read_text()
  self.assertIn("PathogenDataRoutesRuntime::onTick",mod)
  self.assertIn("PATHOGEN_ROUTE_EVENTS_ENABLED",cfg)
  self.assertIn("getEntitiesOfClass(Animal.class",rt)
  self.assertIn("getMainHandItem()",rt)
  self.assertIn("MAX_AMBIENT_TRANSFERS=24",rt)
  self.assertIn("MAX_MOVING_ANIMALS=64",rt)
  self.assertIn("MAX_MOVING_PLAYERS=64",rt)
  self.assertIn("hasChunk(",rt)
  self.assertIn("field.transfer(",rt)
  self.assertIn("PathogenRouteGameMath.canMove(",field)
  self.assertNotIn("PathogenRouteGameMath.move(cells,",field)
  self.assertIn("PathogenRoutePolicy15.allows(ch",math)
  self.assertEqual(policy.count('p("'),15)
  for banned in ("getAllEntities(","forceChunk(","setBlock(","setHealth(",
                 "infectionProbability","transmissionCoefficient",
                 "actualViralLoad","getChunk("):
   self.assertNotIn(banned,rt+field)
 def test_no_new_microbe_entities_or_blocks(self):
  for name in ("PathogenRoutePolicy15.java","PathogenRouteGameMath.java",
               "PathogenDataRoutesRuntime.java"):
   code=(JAVA/"pathogens"/name).read_text()
   for banned in ("registerSimpleBlock", "EntityType.Builder",
                  "DeferredRegister.Blocks","BlockItem"):
    self.assertNotIn(banned,code)
if __name__=="__main__":unittest.main()
