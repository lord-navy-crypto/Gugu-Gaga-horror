package io.github.lordnavycrypto.gugugaga.pathogens;

import java.util.Objects;

/**
 * One lightweight VANILLA ACTION BAR line. This is fictional Minecraft
 * environmental DATA, not a clinical or infection readout.
 */
public final class PathogenHudTextPolicy {
    private PathogenHudTextPolicy(){}
    public static final int CHANNEL_COUNT=15;

    public static String format(PathogenSpatialFieldMath.Cell cell,int[] values) {
        Objects.requireNonNull(cell);
        if(values==null||values.length!=CHANNEL_COUNT)
            throw new IllegalArgumentException("Expect fifteen data-only game channels");
        int active=0,total=0,max=0,channel=-1;
        for(int i=0;i<CHANNEL_COUNT;i++){
            int level=values[i];
            if(level<0||level>PathogenSpatialFieldMath.MAX_PRESSURE)
                throw new IllegalArgumentException("Invalid virtual game number");
            if(level>0){active++;total+=level;}
            if(level>max){channel=i;max=level;}
        }
        String prefix="[GAME FIELD "+cell.x()+","+cell.z()+"] ";
        if(active==0)return prefix+"no active signals";
        String id=PathogenRoster15.FIRST_FIFTEEN.get(channel).id();
        String shortName=id.length()>22?id.substring(0,22)+"…":id;
        return prefix+shortName+" "+max+"/15 | "+active+" labels, "+total+" units";
    }
}
