package com.redpxnda.nucleus.facet.item;

import com.redpxnda.nucleus.facet.*;
import com.redpxnda.nucleus.facet.event.FacetAttachmentEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public interface ItemStackFacet<F extends ItemStackFacet<F, T>, T extends Tag> extends Facet<T> {
    /**
     * Use this to update the nbt of an itemstack with this facet
     */
    default void updateNbtOf(FacetKey<F> key, ItemStack stack) {
        stack.getOrCreateTagElement(FacetRegistry.TAG_FACETS_ID).put(key.id().toString(), toNbt());
    }

    /**
     * Called on the *new* stack's facet when the old stack holding the old facet is copied.
     */
    default void onCopied(F original) {
        loadNbt(original.toNbt());
    }

    static void setupFacets(ItemStack stack) {
        FacetAttachmentEvent.FacetAttacher attacher = new FacetAttachmentEvent.FacetAttacher();
        FacetRegistry.ITEM_FACET_ATTACHMENT.invoker().attach(stack, attacher);
        FacetHolder holder = FacetHolder.of(stack);
        holder.setFacetsFromAttacher(attacher);
        if (!holder.getFacets().isEmpty()) writeFacetsToNbt(stack.getOrCreateTag(), holder);
    }

    static void writeFacetsToNbt(CompoundTag root, FacetHolder holder) {
        CompoundTag facetsNbt = new CompoundTag();
        holder.getFacets().forEach((key, facet) -> facetsNbt.put(key.id().toString(), facet.toNbt()));
        if (!facetsNbt.isEmpty())
            root.getCompound("tag").put(FacetRegistry.TAG_FACETS_ID, facetsNbt);
    }
}
