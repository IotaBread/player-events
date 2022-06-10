package me.bymartrixx.playerevents.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import me.bymartrixx.playerevents.config.PlayerEventsConfig;
import me.bymartrixx.playerevents.util.Utils;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class ReloadCommand {
    public static LiteralCommandNode<ServerCommandSource> getNode() {
        return literal("reload")
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    source.sendFeedback(Utils.literal("Reloading Player Events config..."), true);
                    PlayerEventsConfig.Manager.loadConfig();

                    return 1;
                })
                .build();
    }
}
