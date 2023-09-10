package com.redpxnda.nucleus.resolving.wrappers;

import net.minecraft.server.MinecraftServer;

@SuppressWarnings("unused")
public interface MinecraftServerWrapping {
    static MinecraftServer getAsServer(MinecraftServerWrapping wrapping) {
        return (MinecraftServer) wrapping;
    }
    
    /**
     * Defaults to false.
     */
    @WrapperMethod(alias = "is_hardcore")
    default boolean nucleusWrapper$isHardcore() {
        return getAsServer(this).isHardcore();
    }

    @WrapperMethod(alias = "is_nether_enabled")
    default boolean nucleusWrapper$isNetherEnabled() {
        return getAsServer(this).isNetherEnabled();
    }

    /**
     * Returns the number of players currently on the server.
     */
    @WrapperMethod(alias = "player_count")
    default int nucleusWrapper$getPlayerCount() {
        return getAsServer(this).getPlayerCount();
    }

    /**
     * Returns the maximum number of players allowed on the server.
     */
    @WrapperMethod(alias = "max_players")
    default int nucleusWrapper$getMaxPlayers() {
        return getAsServer(this).getMaxPlayers();
    }

    @WrapperMethod(alias = "is_singleplayer")
    default boolean nucleusWrapper$isSingleplayer() {
        return getAsServer(this).isSingleplayer();
    }

    @WrapperMethod(alias = "are_npcs_enabled")
    default boolean nucleusWrapper$areNpcsEnabled() {
        return getAsServer(this).areNpcsEnabled();
    }

    @WrapperMethod(alias = "is_pvp_allowed")
    default boolean nucleusWrapper$isPvpAllowed() {
        return getAsServer(this).isPvpAllowed();
    }

    @WrapperMethod(alias = "is_flight_allowed")
    default boolean nucleusWrapper$isFlightAllowed() {
        return getAsServer(this).isFlightAllowed();
    }

    @WrapperMethod(alias = "motd")
    default String nucleusWrapper$getMotd() {
        return getAsServer(this).getMotd();
    }

    @WrapperMethod(alias = "hides_online_players")
    default boolean nucleusWrapper$hidesOnlinePlayers() {
        return getAsServer(this).hidesOnlinePlayers();
    }
}