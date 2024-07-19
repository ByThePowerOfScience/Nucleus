package com.redpxnda.nucleus.util;

import com.mojang.datafixers.util.*;
import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.Nucleus;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ByteBufUtil {
    private static final Logger LOGGER = Nucleus.getLogger();

    public static <B, C, T1, T2, T3, T4, T5, T6, T7> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> streamCodec, final Function<C, T1> getter, final StreamCodec<? super B, T2> streamCodec2, final Function<C, T2> getter2, final StreamCodec<? super B, T3> streamCodec3, final Function<C, T3> getter3, final StreamCodec<? super B, T4> streamCodec4, final Function<C, T4> getter4, final StreamCodec<? super B, T5> streamCodec5, final Function<C, T5> getter5, final StreamCodec<? super B, T6> streamCodec6, final Function<C, T6> getter6, final StreamCodec<? super B, T7> streamCodec7, final Function<C, T7> getter7, final Function7<T1, T2, T3, T4, T5, T6, T7, C> creator) {
        return new StreamCodec<B, C>(){

            @Override
            public C decode(B buffer) {
                T1 object1 = streamCodec.decode(buffer);
                T2 object2 = streamCodec2.decode(buffer);
                T3 object3 = streamCodec3.decode(buffer);
                T4 object4 = streamCodec4.decode(buffer);
                T5 object5 = streamCodec5.decode(buffer);
                T6 object6 = streamCodec6.decode(buffer);
                T7 object7 = streamCodec7.decode(buffer);
                return creator.apply(object1, object2, object3, object4, object5, object6, object7);
            }

            @Override
            public void encode(B buffer, C output) {
                streamCodec.encode(buffer, getter.apply(output));
                streamCodec2.encode(buffer, getter2.apply(output));
                streamCodec3.encode(buffer, getter3.apply(output));
                streamCodec4.encode(buffer, getter4.apply(output));
                streamCodec5.encode(buffer, getter5.apply(output));
                streamCodec6.encode(buffer, getter6.apply(output));
                streamCodec7.encode(buffer, getter7.apply(output));
            }
        };
    }

    public static <B, C, T1, T2, T3, T4, T5, T6, T7, T8> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> streamCodec, final Function<C, T1> getter, final StreamCodec<? super B, T2> streamCodec2, final Function<C, T2> getter2, final StreamCodec<? super B, T3> streamCodec3, final Function<C, T3> getter3, final StreamCodec<? super B, T4> streamCodec4, final Function<C, T4> getter4, final StreamCodec<? super B, T5> streamCodec5, final Function<C, T5> getter5, final StreamCodec<? super B, T6> streamCodec6, final Function<C, T6> getter6, final StreamCodec<? super B, T7> streamCodec7, final Function<C, T7> getter7, final StreamCodec<? super B, T8> streamCodec8, final Function<C, T8> getter8, final Function8<T1, T2, T3, T4, T5, T6, T7, T8, C> creator) {
        return new StreamCodec<B, C>(){

            @Override
            public C decode(B buffer) {
                T1 object1 = streamCodec.decode(buffer);
                T2 object2 = streamCodec2.decode(buffer);
                T3 object3 = streamCodec3.decode(buffer);
                T4 object4 = streamCodec4.decode(buffer);
                T5 object5 = streamCodec5.decode(buffer);
                T6 object6 = streamCodec6.decode(buffer);
                T7 object7 = streamCodec7.decode(buffer);
                T8 object8 = streamCodec8.decode(buffer);
                return creator.apply(object1, object2, object3, object4, object5, object6, object7, object8);
            }

            @Override
            public void encode(B buffer, C output) {
                streamCodec.encode(buffer, getter.apply(output));
                streamCodec2.encode(buffer, getter2.apply(output));
                streamCodec3.encode(buffer, getter3.apply(output));
                streamCodec4.encode(buffer, getter4.apply(output));
                streamCodec5.encode(buffer, getter5.apply(output));
                streamCodec6.encode(buffer, getter6.apply(output));
                streamCodec7.encode(buffer, getter7.apply(output));
                streamCodec8.encode(buffer, getter8.apply(output));
            }
        };
    }

    public static <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> streamCodec, final Function<C, T1> getter, final StreamCodec<? super B, T2> streamCodec2, final Function<C, T2> getter2, final StreamCodec<? super B, T3> streamCodec3, final Function<C, T3> getter3, final StreamCodec<? super B, T4> streamCodec4, final Function<C, T4> getter4, final StreamCodec<? super B, T5> streamCodec5, final Function<C, T5> getter5, final StreamCodec<? super B, T6> streamCodec6, final Function<C, T6> getter6, final StreamCodec<? super B, T7> streamCodec7, final Function<C, T7> getter7, final StreamCodec<? super B, T8> streamCodec8, final Function<C, T8> getter8, final StreamCodec<? super B, T9> streamCodec9, final Function<C, T9> getter9, final Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, C> creator) {
        return new StreamCodec<B, C>(){

            @Override
            public C decode(B buffer) {
                T1 object1 = streamCodec.decode(buffer);
                T2 object2 = streamCodec2.decode(buffer);
                T3 object3 = streamCodec3.decode(buffer);
                T4 object4 = streamCodec4.decode(buffer);
                T5 object5 = streamCodec5.decode(buffer);
                T6 object6 = streamCodec6.decode(buffer);
                T7 object7 = streamCodec7.decode(buffer);
                T8 object8 = streamCodec8.decode(buffer);
                T9 object9 = streamCodec9.decode(buffer);
                return creator.apply(object1, object2, object3, object4, object5, object6, object7, object8, object9);
            }

            @Override
            public void encode(B buffer, C output) {
                streamCodec.encode(buffer, getter.apply(output));
                streamCodec2.encode(buffer, getter2.apply(output));
                streamCodec3.encode(buffer, getter3.apply(output));
                streamCodec4.encode(buffer, getter4.apply(output));
                streamCodec5.encode(buffer, getter5.apply(output));
                streamCodec6.encode(buffer, getter6.apply(output));
                streamCodec7.encode(buffer, getter7.apply(output));
                streamCodec8.encode(buffer, getter8.apply(output));
                streamCodec9.encode(buffer, getter9.apply(output));
            }
        };
    }

    public static <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> streamCodec, final Function<C, T1> getter, final StreamCodec<? super B, T2> streamCodec2, final Function<C, T2> getter2, final StreamCodec<? super B, T3> streamCodec3, final Function<C, T3> getter3, final StreamCodec<? super B, T4> streamCodec4, final Function<C, T4> getter4, final StreamCodec<? super B, T5> streamCodec5, final Function<C, T5> getter5, final StreamCodec<? super B, T6> streamCodec6, final Function<C, T6> getter6, final StreamCodec<? super B, T7> streamCodec7, final Function<C, T7> getter7, final StreamCodec<? super B, T8> streamCodec8, final Function<C, T8> getter8, final StreamCodec<? super B, T9> streamCodec9, final Function<C, T9> getter9, final StreamCodec<? super B, T10> streamCodec10, final Function<C, T10> getter10, final Function10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, C> creator) {
        return new StreamCodec<B, C>(){

            @Override
            public C decode(B buffer) {
                T1 object1 = streamCodec.decode(buffer);
                T2 object2 = streamCodec2.decode(buffer);
                T3 object3 = streamCodec3.decode(buffer);
                T4 object4 = streamCodec4.decode(buffer);
                T5 object5 = streamCodec5.decode(buffer);
                T6 object6 = streamCodec6.decode(buffer);
                T7 object7 = streamCodec7.decode(buffer);
                T8 object8 = streamCodec8.decode(buffer);
                T9 object9 = streamCodec9.decode(buffer);
                T10 object10 = streamCodec10.decode(buffer);
                return creator.apply(object1, object2, object3, object4, object5, object6, object7, object8, object9, object10);
            }

            @Override
            public void encode(B buffer, C output) {
                streamCodec.encode(buffer, getter.apply(output));
                streamCodec2.encode(buffer, getter2.apply(output));
                streamCodec3.encode(buffer, getter3.apply(output));
                streamCodec4.encode(buffer, getter4.apply(output));
                streamCodec5.encode(buffer, getter5.apply(output));
                streamCodec6.encode(buffer, getter6.apply(output));
                streamCodec7.encode(buffer, getter7.apply(output));
                streamCodec8.encode(buffer, getter8.apply(output));
                streamCodec9.encode(buffer, getter9.apply(output));
                streamCodec10.encode(buffer, getter10.apply(output));
            }
        };
    }
}
