package me.bymartrixx.playerevents.util;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.TextParserUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;

public class Utils {
    public static Text parseAndReplace(String input, ServerPlayerEntity player, Map<String, ?> placeholderArgs) {
        Text parsed = TextParserUtils.formatText(input);
        Text parsed2 = Placeholders.parseText(parsed, PlaceholderContext.of(player));

        return PlaceholderReplacingUtil.replacePlaceholders(input, parsed2, placeholderArgs);
    }

    public static Text parseAndReplace(String input, ServerCommandSource source, Map<String, ?> placeholderArgs) {
        Text parsed = TextParserUtils.formatText(input);
        Text parsed2;
        if (source.getEntity() != null) {
            parsed2 = Placeholders.parseText(parsed, PlaceholderContext.of(source.getEntity()));
        } else {
            parsed2 = Placeholders.parseText(parsed, PlaceholderContext.of(source.getServer()));
        }

        return PlaceholderReplacingUtil.replacePlaceholders(input, parsed2, placeholderArgs);
    }

    public static void message(ServerPlayerEntity player, Text msg, boolean broadcast) {
        if (!broadcast) {
            player.sendSystemMessage(msg);
        } else {
            MinecraftServer server = player.getServer();
            if (server == null) {
                return; // Shouldn't happen
            }

            server.sendSystemMessage(msg);
            for (ServerPlayerEntity player1 : server.getPlayerManager().getPlayerList()) {
                player1.sendSystemMessage(msg);
            }
        }
    }

    public static String doubleToStr(double d) {
        String str = String.format("%.1f", d);
        return str.replace(',', '.');
    }

    public static Text doubleToText(double d) {
        return Text.literal(doubleToStr(d));
    }
}
