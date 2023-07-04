package me.bymartrixx.playerevents.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import me.bymartrixx.playerevents.config.PlayerEventsConfig;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class ReloadCommand {

    private static final MutableText RELOADING_TEXT = Text.literal("Reloading Player Events config...");

    public static LiteralCommandNode<ServerCommandSource> getNode() {
        return literal("reload")
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    source.sendFeedback(() -> RELOADING_TEXT, true);
                    PlayerEventsConfig.Manager.loadConfig();

                    return 1;
                })
                .build();
    }
}
