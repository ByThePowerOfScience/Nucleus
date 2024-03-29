package com.redpxnda.nucleus.test;

import com.redpxnda.nucleus.registration.RegistryId;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.CompassItem;
import net.minecraft.item.Item;

public class TestRegistries {
    @RegistryId("cool_item")
    public static final CompassItem testItem = new CompassItem(new Item.Settings());

    @RegistryId("cool_block")
    public static final Block coolBlock = new Block(AbstractBlock.Settings.create());
}
