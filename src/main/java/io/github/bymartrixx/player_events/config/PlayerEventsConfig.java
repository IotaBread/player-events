package io.github.bymartrixx.player_events.config;

import io.github.bymartrixx.player_events.Utils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class PlayerEventsConfig {
    private final String[] deathMessages;
    private final String[] joinMessages;
    private final String[] leaveMessages;

    public PlayerEventsConfig() {
        this.deathMessages = new String[] {};
        this.joinMessages = new String[] {};
        this.leaveMessages = new String[] {};
    }

    public String[] getDeathMessages() {
        return this.deathMessages;
    }

    public String[] getJoinMessages() {
        return this.joinMessages;
    }

    public String[] getLeaveMessages() {
        return this.leaveMessages;
    }

    public void sendDeathMessages(MinecraftServer server, ServerPlayerEntity player) {
        Utils.sendMessagesToEveryone(server, player, deathMessages);
    }

    public void sendJoinMessages(MinecraftServer server, ServerPlayerEntity player) {
        Utils.sendMessagesToEveryone(server, player, joinMessages);
    }

    public void sendLeaveMessages(MinecraftServer server, ServerPlayerEntity player) {
        Utils.sendMessagesToEveryone(server, player, leaveMessages);
    }

    public void testDeathMessages(ServerCommandSource source) {
        source.sendFeedback(new LiteralText("Death messages:").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        Utils.sendMessagesToSource(source, deathMessages);
    }

    public void testJoinMessages(ServerCommandSource source) {
        source.sendFeedback(new LiteralText("Join messages:").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        Utils.sendMessagesToSource(source, joinMessages);
    }

    public void testLeaveMessages(ServerCommandSource source) {
        source.sendFeedback(new LiteralText("Leave messages:").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        Utils.sendMessagesToSource(source, leaveMessages);
    }

    public void testEveryMessageGroup(ServerCommandSource source) {
        testDeathMessages(source);
        testJoinMessages(source);
        testLeaveMessages(source);
    }
}
