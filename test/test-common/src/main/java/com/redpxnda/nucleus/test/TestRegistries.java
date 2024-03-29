package com.redpxnda.nucleus.test;

import com.redpxnda.nucleus.registration.RegistryId;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class TestRegistries {
    @RegistryId("cool_item")
    public static final Item testItem = new Item(new Item.Settings());

    @RegistryId("cool_block")
    public static final Block coolBlock = new Block(AbstractBlock.Settings.create());
}
