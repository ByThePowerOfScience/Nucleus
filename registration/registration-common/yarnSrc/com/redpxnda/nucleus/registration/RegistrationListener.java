package com.redpxnda.nucleus.registration;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public interface RegistrationListener {
    Multimap<Object, RegistrationListener> ALL = Multimaps.newMultimap(new ConcurrentHashMap<>(), HashSet::new);

    static void callAllFor(Object registered) {
        if (registered instanceof RegistrationListener rl) rl.registered();
        for (RegistrationListener rl : ALL.get(registered)) {
            rl.registered();
        }
    }

    void registered();
}
