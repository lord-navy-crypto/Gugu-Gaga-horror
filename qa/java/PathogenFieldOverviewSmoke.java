import io.github.lordnavycrypto.gugugaga.pathogens.*;
import java.util.*;
public final class PathogenFieldOverviewSmoke {
 static int passed=0;
 static void check(boolean b,String reason){passed++;if(!b)throw new AssertionError(reason);}
 public static void main(String[]args){
  var center=new PathogenSpatialFieldMath.Cell(0,0);
  var map=new HashMap<PathogenSpatialFieldMath.Cell,int[]>();
  var a=new int[15];
  for(int i=0;i<15;i++)a[i]=i+1;
  map.put(center,a);
  var b=new int[15];b[1]=3;
  map.put(new PathogenSpatialFieldMath.Cell(-1,0),b);
  var view=PathogenFieldOverview.inspect(map,center);
  check(view.active()==15,"15 labels");
  check(view.total()==120,"sum of purely-game values");
  check(view.top().size()==5,"bounded top five");
  check(view.top().get(0).gameUnits()==15,"top sorted");
  check(view.map().get(3).equals("003F000"),"seven-cell center and west side");
  check(view.occupied()==2,"two occupied cells");
  a[14]=0;
  check(view.top().get(0).gameUnits()==15,"display data stable");
  check(PathogenFieldOverview.inspect(Map.of(),center).map().get(3).equals("0000000"),"empty area");
  try{
   PathogenFieldOverview.inspect(Map.of(center,new int[3]),center);
   throw new AssertionError("bad channel array accepted");
  }catch(IllegalArgumentException expected){passed++;}
  System.out.println("GUGUGAGA_FIELD_OVERVIEW_PASS checks="+passed);
 }
}
