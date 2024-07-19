package com.redpxnda.nucleus.codec.tag;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class TaggableEntryCodec<E, P extends TaggableEntry<E>> implements Codec<P> {
    protected final Function<TagKey<E>, P> fromTag;
    protected final Function<E, P> fromObject;
    protected final Registry<E> registry;
    protected final ResourceKey<? extends Registry<E>> registryKey;

    public TaggableEntryCodec(Function<TagKey<E>, P> fromTag, Function<E, P> fromObject, Registry<E> registry, ResourceKey<? extends Registry<E>> registryKey) {
        this.fromTag = fromTag;
        this.fromObject = fromObject;
        this.registry = registry;
        this.registryKey = registryKey;
    }

    @Override
    public <T> DataResult<Pair<P, T>> decode(DynamicOps<T> ops, T input) {
        var potentialStr = ops.getStringValue(input);
        if (potentialStr.result().isPresent()) {
            String str = potentialStr.result().get();
            if (str.startsWith("#")) {
                ResourceLocation id = ResourceLocation.tryParse(str.substring(1));
                if (id == null)
                    return DataResult.error(() -> "Failed to create identifier for taggable entry(tag) '" + str + "'! Make sure it's correctly formatted.");
                return DataResult.success(Pair.of(fromTag.apply(TagKey.create(registryKey, id)), input));
            } else {
                ResourceLocation id = ResourceLocation.tryParse(str);
                if (id == null)
                    return DataResult.error(() -> "Failed to create identifier for taggable entry(object) '" + str + "'! Make sure it's correctly formatted.");

                E obj = registry.getOptional(id).orElse(null);
                if (obj == null)
                    return DataResult.error(() -> "Failed to object of id '" + str + "' for taggable entry! Make sure it's a valid id.");
                return DataResult.success(Pair.of(fromObject.apply(obj), input));
            }
        }
        return DataResult.error(() -> "Element for taggable entry must be a string! Found '" + input + "' instead.");
    }

    @Override
    public <T> DataResult<T> encode(P input, DynamicOps<T> ops, T prefix) {
        if (input.getObject() != null) {
            ResourceLocation id = registry.getKey(input.getObject());
            return ResourceLocation.CODEC.encode(id, ops, prefix);
        }

        assert input.getTag() != null : "tag and object of taggable entry cannot both be null";
        return DataResult.success(ops.createString("#" + input.getTag().location().toString()));
    }
}
