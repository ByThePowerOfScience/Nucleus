package com.redpxnda.nucleus.config.screen.component;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.redpxnda.nucleus.config.screen.widget.EmptyButtonWidget;
import com.redpxnda.nucleus.config.screen.widget.SelectableOptionsWidget;
import com.redpxnda.nucleus.util.Color;
import com.redpxnda.nucleus.util.Comment;
import com.redpxnda.nucleus.util.MiscUtil;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;

public class DropdownComponent<E> extends EditBox implements ConfigComponent<E> { // todo colored when invalid
    public static final Component OPEN_TEXT = Component.literal("∨");
    public static final Component CLOSED_TEXT = Component.literal(">");
    public static final Component DESC_TEXT = Component.translatable("nucleus.config_screen.dropdown.description");

    public ConfigComponent<?> parent;
    public final Font textRenderer;
    public final BiMap<String, E> entries;
    public final Map<String, Component> comments;
    public E selected;
    public boolean isValid = false;
    public boolean isOpen = false;
    public final Button dropdownOpener;
    public final AbstractScrollWidget dropdown;
    public Consumer<E> onSet = (e) -> {
    };

    public DropdownComponent(Font textRenderer, int x, int y, int width, int height, Class<E> enumClass) {
        this(textRenderer, x, y, width, height, MiscUtil.evaluateSupplier(() -> {
            assert enumClass.isEnum() : "Inputted class must be an enum!";
            E[] constants = enumClass.getEnumConstants();
            BiMap<String, E> map = HashBiMap.create();
            Map<String, Component> comments = new HashMap<>();
            for (E constant : constants) {
                String name = ((Enum) constant).name();
                try {
                    Field f = enumClass.getField(name);
                    Comment comment = f.getAnnotation(Comment.class);
                    if (comment != null) comments.put(name, Component.literal(comment.value()));
                } catch (NoSuchFieldException ignored) {
                }
                map.put(name, constant);
            }
            return new Tuple<>(map, comments);
        }));
    }

    public DropdownComponent(Font textRenderer, int x, int y, int width, int height, BiMap<String, E> entries) {
        this(textRenderer, x, y, width, height, new Tuple<>(entries, new HashMap<>()));
    }

    public DropdownComponent(Font textRenderer, int x, int y, int width, int height, Tuple<BiMap<String, E>, Map<String, Component>> entriesAndComments) {
        super(textRenderer, x, y, width, height, Component.empty());
        this.entries = entriesAndComments.getA();
        this.comments = entriesAndComments.getB();
        this.textRenderer = textRenderer;

        this.dropdownOpener = new EmptyButtonWidget(x + width - 20, y, 20, 20, CLOSED_TEXT, wid -> {
            isOpen = !isOpen;
            wid.setMessage(isOpen ? OPEN_TEXT : CLOSED_TEXT);
        }, Color.WHITE.argb(), Color.TEXT_GRAY.argb());
        this.dropdown = getDropdownWidget();

        setEditable(false);
        setTextColorUneditable(Color.WHITE.argb());
    }

    @Override
    public void onRemoved() {
        if (parent != null && !isValid) parent.validateChild(this);
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused && isOpen) dropdownOpener.onPress();
    }

    public void updateValidity() {
        isValid = checkValidity();
        if (parent != null) {
            if (isValid) parent.validateChild(this);
            else parent.invalidateChild(this);
        }
    }

    public AbstractScrollWidget getDropdownWidget() {
        return new SelectableOptionsWidget<>(textRenderer, entries, (op, e) -> {
            setConfigValue(e);
            dropdownOpener.onPress();
        }, getX(), getY(), getWidth(), (textRenderer.lineHeight + 1) * (Math.min(entries.size(), 5)) + 8);
    }

    @Override
    public void drawInstructionText(GuiGraphics context, int mouseX, int mouseY) {
        if (getComment(getValue()) == null && !isOpen)
            ConfigComponent.super.drawInstructionText(context, mouseX, mouseY);
    }

    @Override
    public @Nullable Component getInstructionText() {
        return DESC_TEXT;
    }

    @Override
    public void performPositionUpdate() {
        dropdownOpener.setPosition(getX() + width - 20, getY());
        dropdown.setPosition(getX(), getY());
    }

    @Override
    public void requestPositionUpdate() {
        ConfigComponent.super.requestPositionUpdate();
    }

    @Override
    public InlineMode getInlineMode() {
        return ConfigComponent.super.getInlineMode();
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (isOpen) {
            context.pose().pushPose();
            context.pose().translate(0, 0, 5);
            dropdown.render(context, mouseX, mouseY, delta);
            context.pose().popPose();
        } else {
            dropdownOpener.render(context, mouseX, mouseY, delta);
            super.renderWidget(context, mouseX, mouseY, delta);
            if (isValid && isHovered()) {
                if (!getValue().isEmpty()) {
                    Component text = getComment(getValue());
                    if (text != null)
                        context.renderTooltip(textRenderer, textRenderer.split(text, 150), DefaultTooltipPositioner.INSTANCE, mouseX, mouseY);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isOpen && dropdown.isMouseOver(mouseX, mouseY) && dropdown.mouseClicked(mouseX, mouseY, button))
            return true;
        if (dropdownOpener.isMouseOver(mouseX, mouseY) && dropdownOpener.mouseClicked(mouseX, mouseY, button))
            return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isOpen && dropdown.isMouseOver(mouseX, mouseY) && dropdown.mouseReleased(mouseX, mouseY, button))
            return true;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isOpen && dropdown.isMouseOver(mouseX, mouseY) && dropdown.mouseDragged(mouseX, mouseY, button, deltaX, deltaY))
            return true;
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount, double xAmount) {
        if (isOpen && dropdown.isMouseOver(mouseX, mouseY) && dropdown.mouseScrolled(mouseX, mouseY, amount, xAmount))
            return true;
        return super.mouseScrolled(mouseX, mouseY, amount, xAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isOpen && dropdown.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public @Nullable Component getComment(String key) {
        return comments.get(key);
    }


    public String getKey(E value) {
        return entries.inverse().get(value);
    }

    public E getConfigValue(String key) {
        return entries.get(key);
    }

    @Override
    public boolean checkValidity() {
        return selected != null;
    }

    @Override
    public E getConfigValue() {
        return selected;
    }

    public void setConfigValue(E value) {
        selected = value;
        setValue(getKey(value));
        updateValidity();
        onSet.accept(value);
    }

    @Override
    public void setParent(ConfigComponent<?> widget) {
        parent = widget;
        updateValidity();
    }

    @Override
    public ConfigComponent<?> getParent() {
        return parent;
    }
}
