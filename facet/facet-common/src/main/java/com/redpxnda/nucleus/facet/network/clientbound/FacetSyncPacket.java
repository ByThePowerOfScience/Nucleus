package com.redpxnda.nucleus.facet.network.clientbound;

import com.mojang.datafixers.util.Function3;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.facet.entity.EntityFacet;
import com.redpxnda.nucleus.facet.FacetKey;
import com.redpxnda.nucleus.facet.network.FacetPacketHandling;
import com.redpxnda.nucleus.network.NucleusPacket;
import com.redpxnda.nucleus.util.ByteBufUtil;
import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;

public class FacetSyncPacket<T extends Tag, C extends EntityFacet<T>> implements NucleusPacket {
    public static final Logger LOGGER = Nucleus.getLogger();
    public static final Type<FacetSyncPacket> TYPE = new Type<>(Nucleus.loc("sync_facet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FacetSyncPacket> STREAM_CODEC = createStreamCodec(FacetSyncPacket::new);

    public final int targetId;
    public final String facetId;
    public final T facetData;

    public static <T extends Tag, C extends EntityFacet<T>, P extends FacetSyncPacket<T, C>> StreamCodec<RegistryFriendlyByteBuf, P> createStreamCodec(Function3<Integer, String, T, P> creator) {
        return StreamCodec.composite(ByteBufCodecs.INT, p -> p.targetId, ByteBufCodecs.STRING_UTF8, p -> p.facetId, ByteBufCodecs.TAG, p -> p.facetData, (i, s, d) -> creator.apply(i, s, validateTagType(d)));
    }

    public static <T extends Tag> T validateTagType(Tag tag) {
        try {
            return (T) tag;
        } catch (ClassCastException e) {
            LOGGER.error("Tag in FacetSyncPacket is of wrong type. Failed to cast: ", e);
            throw e;
        }
    }

    public FacetSyncPacket(Entity target, FacetKey<C> key, C facet) {
        targetId = target.getId();
        facetId = key.id().toString();
        facetData = facet.toNbt();
    }

    public FacetSyncPacket(int id, String key, T data) {
        targetId = id;
        facetId = key;
        facetData = data;
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        FacetPacketHandling.getAndSetClientEntityFacet(targetId, ResourceLocation.parse(facetId), facetData);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return null;
    }
}
