package me.bymartrixx.playerevents.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerFirstDeathCallback {
    Event<PlayerFirstDeathCallback> EVENT = EventFactory.createArrayBacked(PlayerFirstDeathCallback.class, (listeners) -> (player, source) -> {
        for (PlayerFirstDeathCallback listener : listeners) {
            listener.firstDeath(player, source);
        }
    });

    void firstDeath(ServerPlayerEntity player, DamageSource source);
}
