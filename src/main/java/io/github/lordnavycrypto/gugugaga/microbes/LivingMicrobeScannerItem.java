package io.github.lordnavycrypto.gugugaga.microbes;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Hand-held living-organism sampler: click mobs, never plants/blocks. */
public final class LivingMicrobeScannerItem extends Item {
    public LivingMicrobeScannerItem(Item.Properties properties){super(properties);}

    @Override public InteractionResult interactLivingEntity(ItemStack stack,
            Player player,LivingEntity mob,InteractionHand hand){
        if(!player.level().isClientSide()&&player instanceof ServerPlayer serverPlayer) {
            LivingMicrobeRuntime.inspect(serverPlayer,mob,player.isShiftKeyDown());
        }
        return InteractionResult.sidedSuccess(player.level().isClientSide());
    }
}
