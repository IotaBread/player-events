package me.bymartrixx.playerevents.api.mixin;

import me.bymartrixx.playerevents.api.event.CommandExecutionCallback;
import me.bymartrixx.playerevents.api.event.PlayerLeaveCallback;
import net.minecraft.network.packet.c2s.play.ChatCommandC2SPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.unmapped.C_mofgmouu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(ServerPlayNetworkHandler.class)
@SuppressWarnings({"InvalidInjectorMethodSignature", "MixinAnnotationTarget", "UnresolvedMixinReference"})
public class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    // Just after the command is executed
    // 1.19
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;checkForSpam()V"),
            method = "onChatCommand", locals = LocalCapture.CAPTURE_FAILSOFT, require = 0)
    private void onCommandExecuted(ChatCommandC2SPacket packet, CallbackInfo ci, ServerCommandSource source) {
        CommandExecutionCallback.EVENT.invoker().onExecuted(packet.command(), source);
    }

    // 1.19.1/2
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;checkForSpam()V"),
            method = "method_44356", require = 0)
    private void onCommandExecuted(ChatCommandC2SPacket packet, CallbackInfo ci) {
        CommandExecutionCallback.EVENT.invoker().onExecuted(packet.command(), this.player.getCommandSource());
    }

    // 1.19.3
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;checkForSpam()V"),
            method = "m_bbegevsu", require = 0)
    private void onCommandExecuted(ChatCommandC2SPacket packet, Optional<C_mofgmouu> optional, CallbackInfo ci) {
        CommandExecutionCallback.EVENT.invoker().onExecuted(packet.command(), this.player.getCommandSource());
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;onDisconnect()V"), method = "onDisconnected")
    private void onPlayerLeave(Text reason, CallbackInfo info) {
        PlayerLeaveCallback.EVENT.invoker().leaveServer(this.player, this.player.getServer());
    }
}
