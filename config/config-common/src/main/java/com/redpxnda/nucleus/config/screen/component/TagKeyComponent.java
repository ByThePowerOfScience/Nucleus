package com.redpxnda.nucleus.config.screen.component;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class TagKeyComponent<T> extends AbstractWidget implements ConfigComponent<TagKey<T>> {
    public static final Component DESC_TEXT = Component.translatable("nucleus.config_screen.tag.description");

    public final IdentifierComponent delegate;
    public final ResourceKey<? extends Registry<T>> registry;
    public ConfigComponent<?> parent;

    public TagKeyComponent(ResourceKey<? extends Registry<T>> registry, Font textRenderer, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.registry = registry;
        this.delegate = new IdentifierComponent(textRenderer, x, y, width, height);
    }

    @Override
    public void onRemoved() {
        delegate.onRemoved();
    }

    @Override
    public boolean checkValidity() {
        return delegate.checkValidity();
    }

    @Override
    public TagKey<T> getValue() {
        ResourceLocation val = delegate.getValue();
        return val == null ? null : TagKey.create(registry, val);
    }

    @Override
    public void performPositionUpdate() {
        delegate.setX(getX());
        delegate.setY(getY());
    }

    @Override
    public void setValue(TagKey<T> value) {
        delegate.setValue(value.location());
    }

    @Override
    public Component getInstructionText() {
        return DESC_TEXT;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return delegate.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return delegate.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return delegate.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return delegate.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return delegate.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return delegate.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return delegate.charTyped(chr, modifiers);
    }

    @Override
    public void setParent(ConfigComponent<?> widget) {
        parent = widget;
        delegate.updateValidity();
        delegate.setParent(this);
    }

    @Override
    public void setFocused(boolean focused) {
        delegate.setFocused(focused);
        if (!focused) {
            delegate.setCursorPosition(0);
            delegate.setHighlightPos(0);
        }
    }

    @Override
    public ConfigComponent<?> getParent() {
        return parent;
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        delegate.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {

    }
}
