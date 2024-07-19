package com.redpxnda.nucleus.config.screen.component;

import com.google.common.collect.HashBiMap;
import com.redpxnda.nucleus.codec.tag.EntityTypeList;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;

public class EntityTypeListComponent extends TagListComponent<EntityType<?>, EntityTypeList> {
    public final PredicateEntryType predicateEntryType = new PredicateEntryType();

    public EntityTypeListComponent(int x, int y) {
        super(EntityTypeList::of, BuiltInRegistries.ENTITY_TYPE, Registries.ENTITY_TYPE, "entity", x, y);
        entryTypes.put("predicate", predicateEntryType);
    }

    @Override
    public void setValue(EntityTypeList value) {
        super.setValue(value);
        value.getBuiltins().forEach(str -> components.add(new Entry(predicateEntryType, str)));
    }

    public class PredicateEntryType extends EntryType<String> {
        @Override
        public ConfigComponent<String> createEntry() {
            return new DropdownComponent<>(Minecraft.getInstance().font, 0, 0, 100, 20,
                    HashBiMap.create(EntityTypeList.builtinPredicates.keySet().stream().collect(Collectors.toMap(v -> v, v -> v))));
        }

        @Override
        public void addToList(EntityTypeList list, String value) {
            list.getBuiltins().add(value);
        }
    }
}
