package com.redpxnda.nucleus.config.screen.component;

import com.google.common.collect.HashBiMap;
import com.redpxnda.nucleus.codec.tag.TaggableEntry;
import com.redpxnda.nucleus.util.MiscUtil;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

@SuppressWarnings({"unchecked", "rawtypes"})
public class TaggableEntryComponent<E, T extends TaggableEntry<E>> extends AbstractWidget implements ConfigComponent<T> {
    public final TagEntryType tagEntryType = new TagEntryType();
    public final ObjectEntryType objectEntryType = new ObjectEntryType();
    public final Registry<E> registry;
    public final ResourceKey<? extends Registry<E>> registryKey;
    public final HashBiMap<String, EntryType<?>> entryTypes;
    public Entry entry;
    public ConfigComponent<?> parent;
    public ConfigComponent<?> focusedComponent = null;
    public final Component description;
    public final Function<E, T> fromObj;
    public final Function<TagKey<E>, T> fromTag;

    public TaggableEntryComponent(Function<E, T> fromObj, Function<TagKey<E>, T> fromTag, Registry<E> registry, ResourceKey<? extends Registry<E>> registryKey, String typeIdentifier, int x, int y) {
        super(x, y, 142, 8, Component.empty());
        this.fromObj = fromObj;
        this.fromTag = fromTag;
        this.registry = registry;
        this.registryKey = registryKey;

        entryTypes = MiscUtil.initialize(HashBiMap.create(), l -> {
            l.put("tag", tagEntryType);
            l.put(typeIdentifier, objectEntryType);
        });
        entry = new Entry();

        description = Component.translatable("nucleus.config_screen.tag_list.description", typeIdentifier);
    }

    @Override
    public boolean checkValidity() {
        return entry.checkValidity();
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            if (focusedComponent != null) focusedComponent.setFocused(false);
            focusedComponent = null;
        }
    }

    @Override
    public T getConfigValue() {
        return (T) ((EntryType) entry.getType()).createTaggableEntry(entry.getValue());
    }

    @Override
    public void setConfigValue(T value) {
        entry = value.getObject() != null ? new Entry(objectEntryType, value.getObject()) : new Entry(tagEntryType, value.getTag());
        requestPositionUpdate();
    }

    @Override
    public void setParent(ConfigComponent<?> widget) {
        parent = widget;
    }

    @Override
    public ConfigComponent<?> getParent() {
        return parent;
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        entry.dropdown.render(context, mouseX, mouseY, delta);
        if (entry.component != null) entry.component.render(context, mouseX, mouseY, delta);
    }

    @Override
    public @Nullable Component getInstructionText() {
        return description;
    }

    @Override
    public void performPositionUpdate() {
        width = entry.getWidth();
        height = entry.getHeight();
        entry.dropdown.setPosition(getX(), getY());
        if (entry.component != null) entry.component.setPosition(getX() + entry.dropdown.getWidth() + 4, getY());
        entry.performPositionUpdate();
    }

    @Override
    public boolean mouseClicked(double mX, double mY, int button) {
        if (!isMouseOver(mX, mY) && !entry.isMouseOver(mX, mY)) return false;
        if (focusedComponent != null && focusedComponent.mouseClicked(mX, mY, button)) {
            return true;
        } else if (entry.dropdown.isMouseOver(mX, mY)) {
            if (button == 0) {
                if (focusedComponent != null) focusedComponent.setFocused(false);
                entry.dropdown.setFocused(true);
                focusedComponent = entry.dropdown;
            }
            entry.dropdown.mouseClicked(mX, mY, button);
            return true;
        } else if (entry.component != null && entry.component.isMouseOver(mX, mY)) {
            if (button == 0) {
                if (focusedComponent != null) focusedComponent.setFocused(false);
                entry.component.setFocused(true);
                focusedComponent = entry.component;
            }
            entry.component.mouseClicked(mX, mY, button);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mX, double mY, int button) {
        if (focusedComponent != null && focusedComponent.mouseReleased(mX, mY, button))
            return true;
        return super.mouseReleased(mX, mY, button);
    }

    @Override
    public boolean mouseDragged(double mX, double mY, int button, double deltaX, double deltaY) {
        if (focusedComponent != null && focusedComponent.mouseDragged(mX, mY, button, deltaX, deltaY))
            return true;
        return super.mouseDragged(mX, mY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mX, double mY, double amount, double xAmount) {
        if (focusedComponent != null && focusedComponent.mouseScrolled(mX, mY, amount, xAmount))
            return true;
        return super.mouseScrolled(mX, mY, amount, xAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (focusedComponent != null && focusedComponent.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (focusedComponent != null && focusedComponent.keyReleased(keyCode, scanCode, modifiers)) return true;
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (focusedComponent != null && focusedComponent.charTyped(chr, modifiers)) return true;
        return super.charTyped(chr, modifiers);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {

    }

    public abstract class EntryType<A> {
        public abstract ConfigComponent<A> createEntry();

        public abstract T createTaggableEntry(A component);
    }

    public class TagEntryType extends EntryType<TagKey<E>> {
        @Override
        public ConfigComponent<TagKey<E>> createEntry() {
            return new TagKeyComponent<>(registryKey, Minecraft.getInstance().font, 0, 0, 150, 20);
        }

        @Override
        public T createTaggableEntry(TagKey<E> component) {
            return fromTag.apply(component);
        }
    }

    public class ObjectEntryType extends EntryType<E> {
        @Override
        public ConfigComponent<E> createEntry() {
            return new RegistryComponent<>(registry, Minecraft.getInstance().font, 0, 0, 150, 20);
        }

        @Override
        public T createTaggableEntry(E component) {
            return fromObj.apply(component);
        }
    }

    public class Entry {
        public final DropdownComponent<EntryType<?>> dropdown;
        public @Nullable ConfigComponent component;

        public <A> Entry(EntryType<A> type, A val) {
            this();
            dropdown.setConfigValue(type);
            component.setConfigValue(val);
        }

        public Entry() {
            dropdown = new DropdownComponent<>(Minecraft.getInstance().font, 0, 0, 50, 20, entryTypes);
            dropdown.onSet = e -> {
                if (component != null) component.onRemoved();
                component = e.createEntry();
                component.setParent(TaggableEntryComponent.this);
                TaggableEntryComponent.this.requestPositionUpdate();
            };
            dropdown.setParent(TaggableEntryComponent.this);
        }

        public boolean isMouseOver(double mouseX, double mouseY) {
            return dropdown.isMouseOver(mouseX, mouseY) || (dropdown.isOpen && dropdown.dropdown.isMouseOver(mouseX, mouseY)) || (component != null && component.isMouseOver(mouseX, mouseY));
        }

        public int getWidth() {
            int width = dropdown.getWidth();
            return component == null ? width : width + component.getWidth() + 4;
        }

        public int getHeight() {
            int height = dropdown.getHeight();
            if (component != null && component.getHeight() > height) height = component.getHeight();
            return height;
        }

        public void performPositionUpdate() {
            dropdown.performPositionUpdate();
            if (component != null) component.performPositionUpdate();
        }

        public boolean checkValidity() {
            return dropdown.checkValidity() && (component == null || component.checkValidity());
        }

        public EntryType<?> getType() {
            return dropdown.getConfigValue();
        }

        public Object getValue() {
            if (component == null) return null;
            return component.getConfigValue();
        }
    }
}
