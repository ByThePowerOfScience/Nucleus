package com.redpxnda.nucleus.codec.tag;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.codec.behavior.CodecBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

@CodecBehavior.Override()
public class TaggableEntityType extends TaggableEntry<EntityType<?>> {
    public static final Codec<TaggableEntityType> CODEC = new TaggableEntryCodec<>(TaggableEntityType::new, TaggableEntityType::new, BuiltInRegistries.ENTITY_TYPE, Registries.ENTITY_TYPE);

    public TaggableEntityType(@NotNull EntityType<?> object) {
        super(object, BuiltInRegistries.ENTITY_TYPE, Registries.ENTITY_TYPE);
    }

    public TaggableEntityType(@NotNull TagKey<EntityType<?>> tag) {
        super(tag, BuiltInRegistries.ENTITY_TYPE, Registries.ENTITY_TYPE);
    }
}
