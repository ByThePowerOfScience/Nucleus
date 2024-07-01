package com.redpxnda.nucleus.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Deque;

@Mixin(PoseStack.class)
public interface MatrixStackAccessor {
    @Accessor
    Deque<PoseStack.Pose> getStack();
}
