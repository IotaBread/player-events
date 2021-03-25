package io.github.bymartrixx.playerevents.config;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.bymartrixx.playerevents.PlayerEvents;
import io.github.bymartrixx.playerevents.Utils;
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
import java.util.function.Consumer;
import java.util.function.Function;

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

    private final Actions death;
    private final Actions join;
    private final Actions killEntity;
    private final Actions killPlayer;
    private final Actions leave;

    public PlayerEventsConfig() {
        this.death = new Actions();
        this.join = new Actions();
        this.killPlayer = new Actions();
        this.killEntity = new Actions();
        this.leave = new Actions();
    }

    private static void executeAction(String action, Consumer<String> command, Consumer<String> message) {
        String action1 = action.trim();
        if (action1.startsWith("/")) {
            command.accept(action1);
        } else {
            message.accept(action1);
        }
    }

    private static void executeBasicAction(String action, MinecraftServer server, ServerPlayerEntity player, boolean sendToEveryone) {
        executeAction(action,
                action1 -> server.getCommandManager().execute(server.getCommandSource(), Utils.replace(action1, player)),
                action1 -> Utils.sendMessage(server, player, Utils.replaceGetText(action1, player), sendToEveryone));
    }

    private static void executeBasicAction(String action, MinecraftServer server, ServerPlayerEntity player) {
        executeBasicAction(action, server, player, true);
    }

    private static Text testAction(String action, Function<String, Text> command, Function<String, Text> message) {
        String action1 = action.trim();
        if (action1.startsWith("/")) {
            return command.apply(action1);
        } else {
            return message.apply(action1);
        }
    }

    private static Text testBasicAction(String action, ServerCommandSource source) {
        return testAction(action, action1 -> {
            try {
                ServerPlayerEntity player = source.getPlayer();
                return new LiteralText(Utils.replace(action1, player));
            } catch (CommandSyntaxException e) {
                return new LiteralText(Utils.replace(action1, "${player}", source.getName()));
            }
        }, action1 -> {
            try {
                ServerPlayerEntity player = source.getPlayer();
                return Utils.replaceGetText(action1, player);
            } catch (CommandSyntaxException e) {
                return Utils.replaceGetText(action, "${player}", source.getDisplayName());
            }
        });
    }

    public String[] getDeathActions() {
        return this.death.actions;
    }

    public String[] getJoinActions() {
        return this.join.actions;
    }

    public String[] getKillEntityActions() {
        return this.killEntity.actions;
    }

    public String[] getKillPlayerActions() {
        return this.killPlayer.actions;
    }

    public String[] getLeaveActions() {
        return this.leave.actions;
    }

    public void runDeathActions(ServerPlayerEntity player) {
        // TODO: add ${source}?
        for (String action : this.death.actions) {
            MinecraftServer server = player.getServer();
            if (server == null) {
                return;
            }

            executeBasicAction(action, server, player, this.death.broadcastToEveryone);
        }
    }

    public void runJoinActions(MinecraftServer server, ServerPlayerEntity player) {
        for (String action : this.join.actions) {
            executeBasicAction(action, server, player, this.join.broadcastToEveryone);
        }
    }

    public void runLeaveActions(MinecraftServer server, ServerPlayerEntity player) {
        for (String action : this.leave.actions) {
            executeBasicAction(action, server, player);
        }
    }

    public void runKillEntityActions(MinecraftServer server, ServerPlayerEntity player, Entity killedEntity) {
        for (String action : this.killEntity.actions) {
            executeAction(action, action1 -> {
                String command = Utils.replace(action1, player);
                command = Utils.replace(command, "${killedEntity}", killedEntity.getName().asString());
                server.getCommandManager().execute(server.getCommandSource(), command);
            }, action1 -> {
                MutableText text = Utils.replaceGetText(action1, new String[]{"${player}", "${killedEntity}"}, new Text[]{player.getDisplayName(), killedEntity.getDisplayName()});
                Utils.sendMessage(server, player, text, this.killEntity.broadcastToEveryone);
            });
        }
    }

    public void runKillPlayerActions(MinecraftServer server, ServerPlayerEntity player, ServerPlayerEntity killedPlayer) {
        for (String action : this.killPlayer.actions) {
            executeAction(action, action1 -> {
                String command = Utils.replace(action1, player);
                command = Utils.replace(command, "${killedPlayer}", killedPlayer.getName().asString());
                server.getCommandManager().execute(server.getCommandSource(), command);
            }, action1 -> {
                MutableText text = Utils.replaceGetText(action1, new String[]{"${player}", "${killedPlayer}"}, new Text[]{player.getDisplayName(), killedPlayer.getDisplayName()});
                Utils.sendMessage(server, player, text, this.killPlayer.broadcastToEveryone);
            });
        }
    }

    public void testDeathActions(ServerCommandSource source) {
        String message = "Death actions (" + (this.death.broadcastToEveryone ? "Send to everyone" : "Send only to the player") + "):";
        source.sendFeedback(new LiteralText(message).formatted(Formatting.GRAY, Formatting.ITALIC), false);

        for (String action : this.death.actions) {
            Utils.sendMessage(source, testBasicAction(action, source));
        }
    }

    public void testJoinActions(ServerCommandSource source) {
        String message = "Join actions (" + (this.join.broadcastToEveryone ? "Send to everyone" : "Send only to the player") + "):";
        source.sendFeedback(new LiteralText(message).formatted(Formatting.GRAY, Formatting.ITALIC), false);

        for (String action : this.join.actions) {
            Utils.sendMessage(source, testBasicAction(action, source));
        }
    }

    public void testKillEntityActions(ServerCommandSource source) {
        String message = "Kill entity actions (" + (killEntity.broadcastToEveryone ? "Send to everyone" : "Send only to the player") + "):";
        source.sendFeedback(new LiteralText(message).formatted(Formatting.GRAY, Formatting.ITALIC), false);

        for (String action : this.killEntity.actions) {
            Utils.sendMessage(source, testAction(action, action1 -> {
                String command;
                try {
                    ServerPlayerEntity player = source.getPlayer();
                    command = Utils.replace(action1, player);
                } catch (CommandSyntaxException e) {
                    command = Utils.replace(action1, "${player}", source.getName());
                }

                command = Utils.replace(command, "${killedEntity}", "dummyEntity");

                return new LiteralText(command);
            }, action1 -> {
                try {
                    ServerPlayerEntity player = source.getPlayer();
                    return Utils.replaceGetText(action1, new String[]{"${player}", "${killedEntity}"}, new Text[]{player.getDisplayName(), new LiteralText("dummyEntity")});
                } catch (CommandSyntaxException e) {
                    return Utils.replaceGetText(action1, new String[]{"${player}", "${killedEntity}"}, new Text[]{source.getDisplayName(), new LiteralText("dummyEntity")});
                }
            }));
        }
    }

    public void testKillPlayerActions(ServerCommandSource source) {
        String message = "Kill player actions (" + (killPlayer.broadcastToEveryone ? "Send to everyone" : "Send only to the player") + "):";
        source.sendFeedback(new LiteralText(message).formatted(Formatting.GRAY, Formatting.ITALIC), false);

        for (String action : this.killPlayer.actions) {
            Utils.sendMessage(source, testAction(action, action1 -> {
                String command;
                try {
                    ServerPlayerEntity player = source.getPlayer();
                    command = Utils.replace(action1, player);
                    command = Utils.replace(command, "${killedPlayer}", player.getName().asString());
                } catch (CommandSyntaxException e) {
                    command = Utils.replace(action1, "${player}", source.getName());
                    command = Utils.replace(command, "${killedPlayer}", source.getName());
                }

                return new LiteralText(command);
            }, action1 -> {
                try {
                    ServerPlayerEntity player = source.getPlayer();
                    return Utils.replaceGetText(action, new String[]{"${player}", "${killedPlayer}"}, new Text[]{player.getDisplayName(), player.getDisplayName()});
                } catch (CommandSyntaxException e) {
                    return Utils.replaceGetText(action, new String[]{"${player}", "${killedPlayer}"}, new Text[]{source.getDisplayName(), source.getDisplayName()});
                }
            }));
        }
    }

    public void testLeaveActions(ServerCommandSource source) {
        String message = "Leave actions:";
        source.sendFeedback(new LiteralText(message).formatted(Formatting.GRAY, Formatting.ITALIC), false);

        for (String action : this.leave.actions) {
            Utils.sendMessage(source, testBasicAction(action, source));
        }
    }

    public void testEveryActionGroup(ServerCommandSource source) {
        this.testDeathActions(source);
        this.testJoinActions(source);
        this.testKillEntityActions(source);
        this.testKillPlayerActions(source);
        this.testLeaveActions(source);
    }

    public static class Actions {
        private final String[] actions;
        private final boolean broadcastToEveryone;

        public Actions() {
            this.actions = new String[] {};
            this.broadcastToEveryone = true;
        }
    }
}
