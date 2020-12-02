package io.github.bymartrixx.player_events;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.util.regex.Pattern;

public class Utils {
    public static String replace(String string, PlayerEntity player) {
        return replace(string, "${player}", player.getName().asString());
    }

    public static String replace(String string, String regex, String replacement) {
        String[] strings = string.split(regex);
        String result = String.join(replacement, strings);

        if (string.endsWith(regex)) {
            result += replacement;
        }

        return result;
    }

    public static MutableText replaceAsText(String string, PlayerEntity player) {
        return replaceAsText(string, "${player}", player.getDisplayName());
    }

    public static MutableText replaceAsText(String string, String regex, Text replacement) {
        String[] strings = string.split(Pattern.quote(regex));
        MutableText result = new LiteralText("");
        MutableText[] texts = new MutableText[strings.length];

        for (int i = 0; i < texts.length; i++) {
            texts[i] = new LiteralText(strings[i]).formatted(Formatting.YELLOW);
        }

        for (int i = 0; i < texts.length + (texts.length - 1); i++) {
            if (i == 0 && strings[i].startsWith(" ")) {
                result = texts[i];
            } else {
                if (i % 2 == 1 || i == 0 && strings[i].startsWith(" ")) {
                    result.append(replacement);
                } else {
                    result.append(texts[i / 2]);
                }
            }
        }

        if (string.endsWith(regex)) {
            result.append(replacement);
        }

        return result;
    }

    public static MutableText replaceAsText(MutableText text, String regex, Text replacement) { // TODO: Change method to keep formatting
        String[] strings = text.getString().split(Pattern.quote(regex));

        MutableText result = new LiteralText("");
        MutableText[] texts = new MutableText[strings.length];

        for (int i = 0; i < texts.length; i++) {
            texts[i] = new LiteralText(strings[i]).formatted(Formatting.YELLOW);
        }

        for (int i = 0; i < texts.length + (texts.length - 1); i++) {
            if (i == 0 && strings[i].startsWith(" ")) {
                result = texts[i];
            } else {
                if (i % 2 == 1 || i == 0 && strings[i].startsWith(" ")) {
                    result.append(replacement);
                } else {
                    result.append(texts[i / 2]);
                }
            }
        }

        if (text.getString().endsWith(regex)) {
            result.append(replacement);
        }

        return result;
    }

    public static void sendMessage(MinecraftServer server, Text message) {
        server.sendSystemMessage(message, Util.NIL_UUID);

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            player.sendSystemMessage(message, Util.NIL_UUID);
        }
    }

    public static void sendMessage(ServerCommandSource source, Text message) {
        source.sendFeedback(message, false);
    }
}
