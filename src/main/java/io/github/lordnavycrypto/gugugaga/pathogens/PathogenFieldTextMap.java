package io.github.lordnavycrypto.gugugaga.pathogens;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Text-only 7x7 heatmap of an abstract Minecraft information field.
 * Hex digits display game levels 0..15; no special blocks or mobs.
 */
public final class PathogenFieldTextMap {
    private PathogenFieldTextMap(){}
    public static final int RADIUS=3;
    public static final int GRID_SIDE=RADIUS*2+1;
    private static final char[] SYMBOLS="0123456789ABCDEF".toCharArray();

    public static List<String> render(Map<PathogenSpatialFieldMath.Cell,int[]> snapshot,
                                      PathogenSpatialFieldMath.Cell center,
                                      int channel){
        Objects.requireNonNull(snapshot);
        Objects.requireNonNull(center);
        if(channel<0||channel>=PathogenSpatialFieldMath.CHANNELS)
            throw new IllegalArgumentException("Invalid field channel");
        List<String> lines=new ArrayList<>(GRID_SIDE);
        for(int dz=-RADIUS;dz<=RADIUS;dz++){
            StringBuilder line=new StringBuilder(GRID_SIDE);
            for(int dx=-RADIUS;dx<=RADIUS;dx++){
                int[] values=snapshot.get(new PathogenSpatialFieldMath.Cell(
                        center.x()+dx,center.z()+dz));
                int number=values==null?0:values[channel];
                if(number<0||number>PathogenSpatialFieldMath.MAX_PRESSURE)
                    throw new IllegalArgumentException("Corrupt scene field value");
                line.append(SYMBOLS[number]);
            }
            lines.add(line.toString());
        }
        return List.copyOf(lines);
    }
}
