package com.redpxnda.nucleus.codec.tag;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class EntityTypeListCodec implements Codec<EntityTypeList> {
    public static final EntityTypeListCodec INSTANCE = new EntityTypeListCodec();

    protected EntityTypeListCodec() {
    }

    @Override
    public <T> DataResult<Pair<EntityTypeList, T>> decode(DynamicOps<T> ops, T input) {
        List<EntityType<?>> objects = new ArrayList<>();
        List<TagKey<EntityType<?>>> tags = new ArrayList<>();
        List<String> builtins = new ArrayList<>();

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

                        tags.add(TagKey.create(Registries.ENTITY_TYPE, id));
                    } else if (str.startsWith("$"))
                        builtins.add(str.substring(1));
                    else {
                        ResourceLocation id = ResourceLocation.tryParse(str);
                        if (id == null) {
                            failedValues.add(t);
                            return;
                        }

                        EntityType<?> obj = BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElse(null);
                        if (obj == null) {
                            failedValues.add(t);
                            return;
                        }
                        objects.add(obj);
                    }
                } else failedValues.add(t);
            });

            EntityTypeList result = new EntityTypeList(objects, tags, builtins);
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
            EntityTypeList result = null;
            if (str.startsWith("#")) {
                id = ResourceLocation.tryParse(str.substring(1));
                if (id != null)
                    result = new EntityTypeList(List.of(), List.of(TagKey.create(Registries.ENTITY_TYPE, id)), List.of());
            } else if (str.startsWith("$"))
                result = new EntityTypeList(List.of(), List.of(), List.of(str.substring(1)));
            else {
                id = ResourceLocation.tryParse(str);
                if (id != null) {
                    EntityType<?> obj = BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElse(null);
                    if (obj != null) result = new EntityTypeList(List.of(obj), List.of(), List.of());
                }
            }

            if (result == null)
                return DataResult.error(() -> "Could not accept value while decoding tag list: Invalid identifier provided! -> " + str);
            return DataResult.success(Pair.of(result, input));
        }

        return DataResult.error(() -> "Failed to create tag list! Not a list or string: " + input);
    }

    @Override
    public <T> DataResult<T> encode(EntityTypeList input, DynamicOps<T> ops, T prefix) {
        List<T> objects = new ArrayList<>();

        input.getObjects().forEach(c -> {
            ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(c);
            objects.add(ops.createString(id.toString()));
        });

        input.getTags().forEach(t -> {
            ResourceLocation id = t.location();
            objects.add(ops.createString('#' + id.toString()));
        });

        input.getBuiltins().forEach(s -> {
            objects.add(ops.createString('$' + s));
        });

        return DataResult.success(ops.createList(objects.stream()));
    }
}
