package com.redpxnda.nucleus.util;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.Nucleus;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ByteBufUtil {
    private static final Logger LOGGER = Nucleus.getLogger();

    public static <T> void writeWithCodec(FriendlyByteBuf buf, T instance, Codec<T> codec) {
        Tag tag = codec.encodeStart(NbtOps.INSTANCE, instance).getOrThrow(false, s ->
                LOGGER.error("Failed to encode {} into a FriendlyByteBuf using codec -> {}", instance, s)
        );
        writeTag(tag, buf);
    }
    public static <T> T readWithCodec(FriendlyByteBuf buf, Codec<T> codec) {
        Tag tag = readTag(buf);
        return codec.parse(NbtOps.INSTANCE, tag).getOrThrow(false, s ->
                LOGGER.error("Failed to decode tag '{}' from FriendlyByteBuf using codec -> {}", tag, s)
        );
    }

    public static void writeTag(Tag tag, FriendlyByteBuf buf) {
        try {
            NbtIo.writeUnnamedTag(tag, new ByteBufOutputStream(buf));
        } catch (IOException e) {
            LOGGER.error("Failed to write Nbt tag '{}' to byte buffer!", tag);
            throw new RuntimeException(e);
        }
    }
    public static Tag readTag(FriendlyByteBuf buf) {
        try {
            return NbtIo.readUnnamedTag(new ByteBufInputStream(buf), 0, NbtAccounter.UNLIMITED);
        } catch (IOException e) {
            LOGGER.error("Failed to read Nbt from byte buffer!");
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes a String -> anything map to a packetbytebuf
     * @param map the
     * @param writer the consumer defining how to write this object to a buffer
     * @param buf the buffer to write to
     */
    public static <T> void writeMap(Map<String, T> map, BiConsumer<FriendlyByteBuf, T> writer, FriendlyByteBuf buf) {
        buf.writeInt(map.size());
        map.forEach((key, value) -> {
            assert value != null : "No null values allowed for map byte buffer writing.";
            buf.writeUtf(key);
            writer.accept(buf, value);
        });
    }

    /**
     * Reads a String -> anything map from a packetbytebuf
     * @param creator the creator for the map (don't create an immutable map!)
     * @param reader the function defining how to read this object
     * @param buf the buffer to read from
     */
    public static <T, M extends Map<String, T>> Map<String, T> readMap(Supplier<M> creator, Function<FriendlyByteBuf, T> reader, FriendlyByteBuf buf) {
        M map = creator.get();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            map.put(buf.readUtf(), reader.apply(buf));
        }
        return map;
    }

    public static Map<String, Long> readLongMap(Supplier<Map<String, Long>> creator, FriendlyByteBuf buf) {
        Map<String, Long> map = creator.get();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            map.put(buf.readUtf(), buf.readLong());
        }
        return map;
    }
    public static void writeLongMap(Map<String, Long> map, FriendlyByteBuf buf) {
        buf.writeInt(map.size());
        map.forEach((key, value) -> {
            assert value != null : "No null values allowed for long map byte buffer writing.";
            buf.writeUtf(key);
            buf.writeLong(value);
        });
    }
}
