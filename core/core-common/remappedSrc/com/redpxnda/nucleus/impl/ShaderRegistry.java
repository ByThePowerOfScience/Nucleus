package com.redpxnda.nucleus.impl;

import com.mojang.blaze3d.vertex.VertexFormat;
import dev.architectury.injectables.annotations.ExpectPlatform;
import java.util.function.Consumer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

public class ShaderRegistry {
    @ExpectPlatform
    public static void register(ResourceLocation loc, VertexFormat vertexFormat, Consumer<ShaderInstance> onLoad) {
        throw new AssertionError();
    }
}
