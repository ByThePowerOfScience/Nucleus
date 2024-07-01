package com.redpxnda.nucleus.codec.tag;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.codec.behavior.CodecBehavior;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@CodecBehavior.Override()
public class BlockList extends TagList<Block> {
    public static final Codec<BlockList> CODEC = getCodec(BlockList::new, BuiltInRegistries.BLOCK, Registries.BLOCK);

    public static BlockList of() {
        return new BlockList(List.of(), List.of());
    }

    public static BlockList of(Block... blocks) {
        return new BlockList(List.of(blocks), List.of());
    }

    @SafeVarargs
    public static BlockList of(TagKey<Block>... tags) {
        return new BlockList(List.of(), List.of(tags));
    }

    public BlockList(List<Block> objects, List<TagKey<Block>> tags) {
        super(objects, tags, BuiltInRegistries.BLOCK, Registries.BLOCK);
    }

    public boolean contains(BlockState block) {
        return contains(block.getBlock());
    }
}
