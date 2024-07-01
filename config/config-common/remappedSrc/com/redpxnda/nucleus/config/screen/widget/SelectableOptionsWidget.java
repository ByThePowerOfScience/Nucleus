package com.redpxnda.nucleus.config.screen.widget;

import com.redpxnda.nucleus.util.Color;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class SelectableOptionsWidget<T> extends AbstractScrollWidget {
    public Map<String, T> options;
    public final Font textRenderer;
    public final BiConsumer<String, T> onSelected;

    public SelectableOptionsWidget(Font textRenderer, Map<String, T> options, BiConsumer<String, T> onSelected, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.textRenderer = textRenderer;
        this.options = options;
        this.onSelected = onSelected;
    }

    @Override
    protected int getInnerHeight() {
        return 4 + options.size()*(textRenderer.lineHeight+1);
    }

    @Override
    protected double scrollRate() {
        return Screen.hasShiftDown() ? 8 : 4;
    }

    @Override
    protected void renderContents(GuiGraphics context, int mouseX, int mouseY, float delta) {
        int y = getY()+4;
        for (String option : options.keySet()) {
            int sectionHeight = textRenderer.lineHeight+1;
            boolean hovered = mouseY >= y-scrollAmount() && mouseY < y-scrollAmount()+sectionHeight && mouseX >= getX() && mouseX < getX()+getWidth();
            renderScrollingString(context, textRenderer, Component.literal(option), getX(), y, getX()+width, y+sectionHeight, hovered ? Color.WHITE.argb() : Color.TEXT_GRAY.argb());
            y += sectionHeight;
        }
    }

    protected static void renderScrollingString(GuiGraphics context, Font textRenderer, Component text, int left, int top, int right, int bottom, int color) {
        int i = textRenderer.width(text);
        int j = (top + bottom - textRenderer.lineHeight) / 2 + 1;
        int k = right - left;
        if (i > k) {
            int l = i - k;
            double d = (double) Util.getMillis() / 1000.0;
            double e = Math.max((double)l * 0.5, 3.0);
            double f = Math.sin(1.5707963267948966 * Math.cos(Math.PI * 2 * d / e)) / 2.0 + 0.5;
            double g = Mth.lerp(f, 0.0, (double)l);
            //context.enableScissor(left, top, right, bottom);
            context.drawString(textRenderer, text, left - (int)g, j, color);
            //context.disableScissor();
        } else {
            context.drawCenteredString(textRenderer, text, (left + right) / 2, j, color);
        }
    }

    @Override
    public void setScrollAmount(double scrollY) {
        super.setScrollAmount(scrollY);
    }

    @Override
    protected void renderDecorations(GuiGraphics context) {
        if (this.scrollbarVisible()) {
            int thumbHeight = 12;
            int left = getX() + width;
            int right = getX() + width + 4;
            int top = Math.max(getY(), (int)scrollAmount() * (height - thumbHeight) / getMaxScrollAmount() + getY());
            int bottom = top + thumbHeight;
            context.fill(left, top, right, bottom, -8355712);
            context.fill(left, top, right - 1, bottom - 1, -4144960);
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return active && visible && mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width+4 && mouseY < getY() + height;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY) && button == 0) {
            int y = getY()+2;
            for (Map.Entry<String, T> option : options.entrySet()) {
                int sectionHeight = textRenderer.lineHeight+1;
                if (mouseY >= y-scrollAmount() && mouseY < y-scrollAmount()+sectionHeight && mouseX >= getX() && mouseX < getX()+getWidth()) {
                    onSelected.accept(option.getKey(), option.getValue());
                    return true;
                }
                y += sectionHeight;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {

    }
}
