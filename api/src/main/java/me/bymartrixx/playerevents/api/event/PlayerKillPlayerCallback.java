package me.bymartrixx.playerevents.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerKillPlayerCallback {
    Event<PlayerKillPlayerCallback> EVENT = EventFactory.createArrayBacked(PlayerKillPlayerCallback.class, (listeners) -> (player, killedPlayer) -> {
        for (PlayerKillPlayerCallback listener : listeners) {
            listener.killPlayer(player, killedPlayer);
        }
    });

    void killPlayer(ServerPlayerEntity player, ServerPlayerEntity killedPlayer);
}
