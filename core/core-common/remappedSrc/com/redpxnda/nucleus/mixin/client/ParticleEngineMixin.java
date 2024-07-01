package com.redpxnda.nucleus.mixin.client;

import net.minecraft.client.particle.ParticleEngine;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {
    /*@Inject(method = "render", at = @At("HEAD"))
    private void nucleus$particleRenderTypesFix(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, LightTexture lightTexture, Camera camera, float f, CallbackInfo ci) {

    }*/
}
