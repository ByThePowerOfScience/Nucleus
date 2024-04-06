package com.redpxnda.nucleus.math;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.redpxnda.nucleus.util.json.JsonUtil;
import net.minecraft.util.dynamic.Codecs;

import java.util.HashMap;
import java.util.Map;

public interface InterpolateMode {
    InterpolateMode NONE = (delta, last, current) -> current;
    InterpolateMode LERP = MathUtil::lerp;
    InterpolateMode COS = (delta, last, current) -> {
        double d = (1 - Math.cos(delta * Math.PI)) / 2;
        return MathUtil.lerp(d, last, current);
    };

    Map<String, Creator> interpolateModes = new HashMap<>();
    Codec<InterpolateMode> codec = Codecs.JSON_ELEMENT.flatComapMap(
            element -> {
                String key;
                if (element instanceof JsonPrimitive primitive)
                    key = primitive.getAsString();
                else if (element instanceof JsonObject object)
                    key = object.getAsJsonPrimitive("type").getAsString();
                else
                    throw new JsonParseException("Failed to get key 'type' from JSON! -> Not a JSON object: " + "'" + element + "'");
                Creator creator = interpolateModes.get(key);
                if (creator == null) throw new JsonParseException("Could not find interpolate mode '" + key + "'! JSON: " + element);
                return creator.createFrom(element);
            },
            mode -> DataResult.error(() -> "Cannot turn InterpolateMode into JsonElement."));

    static void init() {
        interpolateModes.put("none", e -> InterpolateMode.NONE);
        interpolateModes.put("lerp", e -> LERP);
        interpolateModes.put("cosine", e -> COS);
        interpolateModes.put("easeIn", e -> new EaseIn(JsonUtil.getIfObject(e, obj -> JsonUtil.getOrElse(obj, "amplifier", 2f), 2f)));
        interpolateModes.put("easeOut", e -> new EaseOut(JsonUtil.getIfObject(e, obj -> JsonUtil.getOrElse(obj, "amplifier", 2f), 2f)));
        interpolateModes.put("easeInOut", e -> new EaseInOut(JsonUtil.getIfObject(e, obj -> JsonUtil.getOrElse(obj, "amplifier", 2f), 2f)));
    }

    double interpolate(float delta, double last, double current);

    interface Creator {
        InterpolateMode createFrom(JsonElement element);
    }

    record EaseIn(float amplifier) implements InterpolateMode {
        @Override
        public double interpolate(float delta, double last, double current) {
            return MathUtil.lerp(Math.pow(delta, amplifier), last, current);
        }
    }

    record EaseOut(float amplifier) implements InterpolateMode {
        @Override
        public double interpolate(float delta, double last, double current) {
            return MathUtil.lerp(MathUtil.flip(MathUtil.pow(MathUtil.flip(delta), amplifier)), last, current);
        }
    }

    record EaseInOut(float amplifier) implements InterpolateMode {
        @Override
        public double interpolate(float delta, double last, double current) {
            float flippedDelta = MathUtil.flip(MathUtil.pow(MathUtil.flip(delta), amplifier));
            float normalDelta = MathUtil.pow(delta, amplifier);
            float finalDelta = MathUtil.lerp(delta, normalDelta, flippedDelta);
            return MathUtil.lerp(finalDelta, last, current);
        }
    }
}
