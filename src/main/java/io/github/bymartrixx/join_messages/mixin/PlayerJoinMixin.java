package io.github.bymartrixx.join_messages.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.bymartrixx.join_messages.config.JoinMessagesConfigManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(PlayerManager.class)
public class PlayerJoinMixin {
    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    private void playerJoin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
        JoinMessagesConfigManager.getConfig().runJoinMessages(player.getServer(), player);
    }
}