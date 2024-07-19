package com.redpxnda.nucleus.mixin.client;

import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiGraphics.class)
public interface DrawContextAccessor {
    @Accessor
    boolean isRunningDrawCallback();

    @Invoker
    void callTryDraw();

    @Invoker
    void callDrawIfRunning();
}
