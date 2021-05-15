package me.bymartrixx.playerevents.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerKillEntityCallback {
    Event<PlayerKillEntityCallback> EVENT = EventFactory.createArrayBacked(PlayerKillEntityCallback.class, (listeners) -> (player, killedEntity) -> {
        for (PlayerKillEntityCallback listener : listeners) {
            listener.killEntity(player, killedEntity);
        }
    });

    void killEntity(ServerPlayerEntity player, Entity killedEntity);
}
