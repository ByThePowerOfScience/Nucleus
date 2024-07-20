package com.redpxnda.nucleus.forge;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.impl.forge.ShaderRegistryImpl;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import static com.redpxnda.nucleus.Nucleus.MOD_ID;

@Mod(MOD_ID)
public class NucleusForge {
    public NucleusForge() {
        Nucleus.init();
    }

    public static class ClientEvents {
        @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
        public static class ModBus {
            @SubscribeEvent
            public static void onShaderRegistration(RegisterShadersEvent event) {
                ShaderRegistryImpl.SHADERS.forEach(pair -> {
                    event.registerShader(pair.getFirst().apply(event.getResourceProvider()), pair.getSecond());
                });
            }
        }
    }
}
