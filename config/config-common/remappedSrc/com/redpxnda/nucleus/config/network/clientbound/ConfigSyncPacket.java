package com.redpxnda.nucleus.config.network.clientbound;

import com.redpxnda.nucleus.config.ConfigManager;
import com.redpxnda.nucleus.network.SimplePacket;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ConfigSyncPacket implements SimplePacket {
    public final ResourceLocation config;
    public final String data;

    public ConfigSyncPacket(ResourceLocation config, String data) {
        this.config = config;
        this.data = data;
    }

    public ConfigSyncPacket(FriendlyByteBuf buf) {
        this.config = new ResourceLocation(buf.readUtf());
        this.data = buf.readUtf();
    }

    @Override
    public void toBuffer(FriendlyByteBuf buf) {
        buf.writeUtf(config.toString());
        buf.writeUtf(data);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        ConfigManager.getConfigObject(config).load(data);
    }
}
