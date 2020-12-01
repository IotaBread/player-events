package io.github.bymartrixx.player_events.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface PlayerKillEntityCallback {
    Event<PlayerKillEntityCallback> EVENT = EventFactory.createArrayBacked(PlayerKillEntityCallback.class, (listeners) -> (player, entity) -> {
        for (PlayerKillEntityCallback listener : listeners) {
            ActionResult result = listener.killEntity(player, entity);

            if (result != ActionResult.PASS) {
                return result;
            }
        }

        return ActionResult.PASS;
    });

    ActionResult killEntity(ServerPlayerEntity player, Entity entity);
}
