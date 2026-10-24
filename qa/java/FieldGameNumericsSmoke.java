import io.github.lordnavycrypto.gugugaga.pathogens.*;
import java.util.*;

/** Long-run QA of symbolic Minecraft game units, never infection counts. */
public final class FieldGameNumericsSmoke {
    private static int checks=0;
    private static void ok(boolean condition,String why){
        checks++;if(!condition)throw new AssertionError(why);
    }
    private static long[] totals(Map<PathogenSpatialFieldMath.Cell,int[]> grid){
        long[] result=new long[15];
        for(int[] row:grid.values())for(int i=0;i<15;i++)result[i]+=row[i];
        return result;
    }
    private static String digest(Map<PathogenSpatialFieldMath.Cell,int[]> grid){
        List<PathogenSpatialFieldMath.Cell> keys=new ArrayList<>(grid.keySet());
        keys.sort(Comparator.comparingInt(PathogenSpatialFieldMath.Cell::x)
                .thenComparingInt(PathogenSpatialFieldMath.Cell::z));
        StringBuilder sb=new StringBuilder();
        for(var cell:keys)sb.append(cell).append(Arrays.toString(grid.get(cell)));
        return sb.toString();
    }
    private static Map<PathogenSpatialFieldMath.Cell,int[]> scenario(boolean reverse){
        Map<PathogenSpatialFieldMath.Cell,int[]> grid=new LinkedHashMap<>();
        for(int x=-9;x<=9;x++)for(int z=-6;z<=6;z++){
            int[] row=new int[15];
            row[(x*x+z*z)%15]=4+Math.floorMod(11*x+7*z,12);
            row[5]=Math.min(15,row[5]+(Math.floorMod(x+z,3)==0?2:0));
            grid.put(new PathogenSpatialFieldMath.Cell(x,z),row);
        }
        if(!reverse)return grid;
        var backwards=new LinkedHashMap<PathogenSpatialFieldMath.Cell,int[]>();
        var keys=new ArrayList<>(grid.keySet()); Collections.reverse(keys);
        for(var cell:keys)backwards.put(cell,grid.get(cell).clone());
        return backwards;
    }
    private static void audit(int frames){
        Map<PathogenSpatialFieldMath.Cell,int[]> left=scenario(false);
        Map<PathogenSpatialFieldMath.Cell,int[]> right=scenario(true);
        int cursorLeft=0,cursorRight=0;
        for(int t=0;t<frames;t++){
            String oldDigest=digest(left);
            long[] before=totals(left);
            final int gameFrame=t;
            var loaded=(java.util.function.Predicate<PathogenSpatialFieldMath.Cell>)
                    cell->Math.floorMod(cell.x()+cell.z()+gameFrame/15,11)!=0;
            var a=PathogenSpatialFieldMath.step(left,cursorLeft,64,t,loaded);
            var b=PathogenSpatialFieldMath.step(right,cursorRight,64,t,loaded);
            ok(digest(left).equals(oldDigest),"callers cannot mutate input snapshot");
            ok(digest(a.cells()).equals(digest(b.cells())),
                    "independent HashMap insertion order");
            ok(a.nextCursor()==b.nextCursor(),"deterministic cursor rotation");
            ok(a.processed()<=64 && a.moved()<=64*15 && a.faded()<=64*15,
                    "bounded per-frame work");
            ok(a.cells().size()<=1024,"maximum game field regions");
            long[] after=totals(a.cells());
            long debit=0;
            for(int channel=0;channel<15;channel++){
                ok(before[channel]>=after[channel],
                        "game motion cannot invent units on a channel");
                debit+=before[channel]-after[channel];
            }
            ok(debit==a.faded(),
                    "net game field loss exactly equals configured toy fading");
            for(int[] row:a.cells().values())
                for(int n:row)ok(n>=0 && n<=15,
                        "bounded finite game signal only");
            cursorLeft=a.nextCursor();cursorRight=b.nextCursor();
            left=a.cells();right=b.cells();
        }
    }
    public static void main(String[] args){
        ok(PathogenSpatialFieldMath.fromBlock(-1,-1)
                .equals(new PathogenSpatialFieldMath.Cell(-1,-1)),
                "negative coordinates use mathematical floor");
        ok(PathogenSpatialFieldMath.fromBlock(-4,-5)
                .equals(new PathogenSpatialFieldMath.Cell(-1,-2)),
                "negative block/chunk boundaries do not jump");
        var init=scenario(false);
        var halted=PathogenSpatialFieldMath.step(init,0,64,1,c->false);
        ok(digest(init).equals(digest(halted.cells())),
                "inactive and unloaded areas must not change");
        audit(150);
        System.out.println("V19_FIELD_NUMERICS_PASS checks="+checks
                +" deterministic=true conservativeToyTransfers=true");
    }
}
