package com.redpxnda.nucleus.pose.forge;

import com.redpxnda.nucleus.pose.NucleusPose;
import net.neoforged.fml.common.Mod;

@Mod(NucleusPose.MOD_ID)
public class NucleusPoseForge {
    public NucleusPoseForge() {
        NucleusPose.init();
    }
}
