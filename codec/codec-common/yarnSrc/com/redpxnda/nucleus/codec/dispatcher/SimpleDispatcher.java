package com.redpxnda.nucleus.codec.dispatcher;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.redpxnda.nucleus.Nucleus;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class SimpleDispatcher<T extends Dispatchable> extends MapCodec<T> {
    private static final Logger LOGGER = Nucleus.getLogger();

    public final Map<Identifier, MapCodec<T>> types;
    public final String typeKey;
    public final DispatcherCodec<T> codec = new DispatcherCodec<>(this);

    public SimpleDispatcher(Map<Identifier, MapCodec<T>> types, String typeKey) {
        this.types = types;
        this.typeKey = typeKey;
    }

    public SimpleDispatcher(String typeKey) {
        this.types = new ConcurrentHashMap<>();
        this.typeKey = typeKey;
    }

    public SimpleDispatcher() {
        this.types = new ConcurrentHashMap<>();
        this.typeKey = "type";
    }

    public void registerType(Identifier id, Codec<T> codec) {
        types.put(id, codec.fieldOf("value"));
    }

    public void registerType(Identifier id, MapCodec<T> codec) {
        types.put(id, codec);
    }

    @Override
    public <A> Stream<A> keys(DynamicOps<A> ops) {
        return Stream.of(ops.createString("type"));
    }

    public <A> DataResult<Identifier> getIdentifierFromData(DynamicOps<A> ops, MapLike<A> input) {
        A value = input.get(typeKey);
        if (value == null) return DataResult.error(() -> "Could not find key '" + typeKey + "' in data provided to SimpleDispatcher#getIdentifierFromData: " + input);
        return ops.getStringValue(value).map(Identifier::new);
    }

    @Override
    public <A> DataResult<T> decode(DynamicOps<A> ops, MapLike<A> input) {
        DataResult<Identifier> idResult = getIdentifierFromData(ops, input);
        if (idResult.error().isPresent()) {
            String message = idResult.error().get().message();
            return DataResult.error(() -> message);
        }
        Identifier id = idResult.result().get();
        return types.get(id).decode(ops, input);
    }

    @Override
    public <A> RecordBuilder<A> encode(T input, DynamicOps<A> ops, RecordBuilder<A> prefix) {
        Identifier id = input.getId();
        MapCodec<T> codec = types.get(id);
        if (codec != null) {
            prefix.add(typeKey, ops.createString(id.toString()));
            return codec.encode(input, ops, prefix);
        } else LOGGER.warn("Could not find registered entry '{}' for SimpleDispatcher's encoding of '{}'.", id, input);
        return prefix;
    }

    public <A> DataResult<Pair<T, A>> decode(DynamicOps<A> ops, A input) {
        return ops.getMap(input).flatMap(map -> decode(ops, map)).map(obj -> Pair.of(obj, input));
    }

    public <A> DataResult<A> encode(T input, DynamicOps<A> ops, A prefix) {
        return encode(input, ops, ops.mapBuilder()).build(prefix);
    }

    @Override
    public DispatcherCodec<T> codec() {
        return codec;
    }
}
