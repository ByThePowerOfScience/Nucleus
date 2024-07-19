package com.redpxnda.nucleus.codec.tag;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.codec.behavior.CodecBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

@CodecBehavior.Override()
public class TaggableBlock extends TaggableEntry<Block> {
    public static final Codec<TaggableBlock> CODEC = new TaggableEntryCodec<>(TaggableBlock::new, TaggableBlock::new, BuiltInRegistries.BLOCK, Registries.BLOCK);

    public TaggableBlock(@NotNull Block object) {
        super(object, BuiltInRegistries.BLOCK, Registries.BLOCK);
    }

    public TaggableBlock(@NotNull TagKey<Block> tag) {
        super(tag, BuiltInRegistries.BLOCK, Registries.BLOCK);
    }
}
