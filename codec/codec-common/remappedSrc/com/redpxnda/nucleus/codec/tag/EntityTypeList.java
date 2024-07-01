package com.redpxnda.nucleus.codec.tag;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.codec.behavior.CodecBehavior;
import com.redpxnda.nucleus.util.MiscUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.trading.Merchant;

@CodecBehavior.Override()
public class EntityTypeList extends TagList<EntityType<?>> {
    public static final Codec<EntityTypeList> CODEC = EntityTypeListCodec.INSTANCE;
    public static final Map<String, Predicate<Entity>> builtinPredicates = MiscUtil.initialize(new HashMap<>(), m -> {
        m.put("tamables", e -> e instanceof OwnableEntity);
        m.put("animals", e -> e instanceof Animal);
        m.put("merchants", e -> e instanceof Merchant);
        m.put("mobs", e -> e instanceof Mob);
        m.put("chested_horses", e -> e instanceof AbstractChestedHorse);
        m.put("horse_likes", e -> e instanceof AbstractHorse);
    });

    public static EntityTypeList of() {
        return new EntityTypeList(List.of(), List.of(), List.of());
    }

    public static EntityTypeList of(EntityType<?>... entities) {
        return new EntityTypeList(List.of(entities), List.of(), List.of());
    }

    @SafeVarargs
    public static EntityTypeList of(TagKey<EntityType<?>>... tags) {
        return new EntityTypeList(List.of(), List.of(tags), List.of());
    }

    protected final List<String> builtins;

    public EntityTypeList(List<EntityType<?>> objects, List<TagKey<EntityType<?>>> tags, List<String> builtins) {
        super(objects, tags, BuiltInRegistries.ENTITY_TYPE, Registries.ENTITY_TYPE);
        this.builtins = new ArrayList<>(builtins);
    }

    public boolean contains(Entity obj) {
        if (super.contains(obj.getType())) return true;
        for (String builtin : builtins) {
            Predicate<Entity> predicate = builtinPredicates.get(builtin);
            if (predicate != null && predicate.test(obj)) return true;
        }
        return false;
    }

    public List<String> getBuiltins() {
        return builtins;
    }
}
