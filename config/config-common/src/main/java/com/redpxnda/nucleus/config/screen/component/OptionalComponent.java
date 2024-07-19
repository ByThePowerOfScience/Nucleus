package com.redpxnda.nucleus.config.screen.component;

import com.redpxnda.nucleus.Nucleus;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

import static com.redpxnda.nucleus.config.screen.component.ConfigEntriesComponent.KEY_TEXT_WIDTH;

@Environment(EnvType.CLIENT)
public class OptionalComponent<T> extends AbstractWidget implements ConfigComponent<T> {
    public static final Component ENABLED_TEXT = Component.translatable("nucleus.config_screen.optional.description.enabled");
    public static final Component DISABLED_TEXT = Component.translatable("nucleus.config_screen.optional.description.disabled");
    public static final ResourceLocation BUTTON_TEXTURE = ResourceLocation.fromNamespaceAndPath(Nucleus.MOD_ID, "textures/gui/config/optional.png");

    public final Font textRenderer;
    public ConfigComponent<?> parent;
    public boolean enabled = true;
    public final ConfigComponent<T> child;
    public final Consumer<ConfigComponent<T>> emptyValueSetter;
    public int buttonX = 0;
    public boolean renderInstructions = true;

    public OptionalComponent(Font textRenderer, int x, int y, int width, int height, ConfigComponent<T> child, Consumer<ConfigComponent<T>> emptyValueSetter) {
        super(x, y, width, height, Component.empty());
        this.textRenderer = textRenderer;
        this.child = child;
        child.setParent(this);
        this.emptyValueSetter = emptyValueSetter;
    }

    @Override
    public InlineMode getInlineMode() {
        return child.getInlineMode();
    }

    @Override
    public void onRemoved() {
        child.onRemoved();
    }

    @Override
    public void performPositionUpdate() {
        child.setX(getX());
        child.setY(getY());
        child.performPositionUpdate();
        width = child.getWidth();
        height = child.getHeight();
        buttonX = getInlineMode() == InlineMode.INLINE ? -28 : KEY_TEXT_WIDTH - 66;
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        boolean buttonHovered = mouseX >= getX() + buttonX && mouseX < getX() + buttonX + 20 && mouseY >= getY() && mouseY < getY() + 20;
        context.blit(BUTTON_TEXTURE, getX() + buttonX, getY(), enabled ? 0 : 20, buttonHovered ? 20 : 0, 20, 20, 64, 64);
        if (buttonHovered && renderInstructions)
            context.renderTooltip(textRenderer, textRenderer.split(enabled ? ENABLED_TEXT : DISABLED_TEXT, 150), DefaultTooltipPositioner.INSTANCE, mouseX, mouseY);

        if (enabled) {
            child.render(context, mouseX, mouseY, delta);
            if (parent instanceof ConfigEntriesComponent<?> c) renderInstructions = c.renderInstructions;
            if (renderInstructions) child.drawInstructionText(context, mouseX, mouseY);
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY) || (mouseX >= getX() + buttonX && mouseX < getX() + buttonX + 20 && mouseY >= getY() && mouseY < getY() + 20);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX >= getX() + buttonX && mouseX < getX() + buttonX + 20 && mouseY >= getY() && mouseY < getY() + 20) {
            setEnabled(!enabled);
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            child.setFocused(false);
            return true;
        }
        if (enabled && child.mouseClicked(mouseX, mouseY, button)) {
            child.setFocused(true);
            return true;
        }
        child.setFocused(false);
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return enabled && child.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return enabled && child.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount, double xAmount) {
        return enabled && child.mouseScrolled(mouseX, mouseY, amount, xAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return enabled && child.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return enabled && child.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return enabled && child.charTyped(chr, modifiers);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {

    }

    @Override
    public boolean checkValidity() {
        return !enabled || child.checkValidity();
    }

    @Override
    public T getConfigValue() {
        return enabled ? child.getConfigValue() : null;
    }

    public void setConfigValue(T value) {
        if (value == null) {
            setEnabled(false);
            emptyValueSetter.accept(child);
        } else
            child.setConfigValue(value);
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled && !enabled) onRemoved();
        else if (!this.enabled && enabled) {
            boolean valid = child.checkValidity();
            if (!valid) invalidateChild(child);
        }
        this.enabled = enabled;
    }

    @Override
    public void setParent(ConfigComponent<?> widget) {
        this.parent = widget;
    }

    @Override
    public ConfigComponent<?> getParent() {
        return parent;
    }
}
