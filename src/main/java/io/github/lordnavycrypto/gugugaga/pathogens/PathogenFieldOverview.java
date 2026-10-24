package io.github.lordnavycrypto.gugugaga.pathogens;
import java.util.*;
/** Compact, entirely game-data environmental overview; never a diagnosis. */
public final class PathogenFieldOverview {
    private PathogenFieldOverview(){}
    public record Channel(String id,String type,int gameUnits){}
    public record Snapshot(List<Channel> top,List<String> map,int active,int total,int occupied){
        public Snapshot{top=List.copyOf(top);map=List.copyOf(map);}
    }
    public static Snapshot inspect(Map<PathogenSpatialFieldMath.Cell,int[]> field,
                                   PathogenSpatialFieldMath.Cell center){
        Objects.requireNonNull(field);
        Objects.requireNonNull(center);
        int[] here=field.get(center);
        List<Channel> channels=new ArrayList<>();
        int total=0;
        if(here!=null){
            if(here.length!=15)throw new IllegalArgumentException("Not 15 channels");
            for(int i=0;i<15;i++){
                int n=here[i];
                if(n<0||n>15)throw new IllegalArgumentException("Bad game value");
                total+=n;
                if(n>0){
                    var p=PathogenRoster15.FIRST_FIFTEEN.get(i);
                    channels.add(new Channel(p.id(),p.type().name(),n));
                }
            }
        }
        channels.sort(Comparator.comparingInt(Channel::gameUnits).reversed()
                .thenComparing(Channel::id));
        List<String> lines=new ArrayList<>(7);
        int occupied=0;
        String digits="0123456789ABCDEF";
        for(int z=-3;z<=3;z++){
            StringBuilder line=new StringBuilder(7);
            for(int x=-3;x<=3;x++){
                int strongest=0;
                int[] values=field.get(new PathogenSpatialFieldMath.Cell(
                    center.x()+x,center.z()+z));
                if(values!=null){
                    if(values.length!=15)throw new IllegalArgumentException("Bad cell");
                    for(int n:values){
                        if(n<0||n>15)throw new IllegalArgumentException("Bad game value");
                        strongest=Math.max(strongest,n);
                    }
                }
                if(strongest>0)occupied++;
                line.append(digits.charAt(strongest));
            }
            lines.add(line.toString());
        }
        return new Snapshot(channels.subList(0,Math.min(5,channels.size())),
                lines,channels.size(),total,occupied);
    }
}
