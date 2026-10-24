import io.github.lordnavycrypto.gugugaga.pathogens.*;
import java.util.*;
public final class PathogenVanillaSceneSmoke{
    private static int count;
    private static void ok(boolean v,String reason){
        count++;if(!v)throw new AssertionError(reason);
    }
    public static void main(String[] args){
        ok(PathogenRoster15.FIRST_FIFTEEN.size()==15,"exactly 15 real taxa");
        for(var record:PathogenRoster15.FIRST_FIFTEEN){
            var targets=VanillaHostMapping15.byId(record.id());
            ok(!targets.entityIds().isEmpty()||!targets.blockIds().isEmpty(),
                    "every taxon maps to real Minecraft object");
            for(String id:targets.entityIds()){
                var s=new PathogenGameSceneEngine.Scene(record.id(),id,2,0);
                ok(PathogenGameSceneEngine.effect(s)==PathogenGameSceneEngine.Effect.ANIMAL_VISUALS,
                        "animal-specific cosmetic stage");
            }
            for(String id:targets.blockIds()){
                var s=new PathogenGameSceneEngine.Scene(record.id(),id,1,0);
                ok(PathogenGameSceneEngine.effect(s)==PathogenGameSceneEngine.Effect.PLANT_VISUALS,
                        "plant-specific cosmetic stage");
            }
        }
        ok(PathogenGameSceneEngine.chooseNext(null,"minecraft:pig",true)!=null,"real pig");
        ok(!PathogenGameSceneEngine.forMob("minecraft:cow").contains("african_swine_fever_virus"),"no cow swine scene");
        ok(PathogenGameSceneEngine.forBlock("minecraft:oak_leaves").contains("erwinia_amylovora"),"generic tree");
        ok(PathogenGameSceneEngine.forBlock("minecraft:pale_oak_leaves").isEmpty(),"reject 1.21.1 missing tree");
        ok(VanillaHostMapping15.sameStemFamily("minecraft:melon_stem","minecraft:attached_melon_stem"),
                "vanilla melon attachment preserves scene");
        ok(!VanillaHostMapping15.sameStemFamily("minecraft:melon_stem","minecraft:attached_pumpkin_stem"),
                "never confuse melon and pumpkin");
        var potato=PathogenGameSceneEngine.chooseNext(null,"minecraft:potatoes",false);
        ok(potato.pathogenId().equals("potato_virus_y"),"potato first scenario");
        ok(PathogenGameSceneEngine.chooseNext(potato,"minecraft:potatoes",false)
                .pathogenId().equals("phytophthora_infestans"),"potato second");
        var test=PathogenGameSceneEngine.previewAdvance(PathogenGameSceneEngine.previewAdvance(potato));
        ok(PathogenGameSceneEngine.effect(test)==PathogenGameSceneEngine.Effect.CROP_GROWTH_PAUSED,
                "bounded optional crop gameplay effect");
        List<Integer> jobs=new ArrayList<>();for(int i=0;i<512;i++)jobs.add(i);
        Set<Integer> served=new HashSet<>();int cursor=0;
        for(int t=0;t<8;t++){
            var batch=PathogenSceneFairSchedule.rotate(jobs,cursor,64);
            for(int i=0;i<64;i++)ok(served.add(batch.orderedCandidates().get(i)),"no starvation");
            cursor=batch.nextCursor();
        }
        ok(served.size()==512,"all enrolled plants rotate");
        System.out.println("GUGUGAGA_VANILLA_15_SCENE_PASS checks="+count+
                " noRealPathogenPropagation=true zeroNewBlocks=true");
    }
}
