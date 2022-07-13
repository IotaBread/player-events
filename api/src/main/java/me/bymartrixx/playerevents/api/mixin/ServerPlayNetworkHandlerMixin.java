package me.bymartrixx.playerevents.api.mixin;

import me.bymartrixx.playerevents.api.event.CommandExecutionCallback;
import me.bymartrixx.playerevents.api.event.PlayerLeaveCallback;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(at = @At(value = "TAIL"), method = "executeCommand")
    private void onCommandExecuted(String input, CallbackInfo ci) {
        CommandExecutionCallback.EVENT.invoker().onExecuted(input, this.player.getCommandSource());
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;onDisconnect()V"), method = "onDisconnected")
    private void onPlayerLeave(Text reason, CallbackInfo info) {
        PlayerLeaveCallback.EVENT.invoker().leaveServer(this.player, this.player.getServer());
    }
}
