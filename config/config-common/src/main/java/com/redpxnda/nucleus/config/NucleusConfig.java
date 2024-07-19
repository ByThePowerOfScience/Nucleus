package com.redpxnda.nucleus.config;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.codec.auto.AutoCodec;
import com.redpxnda.nucleus.config.network.clientbound.ConfigSyncPacket;
import com.redpxnda.nucleus.util.Comment;
import dev.architectury.networking.NetworkManager;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class NucleusConfig {
    public static final String MOD_ID = "nucleus_config";
    public static void ifPresent(Consumer<NucleusConfig> action) {
        if (INSTANCE != null) action.accept(INSTANCE);
    }
    public static @Nullable NucleusConfig INSTANCE;

    public static void init() {
        ConfigManager.init();
        Nucleus.registerPacket(NetworkManager.Side.S2C, ConfigSyncPacket.TYPE, ConfigSyncPacket.STREAM_CODEC);
    }

    @AutoCodec.Name("watch_changes")
    @Comment("""
            Whether or not changes to configs should be watched and reacted to. (Configs normally auto-update when modified)
            Note: Changing this will require you to restart your game for it to be applied.
            """)
    public boolean watchChanges = true;
}
