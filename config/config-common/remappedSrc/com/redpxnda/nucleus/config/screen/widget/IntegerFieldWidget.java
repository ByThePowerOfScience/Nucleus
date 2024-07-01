package com.redpxnda.nucleus.config.screen.widget;

import com.redpxnda.nucleus.util.Color;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class IntegerFieldWidget extends EditBox {
    public @Nullable String prefix;
    public int value;
    public @Nullable Integer maxValue = null;
    public @Nullable Integer minValue = null;
    protected final Consumer<Integer> onValueUpdate;
    protected final Font textRenderer;

    public IntegerFieldWidget(Font textRenderer, int x, int y, int width, int height, Component message, Consumer<Integer> onValueUpdate) {
        super(textRenderer, x, y, width, height, message);
        this.textRenderer = textRenderer;
        this.onValueUpdate = onValueUpdate;
        setValue(String.valueOf(value));
        setBordered(false);
    }
    public IntegerFieldWidget(Font textRenderer, int x, int y, int width, int height, int minValue, int maxValue, Component message, Consumer<Integer> onValueUpdate, String prefix) {
        this(textRenderer, x, y, width, height, message, onValueUpdate);
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.prefix = prefix;
    }

    public void setValue(int v) {
        if (maxValue != null && v > maxValue) v = maxValue;
        else if (minValue != null && v < minValue) v = minValue;
        value = v;
        setValue(String.valueOf(v));
    }

    public void tryUpdateValue() {
        try {
            value = Integer.parseInt(getValue());
        } catch (Exception e) {
            value = 0;
        }

        if (maxValue != null && value > maxValue) {
            value = maxValue;
            setValue(String.valueOf(value));
        } else if (minValue != null && value < minValue) {
            value = minValue;
            setValue(String.valueOf(value));
        }

        onValueUpdate.accept(value);
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (prefix != null)
            context.drawString(textRenderer, prefix, getX()-textRenderer.width(prefix)-4, getY()-1, Color.WHITE.argb(), true);
        super.renderWidget(context, mouseX, mouseY, delta);
    }

    @Override
    public void insertText(String text) {
        for (int i = 0; i < text.length(); i++) {
            char chr = text.charAt(i);
            if (!(chr >= '0' && chr <= '9')) return;
        }
        super.insertText(text);
        tryUpdateValue();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean result = super.keyPressed(keyCode, scanCode, modifiers);
        if (result) tryUpdateValue();
        return result;
    }
}
