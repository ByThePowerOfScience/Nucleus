package com.redpxnda.nucleus.codec.tag;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.codec.behavior.CodecBehavior;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@CodecBehavior.Override()
public class ItemList extends TagList<Item> {
    public static final Codec<ItemList> CODEC = getCodec(ItemList::new, BuiltInRegistries.ITEM, Registries.ITEM);

    public static ItemList of() {
        return new ItemList(List.of(), List.of());
    }

    public static ItemList of(Item... items) {
        return new ItemList(List.of(items), List.of());
    }

    @SafeVarargs
    public static ItemList of(TagKey<Item>... tags) {
        return new ItemList(List.of(), List.of(tags));
    }

    public ItemList(List<Item> objects, List<TagKey<Item>> tags) {
        super(objects, tags, BuiltInRegistries.ITEM, Registries.ITEM);
    }

    public boolean contains(ItemStack stack) {
        return contains(stack.getItem());
    }
}
