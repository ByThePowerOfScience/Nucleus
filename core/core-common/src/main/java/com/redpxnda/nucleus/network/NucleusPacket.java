package com.redpxnda.nucleus.network;

import com.redpxnda.nucleus.Nucleus;
import dev.architectury.networking.NetworkManager;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public interface NucleusPacket extends PlayerSendable, CustomPacketPayload {
    void handle(NetworkManager.PacketContext context);
    default void send(ServerPlayer player) {
        NetworkManager.sendToPlayer(player, this);
    }
    default void send(Iterable<ServerPlayer> players) {
        NetworkManager.sendToPlayers(players, this);
    }
}
