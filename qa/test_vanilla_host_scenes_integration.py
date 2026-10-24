import json
from pathlib import Path
import unittest
ROOT=Path(__file__).resolve().parents[1]
J=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga"
A=ROOT/"src/main/resources/assets/gugugaga"
D=ROOT/"src/main/resources/data/gugugaga"
class HostSceneIntegration(unittest.TestCase):
 def test_live_vanilla_wiring(self):
  mod=(J/"GuguGagaMod.java").read_text()
  rt=(J/"pathogens/PathogenSceneRuntime.java").read_text()
  scanner=(J/"pathogens/PathogenFieldScannerItem.java").read_text()
  cfg=(J/"config/NightwatchConfig.java").read_text()
  for name in ("PathogenSceneContent.ITEMS.register(modBus)",
    "PathogenSceneRuntime::onServerTick","PathogenSceneRuntime::onCropGrow"):
   self.assertIn(name,mod)
  for name in ("inspectMob(","inspectPlant(","CropGrowEvent.Pre",
    "PathogenCropSceneSavedData.forLevel(","getEntitiesOfClass(Animal.class",
    "MAX_ANIMALS=96","MAX_PLANTS_PER_LEVEL=64","hasChunkAt("):
   self.assertIn(name,rt)
  for name in ("interactLivingEntity(","useOn(","inspectMob(","inspectPlant("):
   self.assertIn(name,scanner)
  for name in ("PATHOGEN_SCENE_ENGINE_ENABLED",
    "PATHOGEN_SCENE_CROP_PAUSE","PATHOGEN_SCENE_ANIMAL_CUES"):
   self.assertIn(name,cfg)
  for forbidden in ("forceChunk(","getAllEntities(","transmissionRate(","infectPlayer("):
   self.assertNotIn(forbidden,rt+scanner)
 def test_actual_item_assets(self):
  m=json.loads((A/"models/item/pathogen_field_scanner.json").read_text())
  self.assertEqual(m["parent"],"minecraft:item/generated")
  recipe=json.loads((D/"recipe/pathogen_field_scanner.json").read_text())
  self.assertEqual(recipe["result"]["id"],"gugugaga:pathogen_field_scanner")
  for lang in ("en_us","zh_cn"):
   entries=json.loads((A/"lang"/(lang+".json")).read_text())
   for key in ("item.gugugaga.pathogen_field_scanner",
       "message.gugugaga.pathogen_scenes.inspect",
       "message.gugugaga.pathogen_scenes.cleared"):
    self.assertIn(key,entries)
 def test_no_new_microbe_blocks(self):
  p=J/"pathogens"
  for f in p.glob("*.java"):
   self.assertNotIn("DeferredRegister.Blocks",f.read_text())
if __name__=="__main__":unittest.main()
