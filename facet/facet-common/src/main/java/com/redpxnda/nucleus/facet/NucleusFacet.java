package com.redpxnda.nucleus.facet;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.facet.network.TrackingUpdateSyncer;
import com.redpxnda.nucleus.facet.network.clientbound.FacetSyncPacket;
import dev.architectury.networking.NetworkManager;

public class NucleusFacet {
    public static final String MOD_ID = "nucleus_facet";
    
    public static void init() {
        TrackingUpdateSyncer.init();
        Nucleus.registerPacket(NetworkManager.Side.S2C, FacetSyncPacket.TYPE, FacetSyncPacket.STREAM_CODEC);
    }
}
