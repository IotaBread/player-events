package me.bymartrixx.playerevents.util;

import eu.pb4.placeholders.PlaceholderAPI;
import eu.pb4.placeholders.TextParser;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.util.Map;

public class Utils {
    public static Text parseAndReplace(String input, ServerPlayerEntity player, Map<String, ?> placeholderArgs) {
        Text parsed = TextParser.parse(input);
        Text parsed2 = PlaceholderAPI.parseText(parsed, player);

        return PlaceholderReplacingUtil.replacePlaceholders(input, parsed2, placeholderArgs);
    }

    public static Text parseAndReplace(String input, ServerCommandSource source, Map<String, ?> placeholderArgs) {
        Text parsed = TextParser.parse(input);
        Text parsed2;
        if (source.getEntity() instanceof ServerPlayerEntity) {
            parsed2 = PlaceholderAPI.parseText(parsed, (ServerPlayerEntity) source.getEntity());
        } else {
            parsed2 = PlaceholderAPI.parseText(parsed, source.getServer());
        }

        return PlaceholderReplacingUtil.replacePlaceholders(input, parsed2, placeholderArgs);
    }

    public static void message(ServerPlayerEntity player, Text msg, boolean broadcast) {
        if (!broadcast) {
            player.sendSystemMessage(msg, Util.NIL_UUID);
        } else {
            MinecraftServer server = player.getServer();
            if (server == null) {
                return; // Shouldn't happen
            }

            server.sendSystemMessage(msg, Util.NIL_UUID);
            for (ServerPlayerEntity player1 : server.getPlayerManager().getPlayerList()) {
                player1.sendSystemMessage(msg, Util.NIL_UUID);
            }
        }
    }

    public static String doubleToStr(double d) {
        String str = String.format("%.1f", d);
        return str.replace(',', '.');
    }

    public static Text doubleToText(double d) {
        return literal(doubleToStr(d));
    }

    public static LiteralText literal(String string) {
        return new LiteralText(string);
    }
}
