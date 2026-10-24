"""Living microbe engine must run on mobs, without generating microbe blocks."""
import json
import unittest
from pathlib import Path
R=Path(__file__).resolve().parents[1]
J=R/"src/main/java/io/github/lordnavycrypto/gugugaga"
A=R/"src/main/resources/assets/gugugaga"
D=R/"src/main/resources/data/gugugaga"
class LivingMicrobesOnAnimals(unittest.TestCase):
 def test_real_mob_and_server_wiring(self):
  mod=(J/"GuguGagaMod.java").read_text()
  rt=(J/"microbes/LivingMicrobeRuntime.java").read_text()
  item=(J/"microbes/LivingMicrobeScannerItem.java").read_text()
  content=(J/"microbes/LivingMicrobeContent.java").read_text()
  for key in ("LivingMicrobeContent.ITEMS.register(modBus)",
              "LivingMicrobeRuntime::onTick"):self.assertIn(key,mod)
  for key in ("getPersistentData()","getEntitiesOfClass(Animal.class",
              "MOB_BUDGET_PER_UPDATE=128","HashSet","server"): pass
  self.assertIn("getPersistentData()",rt)
  self.assertIn("getEntitiesOfClass(Animal.class",rt)
  self.assertIn("MOB_BUDGET_PER_UPDATE=128",rt)
  self.assertIn("Set<UUID> visited",rt)
  self.assertIn("sendParticles(",rt)
  self.assertIn("public InteractionResult interactLivingEntity(",item)
  self.assertIn("LivingMicrobeRuntime.inspect(",item)
  self.assertIn("registerItem(\"living_microbe_scanner\"",content)
  for banned in ("setBlock(","forceChunk(","getAllEntities(","setHealth(",
                 "transmitVirus(","infectNearby(","infectionChance("):
   self.assertNotIn(banned,rt+item)
 def test_scanner_has_actual_item_and_crafting(self):
  m=json.loads((A/"models/item/living_microbe_scanner.json").read_text())
  self.assertEqual(m["parent"],"minecraft:item/generated")
  recipe=json.loads((D/"recipe/living_microbe_scanner.json").read_text())
  self.assertEqual(recipe["result"]["id"],"gugugaga:living_microbe_scanner")
  for lang in ("en_us","zh_cn"):
   j=json.loads((A/"lang"/(lang+".json")).read_text())
   for key in ("item.gugugaga.living_microbe_scanner",
              "message.gugugaga.living_microbes.report"):
    self.assertIn(key,j)
 def test_no_block_content_in_new_system(self):
  for p in ("LivingMicrobeRuntime.java","LivingMicrobeScannerItem.java",
            "LivingMicrobePopulation.java","LivingMicrobeContent.java"):
   text=(J/"microbes"/p).read_text()
   self.assertNotIn("BlockItem",text)
   self.assertNotIn("worldgen",text.lower())
if __name__=="__main__":unittest.main()
