package com.redpxnda.nucleus.config.screen.component;

import com.google.common.collect.HashBiMap;
import com.redpxnda.nucleus.codec.tag.TagList;
import com.redpxnda.nucleus.util.MiscUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import static com.redpxnda.nucleus.config.screen.component.ConfigEntriesComponent.KEY_TEXT_WIDTH;

@SuppressWarnings({"unchecked", "rawtypes"})
public class TagListComponent<E, T extends TagList<E>> extends AbstractWidget implements ConfigComponent<T> {
    public static final Component UP_ICON = Component.literal("∧");
    public static final Component DOWN_ICON = Component.literal("∨");
    public static final Component REMOVE_ICON = Component.literal("×");

    public final TagEntryType tagEntryType = new TagEntryType();
    public final ObjectEntryType objectEntryType = new ObjectEntryType();
    public final Supplier<T> creator;
    public final Registry<E> registry;
    public final ResourceKey<? extends Registry<E>> registryKey;
    public final HashBiMap<String, EntryType<?>> entryTypes;
    public final List<Entry> components = new ArrayList<>();
    public final Button adder;
    public final Button minimizer;
    public ConfigComponent<?> parent;
    public boolean minimized = true;
    public ConfigComponent<?> focusedComponent = null;
    public final Component description;

    public TagListComponent(Supplier<T> creator, Registry<E> registry, ResourceKey<? extends Registry<E>> registryKey, String typeIdentifier, int x, int y) {
        super(x, y, 142, 8, Component.empty());
        this.registry = registry;
        this.registryKey = registryKey;
        this.creator = creator;

        adder = Button.builder(Component.literal("＋"), wid -> {
            components.add(new Entry());
            requestPositionUpdate();
        }).bounds(0, 0, 20, 20).build();

        Component minimizedText = Component.literal(">");
        Component maximizedText = Component.literal("∨");
        minimizer = Button.builder(minimizedText, wid -> {
            minimized = !minimized;
            if (minimized) focusedComponent = null;
            wid.setMessage(minimized ? minimizedText : maximizedText);
            requestPositionUpdate();
        }).bounds(0, 0, 20, 20).build();

        entryTypes = MiscUtil.initialize(HashBiMap.create(), l -> {
            l.put("tag", tagEntryType);
            l.put(typeIdentifier, objectEntryType);
        });

        description = Component.translatable("nucleus.config_screen.tag_list.description", typeIdentifier);
    }

    @Override
    public boolean checkValidity() {
        for (Entry e : components)
            if (!e.checkValidity()) return false;
        return true;
    }

