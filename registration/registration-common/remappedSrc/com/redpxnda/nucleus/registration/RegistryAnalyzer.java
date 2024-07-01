package com.redpxnda.nucleus.registration;

import com.redpxnda.nucleus.Nucleus;
import dev.architectury.injectables.annotations.ExpectPlatform;
import org.slf4j.Logger;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class RegistryAnalyzer {
    public static final Logger LOGGER = Nucleus.getLogger();

    @ExpectPlatform
    public static void register(String modId, Supplier<Class<?>> holderClass) {
        throw new AssertionError("This should never be called.");
    }

    @ExpectPlatform
    public static void register(String modId, Supplier<Class<?>> holderClass, Consumer<Object> finishListener) {
        throw new AssertionError("This should never be called.");
    }
}
