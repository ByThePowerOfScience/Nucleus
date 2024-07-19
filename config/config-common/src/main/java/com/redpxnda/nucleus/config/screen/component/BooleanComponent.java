package com.redpxnda.nucleus.config.screen.component;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class BooleanComponent extends Button implements ConfigComponent<Boolean> {
    public static final Component DESC_TEXT = Component.translatable("nucleus.config_screen.boolean.description");
    public static final Component ON_TEXT = Component.translatable("nucleus.config_screen.boolean.on");
    public static final Component OFF_TEXT = Component.translatable("nucleus.config_screen.boolean.off");

    public ConfigComponent<?> widget;
    public boolean checked = false;

    public BooleanComponent(int x, int y, int width, int height) {
        super(x, y, width, height, OFF_TEXT, wid -> {}, DEFAULT_NARRATION);
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        setFocused(isHovered());
    }

    @Override
    public Component getInstructionText() {
        return DESC_TEXT;
    }

    @Override
    public void onPress() {
        checked = !checked;
        setMessage(checked ? ON_TEXT : OFF_TEXT);
    }

    @Override
    public boolean checkValidity() {
        return true;
    }

    @Override
    public Boolean getConfigValue() {
        return checked;
    }

    public void setConfigValue(Boolean value) {
        checked = value;
        setMessage(checked ? ON_TEXT : OFF_TEXT);
    }

    @Override
    public void setParent(ConfigComponent<?> widget) {
        this.widget = widget;
    }

    @Override
    public ConfigComponent<?> getParent() {
        return widget;
    }
}
