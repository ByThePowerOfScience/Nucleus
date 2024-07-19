package com.redpxnda.nucleus.config.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class EmptyButtonWidget extends Button {
    public final int hoveredColor;
    public final int textColor;

    public EmptyButtonWidget(int x, int y, int width, int height, Component message, OnPress onPress, int hoveredColor, int textColor) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.hoveredColor = hoveredColor;
        this.textColor = textColor;
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        Minecraft minecraftClient = Minecraft.getInstance();
        this.renderString(context, minecraftClient.font, isHovered() ? hoveredColor : textColor);
    }
}
