package io.github.lordnavycrypto.gugugaga.pathogens;
/** Local rendering vocabulary, not real disease detection or severity. */
public final class PathogenGameCuePolicy {
    private PathogenGameCuePolicy(){}
    public enum Visual { NONE, MOSAIC, PLANT_BLIGHT, LEAF_WILT,
        BACTERIAL_TRACE, ANIMAL_EVENT, LIVESTOCK_EVENT }
    public record Cue(Visual visual,int particleCount,boolean animalMovementCue){}
    public static Cue describe(PathogenGameSceneEngine.Scene scene){
        if(scene==null||scene.frame()==0)return new Cue(Visual.NONE,0,false);
        var record=PathogenRoster15.byId(scene.pathogenId());
        boolean plant=VanillaHostMapping15.supportsBlock(scene.pathogenId(),
                scene.minecraftTargetId());
        Visual visual;
        if(plant){
            visual=switch(record.visualKey()){
                case "plant_mosaic_visual"->Visual.MOSAIC;
                case "plant_blight_visual"->Visual.PLANT_BLIGHT;
                case "plant_wilt_visual"->Visual.LEAF_WILT;
                default->Visual.BACTERIAL_TRACE;
            };
        }else if(record.visualKey().equals("swine_context")
                ||record.visualKey().equals("livestock_context"))
            visual=Visual.LIVESTOCK_EVENT;
        else if(record.type()==PathogenRoster15.Type.BACTERIUM)
            visual=Visual.BACTERIAL_TRACE;
        else visual=Visual.ANIMAL_EVENT;
        return new Cue(visual,scene.frame()==2?2:1,!plant&&scene.frame()==2);
    }
}
