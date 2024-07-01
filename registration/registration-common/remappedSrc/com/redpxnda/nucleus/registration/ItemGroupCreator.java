package com.redpxnda.nucleus.registration;

import dev.architectury.registry.CreativeTabRegistry;
import java.util.function.Supplier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class ItemGroupCreator {
    public static CreativeModeTab populate(CreativeModeTab group, Item... items) {
        RegistrationListener.ALL.put(group, () -> {
            CreativeTabRegistry.appendBuiltin(group, items);
        });
        return group;
    }

    @SafeVarargs
    public static CreativeModeTab populate(CreativeModeTab group, Supplier<Item>... items) {
        RegistrationListener.ALL.put(group, () -> {
            CreativeTabRegistry.appendBuiltin(group, items);
        });
        return group;
    }

    public static CreativeModeTab populate(CreativeModeTab group, Supplier<Item[]> items) {
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
