import io.github.lordnavycrypto.gugugaga.pathogens.*;
import java.util.*;
public final class FieldMassBalanceSmoke{
 private static int checks;
 private static void ok(boolean condition,String message){
  checks++;if(!condition)throw new AssertionError(message);
 }
 private static long total(Map<PathogenSpatialFieldMath.Cell,int[]> map,int ch){
  long sum=0;for(int[] row:map.values())sum+=row[ch];return sum;
 }
 private static String digest(Map<PathogenSpatialFieldMath.Cell,int[]> map){
  var keys=new ArrayList<>(map.keySet());
  keys.sort(Comparator.comparingInt(PathogenSpatialFieldMath.Cell::x)
      .thenComparingInt(PathogenSpatialFieldMath.Cell::z));
  StringBuilder out=new StringBuilder();
  for(var cell:keys)out.append(cell).append(Arrays.toString(map.get(cell)));
  return out.toString();
 }
 public static void main(String[]args){
  int virus=PathogenSpatialFieldMath.channel("potato_virus_y");
  int bacteria=PathogenSpatialFieldMath.channel("salmonella_enterica");
  Map<PathogenSpatialFieldMath.Cell,int[]> original=new LinkedHashMap<>();
  for(int x=-5;x<=5;x++)for(int z=-5;z<=5;z++){
   int[] row=new int[15];
   row[virus]=((x*x+2*z*z)%7==0)?12:0;
   row[bacteria]=((x+3*z)&1)==0?4:0;
   if(row[virus]!=0||row[bacteria]!=0)
    original.put(new PathogenSpatialFieldMath.Cell(x,z),row);
  }
  var reversed=new LinkedHashMap<PathogenSpatialFieldMath.Cell,int[]>();
  var keys=new ArrayList<>(original.keySet());Collections.reverse(keys);
  for(var cell:keys)reversed.put(cell,original.get(cell).clone());
  long initialV=total(original,virus),initialB=total(original,bacteria);
  var first=ViralGameSignalWave.advance(original,0,64,0,c->true);
  var second=ViralGameSignalWave.advance(reversed,0,64,0,c->true);
  ok(digest(first.cells()).equals(digest(second.cells())),
    "map insertion ordering cannot alter spatial result");
  ok(total(first.cells(),virus)==initialV,"mass ledger conserved after one tick");
  ok(total(first.cells(),bacteria)==initialB,"unrelated channel unchanged");
  ok(total(original,virus)==initialV,"source snapshot remains unmodified");
  Map<PathogenSpatialFieldMath.Cell,int[]> current=first.cells();
  for(int frame=1;frame<75;frame++){
   var after=ViralGameSignalWave.advance(current,frame%67,64,frame,c->true);
   ok(total(after.cells(),virus)==initialV,"no invented number creation");
   ok(total(after.cells(),bacteria)==initialB,"unrelated labels isolated");
   ok(after.visited()<=64,"bounded work budget");
   ok(after.cells().size()<=1024,"bounded sparse storage");
   for(var row:after.cells().values())
    for(int n:row)ok(n>=0&&n<=15,"all game indicators finite & bounded");
   current=after.cells();
  }
  var blocked=ViralGameSignalWave.advance(original,0,64,0,c->false);
  ok(digest(original).equals(digest(blocked.cells())),
    "unloaded cells cannot be edited");
  var full=new LinkedHashMap<PathogenSpatialFieldMath.Cell,int[]>();
  int[] isolated=new int[15];isolated[virus]=12;
  full.put(new PathogenSpatialFieldMath.Cell(-2000,-2000),isolated);
  for(int i=0;i<1023;i++)
   full.put(new PathogenSpatialFieldMath.Cell(i,-123),new int[15]);
  var saturated=ViralGameSignalWave.advance(full,0,64,0,c->true);
  ok(saturated.emitted()==0,"new region refused when cap reached");
  ok(total(saturated.cells(),virus)==12,"cap cannot erase original score");
  ok(saturated.cells().size()<=1024,"region cap invariant");
  System.out.println("GUGUGAGA_FIELD_MASS_BALANCE_PASS checks="+checks+
    " conservation=true deterministic=true loadedOnly=true");
 }
}
