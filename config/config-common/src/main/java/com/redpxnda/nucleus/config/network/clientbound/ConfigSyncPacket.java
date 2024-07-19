package com.redpxnda.nucleus.config.network.clientbound;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.config.ConfigManager;
import com.redpxnda.nucleus.network.NucleusPacket;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class ConfigSyncPacket implements NucleusPacket {
    public static final Type<ConfigSyncPacket> TYPE = new Type<>(Nucleus.loc("sync_config"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigSyncPacket> STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, p -> p.config, ByteBufCodecs.STRING_UTF8, p -> p.data, ConfigSyncPacket::new);

    public final ResourceLocation config;
    public final String data;

    public ConfigSyncPacket(ResourceLocation config, String data) {
        this.config = config;
        this.data = data;
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        ConfigManager.getConfigObject(config).load(data);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
