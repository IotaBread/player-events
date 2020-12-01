package io.github.bymartrixx.player_events.api.mixin;

import io.github.bymartrixx.player_events.api.event.PlayerJoinCallback;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerJoinMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;onSpawn()V"), method = "onPlayerConnect", cancellable = true)
    private  void onPlayerJoin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
        ActionResult result = PlayerJoinCallback.EVENT.invoker().join(player, player.getServer());

        if (result == ActionResult.FAIL) {
            info.cancel();
        }
    }
}
