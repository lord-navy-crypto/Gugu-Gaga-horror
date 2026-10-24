import io.github.lordnavycrypto.gugugaga.pathogens.*;
import java.util.*;
public class FivePathGameTransportSmoke {
    static int checks;
    static void ok(boolean c,String msg){checks++;if(!c)throw new AssertionError(msg);}
    public static void main(String[]args){
        ok(FivePathGameRoutes.FIVE.size()==5,"five different public-source route concepts");
        ok(FivePathGameRoutes.FIVE.stream().map(x->x.route()).distinct().count()==5,
            "different Minecraft interaction routes");
        var tags=new FivePathGameRoutes.GameTags(
            true,false,false,false,false,false,true,false);
        ok(tags.accepts(FivePathGameRoutes.Path.BIRD_ACTIVITY_AIR_CUE),
            "bird activity route allowed");
        ok(!tags.accepts(FivePathGameRoutes.Path.POTATO_PATCH_INTERACTION),
            "plant route not inferred from birds");
        ok(!tags.accepts(FivePathGameRoutes.Path.FARM_ACTIVITY_WATER_CUE),
            "no fabricated water or farm association");
        ok(FivePathGameTransport.nextGameScore(12,0,true)==6,
            "abstract source 12 gives destination value 6");
        ok(FivePathGameTransport.nextGameScore(12,0,false)==0,
            "disallowed vanilla tag prevents movement");
        ok(FivePathGameTransport.nextGameScore(2,0,true)==0,
            "very small game signal stays local");
        ok(FivePathGameTransport.nextGameScore(12,14,true)==14,
            "weaker source cannot overwrite stronger game score");
        var from=new PathogenSpatialFieldMath.Cell(-4,-4);
        var to=from.adjacent((int)Math.floorMod(from.x()*7L+
            from.z()*11L+PathogenSpatialFieldMath.channel("potato_virus_y")*3L,4L));
        var original=new HashMap<PathogenSpatialFieldMath.Cell,int[]>();
        var extension=new HashMap<PathogenSpatialFieldMath.Cell,int[]>();
        for(var p:FocusFiveProfiles.FIVE){
            int[] row=p.store()==FocusFiveProfiles.Store.ORIGINAL_15?
                original.computeIfAbsent(from,ignored->new int[15]):
                extension.computeIfAbsent(from,ignored->new int[8]);
            row[p.channel()]=12;
        }
        var none=FivePathGameTransport.plan(original,extension,0,c->true,
            (p,c)->false);
        ok(none.proposals().isEmpty(),"no compatible destinations, no signal movement");
        var locked=FivePathGameTransport.plan(original,extension,0,c->false,
            (p,c)->true);
        ok(locked.proposals().isEmpty(),"unloaded chunks block all updates");
        for(var profile:FivePathGameRoutes.FIVE){
            var out=FivePathGameTransport.plan(original,extension,0,c->true,
                (p,c)->p.id().equals(profile.id()));
            ok(out.proposals().size()==1,"one separate route event: "+profile.id());
            var entry=out.proposals().get(0);
            ok(entry.profile().id().equals(profile.id()),"identity preserved");
            ok(entry.newDestination()==6,"all five use same fictional game score rule");
        }
        ok(original.get(from)[PathogenSpatialFieldMath.channel("potato_virus_y")]==12,
            "planner does not mutate original 15-channel field");
        ok(extension.get(from)[EcologyExtendedTaxa.index("varroa_destructor")]==12,
            "planner does not mutate independent 8-channel field");
        ok(FivePathGameTransport.plan(original,extension,0,c->true,
            (p,c)->true).proposals().size()==5,
            "five distinct categorical world-game routes operate together");
        for(int tick=0;tick<50;tick++){
            var result=FivePathGameTransport.plan(original,extension,tick,c->true,
                (p,c)->true);
            ok(result.proposals().size()<=FivePathGameTransport.MAX_EVENTS_PER_FRAME,
               "event count bounded");
            ok(result.scanned()<=5*FivePathGameTransport.MAX_SOURCES_PER_PROFILE,
               "source scans bounded");
        }
        System.out.println("GUGUGAGA_FIVE_GAME_ROUTES_PASS checks="+checks+
          " sourceBackedCategories=true inventedGameScores=true noPhysicalSpread=true");
    }
}
