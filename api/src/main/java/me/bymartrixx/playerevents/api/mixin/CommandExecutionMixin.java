package me.bymartrixx.playerevents.api.mixin;

import me.bymartrixx.playerevents.api.event.CommandExecutionCallback;
import net.minecraft.network.packet.c2s.play.ChatCommandC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerPlayNetworkHandler.class)
public class CommandExecutionMixin {
    @Shadow public ServerPlayerEntity player;

    // Just after the command is executed
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;checkForSpam()V"),
            method = "onChatCommand", locals = LocalCapture.PRINT)
    private void onCommandExecuted(ChatCommandC2SPacket packet, CallbackInfo ci) {
        // TODO: Update locals to include the source
        // CommandExecutionCallback.EVENT.invoker().onExecuted(packet.command(), source);
    }
}
