package io.github.bymartrixx.join_messages.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import io.github.bymartrixx.join_messages.JoinMessages;
import net.fabricmc.loader.api.FabricLoader;

public class JoinMessagesConfigManager {
    private static File configFile;
    private static JoinMessagesConfig config;

    public static void prepareConfigFile() {
        if (configFile != null) {
            return;
        }
        configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), JoinMessages.MOD_ID + ".json");
    }

    public static void createConfigFile() {
        config = new JoinMessagesConfig();

        saveConfig();        
    }

    public static void saveConfig() {
        prepareConfigFile();

        String jsonString = JoinMessages.GSON.toJson(config);
        try (FileWriter fileWriter = new FileWriter(configFile)) {
            fileWriter.write(jsonString);
        } catch (IOException e) {
            System.err.println("Couldn't save Join Messages config.");
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

                JoinMessagesConfig savedConfig = JoinMessages.GSON.fromJson(bReader, JoinMessagesConfig.class);
                if (savedConfig != null) {
                    config = savedConfig;
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Couldn't load configuration for Join Messages. Reverting to default.");
            e.printStackTrace();
            createConfigFile();
        }
    }

    public static JoinMessagesConfig getConfig() {
        loadConfig();

        return config;
    }
}