package io.github.lordnavycrypto.gugugaga.pathogens;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

/** The same item works on real mobs/crops/tree foliage, never a pathogen block. */
public final class PathogenFieldScannerItem extends Item {
    public PathogenFieldScannerItem(Item.Properties properties){super(properties);}
    @Override public InteractionResult interactLivingEntity(ItemStack stack,
            Player player,LivingEntity mob,InteractionHand hand){
        String id=BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
        if(PathogenGameSceneEngine.forMob(id).isEmpty())return InteractionResult.PASS;
        if(!player.level().isClientSide() && player instanceof ServerPlayer sp)
            PathogenSceneRuntime.inspectMob(sp,mob,player.isShiftKeyDown());
        return InteractionResult.sidedSuccess(player.level().isClientSide());
    }
    @Override public InteractionResult useOn(UseOnContext context){
        BlockPos pos=context.getClickedPos();
        String id=BuiltInRegistries.BLOCK.getKey(
                context.getLevel().getBlockState(pos).getBlock()).toString();
        if(PathogenGameSceneEngine.forBlock(id).isEmpty())return InteractionResult.PASS;
        if(!context.getLevel().isClientSide()&&context.getPlayer() instanceof ServerPlayer sp)
            PathogenSceneRuntime.inspectPlant(sp,pos,sp.isShiftKeyDown());
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
    }
}