    @Override
    public void onRemoved() {
        if (!minimized)
            minimizer.onPress();
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
    public T getValue() {
        T list = creator.get();
        components.forEach(e -> {
            Object obj = e.getValue();
            if (obj != null)
                ((EntryType) e.getType()).addToList(list, obj);
        });
        return list;
    }

    @Override
    public void setValue(T value) {
        components.clear();
        value.getObjects().forEach(obj -> components.add(new Entry(objectEntryType, obj)));
        value.getTags().forEach(tag -> components.add(new Entry(tagEntryType, tag)));
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
        minimizer.render(context, mouseX, mouseY, delta);
        if (!minimized) {
            int index = 0;
            for (Entry entry : components) {
                entry.dropdown.render(context, mouseX, mouseY, delta);
                if (entry.component != null) entry.component.render(context, mouseX, mouseY, delta);

                Button remover = entry.remover;
                if (Screen.hasShiftDown()) {
                    remover.setMessage(DOWN_ICON);
                    remover.active = index != components.size()-1;
                } else if (Screen.hasControlDown()) {
                    remover.setMessage(UP_ICON);
                    remover.active = index != 0;
                } else {
                    remover.setMessage(REMOVE_ICON);
                    remover.active = true;
                }
                remover.render(context, mouseX, mouseY, delta);
                index++;
            }
            adder.render(context, mouseX, mouseY, delta);
        }
    }

    @Override
    public InlineMode getInlineMode() {
        return minimized ? InlineMode.NONE : InlineMode.DRAW_LINE;
    }

    @Override
    public void drawInstructionText(GuiGraphics context, int mouseX, int mouseY) {
        if (minimizer.isMouseOver(mouseX, mouseY))
            ConfigComponent.super.drawInstructionText(context, mouseX, mouseY);
    }

    @Override
    public @Nullable Component getInstructionText() {
        return description;
    }

    @Override
    public void performPositionUpdate() {
        minimizer.setPosition(getX()+KEY_TEXT_WIDTH-38, getY());
        width = KEY_TEXT_WIDTH-18;
        height = 20;
        if (!minimized) {
            components.forEach(entry -> {
                height += 8;
                entry.dropdown.setPosition(getX() + 8, getY() + height);
                if (entry.component != null) entry.component.setPosition(getX() + entry.dropdown.getWidth() + 12, getY() + height);
                if (entry.getWidth() + 28 > width) width = entry.getWidth() + 28;
                height += entry.getHeight();
                entry.remover.setPosition(entry.dropdown.getX() + entry.getWidth() + 8, entry.dropdown.getY());
                entry.performPositionUpdate();
            });
            height += 8;
            adder.setPosition(getX() + 8, getY() + height);
            height += 20;
        }
    }

    @Override
    public boolean mouseClicked(double mX, double mY, int button) {
        //if (!isMouseOver(mX, mY)) return false;
        if (minimizer.isMouseOver(mX, mY)) return minimizer.mouseClicked(mX, mY, button);
        if (!minimized) {
            if (adder.isMouseOver(mX, mY)) return adder.mouseClicked(mX, mY, button);
            for (Entry entry : components) {
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
                } else if (entry.remover.isMouseOver(mX, mY) && entry.remover.mouseClicked(mX, mY, button))
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mX, double mY, int button) {
        if (!minimized && focusedComponent != null && focusedComponent.mouseReleased(mX, mY, button))
            return true;
        return super.mouseReleased(mX, mY, button);
    }

    @Override
    public boolean mouseDragged(double mX, double mY, int button, double deltaX, double deltaY) {
        if (!minimized && focusedComponent != null && focusedComponent.mouseDragged(mX, mY, button, deltaX, deltaY))
            return true;
        return super.mouseDragged(mX, mY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mX, double mY, double amount) {
        if (!minimized && focusedComponent != null && focusedComponent.mouseScrolled(mX, mY, amount))
            return true;
        return super.mouseScrolled(mX, mY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!minimized && focusedComponent != null && focusedComponent.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (!minimized && focusedComponent != null && focusedComponent.keyReleased(keyCode, scanCode, modifiers)) return true;
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!minimized && focusedComponent != null && focusedComponent.charTyped(chr, modifiers)) return true;
        return super.charTyped(chr, modifiers);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {

    }

    public abstract class EntryType<A> {
        public abstract ConfigComponent<A> createEntry();
        public abstract void addToList(T list, A value);
    }

    public class TagEntryType extends EntryType<TagKey<E>> {
        @Override
        public ConfigComponent<TagKey<E>> createEntry() {
            return new TagKeyComponent<>(registryKey, Minecraft.getInstance().font, 0, 0, 150, 20);
        }

        @Override
        public void addToList(T list, TagKey<E> value) {
            list.getTags().add(value);
        }
    }

    public class ObjectEntryType extends EntryType<E> {
        @Override
        public ConfigComponent<E> createEntry() {
            return new RegistryComponent<>(registry, Minecraft.getInstance().font, 0, 0, 150, 20);
        }

        @Override
        public void addToList(T list, E value) {
            list.getObjects().add(value);
        }
    }

    public class Entry {
        public final DropdownComponent<EntryType<?>> dropdown;
        public @Nullable ConfigComponent component;
        public final Button remover;

        public <A> Entry(EntryType<A> type, A val) {
            this();
            dropdown.setValue(type);
            component.setValue(val);
        }

        public Entry() {
            dropdown = new DropdownComponent<>(Minecraft.getInstance().font, 0, 0, 80, 20, entryTypes);
            dropdown.onSet = e -> {
                if (component != null) component.onRemoved();
                component = e.createEntry();
                component.setParent(TagListComponent.this);
                TagListComponent.this.requestPositionUpdate();
            };
            dropdown.setParent(TagListComponent.this);

            remover = Button.builder(REMOVE_ICON, wid -> {
                if (focusedComponent != null) focusedComponent.setFocused(false);

                if (Screen.hasShiftDown())
                    MiscUtil.moveListElementDown(components, this);
                else if (Screen.hasControlDown())
                    MiscUtil.moveListElementUp(components, this);
                else {
                    dropdown.onRemoved();
                    if (component != null) {
                        component.onRemoved();
                    }
                    components.remove(this);
                }
                requestPositionUpdate();
            }).bounds(0, 0, 20, 20).build();
        }

        public int getWidth() {
            int width = dropdown.getWidth();
            return component == null ? width : width+component.getWidth()+4;
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
            return dropdown.getValue();
        }

        public Object getValue() {
            if (component == null) return null;
            return component.getValue();
        }
    }
}
