package me.bymartrixx.playerevents.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import me.bymartrixx.playerevents.PlayerEvents;
import me.bymartrixx.playerevents.mixin.CommandFunctionManagerAccessor;
import me.bymartrixx.playerevents.util.Utils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static me.bymartrixx.playerevents.util.PlaceholderReplacingUtil.lazyResolver;

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
            saveConfig();
        }

        public static void saveConfig() {
            prepareConfigFile();

            String jsonString = PlayerEvents.GSON.toJson(PlayerEvents.CONFIG);
            try (FileWriter fileWriter = new FileWriter(configFile)) {
                fileWriter.write(jsonString);
            } catch (IOException e) {
                PlayerEvents.LOGGER.error("Couldn't save Player Events config.", e);
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
                        PlayerEvents.CONFIG.load(savedConfig);
                    }
                }
            } catch (FileNotFoundException e) {
                PlayerEvents.LOGGER.error("Couldn't load configuration for Player Events. Reverting to default.", e);
                createConfigFile();
            }
        }
    }

    private final ActionList firstDeath;
    private final ActionList death;
    private final ActionList firstJoin;
    private final ActionList join;
    private final ActionList killEntity;
    private final ActionList killPlayer;
    private final ActionList leave;
    private final List<CustomCommandActionList> customCommands;

    public PlayerEventsConfig() {
        this.firstDeath = new ActionList();
        this.death = new ActionList();
        this.firstJoin = new ActionList();
        this.join = new ActionList();
        this.killEntity = new ActionList();
        this.killPlayer = new ActionList();
        this.leave = new ActionList();
        this.customCommands = Lists.newArrayList();
    }

    private static void doSimpleAction(ActionList actionList, ServerPlayerEntity player) {
        Map<String, Object> placeholderArgs = playerPlaceholder(player);

        List<String> actions;
        if (actionList.pickMessageRandomly()) {
            actions = actionList.getCommandActions();
            List<String> messageActions = actionList.getMessageActions();
            String message = messageActions.get(player.getRandom().nextInt(messageActions.size()));
            actions.add(message);
        } else {
            actions = actionList.actions;
        }

        for (String action : actions) {
            doAction(action, player, placeholderArgs, actionList.doBroadcastToEveryone());
        }
    }

    private static void doSimpleAction(ActionList actionList, ServerPlayerEntity player,
                                       MinecraftServer server) {
        Map<String, Object> placeholderArgs = playerPlaceholder(player);

        List<String> actions;
        if (actionList.pickMessageRandomly()) {
            actions = actionList.getCommandActions();
            List<String> messageActions = actionList.getMessageActions();
            String message = messageActions.get(player.getRandom().nextInt(messageActions.size()));
            actions.add(message);
        } else {
            actions = actionList.actions;
        }

        for (String action : actions) {
            doAction(action, player, server, placeholderArgs, actionList.doBroadcastToEveryone());
        }
    }

    private static void doAction(String action, ServerPlayerEntity player,
                                 Map<String, ?> placeholderArgs, boolean broadcast) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return; // Shouldn't happen
        }

        doAction(action, player, server, placeholderArgs, broadcast);
    }

    private static void doAction(String action, ServerPlayerEntity player, MinecraftServer server,
                                 Map<String, ?> placeholderArgs, boolean broadcast) {
        action = action.trim();

        Text message = Utils.parseAndReplace(action, player, placeholderArgs);

        if (action.startsWith("/")) {
            String command = message.getString();
            server.getCommandManager().executePrefixedCommand(server.getCommandSource(), command);
        } else {
            Utils.message(player, message, broadcast);
        }
    }

    private static void executeFunctions(String id, MinecraftServer server) {
        Identifier tag = new Identifier("player_events", id);
        CommandFunctionManager commandFunctionManager = server.getCommandFunctionManager();
        Collection<CommandFunction> functions = ((CommandFunctionManagerAccessor) commandFunctionManager).getFunctionLoader().getTagOrEmpty(tag);
        ((CommandFunctionManagerAccessor) commandFunctionManager).invokeExecuteAll(functions, tag);
    }

    public static void testSimpleAction(ActionList actionList, ServerCommandSource source, String title) {
        String message = String.format(title, actionList.doBroadcastToEveryone() ? "Send to everyone" : "Send only to the player");
        if (actionList.pickMessageRandomly()) {
            message = message + " [Message picked randomly]";
        }
        source.sendFeedback(Text.literal("" + Formatting.GRAY + Formatting.ITALIC + message), false);

        Map<String, Object> placeholderArgs = playerPlaceholder(source);

        for (String action : actionList.actions) {
            testAction(action, source, placeholderArgs);
        }
    }

    public static void testAction(String action, ServerCommandSource source,
                                  Map<String, Object> placeholderArgs) {
        action = action.trim();

        Text message = Utils.parseAndReplace(action, source, placeholderArgs);

        if (action.startsWith("/")) {
            String command = message.getString();
            source.sendFeedback(Text.literal("[COMMAND] " + command), false);
        } else {
            source.sendFeedback(message, false);
        }
    }

    private static Map<String, Object> playerPlaceholder(Object player) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("player", player);
        return map;
    }

    private void load(PlayerEventsConfig newConfig) {
        this.firstDeath.load(newConfig.firstDeath);
        this.death.load(newConfig.death);
        this.firstJoin.load(newConfig.firstJoin);
        this.join.load(newConfig.join);
        this.killEntity.load(newConfig.killEntity);
        this.killPlayer.load(newConfig.killPlayer);
        this.leave.load(newConfig.leave);

        this.customCommands.clear();
        for (CustomCommandActionList customCommand : newConfig.customCommands) {
            if (customCommand.getCommand().isEmpty()) {
                PlayerEvents.LOGGER.warn("Found an invalid empty custom command");
            } else {
                this.customCommands.add(customCommand);
            }
        }
    }

    public List<String> getFirstDeathActions() {
        return this.firstDeath.actions;
    }

    public List<String> getDeathActions() {
        return this.death.actions;
    }

    public List<String> getFirstJoinActions() {
        return this.firstJoin.actions;
    }

    public List<String> getJoinActions() {
        return this.join.actions;
    }

    public List<String> getKillEntityActions() {
        return this.killEntity.actions;
    }

    public List<String> getKillPlayerActions() {
        return this.killPlayer.actions;
    }

    public List<String> getLeaveActions() {
        return this.leave.actions;
    }

    public void doFirstDeathActions(ServerPlayerEntity player) {
        doSimpleAction(this.firstDeath, player);
        executeFunctions("first_death", player.getServer());
    }

    public void doDeathActions(ServerPlayerEntity player) {
        doSimpleAction(this.death, player);
        executeFunctions("death", player.getServer());
    }

    public void doFirstJoinActions(ServerPlayerEntity player, MinecraftServer server) {
        doSimpleAction(this.firstJoin, player, server);
        executeFunctions("first_join", server);
    }

    public void doJoinActions(ServerPlayerEntity player, MinecraftServer server) {
        doSimpleAction(this.join, player, server);
        executeFunctions("join", server);
    }

    public void doLeaveActions(ServerPlayerEntity player, MinecraftServer server) {
        doSimpleAction(this.leave, player, server);
        executeFunctions("leave", server);
    }

    public void doKillEntityActions(ServerPlayerEntity player, Entity killedEntity) {
        Map<String, Object> placeholderArgs = playerPlaceholder(player);
        placeholderArgs.put("killedEntity", killedEntity);

        for (String action : this.killEntity.actions) {
            doAction(action, player, placeholderArgs, this.killEntity.doBroadcastToEveryone());
        }

        executeFunctions("kill_entity", player.getServer());
    }

    public void doKillPlayerActions(ServerPlayerEntity player, ServerPlayerEntity killedPlayer) {
        Map<String, Object> placeholderArgs = playerPlaceholder(player);
        placeholderArgs.put("killedPlayer", killedPlayer);

        for (String action : this.killPlayer.actions) {
            doAction(action, player, placeholderArgs, this.killPlayer.doBroadcastToEveryone());
        }

        executeFunctions("kill_player", player.getServer());
    }

    public void doCustomCommandsActions(String command, ServerCommandSource source) {
        // Run actions from the event to allow editing the actions without restarting the server
        for (CustomCommandActionList customCommand : this.customCommands) {
            if (command.trim().equals(customCommand.getCommand())) {
                try {
                    doSimpleAction(customCommand, source.getPlayer());
                } catch (CommandSyntaxException e) {
                    PlayerEvents.LOGGER.error("This should not happen, please report it to the mod author, attaching the logs", e);
                }
            }
        }
    }

    public void testFirstDeathActions(ServerCommandSource source) {
        testSimpleAction(this.firstDeath, source, "First death actions (%s):");
    }

    public void testDeathActions(ServerCommandSource source) {
        testSimpleAction(this.death, source, "Death actions (%s):");
    }

    public void testFirstJoinActions(ServerCommandSource source) {
        testSimpleAction(this.firstJoin, source, "First time join actions (%s):");
    }

    public void testJoinActions(ServerCommandSource source) {
        testSimpleAction(this.join, source, "Join actions (%s):");
    }

    public void testLeaveActions(ServerCommandSource source) {
        testSimpleAction(this.leave, source, "Leave actions:");
    }

    public void testKillEntityActions(ServerCommandSource source) {
        String message = String.format("Kill entity actions (%s):", this.killEntity.doBroadcastToEveryone() ? "Send to everyone" : "Send only to the player");
        source.sendFeedback(Text.literal("" + Formatting.GRAY + Formatting.ITALIC + message), false);

        Map<String, Object> placeholderArgs = playerPlaceholder(source);
        placeholderArgs.put("killedEntity", lazyResolver((subKey) -> switch (subKey) {
            case "" -> Text.literal("dummyEntity");
            case "display" -> Text.literal("Dummy entity");
            case "entityName" -> Text.literal("Dummy uuid");
            case "x", "y", "z" -> Text.literal("0.0");
            default -> null;
        }));

        for (String action : this.killEntity.actions) {
            testAction(action, source, placeholderArgs);
        }
    }

    public void testKillPlayerActions(ServerCommandSource source) {
        String message = String.format("Kill player actions (%s):", this.killPlayer.doBroadcastToEveryone() ? "Send to everyone" : "Send only to the player");
        source.sendFeedback(Text.literal("" + Formatting.GRAY + Formatting.ITALIC + message), false);

        Map<String, Object> placeholderArgs = playerPlaceholder(source);
        placeholderArgs.put("killedPlayer", source);

        for (String action : this.killPlayer.actions) {
            testAction(action, source, placeholderArgs);
        }
    }

    public void testCustomCommandsActions(ServerCommandSource source) {
        Map<String, Object> placeholderArgs = playerPlaceholder(source);

        for (CustomCommandActionList actionList : this.customCommands) {
            String message = String.format("'%s' actions ('%s'):", actionList.getCommandStr(), actionList.doBroadcastToEveryone() ? "Send to everyone" : "Send only to the player");
            source.sendFeedback(Text.literal("§7§o" + message), false);

            for (String action : actionList.actions) {
                testAction(action, source, placeholderArgs);
            }
        }
    }

    public void testEveryActionGroup(ServerCommandSource source) {
        this.testFirstDeathActions(source);
        this.testDeathActions(source);
        this.testFirstJoinActions(source);
        this.testJoinActions(source);
        this.testKillEntityActions(source);
        this.testKillPlayerActions(source);
        this.testLeaveActions(source);
        this.testCustomCommandsActions(source);
    }

    public void registerCustomCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        CommandNode<ServerCommandSource> root = dispatcher.getRoot();
        for (CustomCommandActionList customCommand : this.customCommands) {
            CommandNode<ServerCommandSource> node = root.getChild(customCommand.getCommand());
            if (node == null) {
                dispatcher.register(CommandManager.literal(customCommand.getCommand()).executes(this::executeCommand));
            }
        }
    }

    private int executeCommand(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        if (!(source.getEntity() instanceof ServerPlayerEntity)) {
            source.sendFeedback(Text.literal("This command can only be executed by players"), false);
        }

        return 1;
    }

    public static class ActionList {
        protected final List<String> actions;
        protected boolean broadcastToEveryone;
        protected boolean pickMessageRandomly = false;

        public ActionList() {
            this.actions = Lists.newArrayList();
            this.broadcastToEveryone = true;
        }

        protected void load(ActionList list) {
            this.actions.clear();
            this.actions.addAll(list.actions);
            this.broadcastToEveryone = list.broadcastToEveryone;
            this.pickMessageRandomly = list.pickMessageRandomly;
        }

        public boolean doBroadcastToEveryone() {
            return this.broadcastToEveryone;
        }

        public boolean pickMessageRandomly() {
            return this.pickMessageRandomly;
        }

        public List<String> getCommandActions() {
            List<String> commandActions = Lists.newArrayList();
            for (String action : this.actions) {
                if (action.startsWith("/")) {
                    commandActions.add(action);
                }
            }
            return commandActions;
        }

        public List<String> getMessageActions() {
            List<String> messageActions = Lists.newArrayList();
            for (String action : this.actions) {
                if (!action.startsWith("/")) {
                    messageActions.add(action);
                }
            }
            return messageActions;
        }
    }

    public static class CustomCommandActionList extends ActionList {
        protected String command;

        public CustomCommandActionList() {
            super();
            this.command = "";
        }

        public String getCommandStr() {
            return this.command.startsWith("/") ? this.command : "/" + this.command;
        }

        public String getCommand() {
            return !this.command.startsWith("/") ? this.command : this.command.substring(1);
        }
    }
}
