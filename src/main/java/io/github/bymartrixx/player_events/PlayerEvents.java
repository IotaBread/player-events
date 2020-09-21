package io.github.bymartrixx.player_events;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.bymartrixx.player_events.api.event.PlayerDeathCallback;
import io.github.bymartrixx.player_events.api.event.PlayerJoinCallback;
import io.github.bymartrixx.player_events.api.event.PlayerLeaveCallback;
import io.github.bymartrixx.player_events.config.PlayerEventsConfigManager;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.util.ActionResult;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerEvents implements DedicatedServerModInitializer {

    public static Logger LOGGER = LogManager.getLogger();
    public static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();

    public static final String MOD_ID = "player_events";
    public static final String MOD_NAME = "Player Events";

    @Override
    public void onInitializeServer() {
        log(Level.INFO, "Initializing");

        PlayerEventsConfigManager.loadConfig();

        PlayerDeathCallback.EVENT.register((player, source) -> {
            PlayerEventsConfigManager.getConfig().sendDeathMessages(player);
            return ActionResult.PASS;
        });

        PlayerJoinCallback.EVENT.register((player, server) -> {
            PlayerEventsConfigManager.getConfig().sendJoinMessages(server, player);
            return ActionResult.PASS;
        });

        PlayerLeaveCallback.EVENT.register(((player, server) -> {
            PlayerEventsConfigManager.getConfig().sendLeaveMessages(server, player);
            return ActionResult.PASS;
        }));
    }

    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }

}