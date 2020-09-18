package io.github.bymartrixx.player_events.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

/**
 * Callback for players joining a server.
 * Called after the player has joined.
 * Upon return:
 * - SUCCESS cancels further processing and continues with normal joining behavior.
 * - PASS falls back to further processing and defaults to SUCCESS if no other listeners are available
 * - FAIL cancels further processing and no action is executed from the callback.
 */
public interface PlayerJoinCallback {
    Event<PlayerJoinCallback> EVENT = EventFactory.createArrayBacked(PlayerJoinCallback.class, (listeners) -> (player, server) -> {
        for (PlayerJoinCallback listener : listeners) {
            ActionResult result = listener.join(player, server);

            if (result != ActionResult.PASS) {
                return result;
            }
        }

        return ActionResult.PASS;
    });

    ActionResult join(ServerPlayerEntity player, MinecraftServer server);
}
