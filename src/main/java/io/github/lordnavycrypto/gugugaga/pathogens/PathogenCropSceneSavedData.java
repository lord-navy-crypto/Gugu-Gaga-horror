package io.github.lordnavycrypto.gugugaga.pathogens;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/** Bounded per-dimension persistent SCENARIOS tied to original vanilla crops/leaves. */
public final class PathogenCropSceneSavedData extends SavedData {
    public static final int MAX_TRACKED_PLANTS=512;
    private static final String FILE_ID="gugugaga_vanilla_plant_scenes_v4";
    private static final Factory<PathogenCropSceneSavedData> FACTORY=
            new Factory<>(PathogenCropSceneSavedData::new,PathogenCropSceneSavedData::load);
    private final Map<Long,PathogenGameSceneEngine.Scene> scenes=new LinkedHashMap<>();
    private int cursor;
    private PathogenCropSceneSavedData(){}
    public static PathogenCropSceneSavedData forLevel(ServerLevel level){
        return level.getDataStorage().computeIfAbsent(FACTORY,FILE_ID);
    }
    public PathogenGameSceneEngine.Scene get(BlockPos pos){return scenes.get(pos.asLong());}
    public List<Map.Entry<Long,PathogenGameSceneEngine.Scene>> snapshot(){
        return new ArrayList<>(scenes.entrySet());
    }
    public List<Map.Entry<Long,PathogenGameSceneEngine.Scene>> candidates(int budget){
        var batch=PathogenSceneFairSchedule.rotate(snapshot(),cursor,budget);
        cursor=batch.nextCursor();
        return batch.orderedCandidates();
    }
    public boolean put(BlockPos pos,PathogenGameSceneEngine.Scene value){
        if(value==null)return false;
        if(!scenes.containsKey(pos.asLong())&&scenes.size()>=MAX_TRACKED_PLANTS)
            return false;
        scenes.put(pos.asLong(),value);
        setDirty();
        return true;
    }
    public void remove(BlockPos pos){
        if(scenes.remove(pos.asLong())!=null)setDirty();
    }
    public static PathogenCropSceneSavedData load(CompoundTag root,
                                                   HolderLookup.Provider lookup){
        var loaded=new PathogenCropSceneSavedData();
        ListTag list=root.getList("Scenes",Tag.TAG_COMPOUND);
        for(int i=0;i<list.size()&&loaded.scenes.size()<MAX_TRACKED_PLANTS;i++){
            CompoundTag row=list.getCompound(i);
            try{
                var item=new PathogenGameSceneEngine.Scene(row.getString("Taxon"),
                    row.getString("VanillaBlock"),row.getInt("Frame"),row.getInt("Age"));
                if(VanillaHostMapping15.supportsBlock(item.pathogenId(),
                        item.minecraftTargetId()))
                    loaded.scenes.put(row.getLong("Pos"),item);
            }catch(IllegalArgumentException ignored){
                // Bad local save records cannot register new species or blocks.
            }
        }
        return loaded;
    }
    @Override public CompoundTag save(CompoundTag result,HolderLookup.Provider lookup){
        ListTag list=new ListTag();
        for(var item:scenes.entrySet()){
            CompoundTag row=new CompoundTag();
            row.putLong("Pos",item.getKey());
            row.putString("Taxon",item.getValue().pathogenId());
            row.putString("VanillaBlock",item.getValue().minecraftTargetId());
            row.putInt("Frame",item.getValue().frame());
            row.putInt("Age",item.getValue().gameAge());
            list.add(row);
        }
        result.put("Scenes",list);
        return result;
    }
}
