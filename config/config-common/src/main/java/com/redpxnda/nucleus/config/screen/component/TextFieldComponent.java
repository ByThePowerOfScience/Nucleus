package com.redpxnda.nucleus.config.screen.component;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class TextFieldComponent extends EditBox implements ConfigComponent<String> {
    public static final Component DESC_TEXT = Component.translatable("nucleus.config_screen.text.description");

    public ConfigComponent<?> widget;

    public TextFieldComponent(Font textRenderer, int x, int y, int width, int height) {
        super(textRenderer, x, y, width, height, Component.empty());
        setMaxLength(1024);
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
    }

    @Override
    public Component getInstructionText() {
        return DESC_TEXT;
    }

    @Override
    public boolean checkValidity() {
        return true;
    }

    @Override
    public String getConfigValue() {
        return getValue();
    }
    public void setConfigValue(String value) {
        super.setValue(value);
    }

    @Override
    public void setParent(ConfigComponent<?> widget) {
        this.widget = widget;
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
}
