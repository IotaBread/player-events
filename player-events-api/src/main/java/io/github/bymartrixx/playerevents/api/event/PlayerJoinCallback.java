package io.github.bymartrixx.playerevents.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface PlayerJoinCallback {
    Event<PlayerJoinCallback> EVENT = EventFactory.createArrayBacked(PlayerJoinCallback.class, (listeners) -> (player, server) -> {
        for (PlayerJoinCallback listener : listeners) {
            ActionResult result = listener.joinServer(player, server);

            if (result != ActionResult.PASS) {
                return result;
            }
        }

        return ActionResult.PASS;
    });

    ActionResult joinServer(ServerPlayerEntity player, MinecraftServer server);
}
