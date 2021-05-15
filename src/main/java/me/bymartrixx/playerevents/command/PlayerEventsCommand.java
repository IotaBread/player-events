package me.bymartrixx.playerevents.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.bymartrixx.playerevents.PlayerEvents;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class PlayerEventsCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> node = registerMain(dispatcher);

        dispatcher.register(literal(PlayerEvents.MOD_ID)
                .requires((ServerCommandSource) -> ServerCommandSource.hasPermissionLevel(2))
                .redirect(node)
        );
    }

    public static LiteralCommandNode<ServerCommandSource> registerMain(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> playerEventsNode = literal("pe")
                .requires((ServerCommandSource) -> ServerCommandSource.hasPermissionLevel(2))
                .build();

        LiteralCommandNode<ServerCommandSource> reloadNode = ReloadCommand.getNode();

        LiteralCommandNode<ServerCommandSource> testNode = TestCommand.getNode();

        dispatcher.getRoot().addChild(playerEventsNode);
        playerEventsNode.addChild(reloadNode);
        playerEventsNode.addChild(testNode);

        return playerEventsNode;
    }
}
