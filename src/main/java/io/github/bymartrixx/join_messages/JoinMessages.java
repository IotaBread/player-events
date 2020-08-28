package io.github.bymartrixx.join_messages;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.CallbackI.S;

import io.github.bymartrixx.join_messages.config.JoinMessagesConfigManager;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

public class JoinMessages implements DedicatedServerModInitializer {
    public static final String MOD_ID = "join_messages";
    public static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();
    public static final Logger LOGGER = LogManager.getLogger();
    CommandDispatcher<S> dispatcher = new CommandDispatcher<>();

    @Override
    public void onInitializeServer() {
        JoinMessagesConfigManager.loadConfig();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            if (dedicated) {
                dispatcher.register(CommandManager.literal(MOD_ID).requires((ServerCommandSource) -> {
                    return ServerCommandSource.hasPermissionLevel(2);
                }).then(CommandManager.literal("reload")
                    .executes(context -> {
                        ServerCommandSource source = context.getSource();
                        source.sendFeedback(new LiteralText("Reloading Join Messages config..."), true);
                        JoinMessagesConfigManager.loadConfig();

                        return 1;
                    })
                ));
            }
        });
        
    }
    
}