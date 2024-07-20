package com.redpxnda.nucleus.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.redpxnda.nucleus.mixin.client.DrawContextAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class GuiDrawUtil {
    public static void fill(GuiGraphics graphics, float minX, float minY, float maxX, float maxY, Color color) {
        fill(graphics, minX, minY, maxX, maxY, 0, color);
    }
    public static void fill(GuiGraphics graphics, float minX, float minY, float maxX, float maxY, int z, Color color) {
        fill(graphics, RenderType.gui(), minX, minY, maxX, maxY, z, color);
    }
    public static void fill(GuiGraphics graphics, RenderType renderType, float minX, float minY, float maxX, float maxY, int z, Color color) {
        float i;
        Matrix4f matrix4f = graphics.pose().last().pose();
        if (minX < maxX) {
            i = minX;
            minX = maxX;
            maxX = i;
        }
        if (minY < maxY) {
            i = minY;
            minY = maxY;
            maxY = i;
        }
        float a = color.alphaAsFloat();
        float r = color.redAsFloat();
        float g = color.greenAsFloat();
        float b = color.blueAsFloat();
        VertexConsumer vertexConsumer = graphics.bufferSource().getBuffer(renderType);
        vertexConsumer.addVertex(matrix4f, minX, minY, z).setColor(r, g, b, a);
        vertexConsumer.addVertex(matrix4f, minX, maxY, z).setColor(r, g, b, a);
        vertexConsumer.addVertex(matrix4f, maxX, maxY, z).setColor(r, g, b, a);
        vertexConsumer.addVertex(matrix4f, maxX, minY, z).setColor(r, g, b, a);
        ((DrawContextAccessor) graphics).callTryDraw();
    }
}
