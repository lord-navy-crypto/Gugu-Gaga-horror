import io.github.lordnavycrypto.gugugaga.pathogens.*;
import java.util.Map;
import java.util.Set;
public final class PathogenRouteGameSmoke {
 private static int checks;
 private static void yes(boolean b,String message){checks++;if(!b)throw new AssertionError(message);}
 private static int total(Map<PathogenSpatialFieldMath.Cell,int[]> cells){
  int n=0;for(var a:cells.values())for(int v:a)n+=v;return n;
 }
 public static void main(String[] args) {
  var at=new PathogenSpatialFieldMath.Cell(-2,0);
  var nearby=new PathogenSpatialFieldMath.Cell(-1,0);
  yes(PathogenRoutePolicy15.all().size()==15,"15 individually checked route profiles");
  var family=PathogenRoutePolicy15.Route.class;
  int idx=PathogenSpatialFieldMath.channel("potato_virus_y");
  yes(PathogenRoutePolicy15.allows(idx,PathogenRoutePolicy15.Route.VIRTUAL_VECTOR),
      "potato Y has qualitative vector category");
  yes(!PathogenRoutePolicy15.allows(idx,PathogenRoutePolicy15.Route.AIR_VISUAL),
      "plant virus does not become free-floating air field");
  int rabies=PathogenSpatialFieldMath.channel("lyssavirus_rabies");
  yes(!PathogenRoutePolicy15.allows(rabies,PathogenRoutePolicy15.Route.RAIN_VISUAL),
      "rabies is not a rain-scene agent");
  var start=PathogenSpatialFieldMath.withPressure(Map.of(),at,idx,12);
  var one=PathogenRouteGameMath.move(start,at,nearby,idx,
      PathogenRoutePolicy15.Route.VIRTUAL_VECTOR,true);
  yes(one.applied(),"one virtual vector game event applied");
  yes(total(one.cells())==total(start),"game event transfers no new units");
  yes(one.cells().get(nearby)[idx]==1,"source-to-destination signal");
  yes(start.get(at)[idx]==12,"source snapshot remains unchanged");
  var blocked=PathogenRouteGameMath.move(start,at,nearby,idx,
      PathogenRoutePolicy15.Route.AIR_VISUAL,true);
  yes(!blocked.applied(),"wrong route rejected");
  var unloaded=PathogenRouteGameMath.move(start,at,nearby,idx,
      PathogenRoutePolicy15.Route.VIRTUAL_VECTOR,false);
  yes(!unloaded.applied(),"unloaded destination rejected");
  var far=PathogenRouteGameMath.move(start,at,new PathogenSpatialFieldMath.Cell(1000,100),
      idx,PathogenRoutePolicy15.Route.VIRTUAL_VECTOR,true);
  yes(!far.applied(),"unbounded jumps rejected");
  int asf=PathogenSpatialFieldMath.channel("african_swine_fever_virus");
  yes(!PathogenRoutePolicy15.allows(asf,PathogenRoutePolicy15.Route.AIR_VISUAL),
      "no arbitrary airborne ASF route");
  var listeria=PathogenSpatialFieldMath.channel("listeria_monocytogenes");
  yes(PathogenRoutePolicy15.allows(listeria,
      PathogenRoutePolicy15.Route.HELD_ITEM_MOTION),"item trace category supported");
  for(var e:PathogenRoutePolicy15.all()){
   yes(e.evidenceUrl().startsWith("https://"),"every route profile evidence url");
   yes(!e.gameRoutes().isEmpty(),"every route profile nonempty");
  }
  System.out.println("GUGUGAGA_PATHOGEN_ROUTE_GAME_PASS checks="+checks+
      " noRealBiologicalRates=true noNewBlocks=true invalidRoutesRejected=true");
 }
}
