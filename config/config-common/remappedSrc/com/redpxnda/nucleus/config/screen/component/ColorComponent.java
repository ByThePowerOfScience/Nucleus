package com.redpxnda.nucleus.config.screen.component;

import com.redpxnda.nucleus.config.screen.widget.colorpicker.ColorPickerWidget;
import com.redpxnda.nucleus.util.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ColorComponent extends AbstractWidget implements ConfigComponent<Color> { // todo color picking
    public static final Component DESC_TEXT = Component.translatable("nucleus.config_screen.color.description");

    public final Font textRenderer;
    public final EditBox textWidget;
    public final ColorPickerWidget picker;
    public ConfigComponent<?> parent;
    public boolean isValid = true;
    public boolean pickerOpen = false;
    public @Nullable Color color;
    protected int oldWidth;

    public ColorComponent(Font textR, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        textRenderer = textR;
        textWidget = new EditBox(textR, x+28, y, width-28, height, Component.empty());
        picker = new ColorPickerWidget(textR, y+28, x, this::setValueNoUpdate);
        oldWidth = width;
    }

    @Override
    public void onRemoved() {
        if (parent != null && !isValid) parent.validateChild(this);
    }

    public void updateValidity() {
        if (parent != null) {
            if (!Color.isValidHexString(textWidget.getValue())) {
                if (isValid) {
                    parent.invalidateChild(this);
                    isValid = false;
                }
                color = null;
            } else {
                color = getValue();
                picker.setColor(color);
                if (!isValid) {
                    parent.validateChild(this);
                    isValid = true;
                }
            }
        }
    }

    @Override
    public void performPositionUpdate() {
        textWidget.setX(getX()+28);
        textWidget.setY(getY());
        picker.setX(getX());
        picker.setY(getY()+28);
    }

    public void togglePicker() {
        pickerOpen = !pickerOpen;
        if (pickerOpen) {
            oldWidth = width;
            if (picker.getWidth() > width) width = picker.getWidth();
            height += picker.getHeight()+4;
        } else {
            width = oldWidth;
            height -= picker.getHeight()+4;
        }
        picker.unfocusTextFields();
        requestPositionUpdate();
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        Color contrastingColor = Color.WHITE;
        if (color != null) {
            contrastingColor = (color.r() + color.g() + color.b() + (color.a()*2))/5 >= 127 ? Color.BLACK : Color.WHITE;

            context.fill(getX()-1, getY()-1, getX()+21, getY(), -6250336);
            context.fill(getX()+20, getY()-1, getX()+21, getY()+21, -6250336);
            context.fill(getX()-1, getY()+20, getX()+21, getY()+21, -6250336);
            context.fill(getX()-1, getY()-1, getX(), getY()+21, -6250336);
            context.fill(getX(), getY(), getX()+20, getY()+20, color.argb());
        }
        int top = getY();
        int bottom = getY() + 20;
        int left = getX();
        int right = getX() + 20;
        String icon = pickerOpen ? "∨" : ">";
        context.drawString(textRenderer, icon, (left+right)/2 - textRenderer.width(icon) / 2, (top + bottom - textRenderer.lineHeight) / 2 + 1, contrastingColor.argb(), false);

        textWidget.renderWidget(context, mouseX, mouseY, delta);

        if (pickerOpen)
            picker.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void drawInstructionText(GuiGraphics context, int mouseX, int mouseY) {
        if (!pickerOpen || (mouseX >= getX() && mouseX <= getX()+getWidth()-picker.getWidth() && mouseY >= getY() && mouseY <= getY()+getHeight()-picker.getHeight()))
            ConfigComponent.super.drawInstructionText(context, mouseX, mouseY);
    }

    @Override
    public Component getInstructionText() {
        return DESC_TEXT;
    }

    @Override
    public boolean checkValidity() {
        return getValue() != null;
    }

    @Override
    public Color getValue() {
        try {
            return new Color(textWidget.getValue());
        } catch (Exception e) {
            return null;
        }
    }
    public void setValue(Color value) {
        setValueNoUpdate(value);
        picker.setColor(value);
    }
    public void setValueNoUpdate(Color value) {
        textWidget.setValue(value.hex());
        color = value;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (pickerOpen && picker.charTyped(chr, modifiers)) return true;
        if (!textWidget.canConsumeInput()) {
            return false;
        }
        if ((chr >= '0' && chr <= '9') || (chr >= 'a' && chr <= 'f') || (chr >= 'A' && chr <= 'F')) {
            textWidget.insertText(Character.toString(chr));
            updateValidity();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (pickerOpen && picker.keyPressed(keyCode, scanCode, modifiers)) return true;
        boolean result = textWidget.keyPressed(keyCode, scanCode, modifiers);
        if (result) updateValidity();
        return result;
    }

    public boolean isMouseOverOpener(double mouseX, double mouseY) {
        return mouseX >= getX() && mouseX < getX() + 20 && mouseY >= getY() && mouseY < getY() + 20;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOverOpener(mouseX, mouseY)) {
            togglePicker();
            return true;
        }
        if (pickerOpen && picker.mouseClicked(mouseX, mouseY, button)) return true;
        picker.unfocusTextFields();
        return textWidget.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (pickerOpen && picker.mouseReleased(mouseX, mouseY, button)) return true;
        return textWidget.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (pickerOpen && picker.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) return true;
        return textWidget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void setParent(ConfigComponent<?> widget) {
        this.parent = widget;
    }

    @Override
    public ConfigComponent<?> getParent() {
        return parent;
    }

    @Override
    public void setFocused(boolean focused) {
        textWidget.setFocused(focused);
        if (!focused) {
            textWidget.setCursorPosition(0);
            textWidget.setHighlightPos(0);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {

    }
}