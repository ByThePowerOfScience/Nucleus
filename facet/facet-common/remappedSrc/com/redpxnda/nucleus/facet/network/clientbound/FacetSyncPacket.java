package com.redpxnda.nucleus.facet.network.clientbound;

import com.redpxnda.nucleus.facet.entity.EntityFacet;
import com.redpxnda.nucleus.facet.FacetKey;
import com.redpxnda.nucleus.facet.network.FacetPacketHandling;
import com.redpxnda.nucleus.network.SimplePacket;
import com.redpxnda.nucleus.util.ByteBufUtil;
import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class FacetSyncPacket<T extends Tag, C extends EntityFacet<T>> implements SimplePacket {
    public final int targetId;
    public final String facetId;
    public final T facetData;

    public FacetSyncPacket(Entity target, FacetKey<C> key, C facet) {
        targetId = target.getId();
        facetId = key.id().toString();
        facetData = facet.toNbt();
    }

    public FacetSyncPacket(FriendlyByteBuf buf) {
        targetId = buf.readInt();
        facetId = buf.readUtf();
        facetData = (T) ByteBufUtil.readTag(buf);
    }

    @Override
    public void toBuffer(FriendlyByteBuf buf) {
        buf.writeInt(targetId);
        buf.writeUtf(facetId);
        ByteBufUtil.writeTag(facetData, buf);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        FacetPacketHandling.getAndSetClientEntityFacet(targetId, new ResourceLocation(facetId), facetData);
    }
}
