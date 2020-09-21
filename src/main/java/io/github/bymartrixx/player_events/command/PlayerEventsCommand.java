package io.github.bymartrixx.player_events.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.github.bymartrixx.player_events.PlayerEvents;
import static net.minecraft.server.command.CommandManager.*;
import net.minecraft.server.command.ServerCommandSource;

public class PlayerEventsCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> node = registerMain(dispatcher);

        dispatcher.register(literal(PlayerEvents.MOD_ID)
                .requires((ServerCommandSource) -> {
                    return ServerCommandSource.hasPermissionLevel(2);
                })
                .redirect(node)
        );
    }

    public static LiteralCommandNode<ServerCommandSource> registerMain(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> playerEventsNode = literal("pe")
                .requires((ServerCommandSource) -> {
                    return ServerCommandSource.hasPermissionLevel(2);
                })
                .build();

        LiteralCommandNode<ServerCommandSource> reloadNode = ReloadCommand.getNode();

        LiteralCommandNode<ServerCommandSource> testNode = TestCommand.getNode();

        dispatcher.getRoot().addChild(playerEventsNode);
        playerEventsNode.addChild(reloadNode);
        playerEventsNode.addChild(testNode);

        return playerEventsNode;
    }
}
