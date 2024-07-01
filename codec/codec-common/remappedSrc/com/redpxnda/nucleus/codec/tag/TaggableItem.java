package com.redpxnda.nucleus.codec.tag;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.codec.behavior.CodecBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

@CodecBehavior.Override()
public class TaggableItem extends TaggableEntry<Item> {
    public static final Codec<TaggableItem> CODEC = new TaggableEntryCodec<>(TaggableItem::new, TaggableItem::new, BuiltInRegistries.ITEM, Registries.ITEM);

    public TaggableItem(@NotNull Item object) {
        super(object, BuiltInRegistries.ITEM, Registries.ITEM);
    }

    public TaggableItem(@NotNull TagKey<Item> tag) {
        super(tag, BuiltInRegistries.ITEM, Registries.ITEM);
    }
}
