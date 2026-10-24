import io.github.lordnavycrypto.gugugaga.pathogens.*;
import java.util.*;
public final class ViralGameSignalWaveSmoke {
 private static int count;
 private static void yes(boolean ok,String reason){
  count++;if(!ok)throw new AssertionError(reason);
 }
 public static void main(String[]args){
  var start=new PathogenSpatialFieldMath.Cell(-1,0);
  int virus=PathogenSpatialFieldMath.channel("potato_virus_y");
  Map<PathogenSpatialFieldMath.Cell,int[]> map=
    PathogenSpatialFieldMath.withPressure(Map.of(),start,virus,12);
  var locked=ViralGameSignalWave.advance(map,0,64,0,c->c.equals(start));
  yes(locked.emitted()==0&&locked.cells().size()==1,
    "no signal can reach unloaded Minecraft region");
  yes(map.get(start)[virus]==12,"original world snapshot untouched");
  var first=ViralGameSignalWave.advance(map,0,64,0,c->true);
  yes(first.emitted()==1&&first.cells().size()==2,
    "real first-frame adjacent game information appears");
  yes(first.newCells()==1,"one new numerical region");
  yes(first.cells().get(start)[virus]==6,"source debited for six game units");
  yes(first.cells().values().stream().mapToInt(row->row[virus]).sum()==12,
    "exact symbolic source/destination ledger balance");
  yes(first.visited()<=64,"bounded source budget");
  var current=first.cells();
  for(int f=1;f<200;f++){
   var step=ViralGameSignalWave.advance(current,f%100,64,f,c->true);
   yes(step.visited()<=64,"per-tick finite scanner");
   yes(step.cells().size()<=1024,"global sparse field cap");
   for(var channels:step.cells().values())
    for(int level:channels)yes(level>=0&&level<=15,
      "game-only numbers always bounded");
   current=step.cells();
  }
  yes(current.size()>2,"neighboring game cells receive expanding numeric signals");
  int[] nonVirus=new int[15];
  nonVirus[PathogenSpatialFieldMath.channel("salmonella_enterica")]=12;
  var b=ViralGameSignalWave.advance(Map.of(start,nonVirus),0,64,0,c->true);
  yes(b.emitted()==0,"bacterial labels unaffected by virus-only phase");
  Map<PathogenSpatialFieldMath.Cell,int[]> full=new HashMap<>();
  for(int i=0;i<1024;i++)full.put(
    new PathogenSpatialFieldMath.Cell(i,0),nonVirus.clone());
  full.get(new PathogenSpatialFieldMath.Cell(0,0))[virus]=15;
  var capped=ViralGameSignalWave.advance(full,0,64,0,c->true);
  yes(capped.cells().size()==1024,"no allocation above max data cells");
  yes(capped.cells().values().stream().mapToInt(row->row[virus]).sum()==15,
    "no game units may be created at storage capacity");
  int[] chicken=new int[15];
  chicken[PathogenSpatialFieldMath.channel("influenza_a_virus")]=10;
  yes(ViralHostVisualPolicy.forAnimal("minecraft:chicken",chicken).cue()
    ==ViralHostVisualPolicy.Cue.ANIMAL_MOVEMENT_CUE,"game chicken has observable stage");
  yes(ViralHostVisualPolicy.forAnimal("minecraft:pig",chicken).cue()
    ==ViralHostVisualPolicy.Cue.NONE,"game chicken event not applied to wrong host");
  System.out.println("GUGUGAGA_VIRAL_WORLD_WAVE_PASS checks="+count+
     " regionCap=1024 activeOnly=true noPhysicalVirus=true");
 }
}
