package me.bymartrixx.playerevents;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import me.bymartrixx.playerevents.api.event.CommandExecutionCallback;
import me.bymartrixx.playerevents.api.event.PlayerDeathCallback;
import me.bymartrixx.playerevents.api.event.PlayerFirstJoinCallback;
import me.bymartrixx.playerevents.api.event.PlayerJoinCallback;
import me.bymartrixx.playerevents.api.event.PlayerKillEntityCallback;
import me.bymartrixx.playerevents.api.event.PlayerKillPlayerCallback;
import me.bymartrixx.playerevents.api.event.PlayerLeaveCallback;
import me.bymartrixx.playerevents.command.PlayerEventsCommand;
import me.bymartrixx.playerevents.config.PlayerEventsConfig;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerEvents implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();
    private static int placeholderApiLoadStatus = -1; // -1 = not checked | 0 = not loaded | 1 = loaded

    public static final String MOD_ID = "player_events";
    public static final PlayerEventsConfig CONFIG = new PlayerEventsConfig();

    @Override
    public void onInitializeServer() {
        try {
            PlayerEventsConfig.Manager.loadConfig();
        } catch (JsonSyntaxException e) {
            LOGGER.error("Invalid JSON syntax in the config file", e);
        }

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            PlayerEventsCommand.register(dispatcher);
            CONFIG.registerCustomCommands(dispatcher);
        });

        PlayerDeathCallback.EVENT.register((player, source) -> CONFIG.doDeathActions(player));

        PlayerFirstJoinCallback.EVENT.register(CONFIG::doFirstJoinActions);

        PlayerJoinCallback.EVENT.register(CONFIG::doJoinActions);

        PlayerLeaveCallback.EVENT.register(CONFIG::doLeaveActions);

        PlayerKillEntityCallback.EVENT.register(CONFIG::doKillEntityActions);

        PlayerKillPlayerCallback.EVENT.register(CONFIG::doKillPlayerActions);

        CommandExecutionCallback.EVENT.register(CONFIG::doCustomCommandsActions);
    }

    public static boolean isPlaceholderApiLoaded() {
        if (placeholderApiLoadStatus == -1) {
            placeholderApiLoadStatus = FabricLoader.getInstance().isModLoaded("placeholder-api") ? 1 : 0;
        }

        return placeholderApiLoadStatus == 1;
    }
}
