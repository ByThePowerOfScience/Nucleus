package com.redpxnda.nucleus.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.redpxnda.nucleus.event.MiscEvents;
import dev.architectury.event.EventResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(
            method = "turnPlayer",
            at = @At("HEAD"),
            cancellable = true
    )
    private void nucleus$playerMoveCameraEvent(CallbackInfo ci) {
        EventResult result = MiscEvents.CAN_MOVE_CAMERA.invoker().call(minecraft);
        if (result.interruptsFurtherEvaluation())
            ci.cancel();
    }

    @WrapOperation(
            method = "turnPlayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V")
    )
    private void nucleus$modifyCameraMotionEvent(LocalPlayer instance, double xMotion, double yMotion, Operation<Void> original) {
        MiscEvents.CameraMotion motion = new MiscEvents.CameraMotion(xMotion, yMotion);
        MiscEvents.MODIFY_CAMERA_MOTION.invoker().move(minecraft, motion);
        original.call(instance, motion.getXMotion(), motion.getYMotion());
    }
}
