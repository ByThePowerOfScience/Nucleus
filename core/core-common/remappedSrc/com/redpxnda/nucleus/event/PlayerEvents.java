package com.redpxnda.nucleus.event;

import dev.architectury.event.CompoundEventResult;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Holds events related to players. More player related events can be found in {@link dev.architectury.event.events.common.PlayerEvent}.
 */
public interface PlayerEvents {
    /**
     * Fired when a player attempts to harvest a block.
     * This event is fired whenever a player attempts to harvest a block in {@link Player#hasCorrectToolForDrops(BlockState)}.
     * The value in the compound event result represents whether the harvest should be allowed.
     */
    PrioritizedEvent<PlayerHarvestCheck> CAN_PLAYER_HARVEST = PrioritizedEvent.createCompoundEventResult();

    /**
     * Fired when a player attempts to harvest a block.
     * This event is fired whenever a player attempts to harvest a block in
     * {@link BlockBehaviour#getDestroyProgress(BlockState, Player, BlockGetter, BlockPos)}'s usage of {@link Player#getDestroySpeed(BlockState)}.
     * The value in the compound event result represents the new break speed.
     */
    PrioritizedEvent<PlayerBreakSpeed> PLAYER_BREAK_SPEED = PrioritizedEvent.createCompoundEventResult();

    /**
     * Fired when a player's display name is retrieved. (NOTE: the "old" name will include things like team prefixes)
     * This event is fired whenever a player's name is retrieved in {@link Player#getDisplayName()}.
     * The value in the compound event result represents the new display name.
     */
    PrioritizedEvent<PlayerDisplayName> PLAYER_DISPLAY_NAME = PrioritizedEvent.createCompoundEventResult();

    /**
     * Fired when a player's tab list name is retrieved. (NOTE: the "old" name will usually be null, unless on forge)
     * This event is fired whenever a player's name is retrieved in {@link ServerPlayer#getTabListDisplayName()}.
     * The value in the compound event result represents the new display name.
     */
    PrioritizedEvent<PlayerDisplayName> PLAYER_TAB_LIST_NAME = PrioritizedEvent.createCompoundEventResult();


    interface PlayerHarvestCheck {
        CompoundEventResult<Boolean> check(Player player, BlockState state, boolean success);
    }
    interface PlayerBreakSpeed {
        CompoundEventResult<Float> get(Player player, BlockState state, BlockPos pos, float original);
    }
    interface PlayerDisplayName {
        /**
         * @param old Represents the player's old display name - nullable for tab list names
         */
        CompoundEventResult<Component> get(Player player, @Nullable Component old);
    }
}
