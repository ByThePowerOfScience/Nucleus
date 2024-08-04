package com.redpxnda.nucleus.codec.tag;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TaggableEntry<T> {
    protected final @Nullable T object;
    protected final @Nullable TagKey<T> tag;
    protected final Registry<T> registry;
    protected final ResourceKey<? extends Registry<T>> registryKey;

    public TaggableEntry(@NotNull T object, Registry<T> registry, ResourceKey<? extends Registry<T>> registryKey) {
        this.object = object;
        this.registry = registry;
        this.registryKey = registryKey;
        this.tag = null;
    }

    public TaggableEntry(@NotNull TagKey<T> tag, Registry<T> registry, ResourceKey<? extends Registry<T>> registryKey) {
        this.tag = tag;
        this.registry = registry;
        this.registryKey = registryKey;
        this.object = null;
    }

    public Either<T, TagKey<T>> getAsEither() {
        return object != null ? Either.left(object) : Either.right(tag);
    }

    public @Nullable T getObject() {
        return object;
    }

    public @Nullable TagKey<T> getTag() {
        return tag;
    }

    public boolean matches(@NotNull T obj) {
        return obj.equals(object) || (tag != null && registry.wrapAsHolder(obj).is(tag));
    }
}