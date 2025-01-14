package com.redpxnda.nucleus.fabric.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ParticleEngine.class)
public interface ParticleManagerAccessor {
    @Accessor("factories")
    Int2ObjectMap<ParticleProvider<?>> getFactories();
}
