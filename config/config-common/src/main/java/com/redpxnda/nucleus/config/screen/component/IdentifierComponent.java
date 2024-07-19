package com.redpxnda.nucleus.config.screen.component;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class IdentifierComponent extends EditBox implements ConfigComponent<ResourceLocation> {
    public static final Component DESC_TEXT = Component.translatable("nucleus.config_screen.identifier.description");

    public ConfigComponent<?> parent;
    public final Font textRenderer;
    public boolean isValid = true;

    public IdentifierComponent(Font textRenderer, int x, int y, int width, int height) {
        super(textRenderer, x, y, width, height, Component.empty());
        this.textRenderer = textRenderer;
        setMaxLength(1024);
    }

    @Override
    public void onRemoved() {
        if (parent != null && !isValid) parent.validateChild(this);
    }

    public void updateValidity() {
        if (parent != null) {
            if (getValue().isEmpty()) {
                if (isValid) {
                    parent.invalidateChild(this);
                    isValid = false;
                }
            } else {
                if (!isValid) {
                    parent.validateChild(this);
                    isValid = true;
                }
            }
        }
    }

    @Override
    public Component getInstructionText() {
        return DESC_TEXT;
    }

    @Override
    public boolean checkValidity() {
        return getConfigValue() != null;
    }

    @Override
    public ResourceLocation getConfigValue() {
        return ResourceLocation.tryParse(getValue());
    }
    public void setConfigValue(ResourceLocation value) {
        setValue(value.toString());
        updateValidity();
    }

    @Override
    public void insertText(String text) {
        String old = getValue();
        super.insertText(text);
        setValue(old);
        updateValidity();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean result = super.keyPressed(keyCode, scanCode, modifiers);
        if (result) updateValidity();
        return result;
    }

    @Override
    public void setParent(ConfigComponent<?> widget) {
        this.parent = widget;
        updateValidity();
    }

    @Override
    public ConfigComponent<?> getParent() {
        return parent;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            setCursorPosition(0);
            setHighlightPos(0);
        }
    }
}
