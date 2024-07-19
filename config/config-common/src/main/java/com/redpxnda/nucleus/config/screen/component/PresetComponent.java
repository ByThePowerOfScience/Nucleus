package com.redpxnda.nucleus.config.screen.component;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.redpxnda.nucleus.config.preset.ConfigPreset;
import com.redpxnda.nucleus.config.preset.ConfigProvider;
import com.redpxnda.nucleus.util.Comment;
import com.redpxnda.nucleus.util.MiscUtil;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;

public class PresetComponent<C, E extends Enum<E> & ConfigProvider<C>> extends DropdownComponent<ConfigPreset<C, E>> {
    public static final ResourceLocation WARNING = new ResourceLocation("textures/gui/report_button.png");
    public static final Component DESC_TEXT = Component.translatable("nucleus.config_screen.preset.description");
    public static final Component WARNING_TEXT = Component.translatable("nucleus.config_screen.preset.warning");

    public PresetComponent(Font textRenderer, int x, int y, int width, int height, Class<E> enumClass) {
        super(textRenderer, x, y, width, height, MiscUtil.evaluateSupplier(() -> {
            E[] constants = enumClass.getEnumConstants();
            BiMap<String, ConfigPreset<C, E>> map = HashBiMap.create();
            map.put("none", ConfigPreset.none());
            Map<String, Component> comments = new HashMap<>();
            for (E constant : constants) {
                String name = constant.name();
                try {
                    Field f = enumClass.getField(name);
                    Comment comment = f.getAnnotation(Comment.class);
                    if (comment != null) comments.put(name, Component.literal(comment.value()));
                } catch (NoSuchFieldException ignored) {}
                map.put(name, ConfigPreset.of(constant));
            }
            return new Tuple<>(map, comments);
        }));
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (selected.getEntry() != null) {
            boolean hovered = mouseX >= getX()+getWidth()+8 && mouseX < getX()+getWidth()+28 && mouseY >= getY() && mouseY < getY()+20;
            context.blit(WARNING, getX()+getWidth()+8, getY(), 0, hovered ? 20 : 0, 20, 20, 64, 64);
            if (hovered) context.renderTooltip(textRenderer, textRenderer.split(WARNING_TEXT, 150), DefaultTooltipPositioner.INSTANCE, mouseX, mouseY);
        }
        super.renderWidget(context, mouseX, mouseY, delta);
    }

    @Override
    public @Nullable Component getInstructionText() {
        return DESC_TEXT;
    }
}
