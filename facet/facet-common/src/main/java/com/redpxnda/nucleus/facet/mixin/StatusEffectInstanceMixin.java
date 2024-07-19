package com.redpxnda.nucleus.facet.mixin;

import com.redpxnda.nucleus.facet.*;
import com.redpxnda.nucleus.facet.statuseffect.StatusEffectFacet;
import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

@Mixin(MobEffectInstance.class)
public abstract class StatusEffectInstanceMixin implements FacetHolder {
    @Unique
    private final FacetInventory nucleus$facets = new FacetInventory();

    @Override
    public FacetInventory getFacets() {
        return nucleus$facets;
    }

    @Inject(method = "<init>(Lnet/minecraft/core/Holder;IIZZZLnet/minecraft/world/effect/MobEffectInstance;)V", at = @At("RETURN"))
    private void nucleus$attachFacetsOnConstruction(Holder holder, int i, int j, boolean bl, boolean bl2, boolean bl3, MobEffectInstance mobEffectInstance, CallbackInfo ci) {
        StatusEffectFacet.setupFacets((MobEffectInstance) (Object) this);
    }

    @Inject(method = "setDetailsFrom", at = @At("TAIL"))
    private void nucleus$copyFacets(MobEffectInstance that, CallbackInfo ci) {
        clearFacets();
        StatusEffectFacet.setupFacets((MobEffectInstance) (Object) this);
        FacetInventory inv = FacetHolder.of(that).getFacets();
        nucleus$facets.forEach((key, facet) -> {
            if (facet instanceof StatusEffectFacet sef) {
                Facet<?> old = inv.get(key);
                if (old != null) sef.onCopied((StatusEffectFacet) old);
            }
        });
    }

    @Inject(method = "update", at = @At("TAIL"))
    private void nucleus$upgradeFacets(MobEffectInstance that, CallbackInfoReturnable<Boolean> cir) {
        FacetInventory inv = FacetHolder.of(that).getFacets();
        nucleus$facets.forEach((key, facet) -> {
            if (facet instanceof StatusEffectFacet sef)
                sef.attemptUpgradeWith((StatusEffectFacet) inv.get(key), that);
        });
    }

    @Inject(method = "writeTypelessNbt", at = @At("TAIL"))
    private void nucleus$writeFacetsToNbt(CompoundTag nbt, CallbackInfo ci) {
        StatusEffectFacet.writeFacetsToNbt(nbt, this);
    }

    @Inject(method = "fromNbt(Lnet/minecraft/entity/effect/StatusEffect;Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/entity/effect/StatusEffectInstance;",
            at = @At("TAIL"))
    private static void nucleus$readFacetsFromNbt(MobEffect type, CompoundTag nbt, CallbackInfoReturnable<MobEffectInstance> cir) {
        MobEffectInstance instance = cir.getReturnValue();
        if (nbt.contains(FacetRegistry.TAG_FACETS_ID)) {
            CompoundTag facets = nbt.getCompound(FacetRegistry.TAG_FACETS_ID);
            FacetHolder.of(instance).getFacets().forEach((key, facet) -> {
                Tag element = facets.get(key.id().toString());
                FacetRegistry.loadNbtToFacet(element, key, facet);
            });
        }
    }

    @Inject(method = "applyUpdateEffect",
            at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/effect/StatusEffect;applyUpdateEffect(Lnet/minecraft/entity/LivingEntity;I)V"))
    private void nucleus$facetApplyEffectUpdate(LivingEntity entity, CallbackInfo ci) {
        nucleus$facets.forEach((facetKey, facet) -> {
            if (facet instanceof StatusEffectFacet<?,?> sef)
                sef.applyEffectUpdate(entity, (MobEffectInstance) (Object) this);
        });
    }
}
