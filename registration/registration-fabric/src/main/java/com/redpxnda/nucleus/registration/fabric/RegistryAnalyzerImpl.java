package com.redpxnda.nucleus.registration.fabric;

import com.redpxnda.nucleus.registration.RegistryAnalyzer;
import com.redpxnda.nucleus.registration.RegistryId;
import com.redpxnda.nucleus.util.MiscUtil;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.function.Supplier;

public class RegistryAnalyzerImpl {
    public static void register(String modId, Supplier<Class<?>> holderClass) {
        Class<?> cls = holderClass.get();
        for (Field field : cls.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) continue;
            field.setAccessible(true);

            RegistryId id = field.getAnnotation(RegistryId.class);
            if (id == null) continue;

            Class<?> type = field.getType();
            Registry<?> reg = null;
            for (Map.Entry<Class<?>, Registry<?>> entry : MiscUtil.objectsToRegistries.entrySet()) {
                if (entry.getKey().isAssignableFrom(type)) {
                    reg = entry.getValue();
                    break;
                }
            }
            if (reg == null) {
                RegistryAnalyzer.LOGGER.warn("Failed to find registry type for type '{}' in '{}'!", type.getSimpleName(), cls.getSimpleName());
                continue;
            }

            Identifier identifier = new Identifier(modId, id.value());
            try {
                Registry.register((Registry) reg, identifier, field.get(null));
            } catch (Exception e) {
                RegistryAnalyzer.LOGGER.warn("Failed to register key '" + identifier + "' for registry class '" + cls.getSimpleName() + "'! -> ", e);
            }
        }
    }
}
