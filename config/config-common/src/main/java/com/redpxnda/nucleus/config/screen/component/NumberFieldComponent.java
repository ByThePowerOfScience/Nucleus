package com.redpxnda.nucleus.config.screen.component;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class NumberFieldComponent<N extends Number> extends EditBox implements ConfigComponent<N> {
    public ConfigComponent<?> widget;
    public final NumberParser<N> parser;
    public final boolean allowDecimals;
    public final Font textRenderer;
    public final Component infoText;
    public final String hashtag;
    public final int hashtagWidth;
    public boolean isValid = true;
    public @Nullable N min;
    public @Nullable N max;

    public NumberFieldComponent(Font textRenderer, int x, int y, int width, int height, NumberParser<N> parser, boolean allowDecimals) {
        super(textRenderer, x, y, width, height, Component.empty());
        this.textRenderer = textRenderer;
        this.parser = parser;
        this.allowDecimals = allowDecimals;
        this.infoText = Component.translatable(allowDecimals ? "nucleus.config_screen.double" : "nucleus.config_screen.integer");
        this.hashtag = allowDecimals ? "#.# " : "# ";
        this.hashtagWidth = textRenderer.width(hashtag);

        setBordered(false);
    }

    public NumberFieldComponent(Font textRenderer, int x, int y, int width, int height, NumberParser<N> parser, boolean allowDecimals, N min, N max) {
        this(textRenderer, x, y, width, height, parser, allowDecimals);
        this.min = min;
        this.max = max;
    }

    @Override
    public void onRemoved() {
        if (widget != null && !isValid) widget.validateChild(this);
    }

    public void updateValidity() {
        if (widget != null) {
            if (getValue().isEmpty()) {
                if (isValid) {
                    widget.invalidateChild(this);
                    isValid = false;
                }
            } else {
                if (!isValid) {
                    widget.validateChild(this);
                    isValid = true;
                }
            }
        }
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        context.drawString(textRenderer, hashtag, getX(), getY()+6, isValid ? -11184811 : -43691, true);
        context.pose().pushPose();
        context.pose().translate(hashtagWidth, 6, 0);
        super.renderWidget(context, mouseX, mouseY, delta);
        context.pose().popPose();
    }

    @Override
    public Component getInstructionText() {
        return infoText;
    }

    @Override
    public boolean checkValidity() {
        return getConfigValue() != null;
    }

    public N clamp(N value) {
        if (max != null && value.doubleValue() > max.doubleValue()) value = max;
        else if (min != null && value.doubleValue() < min.doubleValue()) value = min;
        return value;
    }

    @Override
    public N getConfigValue() {
        try {
            return clamp(parser.parse(getValue()));
        } catch (Exception e) {
            return null;
        }
    }
    public void setConfigValue(N value) {
        value = clamp(value);
        setValue(value.toString());
        updateValidity();
    }

    @Override
    public void insertText(String text) {
        for (int i = 0; i < text.length(); i++) {
            char chr = text.charAt(i);
            if (!((chr >= '0' && chr <= '9') || (allowDecimals && chr == '.' && !getValue().contains(".")))) return;
        }
        super.insertText(text);
        updateValidity();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean result = super.keyPressed(keyCode, scanCode, modifiers);
        if (result) updateValidity();
        return result;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount,double xAmount) {
        if (!isMouseOver(mouseX, mouseY)) return false;
        N val = getConfigValue();
        if (val != null) {
            int increment = 0;
            if (Screen.hasShiftDown()) increment+=5;
            if (Screen.hasControlDown()) increment+=10;
            if (increment == 0) increment = 1;

            if (allowDecimals) {
                if (amount > 0 && (max == null || val.doubleValue()+increment <= max.doubleValue())) {
                    double num = val.doubleValue() + increment;
                    setValue(String.valueOf(num));
                } else if (amount < 0 && (min == null || val.doubleValue()-increment >= min.doubleValue())) {
                    double num = val.doubleValue() - increment;
                    setValue(String.valueOf(num));
                }
            } else {
                if (amount > 0 && (max == null || val.intValue()+increment <= max.intValue())) {
                    int num = val.intValue() + increment;
                    setValue(String.valueOf(num));
                } else if (amount < 0 && (min == null || val.intValue()-increment >= min.intValue())) {
                    int num = val.intValue() - increment;
                    setValue(String.valueOf(num));
                }
            }
        }
        return true;
    }

    @Override
    public void setParent(ConfigComponent<?> widget) {
        this.widget = widget;
        updateValidity();
    }

    @Override
    public ConfigComponent<?> getParent() {
        return widget;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            setCursorPosition(0);
            setHighlightPos(0);
        }
    }

    public interface NumberParser<N extends Number> {
        N parse(String str) throws NumberFormatException;
    }
}
