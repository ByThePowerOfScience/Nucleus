package com.redpxnda.nucleus.mixin;

import com.redpxnda.nucleus.event.PlayerEvents;
import dev.architectury.event.CompoundEventResult;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerEntityMixin {
    @Inject(method = "hasCorrectToolForDrops", at = @At("RETURN"), cancellable = true)
    private void nucleus$canPlayerHarvestEvent(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        CompoundEventResult<Boolean> result = PlayerEvents.CAN_PLAYER_HARVEST.invoker().check((Player) (Object) this, state, cir.getReturnValue());
        if (result.interruptsFurtherEvaluation())
            cir.setReturnValue(result.object());
    }

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void nucleus$playerDisplayNameEvent(CallbackInfoReturnable<Component> cir) {
        CompoundEventResult<Component> result = PlayerEvents.PLAYER_DISPLAY_NAME.invoker().get((Player) (Object) this, cir.getReturnValue());
        if (result.interruptsFurtherEvaluation())
            cir.setReturnValue(result.object());
    }
}
