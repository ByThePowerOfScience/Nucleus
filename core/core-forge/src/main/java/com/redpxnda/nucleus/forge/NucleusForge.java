package com.redpxnda.nucleus.forge;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.impl.forge.ShaderRegistryImpl;
import com.redpxnda.nucleus.impl.forge.TrinketItemCreatorImpl;
import dev.architectury.platform.Platform;
import dev.architectury.platform.forge.EventBuses;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static com.redpxnda.nucleus.Nucleus.CORE_MOD_ID;
import static com.redpxnda.nucleus.Nucleus.MOD_ID;

@Mod(CORE_MOD_ID)
public class NucleusForge {
    public NucleusForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(CORE_MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Nucleus.init();

        if (Platform.isModLoaded("curios")) {
            MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, TrinketItemCreatorImpl::attachCuriosCaps);
        }
    }

    public static class ClientEvents {
        @Mod.EventBusSubscriber(modid = CORE_MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
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
