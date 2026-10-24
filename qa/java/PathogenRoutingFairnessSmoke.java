import io.github.lordnavycrypto.gugugaga.pathogens.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class PathogenRoutingFairnessSmoke {
    private static int checks;
    private static void check(boolean valid,String message){
        checks++;if(!valid)throw new AssertionError(message);
    }
    public static void main(String[] args){
        var positions=new ArrayList<PathogenSpatialFieldMath.Cell>();
        for(int i=0;i<1024;i++)positions.add(new PathogenSpatialFieldMath.Cell(i-512,0));
        int cursor=0;
        Set<PathogenSpatialFieldMath.Cell> visited=new HashSet<>();
        for(int frame=0;frame<16;frame++){
            var batch=PathogenRouteFairScheduler.next(positions,cursor,64);
            check(batch.cells().size()==64,"64 cells per frame");
            for(var cell:batch.cells())check(visited.add(cell),
                "Every cell gets exactly one visit in the first full sweep");
            cursor=batch.nextCursor();
        }
        check(visited.size()==1024,"No starvation at storage cap");
        check(cursor==0,"Cursor wraps exactly");
        var batch=PathogenRouteFairScheduler.next(positions,1000,64);
        check(batch.cells().size()==64,"wrap batch still bounded");
        check(batch.cells().get(0).x()==488,"sorted 1000th position");
        check(batch.cells().get(63).x()==-473,"wrap batch finish");

        var one=PathogenRouteFairScheduler.next(
            List.of(new PathogenSpatialFieldMath.Cell(3,7)),100,64);
        check(one.cells().size()==1&&one.nextCursor()==0,"single cell fair");
        check(PathogenRouteFairScheduler.next(List.of(),0,64).cells().isEmpty(),
                "empty field");
        try{
            PathogenRouteFairScheduler.next(positions,0,65);
            throw new AssertionError("Excessive budget accepted");
        }catch(IllegalArgumentException expected){checks++;}

        var origin=new PathogenSpatialFieldMath.Cell(0,0);
        var cells=PathogenSpatialFieldMath.withPressure(
            Map.of(),origin,0,15);
        List<String> picture=PathogenFieldTextMap.render(cells,origin,0);
        check(picture.size()==7,"7 rows");
        check(picture.get(3).equals("000F000"),"center signal is F (15)");
        check(picture.get(0).equals("0000000"),"empty adjacent area");
        check(PathogenFieldTextMap.render(cells,origin,1).get(3)
            .equals("0000000"),"15 game data channels independent");
        System.out.println("GUGUGAGA_ROUTE_FAIRNESS_PASS checks="+checks
             +" maxCellsVisited=1024 zeroPhysicalPathogens=true");
    }
}
