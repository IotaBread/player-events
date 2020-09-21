package io.github.bymartrixx.player_events.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import io.github.bymartrixx.player_events.PlayerEvents;
import net.fabricmc.loader.api.FabricLoader;

public class PlayerEventsConfigManager {
    private static File configFile;
    private static PlayerEventsConfig config;

    public static void prepareConfigFile() {
        if (configFile != null) {
            return;
        }
        configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), PlayerEvents.MOD_ID + ".json");
    }

    public static void createConfigFile() {
        config = new PlayerEventsConfig();

        saveConfig();
    }

    public static void saveConfig() {
        prepareConfigFile();

        String jsonString = PlayerEvents.GSON.toJson(config);
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
                    config = savedConfig;
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Couldn't load configuration for Player Events. Reverting to default.");
            e.printStackTrace();
            createConfigFile();
        }
    }

    public static PlayerEventsConfig getConfig() {
        loadConfig();

        return config;
    }
}
