package com.redpxnda.nucleus.config.forge;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.config.ConfigManager;
import com.redpxnda.nucleus.config.ConfigScreensEvent;
import com.redpxnda.nucleus.config.NucleusConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;

@Mod(NucleusConfig.MOD_ID)
public class NucleusConfigForge {
    private static final Logger LOGGER = Nucleus.getLogger();

    public NucleusConfigForge() {
        NucleusConfig.init();
    }

    @OnlyIn(Dist.CLIENT)
    @EventBusSubscriber(modid = NucleusConfig.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ConfigScreensEvent.ScreenRegisterer registerer = new ConfigScreensEvent.ScreenRegisterer();
                ConfigManager.CONFIG_SCREENS_REGISTRY.invoker().add(registerer);
                registerer.getAllScreenFactories().forEach((id, factory) -> {
                    var optional = ModList.get().getModContainerById(id);
                    if (optional.isPresent())
                        optional.get().registerExtensionPoint(IConfigScreenFactory.class, (c, s) -> factory.apply(s));
                    else
                        LOGGER.warn("Could not find mod '{}' to register config screen.", id);
                });
            });
        }
    }
}
