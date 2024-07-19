package com.redpxnda.nucleus.impl.fabric;

import org.apache.commons.lang3.tuple.Triple;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

public class ShaderRegistryImpl {
    public static final List<Triple<ResourceLocation, VertexFormat, Consumer<ShaderInstance>>> SHADERS = new ArrayList<>();

    public static void register(ResourceLocation loc, VertexFormat vertexFormat, Consumer<ShaderInstance> onLoad) {
        SHADERS.add(Triple.of(loc, vertexFormat, onLoad));
    }
}
