package com.redpxnda.nucleus.forge.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.resources.ResourceLocation;

@Mixin(ParticleEngine.class)
public interface ParticleEngineAccessor {
    @Accessor("providers")
    Map<ResourceLocation, ParticleProvider<?>> getProviders();
}
