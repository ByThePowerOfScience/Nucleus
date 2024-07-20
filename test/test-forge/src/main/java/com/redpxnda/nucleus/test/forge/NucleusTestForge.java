package com.redpxnda.nucleus.test.forge;

import com.redpxnda.nucleus.test.NucleusTest;
import net.neoforged.fml.common.Mod;

@Mod(NucleusTest.MOD_ID)
public class NucleusTestForge {
    public NucleusTestForge() {
        NucleusTest.init();
    }
}
