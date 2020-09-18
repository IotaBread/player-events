package io.github.bymartrixx.player_events.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

/**
 * Callback for players leaving a server.
 * Called after the player has left.
 * Upon return:
 * - SUCCESS cancels further processing and continues with normal leaving behavior.
 * - PASS falls back to further processing and defaults to SUCCESS if no other listeners are available
 * - FAIL cancels further processing and no action is executed from the callback.
 */
public interface PlayerLeaveCallback {
    Event<PlayerLeaveCallback> EVENT = EventFactory.createArrayBacked(PlayerLeaveCallback.class, (listeners) -> (player, server) -> {
        for (PlayerLeaveCallback listener : listeners) {
            ActionResult result = listener.leave(player, server);

            if (result != ActionResult.PASS) {
                return result;
            }
        }

        return ActionResult.PASS;
    });

    ActionResult leave(ServerPlayerEntity player, MinecraftServer server);
}
