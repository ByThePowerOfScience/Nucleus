package com.redpxnda.nucleus.facet;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class FacetKey<T extends Facet<?>> {
    private final ResourceLocation id;
    private final Class<T> cls;

    protected FacetKey(ResourceLocation id, Class<T> cls) {
        this.id = id;
        this.cls = cls;
    }

    public @Nullable T get(Entity holder) {
        return getInternal(holder);
    }

    public @Nullable T get(ItemStack holder) {
        return getInternal(holder);
    }

    public @Nullable T get(MobEffectInstance holder) {
        return getInternal(holder);
    }

    public Optional<T> getOptional(Entity holder) {
        return getOptionalInternal(holder);
    }

    public Optional<T> getOptional(ItemStack holder) {
        return getOptionalInternal(holder);
    }

    public Optional<T> getOptional(MobEffectInstance holder) {
        return getOptionalInternal(holder);
    }

    private @Nullable T getInternal(Object holder) {
        return FacetHolder.of(holder).getFacets().get(this);
    }

    private Optional<T> getOptionalInternal(Object holder) {
        return FacetHolder.of(holder).getFacets().getOptional(this);
    }

    public ResourceLocation id() {
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
