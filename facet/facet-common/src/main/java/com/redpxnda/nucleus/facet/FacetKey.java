package com.redpxnda.nucleus.facet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class FacetKey<T extends Facet<?>> {
    private final Identifier id;
    private final Class<T> cls;

    protected FacetKey(Identifier id, Class<T> cls) {
        this.id = id;
        this.cls = cls;
    }

    public @Nullable T get(Entity holder) {
        return getInternal(holder);
    }

    public @Nullable T get(ItemStack holder) {
        return getInternal(holder);
    }

    public @Nullable T get(StatusEffectInstance holder) {
        return getInternal(holder);
    }

    public Optional<T> getOptional(Entity holder) {
        return getOptionalInternal(holder);
    }

    public Optional<T> getOptional(ItemStack holder) {
        return getOptionalInternal(holder);
    }

    public Optional<T> getOptional(StatusEffectInstance holder) {
        return getOptionalInternal(holder);
    }

    private @Nullable T getInternal(Object holder) {
        return FacetHolder.of(holder).getFacets().get(this);
    }

    private Optional<T> getOptionalInternal(Object holder) {
        return FacetHolder.of(holder).getFacets().getOptional(this);
    }

    public Identifier id() {
        return id;
    }

    public Class<T> cls() {
        return cls;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cls);
    }

    @Override
    public String toString() {
        return "FacetKey[" + id + ']';
    }
}
