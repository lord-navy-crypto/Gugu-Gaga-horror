package io.github.lordnavycrypto.gugugaga.pathogens;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;

/** Reads the real vanilla world from a loaded block position only. */
public final class MinecraftHabitatRuntime {
    private MinecraftHabitatRuntime(){}
    public static MinecraftHabitatScience.Sample sample(ServerLevel level,BlockPos pos){
        boolean available=level.hasChunkAt(pos) && level.hasChunkAt(pos.below());
        if(!available)
            return new MinecraftHabitatScience.Sample(
                pos.getX(),pos.getY(),pos.getZ(),0,false,false,false);
        return new MinecraftHabitatScience.Sample(
                pos.getX(),pos.getY(),pos.getZ(),
                level.getBrightness(LightLayer.SKY,pos),
                level.isRainingAt(pos),
                level.getBlockState(pos.below()).is(Blocks.FARMLAND),
                true);
    }
}
