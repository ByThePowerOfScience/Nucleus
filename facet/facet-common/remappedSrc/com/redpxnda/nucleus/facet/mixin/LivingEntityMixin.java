package com.redpxnda.nucleus.facet.mixin;

import com.redpxnda.nucleus.facet.FacetHolder;
import com.redpxnda.nucleus.facet.statuseffect.StatusEffectFacet;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(
            method = "onStatusEffectApplied",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffect;onApplied(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/attribute/AttributeContainer;I)V"))
    private void nucleus$callStatusEffectFacetApplied(MobEffectInstance effect, Entity source, CallbackInfo ci) {
        FacetHolder.of(effect).getFacets().forEach((key, facet) -> {
            if (facet instanceof StatusEffectFacet<?,?> sef)
                sef.onApplied((LivingEntity) (Object) this, effect);
        });
    }

    @Inject(
            method = "onStatusEffectUpgraded",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffect;onApplied(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/attribute/AttributeContainer;I)V"))
    private void nucleus$callStatusEffectFacetAppliedOnChange(MobEffectInstance effect, boolean reapplyEffect, Entity source, CallbackInfo ci) {
        FacetHolder.of(effect).getFacets().forEach((key, facet) -> {
            if (facet instanceof StatusEffectFacet<?,?> sef)
                sef.onApplied((LivingEntity) (Object) this, effect);
        });
    }

    @Inject(
            method = "onStatusEffectUpgraded",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffect;onRemoved(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/attribute/AttributeContainer;I)V"))
    private void nucleus$callStatusEffectFacetRemovedOnChange(MobEffectInstance effect, boolean reapplyEffect, Entity source, CallbackInfo ci) {
        FacetHolder.of(effect).getFacets().forEach((key, facet) -> {
            if (facet instanceof StatusEffectFacet<?,?> sef)
                sef.onRemoved((LivingEntity) (Object) this, effect);
        });
    }

    @Inject(
            method = "onStatusEffectRemoved",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffect;onRemoved(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/attribute/AttributeContainer;I)V"))
    private void nucleus$callStatusEffectFacetRemoved(MobEffectInstance effect, CallbackInfo ci) {
        FacetHolder.of(effect).getFacets().forEach((key, facet) -> {
            if (facet instanceof StatusEffectFacet<?,?> sef)
                sef.onRemoved((LivingEntity) (Object) this, effect);
        });
    }
}
