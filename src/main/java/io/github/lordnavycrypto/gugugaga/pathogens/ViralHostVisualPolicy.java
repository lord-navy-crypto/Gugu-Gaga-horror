package io.github.lordnavycrypto.gugugaga.pathogens;

/**
 * Non-clinical, reversible Minecraft visual/gameplay feedback for matching
 * vanilla objects only. Labels are real taxa; intensity rules are INVENTED
 * Minecraft settings and are not evidence of infection or host susceptibility.
 */
public final class ViralHostVisualPolicy {
    private ViralHostVisualPolicy(){}
    public enum Cue { NONE, ANIMAL_PARTICLES, ANIMAL_MOVEMENT_CUE, CROP_PARTICLES, CROP_PAUSE }
    public static final int VISUAL_LEVEL=4,STRONG_LEVEL=9;

    public record Observation(String taxonId,int level,Cue cue) {}

    /** Fast rejection for unmodified 1.21.1 vanilla blocks. */
    public static boolean isViralCrop(String blockId){
        return "minecraft:potatoes".equals(blockId)
                ||"minecraft:beetroots".equals(blockId)
                ||"minecraft:melon_stem".equals(blockId)
                ||"minecraft:attached_melon_stem".equals(blockId)
                ||"minecraft:pumpkin_stem".equals(blockId)
                ||"minecraft:attached_pumpkin_stem".equals(blockId);
    }

    public static Observation forAnimal(String vanillaEntityId,int[] field){
        return choose(vanillaEntityId,field,true);
    }
    public static Observation forCrop(String vanillaBlockId,int[] field){
        return choose(vanillaBlockId,field,false);
    }
    private static Observation choose(String vanillaId,int[] field,boolean animal){
        if(vanillaId==null ||field==null ||field.length!=15)
            throw new IllegalArgumentException("Invalid vanilla game host data");
        int strongest=-1,channel=-1;
        for(int i=0;i<field.length;i++){
            int level=field[i];
            if(level<0||level>15)throw new IllegalArgumentException("Invalid game level");
            if(!ViralGameSignalPolicy.isVirusChannel(i))continue;
            String taxon=PathogenRoster15.FIRST_FIFTEEN.get(i).id();
            boolean eligible=animal
                    ?VanillaHostMapping15.supportsEntity(taxon,vanillaId)
                    :VanillaHostMapping15.supportsBlock(taxon,vanillaId);
            if(eligible &&level>strongest){strongest=level;channel=i;}
        }
        if(channel<0 ||strongest<VISUAL_LEVEL)
            return new Observation("",0,Cue.NONE);
        String taxon=PathogenRoster15.FIRST_FIFTEEN.get(channel).id();
        Cue cue=animal ? (strongest>=STRONG_LEVEL
                    ?Cue.ANIMAL_MOVEMENT_CUE:Cue.ANIMAL_PARTICLES)
                :(strongest>=STRONG_LEVEL ?Cue.CROP_PAUSE:Cue.CROP_PARTICLES);
        return new Observation(taxon,strongest,cue);
    }
}
