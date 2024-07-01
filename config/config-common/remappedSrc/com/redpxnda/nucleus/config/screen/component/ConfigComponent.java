package com.redpxnda.nucleus.config.screen.component;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public interface ConfigComponent<T> extends Renderable, GuiEventListener, LayoutElement {
    default void drawInstructionText(GuiGraphics context, int mouseX, int mouseY) {
        Component text = getInstructionText();
        if (isMouseOver(mouseX, mouseY) && text != null)
            context.renderTooltip(Minecraft.getInstance().font, Minecraft.getInstance().font.split(text, 150), DefaultTooltipPositioner.INSTANCE, mouseX, mouseY);
    }

    default @Nullable Component getInstructionText() {
        return null;
    }

    boolean checkValidity();

    T getValue();

    void setValue(T value);

    default void onRemoved() {}

    void setParent(ConfigComponent<?> widget);

    ConfigComponent<?> getParent();

    default void invalidateChild(ConfigComponent<?> child) {
        ConfigComponent<?> parent = getParent();
        if (parent != null) {
            parent.invalidateChild(child);
            parent.invalidateChild(this);
        }
    }
    default void validateChild(ConfigComponent<?> child) {
        ConfigComponent<?> parent = getParent();
        if (parent != null) {
            parent.validateChild(child);
            parent.validateChild(this);
        }
    }

    default void performPositionUpdate() {}

    default void requestPositionUpdate() {
        ConfigComponent<?> parent = getParent();
        if (parent != null) parent.requestPositionUpdate();
    }

    boolean isHovered();

    default InlineMode getInlineMode() {
        return InlineMode.INLINE;
    }

    enum InlineMode {
        NONE,
        DRAW_LINE,
        INLINE
    }

    @Override
    default ScreenRectangle getRectangle() {
        return GuiEventListener.super.getRectangle();
    }
}
