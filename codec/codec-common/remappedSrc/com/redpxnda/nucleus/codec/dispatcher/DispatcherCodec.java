package com.redpxnda.nucleus.codec.dispatcher;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.redpxnda.nucleus.codec.misc.QuickUtilCodec;

public class DispatcherCodec<A extends Dispatchable> implements QuickUtilCodec<A> {
    public final SimpleDispatcher<A> dispatcher;

    public DispatcherCodec(SimpleDispatcher<A> dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
        return dispatcher.decode(ops, input);
    }

    @Override
    public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
        return dispatcher.encode(input, ops, prefix);
    }
}
