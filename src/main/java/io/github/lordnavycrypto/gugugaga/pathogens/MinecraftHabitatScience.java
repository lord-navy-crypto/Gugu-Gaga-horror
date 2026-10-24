package io.github.lordnavycrypto.gugugaga.pathogens;

import java.util.Objects;

/**
 * World-observed environmental covariates for an educational Minecraft
 * One Health simulation. These are ACTUAL Minecraft block coordinates,
 * vanilla sky brightness, rain, and farmland observations, not
 * inferred real-world temperature, humidity, climate or pathogen fitness.
 * At display scale one Minecraft block is approximately one metre.
 */
public final class MinecraftHabitatScience {
    private MinecraftHabitatScience(){}
    public static final int BLOCK_METRES=1;
    public static final int REGION_EDGE_BLOCKS=4;

    public record Sample(int blockX,int blockY,int blockZ,
                         int skyLight,boolean localRain,
                         boolean farmlandBelow,boolean chunkLoaded){
        public Sample{
            if(skyLight<0||skyLight>15)
                throw new IllegalArgumentException("Vanilla sky light is 0..15");
        }
        public PathogenSpatialFieldMath.Cell region(){
            return PathogenSpatialFieldMath.fromBlock(blockX,blockZ);
        }
    }
    public record Interpretation(String environmentTag,int areaBlocks,
                                 String observationSummary) {}

    /** Descriptive environmental classification, never a disease prediction. */
    public static Interpretation interpret(Sample sample){
        Objects.requireNonNull(sample);
        if(!sample.chunkLoaded())
            return new Interpretation("UNLOADED",0,
                    "Chunk not loaded: observation skipped; never force-load");
        String tag;
        if(sample.farmlandBelow()&&sample.localRain())tag="CULTIVATED_RAIN";
        else if(sample.farmlandBelow())tag="CULTIVATED";
        else if(sample.localRain())tag="RAIN";
        else if(sample.skyLight()>=12)tag="BRIGHT";
        else tag="SHADED";
        return new Interpretation(tag,REGION_EDGE_BLOCKS*REGION_EDGE_BLOCKS,
                "Vanilla rain="+sample.localRain()+
                ", sky light="+sample.skyLight()+"/15"+
                ", farmland="+sample.farmlandBelow()+
                ", sampled at Minecraft block "+sample.blockX()+","+
                sample.blockY()+","+sample.blockZ());
    }
}
