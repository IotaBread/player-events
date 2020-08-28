package io.github.bymartrixx.join_messages.config;

import io.github.bymartrixx.join_messages.Utils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;

public class JoinMessagesConfig {
    private String[] joinMessages;
    private String[] leaveMessages;

    public JoinMessagesConfig() {
        this.joinMessages = new String[] {};
        this.leaveMessages = new String[] {};
    }

    public String[] getJoinMessages() {
        return this.joinMessages;
    }

    public String[] getLeaveMessages() {
        return this.leaveMessages;
    }

    public void runJoinMessages(MinecraftServer server, ServerPlayerEntity player) {
        for (String message : joinMessages) {
            if (message.charAt(0) == '/') {
                server.getCommandManager().execute(server.getCommandSource(), Utils.messageAsString(message, player));
            }
            else {
                String text = Utils.messageAsString(message, player);
                server.sendSystemMessage(new LiteralText(text), Util.NIL_UUID);

                for (ServerPlayerEntity sPlayerEntity : server.getPlayerManager().getPlayerList()) {
                    sPlayerEntity.sendSystemMessage(Utils.messageAsText(message, player), Util.NIL_UUID);
                }
            }
        }
    }

    public void runLeaveMessages(MinecraftServer server, ServerPlayerEntity player) {
        for (String message : leaveMessages) {
            if (message.charAt(0) == '/') {
                server.getCommandManager().execute(server.getCommandSource(), Utils.messageAsString(message, player));
            }
            else {
                String text = Utils.messageAsString(message, player);
                server.sendSystemMessage(new LiteralText(text), Util.NIL_UUID);

                for (ServerPlayerEntity sPlayerEntity : server.getPlayerManager().getPlayerList()) {
                    sPlayerEntity.sendSystemMessage(Utils.messageAsText(message, player), Util.NIL_UUID);
                }
            }
        }
    }
}