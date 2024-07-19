package com.redpxnda.nucleus.registration.fabric;

import com.redpxnda.nucleus.registration.NucleusRegistration;
import net.fabricmc.api.ModInitializer;

public class NucleusRegistrationFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        NucleusRegistration.init();
    }
}
