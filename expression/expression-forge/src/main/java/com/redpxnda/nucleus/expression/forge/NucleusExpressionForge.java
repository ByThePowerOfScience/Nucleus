package com.redpxnda.nucleus.expression.forge;

import com.redpxnda.nucleus.expression.NucleusExpression;
import net.neoforged.fml.common.Mod;

@Mod(NucleusExpression.MOD_ID)
public class NucleusExpressionForge {
    public NucleusExpressionForge() {
        NucleusExpression.init();
    }
}
