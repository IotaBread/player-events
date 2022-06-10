package me.bymartrixx.playerevents.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import eu.pb4.placeholders.PlaceholderAPI;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderReplacingUtil {
    public static final KeyResolver<Entity> ENTITY_RESOLVER = (entity, subKey) -> switch (subKey) {
        case "" -> entity.getName();
        case "display" -> entity.getDisplayName();
        case "entityName" -> Utils.literal(entity.getEntityName());
        case "x" -> Utils.doubleToText(entity.getX());
        case "y" -> Utils.doubleToText(entity.getY());
        case "z" -> Utils.doubleToText(entity.getZ());
        default -> null;
    };
    public static final KeyResolver<ServerCommandSource> COMMAND_SOURCE_RESOLVER = (source, subKey) -> {
        Entity entity = source.getEntity();
        if (entity != null) {
            return ENTITY_RESOLVER.resolve(entity, subKey);
        }

        return switch (subKey) {
            case "" -> Utils.literal(source.getName());
            case "display" -> source.getDisplayName();
            case "entityName" -> Utils.literal("none");
            case "x" -> Utils.doubleToText(source.getPosition().x);
            case "y" -> Utils.doubleToText(source.getPosition().y);
            case "z" -> Utils.doubleToText(source.getPosition().z);
            default -> null;
        };
    };
    public static final KeyResolver<Text> TEXT_RESOLVER = (text, subKey) -> text;
    public static final KeyResolver<LazyResolver> LAZY_RESOLVER = LazyResolver::resolve;

    public static final Pattern TOKEN_PATTERN = PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN;

    private static List<String> getPlaceholderKeys(String string) {
        Matcher matcher = TOKEN_PATTERN.matcher(string);
        List<String> keys = Lists.newArrayList();
        while (matcher.find()) {
            keys.add(matcher.group("id"));
        }

        return keys;
    }

    public static Text replacePlaceholders(String string, Text text, Map<String, ?> placeholderArgs) {
        Map<String, Text> placeholders = resolvePlaceholders(string, placeholderArgs);
        return PlaceholderAPI.parsePredefinedText(text, TOKEN_PATTERN, placeholders);
    }

    private static Map<String, Text> resolvePlaceholders(String string, Map<String, ?> placeholderArgs) {
        List<String> usedKeys = getPlaceholderKeys(string);
        Map<String, Text> placeholders = Maps.newHashMap();
        for (String key : usedKeys) {
            Text text = resolvePlaceholder(key, placeholderArgs);
            placeholders.put(key, text);
        }

        return placeholders;
    }

    private static Text resolvePlaceholder(String key, Map<String, ?> placeholderArgs) {
        String baseKey = key.contains(".") ? key.substring(0, key.indexOf(".")) : key;
        Object arg = placeholderArgs.get(baseKey);
        KeyResolver<?> resolver = getResolver(arg);
        if (resolver != null) {
            Text text = resolve(resolver, arg, key);
            if (text != null) {
                return text;
            }
        }

        return Utils.literal("<Unknown placeholder '" + key + "'>");
    }

    private static  <T> Text resolve(KeyResolver<T> resolver, Object arg, String key) {
        //noinspection unchecked
        return resolver.resolveKey((T) arg, key);
    }

    public static KeyResolver<?> getResolver(Object arg) {
        if (arg instanceof Entity) {
            return ENTITY_RESOLVER;
        }

        if (arg instanceof ServerCommandSource) {
            return COMMAND_SOURCE_RESOLVER;
        }

        if (arg instanceof Text) {
            return TEXT_RESOLVER;
        }

        if (arg instanceof LazyResolver) {
            return LAZY_RESOLVER;
        }

        return null;
    }

    public static LazyResolver lazyResolver(Function<String, Text> function) {
        return new LazyResolver(function);
    }
    @FunctionalInterface
    public interface KeyResolver<T> {
        Text resolve(T t, String subKey);

        default Text resolveKey(T t, String key) {
            String subKey = key.contains(".") ? key.substring(key.indexOf('.') + 1) : "";
            return resolve(t, subKey);
        }
    }

    public static class LazyResolver {
        private final Function<String, Text> function;

        public LazyResolver(Function<String, Text> function) {
            this.function = function;
        }

        public Text resolve(String subKey) {
            return function.apply(subKey);
        }
    }
}
