package com.redpxnda.nucleus.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.redpxnda.nucleus.client.ArmRenderer;
import com.redpxnda.nucleus.event.RenderEvents;
import dev.architectury.event.EventResult;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class HeldItemRendererMixin {
    /*@WrapOperation(
            method = "renderItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;getHandRenderType(Lnet/minecraft/client/network/ClientPlayerEntity;)Lnet/minecraft/client/render/item/HeldItemRenderer$HandRenderType;")
    )
    private HeldItemRenderer.HandRenderType nucleus$changeRenderedHandsEvent(ClientPlayerEntity player, Operation<HeldItemRenderer.HandRenderType> original) {
        HeldItemRenderer.HandRenderType selection = original.call(player);
        RenderEvents.RenderedHands hands = switch (selection) {
            case RENDER_BOTH_HANDS -> RenderEvents.RenderedHands.BOTH.copy();
            case RENDER_MAIN_HAND_ONLY -> RenderEvents.RenderedHands.MAINHAND.copy();
            case RENDER_OFF_HAND_ONLY -> RenderEvents.RenderedHands.OFFHAND.copy();
        };
        RenderEvents.CHANGE_RENDERED_HANDS.invoker().evaluate(player, hands);
        if (hands.hasBeenModified()) {
            selection =
                    hands.hasMainhand() && hands.hasOffhand() ? HeldItemRenderer.HandRenderType.RENDER_BOTH_HANDS :
                    hands.hasMainhand() ? HeldItemRenderer.HandRenderType.RENDER_MAIN_HAND_ONLY :
                    HeldItemRenderer.HandRenderType.RENDER_OFF_HAND_ONLY;
        }
        return selection;
    }*/

    @Inject(
            method = "renderArmWithItem",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", shift = At.Shift.AFTER),
            cancellable = true
    )
    private void nucleus$renderArmEventPushStage(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack stack, float equippedProgress, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo ci) {
        if (nucleus$renderArmWithItemEvent(RenderEvents.ArmRenderStage.PUSHED, player, partialTicks, pitch, hand, swingProgress, stack, equippedProgress, poseStack, buffer, combinedLight))
            ci.cancel();
    }

    @Inject(
            method = "renderArmWithItem",
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"),
            cancellable = true
    )
    private void nucleus$renderArmEventArmStage(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack stack, float equippedProgress, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo ci) {
        if (!stack.isEmpty()) return;
        if (nucleus$renderArmWithItemEvent(RenderEvents.ArmRenderStage.ARM, player, partialTicks, pitch, hand, swingProgress, stack, equippedProgress, poseStack, buffer, combinedLight))
            ci.cancel();
    }

    @Inject(
            method = "renderArmWithItem",
            at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"),
            cancellable = true
    )
    private void nucleus$renderArmEventItemStage(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack stack, float equippedProgress, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo ci) {
        if (nucleus$renderArmWithItemEvent(RenderEvents.ArmRenderStage.ITEM, player, partialTicks, pitch, hand, swingProgress, stack, equippedProgress, poseStack, buffer, combinedLight))
            ci.cancel();
    }

    private boolean nucleus$renderArmWithItemEvent(RenderEvents.ArmRenderStage stage,
                                                   AbstractClientPlayer player, float partialTicks, float pitch,
                                                   InteractionHand hand, float swingProgress, ItemStack stack,
                                                   float equippedProgress, PoseStack poseStack, MultiBufferSource buffer,
                                                   int combinedLight
    ) {
        boolean bl = hand == InteractionHand.MAIN_HAND;
        HumanoidArm humanoidArm = bl ? player.getMainArm() : player.getMainArm().getOpposite();

        ArmRenderer armRenderer = new ArmRenderer((poseStack2, buffer2, combinedLight2, equippedProgress2, swingProgress2, side2) -> {
            ItemInHandRenderer heldItemRenderer = (ItemInHandRenderer) (Object) this;
            ((HeldItemRendererAccessor) heldItemRenderer).callRenderArmHoldingItem(poseStack2, buffer2, combinedLight2, equippedProgress2, swingProgress2, side2);
        }, poseStack, buffer, combinedLight, equippedProgress, swingProgress, humanoidArm);
        EventResult eventResult = RenderEvents.RENDER_ARM_WITH_ITEM.invoker().render(
                stage, armRenderer, player, poseStack, buffer, stack, hand, partialTicks, pitch, swingProgress, equippedProgress, combinedLight
        );
        if (stage.shouldInterrupt() && eventResult.interruptsFurtherEvaluation()) {
            poseStack.popPose();
            return true;
        }
        return false;
    }
}
