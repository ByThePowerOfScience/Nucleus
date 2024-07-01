package com.redpxnda.nucleus.pose.server;

import com.redpxnda.nucleus.facet.entity.EntityFacet;
import com.redpxnda.nucleus.facet.FacetKey;
import com.redpxnda.nucleus.network.PlayerSendable;
import com.redpxnda.nucleus.pose.network.clientbound.PoseFacetSyncPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;

public class ServerPoseFacet implements EntityFacet<CompoundTag> {
    public static FacetKey<ServerPoseFacet> KEY;

    public static ServerPoseFacet get(ServerPlayer entity) {
        return KEY.get(entity);
    }

    public String pose = "none";
    public InteractionHand usedHand = InteractionHand.MAIN_HAND;
    public long updateTime = -100;

    public ServerPoseFacet(Entity entity) {
    }

    @Override
    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putString("pose", pose);
        tag.putLong("updateTime", updateTime);
        tag.putString("usedHand", usedHand == InteractionHand.MAIN_HAND ? "main" : "off");
        return tag;
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        pose = tag.getString("pose");
        updateTime = tag.getLong("updateTime");
        usedHand = tag.getString("usedHand").equals("main") ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
    }

    public void set(String pose, long time) {
        setPose(pose);
        setUpdateTime(time);
    }
    public void set(String pose, long time, InteractionHand usedHand) {
        set(pose, time);
        setUsedHand(usedHand);
    }
    public void set(String pose, ServerPlayer facetHolder) {
        set(pose, facetHolder.level().getGameTime());
        sendToTrackers(facetHolder);
        sendToClient(facetHolder);
    }
    public void set(String pose, ServerPlayer facetHolder, InteractionHand usedHand) {
        set(pose, facetHolder.level().getGameTime(), usedHand);
        sendToTrackers(facetHolder);
        sendToClient(facetHolder);
    }
    public void reset() {
        setPose("none");
    }
    public void reset(long time) {
        setPose("none");
        setUpdateTime(time);
    }
    public void reset(ServerPlayer facetHolder) {
        set("none", facetHolder.level().getGameTime());
        sendToTrackers(facetHolder);
        sendToClient(facetHolder);
    }
    public void setPose(String pose) {
        this.pose = pose;
    }
    public String getPose() {
        return pose;
    }
    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
    public long getUpdateTime() {
        return updateTime;
    }
    public InteractionHand getUsedHand() {
        return usedHand;
    }
    public void setUsedHand(InteractionHand usedHand) {
        this.usedHand = usedHand;
    }

    @Override
    public PlayerSendable createPacket(Entity target) {
        return new PoseFacetSyncPacket(target, this);
    }
}
