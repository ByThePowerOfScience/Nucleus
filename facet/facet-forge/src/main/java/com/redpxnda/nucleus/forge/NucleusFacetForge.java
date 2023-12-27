package com.redpxnda.nucleus.forge;

import com.redpxnda.nucleus.NucleusFacet;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(NucleusFacet.MOD_ID)
public class NucleusFacetForge {
    public NucleusFacetForge() {
        EventBuses.registerModEventBus(NucleusFacet.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        NucleusFacet.init();
    }
}
