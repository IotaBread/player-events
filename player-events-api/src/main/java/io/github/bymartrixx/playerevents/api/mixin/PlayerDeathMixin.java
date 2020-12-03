package io.github.bymartrixx.playerevents.api.mixin;

import io.github.bymartrixx.playerevents.api.event.PlayerDeathCallback;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class PlayerDeathMixin {
    @Inject(at = @At(value = "TAIL", target = "Lnet/minecraft/world/World;sendEntityStatus(Lnet/minecraft/entity/Entity;B)V"), method = "onDeath", cancellable = true)
    private  void onPlayerDeath(DamageSource source, CallbackInfo info) {
        ActionResult result = PlayerDeathCallback.EVENT.invoker().kill((ServerPlayerEntity) (Object) this, source);

        if (result == ActionResult.FAIL) {
            info.cancel();
        }

        ActionResult result1 = io.github.bymartrixx.player_events.api.event.PlayerDeathCallback.EVENT.invoker().interact((ServerPlayerEntity) (Object) this, source);

        if (result1 == ActionResult.FAIL) {
            info.cancel();
        }
    }
}
