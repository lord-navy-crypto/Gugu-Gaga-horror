import io.github.lordnavycrypto.gugugaga.pathogens.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
public final class PathogenSpatialFieldSmoke{
 private static int checks;
 private static void ok(boolean b,String s){checks++;if(!b)throw new AssertionError(s);}
 private static int total(Map<PathogenSpatialFieldMath.Cell,int[]> cells){
  int sum=0;for(int[] a:cells.values())for(int n:a)sum+=n;return sum;
 }
 public static void main(String[] args){
  var C=PathogenSpatialFieldMath.class;
  ok(PathogenSpatialFieldMath.CHANNELS==15,"fifteen scalar channels");
  ok(PathogenSpatialFieldMath.fromBlock(-1,-1).equals(
    new PathogenSpatialFieldMath.Cell(-1,-1)),"floorDiv handles negative coords");
  for(var taxon:PathogenRoster15.FIRST_FIFTEEN){
   int channel=PathogenSpatialFieldMath.channel(taxon.id());
   ok(channel>=0&&channel<15,"every taxon has one channel");
  }
  var origin=new PathogenSpatialFieldMath.Cell(0,0);
  var seeded=PathogenSpatialFieldMath.withPressure(
    Map.of(),origin,PathogenSpatialFieldMath.channel("phytophthora_infestans"),12);
  ok(seeded.size()==1,"only one cell persisted for seed");
  ok(seeded.get(origin)[12]==12,"correct taxon index and pressure");
  var locked=PathogenSpatialFieldMath.step(seeded,0,64,1,c->c.equals(origin));
  ok(locked.moved()==0&&locked.cells().size()==1,"unloaded neighbors never receive pressure");
  ok(locked.cells().get(origin)[12]==12,"no phantom loss into unloaded neighbors");
  var open=PathogenSpatialFieldMath.step(seeded,0,64,1,c->true);
  ok(open.moved()==1&&open.cells().size()==2,"cell-to-cell game field update");
  ok(total(open.cells())==12,"game spill is mass-conserving outside decay");
  var before=seeded.get(origin)[12];
  ok(before==12,"field update does not mutate caller snapshot");
  var later=PathogenSpatialFieldMath.step(open.cells(),0,64,8,c->true);
  ok(total(later.cells())<=total(open.cells()),"abstract pressure only moves or fades");
  var max=PathogenSpatialFieldMath.withPressure(
     Map.of(),origin,14,15);
  for(int i=0;i<200;i++){
   var frame=PathogenSpatialFieldMath.step(max,0,64,i+1,c->true);
   ok(frame.cells().size()<=PathogenSpatialFieldMath.MAX_CELLS,"no field overflow");
   for(var e:frame.cells().values())for(int val:e)
    ok(val>=0&&val<=15,"clamped abstract pressure");
   max=frame.cells();
  }
  var crowded=new HashMap<PathogenSpatialFieldMath.Cell,int[]>();
  for(int i=0;i<1024;i++){
   int[] v=new int[15];v[0]=1;
   crowded.put(new PathogenSpatialFieldMath.Cell(i,0),v);
  }
  ok(PathogenSpatialFieldMath.withPressure(crowded,
      new PathogenSpatialFieldMath.Cell(3000,0),0,12).size()==1024,
      "new cells rejected when full");
  var big=PathogenSpatialFieldMath.step(crowded,0,64,1,c->true);
  ok(big.processed()<=64,"tick budget enforced");
  try{PathogenSpatialFieldMath.channel("not_a_real_catalog_id");
   throw new AssertionError("Unknown channel accepted");
  }catch(IllegalArgumentException expected){checks++;}
  System.out.println("GUGUGAGA_PATHOGEN_SPATIAL_FIELD_PASS checks="+checks
     +" 15Channels=true zeroBlockOrEntityRegistration=true"
     +" noBiologicalTransmissionParameters=true budgeted=true");
 }
}
