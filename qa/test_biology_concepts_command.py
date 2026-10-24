import unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
JAVA=ROOT/'src/main/java/io/github/lordnavycrypto/gugugaga'
class BiologyConceptCommands(unittest.TestCase):
 def test_general_biology_and_public_sources(self):
  model=(JAVA/'pathogens/BiologyConcepts.java').read_text()
  command=(JAVA/'command/OneHealthCommands.java').read_text()
  for category in ('VIRUS','BACTERIA','FUNGI','ONE_HEALTH'):
   self.assertIn('"'+category+'"',model)
  for term in ('host cells','binary fission','sourceUrl'):
   self.assertIn(term,model+command)
  self.assertIn('Commands.literal("biology")',command)
  self.assertIn('BiologyConcepts.ENTRIES',command)
  for forbidden in ('setBlock(', 'getFluidState(', 'forceChunk(', 'transfer(', 'setHealth('):
   self.assertNotIn(forbidden,model)
if __name__=='__main__':unittest.main()
