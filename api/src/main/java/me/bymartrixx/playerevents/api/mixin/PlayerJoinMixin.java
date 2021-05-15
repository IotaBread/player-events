package me.bymartrixx.playerevents.api.mixin;

import me.bymartrixx.playerevents.api.event.PlayerJoinCallback;
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
        PlayerJoinCallback.EVENT.invoker().joinServer(player, player.getServer());

        ActionResult result1 = io.github.bymartrixx.playerevents.api.event.PlayerJoinCallback.EVENT.invoker().joinServer(player, player.getServer());

        if (result1 == ActionResult.FAIL) {
            info.cancel();
        }
    }
}
