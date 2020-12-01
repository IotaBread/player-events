package io.github.bymartrixx.player_events.command;

import com.mojang.brigadier.tree.LiteralCommandNode;

import io.github.bymartrixx.player_events.config.PlayerEventsConfig;
import static net.minecraft.server.command.CommandManager.*;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

public class ReloadCommand {
    public static LiteralCommandNode<ServerCommandSource> getNode() {
        LiteralCommandNode<ServerCommandSource> reloadNode = literal("reload")
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    source.sendFeedback(new LiteralText("Reloading Player Events config..."), true);
                    PlayerEventsConfig.Manager.loadConfig();

                    return 1;
                })
                .build();


        return reloadNode;
    }
}
