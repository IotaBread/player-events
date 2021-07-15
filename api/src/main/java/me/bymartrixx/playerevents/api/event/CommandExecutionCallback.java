package me.bymartrixx.playerevents.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.command.ServerCommandSource;

public interface CommandExecutionCallback {
    Event<CommandExecutionCallback> EVENT = EventFactory.createArrayBacked(CommandExecutionCallback.class, listeners -> (command, source) -> {
        for (CommandExecutionCallback listener : listeners) {
            listener.onExecuted(command, source);
        }
    });

    void onExecuted(String command, ServerCommandSource source);
}
