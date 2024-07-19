package com.redpxnda.nucleus.network.clientbound;

import com.google.gson.JsonElement;
import com.redpxnda.nucleus.network.SimplePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public abstract class SyncJsonDataPacket implements SimplePacket {
    protected final JsonElement element;

    public SyncJsonDataPacket(JsonElement element) {
        this.element = element;
    }

    public SyncJsonDataPacket(FriendlyByteBuf buf) {
        this.element = GsonHelper.parse(buf.readUtf());
    }

    @Override
    public void toBuffer(FriendlyByteBuf buf) {
        buf.writeUtf(element.toString());
    }
}
