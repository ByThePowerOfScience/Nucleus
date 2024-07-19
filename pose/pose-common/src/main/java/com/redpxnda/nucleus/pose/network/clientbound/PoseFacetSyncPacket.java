package com.redpxnda.nucleus.pose.network.clientbound;

import com.redpxnda.nucleus.facet.network.FacetPacketHandling;
import com.redpxnda.nucleus.facet.network.clientbound.FacetSyncPacket;
import com.redpxnda.nucleus.pose.client.ClientPoseFacet;
import com.redpxnda.nucleus.pose.server.ServerPoseFacet;
import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public class PoseFacetSyncPacket extends FacetSyncPacket<CompoundTag, ServerPoseFacet> {
    public PoseFacetSyncPacket(Entity target, ServerPoseFacet cap) {
        super(target, ServerPoseFacet.KEY, cap);
    }

    public PoseFacetSyncPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        FacetPacketHandling.getAndSetClientEntityFacet(targetId, ClientPoseFacet.loc, facetData);
    }
}
