package com.redpxnda.nucleus.registration.forge;

import com.redpxnda.nucleus.registration.NucleusRegistration;
import com.redpxnda.nucleus.registration.RegistrationListener;
import dev.architectury.platform.forge.EventBuses;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod(NucleusRegistration.MOD_ID)
public class NucleusRegistrationForge {
    public NucleusRegistrationForge() {
        EventBuses.registerModEventBus(NucleusRegistration.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        NucleusRegistration.init();

        FMLJavaModLoadingContext.get().getModEventBus().register(NucleusRegistrationForge.class);
    }

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        for (Map.Entry<String, Pair<Supplier<Map<RegistryKey<?>, Map<Identifier, Object>>>, Consumer<Object>>> entry : RegistryAnalyzerImpl.registrationListeners.entries()) {
            var supplier = entry.getValue().getLeft();
            Consumer<Object> finishListener = entry.getValue().getRight();
            Map<RegistryKey<?>, Map<Identifier, Object>> map = supplier.get();

            RegistryKey key = event.getRegistryKey();
            Map<Identifier, Object> objects = map.get(key);
            if (objects != null)
                objects.forEach((id, obj) -> {
                    event.register(key, id, () -> obj);
                    RegistrationListener.callAllFor(obj);
                });
        }
    }
}
