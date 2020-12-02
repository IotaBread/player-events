package io.github.bymartrixx.player_events.config;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.bymartrixx.player_events.PlayerEvents;
import io.github.bymartrixx.player_events.Utils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.*;

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
        // TODO: add ${source}?
        for (String action : deathActions.actions) {
            MinecraftServer server = player.getServer();
            if (server == null)
                return;
            if (action.charAt(0) == '/') {
                server.getCommandManager().execute(server.getCommandSource(), Utils.replace(action, player));
            } else {
                Utils.sendMessage(server, Utils.replaceGetText(action, player));
            }
        }
    }

    public void runJoinActions(MinecraftServer server, ServerPlayerEntity player) {
        for (String action : joinActions.actions) {
            if (action.charAt(0) == '/') {
                server.getCommandManager().execute(server.getCommandSource(), Utils.replace(action, player));
            } else {
                Utils.sendMessage(server, Utils.replaceGetText(action, player));
            }
        }
    }

    public void runLeaveActions(MinecraftServer server, ServerPlayerEntity player) {
        for (String action : leaveActions.actions) {
            if (action.charAt(0) == '/') {
                server.getCommandManager().execute(server.getCommandSource(), Utils.replace(action, player));
            } else {
                Utils.sendMessage(server, Utils.replaceGetText(action, player));
            }
        }
    }

    public void runKillPlayerActions(MinecraftServer server, ServerPlayerEntity player, ServerPlayerEntity killedPlayer) {
        for (String action : killPlayerActions.actions) {
            if (action.charAt(0) == '/') {
                String string = Utils.replace(action, player);
                string = Utils.replace(string, "${killedPlayer}", killedPlayer.getName().asString());
                server.getCommandManager().execute(server.getCommandSource(), string);
            } else {
                MutableText text = Utils.replaceGetText(action, new String[]{"${player}", "${killedPlayer}"}, new Text[]{player.getDisplayName(), killedPlayer.getDisplayName()});
                Utils.sendMessage(server, text);
            }
        }
    }

    public void runKillEntityActions(MinecraftServer server, ServerPlayerEntity player, Entity killedEntity) {
        for (String action : killPlayerActions.actions) {
            if (action.charAt(0) == '/') {
                String string = Utils.replace(action, player);
                string = Utils.replace(string, "${killedEntity}", killedEntity.getName().asString());
                server.getCommandManager().execute(server.getCommandSource(), string);
            } else {
                MutableText text = Utils.replaceGetText(action, new String[]{"${player}", "${killedEntity}"}, new Text[]{player.getDisplayName(), killedEntity.getDisplayName()});
                Utils.sendMessage(server, text);
            }
        }
    }

    public void testDeathActions(ServerCommandSource source) {
        String message = "Death actions (" + (deathActions.broadcastToEveryone ? "Send to everyone" : "Send only to the player") + "):";
        source.sendFeedback(new LiteralText(message).formatted(Formatting.GRAY, Formatting.ITALIC), false);
        for (String action : deathActions.actions) {
            try {
                ServerPlayerEntity player = source.getPlayer();
                Text text = action.charAt(0) == '/' ? new LiteralText(Utils.replace(action, player)) : Utils.replaceGetText(action, player);
                Utils.sendMessage(source, text);
            } catch (CommandSyntaxException e) {
                Text text = action.charAt(0) == '/' ? new LiteralText(Utils.replace(action, "${player}", source.getName())) : Utils.replaceGetText(action, "${player}", source.getDisplayName());
                Utils.sendMessage(source, text);
            }
        }
    }

    public void testJoinActions(ServerCommandSource source) {
        String message = "Join actions (" + (joinActions.broadcastToEveryone ? "Send to everyone" : "Send only to the player") + "):";
        source.sendFeedback(new LiteralText(message).formatted(Formatting.GRAY, Formatting.ITALIC), false);
        for (String action : joinActions.actions) {
            try {
                ServerPlayerEntity player = source.getPlayer();
                Text text = action.charAt(0) == '/' ? new LiteralText(Utils.replace(action, player)) : Utils.replaceGetText(action, player);
                Utils.sendMessage(source, text);
            } catch (CommandSyntaxException e) {
                Text text = action.charAt(0) == '/' ? new LiteralText(Utils.replace(action, "${player}", source.getName())) : Utils.replaceGetText(action, "${player}", source.getDisplayName());
                Utils.sendMessage(source, text);
            }
        }
    }

    public void testLeaveActions(ServerCommandSource source) {
        String message = "Leave actions (" + (leaveActions.broadcastToEveryone ? "Send to everyone" : "Send only to the player") + "):";
        source.sendFeedback(new LiteralText(message).formatted(Formatting.GRAY, Formatting.ITALIC), false);
        for (String action : leaveActions.actions) {
            try {
                ServerPlayerEntity player = source.getPlayer();
                Text text = action.charAt(0) == '/' ? new LiteralText(Utils.replace(action, player)) : Utils.replaceGetText(action, player);
                Utils.sendMessage(source, text);
            } catch (CommandSyntaxException e) {
                Text text = action.charAt(0) == '/' ? new LiteralText(Utils.replace(action, "${player}", source.getName())) : Utils.replaceGetText(action, "${player}", source.getDisplayName());
                Utils.sendMessage(source, text);
            }
        }
    }

    public void testKillPlayerActions(ServerCommandSource source) {
        String message = "Kill player actions (" + (killPlayerActions.broadcastToEveryone ? "Send to everyone" : "Send only to the player") + "):";
        source.sendFeedback(new LiteralText(message).formatted(Formatting.GRAY, Formatting.ITALIC), false);
        for (String action : killPlayerActions.actions) {
            try {
                ServerPlayerEntity player = source.getPlayer();
                MutableText text;
                if (action.charAt(0) == '/') {
                    String string = Utils.replace(action, player);
                    string = Utils.replace(string, "${killedPlayer}", player.getName().asString());
                    text = new LiteralText(string);
                } else {
                    text = Utils.replaceGetText(action, new String[]{"${player}", "${killedPlayer}"}, new Text[]{player.getDisplayName(), player.getDisplayName()});
                }
                Utils.sendMessage(source, text);
            } catch (CommandSyntaxException e) {
                MutableText text;
                if (action.charAt(0) == '/') {
                    String string = Utils.replace(action, "${player}", source.getName());
                    string = Utils.replace(string, "${killedPlayer}", source.getName());
                    text = new LiteralText(string);
                } else {
                    text = Utils.replaceGetText(action, new String[]{"${player}", "${killedPlayer}"}, new Text[]{source.getDisplayName(), source.getDisplayName()});
                }
                Utils.sendMessage(source, text);
            }
        }
    }

    public void testKillEntityActions(ServerCommandSource source) {
        String message = "Kill entity actions (" + (killEntityActions.broadcastToEveryone ? "Send to everyone" : "Send only to the player") + "):";
        source.sendFeedback(new LiteralText(message).formatted(Formatting.GRAY, Formatting.ITALIC), false);
        for (String action : killEntityActions.actions) {
            try {
                ServerPlayerEntity player = source.getPlayer();
                MutableText text;
                if (action.charAt(0) == '/') {
                    String string = Utils.replace(action, player);
                    string = Utils.replace(string, "${killedPlayer}", "dummyEntity");
                    text = new LiteralText(string);
                } else {
                    text = Utils.replaceGetText(action, new String[]{"${player}", "${killedPlayer}"}, new Text[]{player.getDisplayName(), new LiteralText("dummyEntity")});
                }
                Utils.sendMessage(source, text);
            } catch (CommandSyntaxException e) {
                MutableText text;
                if (action.charAt(0) == '/') {
                    String string = Utils.replace(action, "${player}", source.getName());
                    string = Utils.replace(string, "${killedPlayer}", "dummyEntity");
                    text = new LiteralText(string);
                } else {
                    text = Utils.replaceGetText(action, new String[]{"${player}", "${killedPlayer}"}, new Text[]{source.getDisplayName(), new LiteralText("dummyEntity")});
                }
                Utils.sendMessage(source, text);
            }
        }
    }

    public void testEveryActionGroup(ServerCommandSource source) { // TODO U W U
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
