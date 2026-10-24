import io.github.lordnavycrypto.gugugaga.pathogens.*;
import java.util.*;
public final class ViralGameSignalsSmoke{
 private static int count;
 private static void ok(boolean valid,String message){
  count++;if(!valid)throw new AssertionError(message);
 }
 public static void main(String[]args){
  int viruses=0;
  for(int i=0;i<15;i++){
   if(!ViralGameSignalPolicy.isVirusChannel(i)){
    ok(ViralGameSignalPolicy.nextLevel(i,12,0)==0,
      "nonviral taxa never create viral visual pulses");
    continue;
   }
   viruses++;
   int value=12;
   for(int n=0;n<64;n++){
    int to=n%16;
    int next=ViralGameSignalPolicy.nextLevel(i,value,to);
    ok(next>=to &&next<=15,"signal bounded and monotone on recipient");
    ok(next==to || value>to,"only from stronger game signal");
   }
  }
  ok(viruses==8,"eight virus identities among fifteen real taxa");
  var center=new PathogenSpatialFieldMath.Cell(-7,3);
  int virus=PathogenSpatialFieldMath.channel("potato_virus_y");
  Set<Integer> directions=new HashSet<>();
  for(int f=0;f<4;f++)directions.add(
    ViralGameSignalPolicy.direction(center,virus,f));
  ok(directions.size()==4,"four game-cell directions reached over four frames");
  ok(ViralGameSignalPolicy.nextLevel(virus,12,0)==6,
    "strong symbolic signal makes a visible adjacent symbol");
  ok(ViralGameSignalPolicy.nextLevel(virus,6,0)==3,
    "new area may create an additional game-only signal");
  ok(ViralGameSignalPolicy.nextLevel(virus,0,0)==0,
    "an empty source never creates a signal");
  ok(ViralGameSignalPolicy.nextLevel(virus,3,15)==15,
    "saturated destination remains bounded");
  int[] levels=new int[15];
  levels[PathogenSpatialFieldMath.channel("african_swine_fever_virus")]=11;
  var pig=ViralHostVisualPolicy.forAnimal("minecraft:pig",levels);
  ok(pig.cue()==ViralHostVisualPolicy.Cue.ANIMAL_MOVEMENT_CUE,
    "original pig gets reversible game cue");
  ok(ViralHostVisualPolicy.forAnimal("minecraft:cow",levels).cue()
     ==ViralHostVisualPolicy.Cue.NONE,"never map pig-only taxon to cow");
  levels=new int[15];
  levels[virus]=11;
  ok(ViralHostVisualPolicy.forCrop("minecraft:potatoes",levels).cue()
    ==ViralHostVisualPolicy.Cue.CROP_PAUSE,"native potatoes get optional cue");
  ok(ViralHostVisualPolicy.forCrop("minecraft:oak_leaves",levels).cue()
    ==ViralHostVisualPolicy.Cue.NONE,"potato virus not displayed on trees");
  ok(ViralHostVisualPolicy.forAnimal("minecraft:player",levels).cue()
    ==ViralHostVisualPolicy.Cue.NONE,"no player infection game state");
  System.out.println("GUGUGAGA_VIRAL_GAME_SIGNAL_PASS checks="+count+
    " eightViruses=true noBlocks=true noClinicalData=true");
 }
}
