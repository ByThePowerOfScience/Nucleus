package com.redpxnda.nucleus.test;

import com.redpxnda.nucleus.registration.ItemGroupCreator;
import com.redpxnda.nucleus.registration.RegistryId;
import dev.architectury.registry.CreativeTabRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.CompassItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class TestRegistries {
    @RegistryId("cool_item")
    public static final CompassItem testItem = new CompassItem(new Item.Settings());

    @RegistryId("cool_block")
    public static final Block coolBlock = new Block(AbstractBlock.Settings.create());

    @RegistryId("cool_tab")
    public static final ItemGroup group = ItemGroupCreator.populate(
            CreativeTabRegistry.create(Text.literal("YOOO WASSGOOD"), () -> Items.STICK.getDefaultStack()),
            testItem
    );
}
