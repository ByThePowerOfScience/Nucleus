package com.redpxnda.nucleus.mixin;

import com.redpxnda.nucleus.event.PlayerEvents;
import dev.architectury.event.CompoundEventResult;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "getPlayerListName", at = @At("RETURN"), cancellable = true)
    private void nucleus$playerTabListNameEvent(CallbackInfoReturnable<Component> cir) {
        CompoundEventResult<Component> result = PlayerEvents.PLAYER_TAB_LIST_NAME.invoker().get((Player) (Object) this, cir.getReturnValue());
        if (result.interruptsFurtherEvaluation())
            cir.setReturnValue(result.object());
    }
}
