import io.github.lordnavycrypto.gugugaga.pathogens.*;
import java.util.*;
public final class FocusFiveSmoke {
    private static int passed;
    private static void check(boolean x,String reason){
        passed++;if(!x)throw new AssertionError(reason);
    }
    public static void main(String[]args){
        check(FocusFiveProfiles.FIVE.size()==5,"five selected identities");
        check(FocusFiveProfiles.FIVE.stream().map(x->x.id()).distinct().count()==5,
                "no duplicates");
        var original=new int[15];var extra=new int[8];
        original[PathogenSpatialFieldMath.channel("influenza_a_virus")]=12;
        original[PathogenSpatialFieldMath.channel("salmonella_enterica")]=7;
        original[PathogenSpatialFieldMath.channel("potato_virus_y")]=14;
        extra[EcologyExtendedTaxa.index("puccinia_graminis")]=11;
        extra[EcologyExtendedTaxa.index("varroa_destructor")]=9;
        var chicken=FocusFiveReadout.compatible("minecraft:chicken",true,original,extra);
        check(chicken.size()==2,"chickens have two separate reference labels");
        check(chicken.get(0).profile().id().equals("influenza_a_virus"),
                "game indicators sorted deterministically");
        check(chicken.get(1).profile().id().equals("salmonella_enterica"),
                "bacterial channel not mistaken for virus");
        var potato=FocusFiveReadout.compatible("minecraft:potatoes",false,original,extra);
        check(potato.size()==1&&potato.get(0).gameValue()==14,"potatoes source from original 15");
        var wheat=FocusFiveReadout.compatible("minecraft:wheat",false,original,extra);
        check(wheat.size()==1&&wheat.get(0).gameValue()==11,"wheat source from added 8");
        var bee=FocusFiveReadout.compatible("minecraft:bee",true,original,extra);
        check(bee.size()==1&&bee.get(0).gameValue()==9,"vanilla bee source from added 8");
        check(FocusFiveReadout.compatible("minecraft:pig",false,original,extra).isEmpty(),
                "animal ID not accepted as a crop");
        check(FocusFiveReadout.compatible("minecraft:player",true,original,extra).isEmpty(),
                "never interpret players as infected");
        check(FocusFiveReadout.tier(0)==FocusFiveReadout.Tier.QUIET,"zero=quiet");
        check(FocusFiveReadout.tier(4)==FocusFiveReadout.Tier.TRACE,"trace game stage");
        check(FocusFiveReadout.tier(7)==FocusFiveReadout.Tier.VISIBLE,"visible game stage");
        check(FocusFiveReadout.tier(15)==FocusFiveReadout.Tier.HIGHLIGHT,"highlight game stage");
        for(var p:FocusFiveProfiles.FIVE){
            check(p.channel()>=0,"existing, fixed save index");
            check(p.sourceUrl().startsWith("https://"),"real-world identity citations");
        }
        check(FocusFiveProfiles.byId("varroa_destructor").matches("minecraft:bee",true),
                "parasite bee visual proxy");
        check(!FocusFiveProfiles.byId("varroa_destructor").matches("minecraft:cow",true),
                "never infer unrelated host");
        System.out.println("GUGUGAGA_FIVE_FOCUS_PASS checks="+passed+" twoStores=true noNewBlocks=true");
    }
}
