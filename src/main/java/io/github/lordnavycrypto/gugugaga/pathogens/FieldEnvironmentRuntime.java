package io.github.lordnavycrypto.gugugaga.pathogens;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;

/**
 * Read-only Minecraft 1.21.1 adapter for observations at one block position.
 * Does not force-load chunks, add mobs or blocks, or update numeric fields.
 */
public final class FieldEnvironmentRuntime {
    private FieldEnvironmentRuntime() {}

    public static FieldEnvironmentObservation.Sample observe(
            ServerLevel level, BlockPos pos) {
        if (!level.hasChunkAt(pos) || pos.getY() <= level.getMinBuildHeight()
                || pos.getY() >= level.getMaxBuildHeight()
                || !level.hasChunkAt(pos.below()))
            return new FieldEnvironmentObservation.Sample(
                    false, false, false, false, false, false, 0, 0.0f);

        var block = level.getBlockState(pos);
        boolean water = level.getFluidState(pos).is(FluidTags.WATER);
        boolean farm = level.getBlockState(pos.below()).is(Blocks.FARMLAND);
        boolean vegetation = block.is(BlockTags.LEAVES)
                || block.is(Blocks.SHORT_GRASS) || block.is(Blocks.TALL_GRASS)
                || block.is(Blocks.WHEAT) || block.is(Blocks.POTATOES)
                || block.is(Blocks.CARROTS) || block.is(Blocks.BEETROOTS);
        return new FieldEnvironmentObservation.Sample(
                true, water, farm, vegetation, level.isRainingAt(pos),
                level.canSeeSky(pos),
                level.getBrightness(LightLayer.SKY, pos),
                level.getBiome(pos).value().getBaseTemperature());
    }
}
