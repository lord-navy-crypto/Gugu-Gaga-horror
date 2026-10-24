package io.github.lordnavycrypto.gugugaga.pathogens;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * Per-dimension SavedData: sparse map of 4x4 block areas and 15 independent
 * abstract game pressure values. No per-pathogen Block, BlockEntity or Entity.
 */
public final class PathogenSpatialFieldSavedData extends SavedData {
    private static final String FILE_ID="gugugaga_spatial_pathogen_field_v1";
    private static final Factory<PathogenSpatialFieldSavedData> FACTORY=
            new Factory<>(PathogenSpatialFieldSavedData::new,
                    PathogenSpatialFieldSavedData::load);
    private Map<PathogenSpatialFieldMath.Cell,int[]> cells=new HashMap<>();
    private int cursor;
    private int routeCursor;
    private int virusWaveCursor;

    private PathogenSpatialFieldSavedData(){}
    public static PathogenSpatialFieldSavedData forLevel(ServerLevel level){
        return level.getDataStorage().computeIfAbsent(FACTORY,FILE_ID);
    }
    public int size(){return cells.size();}
    public int get(PathogenSpatialFieldMath.Cell cell,int channel){
        if(channel<0||channel>=PathogenSpatialFieldMath.CHANNELS)
            throw new IllegalArgumentException("Invalid data channel");
        int[] row=cells.get(cell);
        return row==null?0:row[channel];
    }
    public Map<PathogenSpatialFieldMath.Cell,int[]> snapshot(){
        return PathogenSpatialFieldMath.immutableCopy(cells);
    }
    /**
     * Visit an equal bounded share of spatial data sources every update.
     * The route cursor persists across save/reload, not per Java session.
     */
    public java.util.List<PathogenSpatialFieldMath.Cell> routeCandidates(int scanBudget){
        var batch=PathogenRouteFairScheduler.next(cells.keySet(),routeCursor,scanBudget);
        if(batch.nextCursor()!=routeCursor){
            routeCursor=batch.nextCursor();
            setDirty();
        }
        return batch.cells();
    }
    public boolean set(PathogenSpatialFieldMath.Cell cell,int channel,int value){
        java.util.Objects.requireNonNull(cell);
        if(channel<0||channel>=PathogenSpatialFieldMath.CHANNELS||
                value<0||value>PathogenSpatialFieldMath.MAX_PRESSURE)
            throw new IllegalArgumentException("Invalid game field value");
        // In-place O(1) edit of our OWNED sparse map. snapshot() still
        // deep-copies before returning data to external code.
        int[] row=cells.get(cell);
        int before=row==null?0:row[channel];
        if(before==value)return true;
        if(row==null){
            if(cells.size()>=PathogenSpatialFieldMath.MAX_CELLS)return false;
            row=new int[PathogenSpatialFieldMath.CHANNELS];
            cells.put(cell,row);
        }
        row[channel]=value;
        if(value==0){
            boolean empty=true;
            for(int v:row)if(v!=0){empty=false;break;}
            if(empty)cells.remove(cell);
        }
        setDirty();
        return true;
    }
    /** Transfer a single abstract game number between two already loaded areas. */
    public boolean transfer(PathogenSpatialFieldMath.Cell from,
            PathogenSpatialFieldMath.Cell to,int channel,
            PathogenRoutePolicy15.Route route,boolean loaded){
        if(!PathogenRouteGameMath.canMove(cells,from,to,channel,route,loaded))
            return false;
        // Only two channel integers change; don't clone all 1,024*15 cells
        // for each Minecraft movement/ambient event.
        int[] source=cells.get(from);
        int[] destination=cells.computeIfAbsent(to,
                ignored->new int[PathogenSpatialFieldMath.CHANNELS]);
        source[channel]--;
        destination[channel]++;
        boolean empty=true;
        for(int count:source){
            if(count>0){empty=false;break;}
        }
        if(empty)cells.remove(from);
        setDirty();
        return true;
    }
    /** Advance the GAME-ONLY wave for eight virus-labeled numeric channels. */
    public ViralGameSignalWave.Frame viralWave(long gameFrame,
            java.util.function.Predicate<PathogenSpatialFieldMath.Cell> active){
        var frame=ViralGameSignalWave.advance(cells,virusWaveCursor,64,
                gameFrame,active);
        int previousCursor=virusWaveCursor;
        virusWaveCursor=frame.nextCursor();
        if(frame.emitted()>0){
            cells=new HashMap<>(PathogenSpatialFieldMath.immutableCopy(frame.cells()));
        }
        if(frame.emitted()>0||virusWaveCursor!=previousCursor)setDirty();
        return frame;
    }
    public PathogenSpatialFieldMath.Frame step(long gameStep,
            java.util.function.Predicate<PathogenSpatialFieldMath.Cell> loadedActive){
        var frame=PathogenSpatialFieldMath.step(cells,cursor,64,gameStep,loadedActive);
        // Own independent arrays: callers may inspect/mutate returned Frame data,
        // but they must NEVER mutate our persisted world field through aliasing.
        // Cursor-only updates must persist fairness progress but need not
        // rehydrate 1024 x 15 arrays when values are unchanged.
        if(frame.moved()>0||frame.faded()>0)
            cells=new HashMap<>(PathogenSpatialFieldMath.immutableCopy(frame.cells()));
        int previousCursor=cursor;
        cursor=frame.nextCursor();
        if(frame.moved()>0||frame.faded()>0||cursor!=previousCursor)
            setDirty();
        return frame;
    }

    public static PathogenSpatialFieldSavedData load(CompoundTag nbt,
            HolderLookup.Provider registry){
        var value=new PathogenSpatialFieldSavedData();
        ListTag list=nbt.getList("Cells",Tag.TAG_COMPOUND);
        for(int i=0;i<list.size()&&value.cells.size()<PathogenSpatialFieldMath.MAX_CELLS;i++){
            CompoundTag row=list.getCompound(i);
            int[] levels=row.getIntArray("Pressure");
            if(levels.length!=PathogenSpatialFieldMath.CHANNELS)continue;
            try{
                var c=new PathogenSpatialFieldMath.Cell(
                        row.getInt("CellX"),row.getInt("CellZ"));
                boolean nonzero=false,valid=true;
                for(int n:levels){
                    if(n<0||n>PathogenSpatialFieldMath.MAX_PRESSURE)valid=false;
                    if(n>0)nonzero=true;
                }
                if(valid&&nonzero)value.cells.put(c,levels.clone());
            }catch(IllegalArgumentException ignored){
                // Invalid or stale records are ignored without crashing world loads.
            }
        }
        value.cursor=Math.max(0,nbt.getInt("Cursor"));
        value.routeCursor=Math.max(0,nbt.getInt("RouteCursor"));
        value.virusWaveCursor=Math.max(0,nbt.getInt("VirusWaveCursor"));
        return value;
    }
    @Override public CompoundTag save(CompoundTag result,
            HolderLookup.Provider registry){
        ListTag list=new ListTag();
        for(var cell:cells.entrySet()){
            CompoundTag row=new CompoundTag();
            row.putInt("CellX",cell.getKey().x());
            row.putInt("CellZ",cell.getKey().z());
            row.putIntArray("Pressure",cell.getValue());
            list.add(row);
        }
        result.put("Cells",list);
        result.putInt("Cursor",cursor);
        result.putInt("RouteCursor",routeCursor);
        result.putInt("VirusWaveCursor",virusWaveCursor);
        return result;
    }
}
