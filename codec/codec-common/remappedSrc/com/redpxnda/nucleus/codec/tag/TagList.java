package com.redpxnda.nucleus.codec.tag;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public class TagList<T> {
    public static <T, L extends TagList<T>> Codec<L> getCodec(BiFunction<List<T>, List<TagKey<T>>, L> creator, Registry<T> registry, ResourceKey<? extends Registry<T>> registryKey) {
        return new TagListCodec<>(creator, registry, registryKey);
    }

    protected final Registry<T> registry;
    protected final ResourceKey<? extends Registry<T>> registryKey;
    protected final List<T> objects;
    protected final List<TagKey<T>> tags;

    public TagList(List<T> objects, List<TagKey<T>> tags, Registry<T> registry, ResourceKey<? extends Registry<T>> registryKey) {
        this.registry = registry;
        this.registryKey = registryKey;
        this.objects = new ArrayList<>(objects);
        this.tags = new ArrayList<>(tags);
    }

    /**
     * @return true if this contains the object, otherwise false
     */
    public boolean contains(T obj) {
        if (objects.contains(obj)) return true;
        for (TagKey<T> t : tags) {
            if (registry.wrapAsHolder(obj).is(t)) return true;
        }
        return false;
    }

    /**
     * Executes the specified action if the specified object is present
     */
    public void ifPresent(T obj, Consumer<T> action){
        if (contains(obj)) action.accept(obj);
    }

    /**
     * Gets every object involved in this tag list. CACHE THIS! Repeatedly calling this is usually unnecessary, and will be bad for performance.
     */
    public List<T> getAll() {
        List<T> result = new ArrayList<>(objects);
        tags.forEach(t -> {
            var entries = registry.getTag(t);
            entries.ifPresent(registryEntries -> registryEntries.forEach(entry -> result.add(entry.value())));
        });
        return result;
    }

    public List<T> getObjects() {
        return objects;
    }

    public List<TagKey<T>> getTags() {
        return tags;
    }
}
