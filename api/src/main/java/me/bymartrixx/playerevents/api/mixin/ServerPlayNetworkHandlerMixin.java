package me.bymartrixx.playerevents.api.mixin;

import me.bymartrixx.playerevents.api.event.CommandExecutionCallback;
import me.bymartrixx.playerevents.api.event.PlayerLeaveCallback;
import net.minecraft.network.packet.c2s.play.ChatCommandC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
//don't want to suppress those warnings, custom commands actually don't work
public class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    // Just after the command is executed
    // 1.20
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;submit(Ljava/lang/Runnable;)Ljava/util/concurrent/CompletableFuture;", shift = At.Shift.AFTER),
            method = "onChatCommand(Lnet/minecraft/network/packet/c2s/play/ChatCommandC2SPacket;)V")
    private void onCommandExecuted(ChatCommandC2SPacket packet, CallbackInfo ci) {
        CommandExecutionCallback.EVENT.invoker().onExecuted(packet.command(), this.player.getCommandSource());
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/network/listener/AbstractServerPacketHandler;onDisconnected(Lnet/minecraft/text/Text;)V"), method = "onDisconnected")
    private void onPlayerLeave(Text reason, CallbackInfo info) {
        PlayerLeaveCallback.EVENT.invoker().leaveServer(this.player, this.player.getServer());
    }
}
