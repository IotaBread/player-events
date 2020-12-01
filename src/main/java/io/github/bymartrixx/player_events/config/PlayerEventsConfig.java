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
    private final String[] killPlayerActions;
    private final String[] killEntityActions;

    public PlayerEventsConfig() {
        this.deathActions = new String[] {};
        this.joinActions = new String[] {};
        this.leaveActions = new String[] {};
        this.killPlayerActions = new String[] {};
        this.killEntityActions = new String[] {};
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

    public String[] getKillPlayerActions() {
        return this.killPlayerActions;
    }

    public String[] getKillEntityActions() {
        return this.killEntityActions;
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

    public void executeKillPlayerActions(MinecraftServer server, ServerPlayerEntity player) {
        Utils.sendMessagesToEveryone(server, player, killPlayerActions);
    }

    public void executeKillEntityActions(MinecraftServer server, ServerPlayerEntity player) {
        Utils.sendMessagesToEveryone(server, player, killEntityActions);
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

    public void testKillPlayerActions(ServerCommandSource source) {
        source.sendFeedback(new LiteralText("Kill player actions:").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        Utils.sendMessagesToSource(source, killPlayerActions);
    }

    public void testKillEntityActions(ServerCommandSource source) {
        source.sendFeedback(new LiteralText("Kill entity actions:").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        Utils.sendMessagesToSource(source, killEntityActions);
    }

    public void testEveryActionGroup(ServerCommandSource source) {
        testDeathActions(source);
        testJoinActions(source);
        testLeaveActions(source);
        testKillPlayerActions(source);
        testKillEntityActions(source);
    }
}
