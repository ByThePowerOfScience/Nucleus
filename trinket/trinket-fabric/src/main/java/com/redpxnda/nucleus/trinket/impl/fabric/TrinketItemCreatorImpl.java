package com.redpxnda.nucleus.trinket.impl.fabric;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.redpxnda.nucleus.trinket.curiotrinket.CurioTrinket;
import com.redpxnda.nucleus.trinket.curiotrinket.CurioTrinketRenderer;
import com.redpxnda.nucleus.trinket.fabric.SlotReferenceCreator;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.UUID;

public class TrinketItemCreatorImpl {
    public static void registerCurioTrinket(Item item, CurioTrinket trinket) {
        Trinket t = new Trinket() {
            @Override
            public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
                trinket.tick(stack, entity, SlotReferenceCreator.get(slot));
            }

            @Override
            public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
                trinket.onEquip(stack, entity, SlotReferenceCreator.get(slot));
            }

            @Override
            public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
                trinket.onUnequip(stack, entity, SlotReferenceCreator.get(slot));
            }

            @Override
            public boolean canEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
                return trinket.canEquip(stack, entity, SlotReferenceCreator.get(slot));
            }

            @Override
            public boolean canUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
                return trinket.canUnequip(stack, entity, SlotReferenceCreator.get(slot));
            }

            @Override
            public Multimap<Attribute, AttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
                Multimap<Attribute, AttributeModifier> map = trinket.useNbtAttributeBehavior(stack, entity, SlotReferenceCreator.get(slot), uuid) ?
                        Trinket.super.getModifiers(stack, slot, entity, uuid) :
                        HashMultimap.create();
                map.putAll(trinket.getAttributeModifiers(stack, entity, SlotReferenceCreator.get(slot), uuid));
                return map;
            }

            @Override
            public TrinketEnums.DropRule getDropRule(ItemStack stack, SlotReference slot, LivingEntity entity) {
                CurioTrinket.DropRule dropRule = trinket.getDropRule(stack, entity, slot.index());
                return switch (dropRule) {
                    case KEEP -> TrinketEnums.DropRule.KEEP;
                    case DROP -> TrinketEnums.DropRule.DROP;
                    case DESTROY -> TrinketEnums.DropRule.DESTROY;
                    case DEFAULT -> TrinketEnums.DropRule.DEFAULT;
                };
            }
        };
        TrinketsApi.registerTrinket(item, t);
    }

    @Environment(EnvType.CLIENT)
    public static void registerCurioTrinketRenderer(Item item, CurioTrinketRenderer renderer) {
        TrinketRendererRegistry.registerRenderer(item, new CustomTrinketRenderer(renderer));
    }

    @Environment(EnvType.CLIENT)
    private record CustomTrinketRenderer(CurioTrinketRenderer delegate) implements TrinketRenderer {
        @Override
        public void render(ItemStack itemStack, SlotReference slotReference, EntityModel<? extends LivingEntity> entityModel,
                           PoseStack poseStack, MultiBufferSource multiBufferSource, int light,
                           LivingEntity livingEntity, float limbAngle, float limbDistance,
                           float tickDelta, float animationProgress, float headYaw,
                           float headPitch) {
            delegate.render(
                    itemStack, livingEntity, slotReference.index(), poseStack, entityModel, multiBufferSource, light, limbAngle, limbDistance,
                    tickDelta, animationProgress, headYaw, headPitch
            );
        }
    }
}
