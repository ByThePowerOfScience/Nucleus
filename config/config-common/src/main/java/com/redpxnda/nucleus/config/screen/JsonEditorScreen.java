package com.redpxnda.nucleus.config.screen;

import com.google.gson.JsonElement;
import com.redpxnda.nucleus.Nucleus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class JsonEditorScreen extends Screen {
    protected Button discardButton;
    protected Button saveButton;
    protected MultiLineEditBox editBox;
    protected final Screen parent;
    protected final Consumer<JsonElement> updateListener;
    protected final String initialText;

    public JsonEditorScreen(Screen parent, Consumer<JsonElement> updateListener, String initialText) {
        super(Component.translatable("nucleus.json_editor.title"));
        this.parent = parent;
        this.updateListener = updateListener;
        this.initialText = initialText;
    }

    public void setText(String text) {
        editBox.setValue(text);
    }

    @Override
    protected void init() {
        discardButton = Button.builder(Component.translatable("nucleus.config_screen.discard"), wid -> {
            onClose();
        }).bounds(16, height - 26, 96, 20).build();
        saveButton = Button.builder(Component.translatable("nucleus.config_screen.save"), wid -> {
            JsonElement element = getJson();
            if (element != null) {
                updateListener.accept(element);
                closeNoUpdate();
            } else {
                minecraft.getToasts().addToast(new SystemToast(
                        SystemToast.SystemToastId.PACK_LOAD_FAILURE,
                        Component.translatable("nucleus.json_editor.save_fail"),
                        Component.translatable("nucleus.json_editor.save_fail.description")));
            }
        }).bounds(128, height - 26, 96, 20).build();

        editBox = new MultiLineEditBox(minecraft.font, 8, 8, width - 8, height - 40, Component.empty(), Component.empty());

        addRenderableWidget(discardButton);
        addRenderableWidget(saveButton);
        addRenderableWidget(editBox);

        setInitialFocus(editBox);
        setText(initialText);
    }

    public @Nullable JsonElement getJson() {
        try {
            return Nucleus.GSON.fromJson(editBox.getValue(), JsonElement.class);
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        context.pose().pushPose();
        context.pose().translate(0, 0, -15);
        renderBackground(context, mouseX, mouseY, delta);
        context.pose().popPose();
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        updateListener.accept(null);
        minecraft.setScreen(parent);
    }

    public void closeNoUpdate() {
        minecraft.setScreen(parent);
    }
}
