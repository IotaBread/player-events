package me.bymartrixx.playerevents;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static final Pattern TOKEN_FORMAT = Pattern.compile("\\$\\{(.+?)}");

    public static void addCommandSourcePlaceholders(Map<String, String> placeholders, ServerCommandSource source, String baseKey) {
        Entity entity = source.getEntity();
        if (entity != null) {
            addEntityPlaceholders(placeholders, entity, baseKey);
        } else {
            placeholders.put(baseKey, source.getName());
            placeholders.put(baseKey + ".display", source.getDisplayName().asString());
            placeholders.put(baseKey + ".uuid", "none");
            Vec3d pos = source.getPosition();
            placeholders.put(baseKey + ".x", String.format("%.1f", pos.x));
            placeholders.put(baseKey + ".y", String.format("%.1f", pos.y));
            placeholders.put(baseKey + ".z", String.format("%.1f", pos.z));
        }
    }

    public static void addEntityPlaceholders(Map<String, String> placeholders, Entity entity, String baseKey) {
        placeholders.put(baseKey, entity.getName().asString());
        placeholders.put(baseKey + ".display", entity.getDisplayName().asString());
        placeholders.put(baseKey + ".uuid", entity.getEntityName());
        placeholders.put(baseKey + ".x", String.format("%.1f", entity.getX()));
        placeholders.put(baseKey + ".y", String.format("%.1f", entity.getY()));
        placeholders.put(baseKey + ".z", String.format("%.1f", entity.getZ()));
    }

    public static String replacePlaceholders(String format, Map<String, String> placeholders) {
        Matcher matcher = TOKEN_FORMAT.matcher(format);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String replacement = placeholders.get(matcher.group(1));
            if (replacement != null) {
                matcher.appendReplacement(buffer, "");
                buffer.append(replacement);
            }
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    public static void addEntityTextPlaceholders(Map<String, Text> placeholders, Entity entity, String baseKey) {
        placeholders.put(baseKey, entity.getName());
        placeholders.put(baseKey + ".display", entity.getDisplayName());
        placeholders.put(baseKey + ".uuid", new LiteralText(entity.getEntityName()));
        placeholders.put(baseKey + ".x", new LiteralText(String.format("%.1f", entity.getX())));
        placeholders.put(baseKey + ".y", new LiteralText(String.format("%.1f", entity.getY())));
        placeholders.put(baseKey + ".z", new LiteralText(String.format("%.1f", entity.getZ())));
    }

    public static void addCommandSourceTextPlaceholders(Map<String, Text> placeholders, ServerCommandSource source, String baseKey) {
        Entity entity = source.getEntity();
        if (entity != null) {
            addEntityTextPlaceholders(placeholders, entity, baseKey);
        } else {
            placeholders.put(baseKey, new LiteralText(source.getName()));
            placeholders.put(baseKey + ".display", source.getDisplayName());
            placeholders.put(baseKey + ".uuid", new LiteralText("none"));
            Vec3d pos = source.getPosition();
            placeholders.put(baseKey + ".x", new LiteralText(String.format("%.1f", pos.x)));
            placeholders.put(baseKey + ".y", new LiteralText(String.format("%.1f", pos.y)));
            placeholders.put(baseKey + ".z", new LiteralText(String.format("%.1f", pos.z)));
        }
    }

    public static Text replaceTextPlaceholders(String format, Map<String, Text> placeholders) {
        Matcher matcher = TOKEN_FORMAT.matcher(format);
        TextBuilder textBuilder = new TextBuilder();
        // The matcher pushes to this, the contents of it are appended to the textBuilder and then are cleared
        StringBuffer helper = new StringBuffer();

        while (matcher.find()) {
            Text replacement = placeholders.get(matcher.group(1));
            if (replacement != null) {
                matcher.appendReplacement(helper, "");
                textBuilder.append(helper.toString());
                helper.setLength(0);
                textBuilder.append(replacement);
            }
        }

        matcher.appendTail(helper);
        textBuilder.append(helper.toString());
        helper.setLength(0);
        return textBuilder.toText();
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

    // This class is very similar to BookLib Book.Page.Builder, TODO: merge?
    public static class TextBuilder {
        private String jsonContents = "";

        public void append(Text text) {
            // If there are no contents, use the provided String as content
            if (this.jsonContents.isEmpty()) {
                this.jsonContents = Text.Serializer.toJson(text);
                return;
            }

            MutableText current = Text.Serializer.fromJson(this.jsonContents);

            if (canTextBeUsedAsString(current) && canTextBeUsedAsString(text)) {
                this.jsonContents = Text.Serializer.toJson(new LiteralText(current.asString() + text.asString()));
            } else {
                this.jsonContents = Text.Serializer.toJson(current != null ? current.append(text) : text);
            }
        }

        public void append(String string) {
            // If there are no contents, use the provided String as content
            if (this.jsonContents.isEmpty()) {
                this.jsonContents = Text.Serializer.toJson(new LiteralText(string));
                return;
            }

            MutableText current = Text.Serializer.fromJson(this.jsonContents);

            if (canTextBeUsedAsString(current)) {
                this.jsonContents = Text.Serializer.toJson(new LiteralText(current.asString() + string));
            } else {
                Text text = new LiteralText(string);
                this.jsonContents = Text.Serializer.toJson(current != null ? current.append(text) : text);
            }
        }

        public Text toText() {
            return Text.Serializer.fromJson(this.jsonContents);
        }

        @Override
        public String toString() {
            return this.jsonContents;
        }

        private static boolean canTextBeUsedAsString(Text text) {
            if (text != null && text.getSiblings().size() == 0 && !(text instanceof TranslatableText)) {
                Style style = text.getStyle();
                return style.isEmpty() || style.getColor() == null
                        && !style.isBold()
                        && !style.isItalic()
                        && !style.isUnderlined()
                        && !style.isStrikethrough()
                        && !style.isObfuscated()
                        && style.getClickEvent() == null
                        && style.getHoverEvent() == null
                        && (style.getInsertion() == null || style.getInsertion().isEmpty());
            } else {
                return false;
            }
        }
    }
}
