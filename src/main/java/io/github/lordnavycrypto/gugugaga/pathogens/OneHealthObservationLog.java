package io.github.lordnavycrypto.gugugaga.pathogens;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * Stores observations of vanilla Minecraft objects and abstract game numbers.
 * NOT a clinical case registry, a pathogen count, a disease diagnosis or a
 * patient record. No player names or UUIDs are stored in the NBT.
 */
public final class OneHealthObservationLog extends SavedData {
    public static final String FILE="gugugaga_one_health_observations_v1";
    public static final int CAPACITY=128;
    private static final Factory<OneHealthObservationLog> FACTORY=
            new Factory<>(OneHealthObservationLog::new,OneHealthObservationLog::load);
    public record Record(String taxonId,int cellX,int cellZ,long worldTime,
                         int vanillaObjects,int virtualScore){
        public Record {
            OneHealthEvidence5.byId(taxonId);
            if(worldTime<0||vanillaObjects<0||vanillaObjects>
                    OneHealthObservation.MAX_CROPS_SCANNED||
                    virtualScore<0||virtualScore>15)
                throw new IllegalArgumentException("Invalid Minecraft observation");
        }
    }
    private final List<Record> records=new ArrayList<>();
    private OneHealthObservationLog(){}
    public static OneHealthObservationLog forLevel(ServerLevel level){
        return level.getDataStorage().computeIfAbsent(FACTORY,FILE);
    }
    public void append(Record record){
        if(records.size()==CAPACITY)records.remove(0);
        records.add(record);
        setDirty();
    }
    public List<Record> recent(int count){
        if(count<0||count>CAPACITY)throw new IllegalArgumentException("Too many records");
        int from=Math.max(0,records.size()-count);
        return List.copyOf(records.subList(from,records.size()));
    }
    public int size(){return records.size();}
    public static OneHealthObservationLog load(CompoundTag tag,
                                                HolderLookup.Provider registries){
        var result=new OneHealthObservationLog();
        ListTag list=tag.getList("Observations",Tag.TAG_COMPOUND);
        for(int i=Math.max(0,list.size()-CAPACITY);i<list.size();i++){
            CompoundTag row=list.getCompound(i);
            try {
                result.records.add(new Record(row.getString("Taxon"),
                    row.getInt("CellX"),row.getInt("CellZ"),
                    row.getLong("Tick"),row.getInt("Objects"),
                    row.getInt("GameScore")));
            } catch(IllegalArgumentException invalid){
                // Modpack updates may remove taxonomy labels: skip old rows
                // instead of failing world loading or inventing a diagnosis.
            }
        }
        return result;
    }
    @Override public CompoundTag save(CompoundTag tag,HolderLookup.Provider registries){
        ListTag list=new ListTag();
        for(var record:records){
            CompoundTag row=new CompoundTag();
            row.putString("Taxon",record.taxonId());
            row.putInt("CellX",record.cellX());
            row.putInt("CellZ",record.cellZ());
            row.putLong("Tick",record.worldTime());
            row.putInt("Objects",record.vanillaObjects());
            row.putInt("GameScore",record.virtualScore());
            list.add(row);
        }
        tag.put("Observations",list);
        tag.putInt("Schema",1);
        return tag;
    }
}
