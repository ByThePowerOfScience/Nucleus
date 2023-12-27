package com.redpxnda.nucleus.fabric.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ParticleManager.class)
public interface ParticleManagerAccessor {
    @Accessor("factories")
    Int2ObjectMap<ParticleFactory<?>> getFactories();
}
