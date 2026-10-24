package io.github.lordnavycrypto.gugugaga.pathogens;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/** Independent eight-channel saved GAME ART field; old 15-channel saves stay intact. */
public final class EcologyExtendedSavedData extends SavedData {
    private static final String FILE="gugugaga_ecology_extension_v1";
    private static final Factory<EcologyExtendedSavedData> FACTORY=
            new Factory<>(EcologyExtendedSavedData::new,EcologyExtendedSavedData::load);
    private Map<PathogenSpatialFieldMath.Cell,int[]> regions=new HashMap<>();
    private int cursor;
    private EcologyExtendedSavedData(){}

    public static EcologyExtendedSavedData forLevel(ServerLevel level){
        return level.getDataStorage().computeIfAbsent(FACTORY,FILE);
    }
    public int size(){return regions.size();}
    /** Immutable-in-use snapshot for bounded read-only multi-route planning. */
    public Map<PathogenSpatialFieldMath.Cell,int[]> snapshot(){
        return java.util.Collections.unmodifiableMap(
                EcologyExtensionToyField.deepCopy(regions));
    }
    public int get(PathogenSpatialFieldMath.Cell cell,int channel){
        if(channel<0||channel>=EcologyExtensionToyField.CHANNELS)
            throw new IllegalArgumentException("Outside ecology catalog");
        int[] row=regions.get(cell);
        return row==null?0:row[channel];
    }
    public boolean set(PathogenSpatialFieldMath.Cell cell,int channel,int value){
        if(channel<0||channel>=8||value<0||value>15)
            throw new IllegalArgumentException("Invalid ecology game score");
        int[] row=regions.get(cell);
        if(row==null){
            if(value==0)return true;
            if(regions.size()>=EcologyExtensionToyField.MAX_REGIONS)return false;
            row=new int[8];
            regions.put(cell,row);
        }
        if(row[channel]==value)return true;
        row[channel]=value;
        if(Arrays.stream(row).allMatch(n->n==0))regions.remove(cell);
        setDirty();
        return true;
    }
    public EcologyExtensionToyField.Frame advance(long frame,
            Predicate<PathogenSpatialFieldMath.Cell> loadedActive){
        var next=EcologyExtensionToyField.advance(regions,cursor,frame,loadedActive);
        int oldCursor=cursor;
        cursor=next.nextCursor();
        if(next.changed()>0)regions=EcologyExtensionToyField.deepCopy(next.cells());
        if(next.changed()>0||cursor!=oldCursor)setDirty();
        return next;
    }
    public static EcologyExtendedSavedData load(CompoundTag tag,
            HolderLookup.Provider registries){
        var saved=new EcologyExtendedSavedData();
        ListTag list=tag.getList("Regions",Tag.TAG_COMPOUND);
        for(int i=0;i<list.size()&&saved.regions.size()<EcologyExtensionToyField.MAX_REGIONS;i++){
            CompoundTag nbt=list.getCompound(i);
            int[] values=nbt.getIntArray("Values");
            if(values.length!=8)continue;
            boolean valid=true,nonzero=false;
            for(int v:values){
                if(v<0||v>15)valid=false;
                if(v>0)nonzero=true;
            }
            if(!valid||!nonzero)continue;
            var cell=new PathogenSpatialFieldMath.Cell(
                    nbt.getInt("X"),nbt.getInt("Z"));
            saved.regions.put(cell,values.clone());
        }
        saved.cursor=Math.max(0,tag.getInt("Cursor"));
        return saved;
    }
    @Override public CompoundTag save(CompoundTag tag,HolderLookup.Provider registries){
        ListTag records=new ListTag();
        for(var entry:regions.entrySet()){
            CompoundTag row=new CompoundTag();
            row.putInt("X",entry.getKey().x());
            row.putInt("Z",entry.getKey().z());
            row.putIntArray("Values",entry.getValue());
            records.add(row);
        }
        tag.put("Regions",records);
        tag.putInt("Cursor",cursor);
        tag.putInt("Schema",1);
        return tag;
    }
}
