package io.github.bymartrixx.player_events.api.mixin;

import io.github.bymartrixx.player_events.api.event.PlayerLeaveCallback;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class PlayerLeaveMixin {
    @Shadow
    private ServerPlayerEntity player;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;onDisconnect()V"), method = "onDisconnected", cancellable = true)
    private void onPlayerLeave(Text reason, CallbackInfo info) {
        ActionResult result = PlayerLeaveCallback.EVENT.invoker().leave(this.player, this.player.getServer());

        if (result == ActionResult.FAIL) {
            info.cancel();
        }
    }
}
