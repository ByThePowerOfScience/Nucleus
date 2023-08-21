package com.redpxnda.nucleus.registry.particles;

import com.redpxnda.nucleus.codec.ValueAssigner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class MimicParticle extends DynamicTextureSheetParticle implements MimicParticleOptions.Manager {
    public ResourceLocation texture;
    public final ValueAssigner<MimicParticleOptions.Manager> tick;

    public MimicParticle(
            ValueAssigner<MimicParticleOptions.Manager> setup, ValueAssigner<MimicParticleOptions.Manager> tick,
            ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
    ) {
        super(null, RenderType.cutout(), clientLevel, x, y, z, xSpeed, ySpeed, zSpeed);
        setup.assignTo(this);
        this.tick = tick;

        this.sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture);
    }

    @Override
    public void tick() {
        super.tick();
        tick.assignTo(this);
    }

    @Override
    public ResourceLocation getTexture() {
        return texture;
    }

    @Override
    public void setTexture(ResourceLocation rl) {
        texture = rl;
    }

    public static class Provider implements ParticleProvider<MimicParticleOptions> {
        @Nullable
        @Override
        public Particle createParticle(MimicParticleOptions o, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            return new MimicParticle(o.setup, o.tick, clientLevel, d, e, f, g, h, i);
        }
    }
}
