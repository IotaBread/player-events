package io.github.bymartrixx.join_messages.config;

import io.github.bymartrixx.join_messages.Utils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;

public class JoinMessagesConfig {
    private String[] messages;

    public String[] getMessages() {
        return this.messages;
    }

    public JoinMessagesConfig() {
        this.messages = new String[] {};
    }

    public void runMessages(MinecraftServer server, ServerPlayerEntity player) {
        for (String message : messages) {
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