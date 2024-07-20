package com.redpxnda.nucleus.impl.forge;

import com.redpxnda.nucleus.forge.mixin.ParticleEngineAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class MiscAbstractionImpl {
    @OnlyIn(Dist.CLIENT)
    public static ParticleProvider<?> getProviderFromType(ParticleType<?> type) {
        return ((ParticleEngineAccessor) Minecraft.getInstance().particleEngine).getProviders()
                .get(BuiltInRegistries.PARTICLE_TYPE.getKey(type));
    }
}
