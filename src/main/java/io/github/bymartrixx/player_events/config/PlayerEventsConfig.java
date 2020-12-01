package io.github.bymartrixx.player_events.config;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.bymartrixx.player_events.PlayerEvents;
import io.github.bymartrixx.player_events.Utils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerEventsConfig {
    public static class Manager {
        private static File configFile;

        public static void prepareConfigFile() {
            if (configFile != null) {
                return;
            }
            configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), PlayerEvents.MOD_ID + ".json");
        }

        public static void createConfigFile() {
            PlayerEvents.CONFIG = new PlayerEventsConfig();

            saveConfig();
        }

        public static void saveConfig() {
            prepareConfigFile();

            String jsonString = PlayerEvents.GSON.toJson(PlayerEvents.CONFIG);
            try (FileWriter fileWriter = new FileWriter(configFile)) {
                fileWriter.write(jsonString);
            } catch (IOException e) {
                System.err.println("Couldn't save Player Events config.");
                e.printStackTrace();
            }
        }

        public static void loadConfig() {
            prepareConfigFile();

            try {
                if (!configFile.exists()) {
                    createConfigFile();
                }
                if (configFile.exists()) {
                    BufferedReader bReader = new BufferedReader(new FileReader(configFile));

                    PlayerEventsConfig savedConfig = PlayerEvents.GSON.fromJson(bReader, PlayerEventsConfig.class);
                    if (savedConfig != null) {
                        PlayerEvents.CONFIG = savedConfig;
                    }
                }
            } catch (FileNotFoundException e) {
                System.err.println("Couldn't load configuration for Player Events. Reverting to default.");
                e.printStackTrace();
                createConfigFile();
            }
        }
    }

    private final Actions deathActions;
    private final Actions joinActions;
    private final Actions leaveActions;
    private final Actions killPlayerActions;
    private final Actions killEntityActions;

    public PlayerEventsConfig() {
        this.deathActions = new Actions();
        this.joinActions = new Actions();
        this.leaveActions = new Actions();
        this.killPlayerActions = new Actions();
        this.killEntityActions = new Actions();
    }

    public String[] getDeathActions() {
        return this.deathActions.actions;
    }

    public String[] getJoinActions() {
        return this.joinActions.actions;
    }

    public String[] getLeaveActions() {
        return this.leaveActions.actions;
    }

    public String[] getKillPlayerActions() {
        return this.killPlayerActions.actions;
    }

    public String[] getKillEntityActions() {
        return this.killEntityActions.actions;
    }

    public void runDeathActions(ServerPlayerEntity player) {
        // TODO: add {source}?
        for (String action : deathActions.actions) {
            MinecraftServer server = player.getServer();
            if (server == null)
                return;
            if (action.charAt(0) == '/') {
                server.getCommandManager().execute(server.getCommandSource(), Utils.replace(action, player));
            } else {
                Utils.sendMessage(server, Utils.replaceAsText(action, player));
            }
        }
    }

    public void runJoinActions(MinecraftServer server, ServerPlayerEntity player) {
        for (String action : joinActions.actions) {
            if (action.charAt(0) == '/') {
                server.getCommandManager().execute(server.getCommandSource(), Utils.replace(action, player));
            } else {
                Utils.sendMessage(server, Utils.replaceAsText(action, player));
            }
        }
    }

    public void runLeaveActions(MinecraftServer server, ServerPlayerEntity player) {
        for (String action : leaveActions.actions) {
            if (action.charAt(0) == '/') {
                server.getCommandManager().execute(server.getCommandSource(), Utils.replace(action, player));
            } else {
                Utils.sendMessage(server, Utils.replaceAsText(action, player));
            }
        }
    }

    public void runKillPlayerActions(MinecraftServer server, ServerPlayerEntity player, ServerPlayerEntity killedPlayer) {
        for (String action : killPlayerActions.actions) {
            if (action.charAt(0) == '/') {
                String string = Utils.replace(action, player);
                string = Utils.replace(string, "{killedPlayer}", killedPlayer.getName().asString());
                server.getCommandManager().execute(server.getCommandSource(), string);
            } else {
                MutableText text = Utils.replaceAsText(action, player);
                text = Utils.replaceAsText(text, "{killedPlayer}", killedPlayer.getDisplayName());
                Utils.sendMessage(server, text);
            }
        }
    }

    public void runKillEntityActions(MinecraftServer server, ServerPlayerEntity player, Entity killedEntity) {
        for (String action : killPlayerActions.actions) {
            if (action.charAt(0) == '/') {
                String string = Utils.replace(action, player);
                string = Utils.replace(string, "{killedEntity}", killedEntity.getName().asString());
                server.getCommandManager().execute(server.getCommandSource(), string);
            } else {
                MutableText text = Utils.replaceAsText(action, player);
                text = Utils.replaceAsText(text, "{killedEntity}", killedEntity.getDisplayName());
                Utils.sendMessage(server, text);
            }
        }
    }

    public void testDeathActions(ServerCommandSource source) {
        source.sendFeedback(new LiteralText("Death actions:").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        for (String action : deathActions.actions) {
            try {
                ServerPlayerEntity player = source.getPlayer();
                Text text = action.charAt(0) == '/' ? new LiteralText(Utils.replace(action, player)) : Utils.replaceAsText(action, player);
                Utils.sendMessage(source, text);
            } catch (CommandSyntaxException e) {
                Text text = action.charAt(0) == '/' ? new LiteralText(Utils.replace(action, "{player}", source.getName())) : Utils.replaceAsText(action, "{player}", source.getDisplayName());
            }
        }
    }

    public void testJoinActions(ServerCommandSource source) {
        source.sendFeedback(new LiteralText("Join actions:").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        for (String action : joinActions.actions) {
            try {
                ServerPlayerEntity player = source.getPlayer();
                Text text = action.charAt(0) == '/' ? new LiteralText(Utils.replace(action, player)) : Utils.replaceAsText(action, player);
                Utils.sendMessage(source, text);
            } catch (CommandSyntaxException e) {
                Text text = action.charAt(0) == '/' ? new LiteralText(Utils.replace(action, "{player}", source.getName())) : Utils.replaceAsText(action, "{player}", source.getDisplayName());
            }
        }
    }

    public void testLeaveActions(ServerCommandSource source) {
        source.sendFeedback(new LiteralText("Leave actions:").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        for (String action : leaveActions.actions) {
            try {
                ServerPlayerEntity player = source.getPlayer();
                Text text = action.charAt(0) == '/' ? new LiteralText(Utils.replace(action, player)) : Utils.replaceAsText(action, player);
                Utils.sendMessage(source, text);
            } catch (CommandSyntaxException e) {
                Text text = action.charAt(0) == '/' ? new LiteralText(Utils.replace(action, "{player}", source.getName())) : Utils.replaceAsText(action, "{player}", source.getDisplayName());
            }
        }
    }

    public void testKillPlayerActions(ServerCommandSource source) {
        source.sendFeedback(new LiteralText("Kill player actions:").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        for (String action : killPlayerActions.actions) {
            try {
                ServerPlayerEntity player = source.getPlayer();
                MutableText text;
                if (action.charAt(0) == '/') {
                    String string = Utils.replace(action, player);
                    string = Utils.replace(string, "{killedPlayer}", player.getName().asString());
                    text = new LiteralText(string);
                } else {
                    text = Utils.replaceAsText(action, player);
                    text = Utils.replaceAsText(text, "{killedPlayer}", player.getDisplayName());
                }
                Utils.sendMessage(source, text);
            } catch (CommandSyntaxException e) {
                MutableText text;
                if (action.charAt(0) == '/') {
                    String string = Utils.replace(action, "{player}", source.getName());
                    string = Utils.replace(string, "{killedPlayer}", source.getName());
                    text = new LiteralText(string);
                } else {
                    text = Utils.replaceAsText(action, "{player}", source.getDisplayName());
                    text = Utils.replaceAsText(text, "{killedPlayer}", source.getDisplayName());
                }
                Utils.sendMessage(source, text);
            }
        }
    }

    public void testKillEntityActions(ServerCommandSource source) {
        source.sendFeedback(new LiteralText("Kill entity actions:").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        for (String action : killPlayerActions.actions) {
            try {
                ServerPlayerEntity player = source.getPlayer();
                MutableText text;
                if (action.charAt(0) == '/') {
                    String string = Utils.replace(action, player);
                    string = Utils.replace(string, "{killedPlayer}", "dummyEntity");
                    text = new LiteralText(string);
                } else {
                    text = Utils.replaceAsText(action, player);
                    text = Utils.replaceAsText(text, "{killedPlayer}", new LiteralText("dummyEntity"));
                }
                Utils.sendMessage(source, text);
            } catch (CommandSyntaxException e) {
                MutableText text;
                if (action.charAt(0) == '/') {
                    String string = Utils.replace(action, "{player}", source.getName());
                    string = Utils.replace(string, "{killedPlayer}", "dummyEntity");
                    text = new LiteralText(string);
                } else {
                    text = Utils.replaceAsText(action, "{player}", source.getDisplayName());
                    text = Utils.replaceAsText(text, "{killedPlayer}", new LiteralText("dummyEntity"));
                }
                Utils.sendMessage(source, text);
            }
        }
    }

    public void testEveryActionGroup(ServerCommandSource source) {
        testDeathActions(source);
        testJoinActions(source);
        testLeaveActions(source);
        testKillPlayerActions(source);
        testKillEntityActions(source);
    }

    public class Actions {
        private final String[] actions;
        private final boolean broadcastToEveryone;

        public Actions() {
            this.actions = new String[] {};
            this.broadcastToEveryone = true;
        }
    }
}
