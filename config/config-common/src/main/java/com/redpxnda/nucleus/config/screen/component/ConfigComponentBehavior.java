package com.redpxnda.nucleus.config.screen.component;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.codec.auto.ConfigAutoCodec;
import com.redpxnda.nucleus.codec.behavior.AnnotationBehaviorGetter;
import com.redpxnda.nucleus.codec.behavior.BehaviorOutline;
import com.redpxnda.nucleus.codec.behavior.CodecBehavior;
import com.redpxnda.nucleus.codec.behavior.TypeBehaviorGetter;
import com.redpxnda.nucleus.codec.tag.BlockList;
import com.redpxnda.nucleus.codec.tag.EntityTypeList;
import com.redpxnda.nucleus.codec.tag.ItemList;
import com.redpxnda.nucleus.config.preset.ConfigPreset;
import com.redpxnda.nucleus.util.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
@SuppressWarnings({"rawtypes", "unchecked"})
public class ConfigComponentBehavior {
    private static final Logger LOGGER = Nucleus.getLogger();
    protected static final BehaviorOutline<Getter<?>> getters = new BehaviorOutline<>(false);

    static {
        registerDynamic((field, cls, raw, params, root) -> {
            if (cls.isArray()) {
                Class compCls = cls.getComponentType();
                return new ArrayComponent(compCls, compCls.isPrimitive(), compCls, 0, 0);
            }
            return null;
        });

        registerDynamic((field, cls, raw, params, root) -> {
            if (Collection.class.isAssignableFrom(cls) && !getUnsafeOutline().statics.containsKey(cls) && params != null) {
                Type elementType = MiscUtil.collectionElement(raw);
                return new CollectionComponent(() -> MiscUtil.createCollection((Class) cls), elementType, 0, 0);
            }
            return null;
        });

        registerDynamic((field, cls, raw, params, root) -> {
            if (Map.class.isAssignableFrom(cls) && !getUnsafeOutline().statics.containsKey(cls) && params != null) {
                Type[] types = MiscUtil.mapElements(raw);
                return new MapComponent(() -> MiscUtil.createMap(raw, (Class) cls), types[0], types[1], 0, 0);
            }
            return null;
        });

        registerDynamic((field, cls, raw, params, root) -> {
            if (cls.isEnum() && !getUnsafeOutline().statics.containsKey(cls))
                return new DropdownComponent(MinecraftClient.getInstance().textRenderer, 0, 0, 150, 20, cls);
            return null;
        });

        registerDynamic((field, cls, raw, params, root) -> {
            if (cls.isAnnotationPresent(ConfigAutoCodec.ConfigClassMarker.class)) {
                Screen screen = MinecraftClient.getInstance().currentScreen;
                return new ConfigEntriesComponent(ConfigAutoCodec.performFieldSearch(cls).entrySet().stream()
                        .map(entry -> Map.entry(entry.getKey(), new Pair<>(entry.getValue(), getComponent(entry.getValue(), true))))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
                        MinecraftClient.getInstance().textRenderer, 0, 0, screen == null ? 200 : screen.width, 20); // height updated later
            }
            return null;
        });

        registerAnnotator(CodecBehavior.Optional.class, (annotation, field, cls, raw, params, isRoot) -> {
            if (isRoot) {
                ConfigComponent<?> child = getComponent(field, cls, raw, params, false);
                return new OptionalComponent(MinecraftClient.getInstance().textRenderer, 0, 0, child.getWidth(), child.getHeight(), child, c -> {});
            }
            return null;
        });

        registerAnnotator(IntegerRange.class, (annotation, field, cls, raw, params, isRoot) -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, Integer::parseInt, false, annotation.min(), annotation.max()));
        registerAnnotator(DoubleRange.class, (annotation, field, cls, raw, params, isRoot) -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, Double::parseDouble, true, annotation.min(), annotation.max()));
        registerAnnotator(FloatRange.class, (annotation, field, cls, raw, params, isRoot) -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, Float::parseFloat, true, annotation.min(), annotation.max()));

        registerClass(String.class, () -> new TextFieldComponent(MinecraftClient.getInstance().textRenderer, 0, 0, 150, 20));
        registerClass(int.class, () -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, Integer::parseInt, false));
        registerClass(Integer.class, () -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, Integer::parseInt, false));
        registerClass(byte.class, () -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, Byte::parseByte, false));
        registerClass(Byte.class, () -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, Byte::parseByte, false));
        registerClass(short.class, () -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, Short::parseShort, false));
        registerClass(Short.class, () -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, Short::parseShort, false));
        registerClass(long.class, () -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, Long::parseLong, false));
        registerClass(Long.class, () -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, Long::parseLong, false));
        registerClass(double.class, () -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, Double::parseDouble, true));
        registerClass(Double.class, () -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, Double::parseDouble, true));
        registerClass(float.class, () -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, Float::parseFloat, true));
        registerClass(Float.class, () -> new NumberFieldComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20, Float::parseFloat, true));
        registerClass(boolean.class, () -> new BooleanComponent(0, 0, 100, 20));
        registerClass(Boolean.class, () -> new BooleanComponent(0, 0, 100, 20));

        registerClass(Color.class, () -> new ColorComponent(MinecraftClient.getInstance().textRenderer, 0, 0, 150, 20));

        registerClass(Identifier.class, () -> new IdentifierComponent(MinecraftClient.getInstance().textRenderer, 0, 0, 150, 20));

        registerClass(TagKey.class, (field, cls, raw, params, root) -> {
            if (params == null) return null;
            Type t = params[0];
            Class c;
            if (t instanceof Class<?> cs) c = cs;
            else if (t instanceof ParameterizedType pt && pt.getRawType() instanceof Class<?> cs) c = cs;
            else return null;
            Registry reg = MiscUtil.objectsToRegistries.get(c);
            if (reg == null) return null;
            return new TagKeyComponent(reg.getKey(), MinecraftClient.getInstance().textRenderer, 0, 0, 150, 20);
        });

        registerClass(ConfigPreset.class, (field, cls, raw, params, root) -> {
            if (params == null || !(params[1] instanceof Class<?> clazz)) return null;
            return new PresetComponent(MinecraftClient.getInstance().textRenderer, 0, 0, 150, 20, clazz);
        }); // todo do the rest (all autocodec ones,, TAG LISTS)

        registerClass(ItemList.class, () -> new TagListComponent<>(ItemList::of, Registries.ITEM, RegistryKeys.ITEM, "item", 0, 0));
        registerClass(BlockList.class, () -> new TagListComponent<>(BlockList::of, Registries.BLOCK, RegistryKeys.BLOCK, "block", 0, 0));
        registerClass(EntityTypeList.class, () -> new EntityTypeListComponent(0, 0));

        /*registerClass(ParticleEffect.class, ParticleTypes.TYPE_CODEC);*/ // tod-o dispatches(?)
        MiscUtil.objectsToRegistries.forEach((k, v) -> registerClassIfAbsent(k, (f, cls, raw, params, isRoot) -> new RegistryComponent(v, MinecraftClient.getInstance().textRenderer, 0, 0, 150, 20)));
    }

    public static <T> ConfigComponent<T> getComponent(Type type, boolean isRoot) {
        ConfigComponent<T> comp = (ConfigComponent<T>) getters.get(type, isRoot);
        if (comp != null) return comp;
        Codec codec = CodecBehavior.getCodec(type, true);
        if (codec == null) throw new RuntimeException("Type '" + type + "' has no known codec or config component behavior!");
        return new RawJsonComponent(codec, 0, 0, 20, 20);
    }

    public static <T> ConfigComponent<T> getComponent(Field field, boolean isRoot) {
        ConfigComponent<T> comp = (ConfigComponent<T>) getters.get(field, isRoot);
        if (comp != null) return comp;
        Codec codec = CodecBehavior.getCodec(field, true);
        if (codec == null) throw new RuntimeException("Field '" + field + "' has no known codec or config component behavior!");
        return new RawJsonComponent(codec, 0, 0, 20, 20);
    }

    /**
     * @param field the field to be used as reference... used for things like annotations
     * @param cls the class of the field, or the class of the codec if no field
     * @param raw the raw "generic type" of the field, or the class if no field
     * @param params the type params of the field, or null if no field
     * @return a codec for this field/class
     */
    public static <T> ConfigComponent<T> getComponent(@Nullable Field field, Class<T> cls, Type raw, @Nullable Type[] params, boolean isRoot) {
        ConfigComponent<T> comp = (ConfigComponent<T>) getters.get(field, cls, raw, params, isRoot);
        if (comp != null) return comp;
        Codec codec = CodecBehavior.getCodec(field, cls, raw, params, true);
        if (codec == null) throw new RuntimeException("Field '" + field + "'(type: '" + raw + "') has no known codec or config component behavior!");
        return new RawJsonComponent(codec, 0, 0, 20, 20);
    }

    public static BehaviorOutline<Getter<?>> getUnsafeOutline() {
        return getters;
    }

    public static <T> void registerClass(Class<T> cls, Getter<T> getter) {
        getters.statics.put(cls, getter);
    }
    public static <T> void registerClassIfAbsent(Class<T> cls, Getter<T> getter) {
        getters.statics.putIfAbsent(cls, getter);
    }
    public static <T> void registerClass(Class<T> cls, Supplier<ConfigComponent<T>> getter) {
        getters.statics.put(cls, Getter.fromSupplier(getter));
    }

    public static <A extends Annotation> void registerAnnotator(Class<A> cls, float prio, AnnotationGetter<A> getter) {
        getters.dynamics.put((field, c, raw, params, isRoot) -> {
            if (field != null) {
                A annot = field.getAnnotation(cls);
                if (annot != null) return (ConfigComponent) getter.get(annot, field, c, raw, params, isRoot);
            }
            return null;
        }, prio);
    }

    public static <A extends Annotation> void registerAnnotator(Class<A> cls, AnnotationGetter<A> getter) {
        registerAnnotator(cls, 0f, getter);
    }

    public static void registerDynamic(float prio, Getter<?> getter) {
        getters.dynamics.put(getter, prio);
    }

    public static void registerDynamic(Getter<?> getter) {
        registerDynamic(0f, getter);
    }

    public interface AnnotationGetter<A extends Annotation> extends AnnotationBehaviorGetter<A, ConfigComponent<?>> {}

    public interface Getter<T> extends TypeBehaviorGetter<ConfigComponent<T>, T> {
        static <T> Getter<T> fromSupplier(Supplier<ConfigComponent<T>> supplier) {
            return (field, cls, raw, params, root) -> supplier.get();
        }
    }
}
