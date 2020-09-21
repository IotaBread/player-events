package io.github.bymartrixx.player_events.config;

import io.github.bymartrixx.player_events.Utils;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class PlayerEventsConfig {
    private final String[] deathActions;
    private final String[] joinActions;
    private final String[] leaveActions;

    public PlayerEventsConfig() {
        this.deathActions = new String[] {};
        this.joinActions = new String[] {};
        this.leaveActions = new String[] {};
    }

    public String[] getDeathActions() {
        return this.deathActions;
    }

    public String[] getJoinActions() {
        return this.joinActions;
    }

    public String[] getLeaveActions() {
        return this.leaveActions;
    }

    public void executeDeathActions(ServerPlayerEntity player) {
        Utils.sendMessagesToEveryone(player.getServer(), player, deathActions);
    }

    public void executeJoinActions(MinecraftServer server, ServerPlayerEntity player) {
        Utils.sendMessagesToEveryone(server, player, joinActions);
    }

    public void executeLeaveActions(MinecraftServer server, ServerPlayerEntity player) {
        Utils.sendMessagesToEveryone(server, player, leaveActions);
    }

    public void testDeathActions(ServerCommandSource source) {
        source.sendFeedback(new LiteralText("Death actions:").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        Utils.sendMessagesToSource(source, deathActions);
    }

    public void testJoinActions(ServerCommandSource source) {
        source.sendFeedback(new LiteralText("Join actions:").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        Utils.sendMessagesToSource(source, joinActions);
    }

    public void testLeaveActions(ServerCommandSource source) {
        source.sendFeedback(new LiteralText("Leave actions:").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        Utils.sendMessagesToSource(source, leaveActions);
    }

    public void testEveryActionGroup(ServerCommandSource source) {
        testDeathActions(source);
        testJoinActions(source);
        testLeaveActions(source);
    }
}
