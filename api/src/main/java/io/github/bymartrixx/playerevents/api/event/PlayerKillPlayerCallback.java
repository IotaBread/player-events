package io.github.bymartrixx.playerevents.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

/**
 * @deprecated use {@link me.bymartrixx.playerevents.api.event.PlayerKillPlayerCallback} instead.
 */
@Deprecated
public interface PlayerKillPlayerCallback {
    Event<PlayerKillPlayerCallback> EVENT = EventFactory.createArrayBacked(PlayerKillPlayerCallback.class, (listeners) -> (player, killedPlayer) -> {
        for (PlayerKillPlayerCallback listener : listeners) {
            ActionResult result = listener.killPlayer(player, killedPlayer);

            if (result != ActionResult.PASS) {
                return result;
            }
        }

        return ActionResult.PASS;
    });

    ActionResult killPlayer(ServerPlayerEntity player, ServerPlayerEntity killedPlayer);
}
