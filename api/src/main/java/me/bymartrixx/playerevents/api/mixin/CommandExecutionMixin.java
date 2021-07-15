package me.bymartrixx.playerevents.api.mixin;

import me.bymartrixx.playerevents.api.event.CommandExecutionCallback;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class CommandExecutionMixin {
    @Shadow public ServerPlayerEntity player;

    @Inject(at = @At(value = "TAIL"), method = "executeCommand")
    private void onCommandExecuted(String input, CallbackInfo ci) {
        CommandExecutionCallback.EVENT.invoker().onExecuted(input, this.player.getCommandSource());
    }
}
