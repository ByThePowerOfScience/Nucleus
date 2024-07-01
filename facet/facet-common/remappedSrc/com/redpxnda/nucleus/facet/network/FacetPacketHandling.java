package com.redpxnda.nucleus.facet.network;

import com.redpxnda.nucleus.facet.Facet;
import com.redpxnda.nucleus.facet.FacetRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class FacetPacketHandling {
    public static <T extends Tag> @Nullable Facet<T> getFacetFromSync(int entityId, ResourceLocation facetId) {
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (level == null) return null;

        Entity entity = level.getEntity(entityId);
        if (entity == null) return null;

        return (Facet<T>) FacetRegistry.get(facetId).get(entity);
    }

    public static <T extends Tag> @Nullable Facet<T> getAndSetClientEntityFacet(int entityId, ResourceLocation capId, T data) {
        Facet<T> facet = getFacetFromSync(entityId, capId);
        if (facet != null)
            facet.loadNbt(data);
        return facet;
    }
}
