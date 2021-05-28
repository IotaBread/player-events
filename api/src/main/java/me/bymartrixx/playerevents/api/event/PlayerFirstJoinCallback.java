package me.bymartrixx.playerevents.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerFirstJoinCallback {
    Event<PlayerFirstJoinCallback> EVENT = EventFactory.createArrayBacked(PlayerFirstJoinCallback.class, (listeners) -> (player, server) -> {
        for (PlayerFirstJoinCallback listener : listeners) {
            listener.joinServerForFirstTime(player, server);
        }
    });

    void joinServerForFirstTime(ServerPlayerEntity player, MinecraftServer server);
}
