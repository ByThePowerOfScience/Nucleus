package com.redpxnda.nucleus.pose.network.clientbound;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.facet.network.FacetPacketHandling;
import com.redpxnda.nucleus.facet.network.clientbound.FacetSyncPacket;
import com.redpxnda.nucleus.pose.client.ClientPoseFacet;
import com.redpxnda.nucleus.pose.server.ServerPoseFacet;
import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;

public class PoseFacetSyncPacket extends FacetSyncPacket<CompoundTag, ServerPoseFacet> {
    public static final Type<PoseFacetSyncPacket> TYPE = new Type<>(Nucleus.loc("sync_pose_facet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PoseFacetSyncPacket> STREAM_CODEC = createStreamCodec(PoseFacetSyncPacket::new);

    public PoseFacetSyncPacket(Entity target, ServerPoseFacet cap) {
        super(target, ServerPoseFacet.KEY, cap);
    }
    public PoseFacetSyncPacket(int id, String key, CompoundTag data) {
        super(id, key, data);
    }


    @Override
    public void handle(NetworkManager.PacketContext context) {
        FacetPacketHandling.getAndSetClientEntityFacet(targetId, ClientPoseFacet.loc, facetData);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
