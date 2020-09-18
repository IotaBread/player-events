package io.github.bymartrixx.player_events.api.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.bymartrixx.player_events.api.event.PlayerLeaveCallback;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

@Mixin(ServerPlayNetworkHandler.class)
public class PlayerLeaveMixin {
    private ServerPlayerEntity player;

    public PlayerLeaveMixin(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player) {
        this.player = player;
        new ServerPlayNetworkHandler(server, connection, player);
    }
    
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;onDisconnect()V"), method = "onDisconnected", cancellable = true)
    private void onPlayerLeave(Text reason, CallbackInfo info) {
        ActionResult result = PlayerLeaveCallback.EVENT.invoker().leave(this.player, this.player.getServer());

        if (result == ActionResult.FAIL) {
            info.cancel();
        }
    }
}
