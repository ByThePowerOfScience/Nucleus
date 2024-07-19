package com.redpxnda.nucleus.network.clientbound;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.network.ClientboundHandling;
import com.redpxnda.nucleus.network.NucleusPacket;
import com.redpxnda.nucleus.util.ByteBufUtil;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public class ParticleCreationPacket implements NucleusPacket {
    public static final Type<ParticleCreationPacket> TYPE = new Type<>(Nucleus.loc("particle_creation"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleCreationPacket> STREAM_CODEC = ByteBufUtil.composite(ParticleTypes.STREAM_CODEC, p -> p.options, ByteBufCodecs.DOUBLE, p -> p.x, ByteBufCodecs.DOUBLE, p -> p.y, ByteBufCodecs.DOUBLE, p -> p.z, ByteBufCodecs.DOUBLE, p -> p.xs, ByteBufCodecs.DOUBLE, p -> p.ys, ByteBufCodecs.DOUBLE, p -> p.zs, ParticleCreationPacket::new);

    private final ParticleOptions options;
    private final double x;
    private final double y;
    private final double z;
    private final double xs;
    private final double ys;
    private final double zs;

    public ParticleCreationPacket(ParticleOptions options, double x, double y, double z, double xs, double ys, double zs) {
        this.options = options;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xs = xs;
        this.ys = ys;
        this.zs = zs;
    }

    @Override
    public void send(ServerLevel level) {
        NetworkManager.sendToPlayers(level.getPlayers(serverPlayer -> {
            if (serverPlayer.level() != level) return false;
            BlockPos blockPos = serverPlayer.blockPosition();
            return blockPos.closerToCenterThan(new Vec3(x, y, z), 32.0);
        }), this);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        ClientboundHandling.createClientParticle(options, x, y, z, xs, ys, zs);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return null;
    }
}
