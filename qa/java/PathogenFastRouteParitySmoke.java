import io.github.lordnavycrypto.gugugaga.pathogens.*;
import java.util.*;

public final class PathogenFastRouteParitySmoke {
    private static int checks;
    private static void ok(boolean value,String message){
        checks++;
        if(!value)throw new AssertionError(message);
    }
    private static int total(Map<PathogenSpatialFieldMath.Cell,int[]> map){
        int sum=0;
        for(var values:map.values())for(int n:values)sum+=n;
        return sum;
    }
    public static void main(String[] args) {
        // Ensure route eligibility and the copy-based reference transform
        // always agree, including at the 1024-region cap.
        Random rng=new Random(20261024L);
        var routes=PathogenRoutePolicy15.Route.values();
        for(int size:new int[]{1,16,128,1024}){
            Map<PathogenSpatialFieldMath.Cell,int[]> game=new HashMap<>();
            for(int n=0;n<size;n++){
                int[] levels=new int[15];
                for(int ch=0;ch<15;ch++)
                    levels[ch]=rng.nextInt(16);
                game.put(new PathogenSpatialFieldMath.Cell(n,0),levels);
            }
            for(int t=0;t<400;t++){
                var from=new PathogenSpatialFieldMath.Cell(rng.nextInt(size),0);
                int offset=rng.nextInt(13)-6;
                var to=new PathogenSpatialFieldMath.Cell(from.x()+offset,0);
                int ch=rng.nextInt(15);
                var route=routes[rng.nextInt(routes.length)];
                boolean loaded=rng.nextBoolean();
                int previous=total(game);
                boolean eligible=PathogenRouteGameMath.canMove(
                    game,from,to,ch,route,loaded);
                var old=PathogenRouteGameMath.move(
                    game,from,to,ch,route,loaded);
                ok(eligible==old.applied(),"O(1) fast eligibility must equal reference");
                ok(total(game)==previous,"reference transform cannot mutate source");
                if(eligible){
                    ok(total(old.cells())==previous,"one virtual token transferred");
                    ok(old.cells().get(to)[ch]>0,"destination increases");
                }
            }
        }

        // A single-cell scenario can move into a previously empty neighbor
        // when the field is not at its 1024-area capacity.
        var first=new PathogenSpatialFieldMath.Cell(-1,-1);
        var dest=new PathogenSpatialFieldMath.Cell(0,-1);
        int ch=PathogenSpatialFieldMath.channel("potato_virus_y");
        var game=PathogenSpatialFieldMath.withPressure(Map.of(),first,ch,15);
        var allowed=PathogenRoutePolicy15.Route.VIRTUAL_VECTOR;
        ok(PathogenRouteGameMath.canMove(game,first,dest,ch,allowed,true),
                "virtual signal to an empty loaded neighbor accepted");
        var after=PathogenRouteGameMath.move(game,first,dest,ch,allowed,true);
        ok(after.applied()&&after.cells().size()==2,
                "both locations hold game numbers after event");
        ok(total(after.cells())==15,"numerical event is conservative");

        // 15-channel fairness: every symbolic channel gets first opportunity.
        var center=new PathogenSpatialFieldMath.Cell(-73,19);
        Set<Integer> starts=new HashSet<>();
        for(int t=0;t<15;t++){
            int start=PathogenRouteFairScheduler.startChannel(center,t);
            ok(start>=0&&start<15,"valid game channel order");
            starts.add(start);
        }
        ok(starts.size()==15,"no permanent channel priority");
        ok(PathogenRouteFairScheduler.startChannel(center,0)
            ==PathogenRouteFairScheduler.startChannel(center,15),
            "repeatable channel order across frames");

        // Values are fictional Minecraft numbers; these asserts do not
        // approximate transmissibility, host infection, or viability.
        System.out.println("GUGUGAGA_FAST_ROUTE_PARITY_PASS checks="+checks
            +" noRealMicrobiologyRates=true immutableReference=true");
    }
}
