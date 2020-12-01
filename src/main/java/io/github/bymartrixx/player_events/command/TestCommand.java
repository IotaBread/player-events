package io.github.bymartrixx.player_events.command;

import com.mojang.brigadier.tree.LiteralCommandNode;

import io.github.bymartrixx.player_events.config.PlayerEventsConfigManager;
import static net.minecraft.server.command.CommandManager.*;
import net.minecraft.server.command.ServerCommandSource;

public class TestCommand {
    public static LiteralCommandNode<ServerCommandSource> getNode() {
        LiteralCommandNode<ServerCommandSource> testNode = literal("test").build();

        LiteralCommandNode<ServerCommandSource> joinNode = literal("join")
                .executes(context -> {
                    PlayerEventsConfigManager.getConfig().testJoinActions(context.getSource());
                    return 1;
                })
                .build();

        LiteralCommandNode<ServerCommandSource> leaveNode = literal("leave")
                .executes(context -> {
                    PlayerEventsConfigManager.getConfig().testLeaveActions(context.getSource());
                    return 1;
                })
                .build();

        LiteralCommandNode<ServerCommandSource> deathNode = literal("death")
                .executes(context -> {
                    PlayerEventsConfigManager.getConfig().testDeathActions(context.getSource());
                    return 1;
                })
                .build();

        LiteralCommandNode<ServerCommandSource> killPlayerNode = literal("kill_player")
                .executes(context -> {
                    PlayerEventsConfigManager.getConfig().testKillPlayerActions(context.getSource());
                    return 1;
                })
                .build();

        LiteralCommandNode<ServerCommandSource> killEntityNode = literal("kill_entity")
                .executes(context -> {
                    PlayerEventsConfigManager.getConfig().testKillEntityActions(context.getSource());
                    return 1;
                })
                .build();

        LiteralCommandNode<ServerCommandSource> everyNode = literal("*")
                .executes(context -> {
                    PlayerEventsConfigManager.getConfig().testEveryActionGroup(context.getSource());
                    return 1;
                })
                .build();

        testNode.addChild(joinNode);
        testNode.addChild(leaveNode);
        testNode.addChild(deathNode);
        testNode.addChild(killPlayerNode);
        testNode.addChild(killEntityNode);
        testNode.addChild(everyNode);

        return testNode;
    }
}
