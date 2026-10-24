package io.github.lordnavycrypto.gugugaga.pathogens;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Fixed visual game stages: these are NOT real pathogen stages or clinical times. */
public final class PathogenGameSceneEngine {
    private PathogenGameSceneEngine(){}
    public static final int MAX_AGE_STEPS=32;
    public enum Stage { OBSERVING, MOTTLED, DISRUPTED, FADING }
    public enum Effect { NONE, CROP_GROWTH_PAUSED, PLANT_VISUALS, ANIMAL_VISUALS }
    public record Scene(String pathogenId,String minecraftTargetId,int frame,int gameAge) {
        public Scene {
            Objects.requireNonNull(pathogenId);
            Objects.requireNonNull(minecraftTargetId);
            PathogenRoster15.byId(pathogenId);
            if(frame<0||frame>3||gameAge<0||gameAge>=MAX_AGE_STEPS)
                throw new IllegalArgumentException("Invalid game scene state");
            if(!VanillaHostMapping15.supportsEntity(pathogenId,minecraftTargetId)
                    &&!VanillaHostMapping15.supportsBlock(pathogenId,minecraftTargetId))
                throw new IllegalArgumentException("Unsupported vanilla target");
        }
    }
    private static List<String> matches(String id,boolean entity){
        List<String> found=new ArrayList<>();
        for(var taxon:PathogenRoster15.FIRST_FIFTEEN) {
            boolean ok=entity?VanillaHostMapping15.supportsEntity(taxon.id(),id)
                    :VanillaHostMapping15.supportsBlock(taxon.id(),id);
            if(ok)found.add(taxon.id());
        }
        return List.copyOf(found);
    }
    public static List<String> forMob(String id){return matches(id,true);}
    public static List<String> forBlock(String id){return matches(id,false);}
    public static Scene chooseNext(Scene previous,String id,boolean entity){
        List<String> list=matches(id,entity);
        if(list.isEmpty())return null;
        int index=0;
        if(previous!=null && previous.minecraftTargetId().equals(id)){
            int before=list.indexOf(previous.pathogenId());
            if(before>=0)index=(before+1)%list.size();
        }
        return new Scene(list.get(index),id,0,0);
    }
    public static Scene previewAdvance(Scene old){
        return new Scene(old.pathogenId(),old.minecraftTargetId(),(old.frame()+1)%4,0);
    }
    public static Scene tick(Scene old){
        int age=(old.gameAge()+1)%MAX_AGE_STEPS;
        return new Scene(old.pathogenId(),old.minecraftTargetId(),
                age==0?(old.frame()+1)%4:old.frame(),age);
    }
    public static Stage stage(Scene scene){return Stage.values()[scene.frame()];}
    public static Effect effect(Scene scene){
        if(scene==null||scene.frame()==0)return Effect.NONE;
        if(VanillaHostMapping15.supportsBlock(scene.pathogenId(),scene.minecraftTargetId())){
            if(scene.frame()==2 && (scene.minecraftTargetId().endsWith(":potatoes")
                    ||scene.minecraftTargetId().endsWith(":beetroots")
                    ||scene.minecraftTargetId().endsWith("_stem")))
                return Effect.CROP_GROWTH_PAUSED;
            return Effect.PLANT_VISUALS;
        }
        return Effect.ANIMAL_VISUALS;
    }
}
