package io.github.bymartrixx.playerevents;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static String replace(String string, PlayerEntity player) {
        return replace(string, "${player}", player.getName().asString());
    }

    public static String replace(String string, String token, String replacement) {
        String[] strings = string.split(Pattern.quote(token));
        String result = String.join(replacement, strings);

        if (string.endsWith(token)) {
            result += replacement;
        }

        return result;
    }

    public static MutableText replaceGetText(String string, PlayerEntity player) {
        return replaceGetText(string, "${player}", player.getDisplayName());
    }

    public static MutableText replaceGetText(String string, String token, Text replacement) {
        String[] strings = string.split(Pattern.quote(token));
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

        if (string.endsWith(token)) { // The code above doesn't work if the string ends with the token
            result.append(replacement);
        }

        return result;
    }

    /**
     * What this does is the following:
     *
     * First, splits the {@code string} for each "${".
     * Then, turns the first part of the string into {@link LiteralText} and appends the other parts.
     * After that, it turns the text into a {@link JsonObject}.
     * From the JSON, it gets the text that was just appended, as a {@link JsonArray} (array a).
     * For each element of the array, it appends the corresponding {@code replacement} to another array (array b),
     * and appends the text without the token to the array b.
     * After that, replaces the array a with the array b.
     * Finally, with the {@link Text.Serializer} turns the {@link JsonObject} back to {@link MutableText}
     *
     * @param string The string format. Example: "value is ${value} and name is ${name}"
     * @param tokens The tokens to replace. Example "${value}","${name}"
     * @param replacements The value to replace for each token. Example: 120, "Test"
     * @return the formatted text
     */
    public static MutableText replaceGetText(String string, String[] tokens, Text[] replacements) { // Cursed solution
        if (replacements.length < 1 || tokens.length != replacements.length)
            return null;

        String[] strings = string.split(Pattern.quote("${"));
        MutableText text = new LiteralText("");
        for (int i = 0; i < strings.length; i++) {
            if (i == 0) {
                text = new LiteralText(strings[i]).formatted(Formatting.YELLOW);
            } else {
                text.append(new LiteralText("${" + strings[i]).formatted(Formatting.YELLOW));
            }
        }

        JsonObject textAsJson = Text.Serializer.toJsonTree(text).getAsJsonObject();
        JsonArray extraTextAsJson = textAsJson.get("extra").getAsJsonArray();
        JsonArray extraText2AsJson = new JsonArray();

        Pattern tokenPattern = Pattern.compile("\\$\\{([A-z0-9]+?)}");
        for (int i = 0; i < extraTextAsJson.size(); i++) {
            JsonObject text3 = extraTextAsJson.get(i).getAsJsonObject();
            String text3Str = text3.get("text").getAsString(); // The string of the text3 as plaintext
            Matcher tokenMatcher = tokenPattern.matcher(text3Str);

            String text3Token = "${dummyToken}";
            if (tokenMatcher.find()) {
                text3Token = tokenMatcher.group(0); // The token of the text

                text3Str = tokenMatcher.replaceFirst("");
                text3.remove("text");
                text3.addProperty("text", text3Str);
            }

            int text3TokenIndex = -1; // The index of the token
            for (int j = 0; j < tokens.length; j++) {
                if (text3Token.equals(tokens[j])) {
                    text3TokenIndex = j;
                    break;
                }
            }

            if (text3TokenIndex != -1) {
                extraText2AsJson.add(Text.Serializer.toJsonTree(replacements[text3TokenIndex]));
            }

            extraText2AsJson.add(text3);
        }

        // Replace the extra array
        textAsJson.remove("extra");
        textAsJson.add("extra", extraText2AsJson);

        text = Text.Serializer.fromJson(textAsJson);

        return text;
    }

    public static void sendMessage(MinecraftServer server, ServerPlayerEntity player, Text message, boolean sendToEveryone) {
        if (!sendToEveryone) {
            sendMessage(player, message);
        } else {
            sendMessage(server, message);
        }
    }

    public static void sendMessage(MinecraftServer server, Text message) {
        server.sendSystemMessage(message, Util.NIL_UUID);

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            player.sendSystemMessage(message, Util.NIL_UUID);
        }
    }

    public static void sendMessage(ServerPlayerEntity player, Text message) {
        player.sendSystemMessage(message, Util.NIL_UUID);
    }

    public static void sendMessage(ServerCommandSource source, Text message) {
        source.sendFeedback(message, false);
    }
}
