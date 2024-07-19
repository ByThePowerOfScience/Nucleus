package com.redpxnda.nucleus.registration;

import dev.architectury.registry.CreativeTabRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

import java.util.function.Supplier;

public class ItemGroupCreator {
    public static ItemGroup populate(ItemGroup group, Item... items) {
        RegistrationListener.ALL.put(group, () -> {
            CreativeTabRegistry.appendBuiltin(group, items);
        });
        return group;
    }

    @SafeVarargs
    public static ItemGroup populate(ItemGroup group, Supplier<Item>... items) {
        RegistrationListener.ALL.put(group, () -> {
            CreativeTabRegistry.appendBuiltin(group, items);
        });
        return group;
    }

    public static ItemGroup populate(ItemGroup group, Supplier<Item[]> items) {
        RegistrationListener.ALL.put(group, () -> {
            CreativeTabRegistry.appendBuiltin(group, items.get());
        });
        return group;
    }

    /*public static ItemGroup create(Text title, Supplier<ItemStack> icon) {

    }


    public static ItemGroup create(Consumer<ItemGroup.Builder> callback) {
        return CreativeTabRegistry.create(callback);
    }*/
}
