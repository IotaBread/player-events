package io.github.bymartrixx.player_events;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public class Utils {
    public static void sendMessagesToEveryone(MinecraftServer server, ServerPlayerEntity player, String[] messages) {
        for (String message : messages) {
            if (message.charAt(0) == '/') {
                server.getCommandManager().execute(server.getCommandSource(), Utils.messageAsString(message, player));
            } else {
                String text = Utils.messageAsString(message, player);
                server.sendSystemMessage(new LiteralText(text), Util.NIL_UUID);

                for (ServerPlayerEntity serverPlayerEntity : server.getPlayerManager().getPlayerList()) {
                    serverPlayerEntity.sendSystemMessage(Utils.messageAsText(message, player), Util.NIL_UUID);
                }
            }
        }
    }

    public static void sendMessagesToSource(ServerCommandSource source, String[] messages) {
        for (String message : messages) {
            if (message.charAt(0) == '/') {
                try {
                    ServerPlayerEntity player = source.getPlayer();

                    source.sendFeedback(new LiteralText(Utils.messageAsString(message, player)), false);
                } catch (CommandSyntaxException e) {
                    source.sendFeedback(new LiteralText(Utils.messageAsString(message, source.getName())), false);
                }
            } else {
                try {
                    ServerPlayerEntity player = source.getPlayer();

                    source.sendFeedback(Utils.messageAsText(message, player), false);
                } catch (CommandSyntaxException e) {
                    source.sendFeedback(Utils.messageAsText(message, source.getName()), false);
                }

            }
        }
    }

    public static String messageAsString(String message, PlayerEntity player) {
        return messageAsString(message, player.getName().asString());
    }

    public static String messageAsString(String message, String separator) {
        String[] messagePieces = message.split("%s");

        String result = String.join(separator, messagePieces);

        if (message.charAt(message.length()-2) == '%' && message.charAt(message.length()-1) == 's') {
            result = result + separator;
        }

        return result;
    }

    public static MutableText messageAsText(String message, PlayerEntity player) {
        return messageAsText(message, player.getDisplayName());
    }

    public static MutableText messageAsText(String message, String separator) {
        return messageAsText(message, new LiteralText(separator));
    }

    public static MutableText messageAsText(String message, Text separator) {
        String[] messagePieces = message.split("%s");
        MutableText result = new LiteralText("");
        MutableText[] pieces = new MutableText[messagePieces.length];

        for (int i = 0; i < pieces.length; i++) {
            pieces[i] = new LiteralText(messagePieces[i]).formatted(Formatting.YELLOW);
        }

        for (int i = 0; i < pieces.length + (pieces.length - 1); i++) {
            if (i == 0 && !messagePieces[i].startsWith(" ")) {
                result = pieces[i];
            } else {
                if (i % 2 == 1 || i == 0 && messagePieces[i].startsWith(" ")) {
                    result.append(separator);
                } else {
                    result.append(pieces[i / 2]);
                }
            }
        }

        if (message.charAt(message.length() - 2) == '%' && message.charAt(message.length() - 1) == 's') {
            result.append(separator);
        }

        return result;
    }
}
