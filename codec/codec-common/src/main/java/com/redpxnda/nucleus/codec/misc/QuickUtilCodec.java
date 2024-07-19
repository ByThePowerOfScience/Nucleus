package com.redpxnda.nucleus.codec.misc;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public interface QuickUtilCodec<A> extends Codec<A> {
    static <T> QuickUtilCodec<T> wrap(Codec<T> codec) {
        return new Wrapper<>(codec);
    }

    default <T> @Nullable A tryParse(DynamicOps<T> ops, T input) {
        return parse(ops, input).result().orElse(null);
    }

    default <T> @Nullable T tryEncode(DynamicOps<T> ops, A input) {
        return encodeStart(ops, input).result().orElse(null);
    }

    default @Nullable A tryJsonParse(JsonElement input) {
        return tryParse(JsonOps.INSTANCE, input);
    }

    default @Nullable JsonElement tryJsonEncode(A input) {
        return tryEncode(JsonOps.INSTANCE, input);
    }

    default @Nullable A tryNbtParse(Tag input) {
        return tryParse(NbtOps.INSTANCE, input);
    }

    default @Nullable Tag tryNbtEncode(A input) {
        return tryEncode(NbtOps.INSTANCE, input);
    }

    record Wrapper<A>(Codec<A> delegate) implements QuickUtilCodec<A> {
        @Override
        public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
            return delegate.decode(ops, input);
        }

        @Override
        public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
            return delegate.encode(input, ops, prefix);
        }
    }
}
