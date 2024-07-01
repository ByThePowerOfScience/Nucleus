package com.redpxnda.nucleus.mixin;

import com.redpxnda.nucleus.event.PlayerEvents;
import dev.architectury.event.CompoundEventResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BlockBehaviour.class)
public class AbstractBlockMixin {
    @Inject(method = "calcBlockBreakingDelta", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void nucleus$playerBlockBreakSpeedEvent(BlockState state, Player player, BlockGetter world, BlockPos pos, CallbackInfoReturnable<Float> cir, float f, int i) {
        CompoundEventResult<Float> result = PlayerEvents.PLAYER_BREAK_SPEED.invoker().get(player, state, pos, cir.getReturnValue()*f*i);
        if (result.interruptsFurtherEvaluation())
            cir.setReturnValue(result.object()/f/i);
    }
}
