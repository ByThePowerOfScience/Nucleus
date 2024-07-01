package com.redpxnda.nucleus.codec.tag;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.redpxnda.nucleus.codec.tag.TagList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class TagListCodec<C, L extends TagList<C>> implements Codec<L> {
    protected final BiFunction<List<C>, List<TagKey<C>>, L> creator;
    protected final Registry<C> registry;
    protected final ResourceKey<? extends Registry<C>> registryKey;

    public TagListCodec(BiFunction<List<C>, List<TagKey<C>>, L> creator, Registry<C> registry, ResourceKey<? extends Registry<C>> registryKey) {
        this.creator = creator;
        this.registry = registry;
        this.registryKey = registryKey;
    }

    @Override
    public <T> DataResult<Pair<L, T>> decode(DynamicOps<T> ops, T input) {
        List<C> objects = new ArrayList<>();
        List<TagKey<C>> tags = new ArrayList<>();

        var potentialList = ops.getStream(input);
        if (potentialList.result().isPresent()) {
            Stream<T> stream = potentialList.result().get();
            List<T> failedValues = new ArrayList<>();
            stream.forEach(t -> {
                var potentialStr = ops.getStringValue(t);
                if (potentialStr.result().isPresent()) {
                    String str = potentialStr.result().get();
                    if (str.startsWith("#")) {
                        ResourceLocation id = ResourceLocation.tryParse(str.substring(1));
                        if (id == null) {
                            failedValues.add(t);
                            return;
                        }

                        tags.add(TagKey.create(registryKey, id));
                    } else {
                        ResourceLocation id = ResourceLocation.tryParse(str);
                        if (id == null) {
                            failedValues.add(t);
                            return;
                        }

                        C obj = registry.getOptional(id).orElse(null);
                        if (obj == null) {
                            failedValues.add(t);
                            return;
                        }
                        objects.add(obj);
                    }
                } else failedValues.add(t);
            });

            L result = creator.apply(objects, tags);
            if (!failedValues.isEmpty())
                return DataResult.error(
                        () -> "Could not accept values while decoding tag list: Invalid identifiers found! -> " + failedValues,
                        Pair.of(result, input));
            else
                return DataResult.success(Pair.of(result, input));
        }

        var potentialStr = ops.getStringValue(input);
        if (potentialStr.result().isPresent()) {
            String str = potentialStr.result().get();
            ResourceLocation id;
            L result = null;
            if (str.startsWith("#")) {
                id = ResourceLocation.tryParse(str.substring(1));
                if (id != null) result = creator.apply(List.of(), List.of(TagKey.create(registryKey, id)));
            } else {
                id = ResourceLocation.tryParse(str);
                if (id != null) {
                    C obj = registry.getOptional(id).orElse(null);
                    if (obj != null) result = creator.apply(List.of(obj), List.of());
                }
            }

            if (result == null)
                return DataResult.error(() -> "Could not accept value while decoding tag list: Invalid identifier provided! -> " + str);
            return DataResult.success(Pair.of(result, input));
        }

        return DataResult.error(() -> "Failed to create tag list! Not a list or string: " + input);
    }

    @Override
    public <T> DataResult<T> encode(L input, DynamicOps<T> ops, T prefix) {
        List<T> objects = new ArrayList<>();

        input.getObjects().forEach(c -> {
            ResourceLocation id = registry.getKey(c);
            if (id != null) objects.add(ops.createString(id.toString()));
        });

        input.getTags().forEach(t -> {
            ResourceLocation id = t.location();
            objects.add(ops.createString('#' + id.toString()));
        });

        return DataResult.success(ops.createList(objects.stream()));
    }
}
