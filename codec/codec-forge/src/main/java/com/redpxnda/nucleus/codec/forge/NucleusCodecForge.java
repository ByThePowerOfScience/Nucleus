package com.redpxnda.nucleus.codec.forge;

import com.redpxnda.nucleus.codec.NucleusCodec;
import net.neoforged.fml.common.Mod;

@Mod(NucleusCodec.MOD_ID)
public class NucleusCodecForge {
    public NucleusCodecForge() {
        NucleusCodec.init();
    }
}
